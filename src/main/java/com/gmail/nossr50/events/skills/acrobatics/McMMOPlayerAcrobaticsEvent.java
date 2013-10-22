package com.gmail.nossr50.events.skills.acrobatics;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public abstract class McMMOPlayerAcrobaticsEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private boolean cancelled;

    protected McMMOPlayerAcrobaticsEvent(Player player) {
        super(player, SkillType.ACROBATICS);
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
