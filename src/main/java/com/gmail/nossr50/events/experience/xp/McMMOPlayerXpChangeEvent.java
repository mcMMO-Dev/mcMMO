package com.gmail.nossr50.events.experience.xp;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerExperienceEvent;

public abstract class McMMOPlayerXpChangeEvent extends McMMOPlayerExperienceEvent {
    public McMMOPlayerXpChangeEvent(Player player, SkillType skill) {
        super(player, skill);
    }
}
