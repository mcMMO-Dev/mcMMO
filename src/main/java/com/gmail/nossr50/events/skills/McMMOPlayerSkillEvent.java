package com.gmail.nossr50.events.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.gmail.nossr50.util.player.UserManager;

/**
 * Generic event for mcMMO skill handling.
 */
public abstract class McMMOPlayerSkillEvent extends PlayerEvent {
    protected PrimarySkill skill;
    protected int skillLevel;

    protected McMMOPlayerSkillEvent(Player player, PrimarySkill skill) {
        super(player);
        this.skill = skill;
        this.skillLevel = UserManager.getPlayer(player).getSkillLevel(skill);
    }

    /**
     * @return The skill involved in this event
     */
    public PrimarySkill getSkill() {
        return skill;
    }

    /**
     * @return The level of the skill involved in this event
     */
    public int getSkillLevel() {
        return skillLevel;
    }

    /** Rest of file is required boilerplate for custom events **/
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
