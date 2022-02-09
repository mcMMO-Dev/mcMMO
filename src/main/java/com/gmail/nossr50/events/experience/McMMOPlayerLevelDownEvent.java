package com.gmail.nossr50.events.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a user loses levels in a skill
 */
public class McMMOPlayerLevelDownEvent extends McMMOPlayerLevelChangeEvent {
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

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
