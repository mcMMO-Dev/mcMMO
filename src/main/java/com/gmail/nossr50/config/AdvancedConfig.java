package com.gmail.nossr50.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.fishing.Fishing;
import com.gmail.nossr50.skills.mining.BlastMining;
import com.gmail.nossr50.skills.repair.ArcaneForging;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.skills.smelting.Smelting;
import net.md_5.bungee.api.ChatColor;

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

        /*
         * In the future this method will check keys for all skills, but for now it only checks overhauled skills
         */
        checkKeys(reason);

        /* GENERAL */
        if (getAbilityLengthRetro() < 1) {
            reason.add("Skills.General.Ability.Length.RetroMode.IncreaseLevel should be at least 1!");
        }

        if (getAbilityLengthStandard() < 1) {
            reason.add("Skills.General.Ability.Length.Standard.IncreaseLevel should be at least 1!");
        }

        if (getEnchantBuff() < 1) {
            reason.add("Skills.General.Ability.EnchantBuff should be at least 1!");
        }

        /* ACROBATICS */
        if (getMaxChance(SubSkillType.ACROBATICS_DODGE) < 1) {
            reason.add("Skills.Acrobatics.Dodge.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ACROBATICS_DODGE) < 1) {
            reason.add("Skills.Acrobatics.Dodge.MaxBonusLevel should be at least 1!");
        }

        if (getDodgeDamageModifier() <= 1) {
            reason.add("Skills.Acrobatics.Dodge.DamageModifier should be greater than 1!");
        }

        if (getMaxChance(SubSkillType.ACROBATICS_ROLL) < 1) {
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

        /* ALCHEMY */
        if (getCatalysisUnlockLevel() < 0) {
            reason.add("Skills.Alchemy.Catalysis.UnlockLevel should be at least 0!");
        }

        if (getCatalysisMaxBonusLevel() <= getCatalysisUnlockLevel()) {
            reason.add("Skills.Alchemy.Catalysis.MaxBonusLevel should be greater than Skills.Alchemy.Catalysis.UnlockLevel!");
        }

        if (getCatalysisMinSpeed() <= 0) {
            reason.add("Skills.Alchemy.Catalysis.MinSpeed must be greater than 0!");
        }

        if (getCatalysisMaxSpeed() < getCatalysisMinSpeed()) {
            reason.add("Skills.Alchemy.Catalysis.MaxSpeed should be at least Skills.Alchemy.Catalysis.MinSpeed!");
        }

        List<Alchemy.Tier> alchemyTierList = Arrays.asList(Alchemy.Tier.values());
        for (Alchemy.Tier tier : alchemyTierList) {
            if (getConcoctionsTierLevel(tier) < 0) {
                reason.add("Skills.Alchemy.Rank_Levels.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (tier != Alchemy.Tier.fromNumerical(Alchemy.Tier.values().length)) {
                Alchemy.Tier nextTier = alchemyTierList.get(alchemyTierList.indexOf(tier) - 1);

                if (getConcoctionsTierLevel(tier) > getConcoctionsTierLevel(nextTier)) {
                    reason.add("Skills.Alchemy.Rank_Levels.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Alchemy.Rank_Levels.Rank_" + nextTier.toNumerical() + "!");
                }
            }
        }

        /* ARCHERY */

        if (getSkillShotRankDamageMultiplier() <= 0) {
            reason.add("Skills.Archery.SkillShot.RankDamageMultiplier should be greater than 0!");
        }

        if (getMaxChance(SubSkillType.ARCHERY_DAZE) < 1) {
            reason.add("Skills.Archery.Daze.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ARCHERY_DAZE) < 1) {
            reason.add("Skills.Archery.Daze.MaxBonusLevel should be at least 1!");
        }

        if (getDazeBonusDamage() < 0) {
            reason.add("Skills.Archery.Daze.BonusDamage should be at least 0!");
        }

        if (getMaxChance(SubSkillType.ARCHERY_ARROW_RETRIEVAL) < 1) {
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

        if (getMaxChance(SubSkillType.AXES_CRITICAL_STRIKES) < 1) {
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

        /* FISHING */
        List<Fishing.Tier> fishingTierList = Arrays.asList(Fishing.Tier.values());

        for (Fishing.Tier tier : fishingTierList) {
            if (getFishingTierLevel(tier) < 0) {
                reason.add("Skills.Fishing.Rank_Levels.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getShakeChance(tier) < 0) {
                reason.add("Skills.Fishing.Shake_Chance.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getFishingVanillaXPModifier(tier) < 0) {
                reason.add("Skills.Fishing.VanillaXPMultiplier.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (tier != Fishing.Tier.EIGHT) {
                Fishing.Tier nextTier = fishingTierList.get(fishingTierList.indexOf(tier) - 1);

                if (getFishingTierLevel(tier) > getFishingTierLevel(nextTier)) {
                    reason.add("Skills.Fishing.Rank_Levels.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Fishing.Rank_Levels.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getShakeChance(tier) > getShakeChance(nextTier)) {
                    reason.add("Skills.Fishing.Shake_Chance.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Fishing.Shake_Chance.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getFishingVanillaXPModifier(tier) > getFishingVanillaXPModifier(nextTier)) {
                    reason.add("Skills.Fishing.VanillaXPMultiplier.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Fishing.VanillaXPMultiplier.Rank_" + nextTier.toNumerical() + "!");
                }
            }
        }

        if (getFishermanDietRankChange() < 1) {
            reason.add("Skills.Fishing.FishermansDiet.RankChange should be at least 1!");
        }

        if (getIceFishingUnlockLevel() < 0) {
            reason.add("Skills.Fishing.IceFishing.UnlockLevel should be at least 0!");
        }

        if (getMasterAnglerUnlockLevel() < 0) {
            reason.add("Skills.Fishing.MasterAngler.UnlockLevel should be at least 0!");
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

        if (getMaxChance(SubSkillType.HERBALISM_GREEN_THUMB) < 1) {
            reason.add("Skills.Herbalism.GreenThumb.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_GREEN_THUMB) < 1) {
            reason.add("Skills.Herbalism.GreenThumb.MaxBonusLevel should be at least 1!");
        }

        if (getMaxChance(SubSkillType.HERBALISM_DOUBLE_DROPS) < 1) {
            reason.add("Skills.Herbalism.DoubleDrops.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_DOUBLE_DROPS) < 1) {
            reason.add("Skills.Herbalism.DoubleDrops.MaxBonusLevel should be at least 1!");
        }

        if (getMaxChance(SubSkillType.HERBALISM_HYLIAN_LUCK) < 1) {
            reason.add("Skills.Herbalism.HylianLuck.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_HYLIAN_LUCK) < 1) {
            reason.add("Skills.Herbalism.HylianLuck.MaxBonusLevel should be at least 1!");
        }

        if (getMaxChance(SubSkillType.HERBALISM_SHROOM_THUMB) < 1) {
            reason.add("Skills.Herbalism.ShroomThumb.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_SHROOM_THUMB) < 1) {
            reason.add("Skills.Herbalism.ShroomThumb.MaxBonusLevel should be at least 1!");
        }

        /* MINING */
        if (getMaxChance(SubSkillType.MINING_DOUBLE_DROPS) < 1) {
            reason.add("Skills.Mining.DoubleDrops.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.MINING_DOUBLE_DROPS) < 1) {
            reason.add("Skills.Mining.DoubleDrops.MaxBonusLevel should be at least 1!");
        }

        List<BlastMining.Tier> blastMiningTierList = Arrays.asList(BlastMining.Tier.values());

        for (BlastMining.Tier tier : blastMiningTierList) {
            if (getBlastMiningRankLevel(tier) < 0) {
                reason.add("Skills.Mining.BlastMining.Rank_Levels.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getBlastDamageDecrease(tier) < 0) {
                reason.add("Skills.Mining.BlastMining.BlastDamageDecrease.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getOreBonus(tier) < 0) {
                reason.add("Skills.Mining.BlastMining.OreBonus.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getDebrisReduction(tier) < 0) {
                reason.add("Skills.Mining.BlastMining.DebrisReduction.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getDropMultiplier(tier) < 0) {
                reason.add("Skills.Mining.BlastMining.DropMultiplier.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getBlastRadiusModifier(tier) < 0) {
                reason.add("Skills.Mining.BlastMining.BlastRadiusModifier.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (tier != BlastMining.Tier.EIGHT) {
                BlastMining.Tier nextTier = blastMiningTierList.get(blastMiningTierList.indexOf(tier) - 1);

                if (getBlastMiningRankLevel(tier) > getBlastMiningRankLevel(nextTier)) {
                    reason.add("Skills.Mining.BlastMining.Rank_Levels.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Mining.BlastMining.Rank_Levels.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getBlastDamageDecrease(tier) > getBlastDamageDecrease(nextTier)) {
                    reason.add("Skills.Mining.BlastMining.BlastDamageDecrease.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Mining.BlastMining.BlastDamageDecrease.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getOreBonus(tier) > getOreBonus(nextTier)) {
                    reason.add("Skills.Mining.BlastMining.OreBonus.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Mining.BlastMining.OreBonus.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getDebrisReduction(tier) > getDebrisReduction(nextTier)) {
                    reason.add("Skills.Mining.BlastMining.DebrisReduction.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Mining.BlastMining.DebrisReduction.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getDropMultiplier(tier) > getDropMultiplier(nextTier)) {
                    reason.add("Skills.Mining.BlastMining.DropMultiplier.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Mining.BlastMining.DropMultiplier.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getBlastRadiusModifier(tier) > getBlastRadiusModifier(nextTier)) {
                    reason.add("Skills.Mining.BlastMining.BlastRadiusModifier.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Mining.BlastMining.BlastRadiusModifier.Rank_" + nextTier.toNumerical() + "!");
                }
            }
        }

        /* REPAIR */
        if (getRepairMasteryMaxBonus() < 1) {
            reason.add("Skills.Repair.RepairMastery.MaxBonusPercentage should be at least 1!");
        }

        if (getRepairMasteryMaxLevel() < 1) {
            reason.add("Skills.Repair.RepairMastery.MaxBonusLevel should be at least 1!");
        }

        if (getMaxChance(SubSkillType.REPAIR_SUPER_REPAIR) < 1) {
            reason.add("Skills.Repair.SuperRepair.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.REPAIR_SUPER_REPAIR) < 1) {
            reason.add("Skills.Repair.SuperRepair.MaxBonusLevel should be at least 1!");
        }

        List<ArcaneForging.Tier> arcaneForgingTierList = Arrays.asList(ArcaneForging.Tier.values());

        for (ArcaneForging.Tier tier : arcaneForgingTierList) {
            if (getArcaneForgingRankLevel(tier) < 0) {
                reason.add("Skills.Repair.ArcaneForging.Rank_Levels.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getArcaneForgingDowngradeChance(tier) < 0 || getArcaneForgingDowngradeChance(tier) > 100) {
                reason.add("Skills.Repair.ArcaneForging.Downgrades.Chance.Rank_" + tier.toNumerical() + " only accepts values from 0 to 100!");
            }

            if (getArcaneForgingKeepEnchantsChance(tier) < 0 || getArcaneForgingKeepEnchantsChance(tier) > 100) {
                reason.add("Skills.Repair.ArcaneForging.Keep_Enchants.Chance.Rank_" + tier.toNumerical() + " only accepts values from 0 to 100!");
            }

            if (tier != ArcaneForging.Tier.EIGHT) {
                ArcaneForging.Tier nextTier = arcaneForgingTierList.get(arcaneForgingTierList.indexOf(tier) - 1);

                if (getArcaneForgingRankLevel(tier) > getArcaneForgingRankLevel(nextTier)) {
                    reason.add("Skills.Repair.ArcaneForging.Rank_Levels.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Repair.ArcaneForging.Rank_Levels.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getArcaneForgingDowngradeChance(nextTier) > getArcaneForgingDowngradeChance(tier)) {
                    reason.add("Skills.Repair.ArcaneForging.Downgrades.Chance.Rank_" + nextTier.toNumerical() + " should be less than or equal to Skills.Repair.ArcaneForging.Downgrades.Chance.Rank_" + tier.toNumerical() + "!");
                }

                if (getArcaneForgingKeepEnchantsChance(tier) > getArcaneForgingKeepEnchantsChance(nextTier)) {
                    reason.add("Skills.Repair.ArcaneForging.Keep_Enchants.Chance.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Repair.ArcaneForging.Keep_Enchants.Chance.Rank_" + nextTier.toNumerical() + "!");
                }
            }
        }

        /* SALVAGE */
        if (getSalvageMaxPercentage() < 1) {
            reason.add("Skills.Salvage.MaxPercentage should be at least 1!");
        }

        if (getSalvageMaxPercentageLevel() < 1) {
            reason.add("Skills.Salvage.MaxPercentageLevel should be at least 1!");
        }

        if (getAdvancedSalvageUnlockLevel() < 0) {
            reason.add("Skills.Salvage.AdvancedSalvage.UnlockLevel should be at least 0!");
        }

        List<Salvage.Tier> salvageTierList = Arrays.asList(Salvage.Tier.values());

        for (Salvage.Tier tier : salvageTierList) {
            if (getArcaneSalvageRankLevel(tier) < 0) {
                reason.add("Skills.Salvage.ArcaneSalvage.Rank_Levels.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getArcaneSalvageExtractFullEnchantsChance(tier) < 0 || getArcaneSalvageExtractFullEnchantsChance(tier) > 100) {
                reason.add("Skills.Salvage.ArcaneSalvage.ExtractFullEnchant.Rank_" + tier.toNumerical() + " only accepts values from 0 to 100!");
            }

            if (getArcaneSalvageExtractPartialEnchantsChance(tier) < 0 || getArcaneSalvageExtractPartialEnchantsChance(tier) > 100) {
                reason.add("Skills.Salvage.ArcaneSalvage.ExtractPartialEnchant.Rank_" + tier.toNumerical() + " only accepts values from 0 to 100!");
            }

            if (tier != Salvage.Tier.EIGHT) {
                Salvage.Tier nextTier = salvageTierList.get(salvageTierList.indexOf(tier) - 1);

                if (getArcaneSalvageRankLevel(tier) > getArcaneSalvageRankLevel(nextTier)) {
                    reason.add("Skills.Salvage.ArcaneSalvage.Rank_Levels.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Salvage.ArcaneSalvage.Rank_Levels.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getArcaneSalvageExtractFullEnchantsChance(tier) > getArcaneSalvageExtractFullEnchantsChance(nextTier)) {
                    reason.add("Skills.Salvage.ArcaneSalvage.ExtractFullEnchant.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Salvage.ArcaneSalvage.ExtractFullEnchant.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getArcaneSalvageExtractPartialEnchantsChance(tier) > getArcaneSalvageExtractPartialEnchantsChance(nextTier)) {
                    reason.add("Skills.Salvage.ArcaneSalvage.ExtractPartialEnchant.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Salvage.ArcaneSalvage.ExtractPartialEnchant.Rank_" + nextTier.toNumerical() + "!");
                }
            }
        }

        /* SMELTING */
        if (getBurnModifierMaxLevel() < 1) {
            reason.add("Skills.Smelting.FuelEfficiency.MaxBonusLevel should be at least 1!");
        }

        if (getBurnTimeMultiplier() < 1) {
            reason.add("Skills.Smelting.FuelEfficiency.Multiplier should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SMELTING_SECOND_SMELT) < 1) {
            reason.add("Skills.Smelting.SecondSmelt.MaxBonusLevel should be at least 1!");
        }

        if (getMaxChance(SubSkillType.SMELTING_SECOND_SMELT) < 1) {
            reason.add("Skills.Smelting.SecondSmelt.ChanceMax should be at least 1!");
        }

        if (getFluxMiningUnlockLevel() < 0) {
            reason.add("Skills.Smelting.FluxMining.UnlockLevel should be at least 0!");
        }

        if (getFluxMiningChance() < 1) {
            reason.add("Skills.Smelting.FluxMining.Chance should be at least 1!");
        }

        List<Smelting.Tier> smeltingTierList = Arrays.asList(Smelting.Tier.values());

        for (Smelting.Tier tier : smeltingTierList) {
            if (getSmeltingRankLevel(tier) < 0) {
                reason.add("Skills.Smelting.Rank_Levels.Rank_" + tier.toNumerical() + " should be at least 0!");
            }

            if (getSmeltingVanillaXPBoostMultiplier(tier) < 1) {
                reason.add("Skills.Smelting.VanillaXPMultiplier.Rank_" + tier.toNumerical() + " should be at least 1!");
            }

            if (tier != Smelting.Tier.EIGHT) {
                Smelting.Tier nextTier = smeltingTierList.get(smeltingTierList.indexOf(tier) - 1);

                if (getSmeltingRankLevel(tier) > getSmeltingRankLevel(nextTier)) {
                    reason.add("Skills.Smelting.Rank_Levels.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Smelting.Rank_Levels.Rank_" + nextTier.toNumerical() + "!");
                }

                if (getSmeltingVanillaXPBoostMultiplier(tier) > getSmeltingVanillaXPBoostMultiplier(nextTier)) {
                    reason.add("Skills.Smelting.VanillaXPMultiplier.Rank_" + tier.toNumerical() + " should be less than or equal to Skills.Smelting.VanillaXPMultiplier.Rank_" + nextTier.toNumerical() + "!");
                }
            }
        }

        /* SWORDS */
        if (getMaxChance(SubSkillType.SWORDS_BLEED) < 1) {
            reason.add("Skills.Swords.Bleed.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SWORDS_BLEED) < 1) {
            reason.add("Skills.Swords.Bleed.MaxBonusLevel should be at least 1!");
        }

        if (getBleedMaxTicks() < 1) {
            reason.add("Skills.Swords.Bleed.MaxTicks should be at least 1!");
        }

        if (getBleedMaxTicks() < getBleedBaseTicks()) {
            reason.add("Skills.Swords.Bleed.MaxTicks should be at least Skills.Swords.Bleed.BaseTicks!");
        }

        if (getBleedBaseTicks() < 1) {
            reason.add("Skills.Swords.Bleed.BaseTicks should be at least 1!");
        }

        if (getMaxChance(SubSkillType.SWORDS_COUNTER_ATTACK) < 1) {
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
            reason.add("Skills.Swords.SerratedStrikes.BleedTicks should be at least 1!");
        }

        /* TAMING */

        if (getMaxChance(SubSkillType.TAMING_GORE) < 1) {
            reason.add("Skills.Taming.Gore.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.TAMING_GORE) < 1) {
            reason.add("Skills.Taming.Gore.MaxBonusLevel should be at least 1!");
        }

        if (getGoreBleedTicks() < 1) {
            reason.add("Skills.Taming.Gore.BleedTicks should be at least 1!");
        }

        if (getGoreModifier() < 1) {
            reason.add("Skills.Taming.Gore.Modifier should be at least 1!");
        }

        if (getFastFoodUnlock() < 0) {
            reason.add("Skills.Taming.FastFood.UnlockLevel should be at least 0!");
        }

        if (getFastFoodChance() < 1) {
            reason.add("Skills.Taming.FastFood.Chance should be at least 1!");
        }

        if (getEnviromentallyAwareUnlock() < 0) {
            reason.add("Skills.Taming.EnvironmentallyAware.UnlockLevel should be at least 0!");
        }

        if (getThickFurUnlock() < 0) {
            reason.add("Skills.Taming.ThickFur.UnlockLevel should be at least 0!");
        }

        if (getThickFurModifier() < 1) {
            reason.add("Skills.Taming.ThickFur.Modifier should be at least 1!");
        }

        if (getHolyHoundUnlock() < 0) {
            reason.add("Skills.Taming.HolyHound.UnlockLevel should be at least 0!");
        }

        if (getShockProofUnlock() < 0) {
            reason.add("Skills.Taming.ShockProof.UnlockLevel should be at least 0!");
        }

        if (getShockProofModifier() < 1) {
            reason.add("Skills.Taming.ShockProof.Modifier should be at least 1!");
        }

        if (getSharpenedClawsUnlock() < 0) {
            reason.add("Skills.Taming.SharpenedClaws.UnlockLevel should be at least 0!");
        }

        if (getSharpenedClawsBonus() < 1) {
            reason.add("Skills.Taming.SharpenedClaws.Bonus should be at least 1!");
        }

        if (getMaxHorseJumpStrength() < 0 || getMaxHorseJumpStrength() > 2) {
            reason.add("Skills.Taming.CallOfTheWild.MaxHorseJumpStrength should be between 0 and 2!");
        }

        /* UNARMED */
        if (getMaxChance(SubSkillType.UNARMED_DISARM) < 1) {
            reason.add("Skills.Unarmed.Disarm.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_DISARM) < 1) {
            reason.add("Skills.Unarmed.Disarm.MaxBonusLevel should be at least 1!");
        }

        if (getMaxChance(SubSkillType.UNARMED_ARROW_DEFLECT) < 1) {
            reason.add("Skills.Unarmed.ArrowDeflect.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_ARROW_DEFLECT) < 1) {
            reason.add("Skills.Unarmed.ArrowDeflect.MaxBonusLevel should be at least 1!");
        }

        if (getMaxChance(SubSkillType.UNARMED_IRON_GRIP) < 1) {
            reason.add("Skills.Unarmed.IronGrip.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_IRON_GRIP) < 1) {
            reason.add("Skills.Unarmed.IronGrip.MaxBonusLevel should be at least 1!");
        }

        if (getIronArmMinBonus() < 0) {
            reason.add("Skills.Unarmed.IronArmStyle.BonusMin should be at least 0!");
        }

        if (getIronArmMaxBonus() < 0) {
            reason.add("Skills.Unarmed.IronArmStyle.BonusMax should be at least 0!");
        }

        if (getIronArmMaxBonus() < getIronArmMinBonus()) {
            reason.add("Skills.Unarmed.IronArmStyle.BonusMax should be greater than or equal to Skills.Unarmed.IronArm.BonusMin!");
        }

        if (getIronArmIncreaseLevel() < 1) {
            reason.add("Skills.Unarmed.IronArmStyle.IncreaseLevel should be at least 1!");
        }

        /* WOODCUTTING */

        if (getLeafBlowUnlockLevel() < 0) {
            reason.add("Skills.Woodcutting.LeafBlower.UnlockLevel should be at least 0!");
        }

        if (getMaxChance(SubSkillType.WOODCUTTING_HARVEST_LUMBER) < 1) {
            reason.add("Skills.Woodcutting.HarvestLumber.ChanceMax should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER) < 1) {
            reason.add("Skills.Woodcutting.HarvestLumber.MaxBonusLevel should be at least 1!");
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
    public int getAbilityLengthStandard() { return config.getInt("Skills.General.Ability.Length.Standard.IncreaseLevel", 5); }
    public int getAbilityLengthRetro() { return config.getInt("Skills.General.Ability.Length.RetroMode.IncreaseLevel", 50); }
    public int getEnchantBuff() { return config.getInt("Skills.General.Ability.EnchantBuff", 5); }

    public int getMaxBonusLevel(SubSkillType subSkillType) { return config.getInt(subSkillType.getAdvConfigAddress() + ".MaxBonusLevel"); }
    public double getMaxChance(SubSkillType subSkillType) { return config.getDouble(subSkillType.getAdvConfigAddress() + ".ChanceMax", 100.0D);}

    public int getMaxBonusLevel(AbstractSubSkill abstractSubSkill) {
        return config.getInt("Skills."+abstractSubSkill.getPrimaryKeyName()+"."+abstractSubSkill.getConfigKeyName()+".MaxBonusLevel");
    }

    public double getMaxChance(AbstractSubSkill abstractSubSkill)
    {
        return config.getDouble("Skills."+abstractSubSkill.getPrimaryKeyName()+"."+abstractSubSkill.getConfigKeyName()+".ChanceMax", 100.0D);
    }

    /* Notification Settings */
    public boolean doesNotificationUseActionBar(NotificationType notificationType)
    {
        return config.getBoolean("Feedback.ActionBarNotifications."+notificationType.toString()+".Enabled", true);
    }

    public boolean doesNotificationSendCopyToChat(NotificationType notificationType)
    {
        return config.getBoolean("Feedback.ActionBarNotifications."+notificationType.toString()+".SendCopyOfMessageToChat", false);
    }

    /*
     * JSON Style Settings
     */


    /*public ChatColor getJSONStatHoverElementColor(StatType statType, boolean isPrefix)
    {
        String keyAddress = isPrefix ? "Prefix" : "Value";
        String keyLocation = "Style.JSON.Hover.Details." + StringUtils.getCapitalized(statType.toString()) +"."+keyAddress+".Color";

        return getChatColorFromKey(keyLocation);
    }*/

    /**
     * Used to color our details header in our JSON Hover Object tooltips
     * @return the ChatColor for this element
     */
    public ChatColor getJSONStatHoverDetailsColor()
    {
        String keyLocation = "Style.JSON.Hover.Details.Header.Color";
        return getChatColorFromKey(keyLocation);
    }

    public boolean isJSONDetailsHeaderBold()
    {
        return config.getBoolean("Style.JSON.Hover.Details.Header.Bold");
    }

    public boolean isJSONDetailsHeaderItalic()
    {
        return config.getBoolean("Style.JSON.Hover.Details.Header.Italics");
    }

    public boolean isJSONDetailsHeaderUnderlined()
    {
        return config.getBoolean("Style.JSON.Hover.Details.Header.Underlined");
    }

    public ChatColor getJSONStatHoverDescriptionColor()
    {
        String keyLocation = "Style.JSON.Hover.Details.Description.Color";
        return getChatColorFromKey(keyLocation);
    }

    public boolean isJSONDetailsDescriptionBold()
    {
        return config.getBoolean("Style.JSON.Hover.Details.Description.Bold");
    }

    public boolean isJSONDetailsDescriptionItalic()
    {
        return config.getBoolean("Style.JSON.Hover.Details.Description.Italics");
    }

    public boolean isJSONDetailsDescriptionUnderlined()
    {
        return config.getBoolean("Style.JSON.Hover.Details.Description.Underlined");
    }

    public ChatColor getJSONActionBarColor(NotificationType notificationType)
    {
        return getChatColor(config.getString("Style.JSON.Notification."+notificationType.toString()+".Color"));
    }

    private ChatColor getChatColorFromKey(String keyLocation) {
        String colorName = config.getString(keyLocation);

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

    /*public boolean isJSONStatHoverElementBold(StatType statType, boolean isPrefix)
    {
        String keyAddress = isPrefix ? "Prefix" : "Value";
        String keyLocation = "Style.JSON.Hover.Details." + StringUtils.getCapitalized(statType.toString()) +"."+keyAddress+".Bold";
        return config.getBoolean(keyLocation);
    }

    public boolean isJSONStatHoverElementItalic(StatType statType, boolean isPrefix)
    {
        String keyAddress = isPrefix ? "Prefix" : "Value";
        String keyLocation = "Style.JSON.Hover.Details." + StringUtils.getCapitalized(statType.toString()) +"."+keyAddress+".Italics";
        return config.getBoolean(keyLocation);
    }

    public boolean isJSONStatHoverElementUnderlined(StatType statType, boolean isPrefix)
    {
        String keyAddress = isPrefix ? "Prefix" : "Value";
        String keyLocation = "Style.JSON.Hover.Details." + StringUtils.getCapitalized(statType.toString()) +"."+keyAddress+".Underline";
        return config.getBoolean(keyLocation);
    }*/


    /**
     * Gets the level required to unlock a subskill at a given rank
     * @param subSkillType The subskill
     * @param rank The rank of the skill
     * @return The level required to use this rank of the subskill
     * @deprecated Right now mcMMO is an overhaul process, this will only work for skills I have overhauled. I will be removing the deprecated tag when that is true.
     */
    @Deprecated
    public int getSubSkillUnlockLevel(SubSkillType subSkillType, int rank)
    {
        /*
         * This is a bit messy but
         *
         * Some skills have per-rank settings as child nodes for Rank_x nodes
         * If they do, we have to grab the child node named LevelReq from Rank_x for that skill
         *
         * Other skills which do not have complex per-rank settings will instead find their Level Requirement returned at Rank_x
         */
        if(config.get(subSkillType.getAdvConfigAddress() + ".Rank_Levels.Rank_"+rank+".LevelReq") != null)
            return config.getInt(subSkillType.getAdvConfigAddress() + ".Rank_Levels.Rank_"+rank+".LevelReq");
        else
            return config.getInt(subSkillType.getAdvConfigAddress() + ".Rank_Levels.Rank_"+rank);
    }

    @Deprecated /* NEW VERSION */
    public int getSubSkillUnlockLevel(AbstractSubSkill abstractSubSkill, int rank)
    {
        /*
         * This is a bit messy but
         *
         * Some skills have per-rank settings as child nodes for Rank_x nodes
         * If they do, we have to grab the child node named LevelReq from Rank_x for that skill
         *
         * Other skills which do not have complex per-rank settings will instead find their Level Requirement returned at Rank_x
         */

        String key = "Skills."+abstractSubSkill.getPrimaryKeyName()+"."+abstractSubSkill.getConfigKeyName();

        if(config.get(key + ".Rank_Levels.Rank_"+rank+".LevelReq") != null)
            return config.getInt(key + ".Rank_Levels.Rank_"+rank+".LevelReq");
        else
            return config.getInt(key + ".Rank_Levels.Rank_"+rank);
    }

    /**
     * Some SubSkills have the ability to retain classic functionality
     * @param subSkillType SubSkillType with classic functionality
     * @return true if the subskill is in classic mode
     */
    public boolean isSubSkillClassic(SubSkillType subSkillType)
    {
        return config.getBoolean(subSkillType.getAdvConfigAddress()+".Classic");
    }

    /* ACROBATICS */
    public double getDodgeDamageModifier() { return config.getDouble("Skills.Acrobatics.Dodge.DamageModifier", 2.0D); }

    public double getRollDamageThreshold() { return config.getDouble("Skills.Acrobatics.Roll.DamageThreshold", 7.0D); }

    public double getGracefulRollDamageThreshold() { return config.getDouble("Skills.Acrobatics.GracefulRoll.DamageThreshold", 14.0D); }

    /* ALCHEMY */
    public int getCatalysisUnlockLevel() { return config.getInt("Skills.Alchemy.Catalysis.UnlockLevel", 100); }
    public int getCatalysisMaxBonusLevel() { return config.getInt("Skills.Alchemy.Catalysis.MaxBonusLevel", 1000); }

    public double getCatalysisMinSpeed() { return config.getDouble("Skills.Alchemy.Catalysis.MinSpeed", 1.0D); }
    public double getCatalysisMaxSpeed() { return config.getDouble("Skills.Alchemy.Catalysis.MaxSpeed", 4.0D); }

    public int getConcoctionsTierLevel(Alchemy.Tier tier) { return config.getInt("Skills.Alchemy.Rank_Levels.Rank_" + tier.toNumerical()); }

    /* ARCHERY */
    public int getSkillShotIncreaseLevel() { return config.getInt("Skills.Archery.SkillShot.IncreaseLevel", 50); }
    public double getSkillShotRankDamageMultiplier() { return config.getDouble("Skills.Archery.SkillShot.RankDamageMultiplier", 10.0D); }
    public double getSkillShotDamageMax() { return config.getDouble("Skills.Archery.SkillShot.MaxDamage", 9.0D); }

    public double getDazeBonusDamage() { return config.getDouble("Skills.Archery.Daze.BonusDamage", 4.0D); }

    public double getForceMultiplier() { return config.getDouble("Skills.Archery.ForceMultiplier", 2.0D); }

    /* AXES */
    public double getAxeMasteryRankDamageMultiplier() { return config.getDouble("Skills.Axes.AxeMastery.RankDamageMultiplier", 1.0D); }

    public double getCriticalStrikesPVPModifier() { return config.getDouble("Skills.Axes.CriticalStrikes.PVP_Modifier", 1.5D); }
    public double getCriticalStrikesPVEModifier() { return config.getDouble("Skills.Axes.CriticalStrikes.PVE_Modifier", 2.0D); }

    public double getGreaterImpactChance() { return config.getDouble("Skills.Axes.GreaterImpact.Chance", 25.0D); }
    public double getGreaterImpactModifier() { return config.getDouble("Skills.Axes.GreaterImpact.KnockbackModifier", 1.5D); }
    public double getGreaterImpactBonusDamage() { return config.getDouble("Skills.Axes.GreaterImpact.BonusDamage", 2.0D); }

    public int getArmorImpactIncreaseLevel() { return config.getInt("Skills.Axes.ArmorImpact.IncreaseLevel", 50); }
    public double getImpactChance() { return config.getDouble("Skills.Axes.ArmorImpact.Chance", 25.0D); }
    public double getArmorImpactMaxDurabilityDamage() { return config.getDouble("Skills.Axes.ArmorImpact.MaxPercentageDurabilityDamage", 20.0D); }

    public double getSkullSplitterModifier() { return config.getDouble("Skills.Axes.SkullSplitter.DamageModifier", 2.0D); }

    /* EXCAVATION */
    //Nothing to configure, everything is already configurable in config.yml

    /* FISHING */
    public int getFishingTierLevel(Fishing.Tier tier) { return config.getInt("Skills.Fishing.Rank_Levels.Rank_" + tier.toNumerical()); }
    public double getShakeChance(Fishing.Tier tier) { return config.getDouble("Skills.Fishing.ShakeChance.Rank_" + tier.toNumerical()); }
    public int getFishingVanillaXPModifier(Fishing.Tier tier) { return config.getInt("Skills.Fishing.VanillaXPMultiplier.Rank_" + tier.toNumerical()); }

    public int getFishermanDietRankChange() { return config.getInt("Skills.Fishing.FishermansDiet.RankChange", 200); }

    public int getIceFishingUnlockLevel() { return config.getInt("Skills.Fishing.IceFishing.UnlockLevel", 50); }

    public int getMasterAnglerUnlockLevel() {return config.getInt("Skills.Fishing.MasterAngler.UnlockLevel", 125); }
    public double getMasterAnglerBoatModifier() {return config.getDouble("Skills.Fishing.MasterAngler.BoatModifier", 2.0); }
    public double getMasterAnglerBiomeModifier() {return config.getDouble("Skills.Fishing.MasterAngler.BiomeModifier", 2.0); }

    /* HERBALISM */
    public int getFarmerDietRankChange() { return config.getInt("Skills.Herbalism.FarmersDiet.RankChange", 200); }

    public int getGreenThumbStageChange() { return config.getInt("Skills.Herbalism.GreenThumb.StageChange", 200); }

    /* MINING */
    public int getBlastMiningRankLevel(BlastMining.Tier tier) { return config.getInt("Skills.Mining.BlastMining.Rank_Levels.Rank_" + tier.toNumerical()); }
    public double getBlastDamageDecrease(BlastMining.Tier tier) { return config.getDouble("Skills.Mining.BlastMining.BlastDamageDecrease.Rank_" + tier.toNumerical()); }
    public double getOreBonus(BlastMining.Tier tier) { return config.getDouble("Skills.Mining.BlastMining.OreBonus.Rank_" + tier.toNumerical()); }
    public double getDebrisReduction(BlastMining.Tier tier) { return config.getDouble("Skills.Mining.BlastMining.DebrisReduction.Rank_" + tier.toNumerical()); }
    public int getDropMultiplier(BlastMining.Tier tier) { return config.getInt("Skills.Mining.BlastMining.DropMultiplier.Rank_" + tier.toNumerical()); }
    public double getBlastRadiusModifier(BlastMining.Tier tier) { return config.getDouble("Skills.Mining.BlastMining.BlastRadiusModifier.Rank_" + tier.toNumerical()); }

    /* REPAIR */
    public double getRepairMasteryMaxBonus() { return config.getDouble("Skills.Repair.RepairMastery.MaxBonusPercentage", 200.0D); }
    public int getRepairMasteryMaxLevel() { return config.getInt("Skills.Repair.RepairMastery.MaxBonusLevel", 1000); }

    /* Arcane Forging */
    public int getArcaneForgingRankLevel(ArcaneForging.Tier tier) { return config.getInt("Skills.Repair.ArcaneForging.Rank_Levels.Rank_" + tier.toNumerical()); }

    public boolean getArcaneForgingEnchantLossEnabled() { return config.getBoolean("Skills.Repair.ArcaneForging.May_Lose_Enchants", true); }
    public double getArcaneForgingKeepEnchantsChance(ArcaneForging.Tier tier) { return config.getDouble("Skills.Repair.ArcaneForging.Keep_Enchants_Chance.Rank_" + tier.toNumerical()); }

    public boolean getArcaneForgingDowngradeEnabled() { return config.getBoolean("Skills.Repair.ArcaneForging.Downgrades_Enabled", true); }
    public double getArcaneForgingDowngradeChance(ArcaneForging.Tier tier) { return config.getDouble("Skills.Repair.ArcaneForging.Downgrades_Chance.Rank_" + tier.toNumerical()); }

    /* SALVAGE */
    public double getSalvageMaxPercentage() { return config.getDouble("Skills.Salvage.MaxPercentage", 100.0D); }
    public int getSalvageMaxPercentageLevel() { return config.getInt("Skills.Salvage.MaxPercentageLevel", 1000); }

    public int getAdvancedSalvageUnlockLevel() { return config.getInt("Skills.Salvage.AdvancedSalvage.UnlockLevel", 350); }

    public boolean getArcaneSalvageEnchantDowngradeEnabled() { return config.getBoolean("Skills.Salvage.ArcaneSalvage.EnchantDowngradeEnabled", true); }
    public boolean getArcaneSalvageEnchantLossEnabled() { return config.getBoolean("Skills.Salvage.ArcaneSalvage.EnchantLossEnabled", true); }

    public int getArcaneSalvageRankLevel(Salvage.Tier tier) { return config.getInt("Skills.Salvage.ArcaneSalvage.Rank_Levels.Rank_" + tier.toNumerical()); }
    public double getArcaneSalvageExtractFullEnchantsChance(Salvage.Tier tier) { return config.getDouble("Skills.Salvage.ArcaneSalvage.ExtractFullEnchant.Rank_" + tier.toNumerical()); }
    public double getArcaneSalvageExtractPartialEnchantsChance(Salvage.Tier tier) { return config.getDouble("Skills.Salvage.ArcaneSalvage.ExtractPartialEnchant.Rank_" + tier.toNumerical()); }

    /* SMELTING */
    public int getBurnModifierMaxLevel() { return config.getInt("Skills.Smelting.FuelEfficiency.MaxBonusLevel", 1000); }
    public double getBurnTimeMultiplier() { return config.getDouble("Skills.Smelting.FuelEfficiency.Multiplier", 3.0D); }

    public int getFluxMiningUnlockLevel() { return config.getInt("Skills.Smelting.FluxMining.UnlockLevel", 250); }
    public double getFluxMiningChance() { return config.getDouble("Skills.Smelting.FluxMining.Chance", 33.0D); }

    public int getSmeltingRankLevel(Smelting.Tier tier) { return config.getInt("Skills.Smelting.Rank_Levels.Rank_" + tier.toNumerical()); }

    public int getSmeltingVanillaXPBoostMultiplier(Smelting.Tier tier) { return config.getInt("Skills.Smelting.VanillaXPMultiplier.Rank_" + tier.toNumerical()); }

    /* SWORDS */
    public double getBleedDamagePlayer() { return config.getDouble("Skills.Swords.Bleed.DamagePlayer", 1.0); }
    public double getBleedDamageMobs() { return config.getDouble("Skills.Swords.Bleed.DamageMobs", 2.0); }

    public int getBleedMaxTicks() { return config.getInt("Skills.Swords.Bleed.MaxTicks", 3); }
    public int getBleedBaseTicks() { return config.getInt("Skills.Swords.Bleed.BaseTicks", 2); }

    public boolean getCounterRequiresBlock() { return config.getBoolean("Skills.Swords.CounterAttack.RequiresBlock"); }
    public double getCounterModifier() { return config.getDouble("Skills.Swords.CounterAttack.DamageModifier", 2.0D); }

    public double getSerratedStrikesModifier() { return config.getDouble("Skills.Swords.SerratedStrikes.DamageModifier", 4.0D); }
    public int getSerratedStrikesTicks() { return config.getInt("Skills.Swords.SerratedStrikes.BleedTicks", 5); }

    /* TAMING */
    public int getGoreBleedTicks() { return config.getInt("Skills.Taming.Gore.BleedTicks", 2); }
    public double getGoreModifier() { return config.getDouble("Skills.Taming.Gore.Modifier", 2.0D); }

    public int getFastFoodUnlock() { return config.getInt("Skills.Taming.FastFood.UnlockLevel", 50); }
    public double getFastFoodChance() { return config.getDouble("Skills.Taming.FastFood.Chance", 50.0D); }

    public int getEnviromentallyAwareUnlock() { return config.getInt("Skills.Taming.EnvironmentallyAware.UnlockLevel", 100); }

    public int getThickFurUnlock() { return config.getInt("Skills.Taming.ThickFur.UnlockLevel", 250); }
    public double getThickFurModifier() { return config.getDouble("Skills.Taming.ThickFur.Modifier", 2.0D); }

    public int getHolyHoundUnlock() {return config.getInt("Skills.Taming.HolyHound.UnlockLevel", 375); }

    public int getShockProofUnlock() { return config.getInt("Skills.Taming.ShockProof.UnlockLevel", 500); }
    public double getShockProofModifier() { return config.getDouble("Skills.Taming.ShockProof.Modifier", 6.0D); }

    public int getSharpenedClawsUnlock() { return config.getInt("Skills.Taming.SharpenedClaws.UnlockLevel", 750); }
    public double getSharpenedClawsBonus() { return config.getDouble("Skills.Taming.SharpenedClaws.Bonus", 2.0D); }

    public double getMinHorseJumpStrength() { return config.getDouble("Skills.Taming.CallOfTheWild.MinHorseJumpStrength", 0.7D); }
    public double getMaxHorseJumpStrength() { return config.getDouble("Skills.Taming.CallOfTheWild.MaxHorseJumpStrength", 2.0D); }

    /* UNARMED */
    public double getIronArmMinBonus() { return config.getDouble("Skills.Unarmed.IronArmStyle.BonusMin", 3.0D); }
    public double getIronArmMaxBonus() { return config.getDouble("Skills.Unarmed.IronArmStyle.BonusMax", 8.0D); }
    public int getIronArmIncreaseLevel() { return config.getInt("Skills.Unarmed.IronArmStyle.IncreaseLevel", 50); }
    public boolean getDisarmProtected() { return config.getBoolean("Skills.Unarmed.Disarm.AntiTheft", false); }

    /* WOODCUTTING */
    public int getLeafBlowUnlockLevel() { return config.getInt("Skills.Woodcutting.LeafBlower.UnlockLevel", 100); }

    /* KRAKEN STUFF */
    public boolean getKrakenEnabled() { return config.getBoolean("Kraken.Enabled", true); }
    public int getKrakenTriesBeforeRelease() { return config.getInt("Kraken.Tries_Before_Release", 50); }
    public double getKrakenHealth() { return config.getDouble("Kraken.Health", 50.0D); }
    public String getKrakenName() { return config.getString("Kraken.Name", "The Kraken"); }
    public int getKrakenAttackInterval() { return config.getInt("Kraken.Attack_Interval_Seconds", 1); }
    public double getKrakenAttackDamage() { return config.getDouble("Kraken.Attack_Damage", 1.0D); }
    public boolean getKrakenGlobalEffectsEnabled() { return config.getBoolean("Kraken.Global_Effects", false); }
    public boolean getKrakenEscapeAllowed() { return config.getBoolean("Kraken.Allow_Escaping", false); }
    public String getServerUnleashMessage() { return config.getString("Kraken.Unleashed_Message.Server", ""); }
    public String getPlayerUnleashMessage() { return config.getString("Kraken.Unleashed_Message.Player", ""); }
    public String getPlayerDefeatMessage() { return config.getString("Kraken.Defeated_Message.Killed", ""); }
    public String getPlayerEscapeMessage() { return config.getString("Kraken.Defeated_Message.Escape", ""); }

    /**
     * Checks for valid keys in the advanced.yml file for subskill ranks
     */
    private void checkKeys(List<String> reasons)
    {
        //For now we will only check ranks of stuff I've overhauled
        for(SubSkillType subSkillType : SubSkillType.values())
        {
            if(subSkillType.getParentSkill() == PrimarySkillType.WOODCUTTING)
            {
                //Keeping track of the rank requirements and making sure there are no logical errors
                int curRank = 0;
                int prevRank = 0;

                for(int x = 0; x < subSkillType.getNumRanks(); x++)
                {
                    if(curRank > 0)
                        prevRank = curRank;

                    curRank = getSubSkillUnlockLevel(subSkillType, x);

                    //Do we really care if its below 0? Probably not
                    if(curRank < 0)
                        reasons.add(subSkillType.getAdvConfigAddress() + ".Rank_Levels.Rank_"+curRank+".LevelReq should be above or equal to 0!");

                    if(prevRank > curRank)
                    {
                        //We're going to allow this but we're going to warn them
                        plugin.getLogger().info("You have the ranks for the subskill "+ subSkillType.toString()+" set up poorly, sequential ranks should have ascending requirements");
                    }
                }
            }
        }
    }
}
