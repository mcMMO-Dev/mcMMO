package com.gmail.nossr50.events.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Generic event for mcMMO experience events.
 */
public abstract class McMMOPlayerExperienceEvent extends PlayerEvent implements Cancellable {
    private boolean cancelled;
    protected PrimarySkillType skill;
    protected int skillLevel;
    protected XPGainReason xpGainReason;

    @Deprecated
    protected McMMOPlayerExperienceEvent(Player player, PrimarySkillType skill) {
        super(player);
        this.skill = skill;
        this.skillLevel = UserManager.getPlayer(player).getSkillLevel(skill);
        this.xpGainReason = XPGainReason.UNKNOWN;
    }

    protected McMMOPlayerExperienceEvent(Player player, PrimarySkillType skill, XPGainReason xpGainReason) {
        super(player);
        this.skill = skill;

        if (UserManager.getPlayer(player) != null) {
            this.skillLevel = UserManager.getPlayer(player).getSkillLevel(skill);
        } else {
            this.skillLevel = 0;
        }

        this.xpGainReason = xpGainReason;
    }

    /**
     * @return The skill involved in this event
     */
    public PrimarySkillType getSkill() {
        return skill;
    }

    /**
     * @return The skill level of the skill involved in this event
     */
    public int getSkillLevel() {
        return skillLevel;
    }

    /**
     * @return The combat type involved in this event
     */
    public XPGainReason getXpGainReason() {
        return xpGainReason;
    }

    /** Following are required for Cancellable **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
