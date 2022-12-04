package com.gmail.nossr50.events.fake;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when mcMMO applies damage from an entity due to special abilities.
 */
public class FakeEntityDamageByEntityEvent extends EntityDamageByEntityEvent implements FakeEvent {
    public FakeEntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity damagee, @NotNull DamageCause cause, double damage) {
        super(damager, damagee, cause, damage);
    }
}
