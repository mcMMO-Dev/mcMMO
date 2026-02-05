package com.gmail.nossr50.datatypes.chat;

import com.gmail.nossr50.locale.LocaleLoader;
import org.jetbrains.annotations.Nullable;

public enum ChatChannel {
    ADMIN(LocaleLoader.getString("Commands.AdminChat.On"),
            LocaleLoader.getString("Commands.AdminChat.Off")),
    PARTY(LocaleLoader.getString("Commands.Party.Chat.On"),
            LocaleLoader.getString("Commands.Party.Chat.Off")),
    PARTY_OFFICER(null, null),
    NONE(null, null);

    private final String enabledMessage;
    private final String disabledMessage;

    ChatChannel(@Nullable String enabledMessage, @Nullable String disabledMessage) {
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
