package com.gmail.nossr50.config.hocon.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionLevelCaps {
    /* DEFAULT VALUES */
    public static final boolean TRUNCATE_SKILLS_ABOVE_CAP_DEFAULT = true;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Reduce-Player-Skills-Above-Cap",
            comment = "Players with skills above the cap will have those skills reduced to the cap" +
                    "\nDefault value: "+TRUNCATE_SKILLS_ABOVE_CAP_DEFAULT)
    private boolean truncateSkillsAboveCap = TRUNCATE_SKILLS_ABOVE_CAP_DEFAULT;

    @Setting(value = "Power-Level",
            comment = "Power Level is the sum of all of a players skills." +
                    "\nEnable this cap if you want to force players into specializing into specific skills")
    private ConfigSectionSkillLevelCap powerLevel = new ConfigSectionSkillLevelCap();

    @Setting(value = "Skills", comment = "Per Skill cap settings")
    private ConfigSectionSkills configSectionSkills = new ConfigSectionSkills();

    /*
     * GETTER BOILERPLATE
     */

    public ConfigSectionSkillLevelCap getPowerLevel() {
        return powerLevel;
    }

    public boolean getReducePlayerSkillsAboveCap() {
        return truncateSkillsAboveCap;
    }

    public ConfigSectionSkills getConfigSectionSkills() {
        return configSectionSkills;
    }
}
