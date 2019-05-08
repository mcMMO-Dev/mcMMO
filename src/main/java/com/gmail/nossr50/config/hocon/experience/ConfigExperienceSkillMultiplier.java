package com.gmail.nossr50.config.hocon.experience;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceSkillMultiplier {

    private static final HashMap<PrimarySkillType, Double> SKILL_GLOBAL_MULT_DEFAULT;

    static {
        SKILL_GLOBAL_MULT_DEFAULT = new HashMap<>();
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (primarySkillType.isChildSkill())
                continue;

            SKILL_GLOBAL_MULT_DEFAULT.put(primarySkillType, 1.0D);
        }
    }

    @Setting(value = "Skill-XP-Multipliers")
    private HashMap<PrimarySkillType, Double> perSkillGlobalMultiplier = SKILL_GLOBAL_MULT_DEFAULT;

    public double getSkillGlobalMultiplier(PrimarySkillType primarySkillType) {
        return perSkillGlobalMultiplier.get(primarySkillType);
    }
}
