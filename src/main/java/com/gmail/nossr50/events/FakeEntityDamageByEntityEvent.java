package com.gmail.nossr50.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@SuppressWarnings("serial")
public class FakeEntityDamageByEntityEvent extends EntityDamageByEntityEvent {
	public FakeEntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, int damage) {
		super(damager, damagee, cause, damage);
	}
}
