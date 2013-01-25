package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.skills.Combat;
import com.gmail.nossr50.skills.SkillType;

public class SkullSplitterEventHandler {
    private Player player;
    private LivingEntity target;
    private int damage;

    protected SkullSplitterEventHandler(Player player, int damage, LivingEntity target) {
        this.player = player;
        this.target = target;
        this.damage = damage;
    }

    protected void applyAbilityEffects() {
        Combat.applyAbilityAoE(player, target, damage / Axes.skullSplitterModifier, SkillType.AXES);
    }
}
