package com.gmail.nossr50.skills;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Permissions;

public abstract class SkillManager {
    protected McMMOPlayer mcMMOPlayer;
    protected int skillLevel;
    protected int activationChance;

    public SkillManager(McMMOPlayer mcMMOPlayer, SkillType skill) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.skillLevel = mcMMOPlayer.getProfile().getSkillLevel(skill);
        this.activationChance = SkillTools.calculateActivationChance(Permissions.lucky(mcMMOPlayer.getPlayer(), skill));
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
}
