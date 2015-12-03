package com.gmail.nossr50.config.experience;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.EntityType;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;

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

        if (getWitherSkeletonXP() < 0) {
            reason.add("Experience.Combat.Multiplier.Wither_Skeleton should be at least 0!");
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
        if (getTamingXPWolf() <= 0) {
            reason.add("Experience.Taming.Animal_Taming.Wolf should be greater than 0!");
        }

        if (getTamingXPOcelot() <= 0) {
            reason.add("Experience.Taming.Animal_Taming.Ocelot should be greater than 0!");
        }

        /* Woodcutting */
        for (TreeSpecies species : TreeSpecies.values()) {
            String key = "Experience.Woodcutting." + StringUtils.getPrettyTreeSpeciesString(species).replace(" ", "_");

            if (config.getInt(key) <= 0) {
                reason.add(key + " should be greater than 0!");
            }
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
    public double getAnimalsXP() { return config.getDouble("Experience.Combat.Multiplier.Animals", 1.0); }
    public double getWitherSkeletonXP() { return config.getDouble("Experience.Combat.Multiplier.Wither_Skeleton", 4.0); }
    public double getElderGuardianXP() { return config.getDouble("Experience.Combat.Multiplier.Elder_Guardian", 4.0); }

    /* Materials  */
    public int getXp(SkillType skill, Material material) { return config.getInt("Experience." + StringUtils.getCapitalized(skill.toString()) + "." + StringUtils.getPrettyItemString(material).replace(" ", "_")); }

    /* Acrobatics */
    public int getDodgeXPModifier() { return config.getInt("Experience.Acrobatics.Dodge", 120); }
    public int getRollXPModifier() { return config.getInt("Experience.Acrobatics.Roll", 80); }
    public int getFallXPModifier() { return config.getInt("Experience.Acrobatics.Fall", 120); }

    public double getFeatherFallXPModifier() { return config.getDouble("Experience.Acrobatics.FeatherFall_Multiplier", 2.0); }

    /* Alchemy */
    public double getPotionXP(PotionStage stage) { return config.getDouble("Experience.Alchemy.Potion_Stage_" + stage.toNumerical(), 10D); }

    /* Archery */
    public double getArcheryDistanceMultiplier() { return config.getDouble("Experience.Archery.Distance_Multiplier", 0.025); }

    /* Excavation */
    public int getDirtAndSandXp(MaterialData data) {
        Material type = data.getItemType();

        if (type == Material.DIRT) {
            switch (data.getData()) {
                case 0x0:
                    return config.getInt("Experience.Excavation.Dirt", 40);

                case 0x1:
                    return config.getInt("Experience.Excavation.Coarse_Dirt", 40);

                case 0x2:
                    return config.getInt("Experience.Excavation.Podzol", 40);

                default:
                    return 0;
            }
        }
        else if (type == Material.SAND) {
            switch (data.getData()) {
                case 0x0:
                    return config.getInt("Experience.Excavation.Sand", 40);

                case 0x1:
                    return config.getInt("Experience.Excavation.Red_Sand", 40);

                default:
                    return 0;
            }
        }

        return 0;
    }

    /* Fishing */
    public int getFishXp(MaterialData data) {
        switch (data.getData()) {
            case 0x0:
                return config.getInt("Experience.Fishing.Raw_Fish", 800);

            case 0x1:
                return config.getInt("Experience.Fishing.Raw_Salmon", 800);

            case 0x2:
                return config.getInt("Experience.Fishing.Clownfish", 800);

            case 0x3:
                return config.getInt("Experience.Fishing.Pufferfish", 800);

            default:
                return 0;
        }
    }

    public int getFishingShakeXP() { return config.getInt("Experience.Fishing.Shake", 50); }

    /* Herbalism */
    public int getFlowerAndGrassXp(MaterialData data) {
        Material type = data.getItemType();

        if (type == Material.RED_ROSE) {
            switch (data.getData()) {
                case 0x0:
                    return config.getInt("Experience.Herbalism.Poppy", 100);

                case 0x1:
                    return config.getInt("Experience.Herbalism.Blue_Orchid", 150);

                case 0x2:
                    return config.getInt("Experience.Herbalism.Allium", 300);

                case 0x3:
                    return config.getInt("Experience.Herbalism.Azure_Bluet", 150);

                case 0x4:
                    return config.getInt("Experience.Herbalism.Red_Tulip", 150);

                case 0x5:
                    return config.getInt("Experience.Herbalism.Orange_Tulip", 150);

                case 0x6:
                    return config.getInt("Experience.Herbalism.White_Tulip", 150);

                case 0x7:
                    return config.getInt("Experience.Herbalism.Pink_Tulip", 150);

                case 0x8:
                    return config.getInt("Experience.Herbalism.Oxeye_Daisy", 150);

                default:
                    return 0;
            }

        }
        else if (type == Material.LONG_GRASS) {
            GrassSpecies species = ((LongGrass) data).getSpecies();
            if (species == null) {
                return 0;
            }

            switch (species) {
                case DEAD:
                    return config.getInt("Experience.Herbalism.Dead_Bush", 30);

                case FERN_LIKE:
                    return config.getInt("Experience.Herbalism.Small_Fern", 10);

                case NORMAL:
                    return config.getInt("Experience.Herbalism.Small_Grass", 10);

                default:
                    return 0;
            }
        }
        else if (type == Material.DOUBLE_PLANT) {
            switch (data.getData()) {
                case 0x0:
                    return config.getInt("Experience.Herbalism.Sunflower", 50);

                case 0x1:
                    return config.getInt("Experience.Herbalism.Lilac", 50);

                case 0x2:
                    return config.getInt("Experience.Herbalism.Tall_Grass", 50);

                case 0x3:
                    return config.getInt("Experience.Herbalism.Tall_Fern", 50);

                case 0x4:
                    return config.getInt("Experience.Herbalism.Rose_Bush", 50);

                case 0x5:
                    return config.getInt("Experience.Herbalism.Peony", 50);

                default:
                    return 0;
            }
        }

        return 0;
    }

    /* Repair */
    public double getRepairXPBase() { return config.getDouble("Experience.Repair.Base", 1000.0); }
    public double getRepairXP(MaterialType repairMaterialType) { return config.getDouble("Experience.Repair." + StringUtils.getCapitalized(repairMaterialType.toString())); }

    /* Taming */
    public int getTamingXPHorse() { return config.getInt("Experience.Taming.Animal_Taming.Horse", 1000); }
    public int getTamingXPWolf() { return config.getInt("Experience.Taming.Animal_Taming.Wolf", 250); }
    public int getTamingXPOcelot() { return config.getInt("Experience.Taming.Animal_Taming.Ocelot", 500); }

    /* Woodcutting */
    public int getWoodcuttingTreeXP(TreeSpecies species) { return config.getInt("Experience.Woodcutting." + StringUtils.getPrettyTreeSpeciesString(species).replace(" ", "_")); }
    public int getWoodcuttingXPHugeBrownMushroom() { return config.getInt("Experience.Woodcutting.Huge_Mushroom_Brown", 70); }
    public int getWoodcuttingXPHugeRedMushroom() { return config.getInt("Experience.Woodcutting.Huge_Mushroom_Red", 70); }

}
