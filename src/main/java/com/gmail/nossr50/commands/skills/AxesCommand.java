package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

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

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Axes.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Axes") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.AXES), PP.getSkillXpLevel(SkillType.AXES), PP.getXpToLevel(SkillType.AXES) }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Axes.Effect.0"), LocaleLoader.getString("Axes.Effect.1") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Axes.Effect.2"), LocaleLoader.getString("Axes.Effect.3") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Axes.Effect.4"), LocaleLoader.getString("Axes.Effect.5") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Axes.Effect.6"), LocaleLoader.getString("Axes.Effect.7") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Axes.Effect.8"), LocaleLoader.getString("Axes.Effect.9") }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        player.sendMessage(LocaleLoader.getString("Axes.Combat.CritChance", new Object[] { critChance }));
        player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Axes.Ability.Bonus.0"), LocaleLoader.getString("Axes.Ability.Bonus.1", new Object[] {bonusDamage}) }));
        player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Axes.Ability.Bonus.2"), LocaleLoader.getString("Axes.Ability.Bonus.3", new Object[] {impactDamage}) }));
        player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Axes.Ability.Bonus.4"), LocaleLoader.getString("Axes.Ability.Bonus.5", new Object[] {greaterImpactDamage}) }));
        player.sendMessage(LocaleLoader.getString("Axes.Combat.SS.Length", new Object[] { skullSplitterLength }));

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
