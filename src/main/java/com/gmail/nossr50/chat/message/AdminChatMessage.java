package com.gmail.nossr50.chat.message;

import com.gmail.nossr50.chat.author.Author;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class AdminChatMessage extends AbstractChatMessage {
    public AdminChatMessage(@NotNull Plugin pluginRef, @NotNull Author author,
            @NotNull Audience audience,
            @NotNull String rawMessage, @NotNull TextComponent componentMessage) {
        super(pluginRef, author, audience, rawMessage, componentMessage);
    }

    @Override
    public void sendMessage() {
        audience.sendMessage(author, componentMessage);
    }

    @Override
    public @NotNull String getAuthorDisplayName() {
        return author.getAuthoredName(ChatChannel.ADMIN);
    }
}
