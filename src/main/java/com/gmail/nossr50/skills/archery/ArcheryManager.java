package com.gmail.nossr50.skills.archery;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.archery.McMMOPlayerDazeEvent;
import com.gmail.nossr50.events.skills.archery.McMMOPlayerSkillShotEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class ArcheryManager extends SkillManager {
    public ArcheryManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.ARCHERY);
    }

    /**
     * Calculate bonus XP awarded for Archery when hitting a far-away target.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     * @param damager The {@link Entity} who shot the arrow
     */
    public void awardDistanceXpBonus(LivingEntity target, Entity damager) {
        Location firedLocation = (Location) damager.getMetadata(mcMMO.arrowDistanceKey).get(0).value();
        Location targetLocation = target.getLocation();

        if (firedLocation.getWorld() != targetLocation.getWorld()) {
            return;
        }

        applyXpGain((int) (firedLocation.distanceSquared(targetLocation) * Archery.DISTANCE_XP_MULTIPLIER));
    }

    /**
     * Track arrows fired for later retrieval.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     * @param arrow The {@link Arrow} that damaged the target
     */
    public void trackArrow(LivingEntity target, Arrow arrow) {
        if (!canTrack(arrow)) {
            return;
        }

        Archery.incrementTrackerValue(target);
    }

    /**
     * Handle the effects of the Daze ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param arrow The {@link Arrow} that was fired
     */
    public double daze(LivingEntity target, Arrow arrow) {
        if (!canDaze(target)) {
            return 0;
        }

        Player attacker = getPlayer();

        McMMOPlayerDazeEvent event = new McMMOPlayerDazeEvent(attacker, arrow, target);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return 0;
        }

        Player defender = (Player) target;
        Location dazedLocation = defender.getLocation();
        dazedLocation.setPitch(90 - Misc.getRandom().nextInt(181));

        defender.teleport(dazedLocation);
        defender.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Misc.TICK_CONVERSION_FACTOR * 10, 10));

        if (UserManager.getPlayer(defender).useChatNotifications()) {
            defender.sendMessage(LocaleLoader.getString("Combat.TouchedFuzzy"));
        }

        if (mcMMOPlayer.useChatNotifications()) {
            attacker.sendMessage(LocaleLoader.getString("Combat.TargetDazed"));
        }

        return event.getDamage();
    }

    /**
     * Handle the effects of the Skill Shot ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     * @param arrow The {@link Arrow} that was fired
     */
    public double skillShot(LivingEntity target, double damage, Arrow arrow) {
        if (!canUseSkillShot()) {
            return 0;
        }

        McMMOPlayerSkillShotEvent event = new McMMOPlayerSkillShotEvent(getPlayer(), arrow, target, calculateSkillShotBonus(damage));
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return 0;
        }

        return event.getDamage();
    }

    private double calculateSkillShotBonus(double damage) {
        double damageBonusPercent = Math.min(((getSkillLevel() / Archery.skillShotIncreaseLevel) * Archery.skillShotIncreasePercentage), Archery.skillShotMaxBonusPercentage);
        double archeryBonus = Math.min(damage * damageBonusPercent, Archery.skillShotMaxBonusDamage);

        return archeryBonus;
    }

    private boolean canDaze(LivingEntity target) {
        return target instanceof Player && Permissions.daze(getPlayer()) && SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Archery.dazeMaxBonus, Archery.dazeMaxBonusLevel);
    }

    private boolean canUseSkillShot() {
        return getSkillLevel() >= Archery.skillShotIncreaseLevel && Permissions.bonusDamage(getPlayer(), skill);
    }

    private boolean canTrack(Arrow arrow) {
        return Permissions.arrowRetrieval(getPlayer()) && !arrow.hasMetadata(mcMMO.infiniteArrowKey) && SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Archery.retrieveMaxChance, Archery.retrieveMaxBonusLevel);
    }
}
