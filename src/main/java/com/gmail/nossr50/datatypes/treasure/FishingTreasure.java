package com.gmail.nossr50.datatypes.treasure;

import org.bukkit.inventory.ItemStack;

public class FishingTreasure extends Treasure {
    private int maxLevel;

    public FishingTreasure(ItemStack drop, int xp, Double dropChance, int dropLevel, int maxLevel) {
        super(drop, xp, dropChance, dropLevel);
        this.setMaxLevel(maxLevel);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }
}
