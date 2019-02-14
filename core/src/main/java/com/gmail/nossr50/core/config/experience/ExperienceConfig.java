package com.gmail.nossr50.core.config.experience;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.config.ConfigValidated;
import com.gmail.nossr50.core.datatypes.experience.FormulaType;
import com.gmail.nossr50.core.skills.MaterialType;
import com.gmail.nossr50.core.skills.PotionStage;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ExperienceConfig extends ConfigValidated {
    private static ExperienceConfig instance;

    private ExperienceConfig() {
        super(McmmoCore.getDataFolderPath().getAbsoluteFile(), "experience.yml");
    }

    public static ExperienceConfig getInstance() {
        if (instance == null) {
            instance = new ExperienceConfig();
        }

        return instance;
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
            reason.add("Experience_Formula.Exponential_Values.multiplier should be greater than 0!");
        }

        if (getMultiplier(FormulaType.LINEAR) <= 0) {
            reason.add("Experience_Formula.Linear_Values.multiplier should be greater than 0!");
        }

        if (getExponent(FormulaType.EXPONENTIAL) <= 0) {
            reason.add("Experience_Formula.Exponential_Values.exponent should be greater than 0!");
        }

        /* Global modifier */
        if (getExperienceGainsGlobalMultiplier() <= 0) {
            reason.add("Experience_Formula.Multiplier.Global should be greater than 0!");
        }

        /* PVP modifier */
        if (getPlayerVersusPlayerXP() < 0) {
            reason.add("Experience_Formula.Multiplier.PVP should be at least 0!");
        }

        /* Spawned Mob modifier */
        if (getSpawnedMobXpMultiplier() < 0) {
            reason.add("Experience_Formula.Mobspawners.Multiplier should be at least 0!");
        }

        /* Bred Mob modifier */
        if (getBredMobXpMultiplier() < 0) {
            reason.add("Experience_Formula.Breeding.Multiplier should be at least 0!");
        }

        /* Conversion */
        if (getExpModifier() <= 0) {
            reason.add("Conversion.Exp_Modifier should be greater than 0!");
        }

        /*
         * XP SETTINGS
         */

        /* Alchemy */
        for (PotionStage potionStage : PotionStage.values()) {
            if (getPotionXP(potionStage) < 0) {
                reason.add("Experience.Alchemy.Potion_Stage_" + potionStage.toNumerical() + " should be at least 0!");
            }
        }

        /* Archery */
        if (getArcheryDistanceMultiplier() < 0) {
            reason.add("Experience.Archery.Distance_Multiplier should be at least 0!");
        }

        /* Combat XP Multipliers */
        if (getAnimalsXP() < 0) {
            reason.add("Experience.Combat.Multiplier.Animals should be at least 0!");
        }

        if (getDodgeXPModifier() < 0) {
            reason.add("Skills.Acrobatics.Dodge_XP_Modifier should be at least 0!");
        }

        if (getRollXPModifier() < 0) {
            reason.add("Skills.Acrobatics.Roll_XP_Modifier should be at least 0!");
        }

        if (getFallXPModifier() < 0) {
            reason.add("Skills.Acrobatics.Fall_XP_Modifier should be at least 0!");
        }

        /* Fishing */
        // TODO: Add validation for each fish type once enum is available.

        if (getFishingShakeXP() <= 0) {
            reason.add("Experience.Fishing.Shake should be greater than 0!");
        }

        /* Repair */
        if (getRepairXPBase() <= 0) {
            reason.add("Experience.Repair.Base should be greater than 0!");
        }

        /* Taming */
        if (getTamingXP(EntityType.WOLF) <= 0) {
            reason.add("Experience.Taming.Animal_Taming.Wolf should be greater than 0!");
        }

        if (getTamingXP(EntityType.OCELOT) <= 0) {
            reason.add("Experience.Taming.Animal_Taming.Ocelot should be greater than 0!");
        }

        return reason;
    }

    /*
     * FORMULA SETTINGS
     */

    /* EXPLOIT TOGGLES */
    public boolean isEndermanEndermiteFarmingPrevented() {
        return getBooleanValue("ExploitFix.EndermanEndermiteFarms", true);
    }

    /* Curve settings */
    public FormulaType getFormulaType() {
        return FormulaType.getFormulaType(getStringValue("Experience_Formula.Curve"));
    }

    public boolean getCumulativeCurveEnabled() {
        return getBooleanValue("Experience_Formula.Cumulative_Curve", false);
    }

    /* Curve values */
    public double getMultiplier(FormulaType type) {
        return getDoubleValue("Experience_Formula." + StringUtils.getCapitalized(type.toString()) + "_Values.multiplier");
    }

    public int getBase(FormulaType type) {
        return getIntValue("Experience_Formula." + StringUtils.getCapitalized(type.toString()) + "_Values.base");
    }

    public double getExponent(FormulaType type) {
        return getDoubleValue("Experience_Formula." + StringUtils.getCapitalized(type.toString()) + "_Values.exponent");
    }

    /* Global modifier */
    public double getExperienceGainsGlobalMultiplier() {
        return getDoubleValue("Experience_Formula.Multiplier.Global", 1.0);
    }

    public void setExperienceGainsGlobalMultiplier(double value) {
        config.set("Experience_Formula.Multiplier.Global", value);
    }

    /* PVP modifier */
    public double getPlayerVersusPlayerXP() {
        return getDoubleValue("Experience_Formula.Multiplier.PVP", 1.0);
    }

    /* Spawned Mob modifier */
    public double getSpawnedMobXpMultiplier() {
        return getDoubleValue("Experience_Formula.Mobspawners.Multiplier", 0.0);
    }

    public double getBredMobXpMultiplier() {
        return getDoubleValue("Experience_Formula.Breeding.Multiplier", 1.0);
    }

    /* Skill modifiers */
    public double getFormulaSkillModifier(PrimarySkillType skill) {
        return getDoubleValue("Experience_Formula.Modifier." + StringUtils.getCapitalized(skill.toString()));
    }

    /* Custom XP perk */
    public double getCustomXpPerkBoost() {
        return getDoubleValue("Experience_Formula.Custom_XP_Perk.Boost", 1.25);
    }

    /* Diminished Returns */
    public float getDiminishedReturnsCap() {
        return (float) getDoubleValue("Dimished_Returns.Guaranteed_Minimum_Percentage", 0.05D);
    }

    public boolean getDiminishedReturnsEnabled() {
        return getBooleanValue("Diminished_Returns.Enabled", false);
    }

    public int getDiminishedReturnsThreshold(PrimarySkillType skill) {
        return getIntValue("Diminished_Returns.Threshold." + StringUtils.getCapitalized(skill.toString()), 20000);
    }

    public int getDiminishedReturnsTimeInterval() {
        return getIntValue("Diminished_Returns.Time_Interval", 10);
    }

    /* Conversion */
    public double getExpModifier() {
        return getDoubleValue("Conversion.Exp_Modifier", 1);
    }

    /*
     * XP SETTINGS
     */

    /* General Settings */
    public boolean getExperienceGainsPlayerVersusPlayerEnabled() {
        return getBooleanValue("Experience.PVP.Rewards", true);
    }

    /* Combat XP Multipliers */
    public double getCombatXP(EntityType entity) {
        return getDoubleValue("Experience.Combat.Multiplier." + StringUtils.getPrettyEntityTypeString(entity).replace(" ", "_"));
    }

    public double getAnimalsXP(EntityType entity) {
        return getDoubleValue("Experience.Combat.Multiplier." + StringUtils.getPrettyEntityTypeString(entity).replace(" ", "_"), getAnimalsXP());
    }

    public double getAnimalsXP() {
        return getDoubleValue("Experience.Combat.Multiplier.Animals", 1.0);
    }

    public boolean hasCombatXP(EntityType entity) {
        return config.contains("Experience.Combat.Multiplier." + StringUtils.getPrettyEntityTypeString(entity).replace(" ", "_"));
    }

    /* Materials  */
    public int getXp(PrimarySkillType skill, Material data) {
        String baseString = "Experience." + StringUtils.getCapitalized(skill.toString()) + ".";
        String explicitString = baseString + StringUtils.getExplicitConfigMaterialString(data);
        if (config.contains(explicitString))
            return getIntValue(explicitString);
        String friendlyString = baseString + StringUtils.getFriendlyConfigMaterialString(data);
        if (config.contains(friendlyString))
            return getIntValue(friendlyString);
        String wildcardString = baseString + StringUtils.getWildcardConfigMaterialString(data);
        if (config.contains(wildcardString))
            return getIntValue(wildcardString);
        return 0;
    }

    /* Materials  */
    public int getXp(PrimarySkillType skill, BlockData data) {
        String baseString = "Experience." + StringUtils.getCapitalized(skill.toString()) + ".";
        String explicitString = baseString + StringUtils.getExplicitConfigBlockDataString(data);
        if (config.contains(explicitString))
            return getIntValue(explicitString);
        String friendlyString = baseString + StringUtils.getFriendlyConfigBlockDataString(data);
        if (config.contains(friendlyString))
            return getIntValue(friendlyString);
        String wildcardString = baseString + StringUtils.getWildcardConfigBlockDataString(data);
        if (config.contains(wildcardString))
            return getIntValue(wildcardString);
        return 0;
    }

    public boolean doesBlockGiveSkillXP(PrimarySkillType skill, Material data) {
        String baseString = "Experience." + StringUtils.getCapitalized(skill.toString()) + ".";
        String explicitString = baseString + StringUtils.getExplicitConfigMaterialString(data);
        if (config.contains(explicitString))
            return true;
        String friendlyString = baseString + StringUtils.getFriendlyConfigMaterialString(data);
        if (config.contains(friendlyString))
            return true;
        String wildcardString = baseString + StringUtils.getWildcardConfigMaterialString(data);
        return config.contains(wildcardString);
    }

    public boolean doesBlockGiveSkillXP(PrimarySkillType skill, BlockData data) {
        String baseString = "Experience." + StringUtils.getCapitalized(skill.toString()) + ".";
        String explicitString = baseString + StringUtils.getExplicitConfigBlockDataString(data);
        if (config.contains(explicitString))
            return true;
        String friendlyString = baseString + StringUtils.getFriendlyConfigBlockDataString(data);
        if (config.contains(friendlyString))
            return true;
        String wildcardString = baseString + StringUtils.getWildcardConfigBlockDataString(data);
        return config.contains(wildcardString);
    }

    /*
     * Experience Bar Stuff
     */

    public boolean isPartyExperienceBarsEnabled() {
        return getBooleanValue("Experience_Bars.Update.Party", true);
    }

    public boolean isPassiveGainsExperienceBarsEnabled() {
        return getBooleanValue("Experience_Bars.Update.Passive", true);
    }

    public boolean getDoExperienceBarsAlwaysUpdateTitle() {
        return getBooleanValue("Experience_Bars.ThisMayCauseLag.AlwaysUpdateTitlesWhenXPIsGained.Enable", false) || getAddExtraDetails();
    }

    public boolean getAddExtraDetails() {
        return getBooleanValue("Experience_Bars.ThisMayCauseLag.AlwaysUpdateTitlesWhenXPIsGained.ExtraDetails", false);
    }

    public boolean isExperienceBarsEnabled() {
        return getBooleanValue("Experience_Bars.Enable", true);
    }

    public boolean isExperienceBarEnabled(PrimarySkillType primarySkillType) {
        return getBooleanValue("Experience_Bars." + StringUtils.getCapitalized(primarySkillType.toString()) + ".Enable", true);
    }

    public BarColor getExperienceBarColor(PrimarySkillType primarySkillType) {
        String colorValueFromConfig = getStringValue("Experience_Bars." + StringUtils.getCapitalized(primarySkillType.toString()) + ".Color");

        for (BarColor barColor : BarColor.values()) {
            if (barColor.toString().equalsIgnoreCase(colorValueFromConfig))
                return barColor;
        }

        //In case the value is invalid
        return BarColor.WHITE;
    }

    public BarStyle getExperienceBarStyle(PrimarySkillType primarySkillType) {
        String colorValueFromConfig = getStringValue("Experience_Bars." + StringUtils.getCapitalized(primarySkillType.toString()) + ".BarStyle");

        for (BarStyle barStyle : BarStyle.values()) {
            if (barStyle.toString().equalsIgnoreCase(colorValueFromConfig))
                return barStyle;
        }

        //In case the value is invalid
        return BarStyle.SOLID;
    }

    /* Acrobatics */
    public int getDodgeXPModifier() {
        return getIntValue("Experience.Acrobatics.Dodge", 120);
    }

    public int getRollXPModifier() {
        return getIntValue("Experience.Acrobatics.Roll", 80);
    }

    public int getFallXPModifier() {
        return getIntValue("Experience.Acrobatics.Fall", 120);
    }

    public double getFeatherFallXPModifier() {
        return getDoubleValue("Experience.Acrobatics.FeatherFall_Multiplier", 2.0);
    }

    /* Alchemy */
    public double getPotionXP(PotionStage stage) {
        return getDoubleValue("Experience.Alchemy.Potion_Stage_" + stage.toNumerical(), 10D);
    }

    /* Archery */
    public double getArcheryDistanceMultiplier() {
        return getDoubleValue("Experience.Archery.Distance_Multiplier", 0.025);
    }

    public int getFishingShakeXP() {
        return getIntValue("Experience.Fishing.Shake", 50);
    }

    /* Repair */
    public double getRepairXPBase() {
        return getDoubleValue("Experience.Repair.Base", 1000.0);
    }

    public double getRepairXP(MaterialType repairMaterialType) {
        return getDoubleValue("Experience.Repair." + StringUtils.getCapitalized(repairMaterialType.toString()));
    }

    /* Taming */
    public int getTamingXP(EntityType type) {
        return getIntValue("Experience.Taming.Animal_Taming." + StringUtils.getPrettyEntityTypeString(type));
    }
}
