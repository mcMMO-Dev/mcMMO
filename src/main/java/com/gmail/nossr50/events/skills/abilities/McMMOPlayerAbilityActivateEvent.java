package com.gmail.nossr50.events.skills.abilities;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class McMMOPlayerAbilityActivateEvent extends McMMOPlayerAbilityEvent implements Cancellable {
    private boolean cancelled;

    public McMMOPlayerAbilityActivateEvent(Player player, PrimarySkillType skill) {
        super(player, skill);
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean value) {
        this.cancelled = value;
    }
}
