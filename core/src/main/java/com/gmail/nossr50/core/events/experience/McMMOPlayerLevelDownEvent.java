package com.gmail.nossr50.core.events.experience;

import com.gmail.nossr50.core.datatypes.experience.XPGainReason;
import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.skills.PrimarySkillType;

/**
 * Called when a user loses levels in a skill
 */
public class McMMOPlayerLevelDownEvent extends McMMOPlayerLevelChangeEvent {
    private static final HandlerList handlers = new HandlerList();
    private int levelsLost;

    @Deprecated
    public McMMOPlayerLevelDownEvent(Player player, PrimarySkillType skill) {
        super(player, skill, XPGainReason.UNKNOWN);
        this.levelsLost = 1;
    }

    @Deprecated
    public McMMOPlayerLevelDownEvent(Player player, PrimarySkillType skill, int levelsLost) {
        super(player, skill, XPGainReason.UNKNOWN);
        this.levelsLost = levelsLost;
    }

    public McMMOPlayerLevelDownEvent(Player player, PrimarySkillType skill, XPGainReason xpGainReason) {
        super(player, skill, xpGainReason);
        this.levelsLost = 1;
    }

    public McMMOPlayerLevelDownEvent(Player player, PrimarySkillType skill, int levelsLost, XPGainReason xpGainReason) {
        super(player, skill, xpGainReason);
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
