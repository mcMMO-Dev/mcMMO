package com.gmail.nossr50.events.experience.xp;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;

/**
 * Called when a player gains XP in a skill
 */
public class McMMOPlayerXpGainEvent extends McMMOPlayerXpChangeEvent {
    private float xpGained;

    public McMMOPlayerXpGainEvent(Player player, SkillType skill, float xpGained) {
        super(player, skill);
        this.xpGained = xpGained;
    }

    /**
     * @return The amount of experience gained in this event
     */
    public float getRawXpGained() {
        return xpGained;
    }

    /**
     * @return int amount of experience gained in this event
     */
    @Deprecated
    public int getXpGained() {
        return (int) xpGained;
    }

    /**
     * @param xpGained float amount of experience gained in this event
     */
    public void setRawXpGained(float xpGained) {
        this.xpGained = xpGained;
    }

    /**
     * @param xpGained int amount of experience gained in this event
     */
    @Deprecated
    public void setXpGained(int xpGained) {
        this.xpGained = xpGained;
    }
}
