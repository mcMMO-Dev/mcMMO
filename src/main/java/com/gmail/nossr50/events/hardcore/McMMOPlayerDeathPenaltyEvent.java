package com.gmail.nossr50.events.hardcore;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.gmail.nossr50.datatypes.skills.SkillType;

public class McMMOPlayerDeathPenaltyEvent extends PlayerEvent implements Cancellable {
    private SkillType skill;
    private boolean cancelled;

    @Deprecated
    public McMMOPlayerDeathPenaltyEvent(Player player) {
        super(player);
    }

    public McMMOPlayerDeathPenaltyEvent(Player player, SkillType skill) {
        super(player);
        this.skill = skill;
        this.cancelled = false;
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

    /** Rest of file is required boilerplate for custom events **/
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public SkillType getSkill() {
        return skill;
    }
}
