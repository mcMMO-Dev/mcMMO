package com.gmail.nossr50.skills.crossbows;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.archery.Archery;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class CrossbowManager extends SkillManager {
    public CrossbowManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.CROSSBOWS);
    }

    /**
     * Calculate bonus XP awarded for Archery when hitting a far-away target.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     * @param damager The {@link Entity} who shot the arrow
     */
    public double distanceXpBonusMultiplier(LivingEntity target, Entity damager) {
        //Hacky Fix - some plugins spawn arrows and assign them to players after the ProjectileLaunchEvent fires
        if(!damager.hasMetadata(mcMMO.arrowDistanceKey))
            return damager.getLocation().distance(target.getLocation());

        Location firedLocation = (Location) damager.getMetadata(mcMMO.arrowDistanceKey).get(0).value();
        Location targetLocation = target.getLocation();

        if (firedLocation.getWorld() != targetLocation.getWorld()) {
            return 1;
        }

        //TODO: Should use its own variable
        return 1 + Math.min(firedLocation.distance(targetLocation), 50) * Archery.DISTANCE_XP_MULTIPLIER;
    }

}
