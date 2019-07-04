package com.gmail.nossr50.skills.archery;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.behaviours.ArcheryBehaviour;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArcheryManager extends SkillManager {

    private final ArcheryBehaviour archeryBehaviour;

    public ArcheryManager(mcMMO pluginRef, McMMOPlayer mcMMOPlayer) {
        super(pluginRef, mcMMOPlayer, PrimarySkillType.ARCHERY);

        //Init Behaviour
        this.archeryBehaviour = pluginRef.getDynamicSettingsManager().getSkillBehaviourManager().getArcheryBehaviour();
    }

    public boolean canDaze(LivingEntity target) {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.ARCHERY_DAZE))
            return false;

        return target instanceof Player && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_DAZE);
    }

    public boolean canSkillShot() {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.ARCHERY_SKILL_SHOT))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_SKILL_SHOT);
    }

    public boolean canRetrieveArrows() {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.ARCHERY_ARROW_RETRIEVAL))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.ARCHERY_ARROW_RETRIEVAL);
    }

    /**
     * Calculate bonus XP awarded for Archery when hitting a far-away target.
     *
     * @param target  The {@link LivingEntity} damaged by the arrow
     * @param damager The {@link Entity} who shot the arrow
     */
    public double distanceXpBonusMultiplier(LivingEntity target, Entity damager) {
        //Hacky Fix - some plugins spawn arrows and assign them to players after the ProjectileLaunchEvent fires
        if(!damager.hasMetadata(MetadataConstants.ARROW_DISTANCE_METAKEY))
            return damager.getLocation().distance(target.getLocation());

        Location firedLocation = (Location) damager.getMetadata(MetadataConstants.ARROW_DISTANCE_METAKEY).get(0).value();
        Location targetLocation = target.getLocation();

        if (firedLocation.getWorld() != targetLocation.getWorld()) {
            return 1;
        }

        return 1 + Math.min(firedLocation.distance(targetLocation), 50) * archeryBehaviour.getDistanceXpMultiplier();
    }

    /**
     * Track arrows fired for later retrieval.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     */
    public void processArrowRetrievalActivation(LivingEntity target, Projectile projectile) {
        if(projectile.hasMetadata(MetadataConstants.ARROW_TRACKER_METAKEY)) {
            archeryBehaviour.incrementArrowCount(target);
            projectile.removeMetadata(MetadataConstants.ARROW_TRACKER_METAKEY, pluginRef); //Only 1 entity per projectile
        }
    }

    /**
     * Handle the effects of the Daze ability
     *
     * @param defender The {@link Player} being affected by the ability
     */
    public double daze(Player defender) {
        if (!pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.ARCHERY_DAZE, getPlayer())) {
            return 0;
        }

        Location dazedLocation = defender.getLocation();
        dazedLocation.setPitch(90 - Misc.getRandom().nextInt(181));

        defender.teleport(dazedLocation);
        defender.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 10));


        if (pluginRef.getNotificationManager().doesPlayerUseNotifications(defender)) {
            pluginRef.getNotificationManager().sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Combat.TouchedFuzzy");
        }

        if (mcMMOPlayer.useChatNotifications()) {
            pluginRef.getNotificationManager().sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE, "Combat.TargetDazed");
        }

        return archeryBehaviour.getDazeBonusDamage();
    }

    /**
     * Calculates the damage to deal after Skill Shot has been applied
     *
     * @param oldDamage The raw damage value of this arrow before we modify it
     */
    public double skillShot(double oldDamage) {
        if (!pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.ALWAYS_FIRES, SubSkillType.ARCHERY_SKILL_SHOT, getPlayer())) {
            return oldDamage;
        }

        return archeryBehaviour.getSkillShotBonusDamage(getPlayer(), oldDamage);
    }
}
