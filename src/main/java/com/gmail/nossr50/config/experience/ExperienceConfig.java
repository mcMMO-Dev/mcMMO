package com.gmail.nossr50.config.experience;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.EntityType;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AutoUpdateConfigLoader;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.util.StringUtils;

public class ExperienceConfig extends AutoUpdateConfigLoader {
    private static ExperienceConfig instance;

    private ExperienceConfig() {
        super("experience.yml");
        validate();
    }

    public static ExperienceConfig getInstance() {
        if (instance == null) {
            instance = new ExperienceConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {}

    @Override
    protected boolean validateKeys() {
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

        return noErrorsInConfig(reason);
    }

    /*
     * FORMULA SETTINGS
     */

    /* Curve settings */
    public FormulaType getFormulaType() { return FormulaType.getFormulaType(config.getString("Experience_Formula.Curve")); }
    public boolean getCumulativeCurveEnabled() { return config.getBoolean("Experience_Formula.Cumulative_Curve", false); }

    /* Curve values */
    public double getMultiplier(FormulaType type) { return config.getDouble("Experience_Formula." + StringUtils.getCapitalized(type.toString()) + "_Values.multiplier"); }
    public int getBase(FormulaType type) { return config.getInt("Experience_Formula." + StringUtils.getCapitalized(type.toString()) + "_Values.base"); }
    public double getExponent(FormulaType type) { return config.getDouble("Experience_Formula." + StringUtils.getCapitalized(type.toString()) + "_Values.exponent"); }

    /* Global modifier */
    public double getExperienceGainsGlobalMultiplier() { return config.getDouble("Experience_Formula.Multiplier.Global", 1.0); }
    public void setExperienceGainsGlobalMultiplier(double value) { config.set("Experience_Formula.Multiplier.Global", value); }

    /* PVP modifier */
    public double getPlayerVersusPlayerXP() { return config.getDouble("Experience_Formula.Multiplier.PVP", 1.0); }

    /* Spawned Mob modifier */
    public double getSpawnedMobXpMultiplier() { return config.getDouble("Experience_Formula.Mobspawners.Multiplier", 0.0); }
    public double getBredMobXpMultiplier() { return config.getDouble("Experience_Formula.Breeding.Multiplier", 1.0); }

    /* Skill modifiers */
    public double getFormulaSkillModifier(SkillType skill) { return config.getDouble("Experience_Formula.Modifier." + StringUtils.getCapitalized(skill.toString())); }

    /* Custom XP perk */
    public double getCustomXpPerkBoost() { return config.getDouble("Experience_Formula.Custom_XP_Perk.Boost", 1.25); }

    /* Diminished Returns */
    public boolean getDiminishedReturnsEnabled() { return config.getBoolean("Diminished_Returns.Enabled", false); }
    public int getDiminishedReturnsThreshold(SkillType skill) { return config.getInt("Diminished_Returns.Threshold." + StringUtils.getCapitalized(skill.toString()), 20000); }
    public int getDiminishedReturnsTimeInterval() { return config.getInt("Diminished_Returns.Time_Interval", 10); }

    /* Conversion */
    public double getExpModifier() { return config.getDouble("Conversion.Exp_Modifier", 1); }

    /*
     * XP SETTINGS
     */

    /* General Settings */
    public boolean getExperienceGainsPlayerVersusPlayerEnabled() { return config.getBoolean("Experience.PVP.Rewards", true); }

    /* Combat XP Multipliers */
    public double getCombatXP(EntityType entity) { return config.getDouble("Experience.Combat.Multiplier." + StringUtils.getPrettyEntityTypeString(entity).replace(" ", "_")); }
    public double getAnimalsXP(EntityType entity) { return config.getDouble("Experience.Combat.Multiplier." + StringUtils.getPrettyEntityTypeString(entity).replace(" ", "_"), getAnimalsXP()); }
    public double getAnimalsXP() { return config.getDouble("Experience.Combat.Multiplier.Animals", 1.0); }

    /* Materials  */
    public int getXp(SkillType skill, MaterialData data)
    {
        String baseString = "Experience." + StringUtils.getCapitalized(skill.toString()) + ".";
        String explicitString = baseString + StringUtils.getExplicitConfigMaterialDataString(data);
        if (config.contains(explicitString))
            return config.getInt(explicitString);
        String friendlyString = baseString + StringUtils.getFriendlyConfigMaterialDataString(data);
        if (config.contains(friendlyString))
            return config.getInt(friendlyString);
        String wildcardString = baseString + StringUtils.getWildcardConfigMaterialDataString(data);
        if (config.contains(wildcardString))
            return config.getInt(wildcardString);
        return 0;
    }
    
    public boolean isSkillBlock(SkillType skill, MaterialData data)
    {
        String baseString = "Experience." + StringUtils.getCapitalized(skill.toString()) + ".";
        String explicitString = baseString + StringUtils.getExplicitConfigMaterialDataString(data);
        if (config.contains(explicitString))
            return true;
        String friendlyString = baseString + StringUtils.getFriendlyConfigMaterialDataString(data);
        if (config.contains(friendlyString))
            return true;
        String wildcardString = baseString + StringUtils.getWildcardConfigMaterialDataString(data);
        if (config.contains(wildcardString))
            return true;
        return false;
    }

    /* Acrobatics */
    public int getDodgeXPModifier() { return config.getInt("Experience.Acrobatics.Dodge", 120); }
    public int getRollXPModifier() { return config.getInt("Experience.Acrobatics.Roll", 80); }
    public int getFallXPModifier() { return config.getInt("Experience.Acrobatics.Fall", 120); }

    public double getFeatherFallXPModifier() { return config.getDouble("Experience.Acrobatics.FeatherFall_Multiplier", 2.0); }

    /* Alchemy */
    public double getPotionXP(PotionStage stage) { return config.getDouble("Experience.Alchemy.Potion_Stage_" + stage.toNumerical(), 10D); }

    /* Archery */
    public double getArcheryDistanceMultiplier() { return config.getDouble("Experience.Archery.Distance_Multiplier", 0.025); }

    public int getFishingShakeXP() { return config.getInt("Experience.Fishing.Shake", 50); }

    /* Repair */
    public double getRepairXPBase() { return config.getDouble("Experience.Repair.Base", 1000.0); }
    public double getRepairXP(MaterialType repairMaterialType) { return config.getDouble("Experience.Repair." + StringUtils.getCapitalized(repairMaterialType.toString())); }

    /* Taming */
    public int getTamingXP(EntityType type)
    {
        return config.getInt("Experience.Taming.Animal_Taming." + StringUtils.getPrettyEntityTypeString(type));
    }
}
