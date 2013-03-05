package com.gmail.nossr50.events.experience;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;

/**
 * Called when a user levels up in a skill
 */
public class McMMOPlayerLevelUpEvent extends McMMOPlayerExperienceEvent {
    private int levelsGained;

    public McMMOPlayerLevelUpEvent(Player player, SkillType skill) {
        super(player, skill);
        this.levelsGained = 1;
    }

    public McMMOPlayerLevelUpEvent(Player player, SkillType skill, int levelsGained) {
        super(player, skill);
        this.levelsGained = levelsGained;
    }

    /**
     * @return The number of levels gained in this event
     */
    public int getLevelsGained() {
        return levelsGained;
    }
}
