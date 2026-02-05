package com.gmail.nossr50.events.chat;

import com.gmail.nossr50.chat.author.Author;
import com.gmail.nossr50.chat.message.AbstractChatMessage;
import com.gmail.nossr50.chat.message.ChatMessage;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class McMMOChatEvent extends Event implements Cancellable {
    private boolean cancelled;
    protected final @NotNull Plugin plugin;
    protected final @NotNull AbstractChatMessage chatMessage;

    protected McMMOChatEvent(@NotNull Plugin plugin, @NotNull AbstractChatMessage chatMessage,
            boolean isAsync) {
        super(isAsync);
        this.plugin = plugin;
        this.chatMessage = chatMessage;
    }

    /**
     * The {@link Author} of this message
     *
     * @return the {@link Author} of this message
     */
    public @NotNull Author getAuthor() {
        return chatMessage.getAuthor();
    }

    /**
     * The {@link Audience} for this message
     *
     * @return the {@link Audience} for this message
     */
    public @NotNull Audience getAudience() {
        return chatMessage.getAudience();
    }

    /**
     * Set the {@link Audience} for this message
     *
     * @param audience target {@link Audience}
     */
    public void setAudience(@NotNull Audience audience) {
        chatMessage.setAudience(audience);
    }

    /**
     * @return The plugin responsible for this event
     */
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    /**
     * The name of the author Will return the display name if mcMMO chat config is set to, otherwise
     * returns the players Mojang registered nickname
     *
     * @return the author's name
     */
    public @NotNull String getDisplayName(ChatChannel chatChannel) {
        return getAuthor().getAuthoredName(chatChannel);
    }

    /**
     * Don't use this method
     *
     * @return The raw message
     * @deprecated use {@link #getComponentMessage()} instead
     */
    @Deprecated
    public @NotNull String getMessage() {
        return chatMessage.rawMessage();
    }

    /**
     * The original message typed by the player before any formatting The raw message is immutable
     *
     * @return the message as it was typed by the player, this is before any formatting
     */
    public @NotNull String getRawMessage() {
        return chatMessage.rawMessage();
    }

    /**
     * The {@link TextComponent} as it will be sent to all players which should include formatting
     * such as adding chat prefixes, player names, etc
     *
     * @return the message that will be sent to the {@link Audience}
     */
    public @NotNull TextComponent getComponentMessage() {
        return chatMessage.getChatMessage();
    }

    /**
     * This will be the final message sent to the audience, this should be the message after its
     * been formatted and has had player names added to it etc
     *
     * @param chatMessage the new chat message
     */
    public void setMessagePayload(@NotNull TextComponent chatMessage) {
        this.chatMessage.setChatMessage(chatMessage);
    }

    /**
     * @param message Adjusts the final message sent to players in the party
     * @deprecated use {{@link #setMessagePayload(TextComponent)}}
     */
    @Deprecated
    public void setMessage(@NotNull String message) {
        chatMessage.setChatMessage(Component.text(message));
    }

    /**
     * Following are required for Cancellable
     **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Rest of file is required boilerplate for custom events
     **/
    private static final @NotNull HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * The {@link ChatMessage}
     *
     * @return the chat message
     */
    public @NotNull ChatMessage getChatMessage() {
        return chatMessage;
    }
}
