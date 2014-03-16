package com.gmail.nossr50.events.hardcore;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;

public class McMMOPlayerVampirismPenaltyEvent extends McMMOPlayerDeathPenaltyEvent {
    private int levelChanged;
    private float experienceChanged;

    public McMMOPlayerVampirismPenaltyEvent(Player player, SkillType skill, int levelChanged, float experienceChanged) {
        super(player, skill);
        this.levelChanged = levelChanged;
        this.experienceChanged = experienceChanged;
    }

    public int getLevelChanged() {
        return levelChanged;
    }

    public void setLevelChanged(int levelChanged) {
        this.levelChanged = levelChanged;
    }

    public float getExperienceChanged() {
        return experienceChanged;
    }

    public void setExperienceChanged(float experienceChanged) {
        this.experienceChanged = experienceChanged;
    }
}
