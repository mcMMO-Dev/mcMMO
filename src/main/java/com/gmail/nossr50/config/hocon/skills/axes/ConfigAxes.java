package com.gmail.nossr50.config.hocon.skills.axes;

import com.gmail.nossr50.datatypes.skills.properties.AbstractMaximumProgressionLevel;
import com.gmail.nossr50.datatypes.skills.properties.DamageProperty;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAxes {


    @Setting(value = "Axe-Mastery")
    private ConfigAxesAxeMastery configAxesAxeMastery = new ConfigAxesAxeMastery();

    @Setting(value = "Critical-Strikes")
    private ConfigAxesCriticalStrikes configAxesCriticalStrikes = new ConfigAxesCriticalStrikes();

    @Setting(value = "Greater-Impact")
    private ConfigAxesGreaterImpact configAxesGreaterImpact = new ConfigAxesGreaterImpact();

    @Setting(value = "Impact")
    private ConfigAxesImpact configAxesImpact = new ConfigAxesImpact();

    @Setting(value = "Skull-Splitter")
    private ConfigAxesSkullSplitter configAxesSkullSplitter = new ConfigAxesSkullSplitter();

    public double getCriticalStrikesMaxActivationChance() {
        return configAxesCriticalStrikes.getMaxActivationChance();
    }

    public AbstractMaximumProgressionLevel getCriticalStrikesMaximumProgressionLevel() {
        return configAxesCriticalStrikes.getMaximumProgressionLevel();
    }

    public double getGreaterImpactActivationChance() {
        return configAxesGreaterImpact.getActivationChance();
    }

    public double getGreaterImpactKnockBackModifier() {
        return configAxesGreaterImpact.getKnockBackModifier();
    }

    public double getGreaterImpactBonusDamage() {
        return configAxesGreaterImpact.getBonusDamage();
    }

    public DamageProperty getCriticalStrikesDamageProperty() {
        return configAxesCriticalStrikes.getDamageProperty();
    }

    public double getSkullSplitterDamageDivisor() {
        return configAxesSkullSplitter.getSkullSplitterDamageDivisor();
    }

    public ConfigAxesAxeMastery getConfigAxesAxeMastery() {
        return configAxesAxeMastery;
    }

    public ConfigAxesCriticalStrikes getConfigAxesCriticalStrikes() {
        return configAxesCriticalStrikes;
    }

    public ConfigAxesGreaterImpact getConfigAxesGreaterImpact() {
        return configAxesGreaterImpact;
    }

    public ConfigAxesImpact getConfigAxesImpact() {
        return configAxesImpact;
    }

    public ConfigAxesSkullSplitter getConfigAxesSkullSplitter() {
        return configAxesSkullSplitter;
    }

    public double getImpactChance() {
        return configAxesImpact.getImpactChance();
    }

    public double getImpactDurabilityDamageModifier() {
        return configAxesImpact.getImpactDurabilityDamageModifier();
    }

    public double getAxeMasteryMultiplier() {
        return configAxesAxeMastery.getAxeMasteryMultiplier();
    }
}