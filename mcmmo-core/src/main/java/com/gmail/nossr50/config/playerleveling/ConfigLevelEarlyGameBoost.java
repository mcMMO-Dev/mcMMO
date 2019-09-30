package com.gmail.nossr50.config.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigLevelEarlyGameBoost {

    private static final boolean EARLY_GAME_BOOST_DEFAULT = true;

    @Setting(value = "Enabled", comment = "If set to true, the early game XP boost will be applied." +
            "\nDefault value: " + EARLY_GAME_BOOST_DEFAULT)
    private boolean enableEarlyGameBoost = EARLY_GAME_BOOST_DEFAULT;

    public boolean isEnableEarlyGameBoost() {
        return enableEarlyGameBoost;
    }
}
