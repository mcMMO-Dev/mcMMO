package com.gmail.nossr50.events.items;

import com.gmail.nossr50.api.ItemSpawnReason;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when mcMMO is preparing to drop an item.
 */
public class McMMOItemSpawnEvent extends Event implements Cancellable {
    private Location location;
    private ItemStack itemStack;
    private boolean cancelled;
    private final ItemSpawnReason itemSpawnReason;
    private final Player player;

    public McMMOItemSpawnEvent(@NotNull Location location, @NotNull ItemStack itemStack,
            @NotNull ItemSpawnReason itemSpawnReason, @Nullable Player player) {
        this.location = location;
        this.itemStack = itemStack;
        this.itemSpawnReason = itemSpawnReason;
        this.player = player;
        this.cancelled = false;
    }

    /**
     * Get the associated player This can be null
     *
     * @return the associated player if one exists null otherwise
     */
    public @Nullable Player getPlayer() {
        return player;
    }

    /**
     * The reason an item is being spawned by mcMMO
     *
     * @return the item drop reason
     * @see ItemSpawnReason
     */
    public ItemSpawnReason getItemSpawnReason() {
        return itemSpawnReason;
    }

    /**
     * @return Location where the item will be dropped
     */
    public @NotNull Location getLocation() {
        return location;
    }

    /**
     * @param location Location where to drop the item
     */
    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    /**
     * @return ItemStack that will be dropped
     */
    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * @param itemStack ItemStack to drop
     */
    public void setItemStack(@NotNull ItemStack itemStack) {
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

    /**
     * Rest of file is required boilerplate for custom events
     **/
    private static final @NotNull HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
