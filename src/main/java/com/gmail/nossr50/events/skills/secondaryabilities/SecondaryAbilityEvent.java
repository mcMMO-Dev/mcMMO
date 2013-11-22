package com.gmail.nossr50.events.skills.secondaryabilities;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SecondaryAbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public abstract class SecondaryAbilityEvent extends McMMOPlayerSkillEvent {

    private SecondaryAbilityType secondaryAbility;

    public SecondaryAbilityEvent(Player player, SecondaryAbilityType secondaryAbility) {
        super(player, SkillType.bySecondaryAbility(secondaryAbility));
        this.secondaryAbility = secondaryAbility;
    }

    public SecondaryAbilityType getSecondarySkillAbility() {
        return secondaryAbility;
    }
}
