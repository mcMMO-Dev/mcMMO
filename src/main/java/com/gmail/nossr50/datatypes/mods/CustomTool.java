package com.gmail.nossr50.datatypes.mods;

import org.bukkit.inventory.ItemStack;

public class CustomTool extends CustomItem {
    private double xpMultiplier;
    private boolean abilityEnabled;

    public CustomTool(short durability, ItemStack repairMaterial, int repairQuantity, boolean repairable, boolean abilityEnabled, double xpMultiplier, int itemID) {
        super(durability, repairMaterial, repairQuantity, repairable, itemID);
        this.xpMultiplier = xpMultiplier;
        this.abilityEnabled = abilityEnabled;
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
}
