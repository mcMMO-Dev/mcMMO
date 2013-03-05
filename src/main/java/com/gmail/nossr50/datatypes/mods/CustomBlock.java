package com.gmail.nossr50.datatypes.mods;

import org.bukkit.inventory.ItemStack;

public class CustomBlock {
    private int itemID;
    private byte dataValue;
    private int xpGain;
    private int tier;
    private ItemStack itemDrop;
    private int minimumDropAmount;
    private int maximumDropAmount;

    public CustomBlock(int minimumDropAmount, int maximumDropAmount, ItemStack itemDrop, int tier, int xpGain, byte dataValue, int itemID) {
        this.itemID = itemID;
        this.dataValue = dataValue;
        this.xpGain = xpGain;
        this.tier = tier;
        this.itemDrop = itemDrop;
        this.minimumDropAmount = minimumDropAmount;
        this.maximumDropAmount = maximumDropAmount;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public byte getDataValue() {
        return dataValue;
    }

    public void setDataValue(byte dataValue) {
        this.dataValue = dataValue;
    }

    public int getXpGain() {
        return xpGain;
    }

    public void setXpGain(int xpGain) {
        this.xpGain = xpGain;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public ItemStack getItemDrop() {
        return itemDrop;
    }

    public void setItemDrop(ItemStack itemDrop) {
        this.itemDrop = itemDrop;
    }

    public int getMinimumDropAmount() {
        return minimumDropAmount;
    }

    public void setMinimumDropAmount(int minimumDropAmount) {
        this.minimumDropAmount = minimumDropAmount;
    }

    public int getMaximumDropAmount() {
        return maximumDropAmount;
    }

    public void setMaximumDropAmount(int maximumDropAmount) {
        this.maximumDropAmount = maximumDropAmount;
    }
}
