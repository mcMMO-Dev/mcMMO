package com.gmail.nossr50.skills.repair;


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
    private HashSet<RepairCost> repairItems;

    public RepairTransaction() {
        repairItems = new HashSet<>();
    }

    public void addRepairCost(RepairCost repairCost) {
        repairItems.add(repairCost);
    }

    public HashSet<RepairCost> getRepairItems() {
        return repairItems;
    }

    public void setRepairItems(HashSet<RepairCost> repairItems) {
        this.repairItems = repairItems;
    }
}
