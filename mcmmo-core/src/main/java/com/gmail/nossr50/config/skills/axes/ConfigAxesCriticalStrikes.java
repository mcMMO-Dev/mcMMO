package com.gmail.nossr50.config.skills.axes;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.AbstractDamageProperty;
import com.gmail.nossr50.datatypes.skills.properties.DamageProperty;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAxesCriticalStrikes {

    private static final double MAX_ACTIVATION_CHANCE_DEFAULT = 37.50D;

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = MAX_ACTIVATION_CHANCE_DEFAULT;

    @Setting(value = "Damage-Modifiers", comment = "Damage dealt is multiplied by these values when this skill is successfully activated.")
    private DamageProperty damageProperty = new AbstractDamageProperty(1.5, 2.0);

    public double getMaxActivationChance() {
        return maxChance;
    }

    public DamageProperty getDamageProperty() {
        return damageProperty;
    }
}
