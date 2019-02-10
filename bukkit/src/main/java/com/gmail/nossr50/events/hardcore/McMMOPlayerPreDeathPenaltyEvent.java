package com.gmail.nossr50.events.hardcore;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class McMMOPlayerPreDeathPenaltyEvent extends PlayerEvent implements Cancellable {
    /**
     * Rest of file is required boilerplate for custom events
     **/
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public McMMOPlayerPreDeathPenaltyEvent(Player player) {
        super(player);
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
