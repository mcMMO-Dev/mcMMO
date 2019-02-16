package com.gmail.nossr50.core.events.skills.fishing;

import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.events.skills.McMMOPlayerSkillEvent;

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
