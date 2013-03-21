package com.gmail.nossr50.runnables.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;

public class MctopCommandDisplayTask extends BukkitRunnable {
    private HashMap<Integer, ArrayList<String>> userslist;
    private CommandSender sender;
    private String query;
    private int page;

    public MctopCommandDisplayTask(HashMap<Integer, ArrayList<String>> userslist, int page, String query, CommandSender sender) {
        this.userslist = userslist;
        this.page = page;
        this.query = query;
        this.sender = sender;
    }

    @Override
    public void run() {
        if (query.equalsIgnoreCase("taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing")) {
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
}
