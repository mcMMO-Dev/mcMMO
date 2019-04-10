package com.gmail.nossr50.config.hocon.skills.fishing;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigFishingGeneral {

    private static final boolean ALWAYS_CATCH_FISH_DEFAULT = true;

    @Setting(value = "Always-Catch-Fish", comment = "Enables fish to be caught alongside treasure." +
            "\nDefault value: "+ALWAYS_CATCH_FISH_DEFAULT)
    private boolean alwaysCatchFish = ALWAYS_CATCH_FISH_DEFAULT;

    public boolean isAlwaysCatchFish() {
        return alwaysCatchFish;
    }
}