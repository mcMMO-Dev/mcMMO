package com.gmail.nossr50.api;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public final class ExperienceAPI {
    private ExperienceAPI() {}

    /**
     * Adds raw XP to the player.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     * @deprecated Use {@link #addRawXP(Player, String, int)} instead
     */
    @Deprecated
    public static void addRawXP(Player player, SkillType skillType, int XP) {
        UserManager.getPlayer(player).applyXpGain(skillType, XP);
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
    public static void addRawXP(Player player, String skillType, int XP) {
        UserManager.getPlayer(player).applyXpGain(SkillType.getSkill(skillType), XP);
    }

    /**
     * Adds XP to the player, calculates for XP Rate only.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     * @deprecated Use {@link #addMultipliedXP(Player, String, int)} instead
     */
    @Deprecated
    public static void addMultipliedXP(Player player, SkillType skillType, int XP) {
        UserManager.getPlayer(player).applyXpGain(skillType, (int) (XP * Config.getInstance().getExperienceGainsGlobalMultiplier()));
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
    public static void addMultipliedXP(Player player, String skillType, int XP) {
        UserManager.getPlayer(player).applyXpGain(SkillType.getSkill(skillType), (int) (XP * Config.getInstance().getExperienceGainsGlobalMultiplier()));
    }

    /**
     * Adds XP to the player, calculates for XP Rate, skill modifiers and perks. May be shared with the party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     * @deprecated Use {@link #addXP(Player, String, int)} instead
     */
    @Deprecated
    public static void addXP(Player player, SkillType skillType, int XP) {
        UserManager.getPlayer(player).beginXpGain(skillType, XP);
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
    public static void addXP(Player player, String skillType, int XP) {
        UserManager.getPlayer(player).beginXpGain(SkillType.getSkill(skillType), XP);
    }

    /**
     * Get the amount of XP a player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP in a given skill
     * @deprecated Use {@link #getXP(Player, String)} instead
     */
    @Deprecated
    public static int getXP(Player player, SkillType skillType) {
        return UserManager.getPlayer(player).getProfile().getSkillXpLevel(skillType);
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
    public static int getXP(Player player, String skillType) {
        return UserManager.getPlayer(player).getProfile().getSkillXpLevel(SkillType.getSkill(skillType));
    }

    /**
     * Get the amount of XP left before leveling up.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get the XP amount for
     * @param skillType The skill to get the XP amount for
     * @return the amount of XP left before leveling up a specifc skill
     * @deprecated Use {@link #getXPToNextLevel(Player, String)} instead
     */
    @Deprecated
    public static int getXPToNextLevel(Player player, SkillType skillType) {
        return UserManager.getPlayer(player).getProfile().getXpToLevel(skillType);
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
    public static int getXPToNextLevel(Player player, String skillType) {
        return UserManager.getPlayer(player).getProfile().getXpToLevel(SkillType.getSkill(skillType));
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
        UserManager.getProfile(player).addLevels(skillType, levels);

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
     * @deprecated Use {@link #addLevel(Player, String, int)} instead
     */
    @Deprecated
    public static void addLevel(Player player, SkillType skillType, int levels) {
        UserManager.getPlayer(player).getProfile().addLevels(skillType, levels);
    }

    /**
     * Add levels to a skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add levels to
     * @param skillType Type of skill to add levels to
     * @param levels Number of levels to add
     */
    public static void addLevel(Player player, String skillType, int levels) {
        UserManager.getPlayer(player).getProfile().addLevels(SkillType.getSkill(skillType), levels);
    }

    /**
     * Get the level a player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get the level for
     * @param skillType The skill to get the level for
     * @return the level of a given skill
     * @deprecated Use {@link #getLevel(Player, String)} instead
     */
    @Deprecated
    public static int getLevel(Player player, SkillType skillType) {
        return UserManager.getPlayer(player).getProfile().getSkillLevel(skillType);
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
    public static int getLevel(Player player, String skillType) {
        return UserManager.getPlayer(player).getProfile().getSkillLevel(SkillType.getSkill(skillType));
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
        return UserManager.getPlayer(player).getPowerLevel();
    }

    /**
     * Get the level cap of a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param skillType The skill to get the level cap for
     * @return the level cap of a given skill
     */
    public static int getLevelCap(String skillType) {
        return Config.getInstance().getLevelCap(SkillType.getSkill(skillType));
    }

    /**
     * Get the power level cap.
     * </br>
     * This function is designed for API usage.
     *
     * @return the power level cap of a given skill
     */
    public static int getPowerLevelCap() {
        return Config.getInstance().getPowerLevelCap();
    }

    /**
     * Sets the level of a player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to set the level of
     * @param skillType The skill to set the level for
     * @param skillLevel The value to set the level to
     * @deprecated Use {@link #setLevel(Player, String, int)} instead
     */
    @Deprecated
    public static void setLevel(Player player, SkillType skillType, int skillLevel) {
        UserManager.getPlayer(player).getProfile().modifySkill(skillType, skillLevel);
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
    public static void setLevel(Player player, String skillType, int skillLevel) {
        UserManager.getPlayer(player).getProfile().modifySkill(SkillType.getSkill(skillType), skillLevel);
    }

    /**
     * Sets the XP of a player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to set the XP of
     * @param skillType The skill to set the XP for
     * @param newValue The value to set the XP to
     * @deprecated Use {@link #setXP(Player, String, int)} instead
     */
    @Deprecated
    public static void setXP(Player player, SkillType skillType, int newValue) {
        UserManager.getPlayer(player).getProfile().setSkillXpLevel(skillType, newValue);
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
    public static void setXP(Player player, String skillType, int newValue) {
        UserManager.getPlayer(player).getProfile().setSkillXpLevel(SkillType.getSkill(skillType), newValue);
    }

    /**
     * Removes XP from a player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to change the XP of
     * @param skillType The skill to change the XP for
     * @param xp The amount of XP to remove
     * @deprecated Use {@link #removeXP(Player, String, int)} instead
     */
    @Deprecated
    public static void removeXP(Player player, SkillType skillType, int xp) {
        UserManager.getPlayer(player).getProfile().removeXp(skillType, xp);
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
    public static void removeXP(Player player, String skillType, int xp) {
        UserManager.getPlayer(player).getProfile().removeXp(SkillType.getSkill(skillType), xp);
    }

    /**
     * Check the XP of a player. This should be called after giving XP to process level-ups.
     *
     * @param player The player to check
     * @param skillType The skill to check
     * @deprecated Calling this function is no longer needed and should be avoided
     */
    @Deprecated
    private static void checkXP(Player player, SkillType skillType) {
        SkillUtils.xpCheckSkill(skillType, player, UserManager.getProfile(player));
    }
}
