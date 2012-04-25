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

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Swords.SkillName") }));
        player.sendMessage(mcLocale.getString("Commands.XPGain", new Object[] { mcLocale.getString("Commands.XPGain.Swords") }));
        player.sendMessage(mcLocale.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.SWORDS), PP.getSkillXpLevel(SkillType.SWORDS), PP.getXpToLevel(SkillType.SWORDS) }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Effects.Effects") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Swords.Effect.0"), mcLocale.getString("Swords.Effect.1") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Swords.Effect.2"), mcLocale.getString("Swords.Effect.3") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Swords.Effect.4"), mcLocale.getString("Swords.Effect.5") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Swords.Effect.6"), mcLocale.getString("Swords.Effect.7") }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Commands.Stats.Self") }));
        player.sendMessage(mcLocale.getString("Swords.Combat.Counter.Chance", new Object[] { counterAttackChance }));
        player.sendMessage(mcLocale.getString("Swords.Combat.Bleed.Length", new Object[] { bleedLength }));
        player.sendMessage(mcLocale.getString("Swords.Combat.Bleed.Note"));
        player.sendMessage(mcLocale.getString("Swords.Combat.Bleed.Chance", new Object[] { bleedChance }));
        player.sendMessage(mcLocale.getString("Swords.SS.Length", new Object[] { serratedStrikesLength }));

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
