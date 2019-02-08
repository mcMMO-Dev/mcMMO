package com.gmail.nossr50.chat;

import com.gmail.nossr50.datatypes.chat.ChatMode;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class ChatManagerFactory {
    private static final HashMap<Plugin, AdminChatManager> adminChatManagers = new HashMap<Plugin, AdminChatManager>();
    private static final HashMap<Plugin, PartyChatManager> partyChatManagers = new HashMap<Plugin, PartyChatManager>();

    public static ChatManager getChatManager(Plugin plugin, ChatMode mode) {
        switch (mode) {
            case ADMIN:
                if (!adminChatManagers.containsKey(plugin)) {
                    adminChatManagers.put(plugin, new AdminChatManager(plugin));
                }

                return adminChatManagers.get(plugin);
            case PARTY:
                if (!partyChatManagers.containsKey(plugin)) {
                    partyChatManagers.put(plugin, new PartyChatManager(plugin));
                }

                return partyChatManagers.get(plugin);
            default:
                return null;
        }
    }
}
