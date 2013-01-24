package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.skills.AbilityType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.SkillType;
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
        if (Misc.isNPCPlayer(player) || !Permissions.axeBonus(player)) {
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
        if (Misc.isNPCPlayer(player) || !Permissions.criticalHit(player)) {
            return;
        }

        CriticalHitEventHandler eventHandler = new CriticalHitEventHandler(this, event);

        if (eventHandler.defender instanceof Tameable && Misc.isFriendlyPet(player, (Tameable) eventHandler.defender)) {
            return;
        }

        double chance = (Axes.criticalHitMaxChance / Axes.criticalHitMaxBonusLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(activationChance) && !eventHandler.defender.isDead()) {
            eventHandler.modifyEventDamage();
            eventHandler.sendAbilityMessages();
        }
    }

    /**
     * Check for Impact ability.
     *
     * @param event The event to modify
     */
    public void impact(EntityDamageByEntityEvent event) {
        if (Misc.isNPCPlayer(player) || !Permissions.impact(player)) {
            return;
        }

        ImpactEventHandler eventHandler = new ImpactEventHandler(this, event);

        if (eventHandler.livingDefender == null) {
            return;
        }

        if (Misc.hasArmor(eventHandler.livingDefender)) {
            eventHandler.damageArmor();
        }
        else {
            eventHandler.applyGreaterImpact();
        }
    }

    /**
     * Check for Skull Splitter ability.
     *
     * @param event The event to process
     */
    public void skullSplitter(EntityDamageByEntityEvent event) {
        if (Misc.isNPCPlayer(player) || !Permissions.skullSplitter(player) || !profile.getAbilityMode(AbilityType.SKULL_SPLIITER)) {
            return;
        }

        SkullSplitterEventHandler eventHandler = new SkullSplitterEventHandler(this, event);
        eventHandler.applyAbilityEffects();
    }
}
