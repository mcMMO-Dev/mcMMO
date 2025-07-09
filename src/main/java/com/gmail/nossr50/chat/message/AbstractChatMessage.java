package com.gmail.nossr50.chat.message;

import com.gmail.nossr50.chat.author.Author;
import com.google.common.base.Objects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractChatMessage implements ChatMessage {

    protected final @NotNull Plugin pluginRef;
    protected final @NotNull Author author;
    protected final @NotNull String rawMessage;
    protected @NotNull TextComponent componentMessage;
    protected @NotNull Audience audience;

    public AbstractChatMessage(@NotNull Plugin pluginRef, @NotNull Author author,
            @NotNull Audience audience,
            @NotNull String rawMessage, @NotNull TextComponent componentMessage) {
        this.pluginRef = pluginRef;
        this.author = author;
        this.audience = audience;
        this.rawMessage = rawMessage;
        this.componentMessage = componentMessage;
    }

    @Override
    public @NotNull String rawMessage() {
        return rawMessage;
    }

    @Override
    public @NotNull Author getAuthor() {
        return author;
    }

    @Override
    public @NotNull Audience getAudience() {
        return audience;
    }

    @Override
    public void setAudience(@NotNull Audience newAudience) {
        audience = newAudience;
    }

    @Override
    public @NotNull TextComponent getChatMessage() {
        return componentMessage;
    }

    @Override
    public void setChatMessage(@NotNull TextComponent textComponent) {
        this.componentMessage = textComponent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractChatMessage that = (AbstractChatMessage) o;
        return Objects.equal(pluginRef, that.pluginRef) && Objects.equal(author, that.author)
                && Objects.equal(
                rawMessage, that.rawMessage) && Objects.equal(componentMessage,
                that.componentMessage) && Objects.equal(
                audience, that.audience);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pluginRef, author, rawMessage, componentMessage, audience);
    }
}
