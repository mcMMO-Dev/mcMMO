package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.block.data.BlockData;

public final class MaterialUtils {
    private MaterialUtils() {}

    protected static boolean isOre(BlockData data) {
        switch (data.getMaterial()) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case NETHER_QUARTZ_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case EMERALD_ORE:
                return true;
            default:
                return mcMMO.getModManager().isCustomOre(data);
        }
    }
}
