package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.PerksUtils;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public final class Acrobatics {
    public static double dodgeMaxChance = AdvancedConfig.getInstance().getDodgeChanceMax();
    public static int dodgeMaxBonusLevel = AdvancedConfig.getInstance().getDodgeMaxBonusLevel();
    public static int dodgeXpModifier = AdvancedConfig.getInstance().getDodgeXPModifier();
    public static int dodgeDamageModifier = AdvancedConfig.getInstance().getDodgeDamageModifier();

    public static double rollMaxChance = AdvancedConfig.getInstance().getRollChanceMax();
    public static int rollMaxBonusLevel = AdvancedConfig.getInstance().getRollMaxBonusLevel();
    public static int rollThreshold = AdvancedConfig.getInstance().getRollDamageThreshold();

    public static double gracefulRollMaxChance = AdvancedConfig.getInstance().getGracefulRollChanceMax();
    public static int gracefulRollMaxBonusLevel = AdvancedConfig.getInstance().getGracefulRollMaxBonusLevel();
    public static int gracefulRollThreshold = AdvancedConfig.getInstance().getGracefulRollDamageThreshold();
    public static int gracefulRollSuccessModifier = AdvancedConfig.getInstance().getGracefulRollSuccessModifer();

    public static int rollXpModifier = AdvancedConfig.getInstance().getRollXPModifier();
    public static int fallXpModifier = AdvancedConfig.getInstance().getFallXPModifier();

    public static boolean afkLevelingDisabled = Config.getInstance().getAcrobaticsAFKDisabled();
    public static boolean dodgeLightningDisabled = Config.getInstance().getDodgeLightningDisabled();

    private Acrobatics() {};

    public static boolean canRoll(Player player) {
        return (player.getItemInHand().getType() != Material.ENDER_PEARL) && !(afkLevelingDisabled && player.isInsideVehicle()) && Permissions.roll(player);
    }

    public static boolean canDodge(Player player, Entity damager) {
        if (Permissions.dodge(player)) {
            if (damager instanceof Player && SkillType.ACROBATICS.getPVPEnabled()) {
                return true;
            }
            else if (!(damager instanceof Player) && SkillType.ACROBATICS.getPVEEnabled() && !(damager instanceof LightningStrike && Acrobatics.dodgeLightningDisabled)) {
                return true;
            }
        }

        return false;
    }

    public static int processRoll(Player player, int damage) {
        if (player.isSneaking() && Permissions.gracefulRoll(player)) {
            return processGracefulRoll(player, damage);
        }

        int modifiedDamage = calculateModifiedRollDamage(damage, rollThreshold);

        if (!isFatal(player, modifiedDamage) && isSuccessfulRoll(player, rollMaxChance, rollMaxBonusLevel, 1)) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Text"));
            applyXpGain(player, damage, rollXpModifier);

            return modifiedDamage;
        }
        else if (!isFatal(player, damage)) {
            applyXpGain(player, damage, fallXpModifier);
        }

        return damage;
    }

    private static int processGracefulRoll(Player player, int damage) {
        int modifiedDamage = calculateModifiedRollDamage(damage, gracefulRollThreshold);

        if (!isFatal(player, modifiedDamage) && isSuccessfulRoll(player, gracefulRollMaxChance, gracefulRollMaxBonusLevel, gracefulRollSuccessModifier)) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Ability.Proc"));
            applyXpGain(player, damage, rollXpModifier);

            return modifiedDamage;
        }
        else if (!isFatal(player, damage)) {
            applyXpGain(player, damage, fallXpModifier);
        }

        return damage;
    }

    protected static boolean isFatal(Player player, int damage) {
        return player.getHealth() - damage < 1;
    }

    protected static int calculateModifiedDodgeDamage(int damage, int damageModifier) {
        return Math.max(damage / damageModifier, 1);
    }

    protected static int calculateModifiedRollDamage(int damage, int damageThreshold) {
        return Math.max(damage - damageThreshold, 0);
    }

    protected static boolean isSuccessfulRoll(Player player, double maxChance, int maxLevel, int successModifier) {
        double successChance = (maxChance / maxLevel) * Math.min(Users.getPlayer(player).getProfile().getSkillLevel(SkillType.ACROBATICS), maxLevel) * successModifier;

        return successChance > Misc.getRandom().nextInt(PerksUtils.handleLuckyPerks(player, SkillType.ACROBATICS));
    }

    private static void applyXpGain(Player player, int baseXp, int multiplier) {
        Users.getPlayer(player).beginXpGain(SkillType.ACROBATICS, baseXp * multiplier);
    }
}