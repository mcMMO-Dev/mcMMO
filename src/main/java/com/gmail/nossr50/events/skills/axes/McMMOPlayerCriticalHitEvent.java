package com.gmail.nossr50.events.skills.axes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class McMMOPlayerCriticalHitEvent extends McMMOPlayerAxeCombatEvent {
    public McMMOPlayerCriticalHitEvent(Player player, Entity damagee, double damage) {
        super(player, damagee, DamageCause.ENTITY_ATTACK, damage);
    }
}
