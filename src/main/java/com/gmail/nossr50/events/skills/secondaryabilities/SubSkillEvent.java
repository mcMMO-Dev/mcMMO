package com.gmail.nossr50.events.skills.secondaryabilities;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class SubSkillEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private SubSkillType subSkillType;
    private boolean cancelled = false;

    /**
     * Only skills using the old system will fire this event
     * @param player target player
     * @param subSkillType target subskill
     */
    @Deprecated
    public SubSkillEvent(Player player, SubSkillType subSkillType) {
        super(player, PrimarySkillType.bySecondaryAbility(subSkillType));
        this.subSkillType = subSkillType;
    }

    public SubSkillEvent(Player player, AbstractSubSkill abstractSubSkill)
    {
        super(player, abstractSubSkill.getPrimarySkill());
    }

    /**
     * Gets the SubSkillType involved in the event
     * @return the SubSkillType
     */
    public SubSkillType getSubSkillType() {
        return subSkillType;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }
}
