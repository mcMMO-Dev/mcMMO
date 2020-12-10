package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HerbalismCommand extends SkillCommand {
    private String greenTerraLength;
    private String greenTerraLengthEndurance;
    private String greenThumbChance;
    private String greenThumbChanceLucky;
    private int greenThumbStage;
    private int farmersDietRank;
    private String doubleDropChance;
    private String doubleDropChanceLucky;
    private String hylianLuckChance;
    private String hylianLuckChanceLucky;
    private String shroomThumbChance;
    private String shroomThumbChanceLucky;

    private boolean hasHylianLuck;
    private boolean canGreenTerra;
    private boolean canGreenThumbPlants;
    private boolean canGreenThumbBlocks;
    private boolean canFarmersDiet;
    private boolean canDoubleDrop;
    private boolean canShroomThumb;

    public HerbalismCommand() {
        super(PrimarySkillType.HERBALISM);
    }

    @Override
    protected void dataCalculations(@NotNull McMMOPlayer mmoPlayer, float skillValue) {
        
        // DOUBLE DROPS
        if (canDoubleDrop) {
            String[] doubleDropStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, mmoPlayer, SubSkillType.HERBALISM_DOUBLE_DROPS);
            doubleDropChance = doubleDropStrings[0];
            doubleDropChanceLucky = doubleDropStrings[1];
        }
        
        // FARMERS DIET
        if (canFarmersDiet) {
            farmersDietRank = RankUtils.getRank(mmoPlayer, SubSkillType.HERBALISM_FARMERS_DIET);
        }
        
        // GREEN TERRA
        if (canGreenTerra) {
            String[] greenTerraStrings = calculateLengthDisplayValues(mmoPlayer, skillValue);
            greenTerraLength = greenTerraStrings[0];
            greenTerraLengthEndurance = greenTerraStrings[1];
        }

        // GREEN THUMB
        if (canGreenThumbBlocks || canGreenThumbPlants) {
            greenThumbStage = RankUtils.getRank(mmoPlayer, SubSkillType.HERBALISM_GREEN_THUMB);

            String[] greenThumbStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, mmoPlayer, SubSkillType.HERBALISM_GREEN_THUMB);
            greenThumbChance = greenThumbStrings[0];
            greenThumbChanceLucky = greenThumbStrings[1];
        }

        // HYLIAN LUCK
        if (hasHylianLuck) {
            String[] hylianLuckStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, mmoPlayer, SubSkillType.HERBALISM_HYLIAN_LUCK);
            hylianLuckChance = hylianLuckStrings[0];
            hylianLuckChanceLucky = hylianLuckStrings[1];
        }

        // SHROOM THUMB
        if (canShroomThumb) {
            String[] shroomThumbStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, mmoPlayer, SubSkillType.HERBALISM_SHROOM_THUMB);
            shroomThumbChance = shroomThumbStrings[0];
            shroomThumbChanceLucky = shroomThumbStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(@NotNull McMMOPlayer mmoPlayer) {
        hasHylianLuck = canUseSubskill(mmoPlayer, SubSkillType.HERBALISM_HYLIAN_LUCK);
        canGreenTerra = Permissions.greenTerra(mmoPlayer.getPlayer());
        canGreenThumbPlants = RankUtils.hasUnlockedSubskill(mmoPlayer, SubSkillType.HERBALISM_GREEN_THUMB)
                && (Permissions.greenThumbPlant(mmoPlayer.getPlayer(), Material.WHEAT)
                || Permissions.greenThumbPlant(mmoPlayer.getPlayer(), Material.CARROT)
                || Permissions.greenThumbPlant(mmoPlayer.getPlayer(), Material.POTATO)
                || Permissions.greenThumbPlant(mmoPlayer.getPlayer(), Material.BEETROOT)
                || Permissions.greenThumbPlant(mmoPlayer.getPlayer(), Material.NETHER_WART)
                || Permissions.greenThumbPlant(mmoPlayer.getPlayer(), Material.COCOA));
        canGreenThumbBlocks = RankUtils.hasUnlockedSubskill(mmoPlayer, SubSkillType.HERBALISM_GREEN_THUMB)
                && (Permissions.greenThumbBlock(mmoPlayer.getPlayer(), Material.DIRT)
                || Permissions.greenThumbBlock(mmoPlayer.getPlayer(), Material.COBBLESTONE)
                || Permissions.greenThumbBlock(mmoPlayer.getPlayer(), Material.COBBLESTONE_WALL)
                || Permissions.greenThumbBlock(mmoPlayer.getPlayer(), Material.STONE_BRICKS));
        canFarmersDiet = canUseSubskill(mmoPlayer, SubSkillType.HERBALISM_FARMERS_DIET);
        canDoubleDrop = canUseSubskill(mmoPlayer, SubSkillType.HERBALISM_DOUBLE_DROPS) && !skill.getDoubleDropsDisabled();
        canShroomThumb = canUseSubskill(mmoPlayer, SubSkillType.HERBALISM_SHROOM_THUMB);
    }

    @Override
    protected @NotNull List<String> statsDisplay(@NotNull McMMOPlayer mmoPlayer, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canDoubleDrop) {
            messages.add(getStatMessage(SubSkillType.HERBALISM_DOUBLE_DROPS, doubleDropChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", doubleDropChanceLucky) : ""));
        }
        
        if (canFarmersDiet) {
            messages.add(getStatMessage(false, true, SubSkillType.HERBALISM_FARMERS_DIET, String.valueOf(farmersDietRank)));
        }
        
        if (canGreenTerra) {
            messages.add(getStatMessage(SubSkillType.HERBALISM_GREEN_TERRA, greenTerraLength)
                    + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", greenTerraLengthEndurance) : ""));

            //messages.add(LocaleLoader.getString("Herbalism.Ability.GTe.Length", greenTerraLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", greenTerraLengthEndurance) : ""));
        }

        if (canGreenThumbBlocks || canGreenThumbPlants) {
            messages.add(getStatMessage(SubSkillType.HERBALISM_GREEN_THUMB, greenThumbChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", greenThumbChanceLucky) : ""));
            //messages.add(LocaleLoader.getString("Herbalism.Ability.GTh.Chance", greenThumbChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", greenThumbChanceLucky) : ""));
        }

        if (canGreenThumbPlants) {
            messages.add(getStatMessage(true, true,SubSkillType.HERBALISM_GREEN_THUMB, String.valueOf(greenThumbStage)));
        }

        if (hasHylianLuck) {
            messages.add(getStatMessage(SubSkillType.HERBALISM_HYLIAN_LUCK, hylianLuckChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", hylianLuckChanceLucky) : ""));
        }

        if (canShroomThumb) {
            messages.add(getStatMessage(SubSkillType.HERBALISM_SHROOM_THUMB, shroomThumbChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", shroomThumbChanceLucky) : ""));
        }

        return messages;
    }

    @Override
    protected @NotNull List<Component> getTextComponents(@NotNull McMMOPlayer mmoPlayer) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(mmoPlayer, textComponents, PrimarySkillType.HERBALISM);

        return textComponents;
    }
}
