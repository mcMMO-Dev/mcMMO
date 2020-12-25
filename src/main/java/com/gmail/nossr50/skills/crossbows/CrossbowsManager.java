package com.gmail.nossr50.skills.crossbows;

import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CrossbowsManager extends SkillManager {
    public CrossbowsManager(OnlineMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.CROSSBOWS);
    }
    private static final int SPREAD_VALUE = 12;

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

        if (firedLocation == null || firedLocation.getWorld() != targetLocation.getWorld()) {
            return 1;
        }

        //TODO: Should use its own variable
        return 1 + Math.min(firedLocation.distance(targetLocation), 50) * Archery.DISTANCE_XP_MULTIPLIER;
    }

    /**
     * Used for sub-skills that activate on projectile launch
     * @param projectileLaunchEvent target event
     */
    public void processProjectileLaunchEvent(ProjectileLaunchEvent projectileLaunchEvent) {
        //Testing
        if(Permissions.isSubSkillEnabled(Misc.adaptPlayer(mmoPlayer), SubSkillType.CROSSBOWS_SUPER_SHOTGUN)) {
            if(RankUtils.hasUnlockedSubskill(Misc.adaptPlayer(mmoPlayer), SubSkillType.CROSSBOWS_SUPER_SHOTGUN)) {
                superShotgunProcessing(projectileLaunchEvent);
            }
        }
    }


    private void superShotgunProcessing(ProjectileLaunchEvent projectileLaunchEvent) {
        spawnSuperShotgunArrows(projectileLaunchEvent.getEntity());
    }

    private void spawnSuperShotgunArrows(@NotNull Projectile originProjectile) {
        World world = originProjectile.getWorld();

        Vector originVector = originProjectile.getVelocity().clone();
        float originProjectileMagnitude = (float) originVector.length();

        Vector originUnitVector = originVector.clone().normalize();

        for(int i = 0; i < getSuperShotgunAdditionalArrowCount(); i++) {
            Vector newProjectileVector = byRotateVector(originUnitVector, 0);
            spawnTrackedProjectile(originProjectile, world, originProjectileMagnitude, newProjectileVector, getRandomizedSpreadValue());
        }
    }

    public int getSuperShotgunAdditionalArrowCount() {
        switch(RankUtils.getRank(Misc.adaptPlayer(mmoPlayer), SubSkillType.CROSSBOWS_SUPER_SHOTGUN)) {
            case 1:
                return 9;
            case 2:
                return 18;
            default:
                return 27;
        }
    }

    private int getRandomizedSpreadValue() {
        return SPREAD_VALUE + 12 + RandomUtils.nextInt(24);
    }

    private void spawnTrackedProjectile(@NotNull Projectile originProjectile, World world, float originProjectileMagnitude, Vector additionalProjectileVectorA, int spreadValue) {
        Projectile spawnedProjectile = world.spawnArrow(originProjectile.getLocation(), additionalProjectileVectorA, originProjectileMagnitude, spreadValue);
        spawnedProjectile.setShooter(mmoPlayer.getPlayer());
        mcMMO.getSpawnedProjectileTracker().trackProjectile(spawnedProjectile);
    }

    @NotNull
    private Vector byRotateVector(Vector originUnitVector, double angle) {
        return originUnitVector.clone().rotateAroundAxis(originUnitVector, angle);
    }


}
