package com.gmail.nossr50.skills.repair;

import org.bukkit.Material;

public class RepairableFactory {
    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumQuantity, short maximumDurability) {
        return getRepairable(itemMaterial, repairMaterial, repairMetadata, 0, minimumQuantity, maximumDurability, RepairItemType.OTHER, RepairMaterialType.OTHER, 1);
    }

    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumLevel, int minimumQuantity, short maximumDurability, RepairItemType repairItemType, RepairMaterialType repairMaterialType, double xpMultiplier) {
        // TODO: Add in loading from config what type of repairable we want.
        return new SimpleRepairable(itemMaterial, repairMaterial, repairMetadata, minimumLevel, minimumQuantity, maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
    }
}
