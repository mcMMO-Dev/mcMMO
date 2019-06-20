package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.datatypes.items.ItemWildcards;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;

/**
 * Represents a piece of a RepairTransaction
 * Multiple RepairCost pieces are used to pay for a RepairTransaction
 * This one represents a wildcard cost, which can be paid for with multiple items
 *
 */
public class RepairCostWildcard implements RepairCost {

    private HashSet<SimpleRepairCost> simpleRepairCosts;
    private ItemWildcards itemWildcards;


    public RepairCostWildcard(ItemWildcards itemWildcards) {
        this.itemWildcards = itemWildcards;
        simpleRepairCosts = new HashSet<>();

        for(ItemStack itemStack : )
    }

    @Override
    public ItemStack findPayment(PlayerInventory playerInventory) {
        for(ItemStack itemStack : playerInventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            for(SimpleRepairCost simpleRepairCost : simpleRepairCosts) {
                //Attempt to match the item in the inventory to any of the compatible repair items
                if(simpleRepairCost.findPayment(playerInventory) != null) {
                    return simpleRepairCost.findPayment(playerInventory);
                }
            }
        }

        return null;
    }

    public ItemWildcards getItemWildcards() {
        return itemWildcards;
    }

    public void setItemWildcards(ItemWildcards itemWildcards) {
        this.itemWildcards = itemWildcards;
    }

}
