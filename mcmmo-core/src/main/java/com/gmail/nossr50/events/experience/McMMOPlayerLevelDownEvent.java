package com.gmail.nossr50.events.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a user loses levels in a skill
 */
public class McMMOPlayerLevelDownEvent extends McMMOPlayerLevelChangeEvent {
    private static final HandlerList handlers = new HandlerList();
    private int levelsLost;

    @Deprecated
    public McMMOPlayerLevelDownEvent(Player player, PrimarySkillType skill, int skillLevel) {
        super(player, skill, skillLevel, XPGainReason.UNKNOWN);
        this.levelsLost = 1;
    }

    @Deprecated
    public McMMOPlayerLevelDownEvent(Player player, PrimarySkillType skill, int levelsLost, int skillLevel) {
        super(player, skill, skillLevel, XPGainReason.UNKNOWN);
        this.levelsLost = levelsLost;
    }

    public McMMOPlayerLevelDownEvent(Player player, PrimarySkillType skill, int skillLevel, XPGainReason xpGainReason) {
        super(player, skill, skillLevel, xpGainReason);
        this.levelsLost = 1;
    }

    public McMMOPlayerLevelDownEvent(Player player, PrimarySkillType skill, int levelsLost, int skillLevel, XPGainReason xpGainReason) {
        super(player, skill, skillLevel, xpGainReason);
        this.levelsLost = levelsLost;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return The number of levels lost in this event
     */
    public int getLevelsLost() {
        return levelsLost;
    }

    /**
     * @param levelsLost Set the number of levels lost in this event
     */
    public void setLevelsLost(int levelsLost) {
        this.levelsLost = levelsLost;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
