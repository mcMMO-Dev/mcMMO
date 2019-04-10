package com.gmail.nossr50.config.hocon.skills.fishing;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigFishing {

    @Setting(value = "General")
    private ConfigFishingGeneral fishingGeneral = new ConfigFishingGeneral();

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
}