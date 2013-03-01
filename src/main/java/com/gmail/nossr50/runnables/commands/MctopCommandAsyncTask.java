package com.gmail.nossr50.runnables.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;

public class MctopCommandAsyncTask implements Runnable {

    private CommandSender sender;
    private String query;
    private int page;
    private Command command;

    public MctopCommandAsyncTask(int page, String query, CommandSender sender, Command command) {
        this.page = page;
        this.query = query;
        this.sender = sender;
        this.command = command;
    }

    @Override
    public void run() {
        if (!query.equalsIgnoreCase("taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing")) {
            if (!Permissions.mctop(sender, SkillType.getSkill(query))) {
                sender.sendMessage(command.getPermissionMessage());
                return;
            }
        }
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();
        final HashMap<Integer, ArrayList<String>> userslist = DatabaseManager.read("SELECT " + query + ", user, NOW() FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON (user_id = id) WHERE " + query + " > 0 ORDER BY " + query + " DESC, user LIMIT " + ((page * 10) - 10) + ",10");
        Bukkit.getScheduler().scheduleSyncDelayedTask(mcMMO.p, new Runnable() {
            @Override
            public void run() {
                if (query.equals("taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing")) {
                    sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", StringUtils.getCapitalized(query)));
                }

                int place = (page * 10) - 9;
                for (int i = 1; i <= 10; i++) {
                    if (userslist.get(i) == null) {
                        break;
                    }

                    // Format: 1. Playername - skill value
                    sender.sendMessage(place + ". " + ChatColor.GREEN + userslist.get(i).get(1) + " - " + ChatColor.WHITE + userslist.get(i).get(0));
                    place++;
                }

                sender.sendMessage(LocaleLoader.getString("Commands.mctop.Tip"));
            }
        }, 1L);
    }

}
