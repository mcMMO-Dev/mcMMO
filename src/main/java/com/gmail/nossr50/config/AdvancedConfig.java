package com.gmail.nossr50.config;

public class AdvancedConfig extends ConfigLoader {
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
        // TODO Do we need to use this?
    }
    /* GENERAL */
    public int getAbilityLength() { return config.getInt("Skills.General.Ability_IncreaseLevel", 50); }

    /* ACROBATICS */
    public double getDodgeChanceMax() { return config.getDouble("Skills.Acrobatics.Dodge_ChanceMax", 20.0D); }
    public int getDodgeMaxBonusLevel() { return config.getInt("Skills.Acrobatics.Dodge_MaxBonusLevel", 800); }

    public double getRollChanceMax() { return config.getDouble("Skills.Acrobatics.Roll_ChanceMax", 100.0D); }
    public int getRollMaxBonusLevel() { return config.getInt("Skills.Acrobatics.Roll_MaxBonusLevel", 1000); }

    public double getGracefulRollChanceMax() { return config.getDouble("Skills.Acrobatics.GracefulRoll_ChanceMax", 100.0D); }
    public int getGracefulRollMaxBonusLevel() { return config.getInt("Skills.Acrobatics.GracefulRoll_MaxBonusLevel", 500); }

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
    public double getArmorImpactMaxDurabilityDamage() { return config.getDouble("Skills.Axes.ArmorImpact_MaxPercentageDurabilityDamage", 20.0D); }

    /* EXCAVATION */
    //Nothing to configure, everything is already configurable in config.yml

    /* FISHING */
    public int getFishingTierLevelsTier1() { return config.getInt("Skills.Fishing.Tier_Levels.Tier1", 0); }
    public int getFishingTierLevelsTier2() { return config.getInt("Skills.Fishing.Tier_Levels.Tier2", 200); }
    public int getFishingTierLevelsTier3() { return config.getInt("Skills.Fishing.Tier_Levels.Tier3", 400); }
    public int getFishingTierLevelsTier4() { return config.getInt("Skills.Fishing.Tier_Levels.Tier4", 600); }
    public int getFishingTierLevelsTier5() { return config.getInt("Skills.Fishing.Tier_Levels.Tier5", 800); }

    public int getFishingMagicMultiplier() { return config.getInt("Skills.Fishing.MagicHunter_Multiplier", 5); }

    /* Shake */
    public int getShakeUnlockLevel() { return config.getInt("Skills.Fishing.Shake_UnlockLevel", 150); }
    public int getShakeChanceRank1() { return config.getInt("Skills.Fishing.Shake_Chance.Rank_1", 25); }
    public int getShakeChanceRank2() { return config.getInt("Skills.Fishing.Shake_Chance.Rank_2", 40); }
    public int getShakeChanceRank3() { return config.getInt("Skills.Fishing.Shake_Chance.Rank_3", 55); }
    public int getShakeChanceRank4() { return config.getInt("Skills.Fishing.Shake_Chance.Rank_4", 60); }
    public int getShakeChanceRank5() { return config.getInt("Skills.Fishing.Shake_Chance.Rank_5", 75); }

    /* HERBALISM */
    public int getFarmerDietRankChange() { return config.getInt("Skills.Herbalism.Food_RankChange", 200); }

    public int getGreenThumbStageChange() { return config.getInt("Skills.Herbalism.GreenThumb_StageChange", 200); }
    public int getGreenThumbChanceMax() { return config.getInt("Skills.Herbalism.GreenThumb_ChanceMax", 100); }
    public int getGreenThumbMaxLevel() { return config.getInt("Skills.Herbalism.GreenThumb_MaxBonusLevel", 1500); }

    public int getHerbalismDoubleDropsChanceMax() { return config.getInt("Skills.Herbalism.DoubleDrops_ChanceMax", 100); }
    public int getHerbalismDoubleDropsMaxLevel() { return config.getInt("Skills.Herbalism.DoubleDrops_MaxBonusLevel", 1000); }

    /* MINING */
    public int getMiningDoubleDropChance() { return config.getInt("Skills.Mining.DoubleDrops_ChanceMax", 100); }
    public int getMiningDoubleDropMaxLevel() { return config.getInt("Skills.Mining.DoubleDrops_MaxBonusLevel", 1000); }

    public int getBlastMiningRank1() { return config.getInt("Skills.Mining.BlastMining_Rank1", 125); }
    public int getBlastMiningRank2() { return config.getInt("Skills.Mining.BlastMining_Rank2", 250); }
    public int getBlastMiningRank3() { return config.getInt("Skills.Mining.BlastMining_Rank3", 375); }
    public int getBlastMiningRank4() { return config.getInt("Skills.Mining.BlastMining_Rank4", 500); }
    public int getBlastMiningRank5() { return config.getInt("Skills.Mining.BlastMining_Rank5", 625); }
    public int getBlastMiningRank6() { return config.getInt("Skills.Mining.BlastMining_Rank6", 750); }
    public int getBlastMiningRank7() { return config.getInt("Skills.Mining.BlastMining_Rank7", 875); }
    public int getBlastMiningRank8() { return config.getInt("Skills.Mining.BlastMining_Rank8", 1000); }

    /* REPAIR */
    public int getRepairMasteryMaxBonus() { return config.getInt("Skills.Repair.RepairMastery_MaxBonusPercentage", 200); }
    public int getRepairMasteryMaxLevel() { return config.getInt("Skills.Repair.RepairMastery_MaxBonusLevel", 1000); }
    public int getSuperRepairChanceMax() { return config.getInt("Skills.Repair.SuperRepair_ChanceMax", 100); }
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

    /* SWORDS */
    public int getBleedChanceMax() { return config.getInt("Skills.Swords.Bleed_ChanceMax", 75); }
    public int getBleedMaxBonusLevel() { return config.getInt("Skills.Swords.Bleed_MaxBonusLevel", 750); }
    public int getBleedMaxTicks() { return config.getInt("Skills.Swords.Bleed_MaxTicks", 3); }
    public int getBleedBaseTicks() { return config.getInt("Skills.Swords.Bleed_BaseTicks", 2); }

    public int getCounterChanceMax() { return config.getInt("Skills.Swords.Counter_ChanceMax", 30); }
    public int getCounterMaxBonusLevel() { return config.getInt("Skills.Swords.Counter_MaxBonusLevel", 600); }
    public int getCounterModifier() { return config.getInt("Skills.Swords.Counter_DamageModifier", 2); }

    public int getSerratedStrikesModifier() { return config.getInt("Skills.Swords.SerratedStrikes_DamageModifier", 4); }
    public int getSerratedStrikesTicks() { return config.getInt("Skills.Swords.SerratedStrikes_BleedTicks", 5); }

    /* TAMING */
    public int getGoreChanceMax() { return config.getInt("Skills.Taming.Gore_ChanceMax", 100); }
    public int getGoreMaxBonusLevel() { return config.getInt("Skills.Taming.Gore_MaxBonusLevel", 1000); }
    public int getGoreBleedTicks() { return config.getInt("Skills.Taming.Gore_BleedTicks", 2); }
    public int getGoreModifier() { return config.getInt("Skills.Taming.Gore_Modifier", 2); }

    public int getFastFoodUnlock() { return config.getInt("Skills.Taming.FastFood_UnlockLevel", 50); }
    public int getFastFoodChance() { return config.getInt("Skills.Taming.FastFood_Chance", 50); }

    public int getEnviromentallyAwareUnlock() { return config.getInt("Skills.Taming.EnvironmentallyAware_UnlockLevel", 100); }

    public int getThickFurUnlock() { return config.getInt("Skills.Taming.ThickFur_UnlockLevel", 250); }
    public int getThickFurModifier() { return config.getInt("Skills.Taming.ThickFur_Modifier", 2); }

    public int getShockProofUnlock() { return config.getInt("Skills.Taming.ShockProof_UnlockLevel", 500); }
    public int getShockProofModifier() { return config.getInt("Skills.Taming.ShockProof_Modifier", 6); }

    public int getSharpenedClawsUnlock() { return config.getInt("Skills.Taming.SharpenedClaws_UnlockLevel", 750); }
    public int getSharpenedClawsBonus() { return config.getInt("Skills.Taming.SharpenedClaws_Bonus", 2); }

    /* UNARMED */
    public int getDisarmChanceMax() { return config.getInt("Skills.Unarmed.Disarm_ChanceMax", 33); }
    public int getDisarmMaxBonusLevel() { return config.getInt("Skills.Unarmed.Disarm_MaxBonusLevel", 1000); }
    public int getDeflectChanceMax() { return config.getInt("Skills.Unarmed.Deflect_ChanceMax", 50); }
    public int getDeflectMaxBonusLevel() { return config.getInt("Skills.Unarmed.Deflect_MaxBonusLevel", 1000); }
    public int getIronGripChanceMax() { return config.getInt("Skills.Unarmed.IronGrip_ChanceMax", 100); }
    public int getIronGripMaxBonusLevel() { return config.getInt("Skills.Unarmed.IronGrip_MaxBonusLevel", 1000); }
    public int getIronArmBonus() { return config.getInt("Skills.Unarmed.IronArm_BonusMax", 8); }
    public int getIronArmIncreaseLevel() { return config.getInt("Skills.Unarmed.IronArm_IncreaseLevel", 50); }

    /* WOODCUTTING */
    public int getLeafBlowUnlockLevel() { return config.getInt("Skills.Woodcutting.LeafBlower_UnlockLevel", 100); }
    public int getWoodcuttingDoubleDropChance() { return config.getInt("Skills.Woodcutting.DoubleDrops_ChanceMax", 100); }
    public int getWoodcuttingDoubleDropMaxLevel() { return config.getInt("Skills.Woodcutting.DoubleDrops_MaxBonusLevel", 1000); }

    /* SPOUT STUFF*/
    public int getSpoutNotificationTier1() { return config.getInt("Spout.Notifications.Tier1", 200); }
    public int getSpoutNotificationTier2() { return config.getInt("Spout.Notifications.Tier2", 400); }
    public int getSpoutNotificationTier3() { return config.getInt("Spout.Notifications.Tier3", 600); }
    public int getSpoutNotificationTier4() { return config.getInt("Spout.Notifications.Tier4", 800); }
}
