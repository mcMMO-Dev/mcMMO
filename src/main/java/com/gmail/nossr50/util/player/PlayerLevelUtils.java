package com.gmail.nossr50.util.player;

import com.gmail.nossr50.datatypes.experience.CustomXPPerk;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class PlayerLevelUtils {
    private HashMap<PrimarySkillType, Integer> earlyGameBoostCutoffs;
    private HashSet<CustomXPPerk> customXpPerkNodes;

    public PlayerLevelUtils() {
        registerCustomPerkPermissions();
        earlyGameBoostCutoffs = new HashMap<>();
        calculateEarlyGameBoostCutoffs();
        applyConfigPerks();
    }

    /**
     * Register our custom permission perks with bukkit
     */
    private void registerCustomPerkPermissions() {
        Permissions.addCustomXPPerks();
    }

    /**
     * Updates our custom XP boost map
     */
    private void applyConfigPerks() {
        //Make a copy
        customXpPerkNodes = new HashSet<>(mcMMO.getConfigManager().getConfigExperience().getCustomXPBoosts());
    }

    /**
     * Get the the final level at which players will still receive an early game XP boost
     * Note: This doesn't mean early game boosts are enabled on the server, as that is a config toggle
     *
     * @param primarySkillType target skill
     * @return this skills maximum early game boost level
     */
    public int getEarlyGameCutoff(PrimarySkillType primarySkillType) {
        return earlyGameBoostCutoffs.get(primarySkillType);
    }

    private void calculateEarlyGameBoostCutoffs() {
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            int levelCap = mcMMO.getConfigManager().getConfigLeveling().getLevelCap(primarySkillType);
            int cap;

            if (levelCap == Integer.MAX_VALUE || levelCap <= 0) {
                cap = mcMMO.isRetroModeEnabled() ? 50 : 5;
            } else {
                cap = (int) (levelCap * mcMMO.getConfigManager().getConfigLeveling().getEarlyGameBoostMultiplier());
            }

            earlyGameBoostCutoffs.put(primarySkillType, cap);
        }
    }

    /**
     * Finds all custom XP perks that a player currently qualifies for
     * Used in figuring out which XP boost to give the player
     *
     * @param player target player
     * @return a set of all CustomXPPerk that a player has positive permissions for
     */
    public HashSet<CustomXPPerk> findCustomXPPerks(Player player) {
        HashSet<CustomXPPerk> enabledXPPerks = new HashSet<>();

        //Check all registered XP Perk nodes for this player
        for (CustomXPPerk customXPPerk : customXpPerkNodes) {
            if (Permissions.hasCustomXPPerk(player, customXPPerk)) {
                enabledXPPerks.add(customXPPerk);
            }
        }

        return enabledXPPerks;
    }

    /**
     * Determines a players XP boost in a specific skill
     * If a player has no custom XP perks or other boosts this value should always return 1.0
     * Since a player can have multiple XP perks and boosts, this method only returns the highest that a player qualifies for
     *
     * @param player target player
     * @param skill  target skill
     * @return the highest XP boost that this player qualifies for through perks or otherwise for target skill
     */
    public Double determineXpPerkValue(Player player, PrimarySkillType skill) {
        HashSet<CustomXPPerk> enabledCustomXPPerks = findCustomXPPerks(player);

        //A player can have multiple overlapping perks at a time, we need to compile a list and then sort it for the highest value
        HashSet<Double> xpPerkValues = new HashSet<>();

        if (enabledCustomXPPerks.size() >= 1) {
            //Player has custom XP perks
            for (CustomXPPerk customXPPerk : enabledCustomXPPerks) {
                //Note: This returns 1.0 on skills with unset values
                //Only add results that are not equal to 1.0
                if (customXPPerk.getXPMultiplierValue(skill) != 1.0F)
                    xpPerkValues.add(customXPPerk.getXPMultiplierValue(skill));
            }
        }

        //Disgusting legacy support start
        if (Permissions.quadrupleXp(player, skill)) {
            xpPerkValues.add(4.0);
        } else if (Permissions.tripleXp(player, skill)) {
            xpPerkValues.add(3.0);
        } else if (Permissions.doubleAndOneHalfXp(player, skill)) {
            xpPerkValues.add(2.5);
        } else if (Permissions.doubleXp(player, skill)) {
            xpPerkValues.add(2.0);
        } else if (Permissions.oneAndOneHalfXp(player, skill)) {
            xpPerkValues.add(1.5);
        } else if (Permissions.oneAndOneTenthXp(player, skill)) {
            xpPerkValues.add(1.1);
        }
        //Disgusting legacy support end

        //Return
        if (xpPerkValues.size() >= 1)
            return Collections.max(xpPerkValues);
        else
            return 1.0;
    }


    /**
     * Check if a player is currently qualifying for the early game boosted XP
     * Will return false only if a player is above the boost level cutoff, it does not check config settings to see if the early game boost is on
     * @param mcMMOPlayer target player
     * @param primarySkillType target skill
     * @return if the player would qualify for the XP boost if its enabled
     */
    public static boolean qualifiesForEarlyGameBoost(McMMOPlayer mcMMOPlayer, PrimarySkillType primarySkillType) {
        return mcMMOPlayer.getSkillLevel(primarySkillType) < mcMMO.getPlayerLevelUtils().getEarlyGameCutoff(primarySkillType);
    }
}
