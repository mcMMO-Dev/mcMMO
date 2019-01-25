package com.gmail.nossr50.skills.archery;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArcheryManager extends SkillManager {
    public ArcheryManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.ARCHERY);
    }

    public boolean canDaze(LivingEntity target) {
        return target instanceof Player && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_DAZE);
    }

    public boolean canSkillShot() {
        return getSkillLevel() >= Archery.skillShotIncreaseLevel && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_SKILL_SHOT);
    }

    public boolean canRetrieveArrows() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_ARROW_RETRIEVAL);
    }

    /**
     * Calculate bonus XP awarded for Archery when hitting a far-away target.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     * @param damager The {@link Entity} who shot the arrow
     */
    public void distanceXpBonus(LivingEntity target, Entity damager) {
        Location firedLocation = (Location) damager.getMetadata(mcMMO.arrowDistanceKey).get(0).value();
        Location targetLocation = target.getLocation();

        if (firedLocation.getWorld() != targetLocation.getWorld()) {
            return;
        }

        applyXpGain((int) (Math.min(firedLocation.distanceSquared(targetLocation), 2500) * Archery.DISTANCE_XP_MULTIPLIER), getXPGainReason(target, damager));
    }

    /**
     * Track arrows fired for later retrieval.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     */
    public void retrieveArrows(LivingEntity target) {
        if (RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.ARCHERY_ARROW_RETRIEVAL, getPlayer())) {
            Archery.incrementTrackerValue(target);
        }
    }

    /**
     * Handle the effects of the Daze ability
     *
     * @param defender The {@link Player} being affected by the ability
     */
    public double daze(Player defender) {
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.ARCHERY_DAZE, getPlayer())) {
            return 0;
        }

        Location dazedLocation = defender.getLocation();
        dazedLocation.setPitch(90 - Misc.getRandom().nextInt(181));

        defender.teleport(dazedLocation);
        defender.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 10));

        if (UserManager.getPlayer(defender).useChatNotifications()) {
            NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Combat.TouchedFuzzy");
        }

        if (mcMMOPlayer.useChatNotifications()) {
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

        return Archery.getSkillShotBonusDamage(getPlayer(), oldDamage);
    }
}
