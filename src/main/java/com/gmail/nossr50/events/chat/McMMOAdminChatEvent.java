package com.gmail.nossr50.events.chat;

/**
 * Called when a chat is sent to the admin chat channel
 */
public class McMMOAdminChatEvent extends McMMOChatEvent{

    public McMMOAdminChatEvent(String sender, String message) {
        super(sender, message);
    }
}
