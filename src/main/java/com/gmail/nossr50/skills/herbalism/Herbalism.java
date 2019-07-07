package com.gmail.nossr50.skills.herbalism;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

public class Herbalism {

    /**
     * Convert blocks affected by the Green Thumb & Green Terra abilities.
     *
     * @param blockState
     *            The {@link BlockState} to check ability activation for
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

            case DIRT :
            case GRASS_PATH :
                blockState.setType(Material.GRASS_BLOCK);
                return true;

            case COBBLESTONE :
                blockState.setType(Material.MOSSY_COBBLESTONE);
                return true;

            default :
                return false;
        }
    }

    /**
     * Convert blocks affected by the Green Thumb & Green Terra abilities.
     *
     * @param blockState
     *            The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    protected static boolean convertShroomThumb(BlockState blockState) {
        switch (blockState.getType()) {
            case DIRT :
            case GRASS_BLOCK:
            case GRASS_PATH :
                blockState.setType(Material.MYCELIUM);
                return true;

            default :
                return false;
        }
    }

    /**
     * Check if the block has a recently grown crop from Green Thumb
     *
     * @param blockState
     *            The {@link BlockState} to check green thumb regrown for
     * @return true if the block is recently regrown, false otherwise
     */
    public static boolean isRecentlyRegrown(BlockState blockState) {
        return blockState.hasMetadata(mcMMO.greenThumbDataKey) && !SkillUtils.cooldownExpired(blockState.getMetadata(mcMMO.greenThumbDataKey).get(0).asInt(), 1);
    }
}
