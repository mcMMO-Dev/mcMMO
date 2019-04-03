package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.util.ItemUtils;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;


public class Repairable {
    private final Material itemMaterial;
    private final List<Material> repairMaterials;
    private final int minimumQuantity, minimumLevel;
    private final short maximumDurability, baseRepairDurability;
    private final ItemType repairItemType;
    private final ItemMaterialCategory repairItemMaterialCategory;
    private final double xpMultiplier;

    public Repairable(Material itemMaterial, Material repairMaterial, int minimumQuantity, int minimumLevel, double xpMultiplier) {
        this(itemMaterial.getKey().getKey(), ItemUtils.getRepairItemMaterials(Arrays.asList(repairMaterial)), minimumQuantity, minimumLevel, xpMultiplier);
    }

    public Repairable(Material itemMaterial, List<Material> repairMaterials, int minimumQuantity, int minimumLevel, double xpMultiplier) {
        this(itemMaterial.getKey().getKey(), ItemUtils.getRepairItemMaterials(repairMaterials), minimumQuantity, minimumLevel, xpMultiplier);
    }

    public Repairable(String itemMaterial, List<String> repairMaterials, int minimumQuantity, int minimumLevel, double xpMultiplier) {
        this.itemMaterial = Material.matchMaterial(itemMaterial);
        this.repairMaterials = ItemUtils.matchMaterials(repairMaterials);
        this.minimumQuantity = minimumQuantity;
        this.minimumLevel = minimumLevel;
        this.xpMultiplier = xpMultiplier;

        this.maximumDurability = this.itemMaterial.getMaxDurability();
        this.baseRepairDurability = (short) (maximumDurability / minimumQuantity);

        this.repairItemType = ItemUtils.determineItemType(this.itemMaterial);
        this.repairItemMaterialCategory = ItemUtils.determineMaterialType(this.repairMaterials.get(0));
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public List<Material> getRepairMaterials() {
        return repairMaterials;
    }

    public List<String> getRepairMaterialsRegistryKeys() {
        return ItemUtils.getRepairItemMaterials(repairMaterials);
    }


    public ItemType getRepairItemType() {
        return repairItemType;
    }

    public ItemMaterialCategory getRepairItemMaterialCategory() {
        return repairItemMaterialCategory;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public short getMaximumDurability() {
        return maximumDurability;
    }

    public short getBaseRepairDurability() {
        return baseRepairDurability;
    }

    public int getMinimumLevel() {
        return minimumLevel;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }
}
