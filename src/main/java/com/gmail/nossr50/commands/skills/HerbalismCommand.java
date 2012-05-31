package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class HerbalismCommand extends SkillCommand {
    private String greenTerraLength;
    private String greenThumbChance;
    private String greenThumbStage;
    private String farmersDietRank;
    private String doubleDropChance;

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
        greenTerraLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 1500) {
            greenThumbChance = "100.00%";
            greenThumbStage = "4";
            farmersDietRank = "5";
            doubleDropChance = "100.00%";
        }
        else if (skillValue >= 1000) {
            greenThumbChance = percent.format(skillValue / 1500);
            greenThumbStage = "4";
            farmersDietRank = "5";
            doubleDropChance = "100.00%";
        }
        else if (skillValue >= 800) {
            greenThumbChance = percent.format(skillValue / 1500);
            greenThumbStage = "4";
            farmersDietRank = "4";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else if (skillValue >= 600) {
            greenThumbChance = percent.format(skillValue / 1500);
            greenThumbStage = "4";
            farmersDietRank = "3";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else if (skillValue >= 400) {
            greenThumbChance = percent.format(skillValue / 1500);
            greenThumbStage = "3";
            farmersDietRank = "2";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else if (skillValue >= 200) {
            greenThumbChance = percent.format(skillValue / 1500);
            greenThumbStage = "2";
            farmersDietRank = "1";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else {
            greenThumbChance = percent.format(skillValue / 1500);
            greenThumbStage = "1";
            farmersDietRank = "1";
            doubleDropChance = percent.format(skillValue / 1000);
        }
    }

    @Override
    protected void permissionsCheck() {
        Config configInstance = Config.getInstance();

        canGreenTerra = permInstance.greenTerra(player);
        canGreenThumbWheat = permInstance.greenThumbWheat(player);
        canGreenThumbBlocks = permInstance.greenThumbBlocks(player);
        canFarmersDiet = permInstance.farmersDiet(player);
        canDoubleDrop = permInstance.herbalismDoubleDrops(player);
        doubleDropsDisabled = configInstance.herbalismDoubleDropsDisabled();
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canGreenTerra || (canDoubleDrop && !doubleDropsDisabled) || canFarmersDiet || canGreenThumbBlocks || canGreenThumbWheat;
    }

    @Override
    protected void effectsDisplay() {
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
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.Length", new Object[] { greenTerraLength }));
        }

        if (canGreenThumbBlocks || canGreenThumbWheat) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Chance", new Object[] { greenThumbChance }));
        }

        if (canGreenThumbWheat) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Stage", new Object[] { greenThumbStage }));
        }

        if (canFarmersDiet) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.FD", new Object[] { farmersDietRank } ));
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.DoubleDropChance", new Object[] { doubleDropChance }));
        }
    }
}
