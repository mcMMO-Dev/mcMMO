package com.gmail.nossr50.chat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.events.chat.McMMOChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;

public abstract class ChatManager {
    protected Plugin plugin;
    protected boolean useDisplayNames;
    protected String chatPrefix;

    protected String displayName;
    protected String message;

    protected ChatManager(Plugin plugin, boolean useDisplayNames, String chatPrefix) {
        this.plugin = plugin;
        this.useDisplayNames = useDisplayNames;
        this.chatPrefix = chatPrefix;
    }

    protected void handleChat(McMMOChatEvent event) {
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        displayName = useDisplayNames ? event.getDisplayName() : event.getSender();
        message = LocaleLoader.getString(chatPrefix, displayName) + event.getMessage();

        sendMessage();
    }

    public void handleChat(String senderName, String message) {
        handleChat(senderName, senderName, message, false);
    }

    public void handleChat(Player player, String message, boolean isAsync) {
        handleChat(player.getName(), player.getDisplayName(), message, isAsync);
    }

    public void handleChat(String senderName, String displayName, String message) {
        handleChat(senderName, displayName, message, false);
    }

    public abstract void handleChat(String senderName, String displayName, String message, boolean isAsync);

    protected abstract void sendMessage();
}
