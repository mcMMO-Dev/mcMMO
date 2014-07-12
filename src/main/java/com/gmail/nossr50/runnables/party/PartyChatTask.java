package com.gmail.nossr50.runnables.party;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.locale.LocaleLoader;

public class PartyChatTask extends BukkitRunnable {
    private Plugin plugin;

    private Party party;
    private String senderName;
    private String displayName;
    private String message;

    public PartyChatTask(Plugin plugin, Party party, String senderName, String displayName, String message) {
        this.plugin = plugin;

        this.party = party;
        this.senderName = senderName;
        this.displayName = displayName;
        this.message = message;
    }

    @Override
    public void run() {
        if (Config.getInstance().getPartyChatColorLeaderName() && senderName.equalsIgnoreCase(party.getLeader().getPlayerName())) {
            message = message.replaceFirst(Pattern.quote(displayName), ChatColor.GOLD + Matcher.quoteReplacement(displayName) + ChatColor.RESET);
        }

        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(message);
        }

        if (party.getAlly() != null) {
            for (Player member : party.getAlly().getOnlineMembers()) {
                String allyPrefix = LocaleLoader.formatString(Config.getInstance().getPartyChatPrefixAlly());
                member.sendMessage(allyPrefix + message);
            }
        }

        plugin.getServer().getConsoleSender().sendMessage("[mcMMO] [P]<" + party.getName() + ">" + message);
    }
}
