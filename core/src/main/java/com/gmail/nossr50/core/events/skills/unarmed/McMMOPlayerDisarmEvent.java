package com.gmail.nossr50.core.events.skills.unarmed;

import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerDisarmEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private boolean cancelled;
    private Player defender;

    public McMMOPlayerDisarmEvent(Player defender) {
        super(defender, PrimarySkillType.UNARMED);
        this.defender = defender;
    }

    public Player getDefender() {
        return defender;
    }

    /**
     * Following are required for Cancellable
     **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
