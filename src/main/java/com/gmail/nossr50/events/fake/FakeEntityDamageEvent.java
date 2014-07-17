package com.gmail.nossr50.events.fake;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Called when mcMMO applies damage due to special abilities.
 */
public class FakeEntityDamageEvent extends EntityDamageEvent {
    private static final EnumMap<DamageModifier, Function<? super Double, Double>> functionModifiers = getFunctionModifiers();

    public FakeEntityDamageEvent(Entity damagee, DamageCause cause, final Map<DamageModifier, Double> modifiers) {
        super(damagee, cause, modifiers, functionModifiers);
    }

    @Deprecated
    public FakeEntityDamageEvent(Entity damagee, DamageCause cause, double damage) {
        super(damagee, cause, damage);
    }

    public static EnumMap<DamageModifier, Function<? super Double, Double>> getFunctionModifiers() {
        EnumMap<DamageModifier, Function<? super Double, Double>> modifierFunctions = new EnumMap<DamageModifier, Function<? super Double, Double>>(DamageModifier.class);
        Function<? super Double, Double> ZERO = Functions.constant(-0.0);

        for (DamageModifier modifier : DamageModifier.values()) {
            modifierFunctions.put(modifier, ZERO);
        }

        return modifierFunctions;
    }
}
