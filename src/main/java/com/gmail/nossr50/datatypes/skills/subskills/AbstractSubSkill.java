package com.gmail.nossr50.datatypes.skills.subskills;

import com.gmail.nossr50.config.CoreSkillsConfig;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.Interaction;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.Rank;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.SubSkill;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.SubSkillProperties;
import com.gmail.nossr50.locale.LocaleLoader;

public abstract class AbstractSubSkill implements SubSkill, Interaction, Rank, SubSkillProperties {
    /*
     * The name of the subskill is important is used to pull Locale strings and config settings
     */
    protected String configKeySubSkill;
    protected String configKeyPrimary;

    public AbstractSubSkill(String configKeySubSkill, String configKeyPrimary)
    {
        this.configKeySubSkill = configKeySubSkill;
        this.configKeyPrimary = configKeyPrimary;
    }

    /**
     * Returns the simple description of this subskill from the locale
     *
     * @return the simple description of this subskill from the locale
     */
    @Override
    public String getDescription() {
        return LocaleLoader.getString(getPrimaryKeyName()+".SubSkill."+getConfigKeyName()+".Description");
    }

    /**
     * Whether or not this subskill is enabled
     *
     * @return true if enabled
     */
    @Override @Deprecated
    public boolean isEnabled() {
        //TODO: This might be troublesome...
        return CoreSkillsConfig.getInstance().isSkillEnabled(this);
    }
}
