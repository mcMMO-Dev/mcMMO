package com.gmail.nossr50.events.skills.abilities;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;

public class McMMOPlayerAbilityDeactivateEvent extends McMMOPlayerAbilityEvent {
    public McMMOPlayerAbilityDeactivateEvent(Player player, SkillType skill) {
        super(player, skill);
    }
}
