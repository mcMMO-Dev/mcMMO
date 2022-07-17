package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.SkillActivationPerkEvent;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PerksUtils {
    private static final int LUCKY_SKILL_ACTIVATION_CHANCE = 75;
    private static final int NORMAL_SKILL_ACTIVATION_CHANCE = 100;

    private PerksUtils() {}

    public static int handleCooldownPerks(Player player, int cooldown) {
        if (Permissions.halvedCooldowns(player)) {
            cooldown *= 0.5;
        }
        else if (Permissions.thirdedCooldowns(player)) {
            cooldown *= (2.0 / 3.0);
        }
        else if (Permissions.quarteredCooldowns(player)) {
            cooldown *= 0.75;
        }

        return cooldown;
    }

    public static int handleActivationPerks(Player player, int ticks, int maxTicks) {
        if (maxTicks != 0) {
            ticks = Math.min(ticks, maxTicks);
        }

        if (Permissions.twelveSecondActivationBoost(player)) {
            ticks += 12;
        }
        else if (Permissions.eightSecondActivationBoost(player)) {
            ticks += 8;
        }
        else if (Permissions.fourSecondActivationBoost(player)) {
            ticks += 4;
        }

        final SkillActivationPerkEvent skillActivationPerkEvent = new SkillActivationPerkEvent(player, ticks, maxTicks);
        Bukkit.getPluginManager().callEvent(skillActivationPerkEvent);
        return skillActivationPerkEvent.getTicks();
    }

    public static float handleXpPerks(Player player, float xp, PrimarySkillType skill) {
        double modifier = 1.0F;

        if (Permissions.customXpBoost(player, skill)) {
            if(UserManager.getPlayer(player) != null && UserManager.getPlayer(player).isDebugMode()) {
                player.sendMessage(ChatColor.GOLD + "[DEBUG] " + ChatColor.DARK_GRAY + "XP Perk Multiplier IS CUSTOM! ");
            }

             modifier = ExperienceConfig.getInstance().getCustomXpPerkBoost();
        }
        else if (Permissions.quadrupleXp(player, skill)) {
            modifier = 4;
        }
        else if (Permissions.tripleXp(player, skill)) {
            modifier = 3;
        }
        else if (Permissions.doubleAndOneHalfXp(player, skill)) {
            modifier = 2.5;
        }
        else if (Permissions.doubleXp(player, skill)) {
            modifier = 2;
        }
        else if (Permissions.oneAndOneHalfXp(player, skill)) {
            modifier = 1.5;
        }
        else if (Permissions.oneAndAQuarterXp(player, skill)) {
            modifier = 1.25;
        }
        else if (Permissions.oneAndOneTenthXp(player, skill)) {
            modifier = 1.1;
        }

        float modifiedXP = (float) (xp * modifier);

        if(UserManager.getPlayer(player) != null && UserManager.getPlayer(player).isDebugMode()) {
            player.sendMessage(ChatColor.GOLD + "[DEBUG] " + ChatColor.RESET + "XP Perk Multiplier - " + ChatColor.GOLD + modifier);
            player.sendMessage(ChatColor.GOLD + "[DEBUG] " + ChatColor.RESET + "Original XP before perk boosts " + ChatColor.RED + (double) xp);
            player.sendMessage(ChatColor.GOLD + "[DEBUG] " + ChatColor.RESET + "XP AFTER PERKS " + ChatColor.DARK_RED + modifiedXP);
        }

        return modifiedXP;
    }

    /**
     * Calculate activation chance for a skill.
     *
     * @param player Player to check the activation chance for
     * @param skill PrimarySkillType to check the activation chance of
     * @return the activation chance with "lucky perk" accounted for
     */
    public static int handleLuckyPerks(Player player, PrimarySkillType skill) {
        if (Permissions.lucky(player, skill)) {
            return LUCKY_SKILL_ACTIVATION_CHANCE;
        }

        return NORMAL_SKILL_ACTIVATION_CHANCE;
    }
}
