package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigExperienceArchery {

    public static final double DISTANCE_MULTIPLIER_DEFAULT = 0.025;

    @Setting(value = "Distance-Multiplier", comment = "The distance multiplier is multiplied against the distance an " +
            "arrow travels before hitting its target to determine final XP values awarded." +
            "\nThe maximum distance bonus is 50, so expect this multiplier to peak at being multiplied against 50." +
            "\nDistance is in blocks traveled." +
            "\nThis value is added on to normal XP gains from damage for Archery." +
            "\nDefault value: " + DISTANCE_MULTIPLIER_DEFAULT)
    private double distanceMultiplier = DISTANCE_MULTIPLIER_DEFAULT;

    public double getDistanceMultiplier() {
        return distanceMultiplier;
    }
}