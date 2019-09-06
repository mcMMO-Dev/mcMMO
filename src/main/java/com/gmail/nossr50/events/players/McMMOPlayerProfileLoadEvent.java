package com.gmail.nossr50.events.players;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class McMMOPlayerProfileLoadEvent extends PlayerEvent implements Cancellable {
    private boolean cancelled;
    private PlayerProfile profile;
    private Player player;
    public McMMOPlayerProfileLoadEvent(Player player, PlayerProfile profile){
        super(player);

        this.cancelled = false;
        this.profile = profile;
        this.player = player;
    }
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public PlayerProfile getProfile(){return this.profile;}
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
