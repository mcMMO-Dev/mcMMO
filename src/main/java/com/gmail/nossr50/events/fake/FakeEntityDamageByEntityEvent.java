package com.gmail.nossr50.events.fake;

import com.google.common.base.Function;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

/**
 * Called when mcMMO applies damage from an entity due to special abilities.
 */
public class FakeEntityDamageByEntityEvent extends EntityDamageByEntityEvent implements FakeEvent {

    public FakeEntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity damagee, @NotNull DamageCause cause, @NotNull final Map<DamageModifier, Double> modifiers) {
        super(damager, damagee, cause, modifiers, getFunctionModifiers(modifiers));
    }

    @Deprecated
    public FakeEntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity damagee, @NotNull DamageCause cause, double damage) {
        super(damager, damagee, cause, damage);
    }

    @NotNull
    public static EnumMap<DamageModifier, Function<? super Double, Double>> getFunctionModifiers(@NotNull Map<DamageModifier, Double> modifiers) {
        EnumMap<DamageModifier, Function<? super Double, Double>> modifierFunctions = new EnumMap<>(DamageModifier.class);

        for (DamageModifier modifier : modifiers.keySet()) {
            modifierFunctions.put(modifier, (o -> -0.0));
        }

        return modifierFunctions;
    }
}
