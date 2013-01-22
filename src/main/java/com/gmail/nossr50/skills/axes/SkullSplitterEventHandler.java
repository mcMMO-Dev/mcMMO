package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.skills.Combat;
import com.gmail.nossr50.skills.SkillType;

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
        Combat.applyAbilityAoE(player, target, damage / Axes.skullSplitterModifier, SkillType.AXES);
    }
}
