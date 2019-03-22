package com.gmail.nossr50.skills.salvage.salvageables;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import com.gmail.nossr50.datatypes.skills.ItemType;
import org.bukkit.Material;


public class SimpleSalvageable implements Salvageable {
    private final Material itemMaterial, salvageMaterial;
    private final int maximumQuantity, minimumLevel;
    private final short maximumDurability, baseSalvageDurability;
    private final byte salvageMetadata;
    private final ItemType salvageItemType;
    private final ItemMaterialCategory salvageItemMaterialCategory;
    private final double xpMultiplier;

    protected SimpleSalvageable(Material type, Material salvageMaterial, byte salvageMetadata, int minimumLevel, int maximumQuantity, short maximumDurability, ItemType salvageItemType, ItemMaterialCategory salvageItemMaterialCategory, double xpMultiplier) {
        this.itemMaterial = type;
        this.salvageMaterial = salvageMaterial;
        this.salvageMetadata = salvageMetadata;
        this.salvageItemType = salvageItemType;
        this.salvageItemMaterialCategory = salvageItemMaterialCategory;
        this.minimumLevel = minimumLevel;
        this.maximumQuantity = maximumQuantity;
        this.maximumDurability = maximumDurability;
        this.baseSalvageDurability = (short) (maximumDurability / maximumQuantity);
        this.xpMultiplier = xpMultiplier;
    }

    @Override
    public Material getItemMaterial() {
        return itemMaterial;
    }

    @Override
    public Material getSalvageMaterial() {
        return salvageMaterial;
    }

    @Override
    public byte getSalvageMaterialMetadata() {
        return salvageMetadata;
    }

    @Override
    public ItemType getSalvageItemType() {
        return salvageItemType;
    }

    @Override
    public ItemMaterialCategory getSalvageItemMaterialCategory() {
        return salvageItemMaterialCategory;
    }

    @Override
    public int getMaximumQuantity() {
        return maximumQuantity;
    }

    @Override
    public short getMaximumDurability() {
        return maximumDurability;
    }

    @Override
    public short getBaseSalvageDurability() {
        return baseSalvageDurability;
    }

    @Override
    public int getMinimumLevel() {
        return minimumLevel;
    }

    @Override
    public double getXpMultiplier() {
        return xpMultiplier;
    }
}
