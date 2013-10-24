package com.gmail.nossr50.events.skills.axes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class McMMOPlayerGreaterImpactEvent extends McMMOPlayerAxeCombatEvent {
    private Vector knockbackVelocity;

    public McMMOPlayerGreaterImpactEvent(Player player, Entity damagee, double damage, Vector knockbackVelocity) {
        super(player, damagee, DamageCause.ENTITY_ATTACK, damage);
        this.knockbackVelocity = knockbackVelocity;
    }

    public Vector getKnockbackVelocity() {
        return knockbackVelocity;
    }

    public void setKnockbackVelocity(Vector knockbackVelocity) {
        this.knockbackVelocity = knockbackVelocity;
    }
}
