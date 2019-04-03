package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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

        this.repairItemType = determineItemType(this.itemMaterial);
        this.repairItemMaterialCategory = determineMaterialType(this.repairMaterials.get(0));
    }

    public ItemMaterialCategory determineMaterialType(Material material) {
        switch (material) {
            case STRING:
                return ItemMaterialCategory.STRING;

            case LEATHER:
                return ItemMaterialCategory.LEATHER;

            case ACACIA_PLANKS:
            case BIRCH_PLANKS:
            case DARK_OAK_PLANKS:
            case JUNGLE_PLANKS:
            case OAK_PLANKS:
            case SPRUCE_PLANKS:
                return ItemMaterialCategory.WOOD;

            case STONE:
                return ItemMaterialCategory.STONE;

            case IRON_INGOT:
                return ItemMaterialCategory.IRON;

            case GOLD_INGOT:
                return ItemMaterialCategory.GOLD;

            case DIAMOND:
                return ItemMaterialCategory.DIAMOND;

            default:
                return ItemMaterialCategory.OTHER;
        }
    }

    private ItemType determineItemType(Material material)
    {
        if (ItemUtils.isMinecraftTool(new ItemStack(material))) {
            return ItemType.TOOL;
        }
        else if (ItemUtils.isArmor(new ItemStack((material)))) {
            return ItemType.ARMOR;
        } else {
            return ItemType.OTHER;
        }
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
