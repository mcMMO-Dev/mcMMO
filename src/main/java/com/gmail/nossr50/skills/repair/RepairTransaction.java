package com.gmail.nossr50.skills.repair;


import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;

/**
 * Represents a complete "repair transaction"
 *
 * I will define a "repair transaction" as such
 * - The items used to pay the cost of repairing an item in mcMMO via the Repair Skill
 *
 * A single "RepairTransaction" is made up of a multiple RepairCost objects
 * No two RepairCosts contained within this type can be exact duplicates
 *
 * A RepairCost is used to find a matching ItemStack in a players inventory if one exists to pay its cost
 *
 * A RepairCost can be a single item or it can be multiple items representing a range of compatible items
 *  to pay that part of the RepairTransaction
 */
public class RepairTransaction {
    private HashSet<RepairCost> repairCosts;

    public RepairTransaction() {
        repairCosts = new HashSet<>();
    }

    public void addRepairCost(RepairCost repairCost) {
        repairCosts.add(repairCost);
    }

    public HashSet<RepairCost> getRepairCosts() {
        return repairCosts;
    }

    public void setRepairCosts(HashSet<RepairCost> repairItems) {
        this.repairCosts = repairItems;
    }

    public boolean canPayRepairCosts(PlayerInventory playerInventory) {
        for(RepairCost repairCost : repairCosts) {
            if(!repairCost.hasPayment(playerInventory)) {
                return false;
            }
        }

        return true;
    }
}
