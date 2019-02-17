package com.gmail.nossr50.core.events.skills.alchemy;

import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.events.skills.McMMOPlayerSkillEvent;

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
