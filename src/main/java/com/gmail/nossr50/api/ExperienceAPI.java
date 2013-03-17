package com.gmail.nossr50.api;

import java.util.Set;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.player.UserManager;

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
     */
    public static void addRawXP(Player player, String skillType, int XP) {
        UserManager.getPlayer(player).applyXpGain(SkillType.getSkill(skillType), XP);
    }

    /**
     * Adds raw XP to an offline player.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     *
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void addRawXPOffline(String playerName, String skillType, int XP) {
        addOfflineXP(playerName, skillType, XP);
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
     * Adds XP to an offline player, calculates for XP Rate only.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     *
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void addMultipliedXPOffline(String playerName, String skillType, int XP) {
        addOfflineXP(playerName, skillType, (int) (XP * Config.getInstance().getExperienceGainsGlobalMultiplier()));
    }

    /**
     * Adds XP to the player, calculates for XP Rate and skill modifier.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     */
    public static void addModifiedXP(Player player, String skillType, int XP) {
        SkillType skill = SkillType.getSkill(skillType);

        UserManager.getPlayer(player).applyXpGain(skill, (int) (XP  / skill.getXpModifier() * Config.getInstance().getExperienceGainsGlobalMultiplier()));
    }

    /**
     * Adds XP to an offline player, calculates for XP Rate and skill modifier.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     *
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void addModifiedXPOffline(String playerName, String skillType, int XP) {
        addOfflineXP(playerName, skillType, (int) (XP / SkillType.getSkill(skillType).getXpModifier() * Config.getInstance().getExperienceGainsGlobalMultiplier()));
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
     */
    public static int getXP(Player player, String skillType) {
        return UserManager.getPlayer(player).getProfile().getSkillXpLevel(SkillType.getSkill(skillType));
    }

    /**
     * Get the amount of XP an offline player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP in a given skill
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static int getOfflineXP(String playerName, String skillType) {
        return getOfflineProfile(playerName).getSkillXpLevel(SkillType.getSkill(skillType));
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
     * Get the amount of XP an offline player has left before leveling up.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP in a given skill
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static int getOfflineXPToNextLevel(String playerName, String skillType) {
        return getOfflineProfile(playerName).getXpToLevel(SkillType.getSkill(skillType));
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
     * Add levels to a skill for an offline player.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to add levels to
     * @param skillType Type of skill to add levels to
     * @param levels Number of levels to add
     *
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void addLevelOffline(String playerName, String skillType, int levels) {
        PlayerProfile profile = getOfflineProfile(playerName);

        SkillType skill = SkillType.getSkill(skillType);

        if (skill.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skill);

            for (SkillType parentSkill : parentSkills) {
                profile.addLevels(parentSkill, (levels / parentSkills.size()));
            }

            profile.save();
            return;
        }

        profile.addLevels(skill, levels);
        profile.save();
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
     * Get the level an offline player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to get the level for
     * @param skillType The skill to get the level for
     * @return the level of a given skill
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static int getLevelOffline(String playerName, String skillType) {
        return getOfflineProfile(playerName).getSkillLevel(SkillType.getSkill(skillType));
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
     * Gets the power level of an offline player.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to get the power level for
     * @return the power level of the player
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static int getPowerLevelOffline(String playerName) {
        int powerLevel = 0;
        PlayerProfile profile = getOfflineProfile(playerName);

        for (SkillType type : SkillType.values()) {
            if (type.isChildSkill()) {
                continue;
            }

            powerLevel += profile.getSkillLevel(type);
        }

        return powerLevel;
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
     */
    public static void setLevel(Player player, String skillType, int skillLevel) {
        UserManager.getPlayer(player).getProfile().modifySkill(SkillType.getSkill(skillType), skillLevel);
    }

    /**
     * Sets the level of an offline player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to set the level of
     * @param skillType The skill to set the level for
     * @param skillLevel The value to set the level to
     *
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void setLevelOffline(String playerName, String skillType, int skillLevel) {
        getOfflineProfile(playerName).modifySkill(SkillType.getSkill(skillType), skillLevel);
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
     * Sets the XP of an offline player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to set the XP of
     * @param skillType The skill to set the XP for
     * @param newValue The value to set the XP to
     *
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void setXPOffline(String playerName, String skillType, int newValue) {
        getOfflineProfile(playerName).setSkillXpLevel(SkillType.getSkill(skillType), newValue);
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
     * Removes XP from an offline player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to change the XP of
     * @param skillType The skill to change the XP for
     * @param xp The amount of XP to remove
     *
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void removeXPOffline(String playerName, String skillType, int xp) {
        getOfflineProfile(playerName).removeXp(SkillType.getSkill(skillType), xp);
    }

    /**
     * Add XP to an offline player.
     *
     * @param playerName The player to check
     * @param skillType The skill to check
     * @param XP The amount of XP to award.
     */
    private static void addOfflineXP(String playerName, String skillType, int XP) {
        PlayerProfile profile = getOfflineProfile(playerName);

        SkillType skill = SkillType.getSkill(skillType);

        if (skill.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skill);

            for (SkillType parentSkill : parentSkills) {
                profile.setSkillXpLevel(parentSkill, profile.getSkillLevel(parentSkill) + (XP / parentSkills.size()));
            }

            profile.save();
            return;
        }

        profile.setSkillXpLevel(skill, profile.getSkillXpLevel(skill) + XP);
        profile.save();
    }

    private static PlayerProfile getOfflineProfile(String playerName) {
        PlayerProfile profile = new PlayerProfile(playerName, false);

        if (!profile.isLoaded()) {
            throw new InvalidPlayerException();
        }

        return profile;
    }
}
