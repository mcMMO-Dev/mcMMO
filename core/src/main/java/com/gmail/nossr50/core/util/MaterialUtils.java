package com.gmail.nossr50.core.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

public final class MaterialUtils {
    private MaterialUtils() {}

    protected static boolean isOre(Material data) {
        switch (data) {
            case Material.COAL_ORE:
            case Material.DIAMOND_ORE:
            case Material.NETHER_QUARTZ_ORE:
            case Material.GOLD_ORE:
            case Material.IRON_ORE:
            case Material.LAPIS_ORE:
            case Material.REDSTONE_ORE:
            case Material.EMERALD_ORE:
                return true;
            default:
                return mcMMO.getModManager().isCustomOre(data);
        }
    }
}
