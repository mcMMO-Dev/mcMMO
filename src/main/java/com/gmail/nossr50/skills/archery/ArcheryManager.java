package com.gmail.nossr50.skills.archery;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
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

    public boolean canDaze(LivingEntity target) {
        return target instanceof Player && Permissions.daze(getPlayer());
    }

    public boolean canSkillShot() {
        return getSkillLevel() >= Archery.skillShotIncreaseLevel && Permissions.bonusDamage(getPlayer(), skill);
    }

    public boolean canTrackArrows() {
        Player player = getPlayer();

        return !(player.getItemInHand().containsEnchantment(Enchantment.ARROW_INFINITE)) && Permissions.arrowRetrieval(player);
    }

    /**
     * Calculate bonus XP awarded for Archery when hitting a far-away target.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     */
    public void distanceXpBonus(LivingEntity target) {
        Player player = getPlayer();
        Location shooterLocation = player.getLocation();
        Location targetLocation = target.getLocation();

        if (!shooterLocation.getWorld().equals(targetLocation.getWorld())) {
            return;
        }

        // Cap distance at 100^2 to prevent teleport exploit.
        // TODO: Better way to handle this would be great...
        double squaredDistance = Math.min(shooterLocation.distanceSquared(targetLocation), 10000);

        applyXpGain((int) (squaredDistance * Archery.DISTANCE_XP_MULTIPLIER));
    }

    /**
     * Track arrows fired for later retrieval.
     *
     * @param target The {@link LivingEntity} damaged by the arrow
     */
    public void trackArrows(LivingEntity target) {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Archery.retrieveMaxChance, Archery.retrieveMaxBonusLevel)) {
            Archery.incrementTrackerValue(target);
        }
    }

    /**
     * Handle the effects of the Daze ability
     *
     * @param defender The {@link Player} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    public int dazeCheck(Player defender, int damage) {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Archery.dazeMaxBonus, Archery.dazeMaxBonusLevel)) {
            Location dazedLocation = defender.getLocation();
            dazedLocation.setPitch(90 - Misc.getRandom().nextInt(181));

            defender.teleport(dazedLocation);
            defender.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 10));

            if (UserManager.getPlayer(defender).useChatNotifications()) {
                defender.sendMessage(LocaleLoader.getString("Combat.TouchedFuzzy"));
            }

            if (mcMMOPlayer.useChatNotifications()) {
                getPlayer().sendMessage(LocaleLoader.getString("Combat.TargetDazed"));
            }

            return damage + Archery.dazeModifier;
        }

        return damage;
    }

    /**
     * Handle the effects of the Skill Shot ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage
     */
    public int skillShotCheck(int damage) {
        double damageBonusPercent = Math.min(((getSkillLevel() / Archery.skillShotIncreaseLevel) * Archery.skillShotIncreasePercentage), Archery.skillShotMaxBonusPercentage);
        int archeryBonus = (int) (damage * damageBonusPercent);

        return damage + archeryBonus;
    }
}
