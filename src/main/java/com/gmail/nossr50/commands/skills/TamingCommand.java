package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.util.Page;

public class TamingCommand implements CommandExecutor {
    private float skillValue;
    private String goreChance;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.taming")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.TAMING);
        dataCalculations(skillValue);

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Taming.SkillName") }));
        player.sendMessage(mcLocale.getString("Commands.XPGain", new Object[] { mcLocale.getString("Commands.XPGain.Taming") }));
        player.sendMessage(mcLocale.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.TAMING), PP.getSkillXpLevel(SkillType.TAMING), PP.getXpToLevel(SkillType.TAMING) }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Effects.Effects") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Taming.Effect.0"), mcLocale.getString("Taming.Effect.1") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Taming.Effect.2"), mcLocale.getString("Taming.Effect.3") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Taming.Effect.4"), mcLocale.getString("Taming.Effect.5") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Taming.Effect.6"), mcLocale.getString("Taming.Effect.7") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Taming.Effect.8"), mcLocale.getString("Taming.Effect.9") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Taming.Effect.10"), mcLocale.getString("Taming.Effect.11") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Taming.Effect.12"), mcLocale.getString("Taming.Effect.13") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Taming.Effect.16"), mcLocale.getString("Taming.Effect.17") }));
        player.sendMessage(mcLocale.getString("Taming.Effect.14", new Object[] { LoadProperties.fishConsumedByCOTW }));
        player.sendMessage(mcLocale.getString("Taming.Effect.15", new Object[] { LoadProperties.bonesConsumedByCOTW }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Commands.Stats.Self") }));

        if (PP.getSkillLevel(SkillType.TAMING) < 100) {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template.Lock", new Object[] { mcLocale.getString("Taming.Ability.Locked.0") }));
        }
        else {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template", new Object[] { mcLocale.getString("Taming.Ability.Bonus.0"), mcLocale.getString("Taming.Ability.Bonus.1") }));
        }

        if (PP.getSkillLevel(SkillType.TAMING) < 250) {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template.Lock", new Object[] { mcLocale.getString("Taming.Ability.Locked.1") }));
        }
        else {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template", new Object[] { mcLocale.getString("Taming.Ability.Bonus.2"), mcLocale.getString("Taming.Ability.Bonus.3") }));
        }

        if (PP.getSkillLevel(SkillType.TAMING) < 500) {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template.Lock", new Object[] { mcLocale.getString("Taming.Ability.Locked.2") }));
        }
        else {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template", new Object[] { mcLocale.getString("Taming.Ability.Bonus.4"), mcLocale.getString("Taming.Ability.Bonus.5") }));
        }

        if (PP.getSkillLevel(SkillType.TAMING) < 750) {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template.Lock", new Object[] { mcLocale.getString("Taming.Ability.Locked.3") }));
        }
        else {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template", new Object[] { mcLocale.getString("Taming.Ability.Bonus.6"), mcLocale.getString("Taming.Ability.Bonus.7") }));
        }

        if (PP.getSkillLevel(SkillType.TAMING) < 50) {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template.Lock", new Object[] { mcLocale.getString("Taming.Ability.Locked.4") }));
        }
        else {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template", new Object[] { mcLocale.getString("Taming.Ability.Bonus.8"), mcLocale.getString("Taming.Ability.Bonus.9 ") }));
        }

        player.sendMessage(mcLocale.getString("Taming.Combat.Chance.Gore", new Object[] { goreChance }));

        Page.grabGuidePageForSkill(SkillType.TAMING, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        if (skillValue >= 1000) {
            goreChance = "100";
        }
        else {
            goreChance = String.valueOf(skillValue / 10);
        }
    }
}
