package com.gmail.nossr50.config.skills.fishing;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigFishingGeneral {

    public static final double LURE_MODIFIER_DEFAULT = 4.0D;
    private static final boolean ALWAYS_CATCH_FISH_DEFAULT = true;
    private static final boolean OVERRIDE_VANILLA_TREASURES = true;
    private static final boolean ALLOW_MCMMO_FISHING_REWARDS = true;

    @Setting(value = "Always-Catch-Fish", comment = "Enables fish to be caught alongside treasure." +
            "\nDefault value: " + ALWAYS_CATCH_FISH_DEFAULT)
    private boolean alwaysCatchFish = ALWAYS_CATCH_FISH_DEFAULT;

    @Setting(value = "Override-Vanilla-Fishing-Treasures", comment = "When set to true, mcMMO fishing loot tables will be used instead of vanilla." +
            "\nIt is recommended you use vanilla mcMMO fishing tables, as they are configurable." +
            "\nDefault value: " + OVERRIDE_VANILLA_TREASURES)
    private boolean overrideVanillaTreasures = OVERRIDE_VANILLA_TREASURES;

    @Setting(value = "Lure-Luck-Modifier", comment = "Lure luck modifier is used to determine how much to" +
            " increase drop chance by for fishing rods with the Luck enchantment." +
            "\nDefault value: " + LURE_MODIFIER_DEFAULT)
    private double lureLuckModifier = LURE_MODIFIER_DEFAULT;

    @Setting(value = "Allow-Custom-Fishing-Drops", comment = "If set to true, allows mcMMO fishing treasures to be found while fishing." +
            "\nDefault value: " + ALLOW_MCMMO_FISHING_REWARDS)
    private boolean allowCustomDrops = ALLOW_MCMMO_FISHING_REWARDS;

    public boolean isAllowCustomDrops() {
        return allowCustomDrops;
    }

    public double getLureLuckModifier() {
        return lureLuckModifier;
    }

    public boolean isAlwaysCatchFish() {
        return alwaysCatchFish;
    }

    public boolean isOverrideVanillaTreasures() {
        return overrideVanillaTreasures;
    }
}