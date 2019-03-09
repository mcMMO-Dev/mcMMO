package com.gmail.nossr50.config.experience;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.config.ConfigValidated;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class ExperienceConfig extends ConfigValidated {
    public static final String EXPLOIT_FIX = "ExploitFix";
    public static final String ENDERMAN_ENDERMITE_FARMS = "EndermanEndermiteFarms";
    public static final String EXPERIENCE = "Experience";
    public static final String EXPERIENCE_FORMULA = EXPERIENCE + "_Formula";
    public static final String CURVE = "Curve";
    public static final String VALUES = "_Values";
    //public static final String MULTIPLIER = "multiplier";
    public static final String BASE = "base";
    public static final String EXPONENT = "exponent";
    public static final String MULTIPLIER = "Multiplier";
    public static final String GLOBAL = "Global";
    public static final String MOBSPAWNERS = "Mobspawners";
    public static final String BREEDING = "Breeding";
    public static final String MODIFIER = "Modifier";
    public static final String CUSTOM_XP_PERK = "Custom_XP_Perk";
    public static final String BOOST = "Boost";
    public static final String DIMISHED_RETURNS = "Dimished_Returns";
    public static final String GUARANTEED_MINIMUM_PERCENTAGE = "Guaranteed_Minimum_Percentage";
    public static final String DIMINISHED_RETURNS = "Diminished_Returns";
    public static final String ENABLE = "Enable";
    public static final String ENABLED = ENABLE + "d";
    public static final String TIME_INTERVAL = "Time_Interval";
    public static final String CONVERSION = "Conversion";
    public static final String EXP = "Exp_";
    public static final String PVP = "PVP";
    public static final String REWARDS = "Rewards";
    public static final String COMBAT = "Combat";
    public static final String ANIMALS = "Animals";
    public static final String BARS = "_Bars";
    public static final String UPDATE = "Update";
    public static final String PASSIVE = "Passive";
    public static final String THIS_MAY_CAUSE_LAG = "ThisMayCauseLag";
    public static final String ALWAYS = "Always";
    public static final String TITLES_WHEN_XPIS_GAINED = "TitlesWhenXPIsGained";
    public static final String EXTRA_DETAILS = "ExtraDetails";
    public static final String COLOR = "Color";
    public static final String BAR_STYLE = "BarStyle";
    public static final String ACROBATICS = "Acrobatics";
    public static final String DODGE = "Dodge";
    public static final String ROLL = "Roll";
    public static final String FALL = "Fall";
    public static final String FEATHER = "Feather";
    public static final String ALCHEMY = "Alchemy";
    public static final String POTION_STAGE = "Potion_Stage_";
    public static final String ARCHERY = "Archery";
    public static final String DISTANCE = "Distance_";
    public static final String FISHING = "Fishing";
    public static final String SHAKE = "Shake";
    public static final String REPAIR = "Repair";
    public static final String BASE1 = "Base";
    public static final String TAMING = "Taming";
    public static final String ANIMAL_TAMING = "Animal_Taming";
    public static final String PARTY = "Party";
    public static final String THRESHOLD = "Threshold";
    public static final String CUMULATIVE = "Cumulative_";
    public static final String OCELOT = "Ocelot";
    public static final String WOLF = "Wolf";
    public static final String FEATHER_FALL_MULTIPLIER = "FeatherFall_Multiplier";
    private static ExperienceConfig instance;

    //TODO: Should merge be false? Seems okay to leave it as true..
    public ExperienceConfig() {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(), "experience.yml", true);
        super("experience", mcMMO.p.getDataFolder().getAbsoluteFile(), ConfigConstants.RELATIVE_PATH_CONFIG_DIR, true, false, true, false);
    }

    /**
     * This grabs an instance of this config class from the Config Manager
     * This method is deprecated and will be removed in the future
     * @see mcMMO#getConfigManager()
     * @return the instance of this config
     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
     */
    @Deprecated
    public static ExperienceConfig getInstance() {
        return mcMMO.getConfigManager().getExperienceConfig();
    }

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }

    @Override
    public void unload() {
        instance = null; //TODO: this might be a bit problematic
    }

    @Override
    public List<String> validateKeys() {
        List<String> reason = new ArrayList<String>();

        /*
         * FORMULA SETTINGS
         */

        /* Curve values */
        if (getMultiplier(FormulaType.EXPONENTIAL) <= 0) {
            reason.add(EXPERIENCE_FORMULA + ".Exponential" + VALUES + "." + MULTIPLIER + " should be greater than 0!");
        }

        if (getMultiplier(FormulaType.LINEAR) <= 0) {
            reason.add(EXPERIENCE_FORMULA + ".Linear" + VALUES + "." + MULTIPLIER + " should be greater than 0!");
        }

        if (getExponent(FormulaType.EXPONENTIAL) <= 0) {
            reason.add(EXPERIENCE_FORMULA + ".Exponential" + VALUES + "." + EXPONENT + " should be greater than 0!");
        }

        /* Global modifier */
        if (getExperienceGainsGlobalMultiplier() <= 0) {
            reason.add(EXPERIENCE_FORMULA + "." + MULTIPLIER + "." + GLOBAL + " should be greater than 0!");
        }

        /* PVP modifier */
        if (getPlayerVersusPlayerXP() < 0) {
            reason.add(EXPERIENCE_FORMULA + "." + MULTIPLIER + "." + PVP + " should be at least 0!");
        }

        /* Spawned Mob modifier */
        if (getSpawnedMobXpMultiplier() < 0) {
            reason.add(EXPERIENCE_FORMULA + "." + MOBSPAWNERS + "." + MULTIPLIER + " should be at least 0!");
        }

        /* Bred Mob modifier */
        if (getBredMobXpMultiplier() < 0) {
            reason.add(EXPERIENCE_FORMULA + "." + BREEDING + "." + MULTIPLIER + " should be at least 0!");
        }

        /* Conversion */
        if (getExpModifier() <= 0) {
            reason.add(CONVERSION + "." + EXP + MODIFIER + " should be greater than 0!");
        }

        /*
         * XP SETTINGS
         */

        /* Alchemy */
        for (PotionStage potionStage : PotionStage.values()) {
            if (getPotionXP(potionStage) < 0) {
                reason.add(EXPERIENCE + "." + ALCHEMY + "." + POTION_STAGE + potionStage.toNumerical() + " should be at least 0!");
            }
        }

        /* Archery */
        if (getArcheryDistanceMultiplier() < 0) {
            reason.add(EXPERIENCE + "." + ARCHERY + "." + DISTANCE + MULTIPLIER + " should be at least 0!");
        }

        /* Combat XP Multipliers */
        if (getAnimalsXP() < 0) {
            reason.add(EXPERIENCE + "." + COMBAT + "." + MULTIPLIER + "." + ANIMALS + " should be at least 0!");
        }

        if (getDodgeXPModifier() < 0) {
            reason.add("Skills." + ACROBATICS + "." + DODGE + "_XP_" + MODIFIER + " should be at least 0!");
        }

        if (getRollXPModifier() < 0) {
            reason.add("Skills." + ACROBATICS + "." + ROLL + "_XP_" + MODIFIER + " should be at least 0!");
        }

        if (getFallXPModifier() < 0) {
            reason.add("Skills." + ACROBATICS + "." + FALL + "_XP_" + MODIFIER + " should be at least 0!");
        }

        /* Fishing */
        // TODO: Add validation for each fish type once enum is available.

        if (getFishingShakeXP() <= 0) {
            reason.add(EXPERIENCE + "." + FISHING + "." + SHAKE + " should be greater than 0!");
        }

        /* Repair */
        if (getRepairXPBase() <= 0) {
            reason.add(EXPERIENCE + "." + REPAIR + "." + BASE1 + " should be greater than 0!");
        }

        /* Taming */
        if (getTamingXP(EntityType.WOLF) <= 0) {
            reason.add(EXPERIENCE + "." + TAMING + "." + ANIMAL_TAMING + "." + WOLF + " should be greater than 0!");
        }

        if (getTamingXP(EntityType.OCELOT) <= 0) {
            reason.add(EXPERIENCE + "." + TAMING + "." + ANIMAL_TAMING + "." + OCELOT + " should be greater than 0!");
        }

        return reason;
    }




    /*
     * FORMULA SETTINGS
     */

    /* EXPLOIT TOGGLES */
    public boolean isEndermanEndermiteFarmingPrevented() {
        return getBooleanValue(EXPLOIT_FIX, ENDERMAN_ENDERMITE_FARMS);
    }

    public boolean isFishingExploitingPrevented() { return config.getBoolean("ExploitFix.Fishing", true); }
    /* Curve settings */
    public FormulaType getFormulaType() {
        return FormulaType.getFormulaType(getStringValue(EXPERIENCE_FORMULA, CURVE));
    }

    public boolean getCumulativeCurveEnabled() {
        return getBooleanValue(EXPERIENCE_FORMULA, CUMULATIVE + CURVE);
    }

    /* Curve values */
    public double getMultiplier(FormulaType type) {
        return getDoubleValue(EXPERIENCE_FORMULA, StringUtils.getCapitalized(type.toString()) + VALUES, MULTIPLIER);
    }

    public int getBase(FormulaType type) {
        return getIntValue(EXPERIENCE_FORMULA, StringUtils.getCapitalized(type.toString()) + VALUES, BASE);
    }

    public double getExponent(FormulaType type) {
        return getDoubleValue(EXPERIENCE_FORMULA, StringUtils.getCapitalized(type.toString()) + VALUES, EXPONENT);
    }

    /* Global modifier */
    public double getExperienceGainsGlobalMultiplier() {
        return getDoubleValue(EXPERIENCE_FORMULA, MULTIPLIER, GLOBAL);
    }

    public void setGlobalXPMultiplier(double newXpMultiplier)
    {
        getUserRootNode().getNode(EXPERIENCE_FORMULA, MULTIPLIER, GLOBAL).setValue(newXpMultiplier);
    }

    //TODO: Rewrite this
    /*public void setExperienceGainsGlobalMultiplier(double value) {
        config.set(EXPERIENCE_FORMULA, MULTIPLIER, GLOBAL, value);
    }*/

    /* PVP modifier */
    public double getPlayerVersusPlayerXP() {
        return getDoubleValue(EXPERIENCE_FORMULA, MULTIPLIER, PVP);
    }

    /* Spawned Mob modifier */
    public double getSpawnedMobXpMultiplier() {
        return getDoubleValue(EXPERIENCE_FORMULA, MOBSPAWNERS, MULTIPLIER);
    }

    public double getBredMobXpMultiplier() {
        return getDoubleValue(EXPERIENCE_FORMULA, BREEDING, MULTIPLIER);
    }

    /* Skill modifiers */
    public double getFormulaSkillModifier(PrimarySkillType skill) {
        return getDoubleValue(EXPERIENCE_FORMULA, MODIFIER, StringUtils.getCapitalized(skill.toString()));
    }

    /* Custom XP perk */
    public double getCustomXpPerkBoost() {
        return getDoubleValue(EXPERIENCE_FORMULA, CUSTOM_XP_PERK, BOOST);
    }

    /* Diminished Returns */
    public float getDiminishedReturnsCap() {
        return (float) getDoubleValue(DIMISHED_RETURNS, GUARANTEED_MINIMUM_PERCENTAGE);
    }

    public boolean getDiminishedReturnsEnabled() {
        return getBooleanValue(DIMINISHED_RETURNS, ENABLED);
    }

    public int getDiminishedReturnsThreshold(PrimarySkillType skill) {
        return getIntValue(DIMINISHED_RETURNS, THRESHOLD, StringUtils.getCapitalized(skill.toString()));
    }

    public int getDiminishedReturnsTimeInterval() {
        return getIntValue(DIMINISHED_RETURNS, TIME_INTERVAL);
    }

    /* Conversion */
    public double getExpModifier() {
        return getDoubleValue(CONVERSION, EXP + MODIFIER);
    }

    /*
     * XP SETTINGS
     */

    /* General Settings */
    public boolean getExperienceGainsPlayerVersusPlayerEnabled() {
        return getBooleanValue(EXPERIENCE, PVP, REWARDS);
    }

    /* Combat XP Multipliers */
    public double getCombatXP(EntityType entity) {
        return getDoubleValue(EXPERIENCE, COMBAT, MULTIPLIER, StringUtils.getEntityConfigName(entity));
    }

    public double getAnimalsXP(EntityType entity) {
        return getDoubleValue(EXPERIENCE, COMBAT, MULTIPLIER, StringUtils.getEntityConfigName(entity));
    }

    public double getAnimalsXP() {
        return getDoubleValue(EXPERIENCE, COMBAT, MULTIPLIER, ANIMALS);
    }

    public boolean hasCombatXP(EntityType entity) {
        return hasNode(EXPERIENCE, COMBAT, MULTIPLIER, StringUtils.getEntityConfigName(entity));
    }

    /* Materials  */

    /**
     * Gets the raw XP given for breaking this block, this does not include modifiers
     * @param skill The skill to give XP for
     * @param blockType the type of block
     * @return the raw amount of XP for this block before modifiers
     */
    //public int getXp(PrimarySkillType skill, BlockType blockType) {
    public int getXp(PrimarySkillType skill, Material blockType) {
        //TODO: This is going to need to be changed, this code here is only placeholder
        String[] path = new String[]{ EXPERIENCE, StringUtils.getCapitalized(skill.toString()), blockType.toString()};
        return getIntValue(path);
    }

    /*
     * Experience Bar Stuff
     */

    public boolean isPartyExperienceBarsEnabled() {
        return getBooleanValue(EXPERIENCE + BARS, UPDATE, PARTY);
    }

    public boolean isPassiveGainsExperienceBarsEnabled() {
        return getBooleanValue(EXPERIENCE + BARS, UPDATE, PASSIVE);
    }

    public boolean getDoExperienceBarsAlwaysUpdateTitle() {
        return getBooleanValue(EXPERIENCE + BARS, THIS_MAY_CAUSE_LAG, ALWAYS + UPDATE + TITLES_WHEN_XPIS_GAINED, ENABLE) || getAddExtraDetails();
    }

    public boolean getAddExtraDetails() {
        return getBooleanValue(EXPERIENCE + BARS, THIS_MAY_CAUSE_LAG, ALWAYS + UPDATE + TITLES_WHEN_XPIS_GAINED, EXTRA_DETAILS);
    }

    public boolean isExperienceBarsEnabled() {
        return getBooleanValue(EXPERIENCE + BARS, ENABLE);
    }

    public boolean isExperienceBarEnabled(PrimarySkillType primarySkillType) {
        return getBooleanValue(EXPERIENCE + BARS, StringUtils.getCapitalized(primarySkillType.toString()), ENABLE);
    }

    public BarColor getExperienceBarColor(PrimarySkillType primarySkillType) {
        String colorValueFromConfig = getStringValue(EXPERIENCE + BARS, StringUtils.getCapitalized(primarySkillType.toString()), COLOR);

        for (BarColor barColor : BarColor.values()) {
            if (barColor.toString().equalsIgnoreCase(colorValueFromConfig))
                return barColor;
        }

        //In case the value is invalid
        return BarColor.WHITE;
    }

    public BarStyle getExperienceBarStyle(PrimarySkillType primarySkillType) {
        String colorValueFromConfig = getStringValue(EXPERIENCE + BARS, StringUtils.getCapitalized(primarySkillType.toString()), BAR_STYLE);

        for (BarStyle barStyle : BarStyle.values()) {
            if (barStyle.toString().equalsIgnoreCase(colorValueFromConfig))
                return barStyle;
        }

        //In case the value is invalid
        return BarStyle.SOLID;
    }

    /* Acrobatics */
    public int getDodgeXPModifier() {
        return getIntValue(EXPERIENCE, ACROBATICS, DODGE);
    }

    public int getRollXPModifier() {
        return getIntValue(EXPERIENCE, ACROBATICS, ROLL);
    }

    public int getFallXPModifier() {
        return getIntValue(EXPERIENCE, ACROBATICS, FALL);
    }

    public double getFeatherFallXPModifier() {
        return getDoubleValue(EXPERIENCE, ACROBATICS, FEATHER_FALL_MULTIPLIER);
    }

    /* Alchemy */
    public double getPotionXP(PotionStage stage) {
        return getDoubleValue(EXPERIENCE, ALCHEMY, POTION_STAGE + stage.toNumerical());
    }

    /* Archery */
    public double getArcheryDistanceMultiplier() {
        return getDoubleValue(EXPERIENCE, ARCHERY, DISTANCE + MULTIPLIER);
    }

    public int getFishingShakeXP() {
        return getIntValue(EXPERIENCE, FISHING, SHAKE);
    }

    /* Repair */
    public double getRepairXPBase() {
        return getDoubleValue(EXPERIENCE, REPAIR, BASE1);
    }

    public double getRepairXP(MaterialType repairMaterialType) {
        return getDoubleValue(EXPERIENCE, REPAIR, StringUtils.getCapitalized(repairMaterialType.toString()));
    }

    /* Taming */
    public int getTamingXP(EntityType type) {
        return getIntValue(EXPERIENCE, TAMING, ANIMAL_TAMING, StringUtils.getEntityConfigName(type));
    }
}
