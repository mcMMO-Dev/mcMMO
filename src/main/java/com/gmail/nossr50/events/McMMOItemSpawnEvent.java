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

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Called when mcMMO is preparing to drop an item
 */
public class McMMOItemSpawnEvent extends Event implements Cancellable {
	private Location location;
	private ItemStack itemStack;
	private boolean cancelled;
	
	public McMMOItemSpawnEvent(Location location, ItemStack itemStack) {
		this.location = location;
		this.itemStack = itemStack;
		this.cancelled = false;
	}
	
	/**
	 * @return Location where the item will be dropped
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * @param location Location where to drop the item
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	
	/**
	 * @return ItemStack that will be dropped
	 */
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	/**
	 * @param itemStack ItemStack to drop
	 */
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	
	/** Following are required for Cancellable **/
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
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
