package com.gmail.nossr50.events.party;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class McMMOPartyChangeEvent extends PlayerEvent implements Cancellable{

    protected String oldParty;
    protected String newParty;
    protected EventReason reason;
    protected boolean cancelled;

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

    public String getOldParty() {
        return oldParty;
    }

    public String getNewParty() {
        return newParty;
    }

    public EventReason getReason() {
        return reason;
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

    /** Following are required for Cancellable **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum EventReason{
        JOINED_PARTY,
        LEFT_PARTY,
        KICKED_FROM_PARTY,
        CHANGED_PARTIES;
    }
}
