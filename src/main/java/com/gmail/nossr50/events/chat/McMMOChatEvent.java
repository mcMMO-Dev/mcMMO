package com.gmail.nossr50.events.chat;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class McMMOChatEvent extends Event implements Cancellable{
    private boolean cancelled;
    private String sender;
    private String message;

    public McMMOChatEvent(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    /**
     * @return String name of the player who sent the chat, or "Console"
     */
    public String getSender() {
        return sender;
    }

    /**
     * @return String message that will be sent
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message String message to be sent in chat
     */
    public void setMessage(String message) {
        this.message = message;
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

    /** Following are required for Cancellable **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
