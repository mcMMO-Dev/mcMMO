package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FormulaManager {
    private static final File formulaFile = new File(mcMMO.getFlatFileDirectory() + "formula.yml");

    // Experience needed to reach a level, cached values to improve conversion speed
    private Map<Integer, Integer> experienceNeededRetroLinear;
    private Map<Integer, Integer> experienceNeededStandardLinear;
    private Map<Integer, Integer> experienceNeededRetroExponential;
    private Map<Integer, Integer> experienceNeededStandardExponential;

    private FormulaType previousFormula;

    public FormulaManager() {
        /* Setting for Classic Mode (Scales a lot of stuff up by * 10) */
        initExperienceNeededMaps();
        loadFormula();
    }

    /**
     * Initialize maps used for XP to next level
     */
    private void initExperienceNeededMaps() {
        experienceNeededRetroLinear = new HashMap<>();
        experienceNeededRetroExponential = new HashMap<>();
        experienceNeededStandardLinear = new HashMap<>();
        experienceNeededStandardExponential = new HashMap<>();
    }

    /**
     * Get the formula type that was used before converting
     *
     * @return previously used formula type
     */
    public FormulaType getPreviousFormulaType() {
        return previousFormula;
    }

    /**
     * Set the formula type that was used before converting
     *
     * @param previousFormulaType The {@link FormulaType} previously used
     */
    public void setPreviousFormulaType(FormulaType previousFormulaType) {
        this.previousFormula = previousFormulaType;
    }

    /**
     * Calculate the total amount of experience earned based on
     * the amount of levels and experience, using the previously
     * used formula type.
     *
     * @param skillLevel Amount of levels
     * @param skillXPLevel Amount of experience
     * @return The total amount of experience
     */
    public int calculateTotalExperience(int skillLevel, int skillXPLevel) {
        int totalXP = 0;

        for (int level = 0; level < skillLevel; level++) {
            totalXP += getXPtoNextLevel(level, previousFormula);
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
     * @param formulaType The new {@link FormulaType}
     * @return the amount of levels and experience
     */
    public int[] calculateNewLevel(PrimarySkillType primarySkillType, int experience, FormulaType formulaType) {
        int newLevel = 0;
        int remainder = 0;
        int maxLevel = mcMMO.p.getSkillTools().getLevelCap(primarySkillType);

        while (experience > 0 && newLevel < maxLevel) {
            int experienceToNextLevel = getXPtoNextLevel(newLevel, formulaType);

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
        /*
          Retro mode XP requirements are the default requirements
          Standard mode XP requirements are multiplied by a factor of 10
         */

        //TODO: When the heck is Unknown used?
        if (formulaType == FormulaType.UNKNOWN) {
            formulaType = FormulaType.LINEAR;
        }

        return processXPToNextLevel(level, formulaType);
    }

    /**
     * Gets the value of XP needed for the next level based on the level Scaling, the level, and the formula type
     * @param level target level
     * @param formulaType target formulaType
     */
    private int processXPToNextLevel(int level, FormulaType formulaType) {
        if (mcMMO.isRetroModeEnabled()) {
            return processXPRetroToNextLevel(level, formulaType);
        } else {
            return processStandardXPToNextLevel(level, formulaType);
        }
    }

    /**
     * Calculate the XP needed for the next level for the linear formula for Standard scaling (1-100)
     * @param level target level
     * @return raw xp needed to reach the next level
     */
    private int processStandardXPToNextLevel(int level, FormulaType formulaType) {
        Map<Integer, Integer> experienceMapRef = formulaType == FormulaType.LINEAR ? experienceNeededStandardLinear : experienceNeededStandardExponential;

        if (!experienceMapRef.containsKey(level)) {
            int experienceSum = 0;
            int retroIndex = (level * 10) + 1;

            //Sum the range of levels in Retro that this Standard level would represent
            for(int x = retroIndex; x < (retroIndex + 10); x++) {
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
        Map<Integer, Integer> experienceMapRef = formulaType == FormulaType.LINEAR ? experienceNeededRetroLinear : experienceNeededRetroExponential;

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
        int base = ExperienceConfig.getInstance().getBase(formulaType);
        double multiplier = ExperienceConfig.getInstance().getMultiplier(formulaType);

        switch(formulaType) {
            case LINEAR:
                return (int) Math.floor(base + level * multiplier);
            case EXPONENTIAL:
                double exponent = ExperienceConfig.getInstance().getExponent(formulaType);
                return (int) Math.floor(multiplier * Math.pow(level, exponent) + base);
            default:
                //TODO: Should never be called
                mcMMO.p.getLogger().severe("Invalid formula specified for calculation, defaulting to Linear");
                return calculateXPNeeded(level, FormulaType.LINEAR);
        }
    }

    /**
     * Load formula file.
     */
    public void loadFormula() {
        if (!formulaFile.exists()) {
            previousFormula = FormulaType.UNKNOWN;
            return;
        }

        previousFormula = FormulaType.getFormulaType(YamlConfiguration.loadConfiguration(formulaFile).getString("Previous_Formula", "UNKNOWN"));
    }

    /**
     * Save formula file.
     */
    public void saveFormula() {
        LogUtils.debug(mcMMO.p.getLogger(), "Saving previous XP formula type...");
        YamlConfiguration formulasFile = new YamlConfiguration();
        formulasFile.set("Previous_Formula", previousFormula.toString());

        try {
            formulasFile.save(formulaFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
