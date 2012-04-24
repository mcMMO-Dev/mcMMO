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

public class AxesCommand implements CommandExecutor {
    private float skillValue;
    private String critChance;
    private String bonusDamage;
    private String impactDamage;
    private String greaterImpactDamage;
    private String skullSplitterLength;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.axes")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.AXES);
        dataCalculations(skillValue);

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Axes.SkillName") }));
        player.sendMessage(mcLocale.getString("Commands.XPGain", new Object[] { mcLocale.getString("Commands.XPGain.Axes") }));
        player.sendMessage(mcLocale.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.AXES), PP.getSkillXpLevel(SkillType.AXES), PP.getXpToLevel(SkillType.AXES) }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Effects.Effects") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Axes.Effect.0"), mcLocale.getString("Axes.Effect.1") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Axes.Effect.2"), mcLocale.getString("Axes.Effect.3") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Axes.Effect.4"), mcLocale.getString("Axes.Effect.5") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Axes.Effect.6"), mcLocale.getString("Axes.Effect.7") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Axes.Effect.8"), mcLocale.getString("Axes.Effect.9") }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Commands.Stats.Self") }));
        player.sendMessage(mcLocale.getString("Axes.Combat.CritChance", new Object[] { critChance }));
        player.sendMessage(mcLocale.getString("Ability.Generic.Template", new Object[] { mcLocale.getString("Axes.Ability.Bonus.0"), mcLocale.getString("Axes.Ability.Bonus.1", new Object[] {bonusDamage}) }));
        player.sendMessage(mcLocale.getString("Ability.Generic.Template", new Object[] { mcLocale.getString("Axes.Ability.Bonus.2"), mcLocale.getString("Axes.Ability.Bonus.3", new Object[] {impactDamage}) }));
        player.sendMessage(mcLocale.getString("Ability.Generic.Template", new Object[] { mcLocale.getString("Axes.Ability.Bonus.4"), mcLocale.getString("Axes.Ability.Bonus.5", new Object[] {greaterImpactDamage}) }));
        player.sendMessage(mcLocale.getString("Axes.Combat.SS.Length", new Object[] { skullSplitterLength }));

        return true;
    }

    private void dataCalculations(float skillValue) {
        impactDamage = String.valueOf(5 + ((int) skillValue / 30));
        skullSplitterLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 1000) {
            critChance = "37.5";
            bonusDamage = "4";
            greaterImpactDamage = "2";
        }
        else if (skillValue >= 750) {
            critChance = "37.5";
            bonusDamage = "4";
            greaterImpactDamage = "2";
        }
        else if (skillValue >= 200) {
            critChance = String.valueOf(skillValue / 20);
            bonusDamage = "4";
            greaterImpactDamage = "2";
        }
        else {
            critChance = String.valueOf(skillValue / 20);
            bonusDamage = String.valueOf((int) skillValue / 50);
            greaterImpactDamage = "2";
        }
    }
}
