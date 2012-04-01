package com.gmail.nossr50.events.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.gmail.nossr50.datatypes.SkillType;

/**
 * Generic event for mcMMO skill handling.
 */
public class McMMOPlayerSkillEvent extends PlayerEvent {
    protected SkillType skill;
    protected int skillLevel;

    public McMMOPlayerSkillEvent(Player player, SkillType skill) {
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
