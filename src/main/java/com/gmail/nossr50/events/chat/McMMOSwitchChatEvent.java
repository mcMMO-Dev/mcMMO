package com.gmail.nossr50.events.chat;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class McMMOSwitchChatEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    private Plugin plugin;
    private UUID playerUUID;
    private String playerName;
    private String chatName;

    public McMMOSwitchChatEvent(Plugin plugin, UUID playerUUID, String playerName, String chatName) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.chatName = chatName;
    }

    public McMMOSwitchChatEvent(Plugin plugin, UUID playerUUID, String playerName, String chatName, boolean isAsync) {
        super(isAsync);
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.chatName = chatName;
    }

    /**
     * @return The plugin responsible for the event. Nullable
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * @return The uuid of the player switching chats
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * @return The name of the player switching chats
     */
    public String getPlayer() {
        return playerName;
    }

    /**
     * @return The party name, if applicable. Returns {@code null} if using AdminChat
     */
    public String getChatName() {
        return chatName;
    }

    /**
     * Following are required for Cancellable
     **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Rest of file is required boilerplate for custom events
     **/
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
