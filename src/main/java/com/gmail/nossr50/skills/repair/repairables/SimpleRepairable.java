package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class SimpleRepairable implements Repairable {
    private static final int NOT_CONFIGURED = -1;

    private final Material itemMaterial, repairMaterial;
    private final int minimumLevel;
    private final short maximumDurability;
    private final String repairMaterialPrettyName;
    private final ItemType repairItemType;
    private final MaterialType repairMaterialType;
    private final double xpMultiplier;
    private final int minQuantity;
    /** Zero until the recipe count has been worked out; a worked out count is at least one. */
    private int recipeMinimumQuantity;

    protected SimpleRepairable(Material type, Material repairMaterial,
            String repairMaterialPrettyName, int minimumLevel, short maximumDurability,
            ItemType repairItemType, MaterialType repairMaterialType, double xpMultiplier) {
        this(type, repairMaterial, repairMaterialPrettyName, minimumLevel, maximumDurability,
                repairItemType, repairMaterialType, xpMultiplier, NOT_CONFIGURED);
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
        // Zero or less is not a usable quantity (it is also the divisor of the base repair
        // amount), so it counts as not configured
        if (minQuantity > 0) {
            return minQuantity;
        }

        // Counting ingredients converts every recipe on the server, far too much work for each
        // repair, so the count from the first repair is kept. Recipes changed after that are
        // not picked up, the same as Salvage, which counts once at startup.
        // No lock needed: an int write is atomic and every count is at least one, so a racing
        // thread reads either zero and counts for itself, or a usable count.
        int quantity = recipeMinimumQuantity;

        if (quantity == 0) {
            quantity = Math.max(
                    SkillUtils.getRepairAndSalvageQuantities(itemMaterial, repairMaterial), 1);
            recipeMinimumQuantity = quantity;
        }

        return quantity;
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
