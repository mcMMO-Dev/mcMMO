package com.gmail.nossr50.skills.salvage.salvageables;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import com.gmail.nossr50.datatypes.skills.ItemType;
import org.bukkit.Material;


public class Salvageable {
    private final Material itemMaterial, salvagedItemMaterial;
    private final int maximumQuantity, minimumLevel;
    private final short maximumDurability, baseSalvageDurability;
    private final byte salvageMetadata;
    private final ItemType salvageItemType;
    private final ItemMaterialCategory salvageItemMaterialCategory;
    private final double xpMultiplier;

    /*protected Salvageable(Material type, Material salvagedItemMaterial, byte salvageMetadata, int minimumLevel, int maximumQuantity, short maximumDurability, ItemType salvageItemType, ItemMaterialCategory salvageItemMaterialCategory, double xpMultiplier) {
        this.itemMaterial = type;
        this.salvagedItemMaterial = salvagedItemMaterial;
        this.salvageMetadata = salvageMetadata;
        this.salvageItemType = salvageItemType;
        this.salvageItemMaterialCategory = salvageItemMaterialCategory;
        this.minimumLevel = minimumLevel;
        this.maximumQuantity = maximumQuantity;
        this.maximumDurability = maximumDurability;
        this.baseSalvageDurability = (short) (maximumDurability / maximumQuantity);
        this.xpMultiplier = xpMultiplier;
    }*/

    public Salvageable(String itemRegisterKey, String salvagedMaterialRegisterKey, int minimumLevel, int maximumQuantity)
    {
        this(Material.matchMaterial(itemRegisterKey), Material.matchMaterial(salvagedMaterialRegisterKey), minimumLevel, maximumQuantity);
    }

    public Salvageable(Material itemMaterial, Material salvagedItemMaterial, int minimumLevel, int maximumQuantity)
    {

        this.itemMaterial = itemMaterial;
        this.salvagedItemMaterial = salvagedItemMaterial;
//        this.salvageMetadata = salvageMetadata;
        this.salvageItemType = salvageItemType;
        this.salvageItemMaterialCategory = salvageItemMaterialCategory;
        this.minimumLevel = minimumLevel;
        this.maximumQuantity = maximumQuantity;
        this.maximumDurability = maximumDurability;
        this.baseSalvageDurability = (short) (maximumDurability / maximumQuantity);
        this.xpMultiplier = xpMultiplier;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public Material getSalvagedItemMaterial() {
        return salvagedItemMaterial;
    }

    /*public byte getSalvageMaterialMetadata() {
        return salvageMetadata;
    }*/

    public ItemType getSalvageItemType() {
        return salvageItemType;
    }

    public ItemMaterialCategory getSalvageItemMaterialCategory() {
        return salvageItemMaterialCategory;
    }

    public int getMaximumQuantity() {
        return maximumQuantity;
    }

    public short getMaximumDurability() {
        return maximumDurability;
    }

    public short getBaseSalvageDurability() {
        return baseSalvageDurability;
    }

    public int getMinimumLevel() {
        return minimumLevel;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }
}
