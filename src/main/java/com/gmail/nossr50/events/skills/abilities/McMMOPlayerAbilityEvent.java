package com.gmail.nossr50.events.skills.abilities;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SuperAbility;
import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;

public class McMMOPlayerAbilityEvent extends McMMOPlayerSkillEvent {
    private SuperAbility ability;

    protected McMMOPlayerAbilityEvent(Player player, PrimarySkill skill) {
        super(player, skill);
        ability = skill.getAbility();
    }

    public SuperAbility getAbility() {
        return ability;
    }
}
