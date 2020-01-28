package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;

import java.util.Locale;

public class SkillConfigFactory {

    protected static SerializedConfigLoader initSkillConfig(mcMMO pluginRef, PrimarySkillType primarySkillType, Class<?> clazz) {
        return new SerializedConfigLoader(pluginRef, clazz,
                primarySkillType.toString().toLowerCase(Locale.ENGLISH) + ".conf",
                StringUtils.getCapitalized(primarySkillType.toString()),
                null);
    }
}
