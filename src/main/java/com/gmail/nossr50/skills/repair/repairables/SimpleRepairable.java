package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class SimpleRepairable implements Repairable {
    private final Material itemMaterial, repairMaterial;
    private final int minimumLevel;
    private final short maximumDurability;
    private final String repairMaterialPrettyName;
    private final ItemType repairItemType;
    private final MaterialType repairMaterialType;
    private final double xpMultiplier;
    private int minQuantity = -1;

    protected SimpleRepairable(Material type, Material repairMaterial,
            String repairMaterialPrettyName, int minimumLevel, short maximumDurability,
            ItemType repairItemType, MaterialType repairMaterialType, double xpMultiplier) {
        this.itemMaterial = type;
        this.repairMaterial = repairMaterial;
        this.repairMaterialPrettyName = repairMaterialPrettyName;
        this.repairItemType = repairItemType;
        this.repairMaterialType = repairMaterialType;
        this.minimumLevel = minimumLevel;
        this.maximumDurability = maximumDurability;
        this.xpMultiplier = xpMultiplier;
    }

    protected SimpleRepairable(Material type, Material repairMaterial,
            String repairMaterialPrettyName, int minimumLevel, short maximumDurability,
            ItemType repairItemType, MaterialType repairMaterialType, double xpMultiplier,
            int minQuantity) {
        this.itemMaterial = type;
        this.repairMaterial = repairMaterial;
        this.repairMaterialPrettyName = repairMaterialPrettyName;
        this.repairItemType = repairItemType;
        this.repairMaterialType = repairMaterialType;
        this.minimumLevel = minimumLevel;
        this.maximumDurability = maximumDurability;
        this.xpMultiplier = xpMultiplier;
        this.minQuantity = minQuantity;
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
    public String getRepairMaterialPrettyName() {
        return repairMaterialPrettyName;
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
        if (minQuantity == -1) {
            return Math.max(SkillUtils.getRepairAndSalvageQuantities(itemMaterial, repairMaterial),
                    1);
        } else {
            return minQuantity;
        }
    }

    @Override
    public short getMaximumDurability() {
        return maximumDurability;
    }

    @Override
    public short getBaseRepairDurability(ItemStack itemStack) {
        return (short) (maximumDurability / getMinimumQuantity());
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
