package com.gmail.nossr50.events.skills.archery;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class McMMOPlayerSkillShotEvent extends McMMOPlayerArcheryCombatEvent {
    public McMMOPlayerSkillShotEvent(Player player, Entity damager, Entity damagee, double damage) {
        super(player, damager, damagee, DamageCause.PROJECTILE, damage);
    }
}
