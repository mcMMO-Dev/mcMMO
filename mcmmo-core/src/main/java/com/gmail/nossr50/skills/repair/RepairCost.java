//package com.gmail.nossr50.skills.repair;
//
//import com.gmail.nossr50.datatypes.items.ItemMatch;
//import org.bukkit.inventory.PlayerInventory;
//
///**
// * Represents one item in a Repair Transaction
// */
//public interface RepairCost {
//
//    /**
//     * Searches a player inventory for a matching ItemStack that can be used to pay for the repair transaction
//     * @param playerInventory inventory of player attempting to pay the cost
//     * @return any compatible payment items if found, can be null
//     */
//    ItemMatch<?> findPayment(PlayerInventory playerInventory);
//
//    /**
//     * Whether or not there is an item that can be used for this repair cost in the player's inventory
//     * @param playerInventory target player
//     * @return true if payment is found
//     */
//    boolean hasPayment(PlayerInventory playerInventory);
//
//}
