package com.gmail.nossr50.skills.salvage.salvageables;

import org.bukkit.Material;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;

public interface Salvageable {
    /**
     * Gets the type of this repairable item
     *
     * @return the type of this repairable
     */
    public Material getItemMaterial();

    /**
     * Gets the id of the material used to repair this item
     *
     * @return the id of the repair material
     */
    public Material getSalvageMaterial();

    /**
     * Gets the metadata byte value of the material used to repair this item
     *
     * @return the byte metadata of the repair material
     */
    public byte getSalvageMaterialMetadata();

    /**
     * Gets the RepairItemType value for this repairable item
     *
     * @return the RepairItemType for this repairable
     */
    public ItemType getSalvageItemType();

    /**
     * Gets the RepairMaterialType value for this repairable item
     *
     * @return the RepairMaterialType for this repairable
     */
    public MaterialType getSalvageMaterialType();

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
     * Gets the base repair durability on which to calculate bonuses.
     *
     * This is actually the maximum durability divided by the minimum quantity
     *
     * @return the base repair durability
     */
    public short getBaseSalvageDurability();

    /**
     * Gets the minimum repair level needed to repair this item
     *
     * @return the minimum level to repair this item, or 0 for no minimum
     */
    public int getMinimumLevel();

    /**
     * Gets the xpMultiplier for this repairable
     *
     * @return the xpMultiplier of this repairable
     */
    public double getXpMultiplier();
}
