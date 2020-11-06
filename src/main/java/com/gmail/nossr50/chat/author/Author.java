package com.gmail.nossr50.chat.author;

import com.gmail.nossr50.datatypes.chat.ChatChannel;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface Author extends Identity {

    /**
     * The name of this author as used in mcMMO chat
     * This is the {@link TextComponent} representation of the users current chat username
     * This can either be the player's display name or the player's official registered nickname with Mojang it depends on the servers chat settings for mcMMO
     *
     * NOTE:
     * mcMMO doesn't transform a players name into a component when creating the chat message, instead it converts the whole string from raw legacy text (including md5 stuff) -> TextComponent via {@link net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer}
     * This method is just provided for convenience, it uses lazy initialization
     *
     * @param chatChannel which chat channel this is going to
     * @return The name of this author as used in mcMMO chat
     */
    @NotNull TextComponent getAuthoredComponentName(@NotNull ChatChannel chatChannel);

    /**
     * The name of this author as used in mcMMO chat
     * This is the {@link String} representation of the users current chat username
     * This can either be the player's display name or the player's official registered nickname with Mojang it depends on the servers chat settings for mcMMO
     *
     * @param chatChannel which chat channel this is going to
     * @return The name of this author as used in mcMMO chat
     */
    @NotNull String getAuthoredName(@NotNull ChatChannel chatChannel);

    /**
     * Whether or not this author is a {@link org.bukkit.command.ConsoleCommandSender}
     *
     * @return true if this author is the console
     */
    boolean isConsole();

    /**
     * Whether or not this author is a {@link org.bukkit.entity.Player}
     * @return true if this author is a player
     */
    boolean isPlayer();
}
