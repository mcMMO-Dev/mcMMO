package com.gmail.nossr50.config.hocon.experience;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.*;

@ConfigSerializable
public class ConfigExperienceSkillMultiplier {

    private static final HashMap<PrimarySkillType, Float> SKILL_GLOBAL_MULT_DEFAULT;

    static {
        SKILL_GLOBAL_MULT_DEFAULT = new HashMap<>();
        SKILL_GLOBAL_MULT_DEFAULT.put(ACROBATICS, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(ALCHEMY, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(ARCHERY, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(AXES, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(EXCAVATION, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(FISHING, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(HERBALISM, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(MINING, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(REPAIR, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(SWORDS, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(TAMING, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(UNARMED, 1.0f);
        SKILL_GLOBAL_MULT_DEFAULT.put(WOODCUTTING, 1.0f);
    }

    @Setting(value = "Skill-XP-Multipliers")
    private HashMap<PrimarySkillType, Float> perSkillGlobalMultiplier = SKILL_GLOBAL_MULT_DEFAULT;

    public double getSkillGlobalMultiplier(PrimarySkillType primarySkillType) {
        return perSkillGlobalMultiplier.get(primarySkillType);
    }
}
