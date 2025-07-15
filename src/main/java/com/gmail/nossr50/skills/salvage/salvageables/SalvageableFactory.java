package com.gmail.nossr50.skills.salvage.salvageables;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import org.bukkit.Material;

public final class SalvageableFactory {
    /**
     * This is a static utility class, therefore we don't want any instances of this class. Making
     * the constructor private prevents accidents like that.
     */
    private SalvageableFactory() {
    }

    public static Salvageable getSalvageable(Material itemMaterial, Material recipeMaterial,
            int maximumQuantity, short maximumDurability) {
        return getSalvageable(itemMaterial, recipeMaterial, 0, maximumQuantity, maximumDurability,
                ItemType.OTHER, MaterialType.OTHER, 1);
    }

    public static Salvageable getSalvageable(Material itemMaterial, Material recipeMaterial,
            int minimumLevel, int maximumQuantity, short maximumDurability, ItemType repairItemType,
            MaterialType repairMaterialType, double xpMultiplier) {
        // TODO: Add in loading from config what type of repairable we want.
        return new SimpleSalvageable(itemMaterial, recipeMaterial, minimumLevel, maximumQuantity,
                maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
    }
}
