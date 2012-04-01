package com.gmail.nossr50.events.experience;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.SkillType;

/**
 * Called when a user levels up in a skill
 */
public class McMMOPlayerLevelUpEvent extends McMMOPlayerExperienceEvent {
    private int levelsGained;

    public McMMOPlayerLevelUpEvent(Player player, SkillType skill) {
        super(player, skill);
        this.levelsGained = 1;    // Always 1 for now as we call in the loop where the levelups are calculated, could change later!
    }

    /**
     * @return The number of levels gained in this event
     */
    public int getLevelsGained() {
        return levelsGained;
    }
}
