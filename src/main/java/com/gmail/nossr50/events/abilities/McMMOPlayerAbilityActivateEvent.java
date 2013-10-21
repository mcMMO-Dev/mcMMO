package com.gmail.nossr50.events.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;

public class McMMOPlayerAbilityActivateEvent extends McMMOPlayerAbilityEvent implements Cancellable {
    private boolean cancelled;

    @Deprecated
    public McMMOPlayerAbilityActivateEvent(Player player, SkillType skill) {
        super(player, skill);
        cancelled = false;
    }

    public McMMOPlayerAbilityActivateEvent(Player player, AbilityType ability) {
        super(player, ability);
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean value) {
        this.cancelled = value;
    }
}
