package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Users;

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

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Archery.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Archery") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.ARCHERY), PP.getSkillXpLevel(SkillType.ARCHERY), PP.getXpToLevel(SkillType.ARCHERY) }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Archery.Effect.0"), LocaleLoader.getString("Archery.Effect.1") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Archery.Effect.2"), LocaleLoader.getString("Archery.Effect.3") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Archery.Effect.4"), LocaleLoader.getString("Archery.Effect.5") }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        player.sendMessage(LocaleLoader.getString("Archery.Combat.SkillshotBonus", new Object[] { skillShotBonus }));
        player.sendMessage(LocaleLoader.getString("Archery.Combat.DazeChance", new Object[] { dazeChance }));
        player.sendMessage(LocaleLoader.getString("Archery.Combat.RetrieveChance", new Object[] { retrieveChance }));

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
