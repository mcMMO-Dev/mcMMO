package com.gmail.nossr50.config.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigExperienceArchery {

    private static final double DISTANCE_MULTIPLIER_DEFAULT = 0.025;
    private static final double ARROW_FORCE_XP_MULTIPLIER = 2.0D;

    @Setting(value = "Distance-Multiplier", comment = "The distance multiplier is multiplied against the distance an " +
            "arrow travels before hitting its target to determine final XP values awarded." +
            "\nThe maximum distance bonus is 50, so expect this multiplier to peak at being multiplied against 50." +
            "\nDistance is in blocks traveled." +
            "\nThis value is added on to normal XP gains from damage for Archery." +
            "\nDefault value: " + DISTANCE_MULTIPLIER_DEFAULT)
    private double distanceMultiplier = DISTANCE_MULTIPLIER_DEFAULT;

    @Setting(value = "Arrow-Force-XP-Multiplier", comment = "How much velocity the arrow has after leaving the players bow is used in the XP formula for handing out Archery XP." +
            "\nDefault value: "+ARROW_FORCE_XP_MULTIPLIER)
    private double forceMultiplier = ARROW_FORCE_XP_MULTIPLIER;

    public double getForceMultiplier() {
        return forceMultiplier;
    }

    public double getDistanceMultiplier() {
        return distanceMultiplier;
    }
}