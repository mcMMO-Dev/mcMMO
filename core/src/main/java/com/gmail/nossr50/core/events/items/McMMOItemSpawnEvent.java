package com.gmail.nossr50.core.events.items;

import com.gmail.nossr50.core.mcmmo.item.ItemStack;

/**
 * Called when mcMMO is preparing to drop an item.
 */
public class McMMOItemSpawnEvent extends Event implements Cancellable {
    /**
     * Rest of file is required boilerplate for custom events
     **/
    private static final HandlerList handlers = new HandlerList();
    private Location location;
    private ItemStack itemStack;
    private boolean cancelled;

    public McMMOItemSpawnEvent(Location location, ItemStack itemStack) {
        this.location = location;
        this.itemStack = itemStack;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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

    /**
     * Following are required for Cancellable
     **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
