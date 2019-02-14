package com.gmail.nossr50.core.config;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.datatypes.interactions.NotificationType;
import com.gmail.nossr50.core.mcmmo.colors.ChatColor;
import com.gmail.nossr50.core.skills.SubSkillType;
import com.gmail.nossr50.core.skills.subskills.AbstractSubSkill;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class AdvancedConfig extends ConfigurableLoader {
    public static final String SKILLS = "Skills";
    public static final String GENERAL = "General";
    public static final String ABILITY = "Ability";
    public static final String LENGTH = "Length";
    public static final String INCREASE_LEVEL = "IncreaseLevel";
    public static final String ENCHANT_BUFF = "EnchantBuff";
    public static final String ACROBATICS = "Acrobatics";
    public static final String DODGE = "Dodge";
    public static final String CHANCE = "Chance";
    public static final String CHANCE_MAX = CHANCE + "Max";
    public static final String MAX_BONUS_LEVEL = "MaxBonusLevel";
    public static final String MODIFIER = "Modifier";
    public static final String DAMAGE_MODIFIER = "Damage" + MODIFIER;
    public static final String DAMAGE_THRESHOLD = "DamageThreshold";
    public static final String ALCHEMY = "Alchemy";
    public static final String CATALYSIS = "Catalysis";
    public static final String MIN_SPEED = "MinSpeed";
    public static final String MAX_SPEED = "MaxSpeed";
    public static final String ARCHERY = "Archery";
    public static final String SKILL_SHOT = "SkillShot";
    public static final String RANK_DAMAGE_MULTIPLIER = "RankDamageMultiplier";
    public static final String BONUS_DAMAGE = "BonusDamage";
    public static final String FORCE_MULTIPLIER = "ForceMultiplier";
    public static final String AXES = "Axes";
    public static final String STANDARD = "Standard";
    public static final String RETRO_MODE = "RetroMode";
    public static final String CAP_LEVEL = "CapLevel";
    public static final String KNOCKBACK_MODIFIER = "Knockback" + MODIFIER;
    public static final String PVP_MODIFIER = "PVP_" + MODIFIER;
    public static final String PVE_MODIFIER = "PVE_" + MODIFIER;
    public static final String FISHING = "Fishing";
    public static final String MASTER_ANGLER = "MasterAngler";
    public static final String BOAT_MODIFIER = "Boat" + MODIFIER;
    public static final String BIOME_MODIFIER = "Biome" + MODIFIER;
    public static final String VANILLA_XPMULTIPLIER = "VanillaXPMultiplier";
    public static final String RANK = "Rank_";
    public static final String TAMING = "Taming";
    public static final String CALL_OF_THE_WILD = "CallOfTheWild";
    public static final String MIN_HORSE_JUMP_STRENGTH = "MinHorseJumpStrength";
    public static final String MAX_HORSE_JUMP_STRENGTH = "MaxHorseJumpStrength";
    public static final String SHOCK_PROOF = "ShockProof";
    public static final String UNARMED = "Unarmed";
    public static final String STARTING_LEVEL = "StartingLevel";
    private static AdvancedConfig instance;

    private AdvancedConfig() {
        super(McmmoCore.getDataFolderPath().getAbsoluteFile(), "advanced.yml");
    }

    public static AdvancedConfig getInstance() {
        if (instance == null) {
            instance = new AdvancedConfig();
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
    public List<String> validateKeys() {
        // Validate all the settings!
        List<String> reason = new ArrayList<String>();

        /* GENERAL */
        if (getAbilityLength() < 1) {
            reason.add(SKILLS + "." + GENERAL + "." + ABILITY + "." + LENGTH + ".<mode>." + INCREASE_LEVEL + " should be at least 1!");
        }

        if (getEnchantBuff() < 1) {
            reason.add(SKILLS + "." + GENERAL + "." + ABILITY + "." + ENCHANT_BUFF + " should be at least 1!");
        }

        /* ACROBATICS */
        if (getMaximumProbability(SubSkillType.ACROBATICS_DODGE) < 1) {
            reason.add(SKILLS + "." + ACROBATICS + "." + DODGE + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ACROBATICS_DODGE) < 1) {
            reason.add(SKILLS + "." + ACROBATICS + "." + DODGE + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getDodgeDamageModifier() <= 1) {
            reason.add(SKILLS + "." + ACROBATICS + "." + DODGE + "." + DAMAGE_MODIFIER + " should be greater than 1!");
        }

        if (getMaximumProbability(SubSkillType.ACROBATICS_ROLL) < 1) {
            reason.add(SKILLS + "." + ACROBATICS + ".Roll." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ACROBATICS_ROLL) < 1) {
            reason.add(SKILLS + "." + ACROBATICS + ".Roll." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getRollDamageThreshold() < 0) {
            reason.add(SKILLS + "." + ACROBATICS + ".Roll." + DAMAGE_THRESHOLD + " should be at least 0!");
        }

        if (getGracefulRollDamageThreshold() < 0) {
            reason.add(SKILLS + "." + ACROBATICS + ".GracefulRoll." + DAMAGE_THRESHOLD + " should be at least 0!");
        }

        if (getCatalysisMinSpeed() <= 0) {
            reason.add(SKILLS + "." + ALCHEMY + "." + CATALYSIS + "." + MIN_SPEED + " must be greater than 0!");
        }

        if (getCatalysisMaxSpeed() < getCatalysisMinSpeed()) {
            reason.add(SKILLS + "." + ALCHEMY + "." + CATALYSIS + "." + MAX_SPEED + " should be at least Skills.Alchemy.Catalysis." + MIN_SPEED + "!");
        }

        /* ARCHERY */

        if (getSkillShotRankDamageMultiplier() <= 0) {
            reason.add(SKILLS + "." + ARCHERY + "." + SKILL_SHOT + "." + RANK_DAMAGE_MULTIPLIER + " should be greater than 0!");
        }

        if (getMaximumProbability(SubSkillType.ARCHERY_DAZE) < 1) {
            reason.add(SKILLS + "." + ARCHERY + ".Daze." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ARCHERY_DAZE) < 1) {
            reason.add(SKILLS + "." + ARCHERY + ".Daze." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getDazeBonusDamage() < 0) {
            reason.add(SKILLS + "." + ARCHERY + ".Daze." + BONUS_DAMAGE + " should be at least 0!");
        }

        if (getMaximumProbability(SubSkillType.ARCHERY_ARROW_RETRIEVAL) < 1) {
            reason.add(SKILLS + "." + ARCHERY + ".Retrieve." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ARCHERY_ARROW_RETRIEVAL) < 1) {
            reason.add(SKILLS + "." + ARCHERY + ".Retrieve." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getForceMultiplier() < 0) {
            reason.add(SKILLS + "." + ARCHERY + "." + FORCE_MULTIPLIER + " should be at least 0!");
        }

        /* AXES */
        if(getAxeMasteryRankDamageMultiplier() < 0)
        {
            reason.add(SKILLS + "." + AXES + ".AxeMastery." + RANK_DAMAGE_MULTIPLIER + " should be at least 0!");
        }

        if (getMaximumProbability(SubSkillType.AXES_CRITICAL_STRIKES) < 1) {
            reason.add(SKILLS + "." + AXES + ".CriticalHit." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.AXES_CRITICAL_STRIKES) < 1) {
            reason.add(SKILLS + "." + AXES + ".CriticalHit." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getCriticalStrikesPVPModifier() < 1) {
            reason.add(SKILLS + "." + AXES + ".CriticalStrikes." + PVP_MODIFIER + " should be at least 1!");
        }

        if (getCriticalStrikesPVPModifier() < 1) {
            reason.add(SKILLS + "." + AXES + ".CriticalStrikes." + PVE_MODIFIER + " should be at least 1!");
        }

        if (getGreaterImpactChance() < 1) {
            reason.add(SKILLS + "." + AXES + ".GreaterImpact." + CHANCE + " should be at least 1!");
        }

        if (getGreaterImpactModifier() < 1) {
            reason.add(SKILLS + "." + AXES + ".GreaterImpact." + KNOCKBACK_MODIFIER + " should be at least 1!");
        }

        if (getGreaterImpactBonusDamage() < 1) {
            reason.add(SKILLS + "." + AXES + ".GreaterImpact." + BONUS_DAMAGE + " should be at least 1!");
        }

        if (getArmorImpactIncreaseLevel() < 1) {
            reason.add(SKILLS + "." + AXES + ".ArmorImpact." + INCREASE_LEVEL + " should be at least 1!");
        }

        if (getImpactChance() < 1) {
            reason.add(SKILLS + "." + AXES + ".ArmorImpact." + CHANCE + " should be at least 1!");
        }

        if (getArmorImpactMaxDurabilityDamage() < 1) {
            reason.add(SKILLS + "." + AXES + ".ArmorImpact.MaxPercentageDurabilityDamage should be at least 1!");
        }

        if (getSkullSplitterModifier() < 1) {
            reason.add(SKILLS + "." + AXES + ".SkullSplitter." + DAMAGE_MODIFIER + " should be at least 1!");
        }

        /*if (getFishermanDietRankChange() < 1) {
            reason.add(SKILLS + "." + FISHING + ".FishermansDiet.RankChange should be at least 1!");
        }*/

        if (getMasterAnglerBoatModifier() < 1) {
            reason.add(SKILLS + "." + FISHING + "." + MASTER_ANGLER + "." + BOAT_MODIFIER + " should be at least 1!");
        }

        if (getMasterAnglerBiomeModifier() < 1) {
            reason.add(SKILLS + "." + FISHING + "." + MASTER_ANGLER + "." + BIOME_MODIFIER + " should be at least 1!");
        }

        /* HERBALISM */
        /*if (getFarmerDietRankChange() < 1) {
            reason.add(SKILLS + ".Herbalism.FarmersDiet.RankChange should be at least 1!");
        }

        if (getGreenThumbStageChange() < 1) {
            reason.add(SKILLS + ".Herbalism.GreenThumb.StageChange should be at least 1!");
        }*/

        if (getMaximumProbability(SubSkillType.HERBALISM_GREEN_THUMB) < 1) {
            reason.add(SKILLS + ".Herbalism.GreenThumb." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_GREEN_THUMB) < 1) {
            reason.add(SKILLS + ".Herbalism.GreenThumb." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.HERBALISM_DOUBLE_DROPS) < 1) {
            reason.add(SKILLS + ".Herbalism.DoubleDrops." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_DOUBLE_DROPS) < 1) {
            reason.add(SKILLS + ".Herbalism.DoubleDrops." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.HERBALISM_HYLIAN_LUCK) < 1) {
            reason.add(SKILLS + ".Herbalism.HylianLuck." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_HYLIAN_LUCK) < 1) {
            reason.add(SKILLS + ".Herbalism.HylianLuck." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.HERBALISM_SHROOM_THUMB) < 1) {
            reason.add(SKILLS + ".Herbalism.ShroomThumb." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_SHROOM_THUMB) < 1) {
            reason.add(SKILLS + ".Herbalism.ShroomThumb." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        /* MINING */
        if (getMaximumProbability(SubSkillType.MINING_DOUBLE_DROPS) < 1) {
            reason.add(SKILLS + ".Mining.DoubleDrops." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.MINING_DOUBLE_DROPS) < 1) {
            reason.add(SKILLS + ".Mining.DoubleDrops." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        /* REPAIR */
        if (getRepairMasteryMaxBonus() < 1) {
            reason.add(SKILLS + ".Repair.RepairMastery.MaxBonusPercentage should be at least 1!");
        }

        if (getRepairMasteryMaxLevel() < 1) {
            reason.add(SKILLS + ".Repair.RepairMastery." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.REPAIR_SUPER_REPAIR) < 1) {
            reason.add(SKILLS + ".Repair.SuperRepair." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.REPAIR_SUPER_REPAIR) < 1) {
            reason.add(SKILLS + ".Repair.SuperRepair." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        /* SMELTING */
        if (getBurnModifierMaxLevel() < 1) {
            reason.add(SKILLS + ".Smelting.FuelEfficiency." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getBurnTimeMultiplier() < 1) {
            reason.add(SKILLS + ".Smelting.FuelEfficiency.Multiplier should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SMELTING_SECOND_SMELT) < 1) {
            reason.add(SKILLS + ".Smelting.SecondSmelt." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.SMELTING_SECOND_SMELT) < 1) {
            reason.add(SKILLS + ".Smelting.SecondSmelt." + CHANCE_MAX + " should be at least 1!");
        }

        if (getFluxMiningChance() < 1) {
            reason.add(SKILLS + ".Smelting.FluxMining." + CHANCE + " should be at least 1!");
        }

        /* SWORDS */
        if (getMaximumProbability(SubSkillType.SWORDS_RUPTURE) < 1) {
            reason.add(SKILLS + ".Swords.Rupture." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SWORDS_RUPTURE) < 1) {
            reason.add(SKILLS + ".Swords.Rupture." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getRuptureMaxTicks() < 1) {
            reason.add(SKILLS + ".Swords.Rupture.MaxTicks should be at least 1!");
        }

        if (getRuptureMaxTicks() < getRuptureBaseTicks()) {
            reason.add(SKILLS + ".Swords.Rupture.MaxTicks should be at least Skills.Swords.Rupture.BaseTicks!");
        }

        if (getRuptureBaseTicks() < 1) {
            reason.add(SKILLS + ".Swords.Rupture.BaseTicks should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.SWORDS_COUNTER_ATTACK) < 1) {
            reason.add(SKILLS + ".Swords.CounterAttack." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SWORDS_COUNTER_ATTACK) < 1) {
            reason.add(SKILLS + ".Swords.CounterAttack." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getCounterModifier() < 1) {
            reason.add(SKILLS + ".Swords.CounterAttack." + DAMAGE_MODIFIER + " should be at least 1!");
        }

        if (getSerratedStrikesModifier() < 1) {
            reason.add(SKILLS + ".Swords.SerratedStrikes." + DAMAGE_MODIFIER + " should be at least 1!");
        }

        if (getSerratedStrikesTicks() < 1) {
            reason.add(SKILLS + ".Swords.SerratedStrikes.RuptureTicks should be at least 1!");
        }

        /* TAMING */

        if (getMaximumProbability(SubSkillType.TAMING_GORE) < 1) {
            reason.add(SKILLS + "." + TAMING + ".Gore." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.TAMING_GORE) < 1) {
            reason.add(SKILLS + "." + TAMING + ".Gore." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getGoreModifier() < 1) {
            reason.add(SKILLS + "." + TAMING + ".Gore." + MODIFIER + " should be at least 1!");
        }

        if (getFastFoodChance() < 1) {
            reason.add(SKILLS + "." + TAMING + ".FastFood." + CHANCE + " should be at least 1!");
        }

        if (getThickFurModifier() < 1) {
            reason.add(SKILLS + "." + TAMING + ".ThickFur." + MODIFIER + " should be at least 1!");
        }

        if (getShockProofModifier() < 1) {
            reason.add(SKILLS + "." + TAMING + "." + SHOCK_PROOF + "." + MODIFIER + " should be at least 1!");
        }

        if (getSharpenedClawsBonus() < 1) {
            reason.add(SKILLS + "." + TAMING + ".SharpenedClaws.Bonus should be at least 1!");
        }

        if (getMaxHorseJumpStrength() < 0 || getMaxHorseJumpStrength() > 2) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD + "." + MAX_HORSE_JUMP_STRENGTH + " should be between 0 and 2!");
        }

        /* UNARMED */
        if (getMaximumProbability(SubSkillType.UNARMED_DISARM) < 1) {
            reason.add(SKILLS + "." + UNARMED + ".Disarm." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_DISARM) < 1) {
            reason.add(SKILLS + "." + UNARMED + ".Disarm." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.UNARMED_ARROW_DEFLECT) < 1) {
            reason.add(SKILLS + "." + UNARMED + ".ArrowDeflect." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_ARROW_DEFLECT) < 1) {
            reason.add(SKILLS + "." + UNARMED + ".ArrowDeflect." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.UNARMED_IRON_GRIP) < 1) {
            reason.add(SKILLS + "." + UNARMED + ".IronGrip." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_IRON_GRIP) < 1) {
            reason.add(SKILLS + "." + UNARMED + ".IronGrip." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        /* WOODCUTTING */

        /*if (getLeafBlowUnlockLevel() < 0) {
            reason.add("Skills.Woodcutting.LeafBlower.UnlockLevel should be at least 0!");
        }*/

        if (getMaximumProbability(SubSkillType.WOODCUTTING_HARVEST_LUMBER) < 1) {
            reason.add(SKILLS + ".Woodcutting.HarvestLumber." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER) < 1) {
            reason.add(SKILLS + ".Woodcutting.HarvestLumber." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        return reason;
    }

    /* GENERAL */
    public int getStartingLevel() { return getIntValue(SKILLS, GENERAL, STARTING_LEVEL); }

    /**
     * This returns the maximum level at which superabilities will stop lengthening from scaling alongside skill level.
     * It returns a different value depending on whether or not the server is in retro mode
     * @return the level at which abilities stop increasing in length
     */
    public int getAbilityLengthCap() {
        if(!McmmoCore.isRetroModeEnabled())
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, STANDARD, CAP_LEVEL);
        else
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, RETRO_MODE, CAP_LEVEL);
    }

    /**
     * This returns the frequency at which abilities will increase in length
     * It returns a different value depending on whether or not the server is in retro mode
     * @return the number of levels required per ability length increase
     */
    public int getAbilityLength() {
        if(!McmmoCore.isRetroModeEnabled())
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, STANDARD, INCREASE_LEVEL);
        else
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, RETRO_MODE, INCREASE_LEVEL);
    }

    public int getEnchantBuff() { return getIntValue(SKILLS, GENERAL, ABILITY, ENCHANT_BUFF); }

    /**
     * Grabs the max bonus level for a skill used in RNG calculations
     * All max level values in the config are multiplied by 10 if the server is in retro mode as the values in the config are based around the new 1-100 skill system scaling
     * A value of 10 in the file will be returned as 100 for retro mode servers to accommodate the change in scaling
     * @param subSkillType target subskill
     * @return the level at which this skills max benefits will be reached on the curve
     */
    public int getMaxBonusLevel(SubSkillType subSkillType) {
        if(!McmmoCore.isRetroModeEnabled())
            return getIntValue(subSkillType.getAdvConfigAddress(), MAX_BONUS_LEVEL, STANDARD);
        else
            return getIntValue(subSkillType.getAdvConfigAddress(), MAX_BONUS_LEVEL, RETRO_MODE);
    }

    public int getMaxBonusLevel(AbstractSubSkill abstractSubSkill) {
        return getMaxBonusLevel(abstractSubSkill.getSubSkillType());
    }

    public double getMaximumProbability(SubSkillType subSkillType) {
        double maximumProbability = getDoubleValue(subSkillType.getAdvConfigAddress(), CHANCE_MAX);

        return maximumProbability;
    }

    public double getMaximumProbability(AbstractSubSkill abstractSubSkill)
    {
        return getMaximumProbability(abstractSubSkill.getSubSkillType());
    }

    /* Notification Settings */

    public boolean doesSkillCommandSendBlankLines()
    {
        return getBooleanValue("Feedback.SkillCommand.BlankLinesAboveHeader");
    }

    public boolean doesNotificationUseActionBar(NotificationType notificationType)
    {
        return getBooleanValue("Feedback.ActionBarNotifications."+notificationType.toString()+".Enabled");
    }

    public boolean doesNotificationSendCopyToChat(NotificationType notificationType)
    {
        return getBooleanValue("Feedback.ActionBarNotifications."+notificationType.toString()+".SendCopyOfMessageToChat");
    }

    public boolean useTitlesForXPEvent()
    {
        return getBooleanValue("Feedback.Events.XP.SendTitles");
    }

    private ChatColor getChatColorFromKey(String keyLocation) {
        String colorName = getStringValue(keyLocation);

        return getChatColor(colorName);
    }

    private ChatColor getChatColor(String configColor) {
        for (ChatColor chatColor : ChatColor.values()) {
            if (configColor.equalsIgnoreCase(chatColor.getName()))
                return chatColor;
        }

        //Invalid Color
        System.out.println("[mcMMO] " + configColor + " is an invalid color value");
        return ChatColor.WHITE;
    }

    /**
     * Some SubSkills have the ability to retain classic functionality
     * @param subSkillType SubSkillType with classic functionality
     * @return true if the subskill is in classic mode
     */
    public boolean isSubSkillClassic(SubSkillType subSkillType)
    {
        return getBooleanValue(subSkillType.getAdvConfigAddress()+".Classic");
    }

    /* ACROBATICS */
    public double getDodgeDamageModifier() { return getDoubleValue(SKILLS, ACROBATICS, DODGE, DAMAGE_MODIFIER); }

    public double getRollDamageThreshold() { return getDoubleValue(SKILLS, ACROBATICS + ".Roll." + DAMAGE_THRESHOLD); }

    public double getGracefulRollDamageThreshold() { return getDoubleValue(SKILLS, ACROBATICS + ".GracefulRoll." + DAMAGE_THRESHOLD); }

    /* ALCHEMY */
    public int getCatalysisMaxBonusLevel() { return getIntValue(SKILLS, ALCHEMY, CATALYSIS, MAX_BONUS_LEVEL); }

    public double getCatalysisMinSpeed() { return getDoubleValue(SKILLS, ALCHEMY, CATALYSIS, MIN_SPEED); }
    public double getCatalysisMaxSpeed() { return getDoubleValue(SKILLS, ALCHEMY, CATALYSIS, MAX_SPEED); }

    /* ARCHERY */
    public double getSkillShotRankDamageMultiplier() { return getDoubleValue(SKILLS, ARCHERY, SKILL_SHOT, RANK_DAMAGE_MULTIPLIER); }
    public double getSkillShotDamageMax() { return getDoubleValue(SKILLS, ARCHERY, SKILL_SHOT + ".MaxDamage"); }

    public double getDazeBonusDamage() { return getDoubleValue(SKILLS, ARCHERY + ".Daze." + BONUS_DAMAGE); }

    public double getForceMultiplier() { return getDoubleValue(SKILLS, ARCHERY, FORCE_MULTIPLIER); }

    /* AXES */
    public double getAxeMasteryRankDamageMultiplier() { return getDoubleValue(SKILLS, AXES + ".AxeMastery." + RANK_DAMAGE_MULTIPLIER); }

    public double getCriticalStrikesPVPModifier() { return getDoubleValue(SKILLS, AXES + ".CriticalStrikes." + PVP_MODIFIER); }
    public double getCriticalStrikesPVEModifier() { return getDoubleValue(SKILLS, AXES + ".CriticalStrikes." + PVE_MODIFIER); }

    public double getGreaterImpactChance() { return getDoubleValue(SKILLS, AXES + ".GreaterImpact." + CHANCE); }
    public double getGreaterImpactModifier() { return getDoubleValue(SKILLS, AXES + ".GreaterImpact." + KNOCKBACK_MODIFIER); }
    public double getGreaterImpactBonusDamage() { return getDoubleValue(SKILLS, AXES + ".GreaterImpact." + BONUS_DAMAGE); }

    public int getArmorImpactIncreaseLevel() {
        int increaseLevel = getIntValue(SKILLS, AXES + ".ArmorImpact." + INCREASE_LEVEL);

        if(mcMMO.isRetroModeEnabled())
            return increaseLevel * 10;

        return increaseLevel;
    }

    public double getImpactChance() { return getDoubleValue(SKILLS, AXES + ".ArmorImpact." + CHANCE); }
    public double getArmorImpactMaxDurabilityDamage() { return getDoubleValue(SKILLS, AXES + ".ArmorImpact.MaxPercentageDurabilityDamage"); }

    public double getSkullSplitterModifier() { return getDoubleValue(SKILLS, AXES + ".SkullSplitter." + DAMAGE_MODIFIER); }

    /* EXCAVATION */
    //Nothing to configure, everything is already configurable in config.yml

    /* FISHING */
    public double getShakeChance(int rank) { return getDoubleValue(SKILLS + "." + FISHING + ".Shake" + CHANCE + "." + RANK + rank); }
    public int getFishingVanillaXPModifier(int rank) { return getIntValue(SKILLS + "." + FISHING + "." + VANILLA_XPMULTIPLIER + "." + RANK + rank); }
    //public int getFishermanDietRankChange() { return getIntValue(SKILLS + "." + FISHING + ".FishermansDiet.RankChange"); }
    public double getMasterAnglerBoatModifier() {return getDoubleValue(SKILLS + "." + FISHING + "." + MASTER_ANGLER + "." + BOAT_MODIFIER); }
    public double getMasterAnglerBiomeModifier() {return getDoubleValue(SKILLS + "." + FISHING + "." + MASTER_ANGLER + "." + BIOME_MODIFIER); }

    /* HERBALISM */
    //public int getFarmerDietRankChange() { return getIntValue(SKILLS + ".Herbalism.FarmersDiet.RankChange"); }

    //public int getGreenThumbStageChange() { return getIntValue(SKILLS + ".Herbalism.GreenThumb.StageChange"); }

    /* MINING */
    public int getBlastMiningRankLevel(int rank) { return getIntValue(SKILLS + ".Mining.BlastMining." + RANK + "Levels.Rank_" + rank); }
    public double getBlastDamageDecrease(int rank) { return getDoubleValue(SKILLS + ".Mining.BlastMining.BlastDamageDecrease." + RANK + rank); }
    public double getOreBonus(int rank) { return getDoubleValue(SKILLS + ".Mining.BlastMining.OreBonus." + RANK + rank); }
    public double getDebrisReduction(int rank) { return getDoubleValue(SKILLS + ".Mining.BlastMining.DebrisReduction." + RANK + rank); }
    public int getDropMultiplier(int rank) { return getIntValue(SKILLS + ".Mining.BlastMining.DropMultiplier." + RANK + rank); }
    public double getBlastRadiusModifier(int rank) { return getDoubleValue(SKILLS + ".Mining.BlastMining.BlastRadius" + MODIFIER + "." + RANK + rank); }

    /* REPAIR */
    public double getRepairMasteryMaxBonus() { return getDoubleValue(SKILLS + ".Repair.RepairMastery.MaxBonusPercentage"); }
    public int getRepairMasteryMaxLevel() { return getIntValue(SKILLS + ".Repair.RepairMastery." + MAX_BONUS_LEVEL); }

    /* Arcane Forging */
    public boolean getArcaneForgingEnchantLossEnabled() { return getBooleanValue(SKILLS + ".Repair.ArcaneForging.May_Lose_Enchants"); }
    public double getArcaneForgingKeepEnchantsChance(int rank) { return getDoubleValue(SKILLS + ".Repair.ArcaneForging.Keep_Enchants_" + CHANCE + "." + RANK + rank); }

    public boolean getArcaneForgingDowngradeEnabled() { return getBooleanValue(SKILLS + ".Repair.ArcaneForging.Downgrades_Enabled"); }
    public double getArcaneForgingDowngradeChance(int rank) { return getDoubleValue(SKILLS + ".Repair.ArcaneForging.Downgrades_" + CHANCE + "." + RANK + rank); }

    /* SALVAGE */

    public boolean getArcaneSalvageEnchantDowngradeEnabled() { return getBooleanValue(SKILLS + ".Salvage.ArcaneSalvage.EnchantDowngradeEnabled"); }
    public boolean getArcaneSalvageEnchantLossEnabled() { return getBooleanValue(SKILLS + ".Salvage.ArcaneSalvage.EnchantLossEnabled"); }

    public double getArcaneSalvageExtractFullEnchantsChance(int rank) { return getDoubleValue(SKILLS + ".Salvage.ArcaneSalvage.ExtractFullEnchant." + RANK + rank); }
    public double getArcaneSalvageExtractPartialEnchantsChance(int rank) { return getDoubleValue(SKILLS + ".Salvage.ArcaneSalvage.ExtractPartialEnchant." + RANK + rank); }

    /* SMELTING */
    public int getBurnModifierMaxLevel() { return getIntValue(SKILLS + ".Smelting.FuelEfficiency." + MAX_BONUS_LEVEL); }
    public double getBurnTimeMultiplier() { return getDoubleValue(SKILLS + ".Smelting.FuelEfficiency.Multiplier"); }

    public double getFluxMiningChance() { return getDoubleValue(SKILLS + ".Smelting.FluxMining." + CHANCE); }

    public int getSmeltingRankLevel(int rank) { return getIntValue(SKILLS + ".Smelting." + RANK + "Levels.Rank_" + rank); }

    public int getSmeltingVanillaXPBoostMultiplier(int rank) { return getIntValue(SKILLS + ".Smelting." + VANILLA_XPMULTIPLIER + "." + RANK + rank); }

    /* SWORDS */
    public double getRuptureDamagePlayer() { return getDoubleValue(SKILLS + ".Swords.Rupture.DamagePlayer"); }
    public double getRuptureDamageMobs() { return getDoubleValue(SKILLS + ".Swords.Rupture.DamageMobs"); }

    public int getRuptureMaxTicks() { return getIntValue(SKILLS + ".Swords.Rupture.MaxTicks"); }
    public int getRuptureBaseTicks() { return getIntValue(SKILLS + ".Swords.Rupture.BaseTicks"); }

    public double getCounterModifier() { return getDoubleValue(SKILLS + ".Swords.CounterAttack." + DAMAGE_MODIFIER); }

    public double getSerratedStrikesModifier() { return getDoubleValue(SKILLS + ".Swords.SerratedStrikes." + DAMAGE_MODIFIER); }
    public int getSerratedStrikesTicks() { return getIntValue(SKILLS + ".Swords.SerratedStrikes.RuptureTicks"); }

    /* TAMING */
    public double getGoreModifier() { return getDoubleValue(SKILLS + "." + TAMING + ".Gore." + MODIFIER); }
    public double getFastFoodChance() { return getDoubleValue(SKILLS + "." + TAMING + ".FastFoodService." + CHANCE); }
    public double getPummelChance() { return getDoubleValue(SKILLS + "." + TAMING + ".Pummel." + CHANCE); }
    public double getThickFurModifier() { return getDoubleValue(SKILLS + "." + TAMING + ".ThickFur." + MODIFIER); }
    public double getShockProofModifier() { return getDoubleValue(SKILLS + "." + TAMING + "." + SHOCK_PROOF + "." + MODIFIER); }

    public double getSharpenedClawsBonus() { return getDoubleValue(SKILLS + "." + TAMING + ".SharpenedClaws.Bonus"); }

    public double getMinHorseJumpStrength() { return getDoubleValue(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD + "." + MIN_HORSE_JUMP_STRENGTH); }
    public double getMaxHorseJumpStrength() { return getDoubleValue(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD + "." + MAX_HORSE_JUMP_STRENGTH); }

    /* UNARMED */
    public boolean getDisarmProtected() { return getBooleanValue(SKILLS + "." + UNARMED + ".Disarm.AntiTheft"); }

    /* WOODCUTTING */
}
