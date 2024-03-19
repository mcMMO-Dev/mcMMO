package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.util.text.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ChatConfig extends BukkitConfig {
    private static ChatConfig instance;

    private ChatConfig() {
        super("chat.yml");
        validate();
    }

    public static ChatConfig getInstance() {
        if (instance == null) {
            instance = new ChatConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        //Sigh this old config system...
    }

    @Override
    protected boolean validateKeys() {
        return true;
    }

    public boolean isChatEnabled() {
        return config.getBoolean("Chat.Enable", true);
    }

    public boolean isChatChannelEnabled(@NotNull ChatChannel chatChannel) {
        String key = "Chat.Channels." + StringUtils.getCapitalized(chatChannel.toString()) + ".Enable";
        return config.getBoolean(key, true);
    }

    /**
     * Whether to use display names for players in target {@link ChatChannel}
     *
     * @param chatChannel target chat channel
     *
     * @return true if display names should be used
     */
    public boolean useDisplayNames(@NotNull ChatChannel chatChannel) {
        String key = "Chat.Channels." + StringUtils.getCapitalized(chatChannel.toString()) + ".Use_Display_Names";
        return config.getBoolean(key, true);
    }

    public boolean isConsoleIncludedInAudience(@NotNull ChatChannel chatChannel) {
        String key = "Chat.Channels." + StringUtils.getCapitalized(chatChannel.toString()) + ".Send_To_Console";
        return config.getBoolean(key, true);
    }


    public boolean isSpyingAutomatic() {
        return config.getBoolean("Chat.Channels.Party.Spies.Automatically_Enable_Spying", false);
    }

}