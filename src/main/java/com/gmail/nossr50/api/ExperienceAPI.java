package com.gmail.nossr50.api;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.exceptions.InvalidFormulaTypeException;
import com.gmail.nossr50.api.exceptions.InvalidPlayerException;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.api.exceptions.InvalidXPGainReasonException;
import com.gmail.nossr50.api.exceptions.McMMOPlayerNotFoundException;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.player.UserManager;

public final class ExperienceAPI {
    private ExperienceAPI() {}

    /**
     * Returns whether given string is a valid type of skill suitable for the
     * other API calls in this class.
     * </br>
     * This function is designed for API usage.
     *
     * @param skillType A string that may or may not be a skill
     * @return true if this is a valid mcMMO skill
     */
    public static boolean isValidSkillType(String skillType) {
        return SkillType.getSkill(skillType) != null;
    }

    /**
     * Returns whether the given skill type string is both valid and not a
     * child skill. (Child skills have no XP of their own, and their level is
     * derived from the parent(s).)
     * </br>
     * This function is designed for API usage.
     *
     * @param skillType the skill to check
     * @return true if this is a valid, non-child mcMMO skill
     */
    public static boolean isNonChildSkill(String skillType) {
        SkillType skill = SkillType.getSkill(skillType);

        return skill != null && !skill.isChildSkill();
    }

    @Deprecated
    public static void addRawXP(Player player, String skillType, int XP) {
        addRawXP(player, skillType, (float) XP);
    }

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
    @Deprecated
    public static void addRawXP(Player player, String skillType, float XP) {
        addRawXP(player, skillType, XP, "UNKNOWN");
    }

    /**
     * Adds raw XP to the player.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     * @param xpGainReason The reason to gain XP
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidXPGainReasonException if the given xpGainReason is not valid
     */
    public static void addRawXP(Player player, String skillType, float XP, String xpGainReason) {
        addRawXP(player, skillType, XP, xpGainReason, false);
    }

    /**
     * Adds raw XP to the player.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     * @param xpGainReason The reason to gain XP
     * @param isUnshared true if the XP cannot be shared with party members
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidXPGainReasonException if the given xpGainReason is not valid
     */
    public static void addRawXP(Player player, String skillType, float XP, String xpGainReason, boolean isUnshared) {
        if (isUnshared) {
            getPlayer(player).beginUnsharedXpGain(getSkillType(skillType), XP, getXPGainReason(xpGainReason));
            return;
        }

        getPlayer(player).applyXpGain(getSkillType(skillType), XP, getXPGainReason(xpGainReason));
    }

    /**
     * Adds raw XP to an offline player.
     * </br>
     * This function is designed for API usage.
     *
     * @deprecated We're using float for our XP values now
     * replaced by {@link #addRawXPOffline(String playerName, String skillType, float XP)}
     */
    @Deprecated
    public static void addRawXPOffline(String playerName, String skillType, int XP) {
        addRawXPOffline(playerName, skillType, (float) XP);
    }

    /**
     * Adds raw XP to an offline player.
     * </br>
     * This function is designed for API usage.
     *
     * @deprecated We're using uuids to get an offline player
     * replaced by {@link #addRawXPOffline(UUID uuid, String skillType, float XP)}
     *
     * @param playerName The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    @Deprecated
    public static void addRawXPOffline(String playerName, String skillType, float XP) {
        addOfflineXP(playerName, getSkillType(skillType), (int) Math.floor(XP));
    }

    /**
     * Adds raw XP to an offline player.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The UUID of player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void addRawXPOffline(UUID uuid, String skillType, float XP) {
        addOfflineXP(uuid, getSkillType(skillType), (int) Math.floor(XP));
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
    @Deprecated
    public static void addMultipliedXP(Player player, String skillType, int XP) {
        addMultipliedXP(player, skillType, XP, "UNKNOWN");
    }

    /**
     * Adds XP to the player, calculates for XP Rate only.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     * @param xpGainReason The reason to gain XP
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidXPGainReasonException if the given xpGainReason is not valid
     */
    public static void addMultipliedXP(Player player, String skillType, int XP, String xpGainReason) {
        getPlayer(player).applyXpGain(getSkillType(skillType), (int) (XP * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier()), getXPGainReason(xpGainReason));
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
    @Deprecated
    public static void addMultipliedXPOffline(String playerName, String skillType, int XP) {
        addOfflineXP(playerName, getSkillType(skillType), (int) (XP * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier()));
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
    @Deprecated
    public static void addModifiedXP(Player player, String skillType, int XP) {
        addModifiedXP(player, skillType, XP, "UNKNOWN");
    }

    /**
     * Adds XP to the player, calculates for XP Rate and skill modifier.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     * @param xpGainReason The reason to gain XP
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidXPGainReasonException if the given xpGainReason is not valid
     */
    public static void addModifiedXP(Player player, String skillType, int XP, String xpGainReason) {
        addModifiedXP(player, skillType, XP, xpGainReason, false);
    }

    /**
     * Adds XP to the player, calculates for XP Rate and skill modifier.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     * @param xpGainReason The reason to gain XP
     * @param isUnshared true if the XP cannot be shared with party members
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidXPGainReasonException if the given xpGainReason is not valid
     */
    public static void addModifiedXP(Player player, String skillType, int XP, String xpGainReason, boolean isUnshared) {
        SkillType skill = getSkillType(skillType);

        if (isUnshared) {
            getPlayer(player).beginUnsharedXpGain(skill, (int) (XP / skill.getXpModifier() * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier()), getXPGainReason(xpGainReason));
            return;
        }

        getPlayer(player).applyXpGain(skill, (int) (XP / skill.getXpModifier() * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier()), getXPGainReason(xpGainReason));
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
    @Deprecated
    public static void addModifiedXPOffline(String playerName, String skillType, int XP) {
        SkillType skill = getSkillType(skillType);

        addOfflineXP(playerName, skill, (int) (XP / skill.getXpModifier() * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier()));
    }

    /**
     * Adds XP to the player, calculates for XP Rate, skill modifiers, perks, child skills,
     * and party sharing.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     *
     * @throws InvalidSkillException if the given skill is not valid
     */
    @Deprecated
    public static void addXP(Player player, String skillType, int XP) {
        addXP(player, skillType, XP, "UNKNOWN");
    }

    /**
     * Adds XP to the player, calculates for XP Rate, skill modifiers, perks, child skills,
     * and party sharing.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     * @param xpGainReason The reason to gain XP
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidXPGainReasonException if the given xpGainReason is not valid
     */
    public static void addXP(Player player, String skillType, int XP, String xpGainReason) {
        addXP(player, skillType, XP, xpGainReason, false);
    }

    /**
     * Adds XP to the player, calculates for XP Rate, skill modifiers, perks, child skills,
     * and party sharing.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to add XP to
     * @param skillType The skill to add XP to
     * @param XP The amount of XP to add
     * @param xpGainReason The reason to gain XP
     * @param isUnshared true if the XP cannot be shared with party members
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidXPGainReasonException if the given xpGainReason is not valid
     */
    public static void addXP(Player player, String skillType, int XP, String xpGainReason, boolean isUnshared) {
        if (isUnshared) {
            getPlayer(player).beginUnsharedXpGain(getSkillType(skillType), XP, getXPGainReason(xpGainReason));
            return;
        }

        getPlayer(player).beginXpGain(getSkillType(skillType), XP, getXPGainReason(xpGainReason));
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
        return getPlayer(player).getSkillXpLevel(getNonChildSkillType(skillType));
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
    @Deprecated
    public static int getOfflineXP(String playerName, String skillType) {
        return getOfflineProfile(playerName).getSkillXpLevel(getNonChildSkillType(skillType));
    }

    /**
     * Get the amount of XP an offline player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP in a given skill
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static int getOfflineXP(UUID uuid, String skillType) {
        return getOfflineProfile(uuid).getSkillXpLevel(getNonChildSkillType(skillType));
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
        return getPlayer(player).getSkillXpLevelRaw(getNonChildSkillType(skillType));
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
    @Deprecated
    public static float getOfflineXPRaw(String playerName, String skillType) {
        return getOfflineProfile(playerName).getSkillXpLevelRaw(getNonChildSkillType(skillType));
    }

    /**
     * Get the raw amount of XP an offline player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP in a given skill
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static float getOfflineXPRaw(UUID uuid, String skillType) {
        return getOfflineProfile(uuid).getSkillXpLevelRaw(getNonChildSkillType(skillType));
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
        return getPlayer(player).getXpToLevel(getNonChildSkillType(skillType));
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
    @Deprecated
    public static int getOfflineXPToNextLevel(String playerName, String skillType) {
        return getOfflineProfile(playerName).getXpToLevel(getNonChildSkillType(skillType));
    }

    /**
     * Get the total amount of XP an offline player needs to reach the next level.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The player to get XP for
     * @param skillType The skill to get XP for
     * @return the total amount of XP needed to reach the next level
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static int getOfflineXPToNextLevel(UUID uuid, String skillType) {
        return getOfflineProfile(uuid).getXpToLevel(getNonChildSkillType(skillType));
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
        SkillType skill = getNonChildSkillType(skillType);

        PlayerProfile profile = getPlayer(player).getProfile();

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
    @Deprecated
    public static int getOfflineXPRemaining(String playerName, String skillType) {
        SkillType skill = getNonChildSkillType(skillType);
        PlayerProfile profile = getOfflineProfile(playerName);

        return profile.getXpToLevel(skill) - profile.getSkillXpLevel(skill);
    }

    /**
     * Get the amount of XP an offline player has left before leveling up.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The player to get XP for
     * @param skillType The skill to get XP for
     * @return the amount of XP needed to reach the next level
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static float getOfflineXPRemaining(UUID uuid, String skillType) {
        SkillType skill = getNonChildSkillType(skillType);
        PlayerProfile profile = getOfflineProfile(uuid);

        return profile.getXpToLevel(skill) - profile.getSkillXpLevelRaw(skill);
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
        getPlayer(player).addLevels(getSkillType(skillType), levels);
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
    @Deprecated
    public static void addLevelOffline(String playerName, String skillType, int levels) {
        PlayerProfile profile = getOfflineProfile(playerName);
        SkillType skill = getSkillType(skillType);

        if (skill.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skill);

            for (SkillType parentSkill : parentSkills) {
                profile.addLevels(parentSkill, (levels / parentSkills.size()));
            }

            profile.scheduleAsyncSave();
            return;
        }

        profile.addLevels(skill, levels);
        profile.scheduleAsyncSave();
    }

    /**
     * Add levels to a skill for an offline player.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The player to add levels to
     * @param skillType Type of skill to add levels to
     * @param levels Number of levels to add
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void addLevelOffline(UUID uuid, String skillType, int levels) {
        PlayerProfile profile = getOfflineProfile(uuid);
        SkillType skill = getSkillType(skillType);

        if (skill.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skill);

            for (SkillType parentSkill : parentSkills) {
                profile.addLevels(parentSkill, (levels / parentSkills.size()));
            }

            profile.scheduleAsyncSave();
            return;
        }

        profile.addLevels(skill, levels);
        profile.scheduleAsyncSave();
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
        return getPlayer(player).getSkillLevel(getSkillType(skillType));
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
    @Deprecated
    public static int getLevelOffline(String playerName, String skillType) {
        return getOfflineProfile(playerName).getSkillLevel(getSkillType(skillType));
    }

    /**
     * Get the level an offline player has in a specific skill.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The player to get the level for
     * @param skillType The skill to get the level for
     * @return the level of a given skill
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static int getLevelOffline(UUID uuid, String skillType) {
        return getOfflineProfile(uuid).getSkillLevel(getSkillType(skillType));
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
        return getPlayer(player).getPowerLevel();
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
    @Deprecated
    public static int getPowerLevelOffline(String playerName) {
        int powerLevel = 0;
        PlayerProfile profile = getOfflineProfile(playerName);

        for (SkillType type : SkillType.NON_CHILD_SKILLS) {
            powerLevel += profile.getSkillLevel(type);
        }

        return powerLevel;
    }

    /**
     * Gets the power level of an offline player.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The player to get the power level for
     * @return the power level of the player
     *
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static int getPowerLevelOffline(UUID uuid) {
        int powerLevel = 0;
        PlayerProfile profile = getOfflineProfile(uuid);

        for (SkillType type : SkillType.NON_CHILD_SKILLS) {
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
        return Config.getInstance().getLevelCap(getSkillType(skillType));
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
     * Get the position on the leaderboard of a player.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The name of the player to check
     * @param skillType The skill to check
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     *
     * @return the position on the leaderboard
     */
    @Deprecated
    public static int getPlayerRankSkill(String playerName, String skillType) {
        return mcMMO.getDatabaseManager().readRank(getOfflineProfile(playerName).getPlayerName()).get(getNonChildSkillType(skillType));
    }

    /**
     * Get the position on the leaderboard of a player.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The name of the player to check
     * @param skillType The skill to check
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     *
     * @return the position on the leaderboard
     */
    public static int getPlayerRankSkill(UUID uuid, String skillType) {
        return mcMMO.getDatabaseManager().readRank(getOfflineProfile(uuid).getPlayerName()).get(getNonChildSkillType(skillType));
    }

    /**
     * Get the position on the power level leaderboard of a player.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName The name of the player to check
     *
     * @throws InvalidPlayerException if the given player does not exist in the database
     *
     * @return the position on the power level leaderboard
     */
    @Deprecated
    public static int getPlayerRankOverall(String playerName) {
        return mcMMO.getDatabaseManager().readRank(getOfflineProfile(playerName).getPlayerName()).get(null);
    }

    /**
     * Get the position on the power level leaderboard of a player.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The name of the player to check
     *
     * @throws InvalidPlayerException if the given player does not exist in the database
     *
     * @return the position on the power level leaderboard
     */
    public static int getPlayerRankOverall(UUID uuid) {
        return mcMMO.getDatabaseManager().readRank(getOfflineProfile(uuid).getPlayerName()).get(null);
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
        getPlayer(player).modifySkill(getSkillType(skillType), skillLevel);
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
    @Deprecated
    public static void setLevelOffline(String playerName, String skillType, int skillLevel) {
        getOfflineProfile(playerName).modifySkill(getSkillType(skillType), skillLevel);
    }

    /**
     * Sets the level of an offline player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The player to set the level of
     * @param skillType The skill to set the level for
     * @param skillLevel The value to set the level to
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     */
    public static void setLevelOffline(UUID uuid, String skillType, int skillLevel) {
        getOfflineProfile(uuid).modifySkill(getSkillType(skillType), skillLevel);
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
        getPlayer(player).setSkillXpLevel(getNonChildSkillType(skillType), newValue);
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
    @Deprecated
    public static void setXPOffline(String playerName, String skillType, int newValue) {
        getOfflineProfile(playerName).setSkillXpLevel(getNonChildSkillType(skillType), newValue);
    }

    /**
     * Sets the XP of an offline player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The player to set the XP of
     * @param skillType The skill to set the XP for
     * @param newValue The value to set the XP to
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static void setXPOffline(UUID uuid, String skillType, int newValue) {
        getOfflineProfile(uuid).setSkillXpLevel(getNonChildSkillType(skillType), newValue);
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
        getPlayer(player).removeXp(getNonChildSkillType(skillType), xp);
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
    @Deprecated
    public static void removeXPOffline(String playerName, String skillType, int xp) {
        getOfflineProfile(playerName).removeXp(getNonChildSkillType(skillType), xp);
    }

    /**
     * Removes XP from an offline player in a specific skill type.
     * </br>
     * This function is designed for API usage.
     *
     * @param uuid The player to change the XP of
     * @param skillType The skill to change the XP for
     * @param xp The amount of XP to remove
     *
     * @throws InvalidSkillException if the given skill is not valid
     * @throws InvalidPlayerException if the given player does not exist in the database
     * @throws UnsupportedOperationException if the given skill is a child skill
     */
    public static void removeXPOffline(UUID uuid, String skillType, int xp) {
        getOfflineProfile(uuid).removeXp(getNonChildSkillType(skillType), xp);
    }

    /**
     * Check how much XP is needed for a specific level with the selected level curve.
     * </br>
     * This function is designed for API usage.
     *
     * @param level The level to get the amount of XP for
     *
     * @throws InvalidFormulaTypeException if the given formulaType is not valid
     */
    public static int getXpNeededToLevel(int level) {
        return mcMMO.getFormulaManager().getCachedXpToLevel(level, ExperienceConfig.getInstance().getFormulaType());
    }

    /**
     * Check how much XP is needed for a specific level with the provided level curve.
     * </br>
     * This function is designed for API usage.
     *
     * @param level The level to get the amount of XP for
     * @param formulaType The formula type to get the amount of XP for
     *
     * @throws InvalidFormulaTypeException if the given formulaType is not valid
     */
    public static int getXpNeededToLevel(int level, String formulaType) {
        return mcMMO.getFormulaManager().getCachedXpToLevel(level, getFormulaType(formulaType));
    }

    // Utility methods follow.
    private static void addOfflineXP(UUID playerUniqueId, SkillType skill, int XP) {
        PlayerProfile profile = getOfflineProfile(playerUniqueId);

        profile.addXp(skill, XP);
        profile.save();
    }

    @Deprecated
    private static void addOfflineXP(String playerName, SkillType skill, int XP) {
        PlayerProfile profile = getOfflineProfile(playerName);

        profile.addXp(skill, XP);
        profile.scheduleAsyncSave();
    }

    private static PlayerProfile getOfflineProfile(UUID uuid) {
        PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(uuid);

        if (!profile.isLoaded()) {
            throw new InvalidPlayerException();
        }

        return profile;
    }

    @Deprecated
    private static PlayerProfile getOfflineProfile(String playerName) {
        UUID uuid = mcMMO.p.getServer().getOfflinePlayer(playerName).getUniqueId();
        PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(uuid);

        if (!profile.isLoaded()) {
            throw new InvalidPlayerException();
        }

        return profile;
    }

    private static SkillType getSkillType(String skillType) throws InvalidSkillException {
        SkillType skill = SkillType.getSkill(skillType);

        if (skill == null) {
            throw new InvalidSkillException();
        }

        return skill;
    }

    private static SkillType getNonChildSkillType(String skillType) throws InvalidSkillException, UnsupportedOperationException {
        SkillType skill = getSkillType(skillType);

        if (skill.isChildSkill()) {
            throw new UnsupportedOperationException("Child skills do not have XP");
        }

        return skill;
    }

    private static XPGainReason getXPGainReason(String reason) throws InvalidXPGainReasonException {
        XPGainReason xpGainReason = XPGainReason.getXPGainReason(reason);

        if (xpGainReason == null) {
            throw new InvalidXPGainReasonException();
        }

        return xpGainReason;
    }

    private static FormulaType getFormulaType(String formula) throws InvalidFormulaTypeException {
        FormulaType formulaType = FormulaType.getFormulaType(formula);

        if (formulaType == null) {
            throw new InvalidFormulaTypeException();
        }

        return formulaType;
    }

    private static McMMOPlayer getPlayer(Player player) throws McMMOPlayerNotFoundException {
        if (!UserManager.hasPlayerDataKey(player)) {
            throw new McMMOPlayerNotFoundException(player);
        }

        return UserManager.getPlayer(player);
    }
}
