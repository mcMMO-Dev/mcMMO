package com.gmail.nossr50.skills.woodcutting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
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
                return Config.getInstance().getWoodcuttingXPHugeBrownMushroom();

            case HUGE_MUSHROOM_2:
                return Config.getInstance().getWoodcuttingXPHugeRedMushroom();

            default:
                break;
        }

        if (ModUtils.isCustomLogBlock(blockState)) {
            return ModUtils.getCustomBlock(blockState).getXpGain();
        }

        switch (((Tree) blockState.getData()).getSpecies()) {
            case GENERIC:
                return Config.getInstance().getWoodcuttingXPOak();

            case REDWOOD:
                return Config.getInstance().getWoodcuttingXPSpruce();

            case BIRCH:
                return Config.getInstance().getWoodcuttingXPBirch();

            case JUNGLE:
                int xp = Config.getInstance().getWoodcuttingXPJungle();

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
     * @param mcMMOPlayer Player breaking the block
     * @param blockState Block being broken
     */
    protected static void checkForDoubleDrop(BlockState blockState) {
        if (ModUtils.isCustomLogBlock(blockState)) {
            CustomBlock customBlock = ModUtils.getCustomBlock(blockState);
            int minimumDropAmount = customBlock.getMinimumDropAmount();
            int maximumDropAmount = customBlock.getMaximumDropAmount();
            Location location = blockState.getLocation();
            ItemStack item = customBlock.getItemDrop();

            Misc.dropItems(location, item, minimumDropAmount);

            if (minimumDropAmount != maximumDropAmount) {
                Misc.randomDropItems(location, item, maximumDropAmount - minimumDropAmount);
            }
        }
        else {
            Location location = blockState.getLocation();
            Tree tree = (Tree) blockState.getData();
            ItemStack item = new ItemStack(Material.LOG, 1, tree.getSpecies().getData());

            switch (((Tree) blockState.getData()).getSpecies()) {
                case GENERIC:
                    if (Config.getInstance().getOakDoubleDropsEnabled()) {
                        Misc.dropItem(location, item);
                    }
                    return;

                case REDWOOD:
                    if (Config.getInstance().getSpruceDoubleDropsEnabled()) {
                        Misc.dropItem(location, item);
                    }
                    return;

                case BIRCH:
                    if (Config.getInstance().getBirchDoubleDropsEnabled()) {
                        Misc.dropItem(location, item);
                    }
                    return;

                case JUNGLE:
                    if (Config.getInstance().getJungleDoubleDropsEnabled()) {
                        Misc.dropItem(location, item);
                    }
                    return;

                default:
                    return;
            }
        }
    }

    /**
     * Processes Tree Feller for generic Trees
     *
     * @param blockState Block being checked
     * @param treeFellerBlocks List of blocks to be removed
     */
    protected static void processRegularTrees(BlockState blockState, List<BlockState> treeFellerBlocks) {
        List<BlockState> futureCenterBlocks = new ArrayList<BlockState>();

        // Handle the blocks around 'block'
        for (int y = 0; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockState nextBlock = blockState.getBlock().getRelative(x, y, z).getState();
                    handleBlock(nextBlock, futureCenterBlocks, treeFellerBlocks);

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

            processRegularTrees(futureCenterBlock, treeFellerBlocks);
        }
    }

    /**
     * Processes Tree Feller for Red Mushrooms (Dome Shaped)
     *
     * @param blockState Block being checked
     * @param treeFellerBlocks List of blocks to be removed
     */
    protected static void processRedMushroomTrees(BlockState blockState, List<BlockState> treeFellerBlocks) {
        List<BlockState> futureCenterBlocks = new ArrayList<BlockState>();

        // Handle the blocks around 'block'
        for (int y = 0; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockState nextBlock = blockState.getBlock().getRelative(x, y, z).getState();
                    BlockState otherNextBlock = blockState.getBlock().getRelative(x, y - (y * 2), z).getState();

                    handleBlock(nextBlock, futureCenterBlocks, treeFellerBlocks);
                    handleBlock(otherNextBlock, futureCenterBlocks, treeFellerBlocks);

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

            processRedMushroomTrees(futureCenterBlock, treeFellerBlocks);
        }
    }

    /**
     * Handles the durability loss
     *
     * @param treeFellerBlocks List of blocks to be removed
     * @param inHand tool being used
     * @return True if the tool can sustain the durability loss
     */
    protected static boolean handleDurabilityLoss(List<BlockState> treeFellerBlocks, ItemStack inHand) {
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
        short maxDurability = ModUtils.isCustomTool(inHand) ? ModUtils.getToolFromItemStack(inHand).getDurability() : inHandMaterial.getMaxDurability();

        if (finalDurability >= maxDurability) {
            inHand.setDurability(maxDurability);
            return false;
        }

        inHand.setDurability(finalDurability);
        return true;
    }

    /**
     * Handle a block addition to the list of blocks to be removed and to the list of blocks used for future recursive calls of 'processRecursively()'
     *
     * @param blockState Block to be added
     * @param futureCenterBlocks List of blocks that will be used to call 'processRecursively()'
     * @param treeFellerBlocks List of blocks to be removed
     */
    private static void handleBlock(BlockState blockState, List<BlockState> futureCenterBlocks, List<BlockState> treeFellerBlocks) {
        if (!BlockUtils.affectedByTreeFeller(blockState) || mcMMO.getPlaceStore().isTrue(blockState) || treeFellerBlocks.contains(blockState)) {
            return;
        }

        treeFellerBlocks.add(blockState);

        if (treeFellerBlocks.size() > treeFellerThreshold) {
            WoodcuttingManager.treeFellerReachedThreshold = true;
            return;
        }

        // Without this check Tree Feller propagates through leaves until the threshold is hit
        if (BlockUtils.isLog(blockState)) {
            futureCenterBlocks.add(blockState);
        }
    }
}
