package com.gmail.nossr50.api;

import java.util.Set;

import org.bukkit.entity.Player;

import com.gmail.nossr50.api.exceptions.InvalidPlayerException;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
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
     *
     * @throws InvalidSkillException if the given skill is not valid
     */
    public static void addRawXP(Player player, String skillType, int XP) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        UserManager.getPlayer(player).applyXpGain(skill, XP);
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
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void addRawXPOffline(String playerName, String skillType, int XP) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        addOfflineXP(playerName, skill, XP);
    }

    /**
     * Adds XP to the player, calculates for XP Rate only.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     *
     * @throws InvalidSkillException if the given skill is not valid
     */
    public static void addMultipliedXP(Player player, String skillType, int XP) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        UserManager.getPlayer(player).applyXpGain(skill, (int) (XP * Config.getInstance().getExperienceGainsGlobalMultiplier()));
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
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void addMultipliedXPOffline(String playerName, String skillType, int XP) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        addOfflineXP(playerName, skill, (int) (XP * Config.getInstance().getExperienceGainsGlobalMultiplier()));
    }

    /**
     * Adds XP to the player, calculates for XP Rate and skill modifier.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     *
     * @throws InvalidSkillException if the given skill is not valid
     */
    public static void addModifiedXP(Player player, String skillType, int XP) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

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
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void addModifiedXPOffline(String playerName, String skillType, int XP) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        addOfflineXP(playerName, skill, (int) (XP / skill.getXpModifier() * Config.getInstance().getExperienceGainsGlobalMultiplier()));
    }

    /**
     * Adds XP to the player, calculates for XP Rate, skill modifiers and perks. May be shared with the party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     *
     * @throws InvalidSkillException if the given skill is not valid
     */
    public static void addXP(Player player, String skillType, int XP) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        UserManager.getPlayer(player).beginXpGain(skill, XP);
    }

    /**
     * Get the amount of XP a player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP in a given skill
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static int getXP(Player player, String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        return UserManager.getPlayer(player).getProfile().getSkillXpLevel(skill);
    }

    /**
     * Get the amount of XP an offline player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP in a given skill
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static int getOfflineXP(String playerName, String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        return getOfflineProfile(playerName).getSkillXpLevel(skill);
    }

    /**
     * Get the raw amount of XP a player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP in a given skill
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static float getXPRaw(Player player, String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        return UserManager.getPlayer(player).getProfile().getSkillXpLevelRaw(skill);
    }

    /**
     * Get the raw amount of XP an offline player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP in a given skill
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static float getOfflineXPRaw(String playerName, String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        return getOfflineProfile(playerName).getSkillXpLevelRaw(skill);
    }

    /**
     * Get the total amount of XP needed to reach the next level.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get the XP amount for
     * @param skillType The skill to get the XP amount for
     * @return the total amount of XP needed to reach the next level
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static int getXPToNextLevel(Player player, String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        return UserManager.getPlayer(player).getProfile().getXpToLevel(skill);
    }

    /**
     * Get the total amount of XP an offline player needs to reach the next level.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to get XP for
     * @param skillType The skill to get XP for
     * @return the total amount of XP needed to reach the next level
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static int getOfflineXPToNextLevel(String playerName, String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        return getOfflineProfile(playerName).getXpToLevel(skill);
    }

    /**
     * Get the amount of XP remaining until the next level.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to get the XP amount for
     * @param skillType The skill to get the XP amount for
     * @return the amount of XP remaining until the next level
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static int getXPRemaining(Player player, String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        PlayerProfile profile = UserManager.getPlayer(player).getProfile();

        return profile.getXpToLevel(skill) - profile.getSkillXpLevel(skill);
    }

    /**
     * Get the amount of XP an offline player has left before leveling up.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP needed to reach the next level
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static int getOfflineXPRemaining(String playerName, String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        PlayerProfile profile = getOfflineProfile(playerName);

        return profile.getXpToLevel(skill) - profile.getSkillXpLevel(skill);
    }

    /**
     * Add levels to a skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add levels to
     * @param skillType Type of skill to add levels to
     * @param levels Number of levels to add
     *
     * @throws InvalidSkillException if the given skill is not valid
     */
    public static void addLevel(Player player, String skillType, int levels) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        UserManager.getPlayer(player).getProfile().addLevels(skill, levels);
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
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void addLevelOffline(String playerName, String skillType, int levels) {
        PlayerProfile profile = getOfflineProfile(playerName);
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

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
     *
     * @throws InvalidSkillException if the given skill is not valid
     */
    public static int getLevel(Player player, String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        return UserManager.getPlayer(player).getProfile().getSkillLevel(skill);
    }

    /**
     * Get the level an offline player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The player to get the level for
     * @param skillType The skill to get the level for
     * @return the level of a given skill
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static int getLevelOffline(String playerName, String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        return getOfflineProfile(playerName).getSkillLevel(skill);
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
     *
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
     *
     * @throws InvalidSkillException if the given skill is not valid
     */
    public static int getLevelCap(String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        return Config.getInstance().getLevelCap(skill);
    }

    /**
     * Get the power level cap.
     * </br>
     * This function is designed for API usage.
     *
     * @return the overall power level cap
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
     *
     * @throws InvalidSkillException if the given skill is not valid
     */
    public static void setLevel(Player player, String skillType, int skillLevel) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        UserManager.getPlayer(player).getProfile().modifySkill(skill, skillLevel);
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
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void setLevelOffline(String playerName, String skillType, int skillLevel) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        getOfflineProfile(playerName).modifySkill(skill, skillLevel);
    }

    /**
     * Sets the XP of a player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to set the XP of
     * @param skillType The skill to set the XP for
     * @param newValue The value to set the XP to
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static void setXP(Player player, String skillType, int newValue) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        UserManager.getPlayer(player).getProfile().setSkillXpLevel(skill, newValue);
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
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static void setXPOffline(String playerName, String skillType, int newValue) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        getOfflineProfile(playerName).setSkillXpLevel(skill, newValue);
    }

    /**
     * Removes XP from a player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to change the XP of
     * @param skillType The skill to change the XP for
     * @param xp The amount of XP to remove
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static void removeXP(Player player, String skillType, int xp) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        UserManager.getPlayer(player).getProfile().removeXp(skill, xp);
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
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static void removeXPOffline(String playerName, String skillType, int xp) {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        getOfflineProfile(playerName).removeXp(skill, xp);
    }

    /**
     * Add XP to an offline player.
     *
     * @param playerName The player to check
     * @param skillType The skill to check
     * @param XP The amount of XP to award.
     */
    private static void addOfflineXP(String playerName, SkillType skill, int XP) {
        PlayerProfile profile = getOfflineProfile(playerName);

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
