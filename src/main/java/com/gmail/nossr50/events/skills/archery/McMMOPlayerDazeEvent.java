package com.gmail.nossr50.events.skills.archery;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.skills.archery.Archery;

public class McMMOPlayerDazeEvent extends McMMOPlayerArcheryCombatEvent {
    public McMMOPlayerDazeEvent(Player player, Entity damager, Entity damagee) {
        super(player, damager, damagee, DamageCause.PROJECTILE, Archery.dazeModifier);
    }
}
