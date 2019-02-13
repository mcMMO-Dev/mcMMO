package com.gmail.nossr50.core.chat;

import com.gmail.nossr50.core.config.Config;
import com.gmail.nossr50.core.events.chat.McMMOAdminChatEvent;

public class AdminChatManager extends ChatManager {
    protected AdminChatManager() {
        super(Config.getInstance().getAdminDisplayNames(), Config.getInstance().getAdminChatPrefix());
    }

    @Override
    public void handleChat(String senderName, String displayName, String message, boolean isAsync) {
        handleChat(new McMMOAdminChatEvent(senderName, displayName, message, isAsync));
    }

    @Override
    protected void sendMessage() {
        plugin.getServer().broadcast(message, "mcmmo.chat.adminchat");
    }
}
