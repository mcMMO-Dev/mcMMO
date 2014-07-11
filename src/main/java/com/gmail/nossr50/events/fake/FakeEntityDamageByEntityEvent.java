package com.gmail.nossr50.events.fake;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

/**
 * Called when mcMMO applies damage from an entity due to special abilities.
 */
public class FakeEntityDamageByEntityEvent extends EntityDamageByEntityEvent {
    private static final Function<? super Double, Double> ZERO = Functions.constant(-0.0);

    public FakeEntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, final Map<DamageModifier, Double> modifiers) {
        super(damager, damagee, cause, modifiers, new EnumMap<DamageModifier, Function<? super Double, Double>>(ImmutableMap.of(DamageModifier.BASE, ZERO)));
    }

    @Deprecated
    public FakeEntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, double damage) {
        super(damager, damagee, cause, damage);
    }
}
