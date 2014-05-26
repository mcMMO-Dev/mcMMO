package com.gmail.nossr50.skills.salvage.salvageables;

import org.bukkit.Material;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;

public class SalvageableFactory {
    public static Salvageable getSalvageable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int maximumQuantity, short maximumDurability) {
        return getSalvageable(itemMaterial, repairMaterial, repairMetadata, 0, maximumQuantity, maximumDurability, ItemType.OTHER, MaterialType.OTHER, 1);
    }

    public static Salvageable getSalvageable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumLevel, int maximumQuantity, short maximumDurability, ItemType repairItemType, MaterialType repairMaterialType, double xpMultiplier) {
        // TODO: Add in loading from config what type of repairable we want.
        return new SimpleSalvageable(itemMaterial, repairMaterial, repairMetadata, minimumLevel, maximumQuantity, maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
    }
}
