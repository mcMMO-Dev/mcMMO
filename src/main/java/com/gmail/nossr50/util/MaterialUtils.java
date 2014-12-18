package com.gmail.nossr50.util;

import org.bukkit.material.MaterialData;

import com.gmail.nossr50.mcMMO;

public final class MaterialUtils {
    private MaterialUtils() {}

    protected static boolean isOre(MaterialData data) {
        switch (data.getItemType()) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case GLOWING_REDSTONE_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case QUARTZ_ORE:
            case REDSTONE_ORE:
            case EMERALD_ORE:
                return true;
            default:
                return mcMMO.getModManager().isCustomOre(data);
        }
    }
}
