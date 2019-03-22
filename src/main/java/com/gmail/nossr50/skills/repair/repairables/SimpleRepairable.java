package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class SimpleRepairable implements Repairable {
    private final Material itemMaterial, repairMaterial;
    private final int minimumQuantity, minimumLevel;
    private final short maximumDurability, baseRepairDurability;
    /*private String repairMaterialPrettyName;*/
    private final ItemType repairItemType;
    private final MaterialType repairMaterialType;
    private final double xpMultiplier;

/*    protected SimpleRepairable(Material type, Material repairMaterial, String repairMaterialPrettyName, int minimumLevel, int minimumQuantity, short maximumDurability, ItemType repairItemType, MaterialType repairMaterialType, double xpMultiplier) {
        this.itemMaterial = type;
        this.repairMaterial = repairMaterial;
        this.repairMaterialPrettyName = repairMaterialPrettyName;
        this.repairItemType = repairItemType;
        this.repairMaterialType = repairMaterialType;
        this.minimumLevel = minimumLevel;
        this.minimumQuantity = minimumQuantity;
        this.maximumDurability = maximumDurability;
        this.baseRepairDurability = (short) (maximumDurability / minimumQuantity);
        this.xpMultiplier = xpMultiplier;
    }*/

    public SimpleRepairable(Material itemMaterial, Material repairMaterial, int minimumQuantity, int minimumLevel, double xpMultiplier) {
        this.itemMaterial = itemMaterial;
        this.repairMaterial = repairMaterial;
        this.minimumQuantity = minimumQuantity;
        this.minimumLevel = minimumLevel;
        this.xpMultiplier = xpMultiplier;

        this.maximumDurability = itemMaterial.getMaxDurability();
        this.baseRepairDurability = (short) (maximumDurability / minimumQuantity);

        this.repairItemType = determineItemType(itemMaterial);
        this.repairMaterialType = determineMaterialType(repairMaterial);
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

    @Override
    public Material getItemMaterial() {
        return itemMaterial;
    }

    @Override
    public Material getRepairMaterial() {
        return repairMaterial;
    }

    @Override
    public ItemType getRepairItemType() {
        return repairItemType;
    }

    @Override
    public MaterialType getRepairMaterialType() {
        return repairMaterialType;
    }

    @Override
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    @Override
    public short getMaximumDurability() {
        return maximumDurability;
    }

    @Override
    public short getBaseRepairDurability() {
        return baseRepairDurability;
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
