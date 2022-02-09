package com.gmail.nossr50.events.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a user levels up in a skill
 */
public class McMMOPlayerLevelUpEvent extends McMMOPlayerLevelChangeEvent {
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

    /**
     * @param levelsGained Set the number of levels gained in this event
     */
    public void setLevelsGained(int levelsGained) {
        this.levelsGained = levelsGained;
    }

    /**
     * @return The number of levels gained in this event
     */
    public int getLevelsGained() {
        return levelsGained;
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
