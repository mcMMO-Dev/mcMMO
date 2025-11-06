package com.gmail.nossr50.events.skills.woodcutting;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TreeFellerDestroyTreeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Set<Block> blocks;
    private boolean cancelled;

    public TreeFellerDestroyTreeEvent(@NotNull Player player, @NotNull Set<Block> blocks) {
        this.player = player;
        this.blocks = blocks;
        this.cancelled = false;
    }

    /**
     * The players involved in this event
     * @return The player who is felling the tree
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * The blocks involved in this event
     * @return The blocks being destroyed by tree felling
     */
    @NotNull
    public Set<Block> getBlocks() {
        return blocks;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
