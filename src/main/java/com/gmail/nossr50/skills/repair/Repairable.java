package com.gmail.nossr50.skills.repair;

public interface Repairable {
    /**
     * Gets the item id of this repairable item
     *
     * @return the id of this repairable
     */
    public int getItemId();

    /**
     * Gets the id of the material used to repair this item
     *
     * @return the id of the repair material
     */
    public int getRepairMaterialId();

    /**
     * Gets the metadata byte value of the material used to repair this item
     *
     * @return the byte metadata of the repair material
     */
    public byte getRepairMaterialMetadata();

    /**
     * Gets the RepairItemType value for this repairable item
     *
     * @return the RepairItemType for this repairable
     */
    public RepairItemType getRepairItemType();

    /**
     * Gets the RepairMaterialType value for this repairable item
     *
     * @return the RepairMaterialType for this repairable
     */
    public RepairMaterialType getRepairMaterialType();

    /**
     * Gets the minimum quantity of repair materials ignoring all other repair bonuses
     *
     * This is typically set to the number of items needed to create that item, for example 5 for helmets or 2 for swords
     *
     * @return the minimum number of items
     */
    public int getMinimumQuantity();

    /**
     * Gets the maximum durability of this item before it breaks
     *
     * @return the maximum durability
     */
    public short getMaximumDurability();

    /**
     * Gets the base repair durability on which to calcuate bonuses.
     *
     * This is actually the maximum durability divided by the minimum quantity
     *
     * @return the base repair durability
     */
    public short getBaseRepairDurability();

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
