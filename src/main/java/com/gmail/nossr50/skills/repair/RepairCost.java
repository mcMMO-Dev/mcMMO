package com.gmail.nossr50.skills.repair;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Represents one item in a Repair Transaction
 */
public interface RepairCost {

    /**
     * Searches a player inventory for a matching ItemStack that can be used to pay for the repair transaction
     * @param playerInventory inventory of player attempting to pay the cost
     * @return any compatible payment items if found
     */
    ItemStack findPayment(PlayerInventory playerInventory);

    /**
     * Whether or not this repair cost is strictly matched
     * Strict matching compares Items by using metadata and material type
     * @return true if the RepairCost uses strict matching
     */
    boolean hasStrictMatching();

}
