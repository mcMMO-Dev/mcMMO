package com.gmail.nossr50.skills;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.skills.utilities.PerksUtils;
import com.gmail.nossr50.skills.utilities.SkillType;

public abstract class SkillManager {
    protected McMMOPlayer mcMMOPlayer;
    protected int activationChance;
    protected SkillType skill;

    public SkillManager(McMMOPlayer mcMMOPlayer, SkillType skill) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.activationChance = PerksUtils.handleLuckyPerks(mcMMOPlayer.getPlayer(), skill);
        this.skill = skill;
    }

    public McMMOPlayer getMcMMOPlayer() {
        return mcMMOPlayer;
    }

    public Player getPlayer() {
        return mcMMOPlayer.getPlayer();
    }

    public PlayerProfile getProfile() {
        return mcMMOPlayer.getProfile();
    }

    public int getSkillLevel() {
        return mcMMOPlayer.getProfile().getSkillLevel(skill);
    }

    public int getActivationChance() {
        return activationChance;
    }

    public void applyXpGain(int xp) {
        mcMMOPlayer.beginXpGain(skill, xp);
    }
}
