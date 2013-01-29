package com.gmail.nossr50.skills.archery;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.SkillTools;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class ArcheryManager extends SkillManager {
    public ArcheryManager (Player player) {
        super(player, SkillType.ARCHERY);
    }

    public void distanceXpBonus(LivingEntity target) {
        Location shooterLocation = player.getEyeLocation();
        Location targetLocation = target.getLocation();
        double distance = shooterLocation.distance(targetLocation);

        int bonusXp = (int) (distance * Archery.distanceXpModifer);
        SkillTools.xpProcessing(player, profile, SkillType.ARCHERY, bonusXp);
    }

    /**
     * Track arrows fired for later retrieval.
     *
     * @param livingEntity Entity damaged by the arrow
     */
    public void trackArrows(LivingEntity livingEntity) {
        ArrowTrackingEventHandler eventHandler = new ArrowTrackingEventHandler(this, livingEntity);

        double chance = (Archery.retrieveMaxChance / Archery.retrieveMaxBonusLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
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
        DazeEventHandler eventHandler = new DazeEventHandler(this, event, defender);

        double chance = (Archery.dazeMaxBonus / Archery.dazeMaxBonusLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
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
        if (skillLevel >= Archery.skillShotIncreaseLevel && Permissions.archeryBonus(player)) {
            SkillShotEventHandler eventHandler = new SkillShotEventHandler(this, event);

            eventHandler.calculateDamageBonus();
            eventHandler.modifyEventDamage();
        }
    }
}
