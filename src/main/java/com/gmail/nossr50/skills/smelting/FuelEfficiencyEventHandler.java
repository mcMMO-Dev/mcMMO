package com.gmail.nossr50.skills.smelting;

import org.bukkit.event.inventory.FurnaceBurnEvent;

public class FuelEfficiencyEventHandler {
    private SmeltingManager manager;
    private FurnaceBurnEvent event;
    private double burnModifier;

    protected FuelEfficiencyEventHandler(SmeltingManager manager, FurnaceBurnEvent event) {
        this.manager = manager;
        this.event = event;
    }

    protected void calculateBurnModifier() {
        this.burnModifier = 1 + (((double) manager.getSkillLevel() / Smelting.burnModifierMaxLevel) * Smelting.burnTimeMultiplier);
    }

    protected void modifyBurnTime() {
        int burnTime = event.getBurnTime();
        event.setBurnTime((int)(burnTime * burnModifier));
    }
}
