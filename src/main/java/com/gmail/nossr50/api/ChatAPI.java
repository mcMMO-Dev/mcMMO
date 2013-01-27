package com.gmail.nossr50.api;

import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.ChatManager;

public final class ChatAPI {
    private ChatAPI() {}

    /**
     * Send a message to all members of a party
     * </br>
     * This function is designed for API usage.
     *
     * @param plugin The plugin sending the message
     * @param sender The name of the sender to display in the chat
     * @param party The name of the party to send to
     * @param message The message to send
     */
    public static void sendPartyChat(Plugin plugin, String sender, String party, String message) {
        ChatManager.handlePartyChat(plugin, PartyManager.getParty(party), sender, message);
    }

    /**
     * Send a message to all members of a party
     * </br>
     * This function is designed for API usage.
     *
     * @deprecated Replaced by sendPartyChat(Plugin, String, String, String)
     *
     * @param sender The name of the sender to display in the chat
     * @param party The name of the party to send to
     * @param message The message to send
     */
    @Deprecated
    public static void sendPartyChat(String sender, String party, String message) {
        sendPartyChat(null, party, sender, message);
    }

    /**
     * Send a message to administrators
     * </br>
     * This function is designed for API usage.
     *
     * @param plugin The plugin sending the message
     * @param sender The name of the sender to display in the chat
     * @param message The message to send
     */
    public static void sendAdminChat(Plugin plugin, String sender, String message) {
        ChatManager.handleAdminChat(plugin, sender, message);
    }

    /**
     * Send a message to administrators
     * </br>
     * This function is designed for API usage.
     *
     * @deprecated Replaced by sendAdminChat(Plugin, String, String)
     *
     * @param sender The name of the sender to display in the chat
     * @param message The message to send
     */
    @Deprecated
    public static void sendAdminChat(String sender, String message) {
        sendAdminChat(null, sender, message);
    }
}
