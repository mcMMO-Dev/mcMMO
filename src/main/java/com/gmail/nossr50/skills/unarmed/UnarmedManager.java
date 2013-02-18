package com.gmail.nossr50.skills.unarmed;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.utilities.PerksUtils;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class UnarmedManager extends SkillManager {
    public UnarmedManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.UNARMED);
    }

    /**
     * Check for disarm.
     *
     * @param defender The defending player
     */
    public void disarmCheck(LivingEntity defender) {
        Player defendingPlayer = (Player) defender;
        DisarmEventHandler eventHandler = new DisarmEventHandler(this, defendingPlayer);

        if (eventHandler.isHoldingItem()) {
            eventHandler.calculateSkillModifier();

            float chance = (float) ((Unarmed.disarmMaxChance / Unarmed.disarmMaxBonusLevel) * skillLevel);
            if (chance > Unarmed.disarmMaxChance) chance = (float) Unarmed.disarmMaxChance;

            if (chance > Misc.getRandom().nextInt(activationChance)) {
                if (!hasIronGrip(defendingPlayer)) {
                    eventHandler.handleDisarm();
                }
            }
        }
    }

    /**
     * Check for arrow deflection.
     *
     * @param event The event to modify
     */
    public void deflectCheck(EntityDamageEvent event) {
        DeflectEventHandler eventHandler = new DeflectEventHandler(this, event);

        float chance = (float) ((Unarmed.deflectMaxChance / Unarmed.deflectMaxBonusLevel) * skillLevel);
        if (chance > Unarmed.deflectMaxChance) chance = (float) Unarmed.deflectMaxChance;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
            eventHandler.cancelEvent();
            eventHandler.sendAbilityMessage();
        }
    }

    public void berserkDamage(EntityDamageEvent event) {
        event.setDamage((int) (event.getDamage() * Unarmed.berserkDamageModifier));
    }

    /**
     * Handle Unarmed bonus damage.
     *
     * @param event The event to modify.
     */
    public void bonusDamage(EntityDamageEvent event) {
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
        if (Misc.isNPCEntity(defender) || !Permissions.ironGrip(defender)) {
            return false;
        }

        IronGripEventHandler eventHandler = new IronGripEventHandler(this, defender);

        double chance = (Unarmed.ironGripMaxChance / Unarmed.ironGripMaxBonusLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(PerksUtils.handleLuckyPerks(Permissions.luckyUnarmed(defender)))) {
            eventHandler.sendAbilityMessages();
            return true;
        }

        return false;
    }
}
