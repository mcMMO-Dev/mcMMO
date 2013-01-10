package com.gmail.nossr50.skills.unarmed;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class UnarmedManager extends SkillManager {
    public UnarmedManager (Player player) {
        super(player, SkillType.UNARMED);
    }

    /**
     * Check for disarm.
     *
     * @param defender The defending player
     */
    public void disarmCheck(Player defender) {
        if (player == null)
            return;

        if (!Permissions.disarm(player)) {
            return;
        }

        DisarmEventHandler eventHandler = new DisarmEventHandler(this, defender);

        if (eventHandler.isHoldingItem()) {
            eventHandler.calculateSkillModifier();

            int randomChance = 100;

            if (Permissions.luckyUnarmed(player)) {
                randomChance = (int) (randomChance * 0.75);
            }

            float chance = (float) (((double) Unarmed.DISARM_MAX_CHANCE / (double) Unarmed.DISARM_MAX_BONUS_LEVEL) * skillLevel);
            if (chance > Unarmed.DISARM_MAX_CHANCE) chance = Unarmed.DISARM_MAX_CHANCE;

            if (chance > Misc.getRandom().nextInt(randomChance)) {
                if (!hasIronGrip(defender)) {
                    eventHandler.sendAbilityMessage();
                    eventHandler.handleDisarm();
                }
            }
        }
    }

    /**
     * Check for arrow deflection.
     *
     * @param defender The defending player
     * @param event The event to modify
     */
    public void deflectCheck(EntityDamageEvent event) {
        if (player == null)
            return;

        if (!Permissions.deflect(player)) {
            return;
        }

        DeflectEventHandler eventHandler = new DeflectEventHandler(this, event);

        int randomChance = 100;

        if (Permissions.luckyUnarmed(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        float chance = (float) (((double) Unarmed.DEFLECT_MAX_CHANCE / (double) Unarmed.DEFLECT_MAX_BONUS_LEVEL) * skillLevel);
        if (chance > Unarmed.DEFLECT_MAX_CHANCE) chance = Unarmed.DEFLECT_MAX_CHANCE;

        if (chance > Misc.getRandom().nextInt(randomChance)) {
            eventHandler.cancelEvent();
            eventHandler.sendAbilityMessage();
        }
    }

    /**
     * Handle Unarmed bonus damage.
     *
     * @param event The event to modify.
     */
    public void bonusDamage(EntityDamageEvent event) {
        if (player == null)
            return;

        if (!Permissions.unarmedBonus(player)) {
            return;
        }

        UnarmedBonusDamageEventHandler eventHandler = new UnarmedBonusDamageEventHandler(this, event);

        eventHandler.calculateDamageBonus();
        eventHandler.modifyEventDamage();
    }

    /**
     * Check Iron Grip ability success
     *
     * @param defender The defending player
     * @return true if the defender was not disarmed, false otherwise
     */
    private boolean hasIronGrip(Player defender) {
        if (defender == null)
            return false;

        if (!Permissions.ironGrip(defender)) {
            return false;
        }

        IronGripEventHandler eventHandler = new IronGripEventHandler(this, defender);

        int randomChance = 100;

        if (Permissions.luckyUnarmed(defender)) {
            randomChance = (int) (randomChance * 0.75);
        }

        float chance = (float) (((double) Unarmed.IRON_GRIP_MAX_CHANCE / (double) Unarmed.IRON_GRIP_MAX_BONUS_LEVEL) * skillLevel);
        if (chance > Unarmed.IRON_GRIP_MAX_CHANCE) chance = Unarmed.IRON_GRIP_MAX_CHANCE;

        if (chance > Misc.getRandom().nextInt(randomChance)) {
            eventHandler.sendAbilityMessages();
            return true;
        }

        return false;
    }
}
