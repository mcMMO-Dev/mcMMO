package com.gmail.nossr50.events.experience;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;

public class McMMOPlayerXpLossEvent extends McMMOPlayerXpChangeEvent {
    private float xpLost;

    public McMMOPlayerXpLossEvent(Player player, SkillType skill, float xpLost) {
        super(player, skill);
        this.xpLost = xpLost;
    }

    /**
     * @return The amount of experience lost in this event
     */
    public float getRawXpLost() {
        return xpLost;
    }

    /**
     * @param xpLost amount of experience lost in this event
     */
    public void setRawXpLost(float xpLost) {
        this.xpLost = xpLost;
    }
}
