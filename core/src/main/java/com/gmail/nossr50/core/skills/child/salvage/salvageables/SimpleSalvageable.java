package com.gmail.nossr50.core.skills.child.salvage.salvageables;

import com.gmail.nossr50.core.skills.ConfigItemCategory;
import com.gmail.nossr50.core.skills.MaterialType;
import org.bukkit.Material;


public class SimpleSalvageable implements Salvageable {
    private final Material itemMaterial, salvageMaterial;
    private final int maximumQuantity, minimumLevel;
    private final short maximumDurability, baseSalvageDurability;
    private final byte salvageMetadata;
    private final ConfigItemCategory salvageConfigItemCategory;
    private final MaterialType salvageMaterialType;
    private final double xpMultiplier;

    protected SimpleSalvageable(Material type, Material salvageMaterial, byte salvageMetadata, int minimumLevel, int maximumQuantity, short maximumDurability, ConfigItemCategory salvageConfigItemCategory, MaterialType salvageMaterialType, double xpMultiplier) {
        this.itemMaterial = type;
        this.salvageMaterial = salvageMaterial;
        this.salvageMetadata = salvageMetadata;
        this.salvageConfigItemCategory = salvageConfigItemCategory;
        this.salvageMaterialType = salvageMaterialType;
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
    public ConfigItemCategory getSalvageConfigItemCategory() {
        return salvageConfigItemCategory;
    }

    @Override
    public MaterialType getSalvageMaterialType() {
        return salvageMaterialType;
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
