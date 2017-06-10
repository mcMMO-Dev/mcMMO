package com.gmail.nossr50.skills.woodcutting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.SkillUtils;

public final class Woodcutting {
    public static int leafBlowerUnlockLevel = AdvancedConfig.getInstance().getLeafBlowUnlockLevel();
    public static int treeFellerThreshold = Config.getInstance().getTreeFellerThreshold();

    protected static boolean treeFellerReachedThreshold = false;

    protected enum ExperienceGainMethod {
        DEFAULT,
        TREE_FELLER,
    };

    private Woodcutting() {}

    /**
     * Retrieves the experience reward from a log
     *
     * @param blockState Log being broken
     * @param experienceGainMethod How the log is being broken
     * @return Amount of experience
     */
    protected static int getExperienceFromLog(BlockState blockState, ExperienceGainMethod experienceGainMethod) {
        if (mcMMO.getModManager().isCustomLog(blockState)) {
            return mcMMO.getModManager().getBlock(blockState).getXpGain();
        }

        return ExperienceConfig.getInstance().getXp(SkillType.WOODCUTTING, blockState.getData());
    }

    /**
     * Checks for double drops
     *
     * @param blockState Block being broken
     */
    protected static void checkForDoubleDrop(BlockState blockState) {
        if (mcMMO.getModManager().isCustomLog(blockState) && mcMMO.getModManager().getBlock(blockState).isDoubleDropEnabled()) {
            Misc.dropItems(Misc.getBlockCenter(blockState), blockState.getBlock().getDrops());
        }
        else {
            //TODO Remove this workaround when casting to Tree works again
            TreeSpecies species = TreeSpecies.GENERIC;
            if (blockState.getData() instanceof Tree) {
                species = ((Tree) blockState.getData()).getSpecies();
            }
            if (blockState.getType() == Material.LOG_2) {
                byte data = blockState.getRawData();
                if ((data & 1) != 0) {
                    species = TreeSpecies.ACACIA;
                }
                if ((data & 2) != 0) {
                    species = TreeSpecies.DARK_OAK;
                }
            }

            if (Config.getInstance().getWoodcuttingDoubleDropsEnabled(species)) {
                Misc.dropItems(Misc.getBlockCenter(blockState), blockState.getBlock().getDrops());
            }
        }
    }

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
     * handleBlock() returns a boolean, which is used for the sole purpose of
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
    protected static void processTree(BlockState blockState, Set<BlockState> treeFellerBlocks) {
        List<BlockState> futureCenterBlocks = new ArrayList<BlockState>();

        // Check the block up and take different behavior (smaller search) if it's a log
        if (handleBlock(blockState.getBlock().getRelative(BlockFace.UP).getState(), futureCenterBlocks, treeFellerBlocks)) {
            for (int[] dir : directions) {
                handleBlock(blockState.getBlock().getRelative(dir[0], 0, dir[1]).getState(), futureCenterBlocks, treeFellerBlocks);

                if (treeFellerReachedThreshold) {
                    return;
                }
            }
        }
        else {
            // Cover DOWN
            handleBlock(blockState.getBlock().getRelative(BlockFace.DOWN).getState(), futureCenterBlocks, treeFellerBlocks);
            // Search in a cube
            for (int y = -1; y <= 1; y++) {
                for (int[] dir : directions) {
                    handleBlock(blockState.getBlock().getRelative(dir[0], y, dir[1]).getState(), futureCenterBlocks, treeFellerBlocks);

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
     * @return True if the tool can sustain the durability loss
     */
    protected static boolean handleDurabilityLoss(Set<BlockState> treeFellerBlocks, ItemStack inHand) {
        short durabilityLoss = 0;
        Material type = inHand.getType();

        for (BlockState blockState : treeFellerBlocks) {
            if (BlockUtils.isLog(blockState)) {
                durabilityLoss += Config.getInstance().getAbilityToolDamage();
            }
        }

        SkillUtils.handleDurabilityChange(inHand, durabilityLoss);
        return (inHand.getDurability() < (mcMMO.getRepairableManager().isRepairable(type) ? mcMMO.getRepairableManager().getRepairable(type).getMaximumDurability() : type.getMaxDurability()));
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
    private static boolean handleBlock(BlockState blockState, List<BlockState> futureCenterBlocks, Set<BlockState> treeFellerBlocks) {
        if (treeFellerBlocks.contains(blockState) || mcMMO.getPlaceStore().isTrue(blockState)) {
            return false;
        }

        // Without this check Tree Feller propagates through leaves until the threshold is hit
        if (treeFellerBlocks.size() > treeFellerThreshold) {
            treeFellerReachedThreshold = true;
        }

        if (BlockUtils.isLog(blockState)) {
            treeFellerBlocks.add(blockState);
            futureCenterBlocks.add(blockState);
            return true;
        }
        else if (BlockUtils.isLeaves(blockState)) {
            treeFellerBlocks.add(blockState);
            return false;
        }
        return false;
    }
}
