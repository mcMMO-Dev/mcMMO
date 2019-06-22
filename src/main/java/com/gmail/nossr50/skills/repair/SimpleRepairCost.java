package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.datatypes.items.BukkitMMOItem;
import com.gmail.nossr50.datatypes.items.CustomItemTarget;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Implementation of RepairCost
 *
 * A SimpleRepairCost can be one or more items, any one of which can be used to pay for a RepairTransaction
 * If a SimpleRepairCost is more than one item, then only one of the items are required to represent its cost.
 *
 * This type is strictly for use with RepairTransaction, which represents the full cost of a Repair.
 * @see com.gmail.nossr50.skills.repair.RepairTransaction for more details
 */
public class SimpleRepairCost implements RepairCost {

    private CustomItemTarget desiredItemTarget;

    public SimpleRepairCost(CustomItemTarget customItemTarget) {
        this.desiredItemTarget = customItemTarget;
    }

    @Override
    public ItemStack findPayment(PlayerInventory playerInventory) {
        for(ItemStack itemStack : playerInventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            BukkitMMOItem playerInventoryItem = new BukkitMMOItem(itemStack);

            //If the item matches return it
            if(desiredItemTarget.isMatch(playerInventoryItem))
                return itemStack;
        }

        return null;
    }

    @Override
    public boolean hasPayment(PlayerInventory playerInventory) {
        return findPayment(playerInventory) != null;
    }
}
