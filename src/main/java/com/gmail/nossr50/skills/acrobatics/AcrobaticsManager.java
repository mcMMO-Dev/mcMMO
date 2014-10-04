package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class AcrobaticsManager extends SkillManager {
    private int fallTries = 0;
    Location lastFallLocation;

    public AcrobaticsManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.ACROBATICS);
    }

    public boolean canRoll() {
        return !exploitPrevention() && Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.ROLL);
    }

    public boolean canDodge(Entity damager) {
        if (Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.DODGE)) {
            if (damager instanceof LightningStrike && Acrobatics.dodgeLightningDisabled) {
                return false;
            }

            return skill.shouldProcess(damager);
        }

        return false;
    }

    /**
     * Handle the damage reduction and XP gain from the Dodge ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    public double dodgeCheck(double damage) {
        double modifiedDamage = Acrobatics.calculateModifiedDodgeDamage(damage, Acrobatics.dodgeDamageModifier);
        Player player = getPlayer();

        if (!isFatal(modifiedDamage) && SkillUtils.activationSuccessful(SecondaryAbility.DODGE, player, getSkillLevel(), activationChance)) {
            ParticleEffectUtils.playDodgeEffect(player);

            if (mcMMOPlayer.useChatNotifications()) {
                player.sendMessage(LocaleLoader.getString("Acrobatics.Combat.Proc"));
            }

            // Why do we check respawn cooldown here?
            if (SkillUtils.cooldownExpired(mcMMOPlayer.getRespawnATS(), Misc.PLAYER_RESPAWN_COOLDOWN_SECONDS)) {
                applyXpGain((float) (damage * Acrobatics.dodgeXpModifier), XPGainReason.PVP);
            }

            return modifiedDamage;
        }

        return damage;
    }

    /**
     * Handle the damage reduction and XP gain from the Roll ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    public double rollCheck(double damage) {
        Player player = getPlayer();

        if (player.isSneaking() && Permissions.secondaryAbilityEnabled(player, SecondaryAbility.GRACEFUL_ROLL)) {
            return gracefulRollCheck(damage);
        }

        double modifiedDamage = Acrobatics.calculateModifiedRollDamage(damage, Acrobatics.rollThreshold);

        if (!isFatal(modifiedDamage) && SkillUtils.activationSuccessful(SecondaryAbility.ROLL, player, getSkillLevel(), activationChance)) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Text"));
            applyXpGain(calculateRollXP(damage, true), XPGainReason.PVE);

            return modifiedDamage;
        }
        else if (!isFatal(damage)) {
            applyXpGain(calculateRollXP(damage, false), XPGainReason.PVE);
        }

        lastFallLocation = player.getLocation();

        return damage;
    }

    /**
     * Handle the damage reduction and XP gain from the Graceful Roll ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    private double gracefulRollCheck(double damage) {
        double modifiedDamage = Acrobatics.calculateModifiedRollDamage(damage, Acrobatics.gracefulRollThreshold);

        if (!isFatal(modifiedDamage) && SkillUtils.activationSuccessful(SecondaryAbility.GRACEFUL_ROLL, getPlayer(), getSkillLevel(), activationChance)) {
            getPlayer().sendMessage(LocaleLoader.getString("Acrobatics.Ability.Proc"));
            applyXpGain(calculateRollXP(damage, true), XPGainReason.PVE);

            return modifiedDamage;
        }
        else if (!isFatal(damage)) {
            applyXpGain(calculateRollXP(damage, false), XPGainReason.PVE);
        }

        return damage;
    }

    /**
     * Check if the player is "farming" Acrobatics XP using
     * exploits in the game.
     *
     * @return true if exploits are detected, false otherwise
     */
    public boolean exploitPrevention() {
        if (!Config.getInstance().getAcrobaticsPreventAFK()) {
            return false;
        }

        Player player = getPlayer();

        if (player.getItemInHand().getType() == Material.ENDER_PEARL || player.isInsideVehicle()) {
            return true;
        }

        Location fallLocation = player.getLocation();
        int maxTries = Config.getInstance().getAcrobaticsAFKMaxTries();

        boolean sameLocation = (lastFallLocation != null && Misc.isNear(lastFallLocation, fallLocation, 2));

        fallTries = sameLocation ? Math.min(fallTries + 1, maxTries) : Math.max(fallTries - 1, 0);
        lastFallLocation = fallLocation;

        return fallTries + 1 > maxTries;
    }

    private boolean isFatal(double damage) {
        return getPlayer().getHealth() - damage <= 0;
    }

    private float calculateRollXP(double damage, boolean isRoll) {
        ItemStack boots = getPlayer().getInventory().getBoots();
        float xp = (float) (damage * (isRoll ? Acrobatics.rollXpModifier : Acrobatics.fallXpModifier));

        if (boots != null && boots.containsEnchantment(Enchantment.PROTECTION_FALL)) {
            xp *= Acrobatics.featherFallXPModifier;
        }

        return xp;
    }
}
