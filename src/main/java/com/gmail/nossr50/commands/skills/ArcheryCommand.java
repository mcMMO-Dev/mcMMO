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

public class ArcheryCommand implements CommandExecutor {
    private float skillValue;
    private String skillShotBonus;
    private String dazeChance;
    private String retrieveChance;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.archery")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.ARCHERY);
        dataCalculations(skillValue);

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Archery.SkillName") }));
        player.sendMessage(mcLocale.getString("Commands.XPGain", new Object[] { mcLocale.getString("Commands.XPGain.Archery") }));
        player.sendMessage(mcLocale.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.ARCHERY), PP.getSkillXpLevel(SkillType.ARCHERY), PP.getXpToLevel(SkillType.ARCHERY) }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Effects.Effects") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Archery.Effect.0"), mcLocale.getString("Archery.Effect.1") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Archery.Effect.2"), mcLocale.getString("Archery.Effect.3") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Archery.Effect.4"), mcLocale.getString("Archery.Effect.5") }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Commands.Stats.Self") }));
        player.sendMessage(mcLocale.getString("Archery.Combat.SkillshotBonus", new Object[] { skillShotBonus }));
        player.sendMessage(mcLocale.getString("Archery.Combat.DazeChance", new Object[] { dazeChance }));
        player.sendMessage(mcLocale.getString("Archery.Combat.RetrieveChance", new Object[] { retrieveChance }));

        Page.grabGuidePageForSkill(SkillType.ARCHERY, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        if (skillValue >= 1000) {
            skillShotBonus = "200";
            dazeChance = "50";
            retrieveChance = "100";
        }
        else {
            skillShotBonus = String.valueOf((int) skillValue / 5);
            dazeChance = String.valueOf(skillValue / 20);
            retrieveChance = String.valueOf(skillValue / 10);
        }
    }
}
