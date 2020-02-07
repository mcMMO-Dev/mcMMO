package com.gmail.nossr50.config.skills.swords;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSwordsCounterAttack {

    private static final double DAMAGE_MODIFIER_DEFAULT = 2.0;

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = 30.0;

    @Setting(value = "Damage-Modifier", comment = "The damage returned from Counter-Attack will be equal to the damage dealt divided by this number." +
            "\nDefault value: "+DAMAGE_MODIFIER_DEFAULT)
    private double damageModifier = DAMAGE_MODIFIER_DEFAULT;

    public double getCounterAttackMaxChance() {
        return maxChance;
    }

    public double getCounterAttackDamageModifier() {
        return damageModifier;
    }
}
