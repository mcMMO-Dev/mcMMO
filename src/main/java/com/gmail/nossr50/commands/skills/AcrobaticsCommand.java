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

public class AcrobaticsCommand implements CommandExecutor {
    private float skillValue;
    private String dodgeChance;
    private String rollChance;
    private String gracefulRollChance;

    private boolean canDodge;
    private boolean canRoll;
    private boolean canGracefulRoll;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.acrobatics")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.ACROBATICS);
        dataCalculations(skillValue);
        permissionsCheck(player);

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Acrobatics.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Acrobatics") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.ACROBATICS), PP.getSkillXpLevel(SkillType.ACROBATICS), PP.getXpToLevel(SkillType.ACROBATICS) }));

        if (canDodge || canGracefulRoll || canRoll) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        }

        if (canRoll) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Acrobatics.Effect.0"), LocaleLoader.getString("Acrobatics.Effect.1") }));
        }

        if (canGracefulRoll) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Acrobatics.Effect.2"), LocaleLoader.getString("Acrobatics.Effect.3") }));
        }

        if (canDodge) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Acrobatics.Effect.4"), LocaleLoader.getString("Acrobatics.Effect.5") }));
        }

        if (canDodge || canGracefulRoll || canRoll) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        }

        if (canRoll) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Chance", new Object[] { rollChance }));
        }

        if (canGracefulRoll) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.GraceChance", new Object[] { gracefulRollChance }));
        }

        if (canDodge) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.DodgeChance", new Object[] { dodgeChance }));
        }

        Page.grabGuidePageForSkill(SkillType.ACROBATICS, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        DecimalFormat percent = new DecimalFormat("##0.00%");

        if (skillValue >= 1000) {
            dodgeChance = "20.00%";
            rollChance = "100.00%";
            gracefulRollChance = "100.00%";
        }
        else if (skillValue >= 800) {
            dodgeChance = "20.00%";
            rollChance = percent.format(skillValue / 1000);
            gracefulRollChance = "100.00%";
        }
        else if (skillValue >= 500) {
            dodgeChance = percent.format(skillValue / 4000);
            rollChance = percent.format(skillValue / 1000);
            gracefulRollChance = "100.00%";
        }
        else {
            dodgeChance = percent.format(skillValue / 4000);
            rollChance = percent.format(skillValue / 1000);
            gracefulRollChance = percent.format(skillValue / 500);
        }
    }

    private void permissionsCheck(Player player) {
        Permissions permInstance = Permissions.getInstance();

        canDodge = permInstance.dodge(player);
        canRoll = permInstance.roll(player);
        canGracefulRoll = permInstance.gracefulRoll(player);
    }
}