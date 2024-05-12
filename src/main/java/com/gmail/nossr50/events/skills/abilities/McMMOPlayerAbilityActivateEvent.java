package com.gmail.nossr50.events.skills.abilities;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.Objects;

public class McMMOPlayerAbilityActivateEvent extends McMMOPlayerAbilityEvent implements Cancellable {
    private boolean cancelled;

    @Deprecated(forRemoval = true, since = "2.2.010")
    public McMMOPlayerAbilityActivateEvent(Player player, PrimarySkillType skill) {
        super(Objects.requireNonNull(UserManager.getPlayer(player)), skill);
        cancelled = false;
    }

    public McMMOPlayerAbilityActivateEvent(McMMOPlayer mmoPlayer, PrimarySkillType skill) {
        super(mmoPlayer, skill);
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean value) {
        this.cancelled = value;
    }
}
