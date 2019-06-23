package com.gmail.nossr50.bukkit;

import com.gmail.nossr50.datatypes.items.BukkitMMOItem;
import com.gmail.nossr50.datatypes.items.MMOItem;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.nbt.RawNBT;
import com.sk89q.jnbt.NBTUtils;
import org.bukkit.inventory.ItemStack;

/**
 * Used to convert or construct platform independent types into Bukkit types
 */
public class BukkitFactory {

    /**
     * Creates a BukkitMMOItem which contains Bukkit implementations for the type MMOItem
     * @return a new BukkitMMOItem
     */
    public static MMOItem<?> createItem(String namespaceKey, int amount, RawNBT rawNBT) {
        return new BukkitMMOItem(namespaceKey, amount, rawNBT);
    }

    public static MMOItem<?> createItem(ItemStack itemStack) {
        return createItem(itemStack.getType().getKey().toString(), itemStack.getAmount(), new RawNBT(mcMMO.getNbtManager().getNBT(itemStack).toString()));
    }

}
