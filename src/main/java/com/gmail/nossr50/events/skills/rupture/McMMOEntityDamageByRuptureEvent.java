package com.gmail.nossr50.events.skills.rupture;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class McMMOEntityDamageByRuptureEvent extends EntityEvent implements Cancellable {
    private final McMMOPlayer damager;
    private final Entity damagee;
    private double damage;
    private boolean isCancelled = false;
    private static final HandlerList handlers = new HandlerList();

    public McMMOEntityDamageByRuptureEvent(@NotNull McMMOPlayer damager, @NotNull Entity damagee,
            double damage) {
        super(damagee);
        this.damager = damager;
        this.damagee = damagee;
        this.damage = damage;
    }

    @NotNull
    @Deprecated
    public McMMOPlayer getMcMMODamager() {
        return damager;
    }

    public McMMOPlayer getDamager() {
        return damager;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = Math.max(0, damage);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
