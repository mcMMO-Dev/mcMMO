package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.bukkit.BukkitFactory;
import com.gmail.nossr50.datatypes.items.ItemMatch;
import com.gmail.nossr50.datatypes.items.MMOItem;
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
public class SimpleRepairCost<T extends ItemMatch> implements RepairCost<ItemMatch<?>> {

    private T itemMatch;

    public SimpleRepairCost(T customItemTarget) {
        this.itemMatch = customItemTarget;
    }

    @Override
    public T findPayment(PlayerInventory playerInventory) {
        for(ItemStack itemStack : playerInventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            MMOItem<T> playerInventoryItem = (MMOItem<T>) BukkitFactory.createItem(itemStack);

            if(itemMatch.isMatch(playerInventoryItem)) {
                //Item is a match
                return (T) playerInventoryItem;
            }

        }

        return null;
    }

    public T getItemMatch() {
        return itemMatch;
    }

    @Override
    public boolean hasPayment(PlayerInventory playerInventory) {
        return findPayment(playerInventory) != null;
    }
}
