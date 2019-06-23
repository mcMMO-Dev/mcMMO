package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.items.ItemMatch;
import com.gmail.nossr50.datatypes.items.ItemWildcards;
import com.gmail.nossr50.datatypes.items.MMOItem;
import com.gmail.nossr50.skills.repair.RepairCost;
import com.gmail.nossr50.skills.repair.SimpleRepairCost;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;

public class MultiRepairCost<T extends MMOItem<T>, U extends ItemMatch<T>> implements RepairCost<U> {

    //Multiple potential item matches
    private Set<SimpleRepairCost<U>> repairCostWildcards;

    public MultiRepairCost(ItemWildcards<T> itemWildcards) {
        repairCostWildcards = new HashSet<>();
        for(ItemMatch<T> wildcard : itemWildcards.getItemTargets()) {
            SimpleRepairCost<U> simpleRepairCost = new SimpleRepairCost<U>((U)wildcard);
            repairCostWildcards.add(simpleRepairCost);
        }
    }

    @Override
    public U findPayment(PlayerInventory playerInventory) {
        for(SimpleRepairCost simpleRepairCost : repairCostWildcards) {
            if(simpleRepairCost.findPayment(playerInventory) != null) {
                return (U) simpleRepairCost.findPayment(playerInventory);
            }
        }

        return null;
    }

    @Override
    public boolean hasPayment(PlayerInventory playerInventory) {
        return findPayment(playerInventory) != null;
    }
}
