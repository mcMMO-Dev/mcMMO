package com.gmail.nossr50.chat;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.datatypes.chat.ChatMode;

public final class ChatManagerFactory {
    private static final Map<Plugin, AdminChatManager> adminChatManagers = new HashMap<>();
    private static final Map<Plugin, PartyChatManager> partyChatManagers = new HashMap<>();

    /**
     * This is a static utility class, therefore we don't want any instances of
     * this class. Making the constructor private prevents accidents like that.
     */
    private ChatManagerFactory() {}
    
    @Nullable
    public static ChatManager getChatManager(@Nonnull Plugin plugin, @Nonnull ChatMode mode) {
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
