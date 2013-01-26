package com.gmail.nossr50.events.experience;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.gmail.nossr50.skills.SkillType;

/**
 * Generic event for mcMMO experience events.
 */
public abstract class McMMOPlayerExperienceEvent extends PlayerEvent {
    protected SkillType skill;
    protected int skillLevel;

    protected McMMOPlayerExperienceEvent(Player player, SkillType skill) {
        super(player);
        this.skill = skill;
        this.skillLevel = skill.getSkillLevel(player);
    }

    /**
     * @return The skill involved in this event
     */
    public SkillType getSkill() {
        return skill;
    }

    /**
     * @return The skill level of the skill involved in this event
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
