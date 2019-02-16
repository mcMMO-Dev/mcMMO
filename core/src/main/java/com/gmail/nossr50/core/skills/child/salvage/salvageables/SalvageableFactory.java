package com.gmail.nossr50.core.skills.child.salvage.salvageables;

import com.gmail.nossr50.core.skills.ConfigItemCategory;
import com.gmail.nossr50.core.skills.MaterialType;
import org.bukkit.Material;

public class SalvageableFactory {
    public static Salvageable getSalvageable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int maximumQuantity, short maximumDurability) {
        return getSalvageable(itemMaterial, repairMaterial, repairMetadata, 0, maximumQuantity, maximumDurability, ConfigItemCategory.OTHER, MaterialType.OTHER, 1);
    }

    public static Salvageable getSalvageable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumLevel, int maximumQuantity, short maximumDurability, ConfigItemCategory repairConfigItemCategory, MaterialType repairMaterialType, double xpMultiplier) {
        // TODO: Add in loading from config what type of repairable we want.
        return new SimpleSalvageable(itemMaterial, repairMaterial, repairMetadata, minimumLevel, maximumQuantity, maximumDurability, repairConfigItemCategory, repairMaterialType, xpMultiplier);
    }
}
