package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.mcMMO;
import net.md_5.bungee.api.ChatColor;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class AdvancedConfig extends ConfigLoaderConfigurable {
    private static AdvancedConfig instance;

    private AdvancedConfig() {
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), "advanced.yml");
    }

    public static AdvancedConfig getInstance() {
        if (instance == null) {
            instance = new AdvancedConfig();
        }

        return instance;
    }

    @Setting(value = "Skills.General.StartingLevel", comment = "The starting level for players on your server.\nHistorically this has been 0.\nRecently is has changed to 1.")
    private int startingLevel = 1;

    @Override
    public List<String> validateKeys() {
        // Validate all the settings!
        List<String> reason = new ArrayList<String>();

        /* GENERAL */
        if (getAbilityLength() < 1) {
            reason.add("Skills.General.Ability.Length.<mode>.IncreaseLevel should be at least 1!");
        }

        if (getEnchantBuff() < 1) {
            reason.add("Skills.General.Ability.EnchantBuff should be at least 1!");
        }

        /* ACROBATICS */
        if (getMaximumProbability(SubSkillType.ACROBATICS_DODGE) < 1) {
            reason.add("Skills.Acrobatics.Dodge.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ACROBATICS_DODGE) < 1) {
            reason.add("Skills.Acrobatics.Dodge.MaxBonusLevel should be at least 1!");
        }

        if (getDodgeDamageModifier() <= 1) {
            reason.add("Skills.Acrobatics.Dodge.DamageModifier should be greater than 1!");
        }

        if (getMaximumProbability(SubSkillType.ACROBATICS_ROLL) < 1) {
            reason.add("Skills.Acrobatics.Roll.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ACROBATICS_ROLL) < 1) {
            reason.add("Skills.Acrobatics.Roll.MaxBonusLevel should be at least 1!");
        }

        if (getRollDamageThreshold() < 0) {
            reason.add("Skills.Acrobatics.Roll.DamageThreshold should be at least 0!");
        }

        if (getGracefulRollDamageThreshold() < 0) {
            reason.add("Skills.Acrobatics.GracefulRoll.DamageThreshold should be at least 0!");
        }

        if (getCatalysisMinSpeed() <= 0) {
            reason.add("Skills.Alchemy.Catalysis.MinSpeed must be greater than 0!");
        }

        if (getCatalysisMaxSpeed() < getCatalysisMinSpeed()) {
            reason.add("Skills.Alchemy.Catalysis.MaxSpeed should be at least Skills.Alchemy.Catalysis.MinSpeed!");
        }

        /* ARCHERY */

        if (getSkillShotRankDamageMultiplier() <= 0) {
            reason.add("Skills.Archery.SkillShot.RankDamageMultiplier should be greater than 0!");
        }

        if (getMaximumProbability(SubSkillType.ARCHERY_DAZE) < 1) {
            reason.add("Skills.Archery.Daze.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ARCHERY_DAZE) < 1) {
            reason.add("Skills.Archery.Daze.MaxBonusLevel should be at least 1!");
        }

        if (getDazeBonusDamage() < 0) {
            reason.add("Skills.Archery.Daze.BonusDamage should be at least 0!");
        }

        if (getMaximumProbability(SubSkillType.ARCHERY_ARROW_RETRIEVAL) < 1) {
            reason.add("Skills.Archery.Retrieve.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ARCHERY_ARROW_RETRIEVAL) < 1) {
            reason.add("Skills.Archery.Retrieve.MaxBonusLevel should be at least 1!");
        }

        if (getForceMultiplier() < 0) {
            reason.add("Skills.Archery.ForceMultiplier should be at least 0!");
        }

        /* AXES */
        if(getAxeMasteryRankDamageMultiplier() < 0)
        {
            reason.add("Skills.Axes.AxeMastery.RankDamageMultiplier should be at least 0!");
        }

        if (getMaximumProbability(SubSkillType.AXES_CRITICAL_STRIKES) < 1) {
            reason.add("Skills.Axes.CriticalHit.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.AXES_CRITICAL_STRIKES) < 1) {
            reason.add("Skills.Axes.CriticalHit.MaxBonusLevel should be at least 1!");
        }

        if (getCriticalStrikesPVPModifier() < 1) {
            reason.add("Skills.Axes.CriticalStrikes.PVP_Modifier should be at least 1!");
        }

        if (getCriticalStrikesPVPModifier() < 1) {
            reason.add("Skills.Axes.CriticalStrikes.PVE_Modifier should be at least 1!");
        }

        if (getGreaterImpactChance() < 1) {
            reason.add("Skills.Axes.GreaterImpact.Chance should be at least 1!");
        }

        if (getGreaterImpactModifier() < 1) {
            reason.add("Skills.Axes.GreaterImpact.KnockbackModifier should be at least 1!");
        }

        if (getGreaterImpactBonusDamage() < 1) {
            reason.add("Skills.Axes.GreaterImpact.BonusDamage should be at least 1!");
        }

        if (getArmorImpactIncreaseLevel() < 1) {
            reason.add("Skills.Axes.ArmorImpact.IncreaseLevel should be at least 1!");
        }

        if (getImpactChance() < 1) {
            reason.add("Skills.Axes.ArmorImpact.Chance should be at least 1!");
        }

        if (getArmorImpactMaxDurabilityDamage() < 1) {
            reason.add("Skills.Axes.ArmorImpact.MaxPercentageDurabilityDamage should be at least 1!");
        }

        if (getSkullSplitterModifier() < 1) {
            reason.add("Skills.Axes.SkullSplitter.DamageModifier should be at least 1!");
        }

        if (getFishermanDietRankChange() < 1) {
            reason.add("Skills.Fishing.FishermansDiet.RankChange should be at least 1!");
        }

        if (getMasterAnglerBoatModifier() < 1) {
            reason.add("Skills.Fishing.MasterAngler.BoatModifier should be at least 1!");
        }

        if (getMasterAnglerBiomeModifier() < 1) {
            reason.add("Skills.Fishing.MasterAngler.BiomeModifier should be at least 1!");
        }

        /* HERBALISM */
        if (getFarmerDietRankChange() < 1) {
            reason.add("Skills.Herbalism.FarmersDiet.RankChange should be at least 1!");
        }

        if (getGreenThumbStageChange() < 1) {
            reason.add("Skills.Herbalism.GreenThumb.StageChange should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.HERBALISM_GREEN_THUMB) < 1) {
            reason.add("Skills.Herbalism.GreenThumb.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_GREEN_THUMB) < 1) {
            reason.add("Skills.Herbalism.GreenThumb.MaxBonusLevel should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.HERBALISM_DOUBLE_DROPS) < 1) {
            reason.add("Skills.Herbalism.DoubleDrops.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_DOUBLE_DROPS) < 1) {
            reason.add("Skills.Herbalism.DoubleDrops.MaxBonusLevel should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.HERBALISM_HYLIAN_LUCK) < 1) {
            reason.add("Skills.Herbalism.HylianLuck.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_HYLIAN_LUCK) < 1) {
            reason.add("Skills.Herbalism.HylianLuck.MaxBonusLevel should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.HERBALISM_SHROOM_THUMB) < 1) {
            reason.add("Skills.Herbalism.ShroomThumb.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_SHROOM_THUMB) < 1) {
            reason.add("Skills.Herbalism.ShroomThumb.MaxBonusLevel should be at least 1!");
        }

        /* MINING */
        if (getMaximumProbability(SubSkillType.MINING_DOUBLE_DROPS) < 1) {
            reason.add("Skills.Mining.DoubleDrops.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.MINING_DOUBLE_DROPS) < 1) {
            reason.add("Skills.Mining.DoubleDrops.MaxBonusLevel should be at least 1!");
        }

        /* REPAIR */
        if (getRepairMasteryMaxBonus() < 1) {
            reason.add("Skills.Repair.RepairMastery.MaxBonusPercentage should be at least 1!");
        }

        if (getRepairMasteryMaxLevel() < 1) {
            reason.add("Skills.Repair.RepairMastery.MaxBonusLevel should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.REPAIR_SUPER_REPAIR) < 1) {
            reason.add("Skills.Repair.SuperRepair.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.REPAIR_SUPER_REPAIR) < 1) {
            reason.add("Skills.Repair.SuperRepair.MaxBonusLevel should be at least 1!");
        }

        /* SMELTING */
        if (getBurnModifierMaxLevel() < 1) {
            reason.add("Skills.Smelting.FuelEfficiency.MaxBonusLevel should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SMELTING_SECOND_SMELT) < 1) {
            reason.add("Skills.Smelting.SecondSmelt.MaxBonusLevel should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.SMELTING_SECOND_SMELT) < 1) {
            reason.add("Skills.Smelting.SecondSmelt.ChanceMax should be at least 1!");
        }

        if (getFluxMiningChance() < 1) {
            reason.add("Skills.Smelting.FluxMining.Chance should be at least 1!");
        }

        /* SWORDS */
        if (getMaximumProbability(SubSkillType.SWORDS_RUPTURE) < 1) {
            reason.add("Skills.Swords.Rupture.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SWORDS_RUPTURE) < 1) {
            reason.add("Skills.Swords.Rupture.MaxBonusLevel should be at least 1!");
        }

        if (getRuptureMaxTicks() < 1) {
            reason.add("Skills.Swords.Rupture.MaxTicks should be at least 1!");
        }

        if (getRuptureMaxTicks() < getRuptureBaseTicks()) {
            reason.add("Skills.Swords.Rupture.MaxTicks should be at least Skills.Swords.Rupture.BaseTicks!");
        }

        if (getRuptureBaseTicks() < 1) {
            reason.add("Skills.Swords.Rupture.BaseTicks should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.SWORDS_COUNTER_ATTACK) < 1) {
            reason.add("Skills.Swords.CounterAttack.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SWORDS_COUNTER_ATTACK) < 1) {
            reason.add("Skills.Swords.CounterAttack.MaxBonusLevel should be at least 1!");
        }

        if (getCounterModifier() < 1) {
            reason.add("Skills.Swords.CounterAttack.DamageModifier should be at least 1!");
        }

        if (getSerratedStrikesModifier() < 1) {
            reason.add("Skills.Swords.SerratedStrikes.DamageModifier should be at least 1!");
        }

        if (getSerratedStrikesTicks() < 1) {
            reason.add("Skills.Swords.SerratedStrikes.RuptureTicks should be at least 1!");
        }

        /* TAMING */

        if (getMaximumProbability(SubSkillType.TAMING_GORE) < 1) {
            reason.add("Skills.Taming.Gore.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.TAMING_GORE) < 1) {
            reason.add("Skills.Taming.Gore.MaxBonusLevel should be at least 1!");
        }

        if (getGoreModifier() < 1) {
            reason.add("Skills.Taming.Gore.Modifier should be at least 1!");
        }

        if (getFastFoodChance() < 1) {
            reason.add("Skills.Taming.FastFood.Chance should be at least 1!");
        }

        if (getThickFurModifier() < 1) {
            reason.add("Skills.Taming.ThickFur.Modifier should be at least 1!");
        }

        if (getShockProofModifier() < 1) {
            reason.add("Skills.Taming.ShockProof.Modifier should be at least 1!");
        }

        if (getSharpenedClawsBonus() < 1) {
            reason.add("Skills.Taming.SharpenedClaws.Bonus should be at least 1!");
        }

        if (getMaxHorseJumpStrength() < 0 || getMaxHorseJumpStrength() > 2) {
            reason.add("Skills.Taming.CallOfTheWild.MaxHorseJumpStrength should be between 0 and 2!");
        }

        /* UNARMED */
        if (getMaximumProbability(SubSkillType.UNARMED_DISARM) < 1) {
            reason.add("Skills.Unarmed.Disarm.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_DISARM) < 1) {
            reason.add("Skills.Unarmed.Disarm.MaxBonusLevel should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.UNARMED_ARROW_DEFLECT) < 1) {
            reason.add("Skills.Unarmed.ArrowDeflect.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_ARROW_DEFLECT) < 1) {
            reason.add("Skills.Unarmed.ArrowDeflect.MaxBonusLevel should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.UNARMED_IRON_GRIP) < 1) {
            reason.add("Skills.Unarmed.IronGrip.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_IRON_GRIP) < 1) {
            reason.add("Skills.Unarmed.IronGrip.MaxBonusLevel should be at least 1!");
        }

        /* WOODCUTTING */

        /*if (getLeafBlowUnlockLevel() < 0) {
            reason.add("Skills.Woodcutting.LeafBlower.UnlockLevel should be at least 0!");
        }*/

        if (getMaximumProbability(SubSkillType.WOODCUTTING_HARVEST_LUMBER) < 1) {
            reason.add("Skills.Woodcutting.HarvestLumber.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER) < 1) {
            reason.add("Skills.Woodcutting.HarvestLumber.MaxBonusLevel should be at least 1!");
        }

        return reason;
    }

    /* GENERAL */
    public int getStartingLevel() { return startingLevel; }

    /**
     * This returns the maximum level at which superabilities will stop lengthening from scaling alongside skill level.
     * It returns a different value depending on whether or not the server is in retro mode
     * @return the level at which abilities stop increasing in length
     */
    public int getAbilityLengthCap() {
        if(!mcMMO.isRetroModeEnabled())
            return getIntValue("Skills.General.Ability.Length.Standard.CapLevel");
        else
            return getIntValue("Skills.General.Ability.Length.RetroMode.CapLevel");
    }

    /**
     * This returns the frequency at which abilities will increase in length
     * It returns a different value depending on whether or not the server is in retro mode
     * @return the number of levels required per ability length increase
     */
    public int getAbilityLength() {
        if(!mcMMO.isRetroModeEnabled())
            return getIntValue("Skills.General.Ability.Length.Standard.IncreaseLevel");
        else
            return getIntValue("Skills.General.Ability.Length.RetroMode.IncreaseLevel");
    }

    public int getEnchantBuff() { return getIntValue("Skills.General.Ability.EnchantBuff"); }

    /**
     * Grabs the max bonus level for a skill used in RNG calculations
     * All max level values in the config are multiplied by 10 if the server is in retro mode as the values in the config are based around the new 1-100 skill system scaling
     * A value of 10 in the file will be returned as 100 for retro mode servers to accommodate the change in scaling
     * @param subSkillType target subskill
     * @return the level at which this skills max benefits will be reached on the curve
     */
    public int getMaxBonusLevel(SubSkillType subSkillType) {
        String keyPath = subSkillType.getAdvConfigAddress() + ".MaxBonusLevel.";
        return mcMMO.isRetroModeEnabled() ? getIntValue(keyPath+"RetroMode") : getIntValue(keyPath+"Standard");
    }

    public int getMaxBonusLevel(AbstractSubSkill abstractSubSkill) {
        return getMaxBonusLevel(abstractSubSkill.getSubSkillType());
    }

    public double getMaximumProbability(SubSkillType subSkillType) {
        double maximumProbability = getDoubleValue(subSkillType.getAdvConfigAddress() + ".ChanceMax");

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
    public double getDodgeDamageModifier() { return getDoubleValue("Skills.Acrobatics.Dodge.DamageModifier"); }

    public double getRollDamageThreshold() { return getDoubleValue("Skills.Acrobatics.Roll.DamageThreshold"); }

    public double getGracefulRollDamageThreshold() { return getDoubleValue("Skills.Acrobatics.GracefulRoll.DamageThreshold"); }

    /* ALCHEMY */
    public int getCatalysisMaxBonusLevel() { return getIntValue("Skills.Alchemy.Catalysis.MaxBonusLevel"); }

    public double getCatalysisMinSpeed() { return getDoubleValue("Skills.Alchemy.Catalysis.MinSpeed"); }
    public double getCatalysisMaxSpeed() { return getDoubleValue("Skills.Alchemy.Catalysis.MaxSpeed"); }

    /* ARCHERY */
    public double getSkillShotRankDamageMultiplier() { return getDoubleValue("Skills.Archery.SkillShot.RankDamageMultiplier"); }
    public double getSkillShotDamageMax() { return getDoubleValue("Skills.Archery.SkillShot.MaxDamage"); }

    public double getDazeBonusDamage() { return getDoubleValue("Skills.Archery.Daze.BonusDamage"); }

    public double getForceMultiplier() { return getDoubleValue("Skills.Archery.ForceMultiplier"); }

    /* AXES */
    public double getAxeMasteryRankDamageMultiplier() { return getDoubleValue("Skills.Axes.AxeMastery.RankDamageMultiplier"); }

    public double getCriticalStrikesPVPModifier() { return getDoubleValue("Skills.Axes.CriticalStrikes.PVP_Modifier"); }
    public double getCriticalStrikesPVEModifier() { return getDoubleValue("Skills.Axes.CriticalStrikes.PVE_Modifier"); }

    public double getGreaterImpactChance() { return getDoubleValue("Skills.Axes.GreaterImpact.Chance"); }
    public double getGreaterImpactModifier() { return getDoubleValue("Skills.Axes.GreaterImpact.KnockbackModifier"); }
    public double getGreaterImpactBonusDamage() { return getDoubleValue("Skills.Axes.GreaterImpact.BonusDamage"); }

    public int getArmorImpactIncreaseLevel() {
        int increaseLevel = getIntValue("Skills.Axes.ArmorImpact.IncreaseLevel");

        if(mcMMO.isRetroModeEnabled())
            return increaseLevel * 10;

        return increaseLevel;
    }

    public double getImpactChance() { return getDoubleValue("Skills.Axes.ArmorImpact.Chance"); }
    public double getArmorImpactMaxDurabilityDamage() { return getDoubleValue("Skills.Axes.ArmorImpact.MaxPercentageDurabilityDamage"); }

    public double getSkullSplitterModifier() { return getDoubleValue("Skills.Axes.SkullSplitter.DamageModifier"); }

    /* EXCAVATION */
    //Nothing to configure, everything is already configurable in config.yml

    /* FISHING */
    public double getShakeChance(int rank) { return getDoubleValue("Skills.Fishing.ShakeChance.Rank_" + rank); }
    public int getFishingVanillaXPModifier(int rank) { return getIntValue("Skills.Fishing.VanillaXPMultiplier.Rank_" + rank); }
    public int getFishermanDietRankChange() { return getIntValue("Skills.Fishing.FishermansDiet.RankChange"); }
    public double getMasterAnglerBoatModifier() {return getDoubleValue("Skills.Fishing.MasterAngler.BoatModifier"); }
    public double getMasterAnglerBiomeModifier() {return getDoubleValue("Skills.Fishing.MasterAngler.BiomeModifier"); }

    /* HERBALISM */
    public int getFarmerDietRankChange() { return getIntValue("Skills.Herbalism.FarmersDiet.RankChange"); }

    public int getGreenThumbStageChange() { return getIntValue("Skills.Herbalism.GreenThumb.StageChange"); }

    /* MINING */
    public boolean getDoubleDropSilkTouchEnabled() { return config.getBoolean("Skills.Mining.DoubleDrops.SilkTouch", true); }
    public int getBlastMiningRankLevel(int rank) { return config.getInt("Skills.Mining.BlastMining.Rank_Levels.Rank_" + rank); }
    public double getBlastDamageDecrease(int rank) { return config.getDouble("Skills.Mining.BlastMining.BlastDamageDecrease.Rank_" + rank); }
    public double getOreBonus(int rank) { return config.getDouble("Skills.Mining.BlastMining.OreBonus.Rank_" + rank); }
    public double getDebrisReduction(int rank) { return config.getDouble("Skills.Mining.BlastMining.DebrisReduction.Rank_" + rank); }
    public int getDropMultiplier(int rank) { return config.getInt("Skills.Mining.BlastMining.DropMultiplier.Rank_" + rank); }
    public double getBlastRadiusModifier(int rank) { return config.getDouble("Skills.Mining.BlastMining.BlastRadiusModifier.Rank_" + rank); }

    /* REPAIR */
    public double getRepairMasteryMaxBonus() { return getDoubleValue("Skills.Repair.RepairMastery.MaxBonusPercentage"); }
    public int getRepairMasteryMaxLevel() { return getIntValue("Skills.Repair.RepairMastery.MaxBonusLevel"); }

    /* Arcane Forging */
    public boolean getArcaneForgingEnchantLossEnabled() { return getBooleanValue("Skills.Repair.ArcaneForging.May_Lose_Enchants"); }
    public double getArcaneForgingKeepEnchantsChance(int rank) { return getDoubleValue("Skills.Repair.ArcaneForging.Keep_Enchants_Chance.Rank_" + rank); }

    public boolean getArcaneForgingDowngradeEnabled() { return getBooleanValue("Skills.Repair.ArcaneForging.Downgrades_Enabled"); }
    public double getArcaneForgingDowngradeChance(int rank) { return getDoubleValue("Skills.Repair.ArcaneForging.Downgrades_Chance.Rank_" + rank); }

    /* SALVAGE */

    public boolean getArcaneSalvageEnchantDowngradeEnabled() { return getBooleanValue("Skills.Salvage.ArcaneSalvage.EnchantDowngradeEnabled"); }
    public boolean getArcaneSalvageEnchantLossEnabled() { return getBooleanValue("Skills.Salvage.ArcaneSalvage.EnchantLossEnabled"); }

    public double getArcaneSalvageExtractFullEnchantsChance(int rank) { return getDoubleValue("Skills.Salvage.ArcaneSalvage.ExtractFullEnchant.Rank_" + rank); }
    public double getArcaneSalvageExtractPartialEnchantsChance(int rank) { return getDoubleValue("Skills.Salvage.ArcaneSalvage.ExtractPartialEnchant.Rank_" + rank); }

    /* SMELTING */
    public int getBurnModifierMaxLevel() { return getIntValue("Skills.Smelting.FuelEfficiency.MaxBonusLevel"); }
    public double getBurnTimeMultiplier() { return getDoubleValue("Skills.Smelting.FuelEfficiency.Multiplier"); }

    public double getFluxMiningChance() { return getDoubleValue("Skills.Smelting.FluxMining.Chance"); }

    public int getSmeltingRankLevel(int rank) { return getIntValue("Skills.Smelting.Rank_Levels.Rank_" + rank); }

    public int getSmeltingVanillaXPBoostMultiplier(int rank) { return getIntValue("Skills.Smelting.VanillaXPMultiplier.Rank_" + rank); }

    /* SWORDS */
    public double getRuptureDamagePlayer() { return getDoubleValue("Skills.Swords.Rupture.DamagePlayer"); }
    public double getRuptureDamageMobs() { return getDoubleValue("Skills.Swords.Rupture.DamageMobs"); }

    public int getRuptureMaxTicks() { return getIntValue("Skills.Swords.Rupture.MaxTicks"); }
    public int getRuptureBaseTicks() { return getIntValue("Skills.Swords.Rupture.BaseTicks"); }

    public double getCounterModifier() { return getDoubleValue("Skills.Swords.CounterAttack.DamageModifier"); }

    public double getSerratedStrikesModifier() { return getDoubleValue("Skills.Swords.SerratedStrikes.DamageModifier"); }
    public int getSerratedStrikesTicks() { return getIntValue("Skills.Swords.SerratedStrikes.RuptureTicks"); }

    /* TAMING */
    public double getGoreModifier() { return getDoubleValue("Skills.Taming.Gore.Modifier"); }
    public double getFastFoodChance() { return getDoubleValue("Skills.Taming.FastFoodService.Chance"); }
    public double getPummelChance() { return getDoubleValue("Skills.Taming.Pummel.Chance"); }
    public double getThickFurModifier() { return getDoubleValue("Skills.Taming.ThickFur.Modifier"); }
    public double getShockProofModifier() { return getDoubleValue("Skills.Taming.ShockProof.Modifier"); }

    public double getSharpenedClawsBonus() { return getDoubleValue("Skills.Taming.SharpenedClaws.Bonus"); }

    public double getMinHorseJumpStrength() { return getDoubleValue("Skills.Taming.CallOfTheWild.MinHorseJumpStrength"); }
    public double getMaxHorseJumpStrength() { return getDoubleValue("Skills.Taming.CallOfTheWild.MaxHorseJumpStrength"); }

    /* UNARMED */
    public boolean getDisarmProtected() { return getBooleanValue("Skills.Unarmed.Disarm.AntiTheft"); }

    /* WOODCUTTING */
}
