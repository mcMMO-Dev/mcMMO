package com.gmail.nossr50.events.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.util.text.McMMOMessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

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
    protected McMMOMessageType chatMessageType;

    protected Component notificationTextComponent;
    protected final NotificationType notificationType;

    protected final Player player;

    public McMMOPlayerNotificationEvent(Player player, NotificationType notificationType,
            Component notificationTextComponent, McMMOMessageType chatMessageType,
            boolean isMessageAlsoBeingSentToChat) {
        super(false);
        this.player = player;
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

    public Player getPlayer() {
        return player;
    }

    public Component getNotificationTextComponent() {
        return notificationTextComponent;
    }

    public void setNotificationTextComponent(Component notificationTextComponent) {
        this.notificationTextComponent = notificationTextComponent;
    }

    public McMMOMessageType getChatMessageType() {
        return chatMessageType;
    }

    public void setChatMessageType(McMMOMessageType chatMessageType) {
        this.chatMessageType = chatMessageType;
    }

    /**
     * The notification type for this event
     *
     * @return this event's notification type
     */
    public NotificationType getEventNotificationType() {
        return notificationType;
    }

    /*
     * Custom Event Boilerplate
     */

    @Override
    public @NotNull HandlerList getHandlers() {
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
