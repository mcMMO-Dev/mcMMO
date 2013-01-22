package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.gathering.Herbalism;
import com.gmail.nossr50.util.Permissions;

public class HerbalismCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

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
        farmersDietRank = calculateRank(Herbalism.farmersDietMaxLevel, Herbalism.farmersDietRankChangeLevel);

        //GREEN THUMB
        greenThumbStage = calculateRank(Herbalism.greenThumbStageMaxLevel, Herbalism.greenThumbStageMaxLevel);

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
        Config configInstance = Config.getInstance();

        hasHylianLuck = Permissions.hylianLuck(player);
        canGreenTerra = Permissions.greenTerra(player);
        canGreenThumbWheat = Permissions.greenThumbWheat(player);
        canGreenThumbBlocks = Permissions.greenThumbBlocks(player);
        canFarmersDiet = Permissions.farmersDiet(player);
        canDoubleDrop = Permissions.herbalismDoubleDrops(player);
        doubleDropsDisabled = configInstance.herbalismDoubleDropsDisabled();
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canGreenTerra || (canDoubleDrop && !doubleDropsDisabled) || canFarmersDiet || canGreenThumbBlocks || canGreenThumbWheat;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canGreenTerra) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Herbalism.Effect.0"), LocaleLoader.getString("Herbalism.Effect.1") }));
        }

        if (canGreenThumbWheat) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Herbalism.Effect.2"), LocaleLoader.getString("Herbalism.Effect.3") }));
        }

        if (canGreenThumbBlocks) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Herbalism.Effect.4"), LocaleLoader.getString("Herbalism.Effect.5") }));
        }

        if (canFarmersDiet) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Herbalism.Effect.6"), LocaleLoader.getString("Herbalism.Effect.7") }));
        }

        if (hasHylianLuck) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Herbalism.Effect.10"), LocaleLoader.getString("Herbalism.Effect.11") }));
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Herbalism.Effect.8"), LocaleLoader.getString("Herbalism.Effect.9") }));
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
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.Length", new Object[] { greenTerraLength }) + LocaleLoader.getString("Perks.activationtime.bonus", new Object[] { greenTerraLengthEndurance }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.Length", new Object[] { greenTerraLength }));
            }
        }

        if (canGreenThumbBlocks || canGreenThumbWheat) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Chance", new Object[] { greenThumbChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { greenThumbChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Chance", new Object[] { greenThumbChance }));
            }
        }

        if (canGreenThumbWheat) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Stage", new Object[] { greenThumbStage }));
        }

        if (canFarmersDiet) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.FD", new Object[] { farmersDietRank } ));
        }

        if (hasHylianLuck) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.HylianLuck", new Object[] { hylianLuckChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { hylianLuckChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.HylianLuck", new Object[] { hylianLuckChance }));
            }
        }
        if (canDoubleDrop && !doubleDropsDisabled) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.DoubleDropChance", new Object[] { doubleDropChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { doubleDropChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.DoubleDropChance", new Object[] { doubleDropChance }));
            }
        }
    }
}
