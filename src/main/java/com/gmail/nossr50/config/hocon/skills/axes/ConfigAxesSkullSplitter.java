package com.gmail.nossr50.config.hocon.skills.axes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAxesSkullSplitter {

    private static final double SKULL_SPLITTER_DAMAGE_DIVISOR_DEFAULT = 2.0D;

    @Setting(value = "Damage-Divisor", comment = "Damage dealt to targets by Skull Splitter will be divided by this number" +
            "\nDefault value: "+SKULL_SPLITTER_DAMAGE_DIVISOR_DEFAULT)
    private double skullSplitterDamageDivisor = SKULL_SPLITTER_DAMAGE_DIVISOR_DEFAULT;

    public double getSkullSplitterDamageDivisor() {
        return skullSplitterDamageDivisor;
    }
}
