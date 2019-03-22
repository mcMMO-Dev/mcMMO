package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;


public class SimpleRepairable {
    private final Material itemMaterial;
    private final List<Material> repairMaterials;
    private final int minimumQuantity, minimumLevel;
    private final short maximumDurability, baseRepairDurability;
    private final ItemType repairItemType;
    private final MaterialType repairMaterialType;
    private final double xpMultiplier;

    public SimpleRepairable(Material itemMaterial, Material repairMaterial, int minimumQuantity, int minimumLevel, double xpMultiplier) {
        this(itemMaterial.getKey().getKey(), ItemUtils.getRepairItemMaterials(Arrays.asList(repairMaterial)), minimumQuantity, minimumLevel, xpMultiplier);
    }

    public SimpleRepairable(Material itemMaterial, List<Material> repairMaterials, int minimumQuantity, int minimumLevel, double xpMultiplier) {
        this(itemMaterial.getKey().getKey(), ItemUtils.getRepairItemMaterials(repairMaterials), minimumQuantity, minimumLevel, xpMultiplier);
    }

    public SimpleRepairable(String itemMaterial, List<String> repairMaterials, int minimumQuantity, int minimumLevel, double xpMultiplier) {
        this.itemMaterial = Material.matchMaterial(itemMaterial);
        this.repairMaterials = ItemUtils.matchMaterials(repairMaterials);
        this.minimumQuantity = minimumQuantity;
        this.minimumLevel = minimumLevel;
        this.xpMultiplier = xpMultiplier;

        this.maximumDurability = this.itemMaterial.getMaxDurability();
        this.baseRepairDurability = (short) (maximumDurability / minimumQuantity);

        this.repairItemType = determineItemType(this.itemMaterial);
        this.repairMaterialType = determineMaterialType(this.repairMaterials.get(0));
    }

    public MaterialType determineMaterialType(Material material) {
        switch (material) {
            case STRING:
                return MaterialType.STRING;

            case LEATHER:
                return MaterialType.LEATHER;

            case ACACIA_PLANKS:
            case BIRCH_PLANKS:
            case DARK_OAK_PLANKS:
            case JUNGLE_PLANKS:
            case OAK_PLANKS:
            case SPRUCE_PLANKS:
                return MaterialType.WOOD;

            case STONE:
                return MaterialType.STONE;

            case IRON_INGOT:
                return MaterialType.IRON;

            case GOLD_INGOT:
                return MaterialType.GOLD;

            case DIAMOND:
                return MaterialType.DIAMOND;

            default:
                return MaterialType.OTHER;
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

    public MaterialType getRepairMaterialType() {
        return repairMaterialType;
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
