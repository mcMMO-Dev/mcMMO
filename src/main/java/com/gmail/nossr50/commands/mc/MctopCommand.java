package com.gmail.nossr50.commands.mc;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Database;
import com.gmail.nossr50.util.Leaderboard;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;

public class MctopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = ChatColor.RED + "Proper usage is /mctop [skill] [page]"; //TODO: Needs more locale.

        if (!Config.getInstance().getUseMySQL()) {

            switch (args.length) {
            case 0:
                flatfileDisplay(1, "ALL", sender);
                return true;

            case 1:
                if (Misc.isInt(args[0])) {
                    flatfileDisplay(Integer.valueOf(args[0]), "ALL", sender);
                }
                else if (Skills.isSkill(args[0])) {
                    flatfileDisplay(1, args[0].toUpperCase(), sender);
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                }

                return true;

            case 2:
                if (!Skills.isSkill(args[0])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    return true;
                }

                if (Misc.isInt(args[1])) {
                    flatfileDisplay(Integer.valueOf(args[1]), args[0].toUpperCase(), sender);
                }
                else {
                    sender.sendMessage(usage);
                }

                return true;

            default:
                sender.sendMessage(usage);
                return true;
            }
        }
        else {
            String powerlevel = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing";

            switch (args.length) {
            case 0:
                sqlDisplay(1, powerlevel, sender);
                return true;

            case 1:
                if (Misc.isInt(args[0])) {
                    sqlDisplay(Integer.valueOf(args[0]), powerlevel, sender);
                }
                else if (Skills.isSkill(args[0])) {
                    sqlDisplay(1, args[0].toLowerCase(), sender);
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                }

                return true;

            case 2:
                if (!Skills.isSkill(args[0])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    return true;
                }

                if (Misc.isInt(args[1])) {
                    sqlDisplay(Integer.valueOf(args[1]), args[0].toLowerCase(), sender);
                }
                else {
                    sender.sendMessage(usage);
                }

                return true;

            default:
                sender.sendMessage(usage);
                return true;
            }
        }
    }

    private void flatfileDisplay(int page, String skill, CommandSender sender) {
        String[] info = Leaderboard.retrieveInfo(skill, page);

        if (skill.equals("ALL")) {
            sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", new Object[] { Misc.getCapitalized(skill) }));
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
    }

    private void sqlDisplay(int page, String query, CommandSender sender) {
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();
        Database database = mcMMO.getPlayerDatabase();

        HashMap<Integer, ArrayList<String>> userslist = database.read("SELECT " + query + ", user_id FROM " + tablePrefix + "skills WHERE " + query + " > 0 ORDER BY " + query + " DESC ");

        if (query.equals("taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing")) {
            sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", new Object[] { Misc.getCapitalized(query) }));
        }

        for (int i = (page * 10) - 9; i <= (page * 10); i++) {
            if (i > userslist.size() || database.read("SELECT user FROM " + tablePrefix + "users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null) {
                break;
            }

            HashMap<Integer, ArrayList<String>> username = database.read("SELECT user FROM " + tablePrefix + "users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'");
            sender.sendMessage(String.valueOf(i) + ". " + ChatColor.GREEN + userslist.get(i).get(0) + " - " + ChatColor.WHITE + username.get(1).get(0));
        }
    }
}
