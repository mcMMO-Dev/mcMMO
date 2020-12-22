package com.gmail.nossr50.skills.archery;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ArcheryManager extends SkillManager {
    public ArcheryManager(OnlineMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.ARCHERY);
    }

    public boolean canDaze(LivingEntity target) {
        if(!RankUtils.hasUnlockedSubskill(mmoPlayer, SubSkillType.ARCHERY_DAZE))
            return false;

        return target instanceof Player && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_DAZE);
    }

    public boolean canSkillShot() {
        if(!RankUtils.hasUnlockedSubskill(mmoPlayer, SubSkillType.ARCHERY_SKILL_SHOT))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_SKILL_SHOT);
    }

    public boolean canRetrieveArrows() {
        if(!RankUtils.hasUnlockedSubskill(mmoPlayer, SubSkillType.ARCHERY_ARROW_RETRIEVAL))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_ARROW_RETRIEVAL);
    }

    /**
     * Calculate bonus XP awarded for Archery when hitting a far-away target.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     * @param arrow The {@link Entity} who shot the arrow
     */
    public double distanceXpBonusMultiplier(@NotNull LivingEntity target, @NotNull Entity arrow) {
        //Hacky Fix - some plugins spawn arrows and assign them to players after the ProjectileLaunchEvent fires
        if(!arrow.hasMetadata(mcMMO.arrowDistanceKey))
            return arrow.getLocation().distance(target.getLocation());


        Location firedLocation = (Location) arrow.getMetadata(mcMMO.arrowDistanceKey).get(0).value();
        Location targetLocation = target.getLocation();

        if(firedLocation == null || firedLocation.getWorld() == null)
            return 1;

        if (firedLocation.getWorld() != targetLocation.getWorld()) {
            return 1;
        }

        return 1 + Math.min(firedLocation.distance(targetLocation), 50) * Archery.DISTANCE_XP_MULTIPLIER;
    }

    /**
     * Track arrows fired for later retrieval.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     */
    public void retrieveArrows(@NotNull LivingEntity target, @NotNull Projectile projectile) {
        if(projectile.hasMetadata(mcMMO.trackedArrow)) {
            Archery.incrementTrackerValue(target);
            projectile.removeMetadata(mcMMO.trackedArrow, mcMMO.p); //Only 1 entity per projectile
        }
    }

    /**
     * Handle the effects of the Daze ability
     *
     * @param defender The {@link Player} being affected by the ability
     */
    public double daze(@NotNull Player defender) {
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.ARCHERY_DAZE, getPlayer())) {
            return 0;
        }

        Location dazedLocation = defender.getLocation();
        dazedLocation.setPitch(90 - Misc.getRandom().nextInt(181));

        defender.teleport(dazedLocation);
        defender.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 10));


        if (NotificationManager.doesPlayerUseNotifications(defender)) {
            NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Combat.TouchedFuzzy");
        }

        if (mmoPlayer.hasSkillChatNotifications()) {
            NotificationManager.sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE, "Combat.TargetDazed");
        }

        return Archery.dazeBonusDamage;
    }

    /**
     * Calculates the damage to deal after Skill Shot has been applied
     *
     * @param oldDamage The raw damage value of this arrow before we modify it
     */
    public double skillShot(double oldDamage) {
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.ALWAYS_FIRES, SubSkillType.ARCHERY_SKILL_SHOT, getPlayer())) {
            return oldDamage;
        }

        return Archery.getSkillShotBonusDamage(mmoPlayer, oldDamage);
    }
}
