package com.gmail.nossr50.events.skills.alchemy;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerCatalysisEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private double speed;

    private boolean cancelled;

    public McMMOPlayerCatalysisEvent(Player player, double speed) {
        super(player, PrimarySkill.ALCHEMY);
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
