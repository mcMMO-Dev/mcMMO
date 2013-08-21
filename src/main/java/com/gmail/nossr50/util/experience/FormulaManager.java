package com.gmail.nossr50.util.experience;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.mcMMO;

public class FormulaManager {
    private static String formulaFilePath = mcMMO.getFlatFileDirectory() + "formula.yml";
    private static File formulaFile = new File(formulaFilePath);

    // Experience needed to reach a level, cached values to improve conversion speed
    private final Map<Integer, Integer> experienceNeededLinear = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> experienceNeededExponential = new HashMap<Integer, Integer>();

    private FormulaType previousFormula;

    public FormulaManager() {
        loadFormula();
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
            totalXP += getCachedXpToLevel(level, previousFormula);
        }

        totalXP += skillXPLevel;

        return totalXP;
    }

    /**
     * Calculate how many levels a player should have using
     * the new formula type.
     *
     * @param skillType skill where new levels and experience are calculated for
     * @param experience total amount of experience
     * @param formulaType The new {@link FormulaType}
     * @return the amount of levels and experience
     */
    public int[] calculateNewLevel(SkillType skillType, int experience, FormulaType formulaType) {
        int newLevel = 0;
        int remainder = 0;
        int maxLevel = Config.getInstance().getLevelCap(skillType);

        while (experience > 0 && newLevel < maxLevel) {
            int experienceToNextLevel = getCachedXpToLevel(newLevel, formulaType);

            if (experience - experienceToNextLevel < 0) {
                remainder = experience;
                break;
            }

            newLevel++;
            experience -= experienceToNextLevel;
        }

        return new int[]{newLevel, remainder};
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
    public int getCachedXpToLevel(int level, FormulaType formulaType) {
        int experience;
        double multiplier;

        switch (formulaType) {
            case UNKNOWN:
            case LINEAR:
                if (!experienceNeededLinear.containsKey(level)) {
                    multiplier = ExperienceConfig.getInstance().getLinearMultiplier();

                    //TODO: Validate at load?
                    if (multiplier <= 0) {
                        multiplier = 20;
                    }

                    experience = (int) Math.floor(ExperienceConfig.getInstance().getLinearBase() + level * multiplier);
                    experienceNeededLinear.put(level, experience);
                }

                return experienceNeededLinear.get(level);

            case EXPONENTIAL:
                if (!experienceNeededExponential.containsKey(level)) {
                    multiplier = ExperienceConfig.getInstance().getExponentialMultiplier();
                    double exponent = ExperienceConfig.getInstance().getExponentialExponent();
                    int base = ExperienceConfig.getInstance().getExponentialBase();

                    //TODO: Validate at load?
                    if (multiplier <= 0) {
                        multiplier = 0.1;
                    }

                    //TODO: Validate at load?
                    if (exponent <= 0) {
                        exponent = 1.80;
                    }

                    experience = (int) Math.floor(multiplier * Math.pow(level, exponent) + base);
                    experienceNeededExponential.put(level, experience);
                }

                return experienceNeededExponential.get(level);

            default:
                return 0;
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

        YamlConfiguration formulasFile = YamlConfiguration.loadConfiguration(formulaFile);

        previousFormula = FormulaType.getFormulaType(formulasFile.getString("Previous_Formula", "UNKNOWN"));
    }

    /**
     * Save formula file.
     */
    public void saveFormula() {
        if (formulaFile.exists()) {
            formulaFile.delete();
        }

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
