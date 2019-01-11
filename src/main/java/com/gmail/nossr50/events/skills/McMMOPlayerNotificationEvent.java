package com.gmail.nossr50.events.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * This event is sent for when mcMMO informs a player about various important information
 */
public class McMMOPlayerNotificationEvent extends PlayerEvent implements Cancellable {
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();
    protected ChatMessageType chatMessageType;

    protected TextComponent notificationTextComponent;
    protected final NotificationType notificationType;

    public McMMOPlayerNotificationEvent(Player who, NotificationType notificationType, net.md_5.bungee.api.chat.TextComponent notificationTextComponent, ChatMessageType chatMessageType) {
        super(who);
        this.notificationType = notificationType;
        this.notificationTextComponent = notificationTextComponent;
        this.chatMessageType = chatMessageType;
        isCancelled = false;
    }

    /*
     * Getters & Setters
     */

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
