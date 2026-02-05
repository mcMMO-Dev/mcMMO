package com.gmail.nossr50.events.hardcore;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class McMMOPlayerDeathPenaltyEvent extends PlayerEvent implements Cancellable {
    private HashMap<String, Integer> levelChanged;
    private HashMap<String, Float> experienceChanged;

    private boolean cancelled;

    public McMMOPlayerDeathPenaltyEvent(Player player, HashMap<String, Integer> levelChanged,
            HashMap<String, Float> experienceChanged) {
        super(player);
        this.levelChanged = levelChanged;
        this.experienceChanged = experienceChanged;
        this.cancelled = false;
    }

    @Deprecated
    public McMMOPlayerDeathPenaltyEvent(Player player) {
        super(player);
        this.cancelled = false;
    }

    public HashMap<String, Integer> getLevelChanged() {
        return levelChanged;
    }

    public void setLevelChanged(HashMap<String, Integer> levelChanged) {
        this.levelChanged = levelChanged;
    }

    public HashMap<String, Float> getExperienceChanged() {
        return experienceChanged;
    }

    public void setExperienceChanged(HashMap<String, Float> experienceChanged) {
        this.experienceChanged = experienceChanged;
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
