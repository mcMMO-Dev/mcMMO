package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Combat;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class SwordsManager extends SkillManager {
    public SwordsManager (Player player) {
        super(player, SkillType.SWORDS);
    }

    /**
     * Check for Bleed effect.
     *
     * @param defender The defending entity
     */
    public void bleedCheck(LivingEntity defender) {
        if (player == null)
            return;

        if (!Permissions.swordsBleed(player)) {
            return;
        }

        if (Combat.shouldBeAffected(player, defender)) {
            BleedEventHandler eventHandler = new BleedEventHandler(this, defender);

            int randomChance = 100;

            if (Permissions.luckySwords(player)) {
                randomChance = (int) (randomChance * 0.75);
            }

            float chance = (float) (((double) Swords.BLEED_CHANCE_MAX / (double) Swords.BLEED_MAX_BONUS_LEVEL) * skillLevel);
            if (chance > Swords.BLEED_CHANCE_MAX) chance = Swords.BLEED_CHANCE_MAX;

            if (chance > Misc.getRandom().nextInt(randomChance)) {
                eventHandler.addBleedTicks();
                eventHandler.sendAbilityMessages();
            }
        }
    }

    public void counterAttackChecks(LivingEntity attacker, int damage) {
        if (player == null)
            return;

        if (!Permissions.counterAttack(player)) {
            return;
        }

        CounterAttackEventHandler eventHandler = new CounterAttackEventHandler(this, attacker, damage);

        if (eventHandler.isHoldingSword()) {
            eventHandler.calculateSkillModifier();

            int randomChance = 100;

            if (Permissions.luckySwords(player)) {
                randomChance = (int) (randomChance * 0.75);
            }

            float chance = (float) (((double) Swords.COUNTER_ATTACK_CHANCE_MAX / (double) Swords.COUNTER_ATTACK_MAX_BONUS_LEVEL) * skillLevel);
            if (chance > Swords.COUNTER_ATTACK_CHANCE_MAX) chance = Swords.COUNTER_ATTACK_CHANCE_MAX;

            if (chance > Misc.getRandom().nextInt(randomChance)) {
                eventHandler.dealDamage();
                eventHandler.sendAbilityMessages();
            }
        }
    }

    public void serratedStrikes(LivingEntity target, int damage) {
        if (player == null)
            return;

        if (!Permissions.serratedStrikes(player)) {
            return;
        }

        SerratedStrikesEventHandler eventHandler = new SerratedStrikesEventHandler(this, target, damage);

        eventHandler.applyAbilityEffects();
    }
}
