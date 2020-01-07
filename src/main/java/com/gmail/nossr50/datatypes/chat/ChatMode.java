package com.gmail.nossr50.datatypes.chat;

import com.gmail.nossr50.locale.LocaleLoader;

public enum ChatMode {
    ADMIN(LocaleLoader.getString("Commands.AdminChat.On"), LocaleLoader.getString("Commands.AdminChat.Off"), LocaleLoader.getString("Command.AdminChat.Cancelled")),
    PARTY(LocaleLoader.getString("Commands.Party.Chat.On"), LocaleLoader.getString("Commands.Party.Chat.Off"), LocaleLoader.getString("Command.Party.Chat.Cancelled"));

    private String enabledMessage;
    private String disabledMessage;
    private final String switchCancelledMessage;

    private ChatMode(String enabledMessage, String disabledMessage, String switchCancelledMessage) {
        this.enabledMessage  = enabledMessage;
        this.disabledMessage = disabledMessage;
        this.switchCancelledMessage = switchCancelledMessage;
    }

    public String getEnabledMessage() {
        return enabledMessage;
    }

    public String getDisabledMessage() {
        return disabledMessage;
    }

    public String getSwitchCancelledMessage() {
        return switchCancelledMessage;
    }
}
