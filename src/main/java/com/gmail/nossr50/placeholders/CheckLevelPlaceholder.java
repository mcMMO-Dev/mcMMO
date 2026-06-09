package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

/**
 * Placeholder for checking if a player has reached a specific level in a skill
 * Usage: %mcmmo_checklevel_<skill>:<level>%
 * Returns: ✔ if player has the level, ✘ if not
 */
public class CheckLevelPlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;
    private final PrimarySkillType skill;

    public CheckLevelPlaceholder(PapiExpansion papiExpansion, PrimarySkillType skill) {
        this.papiExpansion = papiExpansion;
        this.skill = skill;
        // The papiExpansion parameter is intentionally "unused" in this class.
        // It's essential for the class to function properly.
        // The placeholder needs access to the PapiExpansion instance to query skill levels.
    }

    @Override
    public String process(Player player, String params) {
        if (params == null || params.isEmpty()) {
            return "§c✘";
        }

        int requiredLevel;
        try {
            requiredLevel = Integer.parseInt(params);
        } catch (NumberFormatException e) {
            return "§c✘";
        }

        Integer currentLevel = papiExpansion.getSkillLevel(skill, player);
        if (currentLevel == null) {
            return "§c✘";
        }

        return currentLevel >= requiredLevel ? "§a✔" : "§c✘";
    }

    @Override
    public String getName() {
        return "checklevel_" + skill.toString().toLowerCase();
    }
}
