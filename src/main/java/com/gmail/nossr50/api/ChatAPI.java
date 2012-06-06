package com.gmail.nossr50.api;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.McMMO;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class ChatAPI {
    /**
     * Send a message to all members of a party
     * </br>
     * This function is designed for API usage.
     *
     * @param sender The name of the sender to display in the chat
     * @param party The name of the party to send to
     * @param message The message to send
     */
    public void sendPartyChat(String sender, String party, String message) {
        McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent(sender, party, message);
        McMMO.p.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        String pPrefix = ChatColor.GREEN + "(" + ChatColor.WHITE + chatEvent.getSender() + ChatColor.GREEN + ") ";

        McMMO.p.getLogger().info("[P](" + chatEvent.getParty() + ")" + "<" + chatEvent.getSender() + "> " + chatEvent.getMessage());

        for (Player player : McMMO.p.getServer().getOnlinePlayers()) {
            if (Users.getProfile(player).inParty()) {
                if (Users.getProfile(player).getParty().equalsIgnoreCase(chatEvent.getParty())) {
                    player.sendMessage(pPrefix + chatEvent.getMessage());
                }
            }
        }
    }

    /**
     * Send a message to administrators
     * </br>
     * This function is designed for API usage.
     *
     * @param sender The name of the sender to display in the chat
     * @param message The message to send
     */
    public void sendAdminChat(String sender, String message) {
        McMMOAdminChatEvent chatEvent = new McMMOAdminChatEvent(sender, message);
        McMMO.p.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        String aPrefix = ChatColor.AQUA + "{" + ChatColor.WHITE + chatEvent.getSender() + ChatColor.AQUA + "} ";

        McMMO.p.getLogger().info("[A]<" + chatEvent.getSender() + "> " + chatEvent.getMessage());

        for (Player player : McMMO.p.getServer().getOnlinePlayers()) {
            if (Permissions.getInstance().adminChat(player) || player.isOp())
                player.sendMessage(aPrefix + chatEvent.getMessage());
        }
    }
}
