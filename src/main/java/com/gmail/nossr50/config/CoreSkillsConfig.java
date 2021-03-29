package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.util.text.StringUtils;

public class CoreSkillsConfig extends AutoUpdateConfigLoader {
    private static CoreSkillsConfig instance;

    public CoreSkillsConfig()
    {
        super("coreskills.yml");
        validate();
    }

    @Override
    protected void loadKeys() {

    }

    public static CoreSkillsConfig getInstance()
    {
        if(instance == null)
            return new CoreSkillsConfig();

        return instance;
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
     * @param abstractSubSkill SubSkill definition to check
     * @return true if subskill is enabled
     */
    public boolean isSkillEnabled(AbstractSubSkill abstractSubSkill)
    {
        return config.getBoolean(StringUtils.getCapitalized(abstractSubSkill.getPrimarySkill().toString())+"."+ abstractSubSkill.getConfigKeyName()+".Enabled", true);
    }

    /**
     * Whether or not this primary skill is enabled
     * @param primarySkillType target primary skill
     * @return true if enabled
     */
    public boolean isPrimarySkillEnabled(PrimarySkillType primarySkillType)
    {
        return config.getBoolean(StringUtils.getCapitalized(primarySkillType.toString())+".Enabled", true);
    }
}
