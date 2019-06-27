package com.gmail.nossr50.datatypes.chat;

public enum ChatMode {
    ADMIN(pluginRef.getLocaleManager().getString("Commands.AdminChat.On"), pluginRef.getLocaleManager().getString("Commands.AdminChat.Off")),
    PARTY(pluginRef.getLocaleManager().getString("Commands.Party.Chat.On"), pluginRef.getLocaleManager().getString("Commands.Party.Chat.Off"));

    private String enabledMessage;
    private String disabledMessage;

    ChatMode(String enabledMessage, String disabledMessage) {
        this.enabledMessage = enabledMessage;
        this.disabledMessage = disabledMessage;
    }

    public String getEnabledMessage() {
        return enabledMessage;
    }

    public String getDisabledMessage() {
        return disabledMessage;
    }
}
