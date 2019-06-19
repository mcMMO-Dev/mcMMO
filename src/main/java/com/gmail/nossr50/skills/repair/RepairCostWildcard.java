package com.gmail.nossr50.skills.repair;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Represents a piece of a RepairTransaction
 * Multiple RepairCost pieces are used to pay for a RepairTransaction
 * This one represents a wildcard cost, which can be paid for with multiple items
 *
 */
public class RepairCostWildcard implements RepairCost {

    private RepairWildcard repairWildcard;

    @Override
    public ItemStack findPayment(PlayerInventory playerInventory, boolean strictMatching) {
        for(ItemStack itemStack : playerInventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            for(ItemStack wildCardItem : repairWildcard.getMatchingItems()) {
                //Attempt to match the item in the inventory to any of the compatible repair items
                if(strictMatching) {
                    //TODO: Replace with strict matching code
                    if(itemStack.isSimilar(wildCardItem))
                        return itemStack;
                } else {
                    if(itemStack.getType() == wildCardItem.getType()) {
                        return itemStack;
                    }
                }
            }
        }

        return null;
    }

    public RepairWildcard getRepairWildcard() {
        return repairWildcard;
    }

    public void setRepairWildcard(RepairWildcard repairWildcard) {
        this.repairWildcard = repairWildcard;
    }

}
