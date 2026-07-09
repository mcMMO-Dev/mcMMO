package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Verifies that the connected blocks of a multi-block plant (bamboo, sugar cane, kelp, cactus,
 * chorus trees, hanging vines) actually broke before awarding their Herbalism XP.
 *
 * <p>Vanilla does not destroy the rest of the plant on the tick the supporting block is broken.
 * Each connected block is destroyed by a scheduled block tick on a later tick, cascading one
 * block per tick, and if the broken block is replaced quickly enough the rest of the plant never
 * breaks at all. Paying XP for the whole plant up front was therefore exploitable: break a
 * middle segment, collect XP for the entire column, replace the segment before the scheduled
 * tick fires, repeat. This task polls the affected positions once per tick and only pays for
 * blocks that are verifiably gone, giving up once the collapse window has fully elapsed.</p>
 *
 * <p>Each position can only be claimed by one task at a time, and a claim is revoked when a new
 * BlockBreakEvent breaks the position directly, so no block is ever paid for twice.</p>
 */
public class PlantCollapseXpTask extends CancellableRunnable {
    /**
     * Extra polling runs beyond one run per claimed block; the full window is
     * {@code pendingBlocks.size() + SETTLE_GRACE_TICKS} runs.
     *
     * <p>Vanilla collapses these plants through scheduled block ticks that propagate one block
     * per tick: each destruction fires the neighbour update that schedules the next block's
     * check one tick later. The farthest dying block therefore sits at most one tick per
     * claimed block away, for a straight kelp column and a branching chorus tree alike, so the
     * per-block term always covers the cascade. The grace runs cover the fixed overheads: the
     * scheduler heartbeat runs before the same tick's block ticks, so a destruction is only
     * observable one run later, and a server with a large scheduled-tick backlog can defer
     * block ticks briefly. If the window is ever exceeded anyway, unresolved blocks simply
     * expire unpaid - XP can be missed under extreme lag, but never paid for unbroken
     * blocks.</p>
     */
    private static final int SETTLE_GRACE_TICKS = 4;

    /**
     * Positions awaiting collapse verification, mapped to the task that claimed them. Concurrent
     * because Folia region threads may process breaks of different plants at the same time.
     */
    private static final Map<BlockPosition, PlantCollapseXpTask> PENDING_VERIFICATIONS =
            new ConcurrentHashMap<>();

    private final McMMOPlayer mmoPlayer;
    private final int xpBudget;
    private final List<PendingPlantBlock> pendingBlocks = new ArrayList<>();
    private int maxRuns;
    private int runCount = 0;
    private int verifiedXp = 0;
    private int verifiedBlockCount = 0;

    /**
     * @param mmoPlayer the player who broke the plant
     * @param xpBudget the most XP this task may pay out in total, after the origin block's
     * immediate reward is accounted for (tall-plant XP limits)
     */
    public PlantCollapseXpTask(@NotNull McMMOPlayer mmoPlayer, int xpBudget) {
        this.mmoPlayer = mmoPlayer;
        this.xpBudget = xpBudget;
    }

    /**
     * Claims a plant block for collapse verification. The claim fails when another task is
     * already watching the position, which prevents rapid re-breaks from collecting XP for the
     * same blocks more than once.
     *
     * @param block the plant block expected to break on an upcoming tick
     * @param xp the XP this block pays if it actually breaks
     * @param wasIneligible whether the block tracker had the position marked as player-placed
     * @return true if this task now owns the position
     */
    public boolean claimBlock(@NotNull Block block, int xp, boolean wasIneligible) {
        final BlockPosition position = BlockPosition.of(block);
        if (PENDING_VERIFICATIONS.putIfAbsent(position, this) != null) {
            return false;
        }

        pendingBlocks.add(new PendingPlantBlock(block, position, xp, wasIneligible));
        return true;
    }

    public boolean hasPendingBlocks() {
        return !pendingBlocks.isEmpty();
    }

    /**
     * Starts polling once per tick on the region that owns the broken plant. The task cancels
     * itself when every claimed block has been resolved or the collapse window has elapsed.
     *
     * @param originLocation the location of the block broken by the event
     */
    public void schedule(@NotNull Location originLocation) {
        maxRuns = pendingBlocks.size() + SETTLE_GRACE_TICKS;
        mcMMO.p.getFoliaLib().getScheduler().runAtLocationTimer(originLocation, this, 1, 1);
    }

    /**
     * Revokes any pending claim on a position. Called when a new BlockBreakEvent breaks the
     * position directly, making that event the authoritative source of rewards for it.
     *
     * @param block the block being broken by an event
     */
    public static void revokeClaim(@NotNull Block block) {
        PENDING_VERIFICATIONS.remove(BlockPosition.of(block));
    }

    /**
     * Drops every pending claim. Called on plugin disable so reloads never leave stale
     * world references behind; also used by tests.
     */
    public static void clearPendingVerifications() {
        PENDING_VERIFICATIONS.clear();
    }

    @Override
    public void run() {
        runCount++;

        try {
            final Iterator<PendingPlantBlock> iterator = pendingBlocks.iterator();
            while (iterator.hasNext()) {
                final PendingPlantBlock pending = iterator.next();
                final Block block = pending.block();

                if (!block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {
                    // Nothing can be verified in an unloaded chunk, and probing it would force
                    // a synchronous chunk load
                    PENDING_VERIFICATIONS.remove(pending.position(), this);
                    iterator.remove();
                    continue;
                }

                if (isBlockGone(block.getType())) {
                    iterator.remove();
                    // Pay only while the claim is still ours; a newer break event may have
                    // taken over rewards for this position
                    if (PENDING_VERIFICATIONS.remove(pending.position(), this)) {
                        verifiedXp += pending.xp();
                        verifiedBlockCount++;
                        if (pending.wasIneligible()) {
                            // The player-placed block is gone; the position is natural again
                            mcMMO.getUserBlockTracker().setEligible(block);
                        }
                    }
                } else if (PENDING_VERIFICATIONS.get(pending.position()) != this) {
                    iterator.remove();
                }
            }
        } finally {
            // Termination is unconditional: even if a tracker write or the XP payment throws,
            // this task must never keep polling forever or keep positions claimed
            if (pendingBlocks.isEmpty() || runCount >= maxRuns) {
                finishVerification();
            }
        }
    }

    private void finishVerification() {
        // Cancel and release everything before paying: nothing below may leave the timer
        // running or positions claimed, even if the payment itself throws
        cancel();
        // Whatever survived the collapse window stays in the world and pays nothing
        for (PendingPlantBlock pending : pendingBlocks) {
            PENDING_VERIFICATIONS.remove(pending.position(), this);
        }
        pendingBlocks.clear();

        if (mmoPlayer.isDebugMode()) {
            mmoPlayer.getPlayer().sendMessage(
                    "Plant collapse verified: " + verifiedBlockCount + " block(s) broke, XP: "
                            + Math.min(verifiedXp, xpBudget));
        }

        final int xpToPay = Math.min(verifiedXp, xpBudget);
        if (xpToPay > 0 && mmoPlayer.getPlayer().isOnline()) {
            mmoPlayer.beginXpGain(PrimarySkillType.HERBALISM, xpToPay, XPGainReason.PVE,
                    XPGainSource.SELF);
        }
    }

    private static boolean isBlockGone(Material material) {
        // Waterlogged plants like kelp and tall seagrass leave water behind instead of air,
        // or a bubble column when they break above magma or soul sand
        return material.isAir() || material == Material.WATER
                || material == Material.BUBBLE_COLUMN;
    }

    /**
     * Immutable registry key for a claimed position. Deliberately built from the world UUID
     * instead of holding World or Location references, so pending claims never retain a world
     * and keys hash consistently even if the world unloads mid-verification.
     */
    private record BlockPosition(UUID worldUid, int x, int y, int z) {
        static BlockPosition of(Block block) {
            return new BlockPosition(block.getWorld().getUID(), block.getX(), block.getY(),
                    block.getZ());
        }
    }

    private record PendingPlantBlock(Block block, BlockPosition position, int xp,
            boolean wasIneligible) {
    }
}
