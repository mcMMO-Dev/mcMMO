package com.gmail.nossr50.api;

import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.ChatManager;

public final class ChatAPI {
    private ChatAPI() {}

    /**
     * Send a message to all members of a party
     * </br>
     * This function is designed for API usage.
     *
     * @param sender The name of the sender to display in the chat
     * @param party The name of the party to send to
     * @param message The message to send
     */
    public static void sendPartyChat(String sender, String party, String message) {
        ChatManager.handlePartyChat(PartyManager.getParty(party), sender, message);
    }

    /**
     * Send a message to administrators
     * </br>
     * This function is designed for API usage.
     *
     * @param sender The name of the sender to display in the chat
     * @param message The message to send
     */
    public static void sendAdminChat(String sender, String message) {
        ChatManager.handleAdminChat(sender, message);
    }
}
