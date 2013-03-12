package com.gmail.nossr50.commands.skills;

import org.bukkit.Material;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.herbalism.Herbalism;
import com.gmail.nossr50.util.Permissions;

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
        super(SkillType.HERBALISM);
    }

    @Override
    protected void dataCalculations() {
        // GREEN TERRA
        if (canGreenTerra) {
            String[] greenTerraStrings = calculateLengthDisplayValues();
            greenTerraLength = greenTerraStrings[0];
            greenTerraLengthEndurance = greenTerraStrings[1];
        }

        // FARMERS DIET
        if (canFarmersDiet) {
            farmersDietRank = calculateRank(Herbalism.farmersDietMaxLevel, Herbalism.farmersDietRankLevel1);
        }

        // GREEN THUMB
        if (canGreenThumbBlocks || canGreenThumbPlants) {
            greenThumbStage = calculateRank(Herbalism.greenThumbStageMaxLevel, Herbalism.greenThumbStageChangeLevel);

            String[] greenThumbStrings = calculateAbilityDisplayValues(Herbalism.greenThumbMaxLevel, Herbalism.greenThumbMaxChance);
            greenThumbChance = greenThumbStrings[0];
            greenThumbChanceLucky = greenThumbStrings[1];
        }

        // DOUBLE DROPS
        if (canDoubleDrop) {
            String[] doubleDropStrings = calculateAbilityDisplayValues(Herbalism.doubleDropsMaxLevel, Herbalism.doubleDropsMaxChance);
            doubleDropChance = doubleDropStrings[0];
            doubleDropChanceLucky = doubleDropStrings[1];
        }

        // HYLIAN LUCK
        if (hasHylianLuck) {
            String[] hylianLuckStrings = calculateAbilityDisplayValues(Herbalism.hylianLuckMaxLevel, Herbalism.hylianLuckMaxChance);
            hylianLuckChance = hylianLuckStrings[0];
            hylianLuckChanceLucky = hylianLuckStrings[1];
        }

        // SHROOM THUMB
        if (canShroomThumb) {
            String[] shroomThumbStrings = calculateAbilityDisplayValues(Herbalism.shroomThumbMaxLevel, Herbalism.shroomThumbMaxChance);
            shroomThumbChance = shroomThumbStrings[0];
            shroomThumbChanceLucky = shroomThumbStrings[1];
        }
    }

    @Override
    protected void permissionsCheck() {
        hasHylianLuck = Permissions.hylianLuck(player);
        canGreenTerra = Permissions.greenTerra(player);
        canGreenThumbPlants = Permissions.greenThumbPlant(player, Material.CROPS) || Permissions.greenThumbPlant(player, Material.CARROT) || Permissions.greenThumbPlant(player, Material.POTATO) || Permissions.greenThumbPlant(player, Material.NETHER_WARTS) || Permissions.greenThumbPlant(player, Material.COCOA);
        canGreenThumbBlocks = Permissions.greenThumbBlock(player, Material.DIRT) || Permissions.greenThumbBlock(player, Material.COBBLESTONE) || Permissions.greenThumbBlock(player, Material.COBBLE_WALL) || Permissions.greenThumbBlock(player, Material.SMOOTH_BRICK);
        canFarmersDiet = Permissions.farmersDiet(player);
        canDoubleDrop = Permissions.doubleDrops(player, skill) && !skill.getDoubleDropsDisabled();
        canShroomThumb = Permissions.shroomThumb(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canGreenTerra || canDoubleDrop || canFarmersDiet || canGreenThumbBlocks || canGreenThumbPlants || canShroomThumb;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canGreenTerra) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.0"), LocaleLoader.getString("Herbalism.Effect.1")));
        }

        if (canGreenThumbPlants) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.2"), LocaleLoader.getString("Herbalism.Effect.3")));
        }

        if (canGreenThumbBlocks) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.4"), LocaleLoader.getString("Herbalism.Effect.5")));
        }

        if (canFarmersDiet) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.6"), LocaleLoader.getString("Herbalism.Effect.7")));
        }

        if (hasHylianLuck) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.10"), LocaleLoader.getString("Herbalism.Effect.11")));
        }

        if (canShroomThumb) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.12"), LocaleLoader.getString("Herbalism.Effect.13")));
        }

        if (canDoubleDrop) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.8"), LocaleLoader.getString("Herbalism.Effect.9")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canGreenTerra || canDoubleDrop || canFarmersDiet || canGreenThumbBlocks || canGreenThumbPlants || canShroomThumb;
    }

    @Override
    protected void statsDisplay() {
        if (canGreenTerra) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.Length", greenTerraLength) + (hasEndurance ? LocaleLoader.getString("Perks.activationtime.bonus", greenTerraLengthEndurance) : ""));
        }

        if (canGreenThumbBlocks || canGreenThumbPlants) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Chance", greenThumbChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", greenThumbChanceLucky) : ""));
        }

        if (canGreenThumbPlants) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Stage", greenThumbStage));
        }

        if (canFarmersDiet) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.FD", farmersDietRank));
        }

        if (hasHylianLuck) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.HylianLuck", hylianLuckChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", hylianLuckChanceLucky) : ""));
        }

        if (canShroomThumb) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.ShroomThumb.Chance", shroomThumbChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", shroomThumbChanceLucky) : ""));
        }

        if (canDoubleDrop) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.DoubleDropChance", doubleDropChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", doubleDropChanceLucky) : ""));
        }
    }
}
