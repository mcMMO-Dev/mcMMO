package com.gmail.nossr50.config.hocon.skills.fishing;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigFishing {

    @Setting(value = "Sub-Skills")
    public ConfigFishingSubskills fishingSubskills = new ConfigFishingSubskills();
    @Setting(value = "General")
    private ConfigFishingGeneral fishingGeneral = new ConfigFishingGeneral();

    /*
     * GETTERS BOILERPLATE
     */

    public ConfigFishingGeneral getFishingGeneral() {
        return fishingGeneral;
    }

    public boolean isAlwaysCatchFish() {
        return fishingGeneral.isAlwaysCatchFish();
    }

    public double getLureLuckModifier() {
        return fishingGeneral.getLureLuckModifier();
    }

    public boolean isOverrideVanillaTreasures() {
        return fishingGeneral.isOverrideVanillaTreasures();
    }

    public boolean isAllowCustomDrops() {
        return fishingGeneral.isAllowCustomDrops();
    }

    public ConfigFishingSubskills getFishingSubskills() {
        return fishingSubskills;
    }

    public ConfigFishingInnerPeace getInnerPeace() {
        return fishingSubskills.getInnerPeace();
    }

    public HashMap<Integer, Integer> getInnerPeaceVanillaXPMultiplier() {
        return getInnerPeace().getInnerPeaceVanillaXPMultiplier();
    }

    public int getVanillaXPMultInnerPeace(int rank) {
        return getInnerPeaceVanillaXPMultiplier().get(rank);
    }
}