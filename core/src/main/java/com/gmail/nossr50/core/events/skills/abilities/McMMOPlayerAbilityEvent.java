package com.gmail.nossr50.core.events.skills.abilities;

import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.skills.SuperAbilityType;
import com.gmail.nossr50.core.events.skills.McMMOPlayerSkillEvent;
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
