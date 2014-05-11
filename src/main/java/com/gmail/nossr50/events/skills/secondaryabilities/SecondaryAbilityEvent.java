package com.gmail.nossr50.events.skills.secondaryabilities;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class SecondaryAbilityEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private SecondaryAbility secondaryAbility;
    private boolean cancelled;

    public SecondaryAbilityEvent(Player player, SecondaryAbility secondaryAbility) {
        super(player, SkillType.bySecondaryAbility(secondaryAbility));
        this.secondaryAbility = secondaryAbility;
        cancelled = false;
    }

    /**
     * Gets the SecondaryAbility involved in the event
     * @return the SecondaryAbility
     */
    public SecondaryAbility getSecondaryAbility() {
        return secondaryAbility;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean newValue) {
        this.cancelled = newValue;
    }
}
