package com.gmail.nossr50.events.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
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
    protected String notificationMessage;
    protected final NotificationType notificationType;

    public McMMOPlayerNotificationEvent(Player who, NotificationType notificationType, String notificationMessage) {
        super(who);
        this.notificationType = notificationType;
        this.notificationMessage = notificationMessage;
        isCancelled = false;
    }

    /*
     * Getters & Setters
     */

    /**
     * The notification type for this event
     * @return this event's notification type
     */
    public NotificationType getEventNotificationType() {
        return notificationType;
    }

    /**
     * The message delivered to players by this notification
     * @return the message that will be delivered to the player
     */
    public String getNotificationMessage() {
        return notificationMessage;
    }

    /**
     * Change the notification message
     * @param newMessage the new replacement message
     */
    public void setNotificationMessage(String newMessage) {
        notificationMessage = newMessage;
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
