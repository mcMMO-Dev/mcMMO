package com.gmail.nossr50.util.skills;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.experience.ExperienceConfig;
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

        return ticks;
    }

    public static float handleXpPerks(Player player, float xp, SkillType skill) {
        if (Permissions.customXpBoost(player, skill)) {
            xp *= ExperienceConfig.getInstance().getCustomXpPerkBoost();
        }
        else if (Permissions.quadrupleXp(player, skill)) {
            xp *= 4;
        }
        else if (Permissions.tripleXp(player, skill)) {
            xp *= 3;
        }
        else if (Permissions.doubleAndOneHalfXp(player, skill)) {
            xp *= 2.5;
        }
        else if (Permissions.doubleXp(player, skill)) {
            xp *= 2;
        }
        else if (Permissions.oneAndOneHalfXp(player, skill)) {
            xp *= 1.5;
        }
        else if (Permissions.oneAndOneTenthXp(player, skill)) {
            xp *= 1.1;
        }

        return xp;
    }

    /**
     * Calculate activation chance for a skill.
     *
     * @param player Player to check the activation chance for
     * @param skill SkillType to check the activation chance of
     * @return the activation chance with "lucky perk" accounted for
     */
    public static int handleLuckyPerks(Player player, SkillType skill) {
        if (Permissions.lucky(player, skill)) {
            return LUCKY_SKILL_ACTIVATION_CHANCE;
        }

        return NORMAL_SKILL_ACTIVATION_CHANCE;
    }
}
