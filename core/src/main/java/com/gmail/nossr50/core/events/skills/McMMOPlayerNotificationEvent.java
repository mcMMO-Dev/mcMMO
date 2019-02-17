package com.gmail.nossr50.core.events.skills;

import com.gmail.nossr50.core.datatypes.interactions.NotificationType;
import com.gmail.nossr50.core.mcmmo.entity.Player;

import java.awt.*;

/**
 * This event is sent for when mcMMO informs a player about various important information
 */
public class McMMOPlayerNotificationEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected final NotificationType notificationType;
    protected ChatMessageType chatMessageType;
    protected TextComponent notificationTextComponent;
    private boolean isCancelled;
    /*
     * Messages can be sent to both places, as configured in advanced.yml
     * If isBeingSentToActionBar is false, then messages will ALWAYS be sent to the chat bar
     * isMessageAlsoBeingSentToChat just indicates a copy of that message will be sent to chat
     */
    private boolean isMessageAlsoBeingSentToChat;

    public McMMOPlayerNotificationEvent(Player who, NotificationType notificationType, TextComponent notificationTextComponent, ChatMessageType chatMessageType, boolean isMessageAlsoBeingSentToChat) {
        super(who);
        this.notificationType = notificationType;
        this.notificationTextComponent = notificationTextComponent;
        this.chatMessageType = chatMessageType;
        this.isMessageAlsoBeingSentToChat = isMessageAlsoBeingSentToChat;
        isCancelled = false;
    }

    /*
     * Getters & Setters
     */

    public static HandlerList getHandlerList() {
        return handlers;
    }

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

    /*
     * Custom Event Boilerplate
     */

    /**
     * The notification type for this event
     *
     * @return this event's notification type
     */
    public NotificationType getEventNotificationType() {
        return notificationType;
    }

    @Override
    public HandlerList getHandlers() {
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
