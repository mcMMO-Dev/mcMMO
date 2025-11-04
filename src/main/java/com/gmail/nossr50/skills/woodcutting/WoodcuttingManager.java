package com.gmail.nossr50.skills.woodcutting;

import static com.gmail.nossr50.util.ItemUtils.spawnItemsFromCollection;
import static com.gmail.nossr50.util.Misc.getBlockCenter;
import static com.gmail.nossr50.util.skills.RankUtils.hasUnlockedSubskill;

import com.gmail.nossr50.api.FakeBlockBreakEventType;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

public class WoodcuttingManager extends SkillManager {
    public static final String SAPLING = "sapling";
    public static final String PROPAGULE = "propagule";
    private static final Predicate<ItemStack> IS_SAPLING_OR_PROPAGULE =
            p -> p.getType().getKey().getKey().toLowerCase().contains(SAPLING)
                    || p.getType().getKey().getKey().toLowerCase().contains(PROPAGULE);
    private boolean treeFellerReachedThreshold = false;
    private static int treeFellerThreshold;

    /**
     * The x/y differences to the blocks in a flat cylinder around the center block, which is
     * excluded.
     */
    private static final int[][] directions = {
            new int[]{-2, -1}, new int[]{-2, 0}, new int[]{-2, 1},
            new int[]{-1, -2}, new int[]{-1, -1}, new int[]{-1, 0}, new int[]{-1, 1},
            new int[]{-1, 2},
            new int[]{0, -2}, new int[]{0, -1}, new int[]{0, 1}, new int[]{0, 2},
            new int[]{1, -2}, new int[]{1, -1}, new int[]{1, 0}, new int[]{1, 1}, new int[]{1, 2},
            new int[]{2, -1}, new int[]{2, 0}, new int[]{2, 1},
    };

    public WoodcuttingManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.WOODCUTTING);
        treeFellerThreshold = mcMMO.p.getGeneralConfig().getTreeFellerThreshold();
    }

    public boolean canUseLeafBlower(ItemStack heldItem) {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.WOODCUTTING_LEAF_BLOWER)
                && hasUnlockedSubskill(getPlayer(), SubSkillType.WOODCUTTING_LEAF_BLOWER)
                && ItemUtils.isAxe(heldItem);
    }

    public boolean canUseTreeFeller(ItemStack heldItem) {
        return mmoPlayer.getAbilityMode(SuperAbilityType.TREE_FELLER)
                && ItemUtils.isAxe(heldItem);
    }

    private boolean checkHarvestLumberActivation(Material material) {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RankUtils.hasReachedRank(1, getPlayer(), SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.WOODCUTTING_HARVEST_LUMBER,
                mmoPlayer)
                && mcMMO.p.getGeneralConfig()
                .getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING, material);
    }

    private boolean checkCleanCutsActivation(Material material) {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RankUtils.hasReachedRank(1, getPlayer(), SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.WOODCUTTING_CLEAN_CUTS,
                mmoPlayer)
                && mcMMO.p.getGeneralConfig()
                .getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING, material);
    }

    /**
     * Processes bonus drops for a block
     *
     * @param blockState Block being broken
     */
    @Deprecated(forRemoval = true, since = "2.2.024")
    public void processBonusDropCheck(@NotNull BlockState blockState) {
        processBonusDropCheck(blockState.getBlock());
    }

    public void processBonusDropCheck(@NotNull Block block) {
        //TODO: Why isn't this using the item drop event? Potentially because of Tree Feller? This should be adjusted either way.
        if (mcMMO.p.getGeneralConfig()
                .getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING, block.getType())) {
            //Mastery enabled for player
            if (Permissions.canUseSubSkill(getPlayer(), SubSkillType.WOODCUTTING_CLEAN_CUTS)) {
                if (checkCleanCutsActivation(block.getType())) {
                    //Triple drops
                    spawnHarvestLumberBonusDrops(block);
                    spawnHarvestLumberBonusDrops(block);
                } else {
                    //Harvest Lumber Check
                    if (checkHarvestLumberActivation(block.getType())) {
                        spawnHarvestLumberBonusDrops(block);
                    }
                }
                //No Mastery (no Clean Cuts)
            } else if (Permissions.canUseSubSkill(getPlayer(),
                    SubSkillType.WOODCUTTING_HARVEST_LUMBER)) {
                if (checkHarvestLumberActivation(block.getType())) {
                    spawnHarvestLumberBonusDrops(block);
                }
            }
        }
    }

    @Deprecated(forRemoval = true, since = "2.2.024")
    public void processWoodcuttingBlockXP(@NotNull BlockState blockState) {
        processWoodcuttingBlockXP(blockState.getBlock());
    }

    public void processWoodcuttingBlockXP(@NotNull Block block) {
        if (mcMMO.getUserBlockTracker().isIneligible(block)) {
            return;
        }

        int xp = getExperienceFromLog(block);
        applyXpGain(xp, XPGainReason.PVE, XPGainSource.SELF);
    }

    /**
     * Begins Tree Feller
     *
     * @param startingBlock The first startingBlock broken
     */
    public void processTreeFeller(Block startingBlock) {
        final Player player = getPlayer();
        Set<Block> treeFellerBlocks = new HashSet<>();

        treeFellerReachedThreshold = false;

        processTree(startingBlock, treeFellerBlocks);

        // If the tool can't sustain the durability loss
        if (!handleDurabilityLoss(treeFellerBlocks, player.getInventory().getItemInMainHand(),
                player)) {
            NotificationManager.sendPlayerInformation(player,
                    NotificationType.SUBSKILL_MESSAGE_FAILED,
                    "Woodcutting.Skills.TreeFeller.Splinter");

            double health = player.getHealth();

            if (health > 1) {
                int dmg = Misc.getRandom().nextInt((int) (health - 1));
                CombatUtils.safeDealDamage(player, dmg);
            }

            return;
        }

        dropTreeFellerLootFromBlocks(treeFellerBlocks);
        treeFellerReachedThreshold = false; // Reset the value after we're done with Tree Feller each time.
    }

    /**
     * Process the tree feller ability.
     * <p>
     * Algorithm: An int[][] of X/Z directions is created on static class initialization,
     * representing a cylinder with radius of about 2 - the (0,0) center and all (+-2, +-2) corners
     * are omitted.
     * <p>
     * processTreeFellerTargetBlock() returns a boolean, which is used for the sole purpose of
     * switching between these two behaviors:
     * <p>
     * (Call blockState "this log" for the below explanation.)
     * <p>
     * [A] There is another log above this log (TRUNK) Only the flat cylinder in the directions
     * array is searched. [B] There is not another log above this log (BRANCH AND TOP) The cylinder
     * in the directions array is extended up and down by 1 block in the Y-axis, and the block below
     * this log is checked as well. Due to the fact that the directions array will catch all blocks
     * on a red mushroom, the special method for it is eliminated.
     * <p>
     * This algorithm has been shown to achieve a performance of 2-5 milliseconds on regular trees
     * and 10-15 milliseconds on jungle trees once the JIT has optimized the function (use the
     * ability about 4 times before taking measurements).
     */
    @VisibleForTesting
    void processTree(Block block, Set<Block> treeFellerBlocks) {
        List<Block> futureCenterBlocks = new ArrayList<>();

        // Check the block up and take different behavior (smaller search) if it's a log
        if (processTreeFellerTargetBlock(block.getRelative(BlockFace.UP), futureCenterBlocks,
                treeFellerBlocks)) {
            for (int[] dir : directions) {
                processTreeFellerTargetBlock(block.getRelative(dir[0], 0, dir[1]),
                        futureCenterBlocks, treeFellerBlocks);

                if (treeFellerReachedThreshold) {
                    return;
                }
            }
        } else {
            // Cover DOWN
            processTreeFellerTargetBlock(block.getRelative(BlockFace.DOWN), futureCenterBlocks,
                    treeFellerBlocks);
            // Search in a cube
            for (int y = -1; y <= 1; y++) {
                for (int[] dir : directions) {
                    processTreeFellerTargetBlock(block.getRelative(dir[0], y, dir[1]),
                            futureCenterBlocks, treeFellerBlocks);

                    if (treeFellerReachedThreshold) {
                        return;
                    }
                }
            }
        }

        // Recursive call for each log found
        for (Block futureCenterBlock : futureCenterBlocks) {
            if (treeFellerReachedThreshold) {
                return;
            }

            processTree(futureCenterBlock, treeFellerBlocks);
        }
    }

    /**
     * Handles the durability loss
     *
     * @param treeFellerBlocks List of blocks to be removed
     * @param inHand tool being used
     * @param player the player holding the item
     * @return True if the tool can sustain the durability loss
     */
    private static boolean handleDurabilityLoss(@NotNull Set<Block> treeFellerBlocks,
            @NotNull ItemStack inHand, @NotNull Player player) {
        //Treat the NBT tag for unbreakable and the durability enchant differently
        ItemMeta meta = inHand.getItemMeta();

        if (meta != null && meta.isUnbreakable()) {
            return true;
        }

        int durabilityLoss = 0;
        Material type = inHand.getType();

        for (Block block : treeFellerBlocks) {
            if (BlockUtils.hasWoodcuttingXP(block)) {
                durabilityLoss += mcMMO.p.getGeneralConfig().getAbilityToolDamage();
            }
        }

        // Call PlayerItemDamageEvent first to make sure it's not cancelled
        //TODO: Put this event stuff in handleDurabilityChange
        final PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, inHand,
                durabilityLoss);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        SkillUtils.handleDurabilityChange(inHand, durabilityLoss);
        int durability = meta instanceof Damageable ? ((Damageable) meta).getDamage() : 0;
        return (durability < (mcMMO.getRepairableManager().isRepairable(type)
                ? mcMMO.getRepairableManager().getRepairable(type).getMaximumDurability()
                : type.getMaxDurability()));
    }

    /**
     * Handle a block addition to the list of blocks to be removed and to the list of blocks used
     * for future recursive calls of 'processTree()'
     *
     * @param block Block to be added
     * @param futureCenterBlocks List of blocks that will be used to call 'processTree()'
     * @param treeFellerBlocks List of blocks to be removed
     * @return true if and only if the given block was a Log not already in treeFellerBlocks.
     */
    private boolean processTreeFellerTargetBlock(@NotNull Block block,
            @NotNull List<Block> futureCenterBlocks,
            @NotNull Set<Block> treeFellerBlocks) {
        if (treeFellerBlocks.contains(block) || mcMMO.getUserBlockTracker().isIneligible(block)) {
            return false;
        }

        // Without this check Tree Feller propagates through leaves until the threshold is hit
        if (treeFellerBlocks.size() > treeFellerThreshold) {
            treeFellerReachedThreshold = true;
        }

        if (BlockUtils.hasWoodcuttingXP(block)) {
            treeFellerBlocks.add(block);
            futureCenterBlocks.add(block);
            return true;
        } else if (BlockUtils.isNonWoodPartOfTree(block)) {
            treeFellerBlocks.add(block);
            return false;
        }
        return false;
    }

    /**
     * Handles the dropping of blocks
     *
     * @param treeFellerBlocks List of blocks to be dropped
     */
    private void dropTreeFellerLootFromBlocks(@NotNull Set<Block> treeFellerBlocks) {
        Player player = getPlayer();
        int xp = 0;
        int processedLogCount = 0;
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        for (Block block : treeFellerBlocks) {
            int beforeXP = xp;

            if (!EventUtils.simulateBlockBreak(block, player,
                    FakeBlockBreakEventType.TREE_FELLER)) {
                continue;
            }

            /*
             * Handle Drops & XP
             */

            if (BlockUtils.hasWoodcuttingXP(block)) {
                //Add XP
                xp += processTreeFellerXPGains(block, processedLogCount);

                //Drop displaced block
                spawnItemsFromCollection(player, getBlockCenter(block),
                        block.getDrops(itemStack), ItemSpawnReason.TREE_FELLER_DISPLACED_BLOCK);

                //Bonus Drops / Harvest lumber checks
                processBonusDropCheck(block);
            } else if (BlockUtils.isNonWoodPartOfTree(block)) {
                // 75% of the time do not drop leaf blocks
                if (ThreadLocalRandom.current().nextInt(100) > 75) {
                    spawnItemsFromCollection(player,
                            getBlockCenter(block),
                            block.getDrops(itemStack),
                            ItemSpawnReason.TREE_FELLER_DISPLACED_BLOCK);
                } else if (hasUnlockedSubskill(player, SubSkillType.WOODCUTTING_KNOCK_ON_WOOD)) {
                    // if KnockOnWood is unlocked, then drop any saplings from the remaining blocks
                    ItemUtils.spawnItemsConditionally(block.getDrops(itemStack),
                            IS_SAPLING_OR_PROPAGULE,
                            ItemSpawnReason.TREE_FELLER_DISPLACED_BLOCK,
                            getBlockCenter(block),
                            // only spawn saplings
                            player
                    );
                }

                //Drop displaced non-woodcutting XP blocks
                if (hasUnlockedSubskill(player, SubSkillType.WOODCUTTING_KNOCK_ON_WOOD)) {
                    if (RankUtils.hasReachedRank(2, player,
                            SubSkillType.WOODCUTTING_KNOCK_ON_WOOD)) {
                        if (mcMMO.p.getAdvancedConfig().isKnockOnWoodXPOrbEnabled()) {
                            if (ProbabilityUtil.isStaticSkillRNGSuccessful(
                                    PrimarySkillType.WOODCUTTING, mmoPlayer, 10)) {
                                int randOrbCount = Math.max(1, Misc.getRandom().nextInt(100));
                                Misc.spawnExperienceOrb(block.getLocation(), randOrbCount);
                            }
                        }
                    }
                }
            }

            block.setType(Material.AIR);

            //Update only when XP changes
            processedLogCount = updateProcessedLogCount(xp, processedLogCount, beforeXP);
        }

        applyXpGain(xp, XPGainReason.PVE, XPGainSource.SELF);
    }

    private int updateProcessedLogCount(int xp, int processedLogCount, int beforeXP) {
        if (beforeXP != xp) {
            processedLogCount += 1;
        }

        return processedLogCount;
    }

    /**
     * Retrieves the experience reward from logging via Tree Feller Experience is reduced per log
     * processed so far Experience is only reduced if the config option to reduce Tree Feller XP is
     * set Experience per log will not fall below 1 unless the experience for that log is set to 0
     * in the config
     *
     * @param block Log being broken
     * @param woodCount how many logs have given out XP for this tree feller so far
     * @return Amount of experience
     */
    private static int processTreeFellerXPGains(Block block, int woodCount) {
        if (mcMMO.getUserBlockTracker().isIneligible(block)) {
            return 0;
        }

        int rawXP = ExperienceConfig.getInstance()
                .getXp(PrimarySkillType.WOODCUTTING, block.getType());

        if (rawXP <= 0) {
            return 0;
        }

        if (ExperienceConfig.getInstance().isTreeFellerXPReduced()) {
            int reducedXP = rawXP - (woodCount * 5);
            rawXP = Math.max(1, reducedXP);
            return rawXP;
        } else {
            return ExperienceConfig.getInstance()
                    .getXp(PrimarySkillType.WOODCUTTING, block.getType());
        }
    }

    /**
     * Retrieves the experience reward from a log
     *
     * @param blockState Log being broken
     * @return Amount of experience
     */
    @Deprecated(forRemoval = true, since = "2.2.024")
    protected static int getExperienceFromLog(BlockState blockState) {
        return getExperienceFromLog(blockState.getBlock());
    }

    /**
     * Retrieves the experience reward from a log
     *
     * @param block Log being broken
     * @return Amount of experience
     */
    protected static int getExperienceFromLog(Block block) {
        return ExperienceConfig.getInstance().getXp(PrimarySkillType.WOODCUTTING, block.getType());
    }

    /**
     * Spawns harvest lumber bonus drops
     *
     * @param blockState Block being broken
     */
    @Deprecated(forRemoval = true, since = "2.2.024")
    void spawnHarvestLumberBonusDrops(@NotNull BlockState blockState) {
        spawnHarvestLumberBonusDrops(blockState.getBlock());
    }

    void spawnHarvestLumberBonusDrops(@NotNull Block block) {
        spawnItemsFromCollection(
                getPlayer(),
                getBlockCenter(block),
                block.getDrops(getPlayer().getInventory().getItemInMainHand()),
                ItemSpawnReason.BONUS_DROPS);
    }
}
