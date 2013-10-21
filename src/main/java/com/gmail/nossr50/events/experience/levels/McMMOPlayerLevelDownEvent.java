package com.gmail.nossr50.events.experience.levels;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;

/**
 * Called when a user loses levels in a skill
 */
public class McMMOPlayerLevelDownEvent extends McMMOPlayerLevelChangeEvent {
    private int levelsLost;

    public McMMOPlayerLevelDownEvent(Player player, SkillType skill) {
        super(player, skill);
        this.levelsLost = 1;
    }

    public McMMOPlayerLevelDownEvent(Player player, SkillType skill, int levelsLost) {
        super(player, skill);
        this.levelsLost = levelsLost;
    }

    /**
     * @param levelsLost Set the number of levels lost in this event
     */
    public void setLevelsLost(int levelsLost) {
        this.levelsLost = levelsLost;
    }

    /**
     * @return The number of levels lost in this event
     */
    public int getLevelsLost() {
        return levelsLost;
    }
}
