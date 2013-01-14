package com.gmail.nossr50.skills.archery;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class ArcheryManager extends SkillManager {
    public ArcheryManager (Player player) {
        super(player, SkillType.ARCHERY);
    }

    /**
     * Track arrows fired for later retrieval.
     *
     * @param livingEntity Entity damaged by the arrow
     */
    public void trackArrows(LivingEntity livingEntity) {
        if (Misc.isNPC(player) || !Permissions.trackArrows(player)) {
            return;
        }

        ArrowTrackingEventHandler eventHandler = new ArrowTrackingEventHandler(this, livingEntity);

        int randomChance = 100;
        if (Permissions.luckyArchery(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        double chance = (Archery.retrieveMaxChance / Archery.retrieveMaxBonusLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(randomChance)) {
            eventHandler.addToTracker();
        }
    }

    /**
     * Check for Daze.
     *
     * @param defender Defending player
     * @param event The event to modify
     */
    public void dazeCheck(Player defender, EntityDamageEvent event) {
        if (Misc.isNPC(player) || !Permissions.daze(player)) {
            return;
        }

        DazeEventHandler eventHandler = new DazeEventHandler(this, event, defender);

        int randomChance = 100;
        if (Permissions.luckyArchery(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        double chance = (Archery.dazeMaxBonus / Archery.dazeMaxBonusLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(randomChance)) {
            eventHandler.handleDazeEffect();
            eventHandler.sendAbilityMessages();
        }
    }

    /**
     * Handle archery bonus damage.
     *
     * @param event The event to modify.
     */
    public void skillShot(EntityDamageEvent event) {
        if (Misc.isNPC(player) || !Permissions.archeryBonus(player)) {
            return;
        }

        if (skillLevel >= Archery.skillShotIncreaseLevel) {
            SkillShotEventHandler eventHandler = new SkillShotEventHandler(this, event);

            eventHandler.calculateDamageBonus();
            eventHandler.modifyEventDamage();
        }
    }
}
