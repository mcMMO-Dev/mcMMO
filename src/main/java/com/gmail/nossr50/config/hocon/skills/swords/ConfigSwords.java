package com.gmail.nossr50.config.hocon.skills.swords;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSwords {

    @Setting(value = ConfigConstants.SUB_SKILL_NODE)
    private ConfigSwordsSubSkills subSkills = new ConfigSwordsSubSkills();

    public ConfigSwordsSubSkills getSubSkills() {
        return subSkills;
    }

    public ConfigSwordsCounterAttack getCounterAttack() {
        return subSkills.getCounterAttack();
    }

    public ConfigSwordsRupture getRupture() {
        return subSkills.getRupture();
    }

    public ConfigSwordsSerratedStrikes getSerratedStrikes() {
        return subSkills.getSerratedStrikes();
    }

    public double getCounterAttackMaxChance() {
        return subSkills.getCounterAttackMaxChance();
    }

    public MaxBonusLevel getCounterAttackMaxBonusLevel() {
        return subSkills.getCounterAttackMaxBonusLevel();
    }

    public double getCounterAttackDamageModifier() {
        return subSkills.getCounterAttackDamageModifier();
    }

    public double getSerratedStrikesDamageModifier() {
        return subSkills.getSerratedStrikesDamageModifier();
    }

    public double getRuptureMaxChance() {
        return subSkills.getRuptureMaxChance();
    }

    public MaxBonusLevel getRuptureMaxBonusLevel() {
        return subSkills.getRuptureMaxBonusLevel();
    }

    public double getRuptureDamagePlayer() {
        return subSkills.getRuptureDamagePlayer();
    }

    public double getRuptureDamageMobs() {
        return subSkills.getRuptureDamageMobs();
    }

    public int getRuptureBaseTicks() {
        return subSkills.getRuptureBaseTicks();
    }
}