package com.gmail.nossr50.events.skills.fishing;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerFishingEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private boolean cancelled;

    protected McMMOPlayerFishingEvent(Player player) {
        super(player, SkillType.fishing);
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }
}
