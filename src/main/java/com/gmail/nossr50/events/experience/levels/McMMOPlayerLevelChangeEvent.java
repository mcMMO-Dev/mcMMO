package com.gmail.nossr50.events.experience.levels;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerExperienceEvent;

/**
 * Called when a user levels change
 */
public abstract class McMMOPlayerLevelChangeEvent extends McMMOPlayerExperienceEvent {
    public McMMOPlayerLevelChangeEvent(Player player, SkillType skill) {
        super(player, skill);
    }
}
