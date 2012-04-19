package com.gmail.nossr50.events.chat;

/**
 * Called when a chat is sent to a party channel
 */
public class McMMOPartyChatEvent extends McMMOChatEvent{
    private String party;

    public McMMOPartyChatEvent(String sender, String party, String message) {
        super(sender, message);
        this.party = party;
    }

    /**
     * @return String name of the party the message will be sent to
     */
    public String getParty() {
        return party;
    }
}
