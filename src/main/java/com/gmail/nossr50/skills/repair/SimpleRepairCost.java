package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.datatypes.items.ItemMatchProperty;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

/**
 * Implementation of RepairCost
 * <p>
 * A SimpleRepairCost can be one or more items, any one of which can be used to pay for a RepairTransaction
 * If a SimpleRepairCost is more than one item, then only one of the items are required to represent its cost.
 *
 * This type is strictly for use with RepairTransaction, which represents the full cost of a Repair.
 * @see com.gmail.nossr50.skills.repair.RepairTransaction for more details
 */
public class SimpleRepairCost implements RepairCost {

    private ItemMatchProperty itemMatchProperty;

    public SimpleRepairCost(ItemMatchProperty itemMatchProperty) {
        this.itemMatchProperty = itemMatchProperty;
    }

    @Override
    public ItemStack findPayment(PlayerInventory playerInventory) {
        for(ItemStack itemStack : playerInventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            //Attempt to match the item in the inventory to any of the compatible repair items
            if(hasStrictMatching()) {
                //TODO: Replace with strict matching code
                if(item)
                    return itemStack;
            } else {
                if(itemStack.getType() == itemMatchProperty.getType()) {
                    return itemStack;
                }
            }
        }

        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemMatchProperty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleRepairCost)) return false;
        SimpleRepairCost that = (SimpleRepairCost) o;
        return itemMatchProperty.equals(that.itemMatchProperty);
    }

    @Override
    public boolean hasStrictMatching() {
        return strictMatching;
    }
}
