package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;

import java.util.HashMap;
import java.util.Map;

public class FormulaManager {
    // Experience needed to reach a level, cached values for speed
    private Map<Integer, Integer> experienceNeededCosmeticLinear;
    private Map<Integer, Integer> experienceNeededLinear;
    private Map<Integer, Integer> experienceNeededCosmeticExponential;
    private Map<Integer, Integer> experienceNeededExponential;

    private FormulaType currentFormula;
    private final mcMMO pluginRef;

    public FormulaManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        currentFormula = pluginRef.getConfigManager().getConfigLeveling().getFormulaType();
        initExperienceNeededMaps();
    }

    /**
     * Initialize maps used for XP to next level
     */
    private void initExperienceNeededMaps() {
        experienceNeededCosmeticLinear = new HashMap<>();
        experienceNeededCosmeticExponential = new HashMap<>();
        experienceNeededLinear = new HashMap<>();
        experienceNeededExponential = new HashMap<>();
    }

    /**
     * Calculate the total amount of experience earned based on
     * the amount of levels and experience, using the previously
     * used formula type.
     *
     * @param skillLevel Amount of levels
     * @param skillXPLevel Amount of experience
     * @param formulaType Formula to calculate XP for
     * @return The total amount of experience
     */
    public int calculateTotalExperience(int skillLevel, int skillXPLevel, FormulaType formulaType) {
        int totalXP = 0;

        for (int level = 0; level < skillLevel; level++) {
            totalXP += getXPtoNextLevel(level, formulaType);
        }

        totalXP += skillXPLevel;

        return totalXP;
    }

    /**
     * Calculate how many levels a player should have using
     * the new formula type.
     *
     * @param primarySkillType skill where new levels and experience are calculated for
     * @param experience total amount of experience
     * @return the amount of levels and experience
     */
    public int[] calculateNewLevel(PrimarySkillType primarySkillType, int experience) {
        int newLevel = 0;
        int remainder = 0;
        int maxLevel = pluginRef.getConfigManager().getConfigLeveling().getSkillLevelCap(primarySkillType);

        while (experience > 0 && newLevel < maxLevel) {
            int experienceToNextLevel = getXPtoNextLevel(newLevel, currentFormula);

            if (experience - experienceToNextLevel < 0) {
                remainder = experience;
                break;
            }

            newLevel++;
            experience -= experienceToNextLevel;
        }

        return new int[]{ newLevel, remainder };
    }

    /**
     * Get the cached amount of experience needed to reach the next level,
     * if cache doesn't contain the given value it is calculated and added
     * to the cached data.
     *
     * @param level level to check
     * @param formulaType The {@link FormulaType} used
     * @return amount of experience needed to reach next level
     */
    public int getXPtoNextLevel(int level, FormulaType formulaType) {
        /**
         * Retro mode XP requirements are the default requirements
         * Standard mode XP requirements are multiplied by a factor of 10
         */

        return processXPToNextLevel(level, formulaType);
    }

    /**
     * Get the cached amount of experience needed to reach the next level,
     * if cache doesn't contain the given value it is calculated and added
     * to the cached data.
     *
     * Uses the formula specified in the user configuration file
     *
     * @param level level to check
     * @return amount of experience needed to reach next level
     */
    public int getXPtoNextLevel(int level) {
        /**
         * Retro mode XP requirements are the default requirements
         * Standard mode XP requirements are multiplied by a factor of 10
         */

        return processXPToNextLevel(level, currentFormula);
    }

    /**
     * Calculate the XP needed for the next level for the linear formula for Standard scaling (1-100)
     * @param level target level
     * @return raw xp needed to reach the next level
     */
    private int processXPToNextLevel(int level, FormulaType formulaType) {
        Map<Integer, Integer> experienceMapRef = formulaType == FormulaType.LINEAR ? experienceNeededLinear : experienceNeededExponential;

        if(!experienceMapRef.containsKey(level)) {
            int cosmeticScaleMod = pluginRef.getPlayerLevelingSettings().getCosmeticLevelScaleModifier();
            int experienceSum = 0;
            int cosmeticIndex = (level * cosmeticScaleMod) + 1;

            //Sum the range of levels in Retro that this Standard level would represent
            for(int x = cosmeticIndex; x < (cosmeticIndex + cosmeticScaleMod); x++) {
                //calculateXPNeeded doesn't cache results so we use that instead of invoking the Retro XP methods to avoid memory bloat
                experienceSum += calculateXPNeeded(x, formulaType);
            }

            experienceMapRef.put(level, experienceSum);
        }

        return experienceMapRef.get(level);
    }

    /**
     * Calculates the XP to next level for Retro Mode scaling
     * Results are cached to reduce needless operations
     * @param level target level
     * @param formulaType target formula type
     * @return raw xp needed to reach the next level based on formula type
     */
    private int processXPRetroToNextLevel(int level, FormulaType formulaType) {
        Map<Integer, Integer> experienceMapRef = formulaType == FormulaType.LINEAR ? experienceNeededCosmeticLinear : experienceNeededCosmeticExponential;

        if (!experienceMapRef.containsKey(level)) {
            int experience = calculateXPNeeded(level, formulaType);
            experienceMapRef.put(level, experience);
        }

        return experienceMapRef.get(level);
    }

    /**
     * Does the actual math to get the XP needed for a level in RetroMode scaling
     * Standard uses a sum of RetroMode XP needed levels for its own thing, so it uses this too
     * @param level target level
     * @param formulaType target formulatype
     * @return the raw XP needed for the next level based on formula type
     */
    private int calculateXPNeeded(int level, FormulaType formulaType) {
        int base = pluginRef.getConfigManager().getConfigLeveling().getConfigExperienceFormula().getBase(formulaType);
        double multiplier = pluginRef.getConfigManager().getConfigLeveling().getConfigExperienceFormula().getMultiplier(formulaType);

        switch(formulaType) {
            case LINEAR:
                return (int) Math.floor(base + level * multiplier);
            case EXPONENTIAL:
                double exponent = pluginRef.getConfigManager().getConfigLeveling().getConfigExperienceFormula().getExponentialExponent();
                return (int) Math.floor(multiplier * Math.pow(level, exponent) + base);
            default:
                //TODO: Should never be called
                pluginRef.getLogger().severe("Invalid formula specified for calculation, defaulting to Linear");
                return calculateXPNeeded(level, FormulaType.LINEAR);
        }
    }

    public void setCurrentFormula(FormulaType currentFormula) {
        this.currentFormula = currentFormula;
    }
}