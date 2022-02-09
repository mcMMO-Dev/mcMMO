package com.gmail.nossr50.events.fake;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.EnumMap;
import java.util.Map;

/**
 * Called when mcMMO applies damage due to special abilities.
 */
public class FakeEntityDamageEvent extends EntityDamageEvent implements FakeEvent {

    public FakeEntityDamageEvent(Entity damagee, DamageCause cause, final Map<DamageModifier, Double> modifiers) {
        super(damagee, cause, modifiers, getFunctionModifiers(modifiers));
    }

    @Deprecated
    public FakeEntityDamageEvent(Entity damagee, DamageCause cause, double damage) {
        super(damagee, cause, damage);
    }

    public static EnumMap<DamageModifier, Function<? super Double, Double>> getFunctionModifiers(Map<DamageModifier, Double> modifiers) {
        EnumMap<DamageModifier, Function<? super Double, Double>> modifierFunctions = new EnumMap<>(DamageModifier.class);
        Function<? super Double, Double> ZERO = Functions.constant(-0.0);

        for (DamageModifier modifier : modifiers.keySet()) {
            modifierFunctions.put(modifier, ZERO);
        }

        return modifierFunctions;
    }
}
