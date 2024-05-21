package com.gmail.nossr50.events.skills.fishing;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class McMMOPlayerFishingEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private boolean cancelled;

    @Deprecated(forRemoval = true, since = "2.2.010")
    protected McMMOPlayerFishingEvent(Player player) {
        super(UserManager.getPlayer(player), PrimarySkillType.FISHING);
        cancelled = false;
    }

    protected McMMOPlayerFishingEvent(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.FISHING);
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }
}
