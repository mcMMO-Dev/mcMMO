package com.gmail.nossr50.skills.salvage.salvageables;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a 'Salvageable' item
 * Includes all the data needed for determining rewards from Salvage
 */
public class Salvageable {
    private final Material itemMaterial, salvagedItemMaterial;
    private final int maximumQuantity, minimumLevel;
    private final short maximumDurability, baseSalvageDurability;
    private final ItemType salvageItemType;
    private final ItemMaterialCategory salvageItemMaterialCategory;
    private final double xpMultiplier;

    public Salvageable(String itemRegisterKey, String salvagedMaterialRegisterKey, int minimumLevel, int maximumQuantity)
    {
        this(Material.matchMaterial(itemRegisterKey), Material.matchMaterial(salvagedMaterialRegisterKey), minimumLevel, maximumQuantity);
    }

    public Salvageable(Material itemMaterial, Material salvagedItemMaterial, int minimumLevel, int maximumQuantity)
    {
        this.itemMaterial = itemMaterial;
        this.salvagedItemMaterial = salvagedItemMaterial;
        this.salvageItemType = ItemUtils.determineItemType(itemMaterial);
        this.salvageItemMaterialCategory = ItemUtils.determineMaterialType(salvagedItemMaterial);
        this.minimumLevel = minimumLevel;
        this.maximumQuantity = maximumQuantity;
        this.maximumDurability = itemMaterial.getMaxDurability();
        this.baseSalvageDurability = (short) (maximumDurability / maximumQuantity);
        this.xpMultiplier = 1.0D;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public Material getSalvagedItemMaterial() {
        return salvagedItemMaterial;
    }

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
