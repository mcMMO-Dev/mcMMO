package com.gmail.nossr50.events.items;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Called when mcMMO is preparing to drop an item.
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
