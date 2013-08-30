package com.gmail.nossr50.config.experience;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.gmail.nossr50.config.AutoUpdateConfigLoader;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.skills.repair.RepairMaterialType;
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

        /* Conversion */
        if (getExpModifier() <= 0) {
            reason.add("Conversion.Exp_Modifier should be greater than 0!");
        }

        /*
         * XP SETTINGS
         */

        /* Combat XP Multipliers */
        if (getAnimalsXP() < 0) {
            reason.add("Experience.Combat.Multiplier.Animals should be at least 0!");
        }

        if (getWitherSkeletonXP() < 0) {
            reason.add("Experience.Combat.Multiplier.Wither_Skeleton should be at least 0!");
        }

        /* Fishing */
        if (getFishingBaseXP() <= 0) {
            reason.add("Experience.Fishing.Base should be greater than 0!");
        }

        /* Repair */
        if (getRepairXPBase() <= 0) {
            reason.add("Experience.Repair.Base should be greater than 0!");
        }

        /* Taming */
        if (getTamingXPWolf() <= 0) {
            reason.add("Experience.Taming.Animal_Taming.Wolf should be greater than 0!");
        }

        if (getTamingXPOcelot() <= 0) {
            reason.add("Experience.Taming.Animal_Taming.Ocelot should be greater than 0!");
        }

        /* Woodcutting */
        if (getWoodcuttingXPOak() <= 0) {
            reason.add("Experience.Woodcutting.Oak should be greater than 0!");
        }

        if (getWoodcuttingXPBirch() <= 0) {
            reason.add("Experience.Woodcutting.Birch should be greater than 0!");
        }

        if (getWoodcuttingXPSpruce() <= 0) {
            reason.add("Experience.Woodcutting.Spruce should be greater than 0!");
        }

        if (getWoodcuttingXPJungle() <= 0) {
            reason.add("Experience.Woodcutting.Jungle should be greater than 0!");
        }

        if (getWoodcuttingXPHugeBrownMushroom() <= 0) {
            reason.add("Experience.Woodcutting.Huge_Mushroom_Brown should be greater than 0!");
        }

        if (getWoodcuttingXPHugeRedMushroom() <= 0) {
            reason.add("Experience.Woodcutting.Huge_Mushroom_Red should be greater than 0!");
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
    public double getExponent(FormulaType type) { return config.getDouble("Experience_Formula." + StringUtils.getCapitalized(type.toString()) +"_Values.exponent"); }

    /* Global modifier */
    public double getExperienceGainsGlobalMultiplier() { return config.getDouble("Experience_Formula.Multiplier.Global", 1.0); }
    public void setExperienceGainsGlobalMultiplier(double value) { config.set("Experience_Formula.Multiplier.Global", value); }

    /* PVP modifier */
    public double getPlayerVersusPlayerXP() { return config.getDouble("Experience_Formula.Multiplier.PVP", 1.0); }

    /* Spawned Mob modifier */
    public double getSpawnedMobXpMultiplier() { return config.getDouble("Experience_Formula.Mobspawners.Multiplier", 0.0); }

    /* Skill modifiers */
    public double getFormulaSkillModifier(SkillType skill) { return config.getDouble("Experience_Formula.Modifier." + StringUtils.getCapitalized(skill.toString())); }

    /* Conversion */
    public double getExpModifier() { return config.getDouble("Conversion.Exp_Modifier", 1); }

    /*
     * XP SETTINGS
     */

    /* General Settings */
    public boolean getExperienceGainsPlayerVersusPlayerEnabled() { return config.getBoolean("Experience.PVP.Rewards", true); }

    /* Combat XP Multipliers */
    public double getCombatXP(EntityType entity) { return config.getDouble("Experience.Combat.Multiplier." + StringUtils.getPrettyEntityTypeString(entity).replace(" ", "_")); }
    public double getAnimalsXP() { return config.getDouble("Experience.Combat.Multiplier.Animals", 1.0); }
    public double getWitherSkeletonXP() { return config.getDouble("Experience.Combat.Multiplier.Wither_Skeleton", 4.0); }

    /* Materials  */
    public int getXp(SkillType skill, Material material) { return config.getInt("Experience." + StringUtils.getCapitalized(skill.toString()) + "." + StringUtils.getPrettyItemString(material).replace(" ", "_")); }

    /* Fishing */
    public int getFishingBaseXP() { return config.getInt("Experience.Fishing.Base", 800); }

    /* Repair */
    public double getRepairXPBase() { return config.getDouble("Experience.Repair.Base", 1000.0); }
    public double getRepairXP(RepairMaterialType repairMaterialType) { return config.getDouble("Experience.Repair." + StringUtils.getCapitalized(repairMaterialType.toString())); }

    /* Taming */
    public int getTamingXPHorse() { return config.getInt("Experience.Taming.Animal_Taming.Horse", 1000); }
    public int getTamingXPWolf() { return config.getInt("Experience.Taming.Animal_Taming.Wolf", 250); }
    public int getTamingXPOcelot() { return config.getInt("Experience.Taming.Animal_Taming.Ocelot", 500); }

    /* Woodcutting */
    public int getWoodcuttingXPOak() { return config.getInt("Experience.Woodcutting.Oak", 70); }
    public int getWoodcuttingXPBirch() { return config.getInt("Experience.Woodcutting.Birch", 90); }
    public int getWoodcuttingXPSpruce() { return config.getInt("Experience.Woodcutting.Spruce", 80); }
    public int getWoodcuttingXPJungle() { return config.getInt("Experience.Woodcutting.Jungle", 100); }
    public int getWoodcuttingXPHugeBrownMushroom() { return config.getInt("Experience.Woodcutting.Huge_Mushroom_Brown", 70); }
    public int getWoodcuttingXPHugeRedMushroom() { return config.getInt("Experience.Woodcutting.Huge_Mushroom_Red", 70); }
}
