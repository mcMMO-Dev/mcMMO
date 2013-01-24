package com.gmail.nossr50.util;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;

public class ChatManager {
    private mcMMO plugin;
    private Player player;
    private String playerName;
    private AsyncPlayerChatEvent event;

    public ChatManager (mcMMO plugin, Player player, AsyncPlayerChatEvent event) {
        this.plugin = plugin;
        this.player = player;
        this.playerName = player.getName();
        this.event = event;
    }

    public void handleAdminChat() {
        McMMOAdminChatEvent chatEvent = new McMMOAdminChatEvent(playerName, event.getMessage());
        plugin.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        String adminMessage = chatEvent.getMessage();

        plugin.getLogger().info("[A]<" + playerName + "> " + adminMessage);

        for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
            if (Permissions.adminChat(otherPlayer) || otherPlayer.isOp()) {
                otherPlayer.sendMessage(LocaleLoader.getString("Commands.AdminChat.Prefix", new Object[] {playerName}) + adminMessage);
            }
        }

        event.setCancelled(true);
    }

    public void handlePartyChat() {
        Party party = Users.getProfile(player).getParty();

        if (party == null) {
            player.sendMessage(LocaleLoader.getString("Commands.Party.None"));
            return;
        }

        String partyName = party.getName();

        McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent(playerName, partyName, event.getMessage());
        plugin.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        String partyMessage = chatEvent.getMessage();

        plugin.getLogger().info("[P](" + partyName + ")" + "<" + playerName + "> " + partyMessage);

        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Commands.Party.Chat.Prefix", new Object[] {playerName}) + partyMessage);
        }

        event.setCancelled(true);
    }
}
