package com.gmail.nossr50.core.config;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.core.util.StringUtils;

public class CoreSkillsConfig extends ConfigurableLoader {
    private static CoreSkillsConfig instance;

    public CoreSkillsConfig() {
        super(McmmoCore.getDataFolderPath().getAbsoluteFile(),"coreskills.yml");
        validate();
    }

    public static CoreSkillsConfig getInstance() {
        if (instance == null)
            return new CoreSkillsConfig();

        return instance;
    }

    @Override
    protected void loadKeys() {

    }

    @Override
    protected boolean validateKeys() {

        return true;
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
        return config.getBoolean(StringUtils.getCapitalized(abstractSubSkill.getPrimarySkill().toString()) + "." + abstractSubSkill.getConfigKeyName() + ".Enabled", true);
    }

    /**
     * Whether or not this primary skill is enabled
     *
     * @param primarySkillType target primary skill
     * @return true if enabled
     */
    public boolean isPrimarySkillEnabled(PrimarySkillType primarySkillType) {
        return config.getBoolean(StringUtils.getCapitalized(primarySkillType.toString()) + ".Enabled", true);
    }
}
