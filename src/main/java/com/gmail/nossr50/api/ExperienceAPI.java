package com.gmail.nossr50.api;

import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.Skills;

public class ExperienceAPI {

    /**
     * Check the XP of a player. This should be called after giving XP to process level-ups.
     *
     * @param player The player to check
     * @param skillType The skill to check
     */
    private void checkXP(Player player, SkillType skillType) {
        if (skillType.equals(SkillType.ALL)) {
            Skills.XpCheckAll(player);
        }
        else {
            Skills.XpCheckSkill(skillType, player);
        }
    }

    /**
     * Adds XP to the player, doesn't calculate for XP Rate or other modifiers.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     */
    public void addRawXP(Player player, SkillType skillType, int XP) {
        Users.getProfile(player).addXPOverride(skillType, XP);
        checkXP(player, skillType);
    }

    /**
     * Adds XP to the player, calculates for XP Rate but not skill modifiers.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     */
    public void addMultipliedXP(Player player, SkillType skillType, int XP) {
        Users.getProfile(player).addXPOverrideBonus(skillType, XP);
        checkXP(player, skillType);
    }

    /**
     * Adds XP to the player, calculates for XP Rate and skill modifiers.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     */
    public void addXP(Player player, SkillType skillType, int XP) {
        Users.getProfile(player).addXP(skillType, XP);
        checkXP(player, skillType);
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
    public int getXP(Player player, SkillType skillType) {
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
    public int getXPToNextLevel(Player player, SkillType skillType) {
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
     * @param notify True if this should fire a level up notification, false otherwise.
     */
    public void addLevel(Player player, SkillType skillType, int levels, boolean notify) {
        Users.getProfile(player).addLevels(skillType, levels);

        if (notify) {
            checkXP(player, skillType);
        }
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
    public int getLevel(Player player, SkillType skillType) {
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
    public int getPowerLevel(Player player) {
        return Users.getProfile(player).getPowerLevel();
    }
}
