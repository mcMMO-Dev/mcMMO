package com.gmail.nossr50.events.chat;

import com.gmail.nossr50.datatypes.party.Party;
import org.bukkit.plugin.Plugin;

/**
 * Called when a chat is sent to a party channel
 */
public class McMMOPartyChatEvent extends McMMOChatEvent {
    private Party party;

    public McMMOPartyChatEvent(Plugin plugin, String sender, String displayName, Party party, String message) {
        super(plugin, sender, displayName, message);
        this.party = party;
    }

    /**
     * @return String name of the party the message will be sent to
     */
    public Party getParty() {
        return party;
    }
}
