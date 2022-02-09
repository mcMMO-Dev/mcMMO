package com.gmail.nossr50.chat.author;

import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerAuthor extends AbstractPlayerAuthor {

    public PlayerAuthor(@NotNull Player player) {
        super(player);
    }

    @Override
    public @NotNull String getAuthoredName(@NotNull ChatChannel chatChannel) {
        return getSanitizedName(chatChannel, ChatConfig.getInstance().useDisplayNames(chatChannel));
    }

}
