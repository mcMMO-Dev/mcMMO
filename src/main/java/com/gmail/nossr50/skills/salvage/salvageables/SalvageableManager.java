package com.gmail.nossr50.skills.salvage.salvageables;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface SalvageableManager {
    /**
     * Register a salvageable with the SalvageManager
     *
     * @param salvageable Salvageable to register
     */
    void registerSalvageable(Salvageable salvageable);

    /**
     * Register a list of salvageables with the SalvageManager
     *
     * @param salvageables List<Salvageable> to register
     */
    void registerSalvageables(List<Salvageable> salvageables);

    /**
     * Checks if an item is salvageable
     *
     * @param type Material to check if salvageable
     * @return true if salvageable, false if not
     */
    boolean isSalvageable(Material type);

    /**
     * Checks if an item is salvageable
     *
     * @param itemStack Item to check if salvageable
     * @return true if salvageable, false if not
     */
    boolean isSalvageable(ItemStack itemStack);

    /**
     * Gets the salvageable with this type
     *
     * @param type Material of the salvageable to look for
     * @return the salvageable, can be null
     */
    Salvageable getSalvageable(Material type);
}
