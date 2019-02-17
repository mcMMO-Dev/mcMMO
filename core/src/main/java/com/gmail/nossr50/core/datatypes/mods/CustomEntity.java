package com.gmail.nossr50.core.datatypes.mods;


import com.gmail.nossr50.core.mcmmo.item.ItemStack;

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

    public boolean canBeTamed() {
        return canBeTamed;
    }

    public int getTamingXP() {
        return tamingXP;
    }

    public boolean canBeSummoned() {
        return canBeSummoned;
    }

    public ItemStack getCallOfTheWildItem() {
        return callOfTheWildItem;
    }

    public int getCallOfTheWildAmount() {
        return callOfTheWildAmount;
    }
}
