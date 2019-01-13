package com.gmail.nossr50.events.skills.fishing;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerFishingEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private boolean cancelled;

    protected McMMOPlayerFishingEvent(Player player) {
        super(player, PrimarySkillType.FISHING);
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }
}
