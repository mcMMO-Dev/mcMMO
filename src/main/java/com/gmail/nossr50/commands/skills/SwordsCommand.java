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

public class SwordsCommand implements CommandExecutor {
    private float skillValue;
    private String counterAttackChance;
    private String bleedLength;
    private String bleedChance;
    private String serratedStrikesLength;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.swords")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.SWORDS);
        dataCalculations(skillValue);

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Swords.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Swords") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.SWORDS), PP.getSkillXpLevel(SkillType.SWORDS), PP.getXpToLevel(SkillType.SWORDS) }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.0"), LocaleLoader.getString("Swords.Effect.1") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.2"), LocaleLoader.getString("Swords.Effect.3") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.4"), LocaleLoader.getString("Swords.Effect.5") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.6"), LocaleLoader.getString("Swords.Effect.7") }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        player.sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Chance", new Object[] { counterAttackChance }));
        player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Length", new Object[] { bleedLength }));
        player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Note"));
        player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Chance", new Object[] { bleedChance }));
        player.sendMessage(LocaleLoader.getString("Swords.SS.Length", new Object[] { serratedStrikesLength }));

        Page.grabGuidePageForSkill(SkillType.SWORDS, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        serratedStrikesLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 750) {
            bleedLength = "3";
            bleedChance = "75";
            counterAttackChance = "30";
        }
        else if (skillValue >= 600) {
            bleedLength = "2";
            bleedChance = String.valueOf(skillValue / 10);
            counterAttackChance = "30";
        }
        else {
            bleedLength = "2";
            bleedChance = String.valueOf(skillValue / 10);
            counterAttackChance = String.valueOf(skillValue / 20);  
        }
    }
}
