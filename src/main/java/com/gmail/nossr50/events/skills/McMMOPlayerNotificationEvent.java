package com.gmail.nossr50.events.skills;

import com.gmail.nossr50.config.hocon.notifications.PlayerNotificationSettings;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is sent for when mcMMO informs a player about various important information
 * Contains a TextComponent if the message contains complex features such as hover objects, clickables, etc
 * TextComponent is not guaranteed to exist, but often it does
 */
public class McMMOPlayerNotificationEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private PlayerNotificationSettings playerNotificationSettings;
    private Player recipient;
    private boolean isCancelled;
    private NotificationType notificationType;
    private TextComponent textComponent;
    private String notificationText;

    public McMMOPlayerNotificationEvent(NotificationType notificationType, Player recipient, PlayerNotificationSettings playerNotificationSettings, String notificationText) {
        super(false);
        this.notificationType = notificationType;
        this.recipient = recipient;
        this.playerNotificationSettings = playerNotificationSettings;
        this.textComponent = null;
        this.notificationText = notificationText;
        isCancelled = false;
    }

    public McMMOPlayerNotificationEvent(NotificationType notificationType, Player recipient, PlayerNotificationSettings playerNotificationSettings, TextComponent textComponent) {
        super(false);
        this.notificationType = notificationType;
        this.recipient = recipient;
        this.playerNotificationSettings = playerNotificationSettings;
        this.textComponent = textComponent;
        this.notificationText = textComponent.getText();
        isCancelled = false;
    }

    /*
     * Getters & Setters
     */

    public PlayerNotificationSettings getPlayerNotificationSettings() {
        return playerNotificationSettings;
    }

    /**
     * Notification text
     * Note that most messages sent by mcMMO use Text Components instead, in which case this will be the text version of the text component
     * @return the notification text of this event
     */
    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    /**
     * Whether or not this notification event uses a text component
     * @return true if this notification has a text component
     */
    public boolean hasTextComponent() {
        return textComponent != null;
    }

    /**
     * The recipient of this notification
     * @return the recipient of this notification
     */
    public Player getRecipient() {
        return recipient;
    }

    public void setRecipient(Player recipient) {
        this.recipient = recipient;
    }

    /**
     * Is this notification being sent to chat
     * @return true if being sent to chat
     */
    public boolean isBeingSentToChat() {
        return playerNotificationSettings.isSendToChat();
    }

    /**
     * Is this notification being sent to action bar
     * @return true if being sent to action bar
     */
    public boolean isBeingSentToActionBar() {
        return playerNotificationSettings.isSendToActionBar();
    }

    /**
     * Change whether or not this notification sends to chat
     * @param sendToChat new value
     */
    public void setSendToChat(boolean sendToChat) {
        playerNotificationSettings.setSendToChat(sendToChat);
    }

    /**
     * Change whether or not this notification sends to action bar
     * @param sendToActionBar new value
     */
    public void setSendToActionBar(boolean sendToActionBar) {
        playerNotificationSettings.setSendToActionBar(sendToActionBar);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the text component if it exists
     * @return the text component if it exists
     */
    public TextComponent getNotificationTextComponent() {
        return textComponent;
    }

    /**
     * Override the text component for this event
     * Note that not all events are using a text component
     * If you set one and it didn't exist before, then mcMMO will use the text component instead of the raw message
     * @param textComponent new text component
     */
    public void setNotificationTextComponent(TextComponent textComponent) {
        this.textComponent = textComponent;
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
