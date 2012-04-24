package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.util.Page;

public class HerbalismCommand implements CommandExecutor {
    private float skillValue;
    private String greenTerraLength;
    private String greenThumbChance;
    private String greenThumbStage;
    private String farmersDietRank;
    private String doubleDropChance;

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

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Herbalism.SkillName") }));
        player.sendMessage(mcLocale.getString("Commands.XPGain", new Object[] { mcLocale.getString("Commands.XPGain.Herbalism") }));
        player.sendMessage(mcLocale.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.HERBALISM), PP.getSkillXpLevel(SkillType.HERBALISM), PP.getXpToLevel(SkillType.HERBALISM) }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Effects.Effects") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Herbalism.Effect.0"), mcLocale.getString("Herbalism.Effect.1") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Herbalism.Effect.2"), mcLocale.getString("Herbalism.Effect.3") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Herbalism.Effect.4"), mcLocale.getString("Herbalism.Effect.5") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Herbalism.Effect.6"), mcLocale.getString("Herbalism.Effect.7") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Herbalism.Effect.8"), mcLocale.getString("Herbalism.Effect.9") }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Commands.Stats.Self") }));
        player.sendMessage(mcLocale.getString("Herbalism.Ability.GTe.Length", new Object[] { greenTerraLength }));
        player.sendMessage(mcLocale.getString("Herbalism.Ability.GTh.Chance", new Object[] { greenThumbChance }));
        player.sendMessage(mcLocale.getString("Herbalism.Ability.GTh.Stage", new Object[] { greenThumbStage }));
        player.sendMessage(mcLocale.getString("Herbalism.Ability.FD", new Object[] { farmersDietRank } ));
        player.sendMessage(mcLocale.getString("Herbalism.Ability.DoubleDropChance", new Object[] { doubleDropChance }));

        Page.grabGuidePageForSkill(SkillType.HERBALISM, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        greenTerraLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 1500) {
            greenThumbChance = "100";
            greenThumbStage = "4";
            farmersDietRank = "5";
            doubleDropChance = "100";
        }
        else if (skillValue >= 1000) {
            greenThumbChance = String.valueOf(skillValue / 15);
            greenThumbStage = "4";
            farmersDietRank = "5";
            doubleDropChance = "100";
        }
        else if (skillValue >= 800) {
            greenThumbChance = String.valueOf(skillValue / 15);
            greenThumbStage = "4";
            farmersDietRank = "4";
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else if (skillValue >= 600) {
            greenThumbChance = String.valueOf(skillValue / 15);
            greenThumbStage = "4";
            farmersDietRank = "3";
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else if (skillValue >= 400) {
            greenThumbChance = String.valueOf(skillValue / 15);
            greenThumbStage = "3";
            farmersDietRank = "2";
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else if (skillValue >= 200) {
            greenThumbChance = String.valueOf(skillValue / 15);
            greenThumbStage = "2";
            farmersDietRank = "1";
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else {
            greenThumbChance = String.valueOf(skillValue / 15);
            greenThumbStage = "1";
            farmersDietRank = "1";
            doubleDropChance = String.valueOf(skillValue / 10);
        }
    }
}
