package com.gmail.nossr50.skills.herbalism;

import org.bukkit.Material;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillCommand;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Permissions;

public class HerbalismCommand extends SkillCommand {
    private String greenTerraLength;
    private String greenTerraLengthEndurance;
    private String greenThumbChance;
    private String greenThumbChanceLucky;
    private String greenThumbStage;
    private String farmersDietRank;
    private String doubleDropChance;
    private String doubleDropChanceLucky;
    private String hylianLuckChance;
    private String hylianLuckChanceLucky;

    private boolean hasHylianLuck;
    private boolean canGreenTerra;
    private boolean canGreenThumbWheat;
    private boolean canGreenThumbBlocks;
    private boolean canFarmersDiet;
    private boolean canDoubleDrop;
    private boolean doubleDropsDisabled;

    public HerbalismCommand() {
        super(SkillType.HERBALISM);
    }

    @Override
    protected void dataCalculations() {
        //GREEN TERRA
        String[] greenTerraStrings = calculateLengthDisplayValues();
        greenTerraLength = greenTerraStrings[0];
        greenTerraLengthEndurance = greenTerraStrings[1];

        //FARMERS DIET
        farmersDietRank = calculateRank(Herbalism.farmersDietMaxLevel, Herbalism.farmersDietRankLevel1);

        //GREEN THUMB
        greenThumbStage = calculateRank(Herbalism.greenThumbStageMaxLevel, Herbalism.greenThumbStageChangeLevel);

        String[] greenThumbStrings = calculateAbilityDisplayValues(Herbalism.greenThumbMaxLevel, Herbalism.greenThumbMaxChance);
        greenThumbChance = greenThumbStrings[0];
        greenThumbChanceLucky = greenThumbStrings[1];

        //DOUBLE DROPS
        String[] doubleDropStrings = calculateAbilityDisplayValues(Herbalism.doubleDropsMaxLevel, Herbalism.doubleDropsMaxChance);
        doubleDropChance = doubleDropStrings[0];
        doubleDropChanceLucky = doubleDropStrings[1];

        //HYLIAN LUCK
        String[] hylianLuckStrings = calculateAbilityDisplayValues(Herbalism.hylianLuckMaxLevel, Herbalism.hylianLuckMaxChance);
        hylianLuckChance = hylianLuckStrings[0];
        hylianLuckChanceLucky = hylianLuckStrings[1];
    }

    @Override
    protected void permissionsCheck() {
        hasHylianLuck = Permissions.hylianLuck(player);
        canGreenTerra = Permissions.greenTerra(player);
        canGreenThumbWheat = Permissions.greenThumbPlant(player, Material.CROPS); //TODO: This isn't really accurate - they could have perms for other crops but not wheat.
        canGreenThumbBlocks = (Permissions.greenThumbBlock(player, Material.DIRT) || Permissions.greenThumbBlock(player, Material.COBBLESTONE) || Permissions.greenThumbBlock(player, Material.COBBLE_WALL) || Permissions.greenThumbBlock(player, Material.SMOOTH_BRICK));
        canFarmersDiet = Permissions.farmersDiet(player);
        canDoubleDrop = Permissions.doubleDrops(player, skill);
        doubleDropsDisabled = skill.getDoubleDropsDisabled();
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canGreenTerra || (canDoubleDrop && !doubleDropsDisabled) || canFarmersDiet || canGreenThumbBlocks || canGreenThumbWheat;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canGreenTerra) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.0"), LocaleLoader.getString("Herbalism.Effect.1")));
        }

        if (canGreenThumbWheat) {
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

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Herbalism.Effect.8"), LocaleLoader.getString("Herbalism.Effect.9")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canGreenTerra || (canDoubleDrop && !doubleDropsDisabled) || canFarmersDiet || canGreenThumbBlocks || canGreenThumbWheat;
    }

    @Override
    protected void statsDisplay() {
        if (canGreenTerra) {
            if (hasEndurance) {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.Length", greenTerraLength) + LocaleLoader.getString("Perks.activationtime.bonus", greenTerraLengthEndurance));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.Length", greenTerraLength));
            }
        }

        if (canGreenThumbBlocks || canGreenThumbWheat) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Chance", greenThumbChance) + LocaleLoader.getString("Perks.lucky.bonus", greenThumbChanceLucky));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Chance", greenThumbChance));
            }
        }

        if (canGreenThumbWheat) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Stage", greenThumbStage));
        }

        if (canFarmersDiet) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.FD", farmersDietRank));
        }

        if (hasHylianLuck) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.HylianLuck", hylianLuckChance) + LocaleLoader.getString("Perks.lucky.bonus", hylianLuckChanceLucky));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.HylianLuck", hylianLuckChance));
            }
        }
        if (canDoubleDrop && !doubleDropsDisabled) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.DoubleDropChance", doubleDropChance) + LocaleLoader.getString("Perks.lucky.bonus", doubleDropChanceLucky));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.DoubleDropChance", doubleDropChance));
            }
        }
    }
}
