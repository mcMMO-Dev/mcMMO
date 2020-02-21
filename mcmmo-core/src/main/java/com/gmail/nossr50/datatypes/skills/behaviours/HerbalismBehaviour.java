package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class HerbalismBehaviour {

    private final mcMMO pluginRef;

    public HerbalismBehaviour(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    /**
     * Convert blocks affected by the Green Thumb & Green Terra abilities.
     *
     * @param blockState
     *            The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean convertGreenTerraBlocks(BlockState blockState) {
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
    public boolean convertShroomThumb(BlockState blockState) {
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

}
