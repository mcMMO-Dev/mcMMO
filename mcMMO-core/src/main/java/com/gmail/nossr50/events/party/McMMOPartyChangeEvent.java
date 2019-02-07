package com.gmail.nossr50.events.party;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a player attempts to join, leave, or change parties.
 */
public class McMMOPartyChangeEvent extends PlayerEvent implements Cancellable {
    private String oldParty;
    private String newParty;
    private EventReason reason;
    private boolean cancelled;

    public McMMOPartyChangeEvent(Player player, String oldParty, String newParty, EventReason reason) {
        super(player);

        if (newParty != null) {
            newParty = newParty.replace(":", ".");
        }

        this.oldParty = oldParty;
        this.newParty = newParty;
        this.reason = reason;
        this.cancelled = false;
    }

    /**
     * @return The party being left, or null if the player was not in a party
     */
    public String getOldParty() {
        return oldParty;
    }

    /**
     * @return The party being joined, or null if the player is not joining a new party
     */
    public String getNewParty() {
        return newParty;
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
         * Joined a party for the first time.
         */
        JOINED_PARTY,

        /**
         * Left a party and did not join a new one.
         */
        LEFT_PARTY,

        /**
         * Was kicked from a party.
         */
        KICKED_FROM_PARTY,

        /**
         * Left one party to join another.
         */
        CHANGED_PARTIES,

        /**
         * Any reason that doesn't fit elsewhere.
         */
        CUSTOM;
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
