package com.gmail.nossr50.skills.salvage.salvageables;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import com.gmail.nossr50.datatypes.skills.ItemType;
import org.bukkit.Material;

public interface Salvageable {
    /**
     * Gets the type of this salvageable item
     *
     * @return the type of this salvageable
     */
    public Material getItemMaterial();

    /**
     * Gets the material of the items dropped when salvaging this item
     *
     * @return the material of the salvage drop
     */
    public Material getSalvageMaterial();

    /**
     * Gets the metadata byte value of the items dropped when salvaging this item
     *
     * @return the byte metadata of the salvage drop
     */
    public byte getSalvageMaterialMetadata();

    /**
     * Gets the ItemType value for this salvageable item
     *
     * @return the ItemType for this salvageable
     */
    public ItemType getSalvageItemType();

    /**
     * Gets the ItemMaterialCategory value for this salvageable item
     *
     * @return the ItemMaterialCategory for this salvageable
     */
    public ItemMaterialCategory getSalvageItemMaterialCategory();

    /**
     * Gets the maximum quantity of salvage materials ignoring all other salvage bonuses
     *
     * This is typically set to the number of items needed to create that item, for example 5 for helmets or 2 for swords
     *
     * @return the maximum number of items
     */
    public int getMaximumQuantity();

    /**
     * Gets the maximum durability of this item before it breaks
     *
     * @return the maximum durability
     */
    public short getMaximumDurability();

    /**
     * Gets the base salvage durability on which to calculate bonuses.
     *
     * This is actually the maximum durability divided by the minimum quantity
     *
     * @return the base salvage durability
     */
    public short getBaseSalvageDurability();

    /**
     * Gets the minimum salvage level needed to salvage this item
     *
     * @return the minimum level to salvage this item, or 0 for no minimum
     */
    public int getMinimumLevel();

    /**
     * Gets the xpMultiplier for this salvageable
     *
     * @return the xpMultiplier of this salvageable
     */
    public double getXpMultiplier();
}
