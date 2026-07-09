package com.gmail.nossr50.skills.acrobatics;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Limits how many Dodge XP rewards a single mob can hand out so players cannot farm unlimited
 * Acrobatics XP from one trapped mob.
 * <p>
 * Rewards are tracked per mob UUID. A mob's reward count resets once it has not been dodged for
 * the idle window, so ordinary combat against a long-lived mob is not penalized forever; every
 * dodge refreshes the idle timer, which means a mob that keeps getting dodged never resets.
 * <p>
 * Entries whose idle window has passed are swept out opportunistically during dodge handling,
 * at most once per idle window. Fresh entries are never evicted no matter how large the map
 * grows, so players fighting large packs of mobs keep an accurate count for every attacker.
 */
public final class DodgeXpTracker {
    @VisibleForTesting
    static final int MAX_XP_REWARDS_PER_MOB = 6;
    @VisibleForTesting
    static final long IDLE_RESET_MILLIS = 60_000;

    private static final ConcurrentHashMap<UUID, DodgeRewards> REWARDS_BY_MOB =
            new ConcurrentHashMap<>();
    private static final AtomicLong LAST_SWEEP_TIME = new AtomicLong();

    private record DodgeRewards(int count, long lastDodgeTime) {
        boolean isStale(long now) {
            return now - lastDodgeTime >= IDLE_RESET_MILLIS;
        }
    }

    private DodgeXpTracker() {
    }

    /**
     * Records a dodge against the given mob and reports whether it should still grant XP.
     *
     * @param mob the mob whose attack was dodged
     * @return true if this dodge may grant XP, false once the mob's reward cap is reached
     */
    public static boolean tryConsumeXpReward(@NotNull Mob mob) {
        return tryConsumeXpReward(mob.getUniqueId(), System.currentTimeMillis());
    }

    @VisibleForTesting
    static boolean tryConsumeXpReward(@NotNull UUID mobId, long now) {
        final DodgeRewards rewards = REWARDS_BY_MOB.get(mobId);
        final boolean rewardable;
        if (rewards == null || rewards.isStale(now)) {
            rewardable = true;
            REWARDS_BY_MOB.put(mobId, new DodgeRewards(1, now));
        } else {
            rewardable = rewards.count() < MAX_XP_REWARDS_PER_MOB;
            REWARDS_BY_MOB.put(mobId,
                    new DodgeRewards(rewardable ? rewards.count() + 1 : rewards.count(), now));
        }

        sweepStaleEntries(now);
        return rewardable;
    }

    /**
     * Removes entries whose idle window has passed, at most once per idle window. Fresh entries
     * are always kept; there is no size-based eviction.
     */
    private static void sweepStaleEntries(long now) {
        final long lastSweepTime = LAST_SWEEP_TIME.get();
        if (now - lastSweepTime < IDLE_RESET_MILLIS
                || !LAST_SWEEP_TIME.compareAndSet(lastSweepTime, now)) {
            return;
        }

        REWARDS_BY_MOB.values().removeIf(rewards -> rewards.isStale(now));
    }

    @VisibleForTesting
    static int trackedMobCount() {
        return REWARDS_BY_MOB.size();
    }

    @VisibleForTesting
    static void clearAll() {
        REWARDS_BY_MOB.clear();
        LAST_SWEEP_TIME.set(0);
    }
}
