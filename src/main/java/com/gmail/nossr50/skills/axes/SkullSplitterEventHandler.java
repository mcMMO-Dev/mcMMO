package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.skills.utilities.CombatTools;
import com.gmail.nossr50.skills.utilities.SkillType;

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
        CombatTools.applyAbilityAoE(player, target, damage / Axes.skullSplitterModifier, SkillType.AXES);
    }
}
