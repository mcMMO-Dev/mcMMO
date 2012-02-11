/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.nossr50.datatypes.SkillType;

@SuppressWarnings("serial")
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
