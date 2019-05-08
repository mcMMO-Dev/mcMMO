package com.gmail.nossr50.config.hocon.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigLevelEarlyGameBoost {

    public static final boolean EARLY_GAME_BOOST_DEFAULT = true;
    public static final double BOOST_MULTIPLIER_DEFAULT = 0.05D;

    @Setting(value = "Enabled", comment = "If set to true, the early game XP boost will be applied." +
            "\nDefault value: " + EARLY_GAME_BOOST_DEFAULT)
    private boolean enableEarlyGameBoost = EARLY_GAME_BOOST_DEFAULT;

    @Setting(value = "Max-Level-Percentage", comment = "This value is multiplied by a skills level cap to see determine when to stop giving a boost." +
            "\nLevels in mcMMO are not capped by default, so if the skill has no set level cap it will instead use the value 100 or 1000 (if in RetroMode)" +
            "\nWith default settings, this will result in the first 5 levels (or 50 in Retro) being boosted" +
            "\nDefault value: " + BOOST_MULTIPLIER_DEFAULT)
    private double earlyGameBoostMultiplier = BOOST_MULTIPLIER_DEFAULT;

    public double getEarlyGameBoostMultiplier() {
        return earlyGameBoostMultiplier;
    }

    public boolean isEnableEarlyGameBoost() {
        return enableEarlyGameBoost;
    }
}
