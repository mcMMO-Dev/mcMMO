package com.gmail.nossr50.commands.player;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.Database;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Leaderboard;
import com.gmail.nossr50.util.Misc;

public class MctopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = LocaleLoader.getString("Commands.Usage.2", "mctop", "[" + LocaleLoader.getString("Commands.Usage.Skill") + "]", "[" + LocaleLoader.getString("Commands.Usage.Page") + "]");

        if (!Config.getInstance().getUseMySQL()) {

            switch (args.length) {
            case 0:
                flatfileDisplay(1, "ALL", sender);
                return true;

            case 1:
                if (Misc.isInt(args[0])) {
                    flatfileDisplay(Integer.valueOf(args[0]), "ALL", sender);
                }
                else if (SkillTools.isSkill(args[0])) {
                    flatfileDisplay(1, args[0].toUpperCase(), sender);
                }
                else if (SkillTools.isLocalizedSkill(args[0])) {
                    flatfileDisplay(1, SkillTools.translateLocalizedSkill(args[0]).toUpperCase(), sender);
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                }

                return true;

            case 2:
                if (Misc.isInt(args[1])) {
                    if (SkillTools.isSkill(args[0])) {
                        flatfileDisplay(Integer.valueOf(args[1]), args[0].toUpperCase(), sender);
                    }
                    else if (SkillTools.isLocalizedSkill(args[0])) {
                        flatfileDisplay(Integer.valueOf(args[1]), SkillTools.translateLocalizedSkill(args[0]).toUpperCase(), sender);
                    }
                    else {
                        sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    }
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

        String powerlevel = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing";

        switch (args.length) {
        case 0:
            sqlDisplay(1, powerlevel, sender);
            return true;

        case 1:
            if (Misc.isInt(args[0])) {
                sqlDisplay(Integer.valueOf(args[0]), powerlevel, sender);
            }
            else if (SkillTools.isSkill(args[0])) {
                sqlDisplay(1, args[0].toLowerCase(), sender);
            }
            else if (SkillTools.isLocalizedSkill(args[0])) {
                sqlDisplay(1, SkillTools.translateLocalizedSkill(args[0]).toLowerCase(), sender);
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
            }

            return true;

        case 2:
            if (Misc.isInt(args[1])) {
                if (SkillTools.isSkill(args[0])) {
                    sqlDisplay(Integer.valueOf(args[1]), args[0].toLowerCase(), sender);
                }
                else if (SkillTools.isLocalizedSkill(args[0])) {
                    sqlDisplay(Integer.valueOf(args[1]), SkillTools.translateLocalizedSkill(args[0]).toLowerCase(), sender);
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                }
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

    private void flatfileDisplay(int page, String skill, CommandSender sender) {
        Leaderboard.updateLeaderboards(); //Make sure we have the latest information
        SkillType skillType = SkillType.getSkill(skill);
        String[] info = Leaderboard.retrieveInfo(skillType, page);

        if (skill.equalsIgnoreCase("ALL")) {
            sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", Misc.getCapitalized(skill)));
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

    private void sqlDisplay(int page, String query, CommandSender sender) {
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();
        HashMap<Integer, ArrayList<String>> userslist = Database.read("SELECT " + query + ", user, NOW() FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON (user_id = id) WHERE " + query + " > 0 ORDER BY " + query + " DESC, user LIMIT "+((page * 10) - 10)+",10");

        if (query.equals("taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing")) {
            sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", Misc.getCapitalized(query)));
        }
        int place = (page * 10) - 9;
        for (int i = 1; i <= 10; i++) {
            if(userslist.get(i) == null) {
                break;
            }
            sender.sendMessage(String.valueOf(place) + ". " + ChatColor.GREEN + userslist.get(i).get(1) + " - " + ChatColor.WHITE + userslist.get(i).get(0));
            place++;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.mctop.Tip"));
    }
}
