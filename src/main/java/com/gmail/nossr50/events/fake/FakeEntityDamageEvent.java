package com.gmail.nossr50.events.fake;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Called when mcMMO applies damage due to special abilities.
 */
public class FakeEntityDamageEvent extends EntityDamageEvent implements FakeEvent {
    @Deprecated
    public FakeEntityDamageEvent(Entity damagee, DamageCause cause, double damage) {
        super(damagee, cause, damage);
    }
}
