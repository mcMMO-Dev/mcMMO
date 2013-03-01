package com.gmail.nossr50.util.skills;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.Permissions;

public final class PerksUtils {
    private static final int LUCKY_SKILL_ACTIVATION_CHANCE = 75;
    private static final int NORMAL_SKILL_ACTIVATION_CHANCE = 100;

    private PerksUtils() {};

    public static int handleCooldownPerks(Player player, int cooldown) {
        if (Permissions.halvedCooldowns(player)) {
            cooldown *= 0.5;
        }
        else if (Permissions.thirdedCooldowns(player)) {
            cooldown *= (1.0 / 3.0);
        }
        else if (Permissions.quarteredCooldowns(player)) {
            cooldown *= 0.75;
        }

        return cooldown;
    }

    public static int handleActivationPerks(Player player, int ticks, int maxTicks) {
        if (Permissions.twelveSecondActivationBoost(player)) {
            ticks += 12;
        }
        else if (Permissions.eightSecondActivationBoost(player)) {
            ticks += 8;
        }
        else if (Permissions.fourSecondActivationBoost(player)) {
            ticks += 4;
        }

        if (maxTicks != 0 && ticks > maxTicks) {
            ticks = maxTicks;
        }

        return ticks;
    }

    public static int handleXpPerks(Player player, int xp) {
        if (Permissions.quadrupleXp(player)) {
            xp *= 4;
        }
        else if (Permissions.tripleXp(player)) {
            xp *= 3;
        }
        else if (Permissions.doubleAndOneHalfXp(player)) {
            xp *= 2.5;
        }
        else if (Permissions.doubleXp(player)) {
            xp *= 2;
        }
        else if (Permissions.oneAndOneHalfXp(player)) {
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
    public static int handleLuckyPerks(Player player, SkillType skill) {
        if (Permissions.lucky(player, skill)) {
            return LUCKY_SKILL_ACTIVATION_CHANCE;
        }

        return NORMAL_SKILL_ACTIVATION_CHANCE;
    }
}
