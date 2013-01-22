package com.gmail.nossr50.skills.unarmed;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.SkillType;
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
        if (Misc.isNPC(player) || !Permissions.disarm(player)) {
            return;
        }

        DisarmEventHandler eventHandler = new DisarmEventHandler(this, defender);

        if (eventHandler.isHoldingItem()) {
            eventHandler.calculateSkillModifier();

            float chance = (float) ((Unarmed.disarmMaxChance / Unarmed.disarmMaxBonusLevel) * skillLevel);
            if (chance > Unarmed.disarmMaxChance) chance = (float) Unarmed.disarmMaxChance;

            if (chance > Misc.getRandom().nextInt(activationChance)) {
                if (!hasIronGrip(defender)) {
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

        float chance = (float) ((Unarmed.deflectMaxChance / Unarmed.deflectMaxBonusLevel) * skillLevel);
        if (chance > Unarmed.deflectMaxChance) chance = (float) Unarmed.deflectMaxChance;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
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

        float chance = (float) ((Unarmed.ironGripMaxChance / Unarmed.ironGripMaxBonusLevel) * skillLevel);
        if (chance > Unarmed.ironGripMaxChance) chance = (float) Unarmed.ironGripMaxChance;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
            eventHandler.sendAbilityMessages();
            return true;
        }

        return false;
    }
}
