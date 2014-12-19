package com.gmail.nossr50.util.temp;

import org.bukkit.block.BlockState;

public class DualSupport {

    public static boolean canActivateAbilities(BlockState blockState) {
        String mat = blockState.getType().name();
        return !mat.equals("IRON_TRAPDOOR") && !mat.equals("ACACIA_DOOR") && !mat.equals("SPRUCE_DOOR") && !mat.equals("BIRCH_DOOR")
                && !mat.equals("JUNGLE_DOOR") && !mat.equals("DARK_OAK_DOOR") && !mat.equals("ACACIA_FENCE") && !mat.equals("DARK_OAK_FENCE")
                && !mat.equals("BIRCH_FENCE") && !mat.equals("JUNGLE_FENCE") && !mat.equals("ARMOR_STAND");
    }

    public static boolean affectedBySuperBreaker(BlockState blockState) {
        String mat = blockState.getType().name();
        return mat.equals("PRISMARINE") || mat.equals("RED_SANDSTONE");
    }

}
