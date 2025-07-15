package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public interface Repairable {
    /**
     * Gets the type of this repairable item
     *
     * @return the type of this repairable
     */
    Material getItemMaterial();

    /**
     * Gets the id of the material used to repair this item
     *
     * @return the id of the repair material
     */
    Material getRepairMaterial();

    /**
     * Gets the pretty name of the material used to repair this item
     *
     * @return the pretty name of the repair material
     */
    String getRepairMaterialPrettyName();

    /**
     * Gets the RepairItemType value for this repairable item
     *
     * @return the RepairItemType for this repairable
     */
    ItemType getRepairItemType();

    /**
     * Gets the RepairMaterialType value for this repairable item
     *
     * @return the RepairMaterialType for this repairable
     */
    MaterialType getRepairMaterialType();

    /**
     * Gets the minimum quantity of repair materials ignoring all other repair bonuses
     * <p>
     * This is typically set to the number of items needed to create that item, for example 5 for
     * helmets or 2 for swords
     *
     * @return the minimum number of items
     */
    int getMinimumQuantity();

    /**
     * Gets the maximum durability of this item before it breaks
     *
     * @return the maximum durability
     */
    short getMaximumDurability();

    /**
     * Gets the base repair durability on which to calculate bonuses.
     * <p>
     * This is actually the maximum durability divided by the minimum quantity
     *
     * @return the base repair durability
     */
    short getBaseRepairDurability(ItemStack itemStack);

    /**
     * Gets the minimum repair level needed to repair this item
     *
     * @return the minimum level to repair this item, or 0 for no minimum
     */
    int getMinimumLevel();

    /**
     * Gets the xpMultiplier for this repairable
     *
     * @return the xpMultiplier of this repairable
     */
    double getXpMultiplier();
}
