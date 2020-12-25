package com.gmail.nossr50.datatypes.skills;

import com.neetgames.mcmmo.skill.RootSkill;
import com.neetgames.mcmmo.skill.SkillImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoreSkill extends SkillImpl {

    //TODO: Change to passing SkillIdentity
    public CoreSkill(@NotNull String pluginName, @NotNull String skillName, @Nullable String permission, @NotNull RootSkill parentSkill) {
        super(pluginName, skillName, permission, parentSkill);
    }

}
