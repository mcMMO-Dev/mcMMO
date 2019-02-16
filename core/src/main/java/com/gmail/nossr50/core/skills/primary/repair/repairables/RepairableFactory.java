package com.gmail.nossr50.core.skills.primary.repair.repairables;

import com.gmail.nossr50.core.skills.ConfigItemCategory;
import com.gmail.nossr50.core.skills.MaterialType;
import org.bukkit.Material;


public class RepairableFactory {
    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumQuantity, short maximumDurability) {
        return getRepairable(itemMaterial, repairMaterial, repairMetadata, null, 0, minimumQuantity, maximumDurability, ConfigItemCategory.OTHER, MaterialType.OTHER, 1);
    }

    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumLevel, int minimumQuantity, short maximumDurability, ConfigItemCategory repairConfigItemCategory, MaterialType repairMaterialType, double xpMultiplier) {
        return getRepairable(itemMaterial, repairMaterial, repairMetadata, null, minimumLevel, minimumQuantity, maximumDurability, repairConfigItemCategory, repairMaterialType, xpMultiplier);
    }

    public static Repairable getRepairable(Material itemMaterial, Material repairMaterial, byte repairMetadata, String repairMaterialPrettyName, int minimumLevel, int minimumQuantity, short maximumDurability, ConfigItemCategory repairConfigItemCategory, MaterialType repairMaterialType, double xpMultiplier) {
        // TODO: Add in loading from config what type of repairable we want.
        return new SimpleRepairable(itemMaterial, repairMaterial, repairMetadata, repairMaterialPrettyName, minimumLevel, minimumQuantity, maximumDurability, repairConfigItemCategory, repairMaterialType, xpMultiplier);
    }
}
