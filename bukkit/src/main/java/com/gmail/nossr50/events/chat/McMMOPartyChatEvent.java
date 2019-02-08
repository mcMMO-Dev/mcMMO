package com.gmail.nossr50.events.chat;

import org.bukkit.plugin.Plugin;

/**
 * Called when a chat is sent to a party channel
 */
public class McMMOPartyChatEvent extends McMMOChatEvent {
    private String party;

    public McMMOPartyChatEvent(Plugin plugin, String sender, String displayName, String party, String message) {
        super(plugin, sender, displayName, message);
        this.party = party;
    }

    public McMMOPartyChatEvent(Plugin plugin, String sender, String displayName, String party, String message, boolean isAsync) {
        super(plugin, sender, displayName, message, isAsync);
        this.party = party;
    }

    /**
     * @return String name of the party the message will be sent to
     */
    public String getParty() {
        return party;
    }
}
