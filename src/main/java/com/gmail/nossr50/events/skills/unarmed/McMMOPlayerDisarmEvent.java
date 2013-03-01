package com.gmail.nossr50.events.skills.unarmed;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerDisarmEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private boolean cancelled;
    private Player defender;

    public McMMOPlayerDisarmEvent(Player defender) {
        super(defender, SkillType.UNARMED);
        this.defender = defender;
    }

    public Player getDefender() {
        return defender;
    }

    /** Following are required for Cancellable **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
