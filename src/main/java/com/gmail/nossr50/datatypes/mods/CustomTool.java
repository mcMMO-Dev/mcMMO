package com.gmail.nossr50.datatypes.mods;

import org.bukkit.Material;

public class CustomTool {
    private Material material;
    private double xpMultiplier;
    private boolean abilityEnabled;
    private int tier;

    public CustomTool(int tier, boolean abilityEnabled, double xpMultiplier, int itemID) {
        this.material = Material.getMaterial(itemID);
        this.xpMultiplier = xpMultiplier;
        this.abilityEnabled = abilityEnabled;
        this.tier = tier;
    }

    public Material getType() {
        return material;
    }

    public void setType(Material material) {
        this.material = material;
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
