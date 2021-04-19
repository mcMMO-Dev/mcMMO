package com.gmail.nossr50.events.skills.rupture;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class McMMOEntityDamageByRupture extends FakeEntityDamageByEntityEvent {
	public McMMOEntityDamageByRupture(@NotNull Player damager, @NotNull Entity damagee, @NotNull DamageCause cause, double damage) {
		super(damager, damagee, cause, getDamageMap(damage));
	}

	public McMMOEntityDamageByRupture(@NotNull McMMOPlayer damager, @NotNull Entity damagee, @NotNull DamageCause cause, double damage) {
		this(damager.getPlayer(), damagee, cause, damage);
	}

	private static Map<EntityDamageEvent.DamageModifier, Double> getDamageMap(double damage) {
		Map<EntityDamageEvent.DamageModifier, Double> damageMap = new HashMap<>();
		damageMap.put(EntityDamageEvent.DamageModifier.BASE, damage);
		return damageMap;
	}

	@NotNull
	@Override
	public Player getDamager() {
		return (Player) super.getDamager();
	}
}
