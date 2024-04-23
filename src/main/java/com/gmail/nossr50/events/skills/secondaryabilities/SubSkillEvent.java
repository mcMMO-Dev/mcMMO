package com.gmail.nossr50.events.skills.secondaryabilities;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class SubSkillEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private SubSkillType subSkillType;
    private boolean cancelled = false;
    private double resultModifier = 1.0D;

    /**
     * Only skills using the old system will fire this event
     * @param player target player
     * @param subSkillType target subskill
     */
    public SubSkillEvent(Player player, SubSkillType subSkillType) {
        super(player, mcMMO.p.getSkillTools().getPrimarySkillBySubSkill(subSkillType));
        this.subSkillType = subSkillType;
    }

    /**
     * Only skills using the old system will fire this event
     * @param player target player
     * @param subSkillType target subskill
     * @param resultModifier a value multiplied against the final result of the dice roll, typically between 0-1.0
     */
    public SubSkillEvent(Player player, SubSkillType subSkillType, double resultModifier) {
        super(player, mcMMO.p.getSkillTools().getPrimarySkillBySubSkill(subSkillType));
        this.subSkillType = subSkillType;
        this.resultModifier = resultModifier;
    }

    public SubSkillEvent(Player player, AbstractSubSkill abstractSubSkill)
    {
        super(player, abstractSubSkill.getPrimarySkill());
    }

    public double getResultModifier() {
        return resultModifier;
    }

    public void setResultModifier(double resultModifier) {
        this.resultModifier = resultModifier;
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
