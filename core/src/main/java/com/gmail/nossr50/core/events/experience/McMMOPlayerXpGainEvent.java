package com.gmail.nossr50.core.events.experience;

import com.gmail.nossr50.core.datatypes.experience.XPGainReason;
import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.skills.PrimarySkillType;

/**
 * Called when a player gains XP in a skill
 */
public class McMMOPlayerXpGainEvent extends McMMOPlayerExperienceEvent {
    private static final HandlerList handlers = new HandlerList();
    private float xpGained;

    @Deprecated
    public McMMOPlayerXpGainEvent(Player player, PrimarySkillType skill, float xpGained) {
        super(player, skill, XPGainReason.UNKNOWN);
        this.xpGained = xpGained;
    }

    public McMMOPlayerXpGainEvent(Player player, PrimarySkillType skill, float xpGained, XPGainReason xpGainReason) {
        super(player, skill, xpGainReason);
        this.xpGained = xpGained;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return The amount of experience gained in this event
     */
    public float getRawXpGained() {
        return xpGained;
    }

    /**
     * @param xpGained int amount of experience gained in this event
     */
    public void setRawXpGained(float xpGained) {
        this.xpGained = xpGained;
    }

    /**
     * @return int amount of experience gained in this event
     */
    @Deprecated
    public int getXpGained() {
        return (int) xpGained;
    }

    /**
     * @param xpGained int amount of experience gained in this event
     */
    @Deprecated
    public void setXpGained(int xpGained) {
        this.xpGained = xpGained;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
