package com.gmail.nossr50.events.chat;

import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class McMMOChatDisableEvent extends McMMOSwitchChatEvent {

    public McMMOChatDisableEvent(Plugin plugin, UUID playerUUID, String playerName, String chatName) {
        super(plugin, playerUUID, playerName, chatName);
    }

    public McMMOChatDisableEvent(Plugin plugin, UUID playerUUID, String playerName, String chatName, boolean isAsync) {
        super(plugin, playerUUID, playerName, chatName, isAsync);
    }
}
