package com.gmail.nossr50.events.chat;

import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class McMMOChatEnableEvent extends McMMOSwitchChatEvent {

    public McMMOChatEnableEvent(Plugin plugin, UUID playerUUID, String playerName, String chatName) {
        super(plugin, playerUUID, playerName, chatName);
    }

    public McMMOChatEnableEvent(Plugin plugin, UUID playerUUID, String playerName, String chatName, boolean isAsync) {
        super(plugin, playerUUID, playerName, chatName, isAsync);
    }
}
