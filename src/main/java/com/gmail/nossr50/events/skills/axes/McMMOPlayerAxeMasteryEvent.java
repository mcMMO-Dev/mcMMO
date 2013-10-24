package com.gmail.nossr50.events.skills.axes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class McMMOPlayerAxeMasteryEvent extends McMMOPlayerAxeCombatEvent {
    public McMMOPlayerAxeMasteryEvent(Player player, Entity damagee, double damage) {
        super(player, damagee, DamageCause.ENTITY_ATTACK, damage);
        // TODO Auto-generated constructor stub
    }

}
