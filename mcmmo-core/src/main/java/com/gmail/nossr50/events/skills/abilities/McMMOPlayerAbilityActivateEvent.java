package com.gmail.nossr50.events.skills.abilities;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class McMMOPlayerAbilityActivateEvent extends McMMOPlayerAbilityEvent implements Cancellable {
    private boolean cancelled;

    public McMMOPlayerAbilityActivateEvent(Player player, PrimarySkillType skill, SuperAbilityType superAbilityType) {
        super(player, skill, superAbilityType);
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean value) {
        this.cancelled = value;
    }
}
