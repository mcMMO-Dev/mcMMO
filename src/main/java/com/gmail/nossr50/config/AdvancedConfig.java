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
        // TODO Do I need to use this?
    }
    /* GENERAL */
    public int getAbilityLength() { return config.getInt("Skills.General.Ability_IncreaseLevel", 50); }
    
    /* ACROBATICS */
    public int getDodgeChanceMax() { return config.getInt("Skills.Acrobatics.Dodge_ChanceMax", 20); }
    public int getDodgeMaxBonusLevel() { return config.getInt("Skills.Acrobatics.Dodge_MaxBonusLevel", 800); }
    
    public int getRollChanceMax() { return config.getInt("Skills.Acrobatics.Roll_ChanceMax", 100); }
    public int getRollMaxBonusLevel() { return config.getInt("Skills.Acrobatics.Roll_MaxBonusLevel", 1000); }
    
    public int getGracefulRollChanceMax() { return config.getInt("Skills.Acrobatics.GracefulRoll_ChanceMax", 100); }
    public int getGracefulRollMaxBonusLevel() { return config.getInt("Skills.Acrobatics.GracefulRoll_MaxBonusLevel", 500); }

    public int getDodgeXPModifier() { return config.getInt("Skills.Acrobatics.Dodge_XP_Modifier", 120); }
    public int getRollXPModifier() { return config.getInt("Skills.Acrobatics.Roll_XP_Modifier", 80); }
    public int getFallXPModifier() { return config.getInt("Skills.Acrobatics.Fall_XP_Modifier", 120); }
    
    /* ARCHERY */
    public int getSkillShotIncreaseLevel() { return config.getInt("Skills.Archery.SkillShot_IncreaseLevel", 50); }
    public double getSkillShotIncreasePercentage() { return config.getDouble("Skills.Archery.SkillShot_IncreasePercentage", 0.1D); }
    public double getSkillShotBonusMax() { return config.getDouble("Skills.Archery.SkillShot_MaxBonus", 2.0D); }

    public int getDazeBonusMax() { return config.getInt("Skills.Archery.Daze_MaxChance", 50); }
    public int getDazeMaxBonusLevel() { return config.getInt("Skills.Archery.Daze_MaxBonusLevel", 1000); }
    public int getDazeModifier() { return config.getInt("Skills.Archery.Daze_BonusDamage", 4); }

    public int getRetrieveBonusMax() { return config.getInt("Skills.Archery.Retrieve_MaxBonus", 100); }
    public int getRetrieveMaxBonusLevel() { return config.getInt("Skills.Archery.Retrieve_MaxBonusLevel", 1000); }
    
    /* AXES */
    public int getBonusDamageAxesBonusMax() { return config.getInt("Skills.Axes.DamageIncrease_MaxBonus", 4); }
    public int getBonusDamageAxesMaxBonusLevel() { return config.getInt("Skills.Axes.DamageIncrease_MaxBonusLevel", 200); }

    public double getAxesCriticalChance() { return config.getDouble("Skills.Axes.AxesCritical_MaxChance", 37.50); }
    public int getAxesCriticalMaxBonusLevel() { return config.getInt("Skills.Axes.AxesCritical_MaxBonusLevel", 750); }
    public double getAxesCriticalPVPModifier() { return config.getDouble("Skills.Axes.AxesCritical_PVP_Modifier", 1.5); }
    public int getAxesCriticalPVEModifier() { return config.getInt("Skills.Axes.AxesCritical_PVE_Modifier", 2); }
    
    public int getGreaterImpactChance() { return config.getInt("Skills.Axes.GreaterImpact_Chance", 25); }
    public double getGreaterImpactModifier() { return config.getDouble("Skills.Axes.GreaterImpact_KnockbackModifier", 1.5); }
    public int getGreaterImpactBonusDamage() { return config.getInt("Skills.Axes.GreaterImpact_BonusDamage", 2); }

    public int getArmorImpactIncreaseLevel() { return config.getInt("Skills.Axes.ArmorImpact_IncreaseLevel", 50); }
    
    /* EXCAVATION */
    //Nothing to configure, everything is already configurable in config.yml
    
    /* FISHING */
    public int getShakeUnlockLevel() { return config.getInt("Skills.Fishing.Shake_UnlockLevel", 150); }
    public int getFishingEnchantmentChance() { return config.getInt("Skills.Fishing.Enchantment_Chance", 10); }
    
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
    public int getRepairMasteryChanceMax() { return config.getInt("Skills.Repair.RepairMastery_ChanceMax", 200); }
    public int getRepairMasteryMaxLevel() { return config.getInt("Skills.Repair.RepairMastery_MaxBonusLevel", 1000); }
    public int getSuperRepairChanceMax() { return config.getInt("Skills.Repair.SuperRepair_ChanceMax", 100); }
    public int getSuperRepairMaxLevel() { return config.getInt("Skills.Repair.SuperRepair_MaxBonusLevel", 1000); }
    
    /* SWORDS */
    public int getBleedChanceMax() { return config.getInt("Skills.Sword.Bleed_ChanceMax", 75); }
    public int getBleedMaxBonusLevel() { return config.getInt("Skills.Sword.Bleed_MaxBonusLevel", 750); }
    public int getBleedMaxTicks() { return config.getInt("Skills.Sword.Bleed_MaxTicks", 3); }
    public int getBleedBaseTicks() { return config.getInt("Skills.Sword.Bleed_BaseTicks", 2); }

    public int getCounterChanceMax() { return config.getInt("Skills.Sword.Counter_ChanceMax", 30); }
    public int getCounterMaxBonusLevel() { return config.getInt("Skills.Sword.Counter_MaxBonusLevel", 600); }
    public int getCounterModifier() { return config.getInt("Skills.Sword.Counter_DamageModifier", 2); }
    
    public int getSerratedStrikesModifier() { return config.getInt("Skills.Sword.SerratedStrikes_DamageModifier", 4); }
    public int getSerratedStrikesTicks() { return config.getInt("Skills.Sword.SerratedStrikes_BleedTicks", 5); }
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
    public int getIronArmBonus() { return config.getInt("Skills.Unarmed.IronArm_Bonus", 8); }
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
    //TODO Make the sounds configurable! :D
//    public String getSpoutSoundRepair() { return config.getString("Spout.Sounds.RepairSound", "url here"); }
//    public String getSpoutSoundLevelUp() { return config.getString("Spout.Sounds.LevelUp", "url here"); }
}
