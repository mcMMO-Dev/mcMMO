package com.gmail.nossr50.config.hocon.skills.archery;

import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigArchery {

    @Setting(value = "Daze")
    private ConfigArcheryDaze daze = new ConfigArcheryDaze();

    @Setting(value = "Skill-Shot")
    private ConfigArcherySkillShot skillShot = new ConfigArcherySkillShot();

    public ConfigArcheryDaze getDaze() {
        return daze;
    }

    public ConfigArcherySkillShot getSkillShot() {
        return skillShot;
    }

    public double getSkillShotDamageMultiplier() {
        return skillShot.getSkillShotDamageMultiplier();
    }

    public double getSkillShotDamageCeiling() {
        return skillShot.getSkillShotDamageCeiling();
    }

    public double getMaxChance() {
        return daze.getMaxChance();
    }

    public MaxBonusLevel getMaxBonusLevel() {
        return daze.getMaxBonusLevel();
    }

    public double getBonusDamage() {
        return daze.getDazeBonusDamage();
    }
}