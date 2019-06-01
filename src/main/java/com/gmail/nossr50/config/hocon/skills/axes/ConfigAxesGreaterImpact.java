package com.gmail.nossr50.config.hocon.skills.axes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAxesGreaterImpact {

    private static final double ACTIVATION_CHANCE_DEFAULT = 25.0D;
    public static final double KNOCKBACK_MODIFIER_DEFAULT = 1.5D;
    public static final double BONUS_DAMAGE_DEFAULT = 2.0D;

    @Setting(value = "Activation-Chance", comment = "Chance for this skill to activate, this does not change." +
            "\nDefault value: "+ACTIVATION_CHANCE_DEFAULT)
    private double activationChance = ACTIVATION_CHANCE_DEFAULT;

    @Setting(value = "Knockback-Velocity-Modifier", comment = "Velocity modifier of GreaterImpact hits, this determines how great the knockback is" +
            "\nThe knockback does not occur in PVP" +
            "\nDefault value: "+KNOCKBACK_MODIFIER_DEFAULT)
    private double knockBackModifier = KNOCKBACK_MODIFIER_DEFAULT;

    @Setting(value = "Bonus-Damage", comment = "This value will be added to the total damage when Greater Impact occurs" +
            "\nDefault value: "+ BONUS_DAMAGE_DEFAULT)
    private double bonusDamage = BONUS_DAMAGE_DEFAULT;

    public double getActivationChance() {
        return activationChance;
    }

    public double getKnockBackModifier() {
        return knockBackModifier;
    }

    public double getBonusDamage() {
        return bonusDamage;
    }
}
