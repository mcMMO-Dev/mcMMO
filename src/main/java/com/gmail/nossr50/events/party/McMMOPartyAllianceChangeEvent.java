package com.gmail.nossr50.events.party;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class McMMOPartyAllianceChangeEvent extends PlayerEvent implements Cancellable {
    private final String oldAlly;
    private final String newAlly;
    private final EventReason reason;
    private boolean cancelled;

    public McMMOPartyAllianceChangeEvent(Player player, String oldAlly, String newAlly,
            EventReason reason) {
        super(player);

        if (newAlly != null) {
            newAlly = newAlly.replace(":", ".");
        }

        this.oldAlly = oldAlly;
        this.newAlly = newAlly;
        this.reason = reason;
        this.cancelled = false;
    }

    /**
     * @return The party being left, or null if the player was not in a party
     */
    public String getOldAlly() {
        return oldAlly;
    }

    /**
     * @return The party being joined, or null if the player is not joining a new party
     */
    public String getNewAlly() {
        return newAlly;
    }

    /**
     * @return The reason for the event being fired
     */
    public EventReason getReason() {
        return reason;
    }

    /**
     * A list of reasons why the event may have been fired
     */
    public enum EventReason {
        /**
         * Formed an alliance for the first time.
         */
        FORMED_ALLIANCE,

        /**
         * Left a party and did not join a new one.
         */
        DISBAND_ALLIANCE,

        /**
         * Any reason that doesn't fit elsewhere.
         */
        CUSTOM
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
    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
