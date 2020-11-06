package com.gmail.nossr50.chat.author;

import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerAuthor extends AbstractPlayerAuthor {

    public PlayerAuthor(@NotNull Player player) {
        super(player);
    }

    @Override
    public @NotNull TextComponent getAuthoredComponentName(@NotNull ChatChannel chatChannel) {
        if(ChatConfig.getInstance().useDisplayNames(chatChannel)) {
            return getComponentDisplayName();
        } else {
            return getComponentUserName();
        }
    }

    @Override
    public @NotNull String getAuthoredName(@NotNull ChatChannel chatChannel) {
        if(ChatConfig.getInstance().useDisplayNames(chatChannel)) {
            return getPlayer().getDisplayName();
        } else {
            return getPlayer().getName();
        }
    }

}
