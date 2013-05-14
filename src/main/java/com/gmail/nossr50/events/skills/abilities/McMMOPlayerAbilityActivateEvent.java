package com.gmail.nossr50.events.skills.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerAbilityActivateEvent extends McMMOPlayerSkillEvent implements Cancellable {

    private AbilityType abilityType;
    private boolean cancelled;

    public McMMOPlayerAbilityActivateEvent(Player player, SkillType skill) {
        super(player, skill);
        abilityType = skill.getAbility();
        cancelled = false;
    }

    /**
     * @return The ability type involved in this event
     */
    public AbilityType getAbilityType() {
        return abilityType;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean value) {
        this.cancelled = value;
    }
}
