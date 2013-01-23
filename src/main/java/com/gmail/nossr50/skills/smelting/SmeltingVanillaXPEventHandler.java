package com.gmail.nossr50.skills.smelting;

import org.bukkit.event.inventory.FurnaceExtractEvent;

public class SmeltingVanillaXPEventHandler {
    private SmeltingManager manager;
    private FurnaceExtractEvent event;
    private int xpBoostModifier;

    protected SmeltingVanillaXPEventHandler(SmeltingManager manager, FurnaceExtractEvent event) {
        this.manager = manager;
        this.event = event;
    }

    protected void calculateModifier() {
        int skillLevel =  manager.getSkillLevel();

        if (skillLevel >= Smelting.vanillaXPBoostRank5Level) {
            xpBoostModifier = 6;
        }
        else if (skillLevel >= Smelting.vanillaXPBoostRank4Level) {
            xpBoostModifier = 5;
        }
        else if (skillLevel >= Smelting.vanillaXPBoostRank3Level) {
            xpBoostModifier = 4;
        }
        else if (skillLevel >= Smelting.vanillaXPBoostRank2Level) {
            xpBoostModifier = 3;
        }
        else {
            xpBoostModifier = 2;
        }
    }

    protected void modifyVanillaXP() {
        int xp = event.getExpToDrop();
        event.setExpToDrop(xp * xpBoostModifier);
    }
}
