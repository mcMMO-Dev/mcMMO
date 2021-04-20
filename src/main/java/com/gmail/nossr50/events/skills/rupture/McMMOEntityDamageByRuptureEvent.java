package com.gmail.nossr50.events.skills.rupture;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

public class McMMOEntityDamageByRuptureEvent extends EntityDamageByEntityEvent {
	private final McMMOPlayer mcMMODamager;

	public McMMOEntityDamageByRuptureEvent(@NotNull McMMOPlayer damager, @NotNull Entity damagee, double damage) {
		super(damager.getPlayer(), damagee, DamageCause.CUSTOM, new EnumMap<>(ImmutableMap.of(DamageModifier.BASE, damage)), new EnumMap<>(ImmutableMap.of(DamageModifier.BASE, (o -> -0.0))));
		this.mcMMODamager = damager;
	}

	@NotNull
	public McMMOPlayer getMcMMODamager() {
		return mcMMODamager;
	}
}
