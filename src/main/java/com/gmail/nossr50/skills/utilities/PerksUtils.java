package com.gmail.nossr50.skills.utilities;

import org.bukkit.entity.Player;

import com.gmail.nossr50.util.Permissions;

public final class PerksUtils {
    private static final int LUCKY_SKILL_ACTIVATION_CHANCE = 75;
    private static final int NORMAL_SKILL_ACTIVATION_CHANCE = 100;

    private PerksUtils() {};

    public static int handleCooldownPerks(Player player, int cooldown) {
        if (Permissions.cooldownsHalved(player)) {
            cooldown *= 0.5;
        }
        else if (Permissions.cooldownsThirded(player)) {
            cooldown *= (1.0 / 3.0);
        }
        else if (Permissions.cooldownsQuartered(player)) {
            cooldown *= 0.75;
        }

        return cooldown;
    }

    public static int handleActivationPerks(Player player, int ticks, int maxTicks) {
        if (Permissions.activationTwelve(player)) {
            ticks += 12;
        }
        else if (Permissions.activationEight(player)) {
            ticks += 8;
        }
        else if (Permissions.activationFour(player)) {
            ticks += 4;
        }

        if (maxTicks != 0 && ticks > maxTicks) {
            ticks = maxTicks;
        }

        return ticks;
    }

    public static int handleXpPerks(Player player, int xp) {
        if (player.hasPermission("mcmmo.perks.xp.quadruple")) {
            xp *= 4;
        }
        else if (player.hasPermission("mcmmo.perks.xp.triple")) {
            xp *= 3;
        }
        else if (player.hasPermission("mcmmo.perks.xp.150percentboost")) {
            xp *= 2.5;
        }
        else if (player.hasPermission("mcmmo.perks.xp.150percentboost")) {
            xp *= 2;
        }
        else if (player.hasPermission("mcmmo.perks.xp.50percentboost")) {
            xp *= 1.5;
        }

        return xp;
    }

    /**
     * Calculate activation chance for a skill.
     *
     * @param isLucky true if the player has the appropriate "lucky" perk, false otherwise
     * @return the activation chance
     */
    public static int handleLuckyPerks(boolean isLucky) {
        if (isLucky) {
            return LUCKY_SKILL_ACTIVATION_CHANCE;
        }
    
        return NORMAL_SKILL_ACTIVATION_CHANCE;
    }
}
