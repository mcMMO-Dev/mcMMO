package com.gmail.nossr50.datatypes.mods;

import org.bukkit.inventory.ItemStack;

public class CustomBlock {
    private int xpGain;
    private ItemStack itemDrop;
    private int minimumDropAmount;
    private int maximumDropAmount;

    public CustomBlock(int minimumDropAmount, int maximumDropAmount, ItemStack itemDrop, int xpGain) {
        this.xpGain = xpGain;
        this.itemDrop = itemDrop;
        this.minimumDropAmount = minimumDropAmount;
        this.maximumDropAmount = maximumDropAmount;
    }

    public int getXpGain() {
        return xpGain;
    }

    public ItemStack getItemDrop() {
        return itemDrop;
    }

    public int getMinimumDropAmount() {
        return minimumDropAmount;
    }

    public int getMaximumDropAmount() {
        return maximumDropAmount;
    }
}
