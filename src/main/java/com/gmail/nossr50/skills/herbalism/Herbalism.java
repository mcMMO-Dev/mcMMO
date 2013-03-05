package com.gmail.nossr50.skills.herbalism;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;

public class Herbalism {
    public static int farmersDietRankLevel1 = AdvancedConfig.getInstance().getFarmerDietRankChange();
    public static int farmersDietRankLevel2 = farmersDietRankLevel1 * 2;
    public static int farmersDietMaxLevel   = farmersDietRankLevel1 * 5;

    public static int greenThumbStageChangeLevel = AdvancedConfig.getInstance().getGreenThumbStageChange();
    public static int greenThumbStageMaxLevel    = greenThumbStageChangeLevel * 4;

    public static int    greenThumbMaxLevel  = AdvancedConfig.getInstance().getGreenThumbMaxLevel();
    public static double greenThumbMaxChance = AdvancedConfig.getInstance().getGreenThumbChanceMax();

    public static int    doubleDropsMaxLevel  = AdvancedConfig.getInstance().getHerbalismDoubleDropsMaxLevel();
    public static double doubleDropsMaxChance = AdvancedConfig.getInstance().getHerbalismDoubleDropsChanceMax();

    public static int    hylianLuckMaxLevel  = AdvancedConfig.getInstance().getHylianLuckMaxLevel();
    public static double hylianLuckMaxChance = AdvancedConfig.getInstance().getHylianLuckChanceMax();

    public static int    shroomThumbMaxLevel  = AdvancedConfig.getInstance().getShroomThumbMaxLevel();
    public static double shroomThumbMaxChance = AdvancedConfig.getInstance().getShroomThumbChanceMax();

    /**
     * Convert blocks affected by the Green Thumb & Green Terra abilities.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    protected static boolean convertGreenTerraBlocks(BlockState blockState) {
        switch (blockState.getType()) {
            case COBBLE_WALL:
            case SMOOTH_BRICK:
                blockState.setRawData((byte) 0x1);
                return true;

            case DIRT:
                blockState.setType(Material.GRASS);
                return true;

            case COBBLESTONE:
                blockState.setType(Material.MOSSY_COBBLESTONE);
                return true;

            default:
                return false;
        }
    }

    /**
     * Calculate the drop amounts for cacti & sugar cane based on the blocks above them.
     *
     * @param blockState The {@link BlockState} of the bottom block of the plant
     * @return the number of bonus drops to award from the blocks in this plant
     */
    protected static int calculateCatciAndSugarDrops(BlockState blockState) {
        Block block = blockState.getBlock();
        Material blockType = blockState.getType();
        int dropAmount = 0;

        // Handle the original block
        if (!mcMMO.placeStore.isTrue(blockState)) {
            dropAmount++;
        }

        // Handle the two blocks above it - cacti & sugar cane can only grow 3 high naturally
        for (int y = 1; y < 3; y++) {
            Block relativeBlock = block.getRelative(BlockFace.UP, y);
            Material relativeBlockType = relativeBlock.getType();

            // If the first one is air, so is the next one
            if (relativeBlockType == Material.AIR) {
                break;
            }

            if (relativeBlockType == blockType && !mcMMO.placeStore.isTrue(relativeBlock)) {
                dropAmount++;
            }
        }

        return dropAmount;
    }

    /**
     * Convert blocks affected by the Green Thumb & Green Terra abilities.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    protected static boolean convertShroomThumb(BlockState blockState) {
        switch (blockState.getType()) {
            case DIRT:
            case GRASS:
                blockState.setType(Material.MYCEL);
                return true;

            default:
                return false;
        }
    }
}
