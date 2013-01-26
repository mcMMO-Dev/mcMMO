package com.gmail.nossr50.events.chat;

import org.bukkit.plugin.Plugin;

/**
 * Called when a chat is sent to the admin chat channel
 */
public class McMMOAdminChatEvent extends McMMOChatEvent {
    public McMMOAdminChatEvent(Plugin plugin, String sender, String message) {
        super(plugin, sender, message);
    }

    public McMMOAdminChatEvent(String sender, String message) {
        this(null, sender, message);
    }
}
