package com.gmail.nossr50.skills.repair;

public class RepairableFactory {
    public static Repairable getRepairable(int itemId, int repairMaterialId, byte repairMetadata, int minimumQuantity, short maximumDurability) {
        return getRepairable(itemId, repairMaterialId, repairMetadata, 0, minimumQuantity, maximumDurability, RepairItemType.OTHER, RepairMaterialType.OTHER, 1);
    }

    public static Repairable getRepairable(int itemId, int repairMaterialId, byte repairMetadata, int minimumLevel, int minimumQuantity, short maximumDurability, RepairItemType repairItemType, RepairMaterialType repairMaterialType, double xpMultiplier) {
        // TODO: Add in loading from config what type of repairable we want.
        return new SimpleRepairable(itemId, repairMaterialId, repairMetadata, minimumLevel, minimumQuantity, maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
    }
}
