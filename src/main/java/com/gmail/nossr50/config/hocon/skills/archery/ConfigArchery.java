package com.gmail.nossr50.config.hocon.skills.archery;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigArchery {

    @Setting(value = ConfigConstants.SUB_SKILL_NODE)
    private ConfigArcherySubSkills subSkills = new ConfigArcherySubSkills();

    public ConfigArcheryDaze getDaze() {
        return subSkills.getDaze();
    }

    public ConfigArcherySkillShot getSkillShot() {
        return subSkills.getSkillShot();
    }

    public ConfigArcheryLimitBreak getLimitBreak() {
        return subSkills.getLimitBreak();
    }

    public double getSkillShotDamageMultiplier() {
        return subSkills.getSkillShotDamageMultiplier();
    }

    public double getSkillShotDamageCeiling() {
        return subSkills.getSkillShotDamageCeiling();
    }

    public double getBonusDamage() {
        return subSkills.getBonusDamage();
    }
}