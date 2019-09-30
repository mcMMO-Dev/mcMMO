package com.gmail.nossr50.config.skills.axes;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.DamageProperty;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAxes {

    @Setting(value = ConfigConstants.SUB_SKILL_NODE, comment = "Settings for Axes Sub-Skills.")
    private ConfigAxesSubSkills subSkills = new ConfigAxesSubSkills();

    public ConfigAxesSubSkills getSubSkills() {
        return subSkills;
    }

    public ConfigAxesAxeMastery getConfigAxesAxeMastery() {
        return subSkills.getConfigAxesAxeMastery();
    }

    public double getGreaterImpactKnockBackModifier() {
        return subSkills.getGreaterImpactKnockBackModifier();
    }

    public double getGreaterImpactBonusDamage() {
        return subSkills.getGreaterImpactBonusDamage();
    }

    public DamageProperty getCriticalStrikesDamageProperty() {
        return subSkills.getCriticalStrikesDamageProperty();
    }

    public double getSkullSplitterDamageDivisor() {
        return subSkills.getSkullSplitterDamageDivisor();
    }

    public ConfigAxesCriticalStrikes getConfigAxesCriticalStrikes() {
        return subSkills.getConfigAxesCriticalStrikes();
    }

    public ConfigAxesGreaterImpact getConfigAxesGreaterImpact() {
        return subSkills.getConfigAxesGreaterImpact();
    }

    public ConfigAxesImpact getConfigAxesImpact() {
        return subSkills.getConfigAxesImpact();
    }

    public ConfigAxesSkullSplitter getConfigAxesSkullSplitter() {
        return subSkills.getConfigAxesSkullSplitter();
    }

    public double getImpactDurabilityDamageModifier() {
        return subSkills.getImpactDurabilityDamageModifier();
    }

    public double getAxeMasteryMultiplier() {
        return subSkills.getAxeMasteryMultiplier();
    }
}