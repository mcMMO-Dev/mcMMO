package com.gmail.nossr50.core.mcmmo.inventory;

/**
 * Represents something that has an inventory
 */
public interface InventoryHolder {
    /**
     * Gets the inventory for this entity
     * @return this inventory
     */
    Inventory getInventory();
}
