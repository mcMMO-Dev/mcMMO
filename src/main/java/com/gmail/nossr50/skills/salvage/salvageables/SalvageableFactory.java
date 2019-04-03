package com.gmail.nossr50.skills.salvage.salvageables;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import com.gmail.nossr50.datatypes.skills.ItemType;
import org.bukkit.Material;

public class SalvageableFactory {
    public static Salvageable getSalvageable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int maximumQuantity, short maximumDurability) {
        return getSalvageable(itemMaterial, repairMaterial, repairMetadata, 0, maximumQuantity, maximumDurability, ItemType.OTHER, ItemMaterialCategory.OTHER, 1);
    }

    public static Salvageable getSalvageable(Material itemMaterial, Material repairMaterial, byte repairMetadata, int minimumLevel, int maximumQuantity, short maximumDurability, ItemType repairItemType, ItemMaterialCategory repairItemMaterialCategory, double xpMultiplier) {
        // TODO: Add in loading from config what type of repairable we want.
        return new Salvageable(itemMaterial, repairMaterial, repairMetadata, minimumLevel, maximumQuantity, maximumDurability, repairItemType, repairItemMaterialCategory, xpMultiplier);
    }
}
