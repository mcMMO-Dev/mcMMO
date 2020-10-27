package com.gmail.nossr50.chat.author;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ConsoleAuthor implements Author {
    private final UUID uuid;
    private final @NotNull String name;

    public ConsoleAuthor(@NotNull String name) {
        this.name = name;
        this.uuid = new UUID(0, 0);
    }

    @Override
    public @NotNull String getAuthoredName() {
        return name;
    }

    @Override
    public boolean isConsole() {
        return true;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public @NonNull UUID uuid() {
        return uuid;
    }
}
