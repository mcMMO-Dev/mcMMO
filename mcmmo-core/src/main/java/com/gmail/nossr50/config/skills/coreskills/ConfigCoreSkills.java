package com.gmail.nossr50.config.skills.coreskills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigCoreSkills {

    @Setting(value = "Core Skills for Acrobatics" +
            "\nCore skills are ones built into mcMMO")
    private ConfigCoreSkillsAcrobatics configCoreSkillsAcrobatics = new ConfigCoreSkillsAcrobatics();

    @Setting(value = "Combat-Settings", comment = "Determine whether or not a skills effects can activate in PVP or PVE" +
            "\nIf a skill has no combat interactions, the toggle for it will still exists in case I ever do add combat interactions for the skill.")
    private ConfigCoreSkillsCombatToggles combatToggles = new ConfigCoreSkillsCombatToggles();

    public boolean isRollEnabled() {
        return configCoreSkillsAcrobatics.isRollEnabled();
    }

    public ConfigCoreSkillsAcrobatics getConfigCoreSkillsAcrobatics() {
        return configCoreSkillsAcrobatics;
    }

    public boolean isAcrobaticsEnabled() {
        return getConfigCoreSkillsAcrobatics().isAcrobaticsEnabled();
    }

    public ConfigCoreSkillsCombatToggles getCombatToggles() {
        return combatToggles;
    }

    public ConfigCoreSkillCombatMap getPve() {
        return combatToggles.getPve();
    }

    public ConfigCoreSkillCombatMap getPvp() {
        return combatToggles.getPvp();
    }

    public boolean isPVEEnabled(PrimarySkillType primarySkillType) {
        return combatToggles.isPVEEnabled(primarySkillType);
    }

    public boolean isPVPEnabled(PrimarySkillType primarySkillType) {
        return combatToggles.isPVPEnabled(primarySkillType);
    }
}
