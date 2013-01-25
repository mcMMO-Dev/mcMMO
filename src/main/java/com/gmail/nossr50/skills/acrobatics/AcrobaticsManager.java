package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class AcrobaticsManager extends SkillManager {
    public AcrobaticsManager (Player player) {
        super(player, SkillType.ACROBATICS);
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
            eventHandler.processXPGain(eventHandler.damage * Acrobatics.rollXpModifier);
        }
        else if (!eventHandler.isFatal(event.getDamage())) {
            eventHandler.processXPGain(eventHandler.damage * Acrobatics.fallXpModifier);
        }
    }

    /**
     * Check for dodge damage reduction.
     *
     * @param event The event to check
     */
    public void dodgeCheck(EntityDamageEvent event) {
        if (Misc.isNPCPlayer(player) || !Permissions.dodge(player)) {
            return;
        }

        DodgeEventHandler eventHandler = new DodgeEventHandler(this, event);

        double chance = (Acrobatics.dodgeMaxChance / Acrobatics.dodgeMaxBonusLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(activationChance) && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
            eventHandler.modifyEventDamage();
            eventHandler.sendAbilityMessage();
            eventHandler.processXPGain(eventHandler.damage * Acrobatics.dodgeXpModifier);
        }
    }
}
