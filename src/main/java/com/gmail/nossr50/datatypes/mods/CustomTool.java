package com.gmail.nossr50.datatypes.mods;

public class CustomTool extends CustomItem {
    private double xpMultiplier;
    private boolean abilityEnabled;
    private int tier;

    public CustomTool(int tier, boolean abilityEnabled, double xpMultiplier, short durability, int itemID) {
        super(itemID, durability);
        this.xpMultiplier = xpMultiplier;
        this.abilityEnabled = abilityEnabled;
        this.tier = tier;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    public void setXpMultiplier(Double xpMultiplier) {
        this.xpMultiplier = xpMultiplier;
    }

    public boolean isAbilityEnabled() {
        return abilityEnabled;
    }

    public void setAbilityEnabled(boolean abilityEnabled) {
        this.abilityEnabled = abilityEnabled;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }
}
