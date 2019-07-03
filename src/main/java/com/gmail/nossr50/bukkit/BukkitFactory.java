package com.gmail.nossr50.bukkit;

import com.gmail.nossr50.datatypes.items.BukkitMMOItem;
import com.gmail.nossr50.datatypes.items.MMOItem;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.nbt.RawNBT;
import org.bukkit.inventory.ItemStack;

/**
 * Used to convert or construct platform independent types into Bukkit types
 */
public class BukkitFactory {

    private final mcMMO pluginRef;

    public BukkitFactory(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    /**
     * Creates a BukkitMMOItem which contains Bukkit implementations for the type MMOItem
     * @return a new BukkitMMOItem
     */
    public MMOItem<?> createItem(String namespaceKey, int amount, RawNBT rawNBT) {
        return new BukkitMMOItem(namespaceKey, amount, rawNBT);
    }

    public MMOItem<?> createItem(ItemStack itemStack) {
        return createItem(itemStack.getType().getKey().toString(), itemStack.getAmount(), new RawNBT(pluginRef.getNbtManager().getNBT(itemStack).toString()));
    }

}
