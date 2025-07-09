package com.gmail.nossr50.datatypes.mods;

import org.bukkit.inventory.ItemStack;

public class CustomEntity {
    private final double xpMultiplier;
    private final boolean canBeTamed;
    private final int tamingXP;
    private final boolean canBeSummoned;
    private final ItemStack callOfTheWildItem;
    private final int callOfTheWildAmount;

    public CustomEntity(double xpMultiplier, boolean canBeTamed, int tamingXP,
            boolean canBeSummoned, ItemStack callOfTheWildItem, int callOfTheWildAmount) {
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
