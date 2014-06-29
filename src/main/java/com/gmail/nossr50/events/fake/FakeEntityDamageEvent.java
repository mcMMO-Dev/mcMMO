package com.gmail.nossr50.events.fake;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Called when mcMMO applies damage due to special abilities.
 */
public class FakeEntityDamageEvent extends EntityDamageEvent {
    public FakeEntityDamageEvent(Entity damagee, DamageCause cause, final Map<DamageModifier, Double> modifiers) {
        super(damagee, cause, modifiers);
    }

    @Deprecated
    public FakeEntityDamageEvent(Entity damagee, DamageCause cause, double damage) {
        super(damagee, cause, damage);
    }
}
