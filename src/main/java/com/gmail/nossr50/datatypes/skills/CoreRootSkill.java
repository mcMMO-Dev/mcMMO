package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.text.StringUtils;
import com.neetgames.mcmmo.skill.RootSkillImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

class CoreRootSkill extends RootSkillImpl {
    public CoreRootSkill(@NotNull String skillName) {
        super(mcMMO.p.getName(), StringUtils.getCapitalized(skillName), "mcmmo.skills." + skillName.toLowerCase(Locale.ENGLISH));
    }
}
