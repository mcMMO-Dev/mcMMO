package com.gmail.nossr50.core.events.experience;

import com.gmail.nossr50.core.datatypes.experience.XPGainReason;
import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.skills.PrimarySkillType;

/**
 * Called when a user levels change
 */
public abstract class McMMOPlayerLevelChangeEvent extends McMMOPlayerExperienceEvent {
    @Deprecated
    public McMMOPlayerLevelChangeEvent(Player player, PrimarySkillType skill) {
        super(player, skill, XPGainReason.UNKNOWN);
    }

    public McMMOPlayerLevelChangeEvent(Player player, PrimarySkillType skill, XPGainReason xpGainReason) {
        super(player, skill, xpGainReason);
    }
}
