package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;

import com.gmail.nossr50.skills.runnables.BleedTimer;
import com.gmail.nossr50.skills.utilities.CombatTools;
import com.gmail.nossr50.skills.utilities.SkillType;

public class SerratedStrikesEventHandler {
    private SwordsManager manager;
    private LivingEntity target;
    private int damage;

    protected SerratedStrikesEventHandler(SwordsManager manager, LivingEntity target, int damage) {
        this.manager = manager;
        this.target = target;
        this.damage = damage;
    }

    protected void applyAbilityEffects() {
        CombatTools.applyAbilityAoE(manager.getMcMMOPlayer().getPlayer(), target, damage / Swords.serratedStrikesModifier, SkillType.SWORDS);
        BleedTimer.add(target, Swords.serratedStrikesBleedTicks);
    }
}
