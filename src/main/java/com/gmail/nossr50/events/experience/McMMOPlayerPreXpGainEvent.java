package com.gmail.nossr50.events.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player gains XP in a skill
 */
public class McMMOPlayerPreXpGainEvent extends McMMOPlayerExperienceEvent {
    private float xpGained;

    @Deprecated
    public McMMOPlayerPreXpGainEvent(Player player, PrimarySkillType skill, float xpGained) {
        super(player, skill, XPGainReason.UNKNOWN);
        this.xpGained = xpGained;
    }

    public McMMOPlayerPreXpGainEvent(Player player, PrimarySkillType skill, float xpGained,
            XPGainReason xpGainReason) {
        super(player, skill, xpGainReason);
        this.xpGained = xpGained;
    }

    /**
     * @return int amount of experience gained in this event
     */
    public int getXpGained() {
        return (int) xpGained;
    }

    /**
     * @param xpGained int amount of experience gained in this event
     */
    public void setXpGained(int xpGained) {
        this.xpGained = xpGained;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
