package com.gmail.nossr50.config.skills.swords;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSwordsSubSkills {

    @Setting(value = "Counter-Attack")
    private ConfigSwordsCounterAttack counterAttack = new ConfigSwordsCounterAttack();

    @Setting(value = "Rupture")
    private ConfigSwordsRupture rupture = new ConfigSwordsRupture();

    @Setting(value = "Serrated-Strikes")
    private ConfigSwordsSerratedStrikes serratedStrikes = new ConfigSwordsSerratedStrikes();

    @Setting(value = "Limit-Break")
    private ConfigSwordsLimitBreak swordsLimitBreak = new ConfigSwordsLimitBreak();

    public ConfigSwordsCounterAttack getCounterAttack() {
        return counterAttack;
    }

    public ConfigSwordsRupture getRupture() {
        return rupture;
    }

    public ConfigSwordsSerratedStrikes getSerratedStrikes() {
        return serratedStrikes;
    }

    public double getCounterAttackMaxChance() {
        return counterAttack.getCounterAttackMaxChance();
    }

    public double getCounterAttackDamageModifier() {
        return counterAttack.getCounterAttackDamageModifier();
    }

    public double getSerratedStrikesDamageModifier() {
        return serratedStrikes.getSerratedStrikesDamageModifier();
    }

    public double getRuptureMaxChance() {
        return rupture.getRuptureMaxChance();
    }

    public double getRuptureDamagePlayer() {
        return rupture.getRuptureDamagePlayer();
    }

    public double getRuptureDamageMobs() {
        return rupture.getRuptureDamageMobs();
    }

    public int getRuptureBaseTicks() {
        return rupture.getRuptureBaseTicks();
    }

    public ConfigSwordsLimitBreak getSwordsLimitBreak() {
        return swordsLimitBreak;
    }
}
