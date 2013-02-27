package com.gmail.nossr50.config;

public class AdvancedConfig extends AutoUpdateConfigLoader {
    private static AdvancedConfig instance;

    private AdvancedConfig() {
        super("advanced.yml");
    }

    public static AdvancedConfig getInstance() {
        if (instance == null) {
            instance = new AdvancedConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {}

    /* GENERAL */
    public int getAbilityLength() { return config.getInt("Skills.General.Ability_IncreaseLevel", 50); }

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
    public int getGracefulRollSuccessModifer() { return config.getInt("Skills.Acrobatics.GracefulRoll_SuccessModifier", 2); }

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

    /* SPOUT STUFF*/
    public int getSpoutNotificationTier1() { return config.getInt("Spout.Notifications.Tier1", 200); }
    public int getSpoutNotificationTier2() { return config.getInt("Spout.Notifications.Tier2", 400); }
    public int getSpoutNotificationTier3() { return config.getInt("Spout.Notifications.Tier3", 600); }
    public int getSpoutNotificationTier4() { return config.getInt("Spout.Notifications.Tier4", 800); }
}
