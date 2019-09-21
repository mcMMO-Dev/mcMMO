package com.gmail.nossr50.config.hocon.skills.archery;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigArcherySubSkills {

    @Setting(value = "Daze")
    private ConfigArcheryDaze daze = new ConfigArcheryDaze();

    @Setting(value = "Skill-Shot")
    private ConfigArcherySkillShot skillShot = new ConfigArcherySkillShot();

    @Setting("Arrow-Retrieval")
    private ConfigArcheryArrowRetrieval arrowRetrieval = new ConfigArcheryArrowRetrieval();

    @Setting("Limit-Break")
    private ConfigArcheryLimitBreak limitBreak = new ConfigArcheryLimitBreak();

    public ConfigArcheryDaze getDaze() {
        return daze;
    }

    public ConfigArcherySkillShot getSkillShot() {
        return skillShot;
    }

    public ConfigArcheryLimitBreak getLimitBreak() {
        return limitBreak;
    }

    public double getSkillShotDamageMultiplier() {
        return skillShot.getSkillShotDamageMultiplier();
    }

    public double getSkillShotDamageCeiling() {
        return skillShot.getSkillShotDamageCeiling();
    }

    public double getBonusDamage() {
        return daze.getDazeBonusDamage();
    }
}
