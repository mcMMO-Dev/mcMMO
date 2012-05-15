package com.gmail.nossr50.datatypes.mods;

import org.bukkit.inventory.ItemStack;

public class CustomItem {
    protected int itemID;
    protected boolean repairable;
    protected ItemStack repairMaterial;
    protected int repairQuantity;
    protected short durability;

    public CustomItem(short durability, ItemStack repairMaterial, int repairQuantity, boolean repairable, int itemID) {
        this.itemID = itemID;
        this.repairable = repairable;
        this.repairMaterial = repairMaterial;
        this.repairQuantity = repairQuantity;
        this.durability = durability;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
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

    public int getRepairQuantity() {
        return repairQuantity;
    }

    public void setRepairQuantity(int repairQuantity) {
        this.repairQuantity = repairQuantity;
    }

    public short getDurability() {
        return durability;
    }

    public void setDurability(short durability) {
        this.durability = durability;
    }
}
