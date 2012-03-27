package com.gmail.nossr50.events.experience;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.SkillType;

public class McMMOPlayerXpGainEvent extends McMMOPlayerExperienceEvent {
	private int xpGained;
	
	public McMMOPlayerXpGainEvent(Player player, SkillType skill, int xpGained) {
	    super(player, skill);
		this.xpGained = xpGained;
	}
	
	/**
	 * @return The number experience gained in this event
	 */
	public int getXpGained() {
		return xpGained;
	}
}
