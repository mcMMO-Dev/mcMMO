package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

public final class PerksUtils {
    private static final int LUCKY_SKILL_ACTIVATION_CHANCE = 75;
    private static final int NORMAL_SKILL_ACTIVATION_CHANCE = 100;

    private PerksUtils() {
    }

    public static int handleCooldownPerks(Player player, int cooldown) {
        if (pluginRef.getPermissionTools().halvedCooldowns(player)) {
            cooldown *= 0.5;
        } else if (pluginRef.getPermissionTools().thirdedCooldowns(player)) {
            cooldown *= (2.0 / 3.0);
        } else if (pluginRef.getPermissionTools().quarteredCooldowns(player)) {
            cooldown *= 0.75;
        }

        return cooldown;
    }

    /**
     * Calculate activation chance for a skill.
     *
     * @param player Player to check the activation chance for
     * @param skill  PrimarySkillType to check the activation chance of
     * @return the activation chance with "lucky perk" accounted for
     */
    public static int handleLuckyPerks(Player player, PrimarySkillType skill) {
        if (pluginRef.getPermissionTools().lucky(player, skill)) {
            return LUCKY_SKILL_ACTIVATION_CHANCE;
        }

        return NORMAL_SKILL_ACTIVATION_CHANCE;
    }
}
