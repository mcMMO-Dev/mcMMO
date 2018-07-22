package com.gmail.nossr50.events;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;

import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called after the mcMMO player profile was successful loaded from data storage. This happens if the player recently
 * joined the server or the data was reloaded (e.g. database convert, reset stats, convert experience)
 * while the player is online.
 */
public class McMMOPlayerLoadedEvent extends PlayerEvent {

    private final McMMOPlayer mcMMOPlayer;

    public McMMOPlayerLoadedEvent(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer.getPlayer());

        this.mcMMOPlayer = mcMMOPlayer;
    }

    /**
     * @return mcMMO player profile associated to the player
     */
    public McMMOPlayer getMcMMOPlayer() {
        return mcMMOPlayer;
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
