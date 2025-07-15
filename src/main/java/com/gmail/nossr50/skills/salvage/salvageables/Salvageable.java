package com.gmail.nossr50.skills.salvage.salvageables;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import org.bukkit.Material;

public interface Salvageable {
    /**
     * Gets the type of this salvageable item
     *
     * @return the type of this salvageable
     */
    Material getItemMaterial();

    /**
     * Gets the material of the items dropped when salvaging this item
     *
     * @return the material of the salvage drop
     */
    Material getSalvageMaterial();

    /**
     * Gets the ItemType value for this salvageable item
     *
     * @return the ItemType for this salvageable
     */
    ItemType getSalvageItemType();

    /**
     * Gets the MaterialType value for this salvageable item
     *
     * @return the MaterialType for this salvageable
     */
    MaterialType getSalvageMaterialType();

    /**
     * Gets the maximum quantity of salvage materials ignoring all other salvage bonuses
     * <p>
     * This is typically set to the number of items needed to create that item, for example 5 for
     * helmets or 2 for swords
     *
     * @return the maximum number of items
     */
    int getMaximumQuantity();

    /**
     * Gets the maximum durability of this item before it breaks
     *
     * @return the maximum durability
     */
    short getMaximumDurability();

    /**
     * Gets the base salvage durability on which to calculate bonuses.
     * <p>
     * This is actually the maximum durability divided by the minimum quantity
     *
     * @return the base salvage durability
     */
    short getBaseSalvageDurability();

    /**
     * Gets the minimum salvage level needed to salvage this item
     *
     * @return the minimum level to salvage this item, or 0 for no minimum
     */
    int getMinimumLevel();

    /**
     * Gets the xpMultiplier for this salvageable
     *
     * @return the xpMultiplier of this salvageable
     */
    double getXpMultiplier();
}
