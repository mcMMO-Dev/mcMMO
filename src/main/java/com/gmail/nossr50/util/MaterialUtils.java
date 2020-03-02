package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

public final class MaterialUtils {
    private MaterialUtils() {}

    protected static boolean isOre(Material data) {
        return mcMMO.getMaterialMapStore().isOre(data.getKey().getKey());
    }
}
