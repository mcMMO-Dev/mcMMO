package com.gmail.nossr50.datatypes.mods;

import org.bukkit.inventory.ItemStack;

public class CustomEntity {
    private double xpMultiplier;
    private boolean canBeTamed;
    private int tamingXP;
    private boolean canBeSummoned;
    private ItemStack callOfTheWildItem;
    private int callOfTheWildAmount;

    public CustomEntity(double xpMultiplier, boolean canBeTamed, int tamingXP, boolean canBeSummoned, ItemStack callOfTheWildItem, int callOfTheWildAmount) {
        this.xpMultiplier = xpMultiplier;
        this.canBeTamed = canBeTamed;
        this.tamingXP = tamingXP;
        this.canBeSummoned = canBeSummoned;
        this.callOfTheWildItem = callOfTheWildItem;
        this.callOfTheWildAmount = callOfTheWildAmount;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    public void setXpMultiplier(double xpMultiplier) {
        this.xpMultiplier = xpMultiplier;
    }

    public boolean isCanBeTamed() {
        return canBeTamed;
    }

    public void setCanBeTamed(boolean canBeTamed) {
        this.canBeTamed = canBeTamed;
    }

    public int getTamingXP() {
        return tamingXP;
    }

    public void setTamingXP(int tamingXP) {
        this.tamingXP = tamingXP;
    }

    public boolean isCanBeSummoned() {
        return canBeSummoned;
    }

    public void setCanBeSummoned(boolean canBeSummoned) {
        this.canBeSummoned = canBeSummoned;
    }

    public ItemStack getCallOfTheWildItem() {
        return callOfTheWildItem;
    }

    public void setCallOfTheWildItem(ItemStack callOfTheWildItem) {
        this.callOfTheWildItem = callOfTheWildItem;
    }

    public int getCallOfTheWildAmount() {
        return callOfTheWildAmount;
    }

    public void setCallOfTheWildAmount(int callOfTheWildAmount) {
        this.callOfTheWildAmount = callOfTheWildAmount;
    }
}
