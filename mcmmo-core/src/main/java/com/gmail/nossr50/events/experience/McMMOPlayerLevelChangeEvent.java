package com.gmail.nossr50.events.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

/**
 * Called when a user levels change
 */
public abstract class McMMOPlayerLevelChangeEvent extends McMMOPlayerExperienceEvent {
    @Deprecated
    public McMMOPlayerLevelChangeEvent(Player player, PrimarySkillType skill, int skillLevel) {
        super(player, skill, skillLevel, XPGainReason.UNKNOWN);
    }

    public McMMOPlayerLevelChangeEvent(Player player, PrimarySkillType skill, int skillLevel, XPGainReason xpGainReason) {
        super(player, skill, skillLevel, xpGainReason);
    }
}
