package com.gmail.nossr50.config;

import com.gmail.nossr50.config.hocon.SerializedConfigLoader;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.StringUtils;

public class SkillConfigFactory {

    protected static SerializedConfigLoader initSkillConfig(PrimarySkillType primarySkillType, Class<?> clazz) {
        return new SerializedConfigLoader(clazz,
                primarySkillType.toString().toLowerCase() + ".conf",
                StringUtils.getCapitalized(primarySkillType.toString()),
                null);
    }
}
