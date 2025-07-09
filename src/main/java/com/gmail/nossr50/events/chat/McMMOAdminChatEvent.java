package com.gmail.nossr50.events.chat;

import com.gmail.nossr50.chat.message.AbstractChatMessage;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a chat is sent to the admin chat channel
 */
public class McMMOAdminChatEvent extends McMMOChatEvent {
    public McMMOAdminChatEvent(@NotNull Plugin plugin, @NotNull AbstractChatMessage chatMessage,
            boolean isAsync) {
        super(plugin, chatMessage, isAsync);
    }
}
