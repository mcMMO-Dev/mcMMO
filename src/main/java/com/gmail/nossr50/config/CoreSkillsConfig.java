package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;

public class CoreSkillsConfig extends Config {
    //private static CoreSkillsConfig instance;

    public CoreSkillsConfig() {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(),"coreskills.yml", true);
        super(mcMMO.p.getDataFolder().getAbsoluteFile(),"coreskills.yml", true);
    }

    /*public static CoreSkillsConfig getInstance() {
        if (instance == null)
            return new CoreSkillsConfig();

        return instance;
    }*/

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }

    @Override
    public void unload() {
        instance = null;
    }

    /*
     * Skill Settings
     */

    /**
     * Whether or not a skill is enabled
     * Defaults true
     *
     * @param abstractSubSkill SubSkill definition to check
     * @return true if subskill is enabled
     */
    public boolean isSkillEnabled(AbstractSubSkill abstractSubSkill) {
        return getBooleanValue(StringUtils.getCapitalized(abstractSubSkill.getPrimarySkill().toString()) + "." + abstractSubSkill.getConfigKeyName() + ".Enabled", true);
    }

    /**
     * Whether or not this primary skill is enabled
     *
     * @param primarySkillType target primary skill
     * @return true if enabled
     */
    public boolean isPrimarySkillEnabled(PrimarySkillType primarySkillType) {
        return getBooleanValue(StringUtils.getCapitalized(primarySkillType.toString()) + ".Enabled", true);
    }
}
