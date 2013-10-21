package com.gmail.nossr50.events.abilities;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;

public class McMMOPlayerAbilityDeactivateEvent extends McMMOPlayerAbilityEvent {
    @Deprecated
    public McMMOPlayerAbilityDeactivateEvent(Player player, SkillType skill) {
        super(player, skill);
    }

    public McMMOPlayerAbilityDeactivateEvent(Player player, AbilityType ability) {
        super(player, ability);
    }
}
