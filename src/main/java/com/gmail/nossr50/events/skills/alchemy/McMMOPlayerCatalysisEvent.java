package com.gmail.nossr50.events.skills.alchemy;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class McMMOPlayerCatalysisEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private double speed;

    private boolean cancelled;

    public McMMOPlayerCatalysisEvent(Player player, double speed) {
        super(player, PrimarySkillType.ALCHEMY);
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
