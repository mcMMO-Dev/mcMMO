package com.gmail.nossr50.config.hocon.notifications;

public class ActionBarNotificationSetting {

    public boolean enabled;
    public boolean sendCopyOfMessageToChat;
    public ActionBarNotificationSetting(boolean enabled, boolean sendCopyOfMessageToChat) {
        this.enabled = enabled;
        this.sendCopyOfMessageToChat = sendCopyOfMessageToChat;
    }
}