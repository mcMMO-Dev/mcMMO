package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Combat;

public class SkullSplitterEventHandler {
    private Player player;
    private LivingEntity target;
    private int damage;

    protected SkullSplitterEventHandler(AxeManager manager, EntityDamageByEntityEvent event) {
        this.player = manager.getPlayer();
        this.target = (LivingEntity) event.getEntity();
        this.damage = event.getDamage();
    }

    protected void applyAbilityEffects() {
        Combat.applyAbilityAoE(player, target, damage / 2, SkillType.AXES);
    }
}
