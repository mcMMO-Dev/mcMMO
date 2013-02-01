package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;

public class AcrobaticsManager extends SkillManager {
    public AcrobaticsManager (McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.ACROBATICS);
    }

    /**
     * Check for fall damage reduction.
     *
     * @param event The event to check
     */
    public void rollCheck(EntityDamageEvent event) {
        RollEventHandler eventHandler = new RollEventHandler(this, event);

        double chance;

        if (eventHandler.isGraceful) {
            chance = (Acrobatics.gracefulRollMaxChance / Acrobatics.gracefulRollMaxBonusLevel) * eventHandler.skillModifier;
        }
        else {
            chance = (Acrobatics.rollMaxChance / Acrobatics.rollMaxBonusLevel) * eventHandler.skillModifier;
        }

        if (chance > Misc.getRandom().nextInt(activationChance) && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
            eventHandler.modifyEventDamage();
            eventHandler.sendAbilityMessage();
            eventHandler.processXpGain(eventHandler.damage * Acrobatics.rollXpModifier);
        }
        else if (!eventHandler.isFatal(event.getDamage())) {
            eventHandler.processXpGain(eventHandler.damage * Acrobatics.fallXpModifier);
        }
    }

    /**
     * Check for dodge damage reduction.
     *
     * @param event The event to check
     */
    public void dodgeCheck(EntityDamageEvent event) {
        DodgeEventHandler eventHandler = new DodgeEventHandler(this, event);

        double chance = (Acrobatics.dodgeMaxChance / Acrobatics.dodgeMaxBonusLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(activationChance) && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
            eventHandler.modifyEventDamage();
            eventHandler.sendAbilityMessage();
            eventHandler.processXpGain(eventHandler.damage * Acrobatics.dodgeXpModifier);
        }
    }
}
