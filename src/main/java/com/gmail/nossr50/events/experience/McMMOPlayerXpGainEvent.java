package com.gmail.nossr50.events.experience;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;

/**
 * Called when a player gains XP in a skill
 */
public class McMMOPlayerXpGainEvent extends McMMOPlayerExperienceEvent {
    private int xpGained;

    public McMMOPlayerXpGainEvent(Player player, SkillType skill, int xpGained) {
        super(player, skill);
        this.xpGained = xpGained;
    }

    /**
     * @return The amount of experience gained in this event
     */
    public int getXpGained() {
        return xpGained;
    }

    /**
     * @param xpGained int amount of experience gained in this event
     */
    public void setXpGained(int xpGained) {
        this.xpGained = xpGained;
    }
}
