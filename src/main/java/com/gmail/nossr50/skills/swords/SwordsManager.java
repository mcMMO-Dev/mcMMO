package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;

public class SwordsManager extends SkillManager {
    public SwordsManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.SWORDS);
    }

    /**
     * Check for Bleed effect.
     *
     * @param defender The defending entity
     */
    public void bleedCheck(LivingEntity defender) {
        BleedEventHandler eventHandler = new BleedEventHandler(this, defender);

        float chance = (float) ((Swords.bleedMaxChance / Swords.bleedMaxBonusLevel) * getSkillLevel());
        if (chance > Swords.bleedMaxChance) chance = (float) Swords.bleedMaxChance;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
            eventHandler.addBleedTicks();
            eventHandler.sendAbilityMessages();
        }
    }

    public void counterAttackChecks(LivingEntity attacker, int damage) {
        CounterAttackEventHandler eventHandler = new CounterAttackEventHandler(this, attacker, damage);
        eventHandler.calculateSkillModifier();

        float chance = (float) ((Swords.counterAttackMaxChance / Swords.counterAttackMaxBonusLevel) * getSkillLevel());
        if (chance > Swords.counterAttackMaxChance) chance = (float) Swords.counterAttackMaxChance;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
            eventHandler.dealDamage();
            eventHandler.sendAbilityMessages();
        }
    }

    public void serratedStrikes(LivingEntity target, int damage) {
        SerratedStrikesEventHandler eventHandler = new SerratedStrikesEventHandler(this, target, damage);
        eventHandler.applyAbilityEffects();
    }
}
