package com.gmail.nossr50.runnables.commands;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;

public class MctopCommandDisplayTask extends BukkitRunnable {
    private Collection<ArrayList<String>> userStats;
    private CommandSender sender;
    private String query;
    private int page;

    public MctopCommandDisplayTask(Collection<ArrayList<String>> userStats, int page, String query, CommandSender sender) {
        this.userStats = userStats;
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

        for (ArrayList<String> stat : userStats) {
            String digit = (place < 10) ? "0" : "" + String.valueOf(place);

            // Format: 1. Playername - skill value
            sender.sendMessage(digit + ". " + ChatColor.GREEN + stat.get(1) + " - " + ChatColor.WHITE + stat.get(0));
            place++;
        }

        sender.sendMessage(LocaleLoader.getString("Commands.mctop.Tip"));
    }
}
