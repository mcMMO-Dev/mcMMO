package com.gmail.nossr50.api;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Users;

public final class ExperienceAPI {
    private ExperienceAPI() {}

    /**
     * Check the XP of a player. This should be called after giving XP to process level-ups.
     *
     * @param player The player to check
     * @param skillType The skill to check
     * @deprecated Calling this function is no longer needed and should be avoided
     */
    private static void checkXP(Player player, SkillType skillType) {
        if (skillType.equals(SkillType.ALL)) {
            SkillTools.xpCheckAll(player, Users.getProfile(player));
        }
        else {
            SkillTools.xpCheckSkill(skillType, player, Users.getProfile(player));
        }
    }

    /**
     * Adds raw XP to the player.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     */
    public static void addRawXP(Player player, SkillType skillType, int XP) {
        Users.getPlayer(player).applyXpGain(skillType, XP);
    }

    /**
     * Adds XP to the player, calculates for XP Rate only.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     */
    public static void addMultipliedXP(Player player, SkillType skillType, int XP) {
        Users.getPlayer(player).applyXpGain(skillType, (int) (XP * Config.getInstance().getExperienceGainsGlobalMultiplier()));
    }

    /**
     * Adds XP to the player, calculates for XP Rate, skill modifiers and perks. May be shared with the party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     */
    public static void addXP(Player player, SkillType skillType, int XP) {
        Users.getPlayer(player).beginXpGain(skillType, XP);
    }

    /**
     * Get the amount of XP a player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP in a given skill
     */
    public static int getXP(Player player, SkillType skillType) {
        return Users.getProfile(player).getSkillXpLevel(skillType);
    }

    /**
     * Get the amount of XP left before leveling up.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get the XP amount for
     * @param skillType The skill to get the XP amount for
     * @return the amount of XP left before leveling up a specifc skill
     */
    public static int getXPToNextLevel(Player player, SkillType skillType) {
        return Users.getProfile(player).getXpToLevel(skillType);
    }

    /**
     * Add levels to a skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add levels to
     * @param skillType Type of skill to add levels to
     * @param levels Number of levels to add
     * @param notify Unused argument
     * @deprecated Use addLevel(Player, SKillType, int) instead
     */
    public static void addLevel(Player player, SkillType skillType, int levels, boolean notify) {
        Users.getProfile(player).addLevels(skillType, levels);

        if (notify) {
            checkXP(player, skillType);
        }
    }

    /**
     * Add levels to a skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add levels to
     * @param skillType Type of skill to add levels to
     * @param levels Number of levels to add
     * @param notify True if this should fire a level up notification, false otherwise.
     */
    public static void addLevel(Player player, SkillType skillType, int levels) {
        Users.getProfile(player).addLevels(skillType, levels);
    }

    /**
     * Get the level a player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get the level for
     * @param skillType The skill to get the level for
     * @return the level of a given skill
     */
    public static int getLevel(Player player, SkillType skillType) {
        return Users.getProfile(player).getSkillLevel(skillType);
    }

    /**
     * Gets the power level of a player.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get the power level for
     * @return the power level of the player
     */
    public static int getPowerLevel(Player player) {
        return Users.getPlayer(player).getPowerLevel();
    }

    /**
     * Sets the level of a player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to set the level of
     * @param skillType The skill to set the level for
     * @param skillLevel The value to set the level to
     */
    public static void setLevel(Player player, SkillType skillType, int skillLevel) {
        Users.getProfile(player).modifySkill(skillType, skillLevel);
    }

    /**
     * Sets the XP of a player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to set the XP of
     * @param skillType The skill to set the XP for
     * @param newValue The value to set the XP to
     */
    public static void setXP(Player player, SkillType skillType, int newValue) {
        Users.getProfile(player).setSkillXpLevel(skillType, newValue);
    }

    /**
     * Removes XP from a player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to change the XP of
     * @param skillType The skill to change the XP for
     * @param xp The amount of XP to remove
     */
    public static void removeXP(Player player, SkillType skillType, int xp) {
        Users.getProfile(player).removeXp(skillType, xp);
    }
}
