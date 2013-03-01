package com.gmail.nossr50.commands.player;

import org.bukkit.Bukkit;
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
import com.gmail.nossr50.util.skills.SkillUtils;

public class MctopCommand implements CommandExecutor {
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
                }
                else if (SkillUtils.isSkill(args[0])) {
                    SkillType skill = SkillType.getSkill(args[0]);

                    if (skill.isChildSkill()) {
                        sender.sendMessage("Child skills are not yet supported by this command."); // TODO: Localize this
                        return true;
                    }

                    display(1, skill.toString(), sender, useMySQL, command);
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                }

                return true;

            case 2:
                if (!StringUtils.isInt(args[1])) {
                    return false;
                }

                if (SkillUtils.isSkill(args[0])) {
                    SkillType skill = SkillType.getSkill(args[0]);

                    if (skill.isChildSkill()) {
                        sender.sendMessage("Child skills are not yet supported by this command."); // TODO: Localize this
                        return true;
                    }

                    display(Integer.parseInt(args[1]), skill.toString(), sender, useMySQL, command);
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                }

                return true;

            default:
                return false;
        }
    }

    private void display(int page, String skill, CommandSender sender, boolean sql, Command command) {
        if (sql) {
            if (skill.equalsIgnoreCase("all")) {
                sqlDisplay(page, "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing", sender, command);
            }
            else {
                sqlDisplay(page, skill, sender, command);
            }
        }
        else {
            flatfileDisplay(page, skill, sender, command);
        }
    }

    private void flatfileDisplay(int page, String skill, CommandSender sender, Command command) {
        if (!skill.equalsIgnoreCase("all") && !Permissions.mctop(sender, SkillType.getSkill(skill))) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        LeaderboardManager.updateLeaderboards(); // Make sure we have the latest information

        String[] info = LeaderboardManager.retrieveInfo(skill, page);

        if (skill.equalsIgnoreCase("all")) {
            sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", StringUtils.getCapitalized(skill)));
        }

        int n = (page * 10) - 9; // Position
        for (String x : info) {
            if (x != null) {
                String digit = String.valueOf(n);

                if (n < 10) {
                    digit = "0" + digit;
                }

                String[] splitx = x.split(":");

                // Format: 1. Playername - skill value
                sender.sendMessage(digit + ". " + ChatColor.GREEN + splitx[1] + " - " + ChatColor.WHITE + splitx[0]);
                n++;
            }
        }

        sender.sendMessage(LocaleLoader.getString("Commands.mctop.Tip"));
    }

    private void sqlDisplay(int page, String query, CommandSender sender, Command command) {
        Bukkit.getScheduler().runTaskAsynchronously(mcMMO.p, new MctopCommandAsyncTask(page, query, sender, command));
    }
}
