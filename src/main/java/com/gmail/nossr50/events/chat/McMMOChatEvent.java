package com.gmail.nossr50.events.chat;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public abstract class McMMOChatEvent extends Event implements Cancellable {
    private boolean cancelled;
    private Plugin plugin;
    private String sender;
    private String displayName;
    private String message;

    protected McMMOChatEvent(Plugin plugin, String sender, String displayName, String message) {
        this.plugin = plugin;
        this.sender = sender;
        this.displayName = displayName;
        this.message = message;
    }

    protected McMMOChatEvent(Plugin plugin, String sender, String displayName, String message, boolean isAsync) {
        super(isAsync);
        this.plugin = plugin;
        this.sender = sender;
        this.displayName = displayName;
        this.message = message;
    }

    /**
     * @return The plugin responsible for this event, note this can be null
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * @return String name of the player who sent the chat, or "Console"
     */
    public String getSender() {
        return sender;
    }

    /**
     * @return String display name of the player who sent the chat, or "Console"
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return String message that will be sent
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param displayName String display name of the player who sent the chat
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @param message String message to be sent in chat
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /** Following are required for Cancellable **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /** Rest of file is required boilerplate for custom events **/
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
