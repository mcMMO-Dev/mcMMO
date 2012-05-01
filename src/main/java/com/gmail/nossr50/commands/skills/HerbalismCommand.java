package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class HerbalismCommand implements CommandExecutor {
    private float skillValue;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.herbalism")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.HERBALISM);
        dataCalculations(skillValue);
        permissionsCheck(player);

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Herbalism.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Herbalism") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.HERBALISM), PP.getSkillXpLevel(SkillType.HERBALISM), PP.getXpToLevel(SkillType.HERBALISM) }));

        if (canGreenTerra || canDoubleDrop || canFarmersDiet || canGreenThumbBlocks || canGreenThumbWheat) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
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

        if (canDoubleDrop) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Herbalism.Effect.8"), LocaleLoader.getString("Herbalism.Effect.9") }));
        }

        if (canGreenTerra || canDoubleDrop || canFarmersDiet || canGreenThumbBlocks || canGreenThumbWheat) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        }

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

        if (canDoubleDrop) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.DoubleDropChance", new Object[] { doubleDropChance }));
        }

        Page.grabGuidePageForSkill(SkillType.HERBALISM, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        DecimalFormat percent = new DecimalFormat("##0.00%");

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

    private void permissionsCheck(Player player) {
        Permissions permInstance = Permissions.getInstance();

         canGreenTerra = permInstance.greenTerra(player);
         canGreenThumbWheat = permInstance.greenThumbWheat(player);
         canGreenThumbBlocks = permInstance.greenThumbBlocks(player);
         canFarmersDiet = permInstance.farmersDiet(player);
         canDoubleDrop = permInstance.herbalismDoubleDrops(player);
    }
}
