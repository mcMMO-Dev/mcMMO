package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class AxeManager extends SkillManager {
    public AxeManager(Player player) {
        super(player, SkillType.AXES);
    }

    /**
     * Apply bonus to damage done by axes.
     *
     * @param event The event to modify
     */
    public void bonusDamage(EntityDamageByEntityEvent event) {
        if (Misc.isNPC(player) || !Permissions.axeBonus(player)) {
            return;
        }

        AxeBonusDamageEventHandler eventHandler = new AxeBonusDamageEventHandler(this, event);

        eventHandler.calculateDamageBonus();
        eventHandler.modifyEventDamage();
    }

    /**
     * Check for critical chances on axe damage.
     *
     * @param event The event to modify
     */
    public void criticalHitCheck(EntityDamageByEntityEvent event) {
        if (Misc.isNPC(player) || !Permissions.criticalHit(player)) {
            return;
        }

        CriticalHitEventHandler eventHandler = new CriticalHitEventHandler(this, event);

        if (eventHandler.defender instanceof Tameable && Misc.isFriendlyPet(player, (Tameable) eventHandler.defender)) {
            return;
        }

        int randomChance = 100;
        if (Permissions.luckyAxes(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        double chance = (Axes.criticalHitMaxChance / Axes.criticalHitMaxBonusLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(randomChance) && !eventHandler.defender.isDead()) {
            eventHandler.modifyEventDamage();
            eventHandler.sendAbilityMessages();
        }
    }

}
