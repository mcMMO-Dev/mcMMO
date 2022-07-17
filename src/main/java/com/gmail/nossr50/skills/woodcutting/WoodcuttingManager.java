package com.gmail.nossr50.skills.woodcutting;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.skills.SkillUtils;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WoodcuttingManager extends SkillManager {
    private boolean treeFellerReachedThreshold = false;
    private static int treeFellerThreshold; //TODO: Shared setting, will be removed in 2.2

    /**
     * The x/y differences to the blocks in a flat cylinder around the center
     * block, which is excluded.
     */
    private static final int[][] directions = {
            new int[] {-2, -1}, new int[] {-2, 0}, new int[] {-2, 1},
            new int[] {-1, -2}, new int[] {-1, -1}, new int[] {-1, 0}, new int[] {-1, 1}, new int[] {-1, 2},
            new int[] { 0, -2}, new int[] { 0, -1},                    new int[] { 0, 1}, new int[] { 0, 2},
            new int[] { 1, -2}, new int[] { 1, -1}, new int[] { 1, 0}, new int[] { 1, 1}, new int[] { 1, 2},
            new int[] { 2, -1}, new int[] { 2, 0}, new int[] { 2, 1},
    };

    public WoodcuttingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.WOODCUTTING);
        treeFellerThreshold = mcMMO.p.getGeneralConfig().getTreeFellerThreshold();
    }

    public boolean canUseLeafBlower(ItemStack heldItem) {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.WOODCUTTING_LEAF_BLOWER)
                && RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.WOODCUTTING_LEAF_BLOWER)
                && ItemUtils.isAxe(heldItem);
    }

    public boolean canUseTreeFeller(ItemStack heldItem) {
        return mmoPlayer.getAbilityMode(SuperAbilityType.TREE_FELLER)
                && ItemUtils.isAxe(heldItem);
    }

    private boolean checkHarvestLumberActivation(@NotNull Material material) {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RankUtils.hasReachedRank(1, getPlayer(), SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.WOODCUTTING_HARVEST_LUMBER, getPlayer())
                && mcMMO.p.getGeneralConfig().getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING, material);
    }

    /**
     * Begins Woodcutting
     *
     * @param blockState Block being broken
     */
    public void processHarvestLumber(@NotNull BlockState blockState) {
        if (checkHarvestLumberActivation(blockState.getType())) {
            spawnHarvestLumberBonusDrops(blockState);
        }
    }

    public void processWoodcuttingBlockXP(@NotNull BlockState blockState) {
        int xp = getExperienceFromLog(blockState);
        applyXpGain(xp, XPGainReason.PVE);
    }

    /**
     * Begins Tree Feller
     *
     * @param blockState Block being broken
     */
    public void processTreeFeller(BlockState blockState) {
        Player player = getPlayer();
        Set<BlockState> treeFellerBlocks = new HashSet<>();

        treeFellerReachedThreshold = false;

        processTree(blockState, treeFellerBlocks);

        // If the player is trying to break too many blocks
        if (treeFellerReachedThreshold) {
            treeFellerReachedThreshold = false;

            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Woodcutting.Skills.TreeFeller.Threshold");

            //Tree feller won't be activated for this block, award normal xp.
            processWoodcuttingBlockXP(blockState);
            processHarvestLumber(blockState);

            return;
        }

        // If the tool can't sustain the durability loss
        if (!handleDurabilityLoss(treeFellerBlocks, player.getInventory().getItemInMainHand(), player)) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Woodcutting.Skills.TreeFeller.Splinter");

            double health = player.getHealth();

            if (health > 1) {
                CombatUtils.dealDamage(player, Misc.getRandom().nextInt((int) (health - 1)));
            }

            return;
        }

        dropTreeFellerLootFromBlocks(treeFellerBlocks);
        treeFellerReachedThreshold = false; // Reset the value after we're done with Tree Feller each time.
    }

    /**
     * Processes Tree Feller in a recursive manner
     *
     * @param blockState Block being checked
     * @param treeFellerBlocks List of blocks to be removed
     */
    /*
     * Algorithm: An int[][] of X/Z directions is created on static class
     * initialization, representing a cylinder with radius of about 2 - the
     * (0,0) center and all (+-2, +-2) corners are omitted.
     *
     * processTreeFellerTargetBlock() returns a boolean, which is used for the sole purpose of
     * switching between these two behaviors:
     *
     * (Call blockState "this log" for the below explanation.)
     *
     *  [A] There is another log above this log (TRUNK)
     *    Only the flat cylinder in the directions array is searched.
     *  [B] There is not another log above this log (BRANCH AND TOP)
     *    The cylinder in the directions array is extended up and down by 1
     *    block in the Y-axis, and the block below this log is checked as
     *    well. Due to the fact that the directions array will catch all
     *    blocks on a red mushroom, the special method for it is eliminated.
     *
     * This algorithm has been shown to achieve a performance of 2-5
     * milliseconds on regular trees and 10-15 milliseconds on jungle trees
     * once the JIT has optimized the function (use the ability about 4 times
     * before taking measurements).
     */
    private void processTree(BlockState blockState, Set<BlockState> treeFellerBlocks) {
        List<BlockState> futureCenterBlocks = new ArrayList<>();

        // Check the block up and take different behavior (smaller search) if it's a log
        if (processTreeFellerTargetBlock(blockState.getBlock().getRelative(BlockFace.UP).getState(), futureCenterBlocks, treeFellerBlocks)) {
            for (int[] dir : directions) {
                processTreeFellerTargetBlock(blockState.getBlock().getRelative(dir[0], 0, dir[1]).getState(), futureCenterBlocks, treeFellerBlocks);

                if (treeFellerReachedThreshold) {
                    return;
                }
            }
        }
        else {
            // Cover DOWN
            processTreeFellerTargetBlock(blockState.getBlock().getRelative(BlockFace.DOWN).getState(), futureCenterBlocks, treeFellerBlocks);
            // Search in a cube
            for (int y = -1; y <= 1; y++) {
                for (int[] dir : directions) {
                    processTreeFellerTargetBlock(blockState.getBlock().getRelative(dir[0], y, dir[1]).getState(), futureCenterBlocks, treeFellerBlocks);

                    if (treeFellerReachedThreshold) {
                        return;
                    }
                }
            }
        }

        // Recursive call for each log found
        for (BlockState futureCenterBlock : futureCenterBlocks) {
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
    private static boolean handleDurabilityLoss(@NotNull Set<BlockState> treeFellerBlocks, @NotNull ItemStack inHand, @NotNull Player player) {
        //Treat the NBT tag for unbreakable and the durability enchant differently
        ItemMeta meta = inHand.getItemMeta();

        if (meta != null && meta.isUnbreakable()) {
            return true;
        }

        int durabilityLoss = 0;
        Material type = inHand.getType();

        for (BlockState blockState : treeFellerBlocks) {
            if (BlockUtils.hasWoodcuttingXP(blockState)) {
                durabilityLoss += mcMMO.p.getGeneralConfig().getAbilityToolDamage();
            }
        }

        // Call PlayerItemDamageEvent first to make sure it's not cancelled
        //TODO: Put this event stuff in handleDurabilityChange
        final PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, inHand, durabilityLoss);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        SkillUtils.handleDurabilityChange(inHand, durabilityLoss);
        int durability = meta instanceof Damageable ? ((Damageable) meta).getDamage(): 0;
        return (durability < (mcMMO.getRepairableManager().isRepairable(type) ? mcMMO.getRepairableManager().getRepairable(type).getMaximumDurability() : type.getMaxDurability()));
    }

    /**
     * Handle a block addition to the list of blocks to be removed and to the
     * list of blocks used for future recursive calls of
     * 'processTree()'
     *
     * @param blockState Block to be added
     * @param futureCenterBlocks List of blocks that will be used to call
     *     'processTree()'
     * @param treeFellerBlocks List of blocks to be removed
     * @return true if and only if the given blockState was a Log not already
     *     in treeFellerBlocks.
     */
    private boolean processTreeFellerTargetBlock(@NotNull BlockState blockState, @NotNull List<BlockState> futureCenterBlocks, @NotNull Set<BlockState> treeFellerBlocks) {
        if (treeFellerBlocks.contains(blockState) || mcMMO.getPlaceStore().isTrue(blockState)) {
            return false;
        }

        // Without this check Tree Feller propagates through leaves until the threshold is hit
        if (treeFellerBlocks.size() > treeFellerThreshold) {
            treeFellerReachedThreshold = true;
        }

        if (BlockUtils.hasWoodcuttingXP(blockState)) {
            treeFellerBlocks.add(blockState);
            futureCenterBlocks.add(blockState);
            return true;
        }
        else if (BlockUtils.isNonWoodPartOfTree(blockState)) {
            treeFellerBlocks.add(blockState);
            return false;
        }
        return false;
    }

    /**
     * Handles the dropping of blocks
     *
     * @param treeFellerBlocks List of blocks to be dropped
     */
    private void dropTreeFellerLootFromBlocks(@NotNull Set<BlockState> treeFellerBlocks) {
        Player player = getPlayer();
        int xp = 0;
        int processedLogCount = 0;
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        for (BlockState blockState : treeFellerBlocks) {
            int beforeXP = xp;
            Block block = blockState.getBlock();

            if (!EventUtils.simulateBlockBreak(block, player, true)) {
                continue;
            }

            /*
             * Handle Drops & XP
             */

            if (BlockUtils.hasWoodcuttingXP(blockState)) {
                //Add XP
                xp += processTreeFellerXPGains(blockState, processedLogCount);

                //Drop displaced block
                Misc.spawnItemsFromCollection(getPlayer(), Misc.getBlockCenter(blockState), block.getDrops(itemStack), ItemSpawnReason.TREE_FELLER_DISPLACED_BLOCK);

                //Bonus Drops / Harvest lumber checks
                processHarvestLumber(blockState);
            } else if (BlockUtils.isNonWoodPartOfTree(blockState)) {
                //Drop displaced non-woodcutting XP blocks

                if(RankUtils.hasUnlockedSubskill(player, SubSkillType.WOODCUTTING_KNOCK_ON_WOOD)) {
                    Misc.spawnItemsFromCollection(getPlayer(), Misc.getBlockCenter(blockState), block.getDrops(itemStack), ItemSpawnReason.TREE_FELLER_DISPLACED_BLOCK);

                    if(RankUtils.hasReachedRank(2, player, SubSkillType.WOODCUTTING_KNOCK_ON_WOOD)) {
                        if(mcMMO.p.getAdvancedConfig().isKnockOnWoodXPOrbEnabled()) {
                            if(RandomChanceUtil.rollDice(10, 100)) {
                                int randOrbCount = Math.max(1, Misc.getRandom().nextInt(100));
                                Misc.spawnExperienceOrb(blockState.getLocation(), randOrbCount);
                            }
                        }
                    }

                } else {
                    Misc.spawnItemsFromCollection(getPlayer(), Misc.getBlockCenter(blockState), block.getDrops(itemStack), ItemSpawnReason.TREE_FELLER_DISPLACED_BLOCK, 1);
                }
            }

            blockState.setType(Material.AIR);
            blockState.update(true);

            //Update only when XP changes
            processedLogCount = updateProcessedLogCount(xp, processedLogCount, beforeXP);
        }

        applyXpGain(xp, XPGainReason.PVE);
    }

    private int updateProcessedLogCount(int xp, int processedLogCount, int beforeXP) {
        if(beforeXP != xp)
            processedLogCount+=1;

        return processedLogCount;
    }

    /**
     * Retrieves the experience reward from logging via Tree Feller
     * Experience is reduced per log processed so far
     * Experience is only reduced if the config option to reduce Tree Feller XP is set
     * Experience per log will not fall below 1 unless the experience for that log is set to 0 in the config
     *
     * @param blockState Log being broken
     * @param woodCount how many logs have given out XP for this tree feller so far
     * @return Amount of experience
     */
    private static int processTreeFellerXPGains(BlockState blockState, int woodCount) {
        int rawXP = ExperienceConfig.getInstance().getXp(PrimarySkillType.WOODCUTTING, blockState.getType());

        if(rawXP <= 0)
            return 0;

        if(ExperienceConfig.getInstance().isTreeFellerXPReduced()) {
            int reducedXP = rawXP - (woodCount * 5);
            rawXP = Math.max(1, reducedXP);
            return rawXP;
        } else {
            return ExperienceConfig.getInstance().getXp(PrimarySkillType.WOODCUTTING, blockState.getType());
        }
    }

    /**
     * Retrieves the experience reward from a log
     *
     * @param blockState Log being broken
     * @return Amount of experience
     */
    protected static int getExperienceFromLog(BlockState blockState) {
        if (mcMMO.getModManager().isCustomLog(blockState)) {
            return mcMMO.getModManager().getBlock(blockState).getXpGain();
        }

        return ExperienceConfig.getInstance().getXp(PrimarySkillType.WOODCUTTING, blockState.getType());
    }

    /**
     * Spawns harvest lumber bonus drops
     *
     * @param blockState Block being broken
     */
    protected void spawnHarvestLumberBonusDrops(@NotNull BlockState blockState) {
        Misc.spawnItemsFromCollection(getPlayer(), Misc.getBlockCenter(blockState), blockState.getBlock().getDrops(), ItemSpawnReason.BONUS_DROPS);
    }
}
