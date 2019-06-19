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
     * @param strictMatching whether or not to match repair cost items strictly with items in a players inventory
     * @return any compatible payment items if found
     */
    ItemStack findPayment(PlayerInventory playerInventory, boolean strictMatching);

}
