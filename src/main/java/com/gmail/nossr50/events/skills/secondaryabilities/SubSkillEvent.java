package com.gmail.nossr50.events.skills.secondaryabilities;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class SubSkillEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private SubSkillType subSkillType;
    private boolean cancelled = false;
    private double resultModifier = 1.0D;

    /**
     * Only skills using the old system will fire this event
     * @param player target player
     * @param subSkillType target subskill
     * @deprecated Use {@link #SubSkillEvent(McMMOPlayer, SubSkillType)} instead
     */
    @Deprecated(forRemoval = true, since = "2.2.010")
    public SubSkillEvent(@NotNull Player player, @NotNull SubSkillType subSkillType) {
        this(requireNonNull(UserManager.getPlayer(player)), subSkillType);
    }

    /**
     * Only skills using the old system will fire this event
     * @param mmoPlayer target player
     * @param subSkillType target subskill
     */
    public SubSkillEvent(@NotNull McMMOPlayer mmoPlayer, @NotNull SubSkillType subSkillType) {
        super(mmoPlayer, mcMMO.p.getSkillTools().getPrimarySkillBySubSkill(subSkillType));
        this.subSkillType = subSkillType;
    }

    /**
     * Only skills using the old system will fire this event
     * @param player target player
     * @param subSkillType target subskill
     * @param resultModifier a value multiplied against the final result of the dice roll, typically between 0-1.0
     */
    @Deprecated(forRemoval = true, since = "2.2.010")
    public SubSkillEvent(@NotNull Player player, @NotNull SubSkillType subSkillType, double resultModifier) {
        this(requireNonNull(UserManager.getPlayer(player)), subSkillType, resultModifier);
    }

    /**
     * Only skills using the old system will fire this event
     * @param player target player
     * @param subSkillType target subskill
     * @param resultModifier a value multiplied against the final result of the dice roll, typically between 0-1.0
     */
    public SubSkillEvent(@NotNull McMMOPlayer player, @NotNull SubSkillType subSkillType, double resultModifier) {
        super(player, mcMMO.p.getSkillTools().getPrimarySkillBySubSkill(subSkillType));
        this.subSkillType = requireNonNull(subSkillType, "subSkillType cannot be null");
        this.resultModifier = resultModifier;
    }

    @Deprecated(forRemoval = true, since = "2.2.010")
    public SubSkillEvent(@NotNull Player player, @NotNull AbstractSubSkill abstractSubSkill) {
        this(requireNonNull(UserManager.getPlayer(player)), abstractSubSkill);
    }

    public SubSkillEvent(@NotNull McMMOPlayer mmoPlayer, @NotNull AbstractSubSkill abstractSubSkill) {
        super(mmoPlayer, abstractSubSkill.getPrimarySkill());
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
