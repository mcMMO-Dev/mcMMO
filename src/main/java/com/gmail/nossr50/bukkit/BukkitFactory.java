package com.gmail.nossr50.bukkit;

import com.gmail.nossr50.datatypes.items.BukkitMMOItem;
import com.gmail.nossr50.util.nbt.RawNBT;

/**
 * Used to convert or construct platform independent types into Bukkit types
 */
public class BukkitFactory {

    /**
     * Creates a BukkitMMOItem which contains Bukkit implementations for the type MMOItem
     * @return a new BukkitMMOItem
     */
    public static BukkitMMOItem createBukkitMMOItem(String namespaceKey, int amount, RawNBT rawNBT) {
        return new BukkitMMOItem(namespaceKey, amount, rawNBT);
    }

}
