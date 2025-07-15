package com.gmail.nossr50.skills.herbalism;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

public class Herbalism {

    /**
     * Convert blocks affected by the Green Thumb & Green Terra abilities.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    protected static boolean convertGreenTerraBlocks(BlockState blockState) {
        switch (blockState.getType()) {
            case COBBLESTONE_WALL:
                blockState.setType(Material.MOSSY_COBBLESTONE_WALL);
                return true;

            case STONE_BRICKS:
                blockState.setType(Material.MOSSY_STONE_BRICKS);
                return true;

            case DIRT:
            case DIRT_PATH:
                blockState.setType(Material.GRASS_BLOCK);
                return true;

            case COBBLESTONE:
                blockState.setType(Material.MOSSY_COBBLESTONE);
                return true;

            default:
                return false;
        }
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
            case GRASS_BLOCK:
            case DIRT_PATH:
                blockState.setType(Material.MYCELIUM);
                return true;

            default:
                return false;
        }
    }

}
