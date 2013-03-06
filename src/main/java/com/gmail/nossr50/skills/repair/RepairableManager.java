package com.gmail.nossr50.skills.repair;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public interface RepairableManager {
    /**
     * Register a repairable with the RepairManager
     *
     * @param repairable Repairable to register
     */
    public void registerRepairable(Repairable repairable);

    /**
     * Register a list of repairables with the RepairManager
     *
     * @param repairables List<Repairable> to register
     */
    public void registerRepairables(List<Repairable> repairables);

    /**
     * Checks if an item is repairable
     *
     * @param itemId id to check if repairable
     * @return true if repairable, false if not
     */
    public boolean isRepairable(int itemId);

    /**
     * Checks if an item is repairable
     *
     * @param itemStack Item to check if repairable
     * @return true if repairable, false if not
     */
    public boolean isRepairable(ItemStack itemStack);

    /**
     * Gets the repairable with this id
     *
     * @param id Id of the repairable to look for
     * @return the repairable, can be null
     */
    public Repairable getRepairable(int id);
}
