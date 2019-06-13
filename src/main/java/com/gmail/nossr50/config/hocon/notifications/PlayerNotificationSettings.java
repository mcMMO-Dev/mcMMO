package com.gmail.nossr50.config.hocon.notifications;

public class PlayerNotificationSettings {

    private boolean enabled;
    private boolean sendToChat;
    private boolean sendToActionBar;

    public PlayerNotificationSettings(boolean enabled, boolean sendToChat, boolean sendToActionBar) {
        this.enabled = enabled;
        this.sendToChat = sendToChat;
        this.sendToActionBar = sendToActionBar;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setSendToChat(boolean sendToChat) {
        this.sendToChat = sendToChat;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isSendToChat() {
        if(enabled)
            return sendToChat;
        else
            return false;
    }

    public boolean isSendToActionBar() {
        if(enabled)
            return sendToActionBar;
        else
            return false;
    }

    public void setSendToActionBar(boolean sendToActionBar) {
        this.sendToActionBar = sendToActionBar;
    }
}