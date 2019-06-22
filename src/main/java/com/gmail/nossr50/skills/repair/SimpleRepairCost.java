package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.datatypes.items.BukkitMMOItem;
import com.gmail.nossr50.datatypes.items.ItemMatch;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

/**
 * Implementation of RepairCost
 *
 * A SimpleRepairCost can be one or more items, any one of which can be used to pay for a RepairTransaction
 * If a SimpleRepairCost is more than one item, then only one of the items are required to represent its cost.
 *
 * This type is strictly for use with RepairTransaction, which represents the full cost of a Repair.
 * @see com.gmail.nossr50.skills.repair.RepairTransaction for more details
 */
public class SimpleRepairCost<T extends ItemMatch> implements RepairCost {

    private T itemMatch;

    public SimpleRepairCost(T customItemTarget) {
        this.itemMatch = customItemTarget;
    }

    @Override
    public ItemStack findPayment(PlayerInventory playerInventory) {
        for(ItemStack itemStack : playerInventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            BukkitMMOItem playerInventoryItem = new BukkitMMOItem(itemStack);

            //TODO:
            //TODO:
            //TODO:
            //TODO:
            //TODO: Write the code that compares playerInventoryItem with the <T extends itemMatch>
            //TODO:
            //TODO:
            //TODO:
            //TODO:
            //TODO:
            //TODO:
            //TODO:

            //If the item matches return it
            if(itemMatch.isMatch(playerInventoryItem))
                return itemStack;
        }

        return null;
    }

    public ItemMatch getItemMatch() {
        return itemMatch;
    }

    @Override
    public boolean hasPayment(PlayerInventory playerInventory) {
        return findPayment(playerInventory) != null;
    }
}
