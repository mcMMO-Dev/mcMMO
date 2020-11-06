package com.gmail.nossr50.chat.author;

import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.util.text.TextUtils;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ConsoleAuthor implements Author {
    private final UUID uuid;
    private final @NotNull String name;
    private final @NotNull TextComponent componentName;

    public ConsoleAuthor(@NotNull String name) {
        this.uuid = new UUID(0, 0);
        this.name = name;
        this.componentName = TextUtils.ofBungeeRawStrings(name);
    }

    //TODO: Think of a better solution later
    @Override
    public @NotNull TextComponent getAuthoredComponentName(@NotNull ChatChannel chatChannel) {
        return componentName;
    }

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
