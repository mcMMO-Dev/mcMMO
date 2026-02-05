package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import org.bukkit.Material;


public class RepairableFactory {
    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial,
            short maximumDurability) {
        return getRepairable(itemMaterial, repairMaterial, null, 0, maximumDurability,
                ItemType.OTHER, MaterialType.OTHER, 1);
    }

    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial,
            int minimumLevel, short maximumDurability, ItemType repairItemType,
            MaterialType repairMaterialType, double xpMultiplier) {
        return getRepairable(itemMaterial, repairMaterial, null, minimumLevel, maximumDurability,
                repairItemType, repairMaterialType, xpMultiplier);
    }

    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial,
            String repairMaterialPrettyName,
            int minimumLevel, short maximumDurability, ItemType repairItemType,
            MaterialType repairMaterialType, double xpMultiplier) {
        // TODO: Add in loading from config what type of repairable we want.
        return new SimpleRepairable(itemMaterial, repairMaterial, repairMaterialPrettyName,
                minimumLevel, maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
    }

    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial,
            String repairMaterialPrettyName,
            int minimumLevel, short maximumDurability, ItemType repairItemType,
            MaterialType repairMaterialType, double xpMultiplier, int minQuantity) {
        // TODO: Add in loading from config what type of repairable we want.
        return new SimpleRepairable(itemMaterial, repairMaterial, repairMaterialPrettyName,
                minimumLevel, maximumDurability, repairItemType, repairMaterialType, xpMultiplier,
                minQuantity);
    }
}
