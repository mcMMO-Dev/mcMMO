package com.gmail.nossr50.core.mcmmo.inventory;

import com.gmail.nossr50.core.mcmmo.item.ItemStack;

public interface Inventory {
    /**
     * Grab the entire Inventory
     * @return this inventory
     */
    ItemStack[] getInventory();

    /**
     * Sets the inventory
     * @param inventory new inventory
     */
    void setInventory(ItemStack[] inventory);
}
