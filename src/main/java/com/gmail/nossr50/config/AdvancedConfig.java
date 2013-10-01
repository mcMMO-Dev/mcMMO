package com.gmail.nossr50.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gmail.nossr50.skills.fishing.Fishing;
import com.gmail.nossr50.skills.mining.BlastMining;
import com.gmail.nossr50.skills.repair.ArcaneForging;
import com.gmail.nossr50.skills.smelting.Smelting;

public class AdvancedConfig extends AutoUpdateConfigLoader {
    private static AdvancedConfig instance;

    private AdvancedConfig() {
        super("advanced.yml");
        validate();
    }

    public static AdvancedConfig getInstance() {
        if (instance == null) {
            instance = new AdvancedConfig();
        }

        return instance;
    }

    @Override
    protected boolean validateKeys() {
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
            reason.add("Skills.Acrobatics.Dodge_DamageModifier should be greater than 1!");
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
        List<Fishing.Tier> fishingTierList = Arrays.asList(Fishing.Tier.values());

        for (Fishing.Tier tier : fishingTierList) {
            if (getFishingTierLevel(tier) < 0) {
                reason.add("Skills.Fishing.Tier_Levels.Tier" + tier.toNumerical() + " should be at least 0!");
            }

            if (getShakeChance(tier) < 0) {
                reason.add("Skills.Fishing.Shake_Chance.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getFishingVanillaXPModifier(tier) < 0) {
                reason.add("Skills.Fishing.VanillaXPBoost.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (tier != Fishing.Tier.EIGHT) {
                Fishing.Tier nextTier = fishingTierList.get(fishingTierList.indexOf(tier) - 1);

                if (getFishingTierLevel(tier) >= getFishingTierLevel(nextTier)) {
                    reason.add("Skills.Fishing.Tier_Levels.Tier" + tier.toNumerical() + " should be less than Skills.Fishing.Tier_Levels.Tier" + nextTier.toNumerical() + "!");
                }

                if (getShakeChance(tier) > getShakeChance(nextTier)) {
                    reason.add("Skills.Fishing.Shake_Chance.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Fishing.Shake_Chance.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getFishingVanillaXPModifier(tier) > getFishingVanillaXPModifier(nextTier)) {
                    reason.add("Skills.Fishing.VanillaXPBoost.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Fishing.VanillaXPBoost.Rank_" + nextTier.toNumerical() + "!");
                }
            }
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

        List<BlastMining.Tier> blastMiningTierList = Arrays.asList(BlastMining.Tier.values());

        for (BlastMining.Tier tier : blastMiningTierList) {
            if (getBlastMiningRankLevel(tier) < 0) {
                reason.add("Skills.Mining.BlastMining_Rank" + tier.toNumerical() + " should be at least 0!");
            }

            if (getBlastDamageDecrease(tier) < 0) {
                reason.add("Skills.Mining.BlastDamageDecrease_Rank" + tier.toNumerical() + " should be at least 0!");
            }

            if (getOreBonus(tier) < 0) {
                reason.add("Skills.Mining.OreBonus_Rank" + tier.toNumerical() + " should be at least 0!");
            }

            if (getDebrisReduction(tier) < 0) {
                reason.add("Skills.Mining.DebrisReduction_Rank" + tier.toNumerical() + " should be at least 0!");
            }

            if (getDropMultiplier(tier) < 0) {
                reason.add("Skills.Mining.DropMultiplier_Rank" + tier.toNumerical() + " should be at least 0!");
            }

            if (getBlastRadiusModifier(tier) < 0) {
                reason.add("Skills.Mining.BlastRadiusModifier_Rank" + tier.toNumerical() + " should be at least 0!");
            }

            if (tier != BlastMining.Tier.EIGHT) {
                BlastMining.Tier nextTier = blastMiningTierList.get(blastMiningTierList.indexOf(tier) - 1);

                if (getBlastMiningRankLevel(tier) >= getBlastMiningRankLevel(nextTier)) {
                    reason.add("Skills.Mining.BlastMining_Rank" + tier.toNumerical() + " should be less than Skills.Mining.BlastMining_Rank" + nextTier.toNumerical() + "!");
                }

                if (getBlastDamageDecrease(tier) > getBlastDamageDecrease(nextTier)) {
                    reason.add("Skills.Mining.BlastDamageDecrease_Rank" + tier.toNumerical() + " should be less than or equal to Skills.Mining.BlastDamageDecrease_Rank" + nextTier.toNumerical() + "!");
                }

                if (getOreBonus(tier) > getOreBonus(nextTier)) {
                    reason.add("Skills.Mining.OreBonus_Rank" + tier.toNumerical() + " should be less than or equal to Skills.Mining.OreBonus_Rank" + nextTier.toNumerical() + "!");
                }

                if (getDebrisReduction(tier) > getDebrisReduction(nextTier)) {
                    reason.add("Skills.Mining.DebrisReduction_Rank" + tier.toNumerical() + " should be less than or equal to Skills.Mining.DebrisReduction_Rank" + nextTier.toNumerical() + "!");
                }

                if (getDropMultiplier(tier) > getDropMultiplier(nextTier)) {
                    reason.add("Skills.Mining.DropMultiplier_Rank" + tier.toNumerical() + " should be less than or equal to Skills.Mining.DropMultiplier_Rank" + nextTier.toNumerical() + "!");
                }

                if (getBlastRadiusModifier(tier) > getBlastRadiusModifier(nextTier)) {
                    reason.add("Skills.Mining.BlastRadiusModifier_Rank" + tier.toNumerical() + " should be less than or equal to Skills.Mining.BlastRadiusModifier_Rank" + nextTier.toNumerical() + "!");
                }
            }
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

        List<ArcaneForging.Tier> arcaneForgingTierList = Arrays.asList(ArcaneForging.Tier.values());

        for (ArcaneForging.Tier tier : arcaneForgingTierList) {
            if (getArcaneForgingRankLevel(tier) < 0) {
                reason.add("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getArcaneForgingDowngradeChance(tier) < 0 || getArcaneForgingDowngradeChance(tier) > 100) {
                reason.add("Skills.Repair.Arcane_Forging.Downgrades.Chance.Rank_" + tier.toNumerical() + " only accepts values from 0 to 100!");
            }

            if (getArcaneForgingKeepEnchantsChance(tier) < 0 || getArcaneForgingKeepEnchantsChance(tier) > 100) {
                reason.add("Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_" + tier.toNumerical() + " only accepts values from 0 to 100!");
            }

            if (tier != ArcaneForging.Tier.EIGHT) {
                ArcaneForging.Tier nextTier = arcaneForgingTierList.get(arcaneForgingTierList.indexOf(tier) - 1);

                if (getArcaneForgingRankLevel(tier) >= getArcaneForgingRankLevel(nextTier)) {
                    reason.add("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_" + tier.toNumerical() + " should be less than Skills.Repair.Arcane_Forging.Rank_Levels.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getArcaneForgingDowngradeChance(nextTier) > getArcaneForgingDowngradeChance(tier)) {
                    reason.add("Skills.Repair.ArcaneForging.Downgrades.Chance.Rank_" + nextTier.toNumerical() + " should be less than or equal to Skills.Repair.ArcaneForging.Downgrades.Chance.Rank_" + tier.toNumerical() + "!");
                }

                if (getArcaneForgingKeepEnchantsChance(tier) > getArcaneForgingKeepEnchantsChance(nextTier)) {
                    reason.add("Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Repair.Arcane_Forging.Keep_Enchants.Chance.Rank_" + nextTier.toNumerical() + "!");
                }
            }
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

        List<Smelting.Tier> smeltingTierList = Arrays.asList(Smelting.Tier.values());

        for (Smelting.Tier tier : smeltingTierList) {
            if (getSmeltingVanillaXPBoostRankLevel(tier) < 0) {
                reason.add("Skills.Smelting.VanillaXPBoost.Rank_Levels.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getSmeltingVanillaXPBoostMultiplier(tier) < 1) {
                reason.add("Skills.Smelting.VanillaXPBoost.XP_Multiplier.Rank_" + tier.toNumerical() + " should be at least 1!");
            }

            if (tier != Smelting.Tier.EIGHT) {
                Smelting.Tier nextTier = smeltingTierList.get(smeltingTierList.indexOf(tier) - 1);

                if (getSmeltingVanillaXPBoostRankLevel(tier) >= getSmeltingVanillaXPBoostRankLevel(nextTier)) {
                    reason.add("Skills.Smelting.VanillaXPBoost.Rank_Levels.Rank_" + tier.toNumerical() + " should be less than Skills.Smelting.VanillaXPBoost.Rank_Levels.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getSmeltingVanillaXPBoostMultiplier(tier) > getSmeltingVanillaXPBoostRankLevel(nextTier)) {
                    reason.add("Skills.Smelting.VanillaXPBoost.XP_Multiplier.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Smelting.VanillaXPBoost.XP_Multiplier.Rank_" + nextTier.toNumerical() + "!");
                }
            }
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

        if (getIronArmMinBonus() < 0) {
            reason.add("Skills.Unarmed.IronArm_BonusMin should be at least 0!");
        }

        if (getIronArmMaxBonus() < 0) {
            reason.add("Skills.Unarmed.IronArm_BonusMax should be at least 0!");
        }

        if (getIronArmMaxBonus() < getIronArmMinBonus()) {
            reason.add("Skills.Unarmed.IronArm_BonusMax should be greater than or equal to Skills.Unarmed.IronArm_BonusMin!");
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

        return noErrorsInConfig(reason);
    }

    @Override
    protected void loadKeys() {}

    /* GENERAL */
    public int getAbilityLength() { return config.getInt("Skills.General.Ability_IncreaseLevel", 50); }
    public int getEnchantBuff() { return config.getInt("Skills.General.Ability_EnchantBuff", 5); }

    /* ACROBATICS */
    public double getDodgeChanceMax() { return config.getDouble("Skills.Acrobatics.Dodge_ChanceMax", 20.0D); }
    public int getDodgeMaxBonusLevel() { return config.getInt("Skills.Acrobatics.Dodge_MaxBonusLevel", 800); }
    public double getDodgeDamageModifier() { return config.getDouble("Skills.Acrobatics.Dodge_DamageModifier", 2.0D); }

    public double getRollChanceMax() { return config.getDouble("Skills.Acrobatics.Roll_ChanceMax", 100.0D); }
    public int getRollMaxBonusLevel() { return config.getInt("Skills.Acrobatics.Roll_MaxBonusLevel", 1000); }
    public double getRollDamageThreshold() { return config.getDouble("Skills.Acrobatics.Roll_DamageThreshold", 7.0D); }

    public double getGracefulRollChanceMax() { return config.getDouble("Skills.Acrobatics.GracefulRoll_ChanceMax", 100.0D); }
    public int getGracefulRollMaxBonusLevel() { return config.getInt("Skills.Acrobatics.GracefulRoll_MaxBonusLevel", 500); }
    public double getGracefulRollDamageThreshold() { return config.getDouble("Skills.Acrobatics.GracefulRoll_DamageThreshold", 14.0D); }

    public int getDodgeXPModifier() { return config.getInt("Skills.Acrobatics.Dodge_XP_Modifier", 120); }
    public int getRollXPModifier() { return config.getInt("Skills.Acrobatics.Roll_XP_Modifier", 80); }
    public int getFallXPModifier() { return config.getInt("Skills.Acrobatics.Fall_XP_Modifier", 120); }

    public double getFeatherFallXPModifier() { return config.getDouble("Skills.Acrobatics.FeatherFall_Multiplier", 2.0); }

    /* ARCHERY */
    public int getSkillShotIncreaseLevel() { return config.getInt("Skills.Archery.SkillShot_IncreaseLevel", 50); }
    public double getSkillShotIncreasePercentage() { return config.getDouble("Skills.Archery.SkillShot_IncreasePercentage", 0.1D); }
    public double getSkillShotBonusMax() { return config.getDouble("Skills.Archery.SkillShot_MaxBonus", 2.0D); }
    public double getSkillShotDamageMax() { return config.getDouble("Skills.Archery.SkillShot_MaxDamage", 9.0D); }

    public double getDazeBonusMax() { return config.getDouble("Skills.Archery.Daze_MaxChance", 50.0D); }
    public int getDazeMaxBonusLevel() { return config.getInt("Skills.Archery.Daze_MaxBonusLevel", 1000); }
    public double getDazeModifier() { return config.getDouble("Skills.Archery.Daze_BonusDamage", 4.0D); }

    public double getRetrieveChanceMax() { return config.getDouble("Skills.Archery.Retrieve_MaxBonus", 100.0D); }
    public int getRetrieveMaxBonusLevel() { return config.getInt("Skills.Archery.Retrieve_MaxBonusLevel", 1000); }

    public double getForceMultiplier() { return config.getDouble("Skills.Archery.Force_Multiplier", 2.0D); }

    /* AXES */
    public double getBonusDamageAxesBonusMax() { return config.getDouble("Skills.Axes.DamageIncrease_MaxBonus", 4.0D); }
    public int getBonusDamageAxesMaxBonusLevel() { return config.getInt("Skills.Axes.DamageIncrease_MaxBonusLevel", 200); }

    public double getAxesCriticalChance() { return config.getDouble("Skills.Axes.AxesCritical_MaxChance", 37.50D); }
    public int getAxesCriticalMaxBonusLevel() { return config.getInt("Skills.Axes.AxesCritical_MaxBonusLevel", 750); }
    public double getAxesCriticalPVPModifier() { return config.getDouble("Skills.Axes.AxesCritical_PVP_Modifier", 1.5D); }
    public double getAxesCriticalPVEModifier() { return config.getDouble("Skills.Axes.AxesCritical_PVE_Modifier", 2.0D); }

    public double getGreaterImpactChance() { return config.getDouble("Skills.Axes.GreaterImpact_Chance", 25.0D); }
    public double getGreaterImpactModifier() { return config.getDouble("Skills.Axes.GreaterImpact_KnockbackModifier", 1.5D); }
    public double getGreaterImpactBonusDamage() { return config.getDouble("Skills.Axes.GreaterImpact_BonusDamage", 2.0D); }

    public int getArmorImpactIncreaseLevel() { return config.getInt("Skills.Axes.ArmorImpact_IncreaseLevel", 50); }
    public double getImpactChance() { return config.getDouble("Skills.Axes.ArmorImpact_Chance", 25.0D); }
    public double getArmorImpactMaxDurabilityDamage() { return config.getDouble("Skills.Axes.ArmorImpact_MaxPercentageDurabilityDamage", 20.0D); }

    public double getSkullSplitterModifier() { return config.getDouble("Skills.Axes.SkullSplitter_DamagerModifier", 2.0D); }

    /* EXCAVATION */
    //Nothing to configure, everything is already configurable in config.yml

    /* FISHING */
    public int getFishingTierLevel(Fishing.Tier tier) { return config.getInt("Skills.Fishing.Rank_Levels.Rank_" + tier.toNumerical()); }
    public double getShakeChance(Fishing.Tier tier) { return config.getDouble("Skills.Fishing.Shake_Chance.Rank_" + tier.toNumerical()); }
    public int getFishingVanillaXPModifier(Fishing.Tier tier) { return config.getInt("Skills.Fishing.VanillaXPBoost.Rank_" + tier.toNumerical()); }

    public double getFishingMagicMultiplier() { return config.getDouble("Skills.Fishing.MagicHunter_Multiplier", 5.0D); }

    public int getFishermanDietRankChange() { return config.getInt("Skills.Fishing.Fisherman_Diet_RankChange", 200); }

    public int getIceFishingUnlockLevel() { return config.getInt("Skills.Fishing.Ice_Fishing_UnlockLevel", 50); }

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

    public int getBlastMiningRankLevel(BlastMining.Tier tier) { return config.getInt("Skills.Mining.BlastMining_Rank" + tier.toNumerical()); }
    public double getBlastDamageDecrease(BlastMining.Tier tier) { return config.getDouble("Skills.Mining.BlastDamageDecrease_Rank" + tier.toNumerical()); }
    public double getOreBonus(BlastMining.Tier tier) { return config.getDouble("Skills.Mining.OreBonus_Rank" + tier.toNumerical()); }
    public double getDebrisReduction(BlastMining.Tier tier) { return config.getDouble("Skills.Mining.DebrisReduction_Rank" + tier.toNumerical()); }
    public int getDropMultiplier(BlastMining.Tier tier) { return config.getInt("Skills.Mining.DropMultiplier_Rank" + tier.toNumerical()); }
    public double getBlastRadiusModifier(BlastMining.Tier tier) { return config.getDouble("Skills.Mining.BlastRadiusModifier_Rank" + tier.toNumerical()); }

    /* REPAIR */
    public double getRepairMasteryMaxBonus() { return config.getDouble("Skills.Repair.RepairMastery_MaxBonusPercentage", 200.0D); }
    public int getRepairMasteryMaxLevel() { return config.getInt("Skills.Repair.RepairMastery_MaxBonusLevel", 1000); }
    public double getSuperRepairChanceMax() { return config.getDouble("Skills.Repair.SuperRepair_ChanceMax", 100.0D); }
    public int getSuperRepairMaxLevel() { return config.getInt("Skills.Repair.SuperRepair_MaxBonusLevel", 1000); }
    public int getSalvageUnlockLevel() { return config.getInt("Skills.Repair.Salvage_UnlockLevel", 600); }

    /* Arcane Forging */
    public int getArcaneForgingRankLevel(ArcaneForging.Tier tier) { return config.getInt("Skills.Repair.Arcane_Forging.Rank_Levels.Rank_" + tier.toNumerical()); }

    public boolean getArcaneForgingEnchantLossEnabled() { return config.getBoolean("Skills.Repair.Arcane_Forging.May_Lose_Enchants", true); }
    public double getArcaneForgingKeepEnchantsChance(ArcaneForging.Tier tier) { return config.getDouble("Skills.Repair.Arcane_Forging.Keep_Enchants_Chance.Rank_" + tier.toNumerical()); }

    public boolean getArcaneForgingDowngradeEnabled() { return config.getBoolean("Skills.Repair.Arcane_Forging.Downgrades_Enabled", true); }
    public double getArcaneForgingDowngradeChance(ArcaneForging.Tier tier) { return config.getDouble("Skills.Repair.Arcane_Forging.Downgrades_Chance.Rank_" + tier.toNumerical()); }

    /* SMELTING */
    public int getBurnModifierMaxLevel() { return config.getInt("Skills.Smelting.FuelEfficiency_MaxBonusLevel", 1000); }
    public double getBurnTimeMultiplier() { return config.getDouble("Skills.Smelting.FuelEfficiency_Multiplier", 3.0D); }

    public int getSecondSmeltMaxLevel() { return config.getInt("Skills.Smelting.SecondSmelt_MaxBonusLevel", 1000); }
    public double getSecondSmeltMaxChance() { return config.getDouble("Skills.Smelting.SecondSmelt_MaxBonusChance", 100.0D); }

    public int getFluxMiningUnlockLevel() { return config.getInt("Skills.Smelting.FluxMining_UnlockLevel", 250); }
    public double getFluxMiningChance() { return config.getDouble("Skills.Smelting.FluxMining_Chance", 33.0D); }

    public int getSmeltingVanillaXPBoostRankLevel(Smelting.Tier tier) { return config.getInt("Skills.Smelting.VanillaXPBoost.Rank_Levels.Rank_" + tier.toNumerical()); }
    public int getSmeltingVanillaXPBoostMultiplier(Smelting.Tier tier) { return config.getInt("Skills.Smelting.VanillaXPBoost.XP_Multiplier.Rank_" + tier.toNumerical()); }

    /* SWORDS */
    public double getBleedChanceMax() { return config.getDouble("Skills.Swords.Bleed_ChanceMax", 75.0D); }
    public int getBleedMaxBonusLevel() { return config.getInt("Skills.Swords.Bleed_MaxBonusLevel", 750); }
    public int getBleedMaxTicks() { return config.getInt("Skills.Swords.Bleed_MaxTicks", 3); }
    public int getBleedBaseTicks() { return config.getInt("Skills.Swords.Bleed_BaseTicks", 2); }

    public boolean getCounterRequiresBlock() { return config.getBoolean("Skills.Swords.Counter_RequiresBlock"); }
    public double getCounterChanceMax() { return config.getDouble("Skills.Swords.Counter_ChanceMax", 30.0D); }
    public int getCounterMaxBonusLevel() { return config.getInt("Skills.Swords.Counter_MaxBonusLevel", 600); }
    public double getCounterModifier() { return config.getDouble("Skills.Swords.Counter_DamageModifier", 2.0D); }

    public double getSerratedStrikesModifier() { return config.getDouble("Skills.Swords.SerratedStrikes_DamageModifier", 4.0D); }
    public int getSerratedStrikesTicks() { return config.getInt("Skills.Swords.SerratedStrikes_BleedTicks", 5); }

    /* TAMING */
    public double getGoreChanceMax() { return config.getDouble("Skills.Taming.Gore_ChanceMax", 100.0D); }
    public int getGoreMaxBonusLevel() { return config.getInt("Skills.Taming.Gore_MaxBonusLevel", 1000); }
    public int getGoreBleedTicks() { return config.getInt("Skills.Taming.Gore_BleedTicks", 2); }
    public double getGoreModifier() { return config.getDouble("Skills.Taming.Gore_Modifier", 2.0D); }

    public int getFastFoodUnlock() { return config.getInt("Skills.Taming.FastFood_UnlockLevel", 50); }
    public double getFastFoodChance() { return config.getDouble("Skills.Taming.FastFood_Chance", 50.0D); }

    public int getEnviromentallyAwareUnlock() { return config.getInt("Skills.Taming.EnvironmentallyAware_UnlockLevel", 100); }

    public int getThickFurUnlock() { return config.getInt("Skills.Taming.ThickFur_UnlockLevel", 250); }
    public double getThickFurModifier() { return config.getDouble("Skills.Taming.ThickFur_Modifier", 2.0D); }

    public int getHolyHoundUnlock() {return config.getInt("Skills.Taming.HolyHound_UnlockLevel", 375); }

    public int getShockProofUnlock() { return config.getInt("Skills.Taming.ShockProof_UnlockLevel", 500); }
    public double getShockProofModifier() { return config.getDouble("Skills.Taming.ShockProof_Modifier", 6.0D); }

    public int getSharpenedClawsUnlock() { return config.getInt("Skills.Taming.SharpenedClaws_UnlockLevel", 750); }
    public double getSharpenedClawsBonus() { return config.getDouble("Skills.Taming.SharpenedClaws_Bonus", 2.0D); }

    /* UNARMED */
    public double getDisarmChanceMax() { return config.getDouble("Skills.Unarmed.Disarm_ChanceMax", 33.0D); }
    public int getDisarmMaxBonusLevel() { return config.getInt("Skills.Unarmed.Disarm_MaxBonusLevel", 1000); }

    public double getDeflectChanceMax() { return config.getDouble("Skills.Unarmed.Deflect_ChanceMax", 50.0D); }
    public int getDeflectMaxBonusLevel() { return config.getInt("Skills.Unarmed.Deflect_MaxBonusLevel", 1000); }

    public double getIronGripChanceMax() { return config.getDouble("Skills.Unarmed.IronGrip_ChanceMax", 100.0D); }
    public int getIronGripMaxBonusLevel() { return config.getInt("Skills.Unarmed.IronGrip_MaxBonusLevel", 1000); }

    public double getIronArmMinBonus() { return config.getDouble("Skills.Unarmed.IronArm_BonusMin", 3.0D); }
    public double getIronArmMaxBonus() { return config.getDouble("Skills.Unarmed.IronArm_BonusMax", 8.0D); }
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
    public double getKrakenHealth() { return config.getDouble("Kraken.Health", 50.0D); }
    public String getKrakenName() { return config.getString("Kraken.Name", "The Kraken"); }
    public String getServerUnleashMessage() { return config.getString("Kraken.Unleashed_Message.Server", ""); }
    public String getPlayerUnleashMessage() { return config.getString("Kraken.Unleashed_Message.Player", ""); }
    public String getPlayerDefeatMessage() { return config.getString("Kraken.Defeated_Message.Killed", ""); }
    public String getPlayerEscapeMessage() { return config.getString("Kraken.Defeated_Message.Escape", ""); }
    public int getKrakenAttackInterval() { return config.getInt("Kraken.Attack_Interval_Seconds", 1); }
    public double getKrakenAttackDamage() { return config.getDouble("Kraken.Attack_Damage", 1.0D); }
}
