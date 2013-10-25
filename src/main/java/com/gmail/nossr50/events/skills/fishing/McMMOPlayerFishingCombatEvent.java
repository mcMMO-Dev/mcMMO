package com.gmail.nossr50.events.skills.fishing;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerCombatEvent;

public abstract class McMMOPlayerFishingCombatEvent extends McMMOPlayerCombatEvent {
    public McMMOPlayerFishingCombatEvent(Player player, Entity damager, Entity damagee, DamageCause cause, double damage) {
        super(player, damager, damagee, cause, damage, SkillType.FISHING);
    }
}
