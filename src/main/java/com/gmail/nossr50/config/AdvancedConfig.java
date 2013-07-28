package com.gmail.nossr50.config;

import java.util.ArrayList;
import java.util.List;

import com.gmail.nossr50.mcMMO;

public class AdvancedConfig extends AutoUpdateConfigLoader {
    private static AdvancedConfig instance;

    private AdvancedConfig() {
        super("advanced.yml");
        loadKeys();
    }

    public static AdvancedConfig getInstance() {
        if (instance == null) {
            instance = new AdvancedConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        // Validate all the settings!
        List<String> reason = new ArrayList<String>();

        /* GENERAL */
        if (getAbilityLength() < 1) {
            reason.add("Skills.General.Ability_IncreaseLevel should be at least 1!");
        }

        if (getEnchantBuff() < 1) {
            reason.add("Skills.General.Ability_EnchantBuff should be at least 1!");
        }

        /* ACROBATICS */
        if (getDodgeChanceMax() < 1) {
            reason.add("Skills.Acrobatics.Dodge_ChanceMax should be at least 1!");
        }

        if (getDodgeMaxBonusLevel() < 1) {
            reason.add("Skills.Acrobatics.Dodge_MaxBonusLevel should be at least 1!");
        }

        if (getDodgeDamageModifier() <= 1) {
            reason.add("Skills.Acrobatics.Dodge_DamageModifer should be greater than 1!");
        }

        if (getRollChanceMax() < 1) {
            reason.add("Skills.Acrobatics.Roll_ChanceMax should be at least 1!");
        }

        if (getRollMaxBonusLevel() < 1) {
            reason.add("Skills.Acrobatics.Roll_MaxBonusLevel should be at least 1!");
        }

        if (getRollDamageThreshold() < 0) {
            reason.add("Skills.Acrobatics.Roll_DamageThreshold should be at least 0!");
        }

        if (getGracefulRollChanceMax() < 1) {
            reason.add("Skills.Acrobatics.GracefulRoll_ChanceMax should be at least 1!");
        }

        if (getGracefulRollMaxBonusLevel() < 1) {
            reason.add("Skills.Acrobatics.GracefulRoll_MaxBonusLevel should be at least 1!");
        }

        if (getGracefulRollDamageThreshold() < 0) {
            reason.add("Skills.Acrobatics.GracefulRoll_DamageThreshold should be at least 0!");
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

        /* ARCHERY */
        if (getSkillShotIncreaseLevel() < 1) {
            reason.add("Skills.Archery.SkillShot_IncreaseLevel should be at least 1!");
        }

        if (getSkillShotIncreasePercentage() <= 0) {
            reason.add("Skills.Archery.SkillShot_IncreasePercentage should be greater than 0!");
        }

        if (getSkillShotBonusMax() < 0) {
            reason.add("Skills.Archery.SkillShot_MaxBonus should be at least 0!");
        }

        if (getDazeBonusMax() < 1) {
            reason.add("Skills.Acrobatics.Daze_MaxChance should be at least 1!");
        }

        if (getDazeMaxBonusLevel() < 1) {
            reason.add("Skills.Acrobatics.Daze_MaxBonusLevel should be at least 1!");
        }

        if (getDazeModifier() < 0) {
            reason.add("Skills.Acrobatics.Daze_BonusDamage should be at least 0!");
        }

        if (getRetrieveChanceMax() < 1) {
            reason.add("Skills.Acrobatics.Retrieve_MaxBonus should be at least 1!");
        }

        if (getRetrieveMaxBonusLevel() < 1) {
            reason.add("Skills.Acrobatics.Retrieve_MaxBonusLevel should be at least 1!");
        }

        if (getForceMultiplier() < 0) {
            reason.add("Skills.Acrobatics.Force_Multiplier should be at least 0!");
        }

        /* AXES */
        if (getBonusDamageAxesBonusMax() < 1) {
            reason.add("Skills.Axes.DamageIncrease_MaxBonus should be at least 1!");
        }

        if (getBonusDamageAxesMaxBonusLevel() < 1) {
            reason.add("Skills.Axes.DamageIncrease_MaxBonusLevel should be at least 1!");
        }

        if (getAxesCriticalChance() < 1) {
            reason.add("Skills.Axes.AxesCritical_MaxChance should be at least 1!");
        }

        if (getAxesCriticalMaxBonusLevel() < 1) {
            reason.add("Skills.Axes.AxesCritical_MaxBonusLevel should be at least 1!");
        }

        if (getAxesCriticalPVPModifier() < 1) {
            reason.add("Skills.Axes.AxesCritical_PVP_Modifier should be at least 1!");
        }

        if (getAxesCriticalPVEModifier() < 1) {
            reason.add("Skills.Axes.AxesCritical_PVE_Modifier should be at least 1!");
        }

        if (getGreaterImpactChance() < 1) {
            reason.add("Skills.Axes.GreaterImpact_Chance should be at least 1!");
        }

        if (getGreaterImpactModifier() < 1) {
            reason.add("Skills.Axes.GreaterImpact_KnockbackModifier should be at least 1!");
        }

        if (getGreaterImpactBonusDamage() < 1) {
            reason.add("Skills.Axes.GreaterImpact_BonusDamage should be at least 1!");
        }

        if (getArmorImpactIncreaseLevel() < 1) {
            reason.add("Skills.Axes.ArmorImpact_IncreaseLevel should be at least 1!");
        }

        if (getImpactChance() < 1) {
            reason.add("Skills.Axes.ArmorImpact_Chance should be at least 1!");
        }

        if (getArmorImpactMaxDurabilityDamage() < 1) {
            reason.add("Skills.Axes.ArmorImpact_MaxPercentageDurabilityDamage should be at least 1!");
        }

        if (getSkullSplitterModifier() < 1) {
            reason.add("Skills.Axes.SkullSplitter_DamagerModifier should be at least 1!");
        }

        /* FISHING */
        if (getFishingTierLevelsTier1() >= getFishingTierLevelsTier2()) {
            reason.add("Skills.Fishing.Tier_Levels.Tier1 should be less than Skills.Fishing.Tier_Levels.Tier2!");
        }

        if (getFishingTierLevelsTier2() >= getFishingTierLevelsTier3()) {
            reason.add("Skills.Fishing.Tier_Levels.Tier2 should be less than Skills.Fishing.Tier_Levels.Tier3!");
        }

        if (getFishingTierLevelsTier3() >= getFishingTierLevelsTier4()) {
            reason.add("Skills.Fishing.Tier_Levels.Tier3 should be less than Skills.Fishing.Tier_Levels.Tier4!");
        }

        if (getFishingTierLevelsTier4() >= getFishingTierLevelsTier5()) {
            reason.add("Skills.Fishing.Tier_Levels.Tier4 should be less than Skills.Fishing.Tier_Levels.Tier5!");
        }

        if (getFishingMagicMultiplier() <= 0) {
            reason.add("Skills.Fishing.MagicHunter_Multiplier should be greater than 0!");
        }

        if (getFishermanDietRankChange() < 1) {
            reason.add("Skills.Fishing.Fisherman_Diet_RankChange should be at least 1!");
        }

        if (getIceFishingUnlockLevel() < 1) {
            reason.add("Skills.Fishing.Ice_Fishing_UnlockLevel should be at least 1!");
        }

        if (getShakeUnlockLevel() < 1) {
            reason.add("Skills.Fishing.Shake_UnlockLevel should be at least 1!");
        }

        if (getShakeChanceRank1() > getShakeChanceRank2()) {
            reason.add("Skills.Fishing.Shake_Chance.Rank_1 should be less or equal to Skills.Fishing.Shake_Chance.Rank_2!");
        }

        if (getShakeChanceRank2() > getShakeChanceRank3()) {
            reason.add("Skills.Fishing.Shake_Chance.Rank_2 should be less or equal to Skills.Fishing.Shake_Chance.Rank_3!");
        }

        if (getShakeChanceRank3() > getShakeChanceRank4()) {
            reason.add("Skills.Fishing.Shake_Chance.Rank_3 should be less or equal to Skills.Fishing.Shake_Chance.Rank_4!");
        }

        if (getShakeChanceRank4() > getShakeChanceRank5()) {
            reason.add("Skills.Fishing.Shake_Chance.Rank_4 should be less or equal to Skills.Fishing.Shake_Chance.Rank_5!");
        }

        if (getFishingVanillaXPModifierRank1() > getFishingVanillaXPModifierRank2()) {
            reason.add("Skills.Fishing.VanillaXPBoost.Rank_1 should be less or equal to Skills.Fishing.VanillaXPBoost.Rank_2!");
        }

        if (getFishingVanillaXPModifierRank2() > getFishingVanillaXPModifierRank3()) {
            reason.add("Skills.Fishing.VanillaXPBoost.Rank_2 should be less or equal to Skills.Fishing.VanillaXPBoost.Rank_3!");
        }

        if (getFishingVanillaXPModifierRank3() > getFishingVanillaXPModifierRank4()) {
            reason.add("Skills.Fishing.VanillaXPBoost.Rank_3 should be less or equal to Skills.Fishing.VanillaXPBoost.Rank_4!");
        }

        if (getFishingVanillaXPModifierRank4() > getFishingVanillaXPModifierRank5()) {
            reason.add("Skills.Fishing.VanillaXPBoost.Rank_4 should be less or equal to Skills.Fishing.VanillaXPBoost.Rank_5!");
        }

        if (getFishingVanillaXPModifierRank1() < 0) {
            reason.add("Skills.Fishing.VanillaXPBoost.Rank_1 should be at least 0!");
        }

        if (getFishingVanillaXPModifierRank2() < 0) {
            reason.add("Skills.Fishing.VanillaXPBoost.Rank_2 should be at least 0!");
        }

        if (getFishingVanillaXPModifierRank3() < 0) {
            reason.add("Skills.Fishing.VanillaXPBoost.Rank_3 should be at least 0!");
        }

        if (getFishingVanillaXPModifierRank4() < 0) {
            reason.add("Skills.Fishing.VanillaXPBoost.Rank_4 should be at least 0!");
        }

        if (getFishingVanillaXPModifierRank5() < 0) {
            reason.add("Skills.Fishing.VanillaXPBoost.Rank_5 should be at least 0!");
        }

        /* HERBALISM */
        if (getFarmerDietRankChange() < 1) {
            reason.add("Skills.Herbalism.Farmer_Diet_RankChange should be at least 1!");
        }

        if (getGreenThumbStageChange() < 1) {
            reason.add("Skills.Herbalism.GreenThumb_StageChange should be at least 1!");
        }

        if (getGreenThumbChanceMax() < 1) {
            reason.add("Skills.Herbalism.GreenThumb_ChanceMax should be at least 1!");
        }

        if (getGreenThumbMaxLevel() < 1) {
            reason.add("Skills.Herbalism.GreenThumb_MaxBonusLevel should be at least 1!");
        }

        if (getHerbalismDoubleDropsChanceMax() < 1) {
            reason.add("Skills.Herbalism.DoubleDrops_ChanceMax should be at least 1!");
        }

        if (getHerbalismDoubleDropsMaxLevel() < 1) {
            reason.add("Skills.Herbalism.DoubleDrops_MaxBonusLevel should be at least 1!");
        }

        if (getHylianLuckChanceMax() < 1) {
            reason.add("Skills.Herbalism.HylianLuck_ChanceMax should be at least 1!");
        }

        if (getHylianLuckMaxLevel() < 1) {
            reason.add("Skills.Herbalism.HylianLuck_MaxBonusLevel should be at least 1!");
        }

        if (getShroomThumbChanceMax() < 1) {
            reason.add("Skills.Herbalism.ShroomThumb_ChanceMax should be at least 1!");
        }

        if (getShroomThumbMaxLevel() < 1) {
            reason.add("Skills.Herbalism.ShroomThumb_MaxBonusLevel should be at least 1!");
        }

        /* MINING */
        if (getMiningDoubleDropChance() < 1) {
            reason.add("Skills.Mining.DoubleDrops_ChanceMax should be at least 1!");
        }

        if (getMiningDoubleDropMaxLevel() < 1) {
            reason.add("Skills.Mining.DoubleDrops_MaxBonusLevel should be at least 1!");
        }

        if (getBlastMiningRank1() > getBlastMiningRank2()) {
            reason.add("Skills.Mining.BlastMining_Rank1 should be less or equal to Skills.Mining.BlastMining_Rank2!");
        }

        if (getBlastMiningRank2() > getBlastMiningRank3()) {
            reason.add("Skills.Mining.BlastMining_Rank2 should be less or equal to Skills.Mining.BlastMining_Rank3!");
        }

        if (getBlastMiningRank3() > getBlastMiningRank4()) {
            reason.add("Skills.Mining.BlastMining_Rank3 should be less or equal to Skills.Mining.BlastMining_Rank4!");
        }

        if (getBlastMiningRank4() > getBlastMiningRank5()) {
            reason.add("Skills.Mining.BlastMining_Rank4 should be less or equal to Skills.Mining.BlastMining_Rank5!");
        }

        if (getBlastMiningRank5() > getBlastMiningRank6()) {
            reason.add("Skills.Mining.BlastMining_Rank5 should be less or equal to Skills.Mining.BlastMining_Rank6!");
        }

        if (getBlastMiningRank6() > getBlastMiningRank7()) {
            reason.add("Skills.Mining.BlastMining_Rank6 should be less or equal to Skills.Mining.BlastMining_Rank7!");
        }

        if (getBlastMiningRank7() > getBlastMiningRank8()) {
            reason.add("Skills.Mining.BlastMining_Rank7 should be less or equal to Skills.Mining.BlastMining_Rank8!");
        }

        if (getBlastDamageDecreaseRank1() > getBlastDamageDecreaseRank2()) {
            reason.add("Skills.Mining.BlastDamageDecrease_Rank1 should be less or equal to Skills.Mining.BlastDamageDecrease_Rank2!");
        }

        if (getBlastDamageDecreaseRank2() > getBlastDamageDecreaseRank3()) {
            reason.add("Skills.Mining.BlastDamageDecrease_Rank2 should be less or equal to Skills.Mining.BlastDamageDecrease_Rank3!");
        }

        if (getBlastDamageDecreaseRank3() > getBlastDamageDecreaseRank4()) {
            reason.add("Skills.Mining.BlastDamageDecrease_Rank3 should be less or equal to Skills.Mining.BlastDamageDecrease_Rank4!");
        }

        if (getBlastDamageDecreaseRank4() > getBlastDamageDecreaseRank5()) {
            reason.add("Skills.Mining.BlastDamageDecrease_Rank4 should be less or equal to Skills.Mining.BlastDamageDecrease_Rank5!");
        }

        if (getBlastDamageDecreaseRank5() > getBlastDamageDecreaseRank6()) {
            reason.add("Skills.Mining.BlastDamageDecrease_Rank5 should be less or equal to Skills.Mining.BlastDamageDecrease_Rank6!");
        }

        if (getBlastDamageDecreaseRank6() > getBlastDamageDecreaseRank7()) {
            reason.add("Skills.Mining.BlastDamageDecrease_Rank6 should be less or equal to Skills.Mining.BlastDamageDecrease_Rank7!");
        }

        if (getBlastDamageDecreaseRank7() > getBlastDamageDecreaseRank8()) {
            reason.add("Skills.Mining.BlastDamageDecrease_Rank7 should be less or equal to Skills.Mining.BlastDamageDecrease_Rank8!");
        }

        if (getOreBonusRank1() > getOreBonusRank2()) {
            reason.add("Skills.Mining.OreBonus_Rank1 should be less or equal to Skills.Mining.OreBonus_Rank2!");
        }

        if (getOreBonusRank2() > getOreBonusRank3()) {
            reason.add("Skills.Mining.OreBonus_Rank2 should be less or equal to Skills.Mining.OreBonus_Rank3!");
        }

        if (getOreBonusRank3() > getOreBonusRank4()) {
            reason.add("Skills.Mining.OreBonus_Rank3 should be less or equal to Skills.Mining.OreBonus_Rank4!");
        }

        if (getOreBonusRank4() > getOreBonusRank5()) {
            reason.add("Skills.Mining.OreBonus_Rank4 should be less or equal to Skills.Mining.OreBonus_Rank5!");
        }

        if (getOreBonusRank5() > getOreBonusRank6()) {
            reason.add("Skills.Mining.OreBonus_Rank5 should be less or equal to Skills.Mining.OreBonus_Rank6!");
        }

        if (getOreBonusRank6() > getOreBonusRank7()) {
            reason.add("Skills.Mining.OreBonus_Rank6 should be less or equal to Skills.Mining.OreBonus_Rank7!");
        }

        if (getOreBonusRank7() > getOreBonusRank8()) {
            reason.add("Skills.Mining.OreBonus_Rank7 should be less or equal to Skills.Mining.OreBonus_Rank8!");
        }

        if (getDebrisReductionRank1() > getDebrisReductionRank2()) {
            reason.add("Skills.Mining.DebrisReduction_Rank1 should be less or equal to Skills.Mining.DebrisReduction_Rank2!");
        }

        if (getDebrisReductionRank2() > getDebrisReductionRank3()) {
            reason.add("Skills.Mining.DebrisReduction_Rank2 should be less or equal to Skills.Mining.DebrisReduction_Rank3!");
        }

        if (getDebrisReductionRank3() > getDebrisReductionRank4()) {
            reason.add("Skills.Mining.DebrisReduction_Rank3 should be less or equal to Skills.Mining.DebrisReduction_Rank4!");
        }

        if (getDebrisReductionRank4() > getDebrisReductionRank5()) {
            reason.add("Skills.Mining.DebrisReduction_Rank4 should be less or equal to Skills.Mining.DebrisReduction_Rank5!");
        }

        if (getDebrisReductionRank5() > getDebrisReductionRank6()) {
            reason.add("Skills.Mining.DebrisReduction_Rank5 should be less or equal to Skills.Mining.DebrisReduction_Rank6!");
        }

        if (getDebrisReductionRank6() > getDebrisReductionRank7()) {
            reason.add("Skills.Mining.DebrisReduction_Rank6 should be less or equal to Skills.Mining.DebrisReduction_Rank7!");
        }

        if (getDebrisReductionRank7() > getDebrisReductionRank8()) {
            reason.add("Skills.Mining.DebrisReduction_Rank7 should be less or equal to Skills.Mining.DebrisReduction_Rank8!");
        }

        if (getDropMultiplierRank1() > getDropMultiplierRank2()) {
            reason.add("Skills.Mining.DropMultiplier_Rank1 should be less or equal to Skills.Mining.DropMultiplier_Rank2!");
        }

        if (getDropMultiplierRank2() > getDropMultiplierRank3()) {
            reason.add("Skills.Mining.DropMultiplier_Rank2 should be less or equal to Skills.Mining.DropMultiplier_Rank3!");
        }

        if (getDropMultiplierRank3() > getDropMultiplierRank4()) {
            reason.add("Skills.Mining.DropMultiplier_Rank3 should be less or equal to Skills.Mining.DropMultiplier_Rank4!");
        }

        if (getDropMultiplierRank4() > getDropMultiplierRank5()) {
            reason.add("Skills.Mining.DropMultiplier_Rank4 should be less or equal to Skills.Mining.DropMultiplier_Rank5!");
        }

        if (getDropMultiplierRank5() > getDropMultiplierRank6()) {
            reason.add("Skills.Mining.DropMultiplier_Rank5 should be less or equal to Skills.Mining.DropMultiplier_Rank6!");
        }

        if (getDropMultiplierRank6() > getDropMultiplierRank7()) {
            reason.add("Skills.Mining.DropMultiplier_Rank6 should be less or equal to Skills.Mining.DropMultiplier_Rank7!");
        }

        if (getDropMultiplierRank7() > getDropMultiplierRank8()) {
            reason.add("Skills.Mining.DropMultiplier_Rank7 should be less or equal to Skills.Mining.DropMultiplier_Rank8!");
        }

        if (getBlastRadiusModifierRank1() > getBlastRadiusModifierRank2()) {
            reason.add("Skills.Mining.BlastRadiusModifier_Rank1 should be less or equal to Skills.Mining.BlastRadiusModifier_Rank2!");
        }

        if (getBlastRadiusModifierRank2() > getBlastRadiusModifierRank3()) {
            reason.add("Skills.Mining.BlastRadiusModifier_Rank2 should be less or equal to Skills.Mining.BlastRadiusModifier_Rank3!");
        }

        if (getBlastRadiusModifierRank3() > getBlastRadiusModifierRank4()) {
            reason.add("Skills.Mining.BlastRadiusModifier_Rank3 should be less or equal to Skills.Mining.BlastRadiusModifier_Rank4!");
        }

        if (getBlastRadiusModifierRank4() > getBlastRadiusModifierRank5()) {
            reason.add("Skills.Mining.BlastRadiusModifier_Rank4 should be less or equal to Skills.Mining.BlastRadiusModifier_Rank5!");
        }

        if (getBlastRadiusModifierRank5() > getBlastRadiusModifierRank6()) {
            reason.add("Skills.Mining.BlastRadiusModifier_Rank5 should be less or equal to Skills.Mining.BlastRadiusModifier_Rank6!");
        }

        if (getBlastRadiusModifierRank6() > getBlastRadiusModifierRank7()) {
            reason.add("Skills.Mining.BlastRadiusModifier_Rank6 should be less or equal to Skills.Mining.BlastRadiusModifier_Rank7!");
        }

        if (getBlastRadiusModifierRank7() > getBlastRadiusModifierRank8()) {
            reason.add("Skills.Mining.BlastRadiusModifier_Rank7 should be less or equal to Skills.Mining.BlastRadiusModifier_Rank8!");
        }

        /* REPAIR */
        if (getRepairMasteryMaxBonus() < 1) {
            reason.add("Skills.Repair.RepairMastery_MaxBonusPercentage should be at least 1!");
        }

        if (getRepairMasteryMaxLevel() < 1) {
            reason.add("Skills.Repair.RepairMastery_MaxBonusLevel should be at least 1!");
        }

        if (getSuperRepairChanceMax() < 1) {
            reason.add("Skills.Repair.SuperRepair_ChanceMax should be at least 1!");
        }

        if (getSuperRepairMaxLevel() < 1) {
            reason.add("Skills.Repair.SuperRepair_MaxBonusLevel should be at least 1!");
        }

        if (getSalvageUnlockLevel() < 1) {
            reason.add("Skills.Repair.Salvage_UnlockLevel should be at least 1!");
        }

        if (getArcaneForgingDowngradeChanceRank1() < 1 || getArcaneForgingDowngradeChanceRank1() > 100) {
            reason.add("Skills.Repair.Arcane_Forging.Downgrades.Chance.Rank_1 only accepts values from 1 to 100!");
        }

        if (getArcaneForgingDowngradeChanceRank2() < 1 || getArcaneForgingDowngradeChanceRank2() > 100) {
            reason.add("Skills.Repair.Arcane_Forging.Downgrades.Chance.Rank_2 only accepts values from 1 to 100!");
        }

        if (getArcaneForgingDowngradeChanceRank3() < 1 || getArcaneForgingDowngradeChanceRank3() > 100) {
            reason.add("Skills.Repair.Arcane_Forging.Downgrades.Chance.Rank_3 only accepts values from 1 to 100!");
        }

        if (getArcaneForgingDowngradeChanceRank4() < 1 || getArcaneForgingDowngradeChanceRank4() > 100) {
            reason.add("Skills.Repair.Arcane_Forging.Downgrades.Chance.Rank_4 only accepts values from 1 to 100!");
        }

        if (getArcaneForgingKeepEnchantsChanceRank1() < 1 || getArcaneForgingKeepEnchantsChanceRank1() > 100) {
            reason.add("Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_1 only accepts values from 1 to 100!");
        }

        if (getArcaneForgingDowngradeChanceRank2() < 1 || getArcaneForgingDowngradeChanceRank2() > 100) {
            reason.add("Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_2 only accepts values from 1 to 100!");
        }

        if (getArcaneForgingDowngradeChanceRank3() < 1 || getArcaneForgingDowngradeChanceRank3() > 100) {
            reason.add("Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_3 only accepts values from 1 to 100!");
        }

        if (getArcaneForgingDowngradeChanceRank4() < 1 || getArcaneForgingDowngradeChanceRank4() > 100) {
            reason.add("Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_4 only accepts values from 1 to 100!");
        }

        if (getArcaneForgingRankLevels1() < 0) {
            reason.add("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_1 should be at least 0!");
        }

        if (getArcaneForgingRankLevels2() < 0) {
            reason.add("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_2 should be at least 0!");
        }

        if (getArcaneForgingRankLevels3() < 0) {
            reason.add("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_3 should be at least 0!");
        }

        if (getArcaneForgingRankLevels4() < 0) {
            reason.add("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_4 should be at least 0!");
        }

        /* SMELTING */
        if (getBurnModifierMaxLevel() < 1) {
            reason.add("Skills.Smelting.FuelEfficiency_MaxBonusLevel should be at least 1!");
        }

        if (getBurnTimeMultiplier() < 1) {
            reason.add("Skills.Smelting.FuelEfficiency_Multiplier should be at least 1!");
        }

        if (getSecondSmeltMaxLevel() < 1) {
            reason.add("Skills.Smelting.SecondSmelt_MaxBonusLevel should be at least 1!");
        }

        if (getSecondSmeltMaxChance() < 1) {
            reason.add("Skills.Smelting.SecondSmelt_MaxBonusChance should be at least 1!");
        }

        if (getFluxMiningUnlockLevel() < 1) {
            reason.add("Skills.Smelting.FluxMining_UnlockLevel should be at least 1!");
        }

        if (getFluxMiningChance() < 1) {
            reason.add("Skills.Smelting.FluxMining_Chance should be at least 1!");
        }

        if (getSmeltingVanillaXPBoostRank1Level() > getSmeltingVanillaXPBoostRank2Level()) {
            reason.add("Skills.Smelting.VanillaXPBoost_Rank1Level should be less or equal to Skills.Smelting.VanillaXPBoost_Rank2Level!");
        }

        if (getSmeltingVanillaXPBoostRank2Level() > getSmeltingVanillaXPBoostRank3Level()) {
            reason.add("Skills.Smelting.VanillaXPBoost_Rank2Level should be less or equal to Skills.Smelting.VanillaXPBoost_Rank3Level!");
        }

        if (getSmeltingVanillaXPBoostRank3Level() > getSmeltingVanillaXPBoostRank4Level()) {
            reason.add("Skills.Smelting.VanillaXPBoost_Rank3Level should be less or equal to Skills.Smelting.VanillaXPBoost_Rank4Level!");
        }

        if (getSmeltingVanillaXPBoostRank4Level() > getSmeltingVanillaXPBoostRank5Level()) {
            reason.add("Skills.Smelting.VanillaXPBoost_Rank4Level should be less or equal to Skills.Smelting.VanillaXPBoost_Rank5Level!");
        }

        if (getSmeltingVanillaXPBoostRank1Multiplier() < 1) {
            reason.add("Skills.Smelting.VanillaXPBoost_Rank1Multiplier should be at least 1!");
        }

        if (getSmeltingVanillaXPBoostRank2Multiplier() < 1) {
            reason.add("Skills.Smelting.VanillaXPBoost_Rank2Multiplier should be at least 1!");
        }

        if (getSmeltingVanillaXPBoostRank3Multiplier() < 1) {
            reason.add("Skills.Smelting.VanillaXPBoost_Rank3Multiplier should be at least 1!");
        }

        if (getSmeltingVanillaXPBoostRank4Multiplier() < 1) {
            reason.add("Skills.Smelting.VanillaXPBoost_Rank4Multiplier should be at least 1!");
        }

        if (getSmeltingVanillaXPBoostRank5Multiplier() < 1) {
            reason.add("Skills.Smelting.VanillaXPBoost_Rank5Multiplier should be at least 1!");
        }

        /* SWORDS */
        if (getBleedChanceMax() < 1) {
            reason.add("Skills.Swords.Bleed_ChanceMax should be at least 1!");
        }

        if (getBleedMaxBonusLevel() < 1) {
            reason.add("Skills.Swords.Bleed_MaxBonusLevel should be at least 1!");
        }

        if (getBleedMaxTicks() < 1) {
            reason.add("Skills.Swords.Bleed_MaxTicks should be at least 1!");
        }

        if (getBleedMaxTicks() < getBleedBaseTicks()) {
            reason.add("Skills.Swords.Bleed_MaxTicks should be at least Skills.Swords.Bleed_BaseTicks!");
        }

        if (getBleedBaseTicks() < 1) {
            reason.add("Skills.Swords.Bleed_BaseTicks should be at least 1!");
        }

        if (getCounterChanceMax() < 1) {
            reason.add("Skills.Swords.Counter_ChanceMax should be at least 1!");
        }

        if (getCounterMaxBonusLevel() < 1) {
            reason.add("Skills.Swords.Counter_MaxBonusLevel should be at least 1!");
        }

        if (getCounterModifier() < 1) {
            reason.add("Skills.Swords.Counter_DamageModifier should be at least 1!");
        }

        if (getSerratedStrikesModifier() < 1) {
            reason.add("Skills.Swords.SerratedStrikes_DamageModifier should be at least 1!");
        }

        if (getSerratedStrikesTicks() < 1) {
            reason.add("Skills.Swords.SerratedStrikes_BleedTicks should be at least 1!");
        }

        /* TAMING */

        if (getGoreChanceMax() < 1) {
            reason.add("Skills.Taming.Gore_ChanceMax should be at least 1!");
        }

        if (getGoreMaxBonusLevel() < 1) {
            reason.add("Skills.Taming.Gore_MaxBonusLevel should be at least 1!");
        }

        if (getGoreBleedTicks() < 1) {
            reason.add("Skills.Taming.Gore_BleedTicks should be at least 1!");
        }

        if (getGoreModifier() < 1) {
            reason.add("Skills.Taming.Gore_Modifier should be at least 1!");
        }

        if (getFastFoodUnlock() < 1) {
            reason.add("Skills.Taming.FastFood_UnlockLevel should be at least 1!");
        }

        if (getFastFoodChance() < 1) {
            reason.add("Skills.Taming.FastFood_Chance should be at least 1!");
        }

        if (getEnviromentallyAwareUnlock() < 1) {
            reason.add("Skills.Taming.EnvironmentallyAware_UnlockLevel should be at least 1!");
        }

        if (getThickFurUnlock() < 1) {
            reason.add("Skills.Taming.ThickFur_UnlockLevel should be at least 1!");
        }

        if (getThickFurModifier() < 1) {
            reason.add("Skills.Taming.ThickFur_Modifier should be at least 1!");
        }

        if (getHolyHoundUnlock() < 1) {
            reason.add("Skills.Taming.HolyHound_UnlockLevel should be at least 1!");
        }

        if (getShockProofUnlock() < 1) {
            reason.add("Skills.Taming.ShockProof_UnlockLevel should be at least 1!");
        }

        if (getShockProofModifier() < 1) {
            reason.add("Skills.Taming.ShockProof_Modifier should be at least 1!");
        }

        if (getSharpenedClawsUnlock() < 1) {
            reason.add("Skills.Taming.SharpenedClaws_UnlockLevel should be at least 1!");
        }

        if (getSharpenedClawsBonus() < 1) {
            reason.add("Skills.Taming.SharpenedClaws_Bonus should be at least 1!");
        }


        /* UNARMED */
        if (getDisarmChanceMax() < 1) {
            reason.add("Skills.Unarmed.Disarm_ChanceMax should be at least 1!");
        }

        if (getDisarmMaxBonusLevel() < 1) {
            reason.add("Skills.Unarmed.Disarm_MaxBonusLevel should be at least 1!");
        }

        if (getDeflectChanceMax() < 1) {
            reason.add("Skills.Unarmed.Deflect_ChanceMax should be at least 1!");
        }

        if (getDeflectMaxBonusLevel() < 1) {
            reason.add("Skills.Unarmed.Deflect_MaxBonusLevel should be at least 1!");
        }

        if (getIronGripChanceMax() < 1) {
            reason.add("Skills.Unarmed.IronGrip_ChanceMax should be at least 1!");
        }

        if (getIronGripMaxBonusLevel() < 1) {
            reason.add("Skills.Unarmed.IronGrip_MaxBonusLevel should be at least 1!");
        }

        if (getIronArmMaxBonus() < 0) {
            reason.add("Skills.Unarmed.IronArm_BonusMax should be at least 0!");
        }

        if (getIronArmIncreaseLevel() < 1) {
            reason.add("Skills.Unarmed.IronArm_IncreaseLevel should be at least 1!");
        }

        /* WOODCUTTING */
        if (getLeafBlowUnlockLevel() < 1) {
            reason.add("Skills.Woodcutting.LeafBlower_UnlockLevel should be at least 1!");
        }

        if (getWoodcuttingDoubleDropChance() < 1) {
            reason.add("Skills.Woodcutting.DoubleDrops_ChanceMax should be at least 1!");
        }

        if (getWoodcuttingDoubleDropMaxLevel() < 1) {
            reason.add("Skills.Woodcutting.DoubleDrops_MaxBonusLevel should be at least 1!");
        }

        /* KRAKEN */
        if (getKrakenTriesBeforeRelease() < 1) {
            reason.add("Kraken.Tries_Before_Release should be at least 1!");
        }

        if (getKrakenHealth() < 1) {
            reason.add("Kraken.Health should be at least 1!");
        }

        if (getKrakenAttackInterval() < 1) {
            reason.add("Kraken.Attack_Interval_Seconds should be at least 1!");
        }

        if (getKrakenAttackDamage() < 1) {
            reason.add("Kraken.Attack_Damage should be at least 1!");
        }

        // Check if there were any errors
        if (noErrorsInConfig(reason)) {
            mcMMO.p.debug("No errors found in " + fileName + "!");
        } else {
            mcMMO.p.getLogger().warning("Errors were found in " + fileName + "! mcMMO was disabled!");
            mcMMO.p.getServer().getPluginManager().disablePlugin(mcMMO.p);
        }
    }

    /* GENERAL */
    public int getAbilityLength() { return config.getInt("Skills.General.Ability_IncreaseLevel", 50); }
    public int getEnchantBuff() { return config.getInt("Skills.General.Ability_EnchantBuff", 5); }

    /* ACROBATICS */
    public double getDodgeChanceMax() { return config.getDouble("Skills.Acrobatics.Dodge_ChanceMax", 20.0D); }
    public int getDodgeMaxBonusLevel() { return config.getInt("Skills.Acrobatics.Dodge_MaxBonusLevel", 800); }
    public int getDodgeDamageModifier() { return config.getInt("Skills.Acrobatics.Dodge_DamageModifer", 2); }

    public double getRollChanceMax() { return config.getDouble("Skills.Acrobatics.Roll_ChanceMax", 100.0D); }
    public int getRollMaxBonusLevel() { return config.getInt("Skills.Acrobatics.Roll_MaxBonusLevel", 1000); }
    public int getRollDamageThreshold() { return config.getInt("Skills.Acrobatics.Roll_DamageThreshold", 7); }

    public double getGracefulRollChanceMax() { return config.getDouble("Skills.Acrobatics.GracefulRoll_ChanceMax", 100.0D); }
    public int getGracefulRollMaxBonusLevel() { return config.getInt("Skills.Acrobatics.GracefulRoll_MaxBonusLevel", 500); }
    public int getGracefulRollDamageThreshold() { return config.getInt("Skills.Acrobatics.GracefulRoll_DamageThreshold", 14); }

    public int getDodgeXPModifier() { return config.getInt("Skills.Acrobatics.Dodge_XP_Modifier", 120); }
    public int getRollXPModifier() { return config.getInt("Skills.Acrobatics.Roll_XP_Modifier", 80); }
    public int getFallXPModifier() { return config.getInt("Skills.Acrobatics.Fall_XP_Modifier", 120); }

    /* ARCHERY */
    public int getSkillShotIncreaseLevel() { return config.getInt("Skills.Archery.SkillShot_IncreaseLevel", 50); }
    public double getSkillShotIncreasePercentage() { return config.getDouble("Skills.Archery.SkillShot_IncreasePercentage", 0.1D); }
    public double getSkillShotBonusMax() { return config.getDouble("Skills.Archery.SkillShot_MaxBonus", 2.0D); }

    public double getDazeBonusMax() { return config.getDouble("Skills.Archery.Daze_MaxChance", 50.0D); }
    public int getDazeMaxBonusLevel() { return config.getInt("Skills.Archery.Daze_MaxBonusLevel", 1000); }
    public int getDazeModifier() { return config.getInt("Skills.Archery.Daze_BonusDamage", 4); }

    public double getRetrieveChanceMax() { return config.getDouble("Skills.Archery.Retrieve_MaxBonus", 100.0D); }
    public int getRetrieveMaxBonusLevel() { return config.getInt("Skills.Archery.Retrieve_MaxBonusLevel", 1000); }

    public double getForceMultiplier() { return config.getDouble("Skills.Archery.Force_Multiplier", 2.0D); }

    /* AXES */
    public int getBonusDamageAxesBonusMax() { return config.getInt("Skills.Axes.DamageIncrease_MaxBonus", 4); }
    public int getBonusDamageAxesMaxBonusLevel() { return config.getInt("Skills.Axes.DamageIncrease_MaxBonusLevel", 200); }

    public double getAxesCriticalChance() { return config.getDouble("Skills.Axes.AxesCritical_MaxChance", 37.50D); }
    public int getAxesCriticalMaxBonusLevel() { return config.getInt("Skills.Axes.AxesCritical_MaxBonusLevel", 750); }
    public double getAxesCriticalPVPModifier() { return config.getDouble("Skills.Axes.AxesCritical_PVP_Modifier", 1.5D); }
    public double getAxesCriticalPVEModifier() { return config.getDouble("Skills.Axes.AxesCritical_PVE_Modifier", 2.0D); }

    public double getGreaterImpactChance() { return config.getDouble("Skills.Axes.GreaterImpact_Chance", 25.0D); }
    public double getGreaterImpactModifier() { return config.getDouble("Skills.Axes.GreaterImpact_KnockbackModifier", 1.5); }
    public int getGreaterImpactBonusDamage() { return config.getInt("Skills.Axes.GreaterImpact_BonusDamage", 2); }

    public int getArmorImpactIncreaseLevel() { return config.getInt("Skills.Axes.ArmorImpact_IncreaseLevel", 50); }
    public double getImpactChance() { return config.getDouble("Skills.Axes.ArmorImpact_Chance", 25.0D); }
    public double getArmorImpactMaxDurabilityDamage() { return config.getDouble("Skills.Axes.ArmorImpact_MaxPercentageDurabilityDamage", 20.0D); }

    public int getSkullSplitterModifier() { return config.getInt("Skills.Axes.SkullSplitter_DamagerModifier", 2); }

    /* EXCAVATION */
    //Nothing to configure, everything is already configurable in config.yml

    /* FISHING */
    public int getFishingTierLevelsTier1() { return config.getInt("Skills.Fishing.Tier_Levels.Tier1", 0); }
    public int getFishingTierLevelsTier2() { return config.getInt("Skills.Fishing.Tier_Levels.Tier2", 200); }
    public int getFishingTierLevelsTier3() { return config.getInt("Skills.Fishing.Tier_Levels.Tier3", 400); }
    public int getFishingTierLevelsTier4() { return config.getInt("Skills.Fishing.Tier_Levels.Tier4", 600); }
    public int getFishingTierLevelsTier5() { return config.getInt("Skills.Fishing.Tier_Levels.Tier5", 800); }

    public int getFishingMagicMultiplier() { return config.getInt("Skills.Fishing.MagicHunter_Multiplier", 5); }

    public int getFishermanDietRankChange() { return config.getInt("Skills.Fishing.Fisherman_Diet_RankChange", 200); }

    public int getIceFishingUnlockLevel() { return config.getInt("Skills.Fishing.Ice_Fishing_UnlockLevel", 50); }

    /* Shake */
    public int getShakeUnlockLevel() { return config.getInt("Skills.Fishing.Shake_UnlockLevel", 150); }
    public int getShakeChanceRank1() { return config.getInt("Skills.Fishing.Shake_Chance.Rank_1", 25); }
    public int getShakeChanceRank2() { return config.getInt("Skills.Fishing.Shake_Chance.Rank_2", 40); }
    public int getShakeChanceRank3() { return config.getInt("Skills.Fishing.Shake_Chance.Rank_3", 55); }
    public int getShakeChanceRank4() { return config.getInt("Skills.Fishing.Shake_Chance.Rank_4", 60); }
    public int getShakeChanceRank5() { return config.getInt("Skills.Fishing.Shake_Chance.Rank_5", 75); }

    /* Vanilla XP Boost */
    public int getFishingVanillaXPModifierRank1() { return config.getInt("Skills.Fishing.VanillaXPBoost.Rank_1", 1); }
    public int getFishingVanillaXPModifierRank2() { return config.getInt("Skills.Fishing.VanillaXPBoost.Rank_2", 2); }
    public int getFishingVanillaXPModifierRank3() { return config.getInt("Skills.Fishing.VanillaXPBoost.Rank_3", 3); }
    public int getFishingVanillaXPModifierRank4() { return config.getInt("Skills.Fishing.VanillaXPBoost.Rank_4", 4); }
    public int getFishingVanillaXPModifierRank5() { return config.getInt("Skills.Fishing.VanillaXPBoost.Rank_5", 5); }

    /* HERBALISM */
    public int getFarmerDietRankChange() { return config.getInt("Skills.Herbalism.Farmer_Diet_RankChange", 200); }

    public int getGreenThumbStageChange() { return config.getInt("Skills.Herbalism.GreenThumb_StageChange", 200); }
    public double getGreenThumbChanceMax() { return config.getDouble("Skills.Herbalism.GreenThumb_ChanceMax", 100.0D); }
    public int getGreenThumbMaxLevel() { return config.getInt("Skills.Herbalism.GreenThumb_MaxBonusLevel", 1500); }

    public double getHerbalismDoubleDropsChanceMax() { return config.getDouble("Skills.Herbalism.DoubleDrops_ChanceMax", 100.0D); }
    public int getHerbalismDoubleDropsMaxLevel() { return config.getInt("Skills.Herbalism.DoubleDrops_MaxBonusLevel", 1000); }

    public double getHylianLuckChanceMax() { return config.getDouble("Skills.Herbalism.HylianLuck_ChanceMax", 10.0D); }
    public int getHylianLuckMaxLevel() { return config.getInt("Skills.Herbalism.HylianLuck_MaxBonusLevel", 1000); }

    public double getShroomThumbChanceMax() { return config.getDouble("Skills.Herbalism.ShroomThumb_ChanceMax", 50.0D); }
    public int getShroomThumbMaxLevel() { return config.getInt("Skills.Herbalism.ShroomThumb_MaxBonusLevel", 1500); }

    /* MINING */
    public double getMiningDoubleDropChance() { return config.getDouble("Skills.Mining.DoubleDrops_ChanceMax", 100.0D); }
    public int getMiningDoubleDropMaxLevel() { return config.getInt("Skills.Mining.DoubleDrops_MaxBonusLevel", 1000); }

    public int getBlastMiningRank1() { return config.getInt("Skills.Mining.BlastMining_Rank1", 125); }
    public int getBlastMiningRank2() { return config.getInt("Skills.Mining.BlastMining_Rank2", 250); }
    public int getBlastMiningRank3() { return config.getInt("Skills.Mining.BlastMining_Rank3", 375); }
    public int getBlastMiningRank4() { return config.getInt("Skills.Mining.BlastMining_Rank4", 500); }
    public int getBlastMiningRank5() { return config.getInt("Skills.Mining.BlastMining_Rank5", 625); }
    public int getBlastMiningRank6() { return config.getInt("Skills.Mining.BlastMining_Rank6", 750); }
    public int getBlastMiningRank7() { return config.getInt("Skills.Mining.BlastMining_Rank7", 875); }
    public int getBlastMiningRank8() { return config.getInt("Skills.Mining.BlastMining_Rank8", 1000); }

    public double getBlastDamageDecreaseRank1() { return config.getDouble("Skills.Mining.BlastDamageDecrease_Rank1", 0.0D); }
    public double getBlastDamageDecreaseRank2() { return config.getDouble("Skills.Mining.BlastDamageDecrease_Rank2", 0.0D); }
    public double getBlastDamageDecreaseRank3() { return config.getDouble("Skills.Mining.BlastDamageDecrease_Rank3", 0.0D); }
    public double getBlastDamageDecreaseRank4() { return config.getDouble("Skills.Mining.BlastDamageDecrease_Rank4", 25.0D); }
    public double getBlastDamageDecreaseRank5() { return config.getDouble("Skills.Mining.BlastDamageDecrease_Rank5", 25.0D); }
    public double getBlastDamageDecreaseRank6() { return config.getDouble("Skills.Mining.BlastDamageDecrease_Rank6", 50.0D); }
    public double getBlastDamageDecreaseRank7() { return config.getDouble("Skills.Mining.BlastDamageDecrease_Rank7", 50.0D); }
    public double getBlastDamageDecreaseRank8() { return config.getDouble("Skills.Mining.BlastDamageDecrease_Rank8", 100.0D); }

    public double getOreBonusRank1() { return config.getDouble("Skills.Mining.OreBonus_Rank1", 35.0D); }
    public double getOreBonusRank2() { return config.getDouble("Skills.Mining.OreBonus_Rank2", 40.0D); }
    public double getOreBonusRank3() { return config.getDouble("Skills.Mining.OreBonus_Rank3", 45.0D); }
    public double getOreBonusRank4() { return config.getDouble("Skills.Mining.OreBonus_Rank4", 50.0D); }
    public double getOreBonusRank5() { return config.getDouble("Skills.Mining.OreBonus_Rank5", 55.0D); }
    public double getOreBonusRank6() { return config.getDouble("Skills.Mining.OreBonus_Rank6", 60.0D); }
    public double getOreBonusRank7() { return config.getDouble("Skills.Mining.OreBonus_Rank7", 65.0D); }
    public double getOreBonusRank8() { return config.getDouble("Skills.Mining.OreBonus_Rank8", 70.0D); }

    public double getDebrisReductionRank1() { return config.getDouble("Skills.Mining.DebrisReduction_Rank1", 10.0D); }
    public double getDebrisReductionRank2() { return config.getDouble("Skills.Mining.DebrisReduction_Rank2", 20.0D); }
    public double getDebrisReductionRank3() { return config.getDouble("Skills.Mining.DebrisReduction_Rank3", 30.0D); }
    public double getDebrisReductionRank4() { return config.getDouble("Skills.Mining.DebrisReduction_Rank4", 30.0D); }
    public double getDebrisReductionRank5() { return config.getDouble("Skills.Mining.DebrisReduction_Rank5", 30.0D); }
    public double getDebrisReductionRank6() { return config.getDouble("Skills.Mining.DebrisReduction_Rank6", 30.0D); }
    public double getDebrisReductionRank7() { return config.getDouble("Skills.Mining.DebrisReduction_Rank7", 30.0D); }
    public double getDebrisReductionRank8() { return config.getDouble("Skills.Mining.DebrisReduction_Rank8", 30.0D); }

    public int getDropMultiplierRank1() { return config.getInt("Skills.Mining.DropMultiplier_Rank1", 1); }
    public int getDropMultiplierRank2() { return config.getInt("Skills.Mining.DropMultiplier_Rank2", 1); }
    public int getDropMultiplierRank3() { return config.getInt("Skills.Mining.DropMultiplier_Rank3", 1); }
    public int getDropMultiplierRank4() { return config.getInt("Skills.Mining.DropMultiplier_Rank4", 1); }
    public int getDropMultiplierRank5() { return config.getInt("Skills.Mining.DropMultiplier_Rank5", 2); }
    public int getDropMultiplierRank6() { return config.getInt("Skills.Mining.DropMultiplier_Rank6", 2); }
    public int getDropMultiplierRank7() { return config.getInt("Skills.Mining.DropMultiplier_Rank7", 3); }
    public int getDropMultiplierRank8() { return config.getInt("Skills.Mining.DropMultiplier_Rank8", 3); }

    public double getBlastRadiusModifierRank1() { return config.getDouble("Skills.Mining.BlastRadiusModifier_Rank1", 1.0); }
    public double getBlastRadiusModifierRank2() { return config.getDouble("Skills.Mining.BlastRadiusModifier_Rank2", 1.0); }
    public double getBlastRadiusModifierRank3() { return config.getDouble("Skills.Mining.BlastRadiusModifier_Rank3", 2.0); }
    public double getBlastRadiusModifierRank4() { return config.getDouble("Skills.Mining.BlastRadiusModifier_Rank4", 2.0); }
    public double getBlastRadiusModifierRank5() { return config.getDouble("Skills.Mining.BlastRadiusModifier_Rank5", 3.0); }
    public double getBlastRadiusModifierRank6() { return config.getDouble("Skills.Mining.BlastRadiusModifier_Rank6", 3.0); }
    public double getBlastRadiusModifierRank7() { return config.getDouble("Skills.Mining.BlastRadiusModifier_Rank7", 4.0); }
    public double getBlastRadiusModifierRank8() { return config.getDouble("Skills.Mining.BlastRadiusModifier_Rank8", 4.0); }

    /* REPAIR */
    public double getRepairMasteryMaxBonus() { return config.getDouble("Skills.Repair.RepairMastery_MaxBonusPercentage", 200.0D); }
    public int getRepairMasteryMaxLevel() { return config.getInt("Skills.Repair.RepairMastery_MaxBonusLevel", 1000); }
    public double getSuperRepairChanceMax() { return config.getDouble("Skills.Repair.SuperRepair_ChanceMax", 100.0D); }
    public int getSuperRepairMaxLevel() { return config.getInt("Skills.Repair.SuperRepair_MaxBonusLevel", 1000); }
    public int getSalvageUnlockLevel() { return config.getInt("Skills.Repair.Salvage_UnlockLevel", 600); }

    /* Arcane Forging */
    public boolean getArcaneForgingDowngradeEnabled() { return config.getBoolean("Skills.Repair.Arcane_Forging.Downgrades.Enabled", true); }
    public int getArcaneForgingDowngradeChanceRank1() { return config.getInt("Skills.Repair.Arcane_Forging.Downgrades.Chance.Rank_1", 75); }
    public int getArcaneForgingDowngradeChanceRank2() { return config.getInt("Skills.Repair.Arcane_Forging.Downgrades.Chance.Rank_2", 50); }
    public int getArcaneForgingDowngradeChanceRank3() { return config.getInt("Skills.Repair.Arcane_Forging.Downgrades.Chance.Rank_3", 25); }
    public int getArcaneForgingDowngradeChanceRank4() { return config.getInt("Skills.Repair.Arcane_Forging.Downgrades.Chance.Rank_4", 15); }
    public boolean getArcaneForgingEnchantLossEnabled() { return config.getBoolean("Skills.Repair.Arcane_Forging.May_Lose_Enchants", true); }
    public int getArcaneForgingKeepEnchantsChanceRank1() { return config.getInt("Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_1", 10); }
    public int getArcaneForgingKeepEnchantsChanceRank2() { return config.getInt("Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_2", 20); }
    public int getArcaneForgingKeepEnchantsChanceRank3() { return config.getInt("Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_3", 30); }
    public int getArcaneForgingKeepEnchantsChanceRank4() { return config.getInt("Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_4", 40); }
    public int getArcaneForgingRankLevels1() { return config.getInt("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_1", 100); }
    public int getArcaneForgingRankLevels2() { return config.getInt("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_2", 250); }
    public int getArcaneForgingRankLevels3() { return config.getInt("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_3", 500); }
    public int getArcaneForgingRankLevels4() { return config.getInt("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_4", 750); }

    /* SMELTING */
    public int getBurnModifierMaxLevel() { return config.getInt("Skills.Smelting.FuelEfficiency_MaxBonusLevel", 1000); }
    public double getBurnTimeMultiplier() { return config.getDouble("Skills.Smelting.FuelEfficiency_Multiplier", 3.0D); }

    public int getSecondSmeltMaxLevel() { return config.getInt("Skills.Smelting.SecondSmelt_MaxBonusLevel", 1000); }
    public double getSecondSmeltMaxChance() { return config.getDouble("Skills.Smelting.SecondSmelt_MaxBonusChance", 100.0D); }

    public int getFluxMiningUnlockLevel() { return config.getInt("Skills.Smelting.FluxMining_UnlockLevel", 250); }
    public double getFluxMiningChance() { return config.getDouble("Skills.Smelting.FluxMining_Chance", 33.0D); }

    public int getSmeltingVanillaXPBoostRank1Level() { return config.getInt("Skills.Smelting.VanillaXPBoost_Rank1Level", 100); }
    public int getSmeltingVanillaXPBoostRank2Level() { return config.getInt("Skills.Smelting.VanillaXPBoost_Rank2Level", 300); }
    public int getSmeltingVanillaXPBoostRank3Level() { return config.getInt("Skills.Smelting.VanillaXPBoost_Rank3Level", 500); }
    public int getSmeltingVanillaXPBoostRank4Level() { return config.getInt("Skills.Smelting.VanillaXPBoost_Rank4Level", 700); }
    public int getSmeltingVanillaXPBoostRank5Level() { return config.getInt("Skills.Smelting.VanillaXPBoost_Rank5Level", 900); }

    public int getSmeltingVanillaXPBoostRank1Multiplier() { return config.getInt("Skills.Smelting.VanillaXPBoost_Rank1Multiplier", 2); }
    public int getSmeltingVanillaXPBoostRank2Multiplier() { return config.getInt("Skills.Smelting.VanillaXPBoost_Rank2Multiplier", 3); }
    public int getSmeltingVanillaXPBoostRank3Multiplier() { return config.getInt("Skills.Smelting.VanillaXPBoost_Rank3Multiplier", 4); }
    public int getSmeltingVanillaXPBoostRank4Multiplier() { return config.getInt("Skills.Smelting.VanillaXPBoost_Rank4Multiplier", 5); }
    public int getSmeltingVanillaXPBoostRank5Multiplier() { return config.getInt("Skills.Smelting.VanillaXPBoost_Rank5Multiplier", 6); }

    /* SWORDS */
    public double getBleedChanceMax() { return config.getDouble("Skills.Swords.Bleed_ChanceMax", 75.0D); }
    public int getBleedMaxBonusLevel() { return config.getInt("Skills.Swords.Bleed_MaxBonusLevel", 750); }
    public int getBleedMaxTicks() { return config.getInt("Skills.Swords.Bleed_MaxTicks", 3); }
    public int getBleedBaseTicks() { return config.getInt("Skills.Swords.Bleed_BaseTicks", 2); }

    public double getCounterChanceMax() { return config.getDouble("Skills.Swords.Counter_ChanceMax", 30.0D); }
    public int getCounterMaxBonusLevel() { return config.getInt("Skills.Swords.Counter_MaxBonusLevel", 600); }
    public int getCounterModifier() { return config.getInt("Skills.Swords.Counter_DamageModifier", 2); }

    public int getSerratedStrikesModifier() { return config.getInt("Skills.Swords.SerratedStrikes_DamageModifier", 4); }
    public int getSerratedStrikesTicks() { return config.getInt("Skills.Swords.SerratedStrikes_BleedTicks", 5); }

    /* TAMING */
    public double getGoreChanceMax() { return config.getInt("Skills.Taming.Gore_ChanceMax", 100); }
    public int getGoreMaxBonusLevel() { return config.getInt("Skills.Taming.Gore_MaxBonusLevel", 1000); }
    public int getGoreBleedTicks() { return config.getInt("Skills.Taming.Gore_BleedTicks", 2); }
    public int getGoreModifier() { return config.getInt("Skills.Taming.Gore_Modifier", 2); }

    public int getFastFoodUnlock() { return config.getInt("Skills.Taming.FastFood_UnlockLevel", 50); }
    public double getFastFoodChance() { return config.getInt("Skills.Taming.FastFood_Chance", 50); }

    public int getEnviromentallyAwareUnlock() { return config.getInt("Skills.Taming.EnvironmentallyAware_UnlockLevel", 100); }

    public int getThickFurUnlock() { return config.getInt("Skills.Taming.ThickFur_UnlockLevel", 250); }
    public int getThickFurModifier() { return config.getInt("Skills.Taming.ThickFur_Modifier", 2); }

    public int getHolyHoundUnlock() {return config.getInt("Skills.Taming.HolyHound_UnlockLevel", 375); }

    public int getShockProofUnlock() { return config.getInt("Skills.Taming.ShockProof_UnlockLevel", 500); }
    public int getShockProofModifier() { return config.getInt("Skills.Taming.ShockProof_Modifier", 6); }

    public int getSharpenedClawsUnlock() { return config.getInt("Skills.Taming.SharpenedClaws_UnlockLevel", 750); }
    public int getSharpenedClawsBonus() { return config.getInt("Skills.Taming.SharpenedClaws_Bonus", 2); }

    /* UNARMED */
    public double getDisarmChanceMax() { return config.getDouble("Skills.Unarmed.Disarm_ChanceMax", 33.0D); }
    public int getDisarmMaxBonusLevel() { return config.getInt("Skills.Unarmed.Disarm_MaxBonusLevel", 1000); }

    public double getDeflectChanceMax() { return config.getDouble("Skills.Unarmed.Deflect_ChanceMax", 50.0D); }
    public int getDeflectMaxBonusLevel() { return config.getInt("Skills.Unarmed.Deflect_MaxBonusLevel", 1000); }

    public double getIronGripChanceMax() { return config.getDouble("Skills.Unarmed.IronGrip_ChanceMax", 100.0D); }
    public int getIronGripMaxBonusLevel() { return config.getInt("Skills.Unarmed.IronGrip_MaxBonusLevel", 1000); }

    public int getIronArmMaxBonus() { return config.getInt("Skills.Unarmed.IronArm_BonusMax", 8); }
    public int getIronArmIncreaseLevel() { return config.getInt("Skills.Unarmed.IronArm_IncreaseLevel", 50); }

    /* WOODCUTTING */
    public int getLeafBlowUnlockLevel() { return config.getInt("Skills.Woodcutting.LeafBlower_UnlockLevel", 100); }

    public double getWoodcuttingDoubleDropChance() { return config.getDouble("Skills.Woodcutting.DoubleDrops_ChanceMax", 100.0D); }
    public int getWoodcuttingDoubleDropMaxLevel() { return config.getInt("Skills.Woodcutting.DoubleDrops_MaxBonusLevel", 1000); }

    /* KRAKEN STUFF */
    public boolean getKrakenEnabled() { return config.getBoolean("Kraken.Enabled", true); }
    public boolean getKrakenGlobalSoundsEnabled() { return config.getBoolean("Kraken.Global_Sounds", true); }
    public boolean getKrakenEscapeAllowed() { return config.getBoolean("Kraken.Allow_Escaping", false); }
    public int getKrakenTriesBeforeRelease() { return config.getInt("Kraken.Tries_Before_Release", 50); }
    public int getKrakenHealth() { return config.getInt("Kraken.Health", 50); }
    public String getKrakenName() { return config.getString("Kraken.Name", "The Kraken"); }
    public String getServerUnleashMessage() { return config.getString("Kraken.Unleashed_Message.Server", "(PLAYER) has unleashed the kraken!"); }
    public String getPlayerUnleashMessage() { return config.getString("Kraken.Unleashed_Message.Player", "THE KRAKEN HAS BEEN UNLEASHED!"); }
    public String getPlayerDefeatMessage() { return config.getString("Kraken.Defeated_Message.Killed", "You have slain the kraken!"); }
    public String getPlayerEscapeMessage() { return config.getString("Kraken.Defeated_Message.Escape", "You have escaped from the kraken!"); }
    public int getKrakenAttackInterval() { return config.getInt("Kraken.Attack_Interval_Seconds", 1); }
    public int getKrakenAttackDamage() { return config.getInt("Kraken.Attack_Damage", 1); }

    private boolean noErrorsInConfig(List<String> issues) {//TODO move this to misc
        for (String issue : issues) {
            plugin.getLogger().warning(issue);
        }

        return issues.isEmpty();
    }
}
