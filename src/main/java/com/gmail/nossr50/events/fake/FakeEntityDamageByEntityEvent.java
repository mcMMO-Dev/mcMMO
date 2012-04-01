package com.gmail.nossr50.events.fake;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Called when mcMMO applies damage from an entity due to special abilities.
 */
public class FakeEntityDamageByEntityEvent extends EntityDamageByEntityEvent {

    public FakeEntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, int damage) {
        super(damager, damagee, cause, damage);
    }
}
