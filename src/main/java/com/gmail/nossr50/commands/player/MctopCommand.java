package com.gmail.nossr50.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.LeaderboardManager;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.commands.MctopCommandAsyncTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;

public class MctopCommand implements CommandExecutor {
    private SkillType skill;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean useMySQL = Config.getInstance().getUseMySQL();


        switch (args.length) {
            case 0:
                display(1, "ALL", sender, useMySQL, command);
                return true;

            case 1:
                if (StringUtils.isInt(args[0])) {
                    display(Integer.parseInt(args[0]), "ALL", sender, useMySQL, command);
                    return true;
                }

                if (!extractSkill(sender, args[0])) {
                    return true;
                }

                display(1, skill.toString(), sender, useMySQL, command);
                return true;

            case 2:
                if (CommandUtils.isInvalidInteger(sender, args[1])) {
                    return true;
                }

                if (!extractSkill(sender, args[0])) {
                    return true;
                }

                display(Integer.parseInt(args[1]), skill.toString(), sender, useMySQL, command);
                return true;

            default:
                return false;
        }
    }

    private void display(int page, String skill, CommandSender sender, boolean sql, Command command) {
        if (!skill.equalsIgnoreCase("all") && !Permissions.mctop(sender, this.skill)) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        if (sql) {
            if (skill.equalsIgnoreCase("all")) {
                sqlDisplay(page, "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing", sender);
            }
            else {
                sqlDisplay(page, skill, sender);
            }
        }
        else {
            flatfileDisplay(page, skill, sender);
        }
    }

    private void flatfileDisplay(int page, String skill, CommandSender sender) {
        LeaderboardManager.updateLeaderboards(); // Make sure we have the latest information

        if (skill.equalsIgnoreCase("all")) {
            sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", StringUtils.getCapitalized(skill)));
        }

        int position = (page * 10) - 9;

        for (String playerStat : LeaderboardManager.retrieveInfo(skill, page)) {
            if (playerStat == null) {
                continue;
            }

            String digit = String.valueOf(position);

            if (position < 10) {
                digit = "0" + digit;
            }

            String[] splitStat = playerStat.split(":");

            // Format: 1. Playername - skill value
            sender.sendMessage(digit + ". " + ChatColor.GREEN + splitStat[1] + " - " + ChatColor.WHITE + splitStat[0]);
            position++;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.mctop.Tip"));
    }

    private void sqlDisplay(int page, String query, CommandSender sender) {
        new MctopCommandAsyncTask(page, query, sender).runTaskAsynchronously(mcMMO.p);
    }

    private boolean extractSkill(CommandSender sender, String skillName) {
        if (CommandUtils.isInvalidSkill(sender, skillName)) {
            return false;
        }

        skill = SkillType.getSkill(skillName);

        if (CommandUtils.isChildSkill(sender, skill)) {
            return true;
        }

        return true;
    }
}
