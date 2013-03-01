package com.gmail.nossr50.chat;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;

public enum ChatMode {
    ADMIN(LocaleLoader.getString("Commands.AdminChat.On"), LocaleLoader.getString("Commands.AdminChat.Off")),
    PARTY(LocaleLoader.getString("Commands.Party.Chat.On"), LocaleLoader.getString("Commands.Party.Chat.Off"));

    private String enabledMessage;
    private String disabledMessage;

    private ChatMode(String enabledMessage, String disabledMessage) {
        this.enabledMessage  = enabledMessage;
        this.disabledMessage = disabledMessage;
    }

    public boolean isEnabled(McMMOPlayer mcMMOPlayer) {
        switch (this) {
            case ADMIN:
                return mcMMOPlayer.getAdminChatMode();

            case PARTY:
                return mcMMOPlayer.getPartyChatMode();

            default:
                return false;
        }
    }

    public void disable(McMMOPlayer mcMMOPlayer) {
        switch (this) {
            case ADMIN:
                mcMMOPlayer.setAdminChat(false);
                return;

            case PARTY:
                mcMMOPlayer.setPartyChat(false);
                return;

            default:
                return;
        }
    }

    public void enable(McMMOPlayer mcMMOPlayer) {
        switch (this) {
            case ADMIN:
                mcMMOPlayer.setAdminChat(true);
                mcMMOPlayer.setPartyChat(false);
                return;

            case PARTY:
                mcMMOPlayer.setPartyChat(true);
                mcMMOPlayer.setAdminChat(false);
                return;

            default:
                return;
        }
    }

    public String getEnabledMessage() {
        return enabledMessage;
    }

    public String getDisabledMessage() {
        return disabledMessage;
    }
}
