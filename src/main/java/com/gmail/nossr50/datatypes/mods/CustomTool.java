package com.gmail.nossr50.datatypes.mods;

import org.bukkit.inventory.ItemStack;

public class CustomTool {
    private int itemID;
    private double xpMultiplier;
    private boolean repairable;
    private ItemStack repairMaterial;
    private short durability;

    public CustomTool(short durability, ItemStack repairMaterial, boolean repairable, double xpMultiplier, int itemID) {
        this.itemID = itemID;
        this.xpMultiplier = xpMultiplier;
        this.repairable = repairable;
        this.repairMaterial = repairMaterial;
        this.durability = durability;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    public void setXpMultiplier(Double xpMultiplier) {
        this.xpMultiplier = xpMultiplier;
    }

    public boolean isRepairable() {
        return repairable;
    }

    public void setRepairable(boolean repairable) {
        this.repairable = repairable;
    }

    public ItemStack getRepairMaterial() {
        return repairMaterial;
    }

    public void setRepairMaterial(ItemStack repairMaterial) {
        this.repairMaterial = repairMaterial;
    }

    public short getDurability() {
        return durability;
    }

    public void setDurability(short durability) {
        this.durability = durability;
    }
}
