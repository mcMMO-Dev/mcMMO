package com.gmail.nossr50.core.events.chat;


/**
 * Called when a chat is sent to the admin chat channel
 */
public class McMMOAdminChatEvent extends McMMOChatEvent {
    public McMMOAdminChatEvent(String sender, String displayName, String message) {
        super(sender, displayName, message);
    }

    public McMMOAdminChatEvent(String sender, String displayName, String message, boolean isAsync) {
        super(sender, displayName, message, isAsync);
    }
}
