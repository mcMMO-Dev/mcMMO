package com.gmail.nossr50.skills;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.utilities.PerksUtils;
import com.gmail.nossr50.skills.utilities.SkillType;

public abstract class SkillManager {
    protected McMMOPlayer mcMMOPlayer;
    protected int skillLevel;
    protected int activationChance;
    protected SkillType skill;

    public SkillManager(McMMOPlayer mcMMOPlayer, SkillType skill) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.skillLevel = mcMMOPlayer.getProfile().getSkillLevel(skill);
        this.activationChance = PerksUtils.handleLuckyPerks(mcMMOPlayer.getPlayer(), skill);
        this.skill = skill;
    }

    public McMMOPlayer getMcMMOPlayer() {
        return mcMMOPlayer;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getActivationChance() {
        return activationChance;
    }

    public SkillType getSkill() {
        return skill;
    }
}
