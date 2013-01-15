package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.util.Combat;

public class SerratedStrikesEventHandler {
    private Player player;
    private LivingEntity target;
    private int damage;

    protected SerratedStrikesEventHandler(SwordsManager manager, LivingEntity target, int damage) {
        this.player = manager.getPlayer();
        this.target = target;
        this.damage = damage;
    }

    protected void applyAbilityEffects() {
        Combat.applyAbilityAoE(player, target, damage / Swords.SERRATED_STRIKES_MODIFIER, SkillType.SWORDS);
        BleedTimer.add(target, Swords.SERRATED_STRIKES_BLEED_TICKS);
    }
}
