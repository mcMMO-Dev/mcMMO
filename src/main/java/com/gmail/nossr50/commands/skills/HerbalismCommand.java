package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.herbalism.Herbalism;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.skills.RankUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // GREEN TERRA
        if (canGreenTerra) {
            String[] greenTerraStrings = calculateLengthDisplayValues(player, skillValue);
            greenTerraLength = greenTerraStrings[0];
            greenTerraLengthEndurance = greenTerraStrings[1];
        }

        // FARMERS DIET
        if (canFarmersDiet) {
            farmersDietRank = calculateRank(skillValue, Herbalism.farmersDietMaxLevel, Herbalism.farmersDietRankLevel1);
        }

        // GREEN THUMB
        if (canGreenThumbBlocks || canGreenThumbPlants) {
            greenThumbStage = calculateRank(skillValue, Herbalism.greenThumbStageMaxLevel, Herbalism.greenThumbStageChangeLevel);

            String[] greenThumbStrings = calculateAbilityDisplayValues(skillValue, SubSkillType.HERBALISM_GREEN_THUMB, isLucky);
            greenThumbChance = greenThumbStrings[0];
            greenThumbChanceLucky = greenThumbStrings[1];
        }

        // DOUBLE DROPS
        if (canDoubleDrop) {
            String[] doubleDropStrings = calculateAbilityDisplayValues(skillValue, SubSkillType.HERBALISM_DOUBLE_DROPS, isLucky);
            doubleDropChance = doubleDropStrings[0];
            doubleDropChanceLucky = doubleDropStrings[1];
        }

        // HYLIAN LUCK
        if (hasHylianLuck) {
            String[] hylianLuckStrings = calculateAbilityDisplayValues(skillValue, SubSkillType.HERBALISM_HYLIAN_LUCK, isLucky);
            hylianLuckChance = hylianLuckStrings[0];
            hylianLuckChanceLucky = hylianLuckStrings[1];
        }

        // SHROOM THUMB
        if (canShroomThumb) {
            String[] shroomThumbStrings = calculateAbilityDisplayValues(skillValue, SubSkillType.HERBALISM_SHROOM_THUMB, isLucky);
            shroomThumbChance = shroomThumbStrings[0];
            shroomThumbChanceLucky = shroomThumbStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        hasHylianLuck = canUseSubskill(player, SubSkillType.HERBALISM_HYLIAN_LUCK);
        canGreenTerra = Permissions.greenTerra(player);
        canGreenThumbPlants = RankUtils.hasUnlockedSubskill(player, SubSkillType.HERBALISM_GREEN_THUMB) && Permissions.greenThumbPlant(player, Material.WHEAT) || Permissions.greenThumbPlant(player, Material.CARROT) || Permissions.greenThumbPlant(player, Material.POTATO) || Permissions.greenThumbPlant(player, Material.BEETROOT) || Permissions.greenThumbPlant(player, Material.NETHER_WART) || Permissions.greenThumbPlant(player, Material.COCOA);
        canGreenThumbBlocks = RankUtils.hasUnlockedSubskill(player, SubSkillType.HERBALISM_GREEN_THUMB) && Permissions.greenThumbBlock(player, Material.DIRT) || Permissions.greenThumbBlock(player, Material.COBBLESTONE) || Permissions.greenThumbBlock(player, Material.COBBLESTONE_WALL) || Permissions.greenThumbBlock(player, Material.STONE_BRICKS);
        canFarmersDiet = canUseSubskill(player, SubSkillType.HERBALISM_FARMERS_DIET);
        canDoubleDrop = canUseSubskill(player, SubSkillType.HERBALISM_DOUBLE_DROPS) && !skill.getDoubleDropsDisabled();
        canShroomThumb = canUseSubskill(player, SubSkillType.HERBALISM_SHROOM_THUMB);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canGreenTerra) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.0"), LocaleLoader.getString("Herbalism.Effect.1")));
        }

        if (canGreenThumbPlants) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.2"), LocaleLoader.getString("Herbalism.Effect.3")));
        }

        if (canGreenThumbBlocks) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.4"), LocaleLoader.getString("Herbalism.Effect.5")));
        }

        if (canFarmersDiet) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.6"), LocaleLoader.getString("Herbalism.Effect.7")));
        }

        if (hasHylianLuck) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.10"), LocaleLoader.getString("Herbalism.Effect.11")));
        }

        if (canShroomThumb) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.12"), LocaleLoader.getString("Herbalism.Effect.13")));
        }

        if (canDoubleDrop) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.8"), LocaleLoader.getString("Herbalism.Effect.9")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canGreenTerra) {
            messages.add(LocaleLoader.getString("Herbalism.Ability.GTe.Length", greenTerraLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", greenTerraLengthEndurance) : ""));
        }

        if (canGreenThumbBlocks || canGreenThumbPlants) {
            messages.add(LocaleLoader.getString("Herbalism.Ability.GTh.Chance", greenThumbChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", greenThumbChanceLucky) : ""));
        }

        if (canGreenThumbPlants) {
            messages.add(LocaleLoader.getString("Herbalism.Ability.GTh.Stage", greenThumbStage));
        }

        if (canFarmersDiet) {
            messages.add(LocaleLoader.getString("Herbalism.Ability.FD", farmersDietRank));
        }

        if (hasHylianLuck) {
            messages.add(LocaleLoader.getString("Herbalism.Ability.HylianLuck", hylianLuckChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", hylianLuckChanceLucky) : ""));
        }

        if (canShroomThumb) {
            messages.add(LocaleLoader.getString("Herbalism.Ability.ShroomThumb.Chance", shroomThumbChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", shroomThumbChanceLucky) : ""));
        }

        if (canDoubleDrop) {
            messages.add(LocaleLoader.getString("Herbalism.Ability.DoubleDropChance", doubleDropChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", doubleDropChanceLucky) : ""));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.HERBALISM);

        return textComponents;
    }
}
