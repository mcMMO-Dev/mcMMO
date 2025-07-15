package com.gmail.nossr50.events.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SkillActivationPerkEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private int ticks;
    private final int maxTicks;

    public SkillActivationPerkEvent(Player player, int ticks, int maxTicks) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.ticks = ticks;
        this.maxTicks = maxTicks;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public int getMaxTicks() {
        return maxTicks;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
