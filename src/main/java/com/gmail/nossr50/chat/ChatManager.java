package com.gmail.nossr50.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;

public final class ChatManager {
    public ChatManager () {}

    public static void handleAdminChat(Plugin plugin, String playerName, String displayName, String message, boolean isAsync) {
        McMMOAdminChatEvent chatEvent = new McMMOAdminChatEvent(plugin, playerName, displayName, message, isAsync);
        mcMMO.p.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        if (Config.getInstance().getAdminDisplayNames()) {
            displayName = chatEvent.getDisplayName();
        }
        else {
            displayName = chatEvent.getSender();
        }

        String adminMessage = chatEvent.getMessage();

        mcMMO.p.getServer().broadcast(LocaleLoader.getString("Commands.AdminChat.Prefix", displayName) + adminMessage, "mcmmo.chat.adminchat");
    }

    public static void handleAdminChat(Plugin plugin, String senderName, String message) {
        handleAdminChat(plugin, senderName, senderName, message);
    }

    public static void handleAdminChat(Plugin plugin, String playerName, String displayName, String message) {
        handleAdminChat(plugin, playerName, displayName, message, false);
    }

    public static void handlePartyChat(Plugin plugin, Party party, String playerName, String displayName, String message, boolean isAsync) {
        String partyName = party.getName();

        McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent(plugin, playerName, displayName, partyName, message, isAsync);
        mcMMO.p.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        if (Config.getInstance().getPartyDisplayNames()) {
            displayName = chatEvent.getDisplayName();
        }
        else {
            displayName = chatEvent.getSender();
        }

        String partyMessage = chatEvent.getMessage();

        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Commands.Party.Chat.Prefix", displayName) + partyMessage);
        }

        mcMMO.p.getLogger().info("[P](" + partyName + ")" + "<" + ChatColor.stripColor(displayName) + "> " + partyMessage);
    }

    public static void handlePartyChat(Plugin plugin, Party party, String senderName, String message) {
        handlePartyChat(plugin, party, senderName, senderName, message);
    }

    public static void handlePartyChat(Plugin plugin, Party party, String playerName, String displayName, String message) {
        handlePartyChat(plugin, party, playerName, displayName, message, false);
    }
}
