package com.gmail.nossr50.config.hocon.skills.axes;

import com.gmail.nossr50.datatypes.skills.properties.AbstractDamageProperty;
import com.gmail.nossr50.datatypes.skills.properties.AbstractMaximumProgressionLevel;
import com.gmail.nossr50.datatypes.skills.properties.DamageProperty;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAxesCriticalStrikes {

    private static final double MAX_ACTIVATION_CHANCE_DEFAULT = 37.50D;

    @Setting(value = "Max-Activation-Chance", comment = "This is max percentage chance that is used to determine whether or not the ability is successful." +
            "\nA value of 50.0 would mean at most the ability can only have a 50% chance to work at max skill level.")
    private double maxActivationChance = MAX_ACTIVATION_CHANCE_DEFAULT;

    @Setting(value = "Maximum-Level", comment = "This is the level at which full benefits for this skill will be reached." +
            "\nProperties of this skill may or may not scale with level, but those that do will gradually increase until max level is achieved.")
    private AbstractMaximumProgressionLevel maximumProgressionLevel = new AbstractMaximumProgressionLevel(100, 1000);

    @Setting(value = "Damage-Modifiers", comment = "Damage dealt is multiplied by these values when this skill is successfully activated.")
    private DamageProperty damageProperty = new AbstractDamageProperty(1.5, 2.0);

    public double getMaxActivationChance() {
        return maxActivationChance;
    }

    public AbstractMaximumProgressionLevel getMaximumProgressionLevel() {
        return maximumProgressionLevel;
    }

    public DamageProperty getDamageProperty() {
        return damageProperty;
    }
}
