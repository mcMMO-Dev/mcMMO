package com.gmail.nossr50.events.skills.secondaryabilities;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class SubSkillEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private final @NotNull SubSkillType subSkillType;
    private boolean cancelled = false;
    private double resultModifier = 1.0D;

    /**
     * @param player target player
     * @param subSkillType target subskill
     */
    public SubSkillEvent(@NotNull Player player, @NotNull SubSkillType subSkillType) {
        super(player, subSkillType.getParentSkill());
        this.subSkillType = subSkillType;
    }

    /**
     * @param player target player
     * @param subSkillType target subskill
     * @param resultModifier a value multiplied against the probability (1.5 would increase probability by 50%)
     */
    public SubSkillEvent(@NotNull Player player, @NotNull SubSkillType subSkillType, double resultModifier) {
        super(player, subSkillType.getParentSkill());
        this.subSkillType = subSkillType;
        this.resultModifier = resultModifier;
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
    public @NotNull SubSkillType getSubSkillType() {
        return subSkillType;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }
}
