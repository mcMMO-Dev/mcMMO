package com.gmail.nossr50.events.experience;

import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.experience.XPGainReason;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Generic event for mcMMO experience events.
 */
public abstract class McMMOPlayerExperienceEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected PrimarySkillType skill;
    protected int skillLevel;
    protected XPGainReason xpGainReason;
    private boolean cancelled;

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
        this.skillLevel = UserManager.getPlayer(player).getSkillLevel(skill);
        this.xpGainReason = xpGainReason;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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

    /**
     * Following are required for Cancellable
     **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
