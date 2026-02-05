package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.mcMMO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;

public class AdvancedConfig extends BukkitConfig {
    int[] defaultCrippleValues = new int[]{10, 15, 20, 25};
    int[] defaultMomentumValues = new int[]{5, 10, 15, 20, 25, 30, 35, 40, 45, 50};

    public AdvancedConfig(File dataFolder) {
        super("advanced.yml", dataFolder);
        validate();
    }

    @Override
    protected boolean validateKeys() {
        // Validate all the settings!
        List<String> reason = new ArrayList<>();

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
            reason.add(
                    "Skills.Alchemy.Catalysis.MaxSpeed should be at least Skills.Alchemy.Catalysis.MinSpeed!");
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
        if (getAxeMasteryRankDamageMultiplier() < 0) {
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

        if (getImpactChance() < 1) {
            reason.add("Skills.Axes.ArmorImpact.Chance should be at least 1!");
        }

        if (getSkullSplitterModifier() < 1) {
            reason.add("Skills.Axes.SkullSplitter.DamageModifier should be at least 1!");
        }

        /* FISHING */
        /*List<Fishing.Tier> fishingTierList = Arrays.asList(Fishing.Tier.values());

        for (int rank : fishingTierList) {
            if (getFishingTierLevel(tier) < 0) {
                reason.add("Skills.Fishing.Rank_Levels.Rank_" + rank + " should be at least 0!");
            }

            if (getShakeChance(tier) < 0) {
                reason.add("Skills.Fishing.Shake_Chance.Rank_" + rank + " should be at least 0!");
            }

            if (getFishingVanillaXPModifier(tier) < 0) {
                reason.add("Skills.Fishing.VanillaXPMultiplier.Rank_" + rank + " should be at least 0!");
            }

            if (tier != Fishing.Tier.EIGHT) {
                Fishing.Tier nextTier = fishingTierList.get(fishingTierList.indexOf(tier) - 1);

                if (getFishingTierLevel(tier) > getFishingTierLevel(nextTier)) {
                    reason.add("Skills.Fishing.Rank_Levels.Rank_" + rank + " should be less than or equal to Skills.Fishing.Rank_Levels.Rank_" + nextrank + "!");
                }

                if (getShakeChance(tier) > getShakeChance(nextTier)) {
                    reason.add("Skills.Fishing.Shake_Chance.Rank_" + rank + " should be less than or equal to Skills.Fishing.Shake_Chance.Rank_" + nextrank + "!");
                }

                if (getFishingVanillaXPModifier(tier) > getFishingVanillaXPModifier(nextTier)) {
                    reason.add("Skills.Fishing.VanillaXPMultiplier.Rank_" + rank + " should be less than or equal to Skills.Fishing.VanillaXPMultiplier.Rank_" + nextrank + "!");
                }
            }
        }*/

        if (getFishermanDietRankChange() < 1) {
            reason.add("Skills.Fishing.FishermansDiet.RankChange should be at least 1!");
        }

        /*if (getIceFishingUnlockLevel() < 0) {
            reason.add("Skills.Fishing.IceFishing.UnlockLevel should be at least 0!");
        }

        if (getMasterAnglerUnlockLevel() < 0) {
            reason.add("Skills.Fishing.MasterAngler.UnlockLevel should be at least 0!");
        }*/

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

        /*if (getGoreRuptureTicks() < 1) {
            reason.add("Skills.Taming.Gore.RuptureTicks should be at least 1!");
        }*/

        if (getGoreModifier() < 1) {
            reason.add("Skills.Taming.Gore.Modifier should be at least 1!");
        }

        /*if (getFastFoodUnlock() < 0) {
            reason.add("Skills.Taming.FastFood.UnlockLevel should be at least 0!");
        }*/

        if (getFastFoodChance() < 1) {
            reason.add("Skills.Taming.FastFood.Chance should be at least 1!");
        }

        /*if (getEnviromentallyAwareUnlock() < 0) {
            reason.add("Skills.Taming.EnvironmentallyAware.UnlockLevel should be at least 0!");
        }*/

        /*if (getThickFurUnlock() < 0) {
            reason.add("Skills.Taming.ThickFur.UnlockLevel should be at least 0!");
        }*/

        if (getThickFurModifier() < 1) {
            reason.add("Skills.Taming.ThickFur.Modifier should be at least 1!");
        }

        /*if (getHolyHoundUnlock() < 0) {
            reason.add("Skills.Taming.HolyHound.UnlockLevel should be at least 0!");
        }

        if (getShockProofUnlock() < 0) {
            reason.add("Skills.Taming.ShockProof.UnlockLevel should be at least 0!");
        }*/

        if (getShockProofModifier() < 1) {
            reason.add("Skills.Taming.ShockProof.Modifier should be at least 1!");
        }

        /*if (getSharpenedClawsUnlock() < 0) {
            reason.add("Skills.Taming.SharpenedClaws.UnlockLevel should be at least 0!");
        }*/

        if (getSharpenedClawsBonus() < 1) {
            reason.add("Skills.Taming.SharpenedClaws.Bonus should be at least 1!");
        }

        if (getMaxHorseJumpStrength() < 0 || getMaxHorseJumpStrength() > 2) {
            reason.add(
                    "Skills.Taming.CallOfTheWild.MaxHorseJumpStrength should be between 0 and 2!");
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

        return noErrorsInConfig(reason);
    }

    @Override
    protected void loadKeys() {
    }

    /* GENERAL */

    public boolean useAttackCooldown() {
        return config.getBoolean("Skills.General.Attack_Cooldown.Adjust_Skills_For_Attack_Cooldown",
                true);
    }

    public boolean canApplyLimitBreakPVE() {
        return config.getBoolean("Skills.General.LimitBreak.AllowPVE", false);
    }

    public int getStartingLevel() {
        return config.getInt("Skills.General.StartingLevel", 1);
    }

    public boolean allowPlayerTips() {
        return config.getBoolean("Feedback.PlayerTips", true);
    }

    /**
     * This returns the maximum level at which superabilities will stop lengthening from scaling
     * alongside skill level. It returns a different value depending on whether the server is in
     * retro mode
     *
     * @return the level at which abilities stop increasing in length
     */
    public int getAbilityLengthCap() {
        if (!mcMMO.isRetroModeEnabled()) {
            return config.getInt("Skills.General.Ability.Length.Standard.CapLevel", 50);
        } else {
            return config.getInt("Skills.General.Ability.Length.RetroMode.CapLevel", 500);
        }
    }

    /**
     * This returns the frequency at which abilities will increase in length It returns a different
     * value depending on whether the server is in retro mode
     *
     * @return the number of levels required per ability length increase
     */
    public int getAbilityLength() {
        if (!mcMMO.isRetroModeEnabled()) {
            return config.getInt("Skills.General.Ability.Length.Standard.IncreaseLevel", 5);
        } else {
            return config.getInt("Skills.General.Ability.Length.RetroMode.IncreaseLevel", 50);
        }
    }

    public int getEnchantBuff() {
        return config.getInt("Skills.General.Ability.EnchantBuff", 5);
    }

    /**
     * Grabs the max bonus level for a skill used in RNG calculations All max level values in the
     * config are multiplied by 10 if the server is in retro mode as the values in the config are
     * based around the new 1-100 skill system scaling A value of 10 in the file will be returned as
     * 100 for retro mode servers to accommodate the change in scaling
     *
     * @param subSkillType target subskill
     * @return the level at which this skills max benefits will be reached on the curve
     */
    public int getMaxBonusLevel(SubSkillType subSkillType) {
        String keyPath = subSkillType.getAdvConfigAddress() + ".MaxBonusLevel.";
        return mcMMO.isRetroModeEnabled() ? config.getInt(keyPath + "RetroMode", 1000)
                : config.getInt(
                        keyPath + "Standard", 100);
    }

    public int getMaxBonusLevel(AbstractSubSkill abstractSubSkill) {
        return getMaxBonusLevel(abstractSubSkill.getSubSkillType());
    }

    public double getMaximumProbability(SubSkillType subSkillType) {

        return config.getDouble(subSkillType.getAdvConfigAddress() + ".ChanceMax", 100.0D);
    }

    public double getMaximumProbability(AbstractSubSkill abstractSubSkill) {
        return getMaximumProbability(abstractSubSkill.getSubSkillType());
    }

    /* Notification Settings */

    public boolean doesSkillCommandSendBlankLines() {
        return config.getBoolean("Feedback.SkillCommand.BlankLinesAboveHeader", true);
    }

    public boolean doesNotificationUseActionBar(NotificationType notificationType) {
        return config.getBoolean(
                "Feedback.ActionBarNotifications." + notificationType.toString() + ".Enabled",
                true);
    }

    public boolean doesNotificationSendCopyToChat(NotificationType notificationType) {
        return config.getBoolean(
                "Feedback.ActionBarNotifications." + notificationType.toString()
                        + ".SendCopyOfMessageToChat", false);
    }

    public boolean useTitlesForXPEvent() {
        return config.getBoolean("Feedback.Events.XP.SendTitles", true);
    }

    public boolean sendAbilityNotificationToOtherPlayers() {
        return config.getBoolean("Feedback.Events.AbilityActivation.SendNotificationToOtherPlayers",
                true);
    }

    private ChatColor getChatColorFromKey(String keyLocation) {
        String colorName = config.getString(keyLocation);

        return getChatColor(colorName);
    }

    private ChatColor getChatColor(String configColor) {
        for (ChatColor chatColor : ChatColor.values()) {
            if (configColor.equalsIgnoreCase(chatColor.getName())) {
                return chatColor;
            }
        }

        //Invalid Color
        mcMMO.p.getLogger().warning(configColor + " is an invalid color value");
        return ChatColor.WHITE;
    }

    /* ACROBATICS */
    public double getDodgeDamageModifier() {
        return config.getDouble("Skills.Acrobatics.Dodge.DamageModifier", 2.0D);
    }

    public double getRollDamageThreshold() {
        return config.getDouble("Skills.Acrobatics.Roll.DamageThreshold", 7.0D);
    }

    public double getGracefulRollDamageThreshold() {
        return config.getDouble("Skills.Acrobatics.GracefulRoll.DamageThreshold", 14.0D);
    }

    /* ALCHEMY */
    public int getCatalysisMaxBonusLevel() {
        if (mcMMO.isRetroModeEnabled()) {
            return config.getInt("Skills.Alchemy.Catalysis.MaxBonusLevel.RetroMode", 1000);
        } else {
            return config.getInt("Skills.Alchemy.Catalysis.MaxBonusLevel.Standard", 100);
        }
    }

    public double getCatalysisMinSpeed() {
        return config.getDouble("Skills.Alchemy.Catalysis.MinSpeed", 1.0D);
    }

    public double getCatalysisMaxSpeed() {
        return config.getDouble("Skills.Alchemy.Catalysis.MaxSpeed", 4.0D);
    }


    /* ARCHERY */
    public double getSkillShotRankDamageMultiplier() {
        return config.getDouble("Skills.Archery.SkillShot.RankDamageMultiplier", 10.0D);
    }

    public double getSkillShotDamageMax() {
        return config.getDouble("Skills.Archery.SkillShot.MaxDamage", 9.0D);
    }

    public double getDazeBonusDamage() {
        return config.getDouble("Skills.Archery.Daze.BonusDamage", 4.0D);
    }

    public double getForceMultiplier() {
        return config.getDouble("Skills.Archery.ForceMultiplier", 2.0D);
    }

    /* AXES */
    public double getAxeMasteryRankDamageMultiplier() {
        return config.getDouble("Skills.Axes.AxeMastery.RankDamageMultiplier", 1.0D);
    }

    public double getCriticalStrikesPVPModifier() {
        return config.getDouble("Skills.Axes.CriticalStrikes.PVP_Modifier", 1.5D);
    }

    public double getCriticalStrikesPVEModifier() {
        return config.getDouble("Skills.Axes.CriticalStrikes.PVE_Modifier", 2.0D);
    }

    public double getGreaterImpactChance() {
        return config.getDouble("Skills.Axes.GreaterImpact.Chance", 25.0D);
    }

    public double getGreaterImpactModifier() {
        return config.getDouble("Skills.Axes.GreaterImpact.KnockbackModifier", 1.5D);
    }

    public double getGreaterImpactBonusDamage() {
        return config.getDouble("Skills.Axes.GreaterImpact.BonusDamage", 2.0D);
    }

    public double getImpactChance() {
        return config.getDouble("Skills.Axes.ArmorImpact.Chance", 25.0D);
    }

    public double getImpactDurabilityDamageMultiplier() {
        return config.getDouble("Skills.Axes.ArmorImpact.DamagePerRank", 6.5D);
    }

    public double getSkullSplitterModifier() {
        return config.getDouble("Skills.Axes.SkullSplitter.DamageModifier", 2.0D);
    }

    /* CROSSBOWS */
    public double getPoweredShotRankDamageMultiplier() {
        return config.getDouble("Skills.Crossbows.PoweredShot.RankDamageMultiplier", 10.0D);
    }

    public double getPoweredShotDamageMax() {
        return config.getDouble("Skills.Archery.SkillShot.MaxDamage", 9.0D);
    }

    /* EXCAVATION */
    //Nothing to configure, everything is already configurable in config.yml

    /* FISHING */
    public double getShakeChance(int rank) {
        return config.getDouble("Skills.Fishing.ShakeChance.Rank_" + rank);
    }

    public int getFishingVanillaXPModifier(int rank) {
        return config.getInt("Skills.Fishing.VanillaXPMultiplier.Rank_" + rank);
    }

    public int getFishingReductionMinWaitTicks() {
        return config.getInt("Skills.Fishing.MasterAngler.Tick_Reduction_Per_Rank.Min_Wait", 10);
    }

    public int getFishingReductionMaxWaitTicks() {
        return config.getInt("Skills.Fishing.MasterAngler.Tick_Reduction_Per_Rank.Max_Wait", 30);
    }

    public int getFishingBoatReductionMinWaitTicks() {
        return config.getInt("Skills.Fishing.MasterAngler.Boat_Tick_Reduction.Min_Wait", 10);
    }

    public int getFishingBoatReductionMaxWaitTicks() {
        return config.getInt("Skills.Fishing.MasterAngler.Boat_Tick_Reduction.Max_Wait", 30);
    }

    public int getFishingReductionMinWaitCap() {
        return config.getInt("Skills.Fishing.MasterAngler.Tick_Reduction_Caps.Min_Wait", 40);
    }

    public int getFishingReductionMaxWaitCap() {
        return config.getInt("Skills.Fishing.MasterAngler.Tick_Reduction_Caps.Max_Wait", 100);
    }

    public int getFishermanDietRankChange() {
        return config.getInt("Skills.Fishing.FishermansDiet.RankChange", 200);
    }


    public double getMasterAnglerBoatModifier() {
        return config.getDouble("Skills.Fishing.MasterAngler.BoatModifier", 2.0);
    }

    public double getMasterAnglerBiomeModifier() {
        return config.getDouble("Skills.Fishing.MasterAngler.BiomeModifier", 2.0);
    }

    /* HERBALISM */
    public int getFarmerDietRankChange() {
        return config.getInt("Skills.Herbalism.FarmersDiet.RankChange", 200);
    }

    public int getGreenThumbStageChange() {
        return config.getInt("Skills.Herbalism.GreenThumb.StageChange", 200);
    }

    /* MINING */
    public boolean getDoubleDropSilkTouchEnabled() {
        return config.getBoolean("Skills.Mining.DoubleDrops.SilkTouch", true);
    }

    public boolean getAllowMiningTripleDrops() {
        return config.getBoolean("Skills.Mining.SuperBreaker.AllowTripleDrops", true);
    }

    public int getBlastMiningRankLevel(int rank) {
        return config.getInt("Skills.Mining.BlastMining.Rank_Levels.Rank_" + rank);
    }

    public double getBlastDamageDecrease(int rank) {
        return config.getDouble("Skills.Mining.BlastMining.BlastDamageDecrease.Rank_" + rank);
    }

    public double getOreBonus(int rank) {
        return config.getDouble("Skills.Mining.BlastMining.OreBonus.Rank_" + rank);
    }

    public boolean isBlastMiningBonusDropsEnabled() {
        return config.getBoolean("Skills.Mining.BlastMining.Bonus_Drops.Enabled", true);
    }

    public double getDebrisReduction(int rank) {
        return config.getDouble("Skills.Mining.BlastMining.DebrisReduction.Rank_" + rank);
    }

    public int getDropMultiplier(int rank) {
        return config.getInt("Skills.Mining.BlastMining.DropMultiplier.Rank_" + rank);
    }

    public double getBlastRadiusModifier(int rank) {
        return config.getDouble("Skills.Mining.BlastMining.BlastRadiusModifier.Rank_" + rank);
    }

    /* REPAIR */
    public double getRepairMasteryMaxBonus() {
        return config.getDouble("Skills.Repair.RepairMastery.MaxBonusPercentage", 200.0D);
    }

    public int getRepairMasteryMaxLevel() {
        return config.getInt("Skills.Repair.RepairMastery.MaxBonusLevel", 100);
    }

    public boolean getAllowEnchantedRepairMaterials() {
        return config.getBoolean("Skills.Repair.Use_Enchanted_Materials", false);
    }

    public boolean getArcaneForgingEnchantLossEnabled() {
        return config.getBoolean("Skills.Repair.ArcaneForging.May_Lose_Enchants", true);
    }

    public double getArcaneForgingKeepEnchantsChance(int rank) {
        return config.getDouble("Skills.Repair.ArcaneForging.Keep_Enchants_Chance.Rank_" + rank);
    }

    public boolean getArcaneForgingDowngradeEnabled() {
        return config.getBoolean("Skills.Repair.ArcaneForging.Downgrades_Enabled", true);
    }

    public double getArcaneForgingDowngradeChance(int rank) {
        return config.getDouble("Skills.Repair.ArcaneForging.Downgrades_Chance.Rank_" + rank);
    }

    public boolean getArcaneSalvageEnchantDowngradeEnabled() {
        return config.getBoolean("Skills.Salvage.ArcaneSalvage.EnchantDowngradeEnabled", true);
    }

    public boolean getArcaneSalvageEnchantLossEnabled() {
        return config.getBoolean("Skills.Salvage.ArcaneSalvage.EnchantLossEnabled", true);
    }

    public double getArcaneSalvageExtractFullEnchantsChance(int rank) {
        return config.getDouble("Skills.Salvage.ArcaneSalvage.ExtractFullEnchant.Rank_" + rank);
    }

    public double getArcaneSalvageExtractPartialEnchantsChance(int rank) {
        return config.getDouble("Skills.Salvage.ArcaneSalvage.ExtractPartialEnchant.Rank_" + rank);
    }

    /* SMELTING */
    public int getBurnModifierMaxLevel() {
        if (mcMMO.isRetroModeEnabled()) {
            return config.getInt("Skills.Smelting.FuelEfficiency.RetroMode.MaxBonusLevel", 1000);
        } else {
            return config.getInt("Skills.Smelting.FuelEfficiency.Standard.MaxBonusLevel", 100);
        }
    }

    public double getFluxMiningChance() {
        return config.getDouble("Skills.Smelting.FluxMining.Chance", 33.0D);
    }

    /* SWORDS */
    public double getStabBaseDamage() {
        return config.getDouble("Skills.Swords.Stab.Base_Damage", 1.0D);
    }

    public double getStabPerRankMultiplier() {
        return config.getDouble("Skills.Swords.Stab.Per_Rank_Multiplier", 1.5D);
    }

    public double getRuptureTickDamage(boolean isTargetPlayer, int rank) {
        String root = "Skills.Swords.Rupture.Rupture_Mechanics.Tick_Interval_Damage.Against_";
        String targetType = isTargetPlayer ? "Players" : "Mobs";
        String key = root + targetType + ".Rank_" + rank;

        return config.getDouble(key, 1.0D);
    }

    public int getRuptureDurationSeconds(boolean isTargetPlayer) {
        String root = "Skills.Swords.Rupture.Rupture_Mechanics.Duration_In_Seconds.Against_";
        String targetType = isTargetPlayer ? "Players" : "Mobs";
        return config.getInt(root + targetType, 5);
    }

    public double getRuptureExplosionDamage(boolean isTargetPlayer, int rank) {
        String root = "Skills.Swords.Rupture.Rupture_Mechanics.Explosion_Damage.Against_";
        String targetType = isTargetPlayer ? "Players" : "Mobs";
        String key = root + targetType + ".Rank_" + rank;

        return config.getDouble(key, 40.0D);
    }

    public double getRuptureChanceToApplyOnHit(int rank) {
        String root = "Skills.Swords.Rupture.Rupture_Mechanics.Chance_To_Apply_On_Hit.Rank_";
        return config.getDouble(root + rank, 33);
    }

    public double getCounterModifier() {
        return config.getDouble("Skills.Swords.CounterAttack.DamageModifier", 2.0D);
    }

    public double getSerratedStrikesModifier() {
        return config.getDouble("Skills.Swords.SerratedStrikes.DamageModifier", 4.0D);
    }

    public int getSerratedStrikesTicks() {
        return config.getInt("Skills.Swords.SerratedStrikes.RuptureTicks", 5);
    }

    /* TAMING */
    public double getGoreModifier() {
        return config.getDouble("Skills.Taming.Gore.Modifier", 2.0D);
    }

    public double getFastFoodChance() {
        return config.getDouble("Skills.Taming.FastFoodService.Chance", 50.0D);
    }

    public double getPummelChance() {
        return config.getDouble("Skills.Taming.Pummel.Chance", 10.0D);
    }

    public double getThickFurModifier() {
        return config.getDouble("Skills.Taming.ThickFur.Modifier", 2.0D);
    }

    public double getShockProofModifier() {
        return config.getDouble("Skills.Taming.ShockProof.Modifier", 6.0D);
    }

    public double getSharpenedClawsBonus() {
        return config.getDouble("Skills.Taming.SharpenedClaws.Bonus", 2.0D);
    }

    public double getMinHorseJumpStrength() {
        return config.getDouble("Skills.Taming.CallOfTheWild.MinHorseJumpStrength", 0.7D);
    }

    public double getMaxHorseJumpStrength() {
        return config.getDouble("Skills.Taming.CallOfTheWild.MaxHorseJumpStrength", 2.0D);
    }

    /* UNARMED */
    public boolean isSteelArmDamageCustom() {
        return config.getBoolean("Skills.Unarmed.SteelArmStyle.Damage_Override", false);
    }

    public double getSteelArmOverride(int rank, double def) {
        String key = "Rank_" + rank;
        return config.getDouble("Skills.Unarmed.SteelArmStyle.Override." + key, def);
    }

    public boolean getDisarmProtected() {
        return config.getBoolean("Skills.Unarmed.Disarm.AntiTheft", false);
    }

    /* WOODCUTTING */
    public boolean isKnockOnWoodXPOrbEnabled() {
        return config.getBoolean("Skills.Woodcutting.TreeFeller.Knock_On_Wood.Add_XP_Orbs_To_Drops",
                true);
    }

    /* MACES */
    public double getCrippleChanceToApplyOnHit(int rank) {
        return config.getDouble("Skills.Maces.Cripple.Chance_To_Apply_On_Hit.Rank_" + rank,
                defaultCrippleValues[rank - 1]);
    }

    /* SPEARS */
    public double getMomentumChanceToApplyOnHit(int rank) {
        return config.getDouble("Skills.Spears.Momentum.Chance_To_Apply_On_Hit.Rank_" + rank,
                defaultMomentumValues[rank - 1]);
    }

    public double getSpearMasteryRankDamageMultiplier() {
        return config.getDouble("Skills.Spears.SpearMastery.Rank_Damage_Multiplier", 0.4D);
    }
}
