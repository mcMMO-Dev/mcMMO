package com.gmail.nossr50.config.skills.axes;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAxesImpact {

    private static final double IMPACT_CHANCE_DEFAULT = 25.0D;
    private static final double IMPACT_DURABILITY_MULTIPLIER_DEFAULT = 6.5D;

    @Setting(value = ConfigConstants.STATIC_ACTIVATION_FIELD_NAME, comment = "Chance to activate the Impact skill, this is a static chance and does not change per rank of the skill." +
            "\nDefault value: "+IMPACT_CHANCE_DEFAULT)
    private double impactChance = IMPACT_CHANCE_DEFAULT;

    @Setting(value = "Impact-Durability-Damage-Multiplier", comment = "The amount of durability damage done by Impact is multiplied by this number" +
            "\nThe damage done by impact starts at 1 and increases by 1 every rank, this value is then multiplied by this variable to determine the durability damage done to armor." +
            "\nDefault value: "+IMPACT_DURABILITY_MULTIPLIER_DEFAULT)
    private double impactDurabilityDamageModifier = IMPACT_DURABILITY_MULTIPLIER_DEFAULT;

    public double getImpactDurabilityDamageModifier() {
        return impactDurabilityDamageModifier;
    }
}
