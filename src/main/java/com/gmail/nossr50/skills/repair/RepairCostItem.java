package com.gmail.nossr50.skills.repair;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class RepairCostItem implements RepairCost {

    private ItemStack repairCostItem;

    public RepairCostItem(ItemStack repairCostItem) {
        this.repairCostItem = repairCostItem;
    }

    @Override
    public ItemStack findPayment(PlayerInventory playerInventory, boolean strictMatching) {
        for(ItemStack itemStack : playerInventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            //Attempt to match the item in the inventory to any of the compatible repair items
            if(strictMatching) {
                //TODO: Replace with strict matching code
                if(itemStack.isSimilar(repairCostItem))
                    return itemStack;
            } else {
                if(itemStack.getType() == repairCostItem.getType()) {
                    return itemStack;
                }
            }
        }

        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(repairCostItem);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepairCostItem)) return false;
        RepairCostItem that = (RepairCostItem) o;
        return repairCostItem.equals(that.repairCostItem);
    }
}
