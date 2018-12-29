package com.gmail.nossr50.events.skills.secondaryabilities;

import com.gmail.nossr50.datatypes.skills.SubSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class SubSkillEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private SubSkill subSkill;
    private boolean cancelled;

    public SubSkillEvent(Player player, SubSkill subSkill) {
        super(player, PrimarySkill.bySecondaryAbility(subSkill));
        this.subSkill = subSkill;
        cancelled = false;
    }

    /**
     * Gets the SubSkill involved in the event
     * @return the SubSkill
     */
    public SubSkill getSubSkill() {
        return subSkill;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }
}
