package com.gmail.nossr50.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;

public final class ChatManager {
    public ChatManager () {}

    public static void handleAdminChat(Plugin plugin, String playerName, String message) {
        McMMOAdminChatEvent chatEvent = new McMMOAdminChatEvent(plugin, playerName, message);
        mcMMO.p.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        String adminMessage = chatEvent.getMessage();

        mcMMO.p.getLogger().info("[A]<" + playerName + "> " + adminMessage);

        for (Player otherPlayer : mcMMO.p.getServer().getOnlinePlayers()) {
            if (Permissions.adminChat(otherPlayer) || otherPlayer.isOp()) {
                otherPlayer.sendMessage(LocaleLoader.getString("Commands.AdminChat.Prefix", new Object[] {playerName}) + adminMessage);
            }
        }
    }

    public static void handlePartyChat(Plugin plugin, Party party, String playerName, String message) {
        String partyName = party.getName();

        McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent(plugin, playerName, partyName, message);
        mcMMO.p.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        String partyMessage = chatEvent.getMessage();

        mcMMO.p.getLogger().info("[P](" + partyName + ")" + "<" + playerName + "> " + partyMessage);

        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Commands.Party.Chat.Prefix", new Object[] {playerName}) + partyMessage);
        }
    }
}
