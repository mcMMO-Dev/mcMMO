package com.gmail.nossr50.chat;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.mcMMO;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager {
    private final String ADMIN_CHAT_PERMISSION = "mcmmo.chat.adminchat";
    private final mcMMO pluginRef;

    public ChatManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public void processAdminChat(Player player, String message) {
        sendAdminChatMessage(new McMMOAdminChatEvent(pluginRef, player.getName(), player.getDisplayName(), message));
    }

    public void processAdminChat(String senderName, String displayName, String message) {
        sendAdminChatMessage(new McMMOAdminChatEvent(pluginRef, senderName, displayName, message));
    }

    public void processPartyChat(Party party, Player sender, String message) {
        sendPartyChatMessage(new McMMOPartyChatEvent(pluginRef, sender.getName(), sender.getDisplayName(), party, message));
    }

    public void processPartyChat(Party party, String senderName, String message) {
        sendPartyChatMessage(new McMMOPartyChatEvent(pluginRef, senderName, senderName, party, message));
    }

    private void sendAdminChatMessage(McMMOAdminChatEvent event) {
        pluginRef.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        String chatPrefix = pluginRef.getConfigManager().getConfigCommands().getAdminChatPrefix();
        String senderName = event.getSender();
        String displayName = pluginRef.getConfigManager().getConfigCommands().isUseDisplayNames() ? event.getDisplayName() : senderName;
        String message = pluginRef.getLocaleManager().formatString(chatPrefix, displayName) + " " + event.getMessage();

        pluginRef.getServer().broadcast(message, ADMIN_CHAT_PERMISSION);
    }

    private void sendPartyChatMessage(McMMOPartyChatEvent event) {
        pluginRef.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        Party party = event.getParty();
        String chatPrefix = pluginRef.getConfigManager().getConfigParty().getPartyChatPrefixFormat();
        String senderName = event.getSender();
        String displayName = pluginRef.getConfigManager().getConfigCommands().isUseDisplayNames() ? event.getDisplayName() : senderName;
        String message = pluginRef.getLocaleManager().formatString(chatPrefix, displayName) + " " + event.getMessage();

        if (pluginRef.getConfigManager().getConfigParty().isPartyLeaderColoredGold()
                && senderName.equalsIgnoreCase(party.getLeader().getPlayerName())) {
            message = message.replaceFirst(Pattern.quote(displayName), ChatColor.GOLD + Matcher.quoteReplacement(displayName) + ChatColor.RESET);
        }

        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(message);
        }

        if (party.getAlly() != null) {
            for (Player member : party.getAlly().getOnlineMembers()) {
                String allyPrefix = pluginRef.getLocaleManager().formatString(pluginRef.getConfigManager().getConfigParty().getPartyChatPrefixAlly());
                member.sendMessage(allyPrefix + message);
            }
        }

        pluginRef.getServer().getConsoleSender().sendMessage(ChatColor.stripColor("[mcMMO] [P]<" + party.getName() + ">" + message));

        /*
         * Party Chat Spying
         */
        for (BukkitMMOPlayer mcMMOPlayer : pluginRef.getUserManager().getPlayers()) {
            Player player = mcMMOPlayer.getNative();

            //Check for toggled players
            if (mcMMOPlayer.isPartyChatSpying()) {
                Party adminParty = mcMMOPlayer.getParty();

                //Only message admins not part of this party
                if (adminParty != null) {
                    //TODO: Incorporate JSON
                    if (adminParty != event.getParty())
                        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.AdminChatSpy.Chat", event.getParty(), message));
                } else {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Commands.AdminChatSpy.Chat", event.getParty(), message));
                }
            }
        }
    }
}
