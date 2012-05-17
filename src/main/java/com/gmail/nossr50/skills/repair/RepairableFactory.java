package com.gmail.nossr50.skills.repair;

public class RepairableFactory {
    public static Repairable getRepairable(int itemId, int repairMaterialId, int minimumLevel, int minimumQuantity, short maximumDurability) {
        // TODO: Add in loading from config what type of manager we want.
        return new SimpleRepairable(itemId, repairMaterialId, minimumLevel, minimumQuantity, maximumDurability);
    }
}
