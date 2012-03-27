package com.gmail.nossr50.events.fake;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public class FakeEntityDamageEvent extends EntityDamageEvent{
    public FakeEntityDamageEvent(Entity damagee, DamageCause cause, int damage) {
        super(damagee, cause, damage);
    }
}