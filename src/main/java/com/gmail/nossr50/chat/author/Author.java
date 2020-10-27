package com.gmail.nossr50.chat.author;

import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

public interface Author extends Identity {

    /**
     * The name of this author
     * @return the name of this author
     */
    @NotNull String getAuthoredName();

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
