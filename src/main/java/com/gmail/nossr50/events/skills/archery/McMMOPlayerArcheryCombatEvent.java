package com.gmail.nossr50.events.skills.archery;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerCombatEvent;

public class McMMOPlayerArcheryCombatEvent extends McMMOPlayerCombatEvent {
    public McMMOPlayerArcheryCombatEvent(Player player, Entity damager, Entity damagee, DamageCause cause, double damage) {
        super(player, damager, damagee, cause, damage, SkillType.ARCHERY);
    }
}
