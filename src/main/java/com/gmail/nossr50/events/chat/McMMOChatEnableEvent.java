package com.gmail.nossr50.events.chat;

import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class McMMOChatEnableEvent extends McMMOSwitchChatEvent {

    public McMMOChatEnableEvent(Plugin plugin, UUID playerUUID, String playerName) {
        super(plugin, playerUUID, playerName);
    }

    public McMMOChatEnableEvent(Plugin plugin, UUID playerUUID, String playerName, boolean isAsync) {
        super(plugin, playerUUID, playerName, isAsync);
    }
}
