package com.gmail.nossr50.core.chat;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.config.MainConfig;
import com.gmail.nossr50.core.events.chat.McMMOAdminChatEvent;

public class AdminChatManager extends ChatManager {
    protected AdminChatManager() {
        super(MainConfig.getInstance().getAdminDisplayNames(), MainConfig.getInstance().getAdminChatPrefix());
    }

    @Override
    public void handleChat(String senderName, String displayName, String message, boolean isAsync) {
        handleChat(new McMMOAdminChatEvent(senderName, displayName, message, isAsync));
    }

    @Override
    protected void sendMessage() {
        McmmoCore.getServer().broadcast(message, "mcmmo.chat.adminchat");
    }
}
