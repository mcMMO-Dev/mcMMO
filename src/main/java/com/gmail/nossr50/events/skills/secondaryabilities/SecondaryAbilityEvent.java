package com.gmail.nossr50.events.skills.secondaryabilities;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public abstract class SecondaryAbilityEvent extends McMMOPlayerSkillEvent {

    private SecondaryAbility secondaryAbility;

    public SecondaryAbilityEvent(Player player, SecondaryAbility secondaryAbility) {
        super(player, SkillType.bySecondaryAbility(secondaryAbility));
        this.secondaryAbility = secondaryAbility;
    }

    /**
     * Gets the SecondaryAbility involved in the event
     * @return the SecondaryAbility
     */
    public SecondaryAbility getSecondaryAbility() {
        return secondaryAbility;
    }
}
