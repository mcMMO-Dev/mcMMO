package com.gmail.nossr50.events.fake;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.google.common.base.Function;

/**
 * Called when mcMMO applies damage from an entity due to special abilities.
 */
public class FakeEntityDamageByEntityEvent extends EntityDamageByEntityEvent {
    public FakeEntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, final Map<DamageModifier, Double> modifiers, final Map<DamageModifier, ? extends Function<? super Double, Double>> modifierFunctions) {
        super(damager, damagee, cause, modifiers, modifierFunctions);
    }

    @Deprecated
    public FakeEntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, double damage) {
        super(damager, damagee, cause, damage);
    }
}
