package com.gmail.nossr50.skills.repair.repairables;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface RepairableManager {
    /**
     * Register a repairable with the RepairManager
     *
     * @param repairable Repairable to register
     */
    void registerRepairable(Repairable repairable);

    /**
     * Register a list of repairables with the RepairManager
     *
     * @param repairables List<Repairable> to register
     */
    void registerRepairables(List<Repairable> repairables);

    /**
     * Checks if an item is repairable
     *
     * @param type Material to check if repairable
     * @return true if repairable, false if not
     */
    boolean isRepairable(Material type);

    /**
     * Checks if an item is repairable
     *
     * @param itemStack Item to check if repairable
     * @return true if repairable, false if not
     */
    boolean isRepairable(ItemStack itemStack);

    /**
     * Gets the repairable with this type
     *
     * @param type Material of the repairable to look for
     * @return the repairable, can be null
     */
    Repairable getRepairable(Material type);
}
