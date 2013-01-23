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
            xpBoostModifier = Smelting.vanillaXPBoostRank5Multiplier;
        }
        else if (skillLevel >= Smelting.vanillaXPBoostRank4Level) {
            xpBoostModifier = Smelting.vanillaXPBoostRank4Multiplier;
        }
        else if (skillLevel >= Smelting.vanillaXPBoostRank3Level) {
            xpBoostModifier = Smelting.vanillaXPBoostRank3Multiplier;
        }
        else if (skillLevel >= Smelting.vanillaXPBoostRank2Level) {
            xpBoostModifier = Smelting.vanillaXPBoostRank2Multiplier;
        }
        else {
            xpBoostModifier = Smelting.vanillaXPBoostRank1Multiplier;
        }
    }

    protected void modifyVanillaXP() {
        int xp = event.getExpToDrop();
        event.setExpToDrop(xp * xpBoostModifier);
    }
}
