package com.gmail.nossr50.events.experience;

import com.gmail.nossr50.core.datatypes.experience.XPGainReason;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a user levels up in a skill
 */
public class McMMOPlayerLevelUpEvent extends McMMOPlayerLevelChangeEvent {
    private static final HandlerList handlers = new HandlerList();
    private int levelsGained;

    @Deprecated
    public McMMOPlayerLevelUpEvent(Player player, PrimarySkillType skill) {
        super(player, skill, XPGainReason.UNKNOWN);
        this.levelsGained = 1;
    }

    @Deprecated
    public McMMOPlayerLevelUpEvent(Player player, PrimarySkillType skill, int levelsGained) {
        super(player, skill, XPGainReason.UNKNOWN);
        this.levelsGained = levelsGained;
    }

    public McMMOPlayerLevelUpEvent(Player player, PrimarySkillType skill, XPGainReason xpGainReason) {
        super(player, skill, xpGainReason);
        this.levelsGained = 1;
    }

    public McMMOPlayerLevelUpEvent(Player player, PrimarySkillType skill, int levelsGained, XPGainReason xpGainReason) {
        super(player, skill, xpGainReason);
        this.levelsGained = levelsGained;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return The number of levels gained in this event
     */
    public int getLevelsGained() {
        return levelsGained;
    }

    /**
     * @param levelsGained Set the number of levels gained in this event
     */
    public void setLevelsGained(int levelsGained) {
        this.levelsGained = levelsGained;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
