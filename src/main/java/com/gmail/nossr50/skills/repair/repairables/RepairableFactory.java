package com.gmail.nossr50.skills.repair.repairables;

import org.bukkit.Material;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;


public class RepairableFactory {
    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumQuantity, short maximumDurability) {
        return getRepairable(itemMaterial, repairMaterial, repairMetadata, null, 0, minimumQuantity, maximumDurability, ItemType.OTHER, MaterialType.OTHER, 1);
    }

    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumLevel, int minimumQuantity, short maximumDurability, ItemType repairItemType, MaterialType repairMaterialType, double xpMultiplier) {
        return getRepairable(itemMaterial, repairMaterial, repairMetadata, null, minimumLevel, minimumQuantity, maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
    }

    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial, byte repairMetadata, String repairMaterialPrettyName, int minimumLevel, int minimumQuantity, short maximumDurability, ItemType repairItemType, MaterialType repairMaterialType, double xpMultiplier) {
        // TODO: Add in loading from config what type of repairable we want.
        return new SimpleRepairable(itemMaterial, repairMaterial, repairMetadata, repairMaterialPrettyName, minimumLevel, minimumQuantity, maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
    }
}
