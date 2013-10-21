package com.gmail.nossr50.events.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;

public abstract class McMMOPlayerAbilityEvent extends PlayerEvent {
    private AbilityType ability;

    @Deprecated
    protected McMMOPlayerAbilityEvent(Player player, SkillType skill) {
        super(player);
        ability = skill.getAbility();
    }

    protected McMMOPlayerAbilityEvent(Player player, AbilityType ability) {
        super(player);
        this.ability = ability;
    }

    public AbilityType getAbility() {
        return ability;
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
