package com.gmail.nossr50.events.experience;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;

/**
 * Called when a user levels change
 */
public abstract class McMMOPlayerLevelChangeEvent extends McMMOPlayerExperienceEvent {
    @Deprecated
    public McMMOPlayerLevelChangeEvent(Player player, SkillType skill) {
        super(player, skill, XPGainReason.UNKNOWN);
    }

    public McMMOPlayerLevelChangeEvent(Player player, SkillType skill, XPGainReason xpGainReason) {
        super(player, skill, xpGainReason);
    }
}
