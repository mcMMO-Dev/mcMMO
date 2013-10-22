package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.acrobatics.McMMOPlayerDodgeEvent;
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
        return !exploitPrevention() && Permissions.roll(getPlayer());
    }

    private boolean canDodge(Entity damager, double modifiedDamage) {
        return (Permissions.dodge(getPlayer()) && !(damager instanceof LightningStrike && Acrobatics.dodgeLightningDisabled) && skill.shouldProcess(damager) && SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Acrobatics.dodgeMaxChance, Acrobatics.dodgeMaxBonusLevel) && !isFatal(modifiedDamage));
    }

    /**
     * Handle the damage reduction and XP gain from the Roll ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    public double rollCheck(double damage) {
        Player player = getPlayer();

        if (player.isSneaking() && Permissions.gracefulRoll(player)) {
            return gracefulRollCheck(damage);
        }

        double modifiedDamage = Acrobatics.calculateModifiedRollDamage(damage, Acrobatics.rollThreshold);

        if (!isFatal(modifiedDamage) && isSuccessfulRoll(Acrobatics.rollMaxChance, Acrobatics.rollMaxBonusLevel)) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Text"));
            applyXpGain(calculateRollXP(damage, true));

            return modifiedDamage;
        }
        else if (!isFatal(damage)) {
            applyXpGain(calculateRollXP(damage, false));
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

        if (!isFatal(modifiedDamage) && isSuccessfulRoll(Acrobatics.gracefulRollMaxChance, Acrobatics.gracefulRollMaxBonusLevel)) {
            getPlayer().sendMessage(LocaleLoader.getString("Acrobatics.Ability.Proc"));
            applyXpGain(calculateRollXP(damage, true));

            return modifiedDamage;
        }
        else if (!isFatal(damage)) {
            applyXpGain(calculateRollXP(damage, false));
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

        boolean sameLocation = (lastFallLocation != null && Misc.isNear(lastFallLocation, fallLocation, 2));

        fallTries = sameLocation ? fallTries + 1 : Math.max(fallTries - 1, 0);
        lastFallLocation = fallLocation;

        return fallTries > Config.getInstance().getAcrobaticsAFKMaxTries();
    }

    private boolean isSuccessfulRoll(double maxChance, int maxLevel) {
        return (maxChance / maxLevel) * Math.min(getSkillLevel(), maxLevel) > Misc.getRandom().nextInt(activationChance);
    }

    private boolean isFatal(double damage) {
        return getPlayer().getHealth() - damage < 1;
    }

    private float calculateRollXP(double damage, boolean isRoll) {
        ItemStack boots = getPlayer().getInventory().getBoots();
        float xp = (float) (damage * (isRoll ? Acrobatics.rollXpModifier : Acrobatics.fallXpModifier));

        if (boots != null && boots.containsEnchantment(Enchantment.PROTECTION_FALL)) {
            xp *= Acrobatics.featherFallXPModifier;
        }

        return xp;
    }

    /**
     * Handle the damage reduction and XP gain from the Dodge ability
     *
     * @param damager The entity that dealt the damage
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    public double dodge(Entity damager, double damage) {
        double modifiedDamage = Acrobatics.calculateModifiedDodgeDamage(damage, Acrobatics.dodgeDamageModifier);

        if (!canDodge(damager, modifiedDamage)) {
            return damage;
        }

        Player player = getPlayer();

        McMMOPlayerDodgeEvent event = new McMMOPlayerDodgeEvent(player, modifiedDamage, (float) (damage * Acrobatics.dodgeXpModifier));
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return damage;
        }

        if (event.shouldUseParticles()) {
            ParticleEffectUtils.playDodgeEffect(player);
        }

        if (mcMMOPlayer.useChatNotifications()) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Combat.Proc"));
        }

        // Why do we check respawn cooldown here?
        // No, seriously, why?
        if (SkillUtils.cooldownExpired(mcMMOPlayer.getRespawnATS(), Misc.PLAYER_RESPAWN_COOLDOWN_SECONDS)) {
            applyXpGain(event.getXpGained());
        }

        return event.getDamageTaken();
    }
}
