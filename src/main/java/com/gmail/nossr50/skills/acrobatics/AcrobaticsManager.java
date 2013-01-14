package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.SkillManager;
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
        if (Misc.isNPC(player) || !Permissions.roll(player)) {
            return;
        }

        if (Config.getInstance().getAcrobaticsAFKDisabled() && player.isInsideVehicle()) {
            return;
        }

        RollEventHandler eventHandler = new RollEventHandler(this, event);

        int randomChance = 100;
        if (Permissions.luckyAcrobatics(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        double chance;

        if (eventHandler.isGraceful) {
            chance = (Acrobatics.gracefulRollMaxChance / Acrobatics.gracefulRollMaxBonusLevel) * eventHandler.skillModifier;
        }
        else {
            chance = (Acrobatics.rollMaxChance / Acrobatics.rollMaxBonusLevel) * eventHandler.skillModifier;
        }

        if (chance > Misc.getRandom().nextInt(randomChance) && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
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
        if (Misc.isNPC(player) || !Permissions.dodge(player)) {
            return;
        }

        DodgeEventHandler eventHandler = new DodgeEventHandler(this, event);

        int randomChance = 100;
        if (Permissions.luckyAcrobatics(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        double chance = (Acrobatics.dodgeMaxChance / Acrobatics.dodgeMaxBonusLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(randomChance) && !eventHandler.isFatal(eventHandler.modifiedDamage)) {
            eventHandler.modifyEventDamage();
            eventHandler.sendAbilityMessage();
            eventHandler.processXPGain(eventHandler.damage * Acrobatics.dodgeXpModifier);
        }
    }
}
