package com.gmail.nossr50.commands.skills;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
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

    private int abilityLengthIncreaseLevel = advancedConfig.getAbilityLength();
    private int farmersDietRankChange = advancedConfig.getFarmerDietRankChange();
    private int farmersDietMaxLevel = farmersDietRankChange * 5;
    private int greenThumbStageChange = advancedConfig.getGreenThumbStageChange();
    private int greenThumbStageMaxLevel = greenThumbStageChange * 4;
    private double greenThumbMaxBonus = advancedConfig.getGreenThumbChanceMax();
    private int greenThumbMaxLevel = advancedConfig.getGreenThumbMaxLevel();
    private double doubleDropsMaxBonus = advancedConfig.getHerbalismDoubleDropsChanceMax();
    private int doubleDropsMaxLevel = advancedConfig.getHerbalismDoubleDropsMaxLevel();

    private boolean canGreenTerra;
    private boolean canGreenThumbWheat;
    private boolean canGreenThumbBlocks;
    private boolean canFarmersDiet;
    private boolean canDoubleDrop;
    private boolean doubleDropsDisabled;
    private boolean lucky;
    private boolean endurance;

    public HerbalismCommand() {
        super(SkillType.HERBALISM);
    }

    @Override
    protected void dataCalculations() {
        float greenThumbChanceF;
        float doubleDropChanceF;

        int length = 2 + (int) ((double) skillValue / (double) abilityLengthIncreaseLevel);
        greenTerraLength = String.valueOf(length);

        if (Permissions.activationTwelve(player)) {
            length = length + 12;
        }
        else if (Permissions.activationEight(player)) {
            length = length + 8;
        }
        else if (Permissions.activationFour(player)) {
            length = length + 4;
        }
        int maxLength = SkillType.HERBALISM.getAbility().getMaxTicks();
        if (maxLength != 0 && length > maxLength) {
            length = maxLength;
        }
        greenTerraLengthEndurance = String.valueOf(length);
        //FARMERS DIET
        if (skillValue >= farmersDietMaxLevel) farmersDietRank = "5";
        else farmersDietRank = String.valueOf((int) ((double) skillValue / (double) farmersDietRankChange));
        //GREEN THUMB
        if (skillValue >= greenThumbStageMaxLevel) greenThumbStage = "4";
        else greenThumbStage = String.valueOf((int) ((double) skillValue / (double) greenThumbStageChange));

        if (skillValue >= greenThumbMaxLevel) greenThumbChanceF = (float) (greenThumbMaxBonus);
        else greenThumbChanceF = (float) ((greenThumbMaxBonus / greenThumbMaxLevel) * skillValue);
        greenThumbChance = percent.format(greenThumbChanceF / 100D);
        if (greenThumbChanceF * 1.3333D >= 100D) greenThumbChanceLucky = percent.format(1D);
        else greenThumbChanceLucky = percent.format(greenThumbChanceF * 1.3333D / 100D);
        //DOUBLE DROPS
        if (skillValue >= doubleDropsMaxLevel) doubleDropChanceF = (float) (doubleDropsMaxBonus);
        else doubleDropChanceF = (float) ((doubleDropsMaxBonus / doubleDropsMaxLevel) * skillValue);
        doubleDropChance = percent.format(doubleDropChanceF / 100D);
        if (doubleDropChanceF * 1.3333D >= 100D) doubleDropChanceLucky = percent.format(1D);
        else doubleDropChanceLucky = percent.format(doubleDropChanceF * 1.3333D / 100D);
    }

    @Override
    protected void permissionsCheck() {
        Config configInstance = Config.getInstance();

        canGreenTerra = Permissions.greenTerra(player);
        canGreenThumbWheat = Permissions.greenThumbWheat(player);
        canGreenThumbBlocks = Permissions.greenThumbBlocks(player);
        canFarmersDiet = Permissions.farmersDiet(player);
        canDoubleDrop = Permissions.herbalismDoubleDrops(player);
        doubleDropsDisabled = configInstance.herbalismDoubleDropsDisabled();
        lucky = Permissions.luckyHerbalism(player);
        endurance = Permissions.activationTwelve(player) || Permissions.activationEight(player) || Permissions.activationFour(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canGreenTerra || (canDoubleDrop && !doubleDropsDisabled) || canFarmersDiet || canGreenThumbBlocks || canGreenThumbWheat;
    }

    @Override
    protected void effectsDisplay() {
        if (lucky) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Herbalism" }) }));
        }

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
            if (endurance)
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.Length", new Object[] { greenTerraLength }) + LocaleLoader.getString("Perks.activationtime.bonus", new Object[] { greenTerraLengthEndurance }));
            else
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.Length", new Object[] { greenTerraLength }));
        }

        if (canGreenThumbBlocks || canGreenThumbWheat) {
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Chance", new Object[] { greenThumbChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { greenThumbChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Chance", new Object[] { greenThumbChance }));
        }

        if (canGreenThumbWheat) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Stage", new Object[] { greenThumbStage }));
        }

        if (canFarmersDiet) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.FD", new Object[] { farmersDietRank } ));
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.DoubleDropChance", new Object[] { doubleDropChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { doubleDropChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Herbalism.Ability.DoubleDropChance", new Object[] { doubleDropChance }));
        }
    }
}
