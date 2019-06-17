package com.gmail.nossr50.skills.repair;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents one item in a Repair Transaction
 */
public class RepairCost {

    private final ArrayList<ItemStack> compatibleRepairItems;

    public RepairCost(ArrayList<ItemStack> compatibleRepairItems) {
        this.compatibleRepairItems = compatibleRepairItems;
    }

    public RepairCost(RepairWildcard repairWildcard) {
        compatibleRepairItems = new ArrayList<>();
        compatibleRepairItems.addAll(repairWildcard.getMatchingItems());
    }

    public RepairCost(ItemStack repairItem) {
        compatibleRepairItems = new ArrayList<>();
        compatibleRepairItems.add(repairItem);
    }

    public ItemStack getCost(PlayerInventory playerInventory, boolean strictMatching) {
        for(ItemStack itemStack : playerInventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            //Attempt to match the item in the inventory to any of the compatible repair items
            for(ItemStack repairItem : compatibleRepairItems) {
                if(strictMatching) {
                    if(itemStack.isSimilar(repairItem))
                        return itemStack;
                } else {
                    if(itemStack.getType() == repairItem.getType()) {
                        return itemStack;
                    }
                }
            }

        }

        return null;
    }

    public ArrayList<ItemStack> getCompatibleRepairItems() {
        return compatibleRepairItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepairCost)) return false;
        RepairCost that = (RepairCost) o;
        return getCompatibleRepairItems().equals(that.getCompatibleRepairItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCompatibleRepairItems());
    }
}
