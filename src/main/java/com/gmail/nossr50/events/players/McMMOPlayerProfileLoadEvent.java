package com.gmail.nossr50.events.players;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class McMMOPlayerProfileLoadEvent extends Event implements Cancellable {
    private boolean cancelled;
    private final PlayerProfile profile;
    private final Player player;

    public McMMOPlayerProfileLoadEvent(Player player, PlayerProfile profile) {
        super(!Bukkit.isPrimaryThread());

        this.cancelled = false;
        this.profile = profile;
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public PlayerProfile getProfile() {
        return this.profile;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }
}
