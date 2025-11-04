package com.gmail.nossr50.events.skills.alchemy;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class McMMOPlayerCatalysisEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private double speed;

    private boolean cancelled;

    @Deprecated(forRemoval = true, since = "2.2.010")
    public McMMOPlayerCatalysisEvent(Player player, double speed) {
        super(requireNonNull(UserManager.getPlayer(player)), PrimarySkillType.ALCHEMY);
        this.speed = speed;
        cancelled = false;
    }

    public McMMOPlayerCatalysisEvent(McMMOPlayer mmoPlayer, double speed) {
        super(mmoPlayer, PrimarySkillType.ALCHEMY);
        this.speed = speed;
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
