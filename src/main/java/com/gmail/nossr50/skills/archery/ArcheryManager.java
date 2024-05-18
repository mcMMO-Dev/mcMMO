package com.gmail.nossr50.skills.archery;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;

import static com.gmail.nossr50.util.PotionEffectUtil.getNauseaPotionEffectType;

public class ArcheryManager extends SkillManager {
    public ArcheryManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.ARCHERY);
    }

    public boolean canDaze(LivingEntity target) {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.ARCHERY_DAZE))
            return false;

        return target instanceof Player && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_DAZE);
    }

    public boolean canSkillShot() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.ARCHERY_SKILL_SHOT))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_SKILL_SHOT);
    }

    public boolean canRetrieveArrows() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.ARCHERY_ARROW_RETRIEVAL))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_ARROW_RETRIEVAL);
    }

    /**
     * Calculate bonus XP awarded for Archery when hitting a far-away target.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     * @param arrow The {@link Entity} who shot the arrow
     */
    public static double distanceXpBonusMultiplier(LivingEntity target, Entity arrow) {
        //Hacky Fix - some plugins spawn arrows and assign them to players after the ProjectileLaunchEvent fires
        if (!arrow.hasMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE))
            return 1;

        Location firedLocation = (Location) arrow.getMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE).get(0).value();
        Location targetLocation = target.getLocation();

        if (firedLocation == null || firedLocation.getWorld() == null)
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
    public void retrieveArrows(LivingEntity target, Projectile projectile) {
        if (projectile.hasMetadata(MetadataConstants.METADATA_KEY_TRACKED_ARROW)) {
            Archery.incrementTrackerValue(target);
            projectile.removeMetadata(MetadataConstants.METADATA_KEY_TRACKED_ARROW, mcMMO.p); //Only 1 entity per projectile
        }
    }

    /**
     * Handle the effects of the Daze ability
     *
     * @param defender The {@link Player} being affected by the ability
     */
    public double daze(Player defender) {
        if (!ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.ARCHERY_DAZE, mmoPlayer)) {
            return 0;
        }

        Location dazedLocation = defender.getLocation();
        dazedLocation.setPitch(90 - Misc.getRandom().nextInt(181));

        mcMMO.p.getFoliaLib().getImpl().teleportAsync(defender, dazedLocation);
        defender.addPotionEffect(new PotionEffect(getNauseaPotionEffectType(), 20 * 10, 10));

        if (NotificationManager.doesPlayerUseNotifications(defender)) {
            NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Combat.TouchedFuzzy");
        }

        if (mmoPlayer.useChatNotifications()) {
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
        if (ProbabilityUtil.isNonRNGSkillActivationSuccessful(SubSkillType.ARCHERY_SKILL_SHOT, mmoPlayer)) {
            return Archery.getSkillShotBonusDamage(getPlayer(), oldDamage);
        } else {
            return oldDamage;
        }
    }
}
