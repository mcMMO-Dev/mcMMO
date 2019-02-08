package com.gmail.nossr50.events.skills.abilities;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import org.bukkit.entity.Player;

public class McMMOPlayerAbilityEvent extends McMMOPlayerSkillEvent {
    private SuperAbilityType ability;

    protected McMMOPlayerAbilityEvent(Player player, PrimarySkillType skill) {
        super(player, skill);
        ability = skill.getAbility();
    }

    public SuperAbilityType getAbility() {
        return ability;
    }
}
