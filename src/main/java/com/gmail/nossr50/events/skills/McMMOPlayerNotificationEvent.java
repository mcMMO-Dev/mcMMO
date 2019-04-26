package com.gmail.nossr50.events.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is sent for when mcMMO informs a player about various important information
 */
public class McMMOPlayerNotificationEvent extends Event implements Cancellable {
    private boolean isCancelled;
    /*
     * Messages can be sent to both places, as configured in advanced.yml
     * If isBeingSentToActionBar is false, then messages will ALWAYS be sent to the chat bar
     * isMessageAlsoBeingSentToChat just indicates a copy of that message will be sent to chat
     */
    private boolean isMessageAlsoBeingSentToChat;

    private static final HandlerList handlers = new HandlerList();
    protected ChatMessageType chatMessageType;

    protected TextComponent notificationTextComponent;
    protected final NotificationType notificationType;

    public McMMOPlayerNotificationEvent(Player who, NotificationType notificationType, TextComponent notificationTextComponent, ChatMessageType chatMessageType, boolean isMessageAlsoBeingSentToChat) {
        super(false);
        this.notificationType = notificationType;
        this.notificationTextComponent = notificationTextComponent;
        this.chatMessageType = chatMessageType;
        this.isMessageAlsoBeingSentToChat = isMessageAlsoBeingSentToChat;
        isCancelled = false;
    }

    /*
     * Getters & Setters
     */

    public boolean isMessageAlsoBeingSentToChat() {
        return isMessageAlsoBeingSentToChat;
    }

    public void setMessageAlsoBeingSentToChat(boolean messageAlsoBeingSentToChat) {
        isMessageAlsoBeingSentToChat = messageAlsoBeingSentToChat;
    }

    public TextComponent getNotificationTextComponent() {
        return notificationTextComponent;
    }

    public void setNotificationTextComponent(TextComponent notificationTextComponent) {
        this.notificationTextComponent = notificationTextComponent;
    }

    public ChatMessageType getChatMessageType() {
        return chatMessageType;
    }

    public void setChatMessageType(ChatMessageType chatMessageType) {
        this.chatMessageType = chatMessageType;
    }

    /**
     * The notification type for this event
     * @return this event's notification type
     */
    public NotificationType getEventNotificationType() {
        return notificationType;
    }

    /*
     * Custom Event Boilerplate
     */

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /*
     * Cancellable Interface Boilerplate
     */

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }
}
