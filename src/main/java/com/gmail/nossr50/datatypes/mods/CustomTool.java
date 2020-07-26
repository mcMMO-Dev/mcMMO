package com.gmail.nossr50.datatypes.mods;

public class CustomTool {
    private final double xpMultiplier;
    private final boolean abilityEnabled;
    private final int tier;

    public CustomTool(int tier, boolean abilityEnabled, double xpMultiplier) {
        this.xpMultiplier = xpMultiplier;
        this.abilityEnabled = abilityEnabled;
        this.tier = tier;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    public boolean isAbilityEnabled() {
        return abilityEnabled;
    }

    public int getTier() {
        return tier;
    }
}
