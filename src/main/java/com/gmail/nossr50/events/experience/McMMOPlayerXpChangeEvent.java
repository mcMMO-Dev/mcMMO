package com.gmail.nossr50.events.experience;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;

public abstract class McMMOPlayerXpChangeEvent extends McMMOPlayerExperienceEvent {
    public McMMOPlayerXpChangeEvent(Player player, SkillType skill) {
        super(player, skill);
    }
}
