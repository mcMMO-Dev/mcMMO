package com.gmail.nossr50.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.nossr50.datatypes.SkillType;

/**
 * Called when a user levels up in a skill
 */
public class McMMOPlayerLevelUpEvent extends Event {
	private Player player;
	private SkillType skill;
	private int levelsGained;
	
	public McMMOPlayerLevelUpEvent(Player player, SkillType skill) {
		this.player = player;
		this.skill = skill;
		this.levelsGained = 1;	// Always 1 for now as we call in the loop where the levelups are calculated, could change later!
	}
	
	/**
	 * @return Player leveling up
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @return SkillType that is being leveled up
	 */
	public SkillType getSkill() {
		return skill;
	}
	
	/**
	 * @return The number of levels gained in this event
	 */
	public int getLevelsGained() {
		return levelsGained;
	}
	
	/** Rest of file is required boilerplate for custom events **/
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
