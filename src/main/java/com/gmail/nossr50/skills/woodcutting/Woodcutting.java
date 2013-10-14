package com.gmail.nossr50.skills.woodcutting;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModUtils;

public final class Woodcutting {
    public static int    doubleDropsMaxLevel  = AdvancedConfig.getInstance().getWoodcuttingDoubleDropMaxLevel();
    public static double doubleDropsMaxChance = AdvancedConfig.getInstance().getWoodcuttingDoubleDropChance();

    public static int leafBlowerUnlockLevel = AdvancedConfig.getInstance().getLeafBlowUnlockLevel();
    public static int treeFellerThreshold = Config.getInstance().getTreeFellerThreshold();

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
        // Mushrooms aren't trees so we could never get species data from them
        switch (blockState.getType()) {
            case HUGE_MUSHROOM_1:
                return ExperienceConfig.getInstance().getWoodcuttingXPHugeBrownMushroom();

            case HUGE_MUSHROOM_2:
                return ExperienceConfig.getInstance().getWoodcuttingXPHugeRedMushroom();

            default:
                break;
        }

        if (ModUtils.isCustomLogBlock(blockState)) {
            return ModUtils.getCustomBlock(blockState).getXpGain();
        }

        switch (((Tree) blockState.getData()).getSpecies()) {
            case GENERIC:
                return ExperienceConfig.getInstance().getWoodcuttingXPOak();

            case REDWOOD:
                return ExperienceConfig.getInstance().getWoodcuttingXPSpruce();

            case BIRCH:
                return ExperienceConfig.getInstance().getWoodcuttingXPBirch();

            case JUNGLE:
                int xp = ExperienceConfig.getInstance().getWoodcuttingXPJungle();

                if (experienceGainMethod == ExperienceGainMethod.TREE_FELLER) {
                    xp *= 0.5;
                }

                return xp;

            default:
                return 0;
        }
    }

    /**
     * Checks for double drops
     *
     * @param blockState Block being broken
     */
    protected static void checkForDoubleDrop(BlockState blockState) {
        if (ModUtils.isCustomLogBlock(blockState) && ModUtils.getCustomBlock(blockState).isDoubleDropEnabled()) {
            Misc.dropItems(blockState.getLocation(), blockState.getBlock().getDrops());
        }
        else {
            switch (((Tree) blockState.getData()).getSpecies()) {
                case GENERIC:
                    if (Config.getInstance().getOakDoubleDropsEnabled()) {
                        Misc.dropItems(blockState.getLocation(), blockState.getBlock().getDrops());
                    }
                    return;

                case REDWOOD:
                    if (Config.getInstance().getSpruceDoubleDropsEnabled()) {
                        Misc.dropItems(blockState.getLocation(), blockState.getBlock().getDrops());
                    }
                    return;

                case BIRCH:
                    if (Config.getInstance().getBirchDoubleDropsEnabled()) {
                        Misc.dropItems(blockState.getLocation(), blockState.getBlock().getDrops());
                    }
                    return;

                case JUNGLE:
                    if (Config.getInstance().getJungleDoubleDropsEnabled()) {
                        Misc.dropItems(blockState.getLocation(), blockState.getBlock().getDrops());
                    }
                    return;

                default:
                    return;
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
    protected static void processTree(BlockState blockState, LinkedHashSet<BlockState> treeFellerBlocks) {
        List<BlockState> futureCenterBlocks = new ArrayList<BlockState>();

        // Check the block up and take different behavior (smaller search) if it's a log
        if (handleBlock(blockState.getBlock().getRelative(BlockFace.UP).getState(), futureCenterBlocks, treeFellerBlocks)) {
            for (int[] dir : directions) {
                handleBlock(blockState.getBlock().getRelative(dir[0], 0, dir[1]).getState(), futureCenterBlocks, treeFellerBlocks);

                if (WoodcuttingManager.treeFellerReachedThreshold) {
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

                    if (WoodcuttingManager.treeFellerReachedThreshold) {
                        return;
                    }
                }
            }
        }

        // Recursive call for each log found
        for (BlockState futureCenterBlock : futureCenterBlocks) {
            if (WoodcuttingManager.treeFellerReachedThreshold) {
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
    protected static boolean handleDurabilityLoss(LinkedHashSet<BlockState> treeFellerBlocks, ItemStack inHand) {
        Material inHandMaterial = inHand.getType();

        if (inHandMaterial == Material.AIR) {
            return false;
        }

        short durabilityLoss = 0;
        int unbreakingLevel = inHand.getEnchantmentLevel(Enchantment.DURABILITY);

        for (BlockState blockState : treeFellerBlocks) {
            if (BlockUtils.isLog(blockState) && Misc.getRandom().nextInt(unbreakingLevel + 1) == 0) {
                durabilityLoss += Config.getInstance().getAbilityToolDamage();
            }
        }

        short finalDurability = (short) (inHand.getDurability() + durabilityLoss);
        short maxDurability = inHandMaterial.getMaxDurability();
        boolean overMax = (finalDurability >= maxDurability);

        inHand.setDurability(overMax ? maxDurability : finalDurability);
        return !overMax;
    }

    /**
     * Handle a block addition to the list of blocks to be removed and to the
     * list of blocks used for future recursive calls of
     * 'processRecursively()'
     *
     * @param blockState Block to be added
     * @param futureCenterBlocks List of blocks that will be used to call
     *     'processRecursively()'
     * @param treeFellerBlocks List of blocks to be removed
     * @return true if and only if the given blockState was a Log not already
     *     in treeFellerBlocks.
     */
    private static boolean handleBlock(BlockState blockState, List<BlockState> futureCenterBlocks, LinkedHashSet<BlockState> treeFellerBlocks) {
        if (mcMMO.getPlaceStore().isTrue(blockState) || treeFellerBlocks.contains(blockState)) {
            return false;
        }

        if (treeFellerBlocks.size() > treeFellerThreshold) {
            WoodcuttingManager.treeFellerReachedThreshold = true;
        }

        // Without this check Tree Feller propagates through leaves until the threshold is hit
        if (BlockUtils.isLog(blockState)) {
            treeFellerBlocks.add(blockState);
            futureCenterBlocks.add(blockState);
            return true;
        } else if (BlockUtils.isLeaves(blockState)) {
            treeFellerBlocks.add(blockState);
            return false;
        }
        return false;
    }
}
