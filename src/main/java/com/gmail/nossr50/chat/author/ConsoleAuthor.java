package com.gmail.nossr50.chat.author;

import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.util.text.TextUtils;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class ConsoleAuthor implements Author {
    private final UUID uuid;
    private final @NotNull String name;

    public ConsoleAuthor(@NotNull String name) {
        this.uuid = new UUID(0, 0);
        this.name = TextUtils.sanitizeForSerializer(name);
    }

    //TODO: Think of a less clunky solution later
    @Override
    public @NotNull String getAuthoredName(@NotNull ChatChannel chatChannel) {
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
