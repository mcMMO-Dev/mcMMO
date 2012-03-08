package com.gmail.nossr50.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.nossr50.datatypes.SkillType;

public class McMMOPlayerXpGainEvent extends Event {
	private Player player;
	private SkillType skill;
	private int xpGained;
	
	public McMMOPlayerXpGainEvent(Player player, SkillType skill, int xpGained) {
		this.player = player;
		this.skill = skill;
		this.xpGained = xpGained;
	}
	
	/**
	 * @return Player gaining experience (can be null)
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @return SkillType that is gaining experience
	 */
	public SkillType getSkill() {
		return skill;
	}
	
	/**
	 * @return The number experience gained in this event
	 */
	public int getXpGained() {
		return xpGained;
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
