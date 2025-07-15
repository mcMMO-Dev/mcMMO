package com.gmail.nossr50.database;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class UserQueryFull implements UserQueryUUID, UserQueryName {

    private final @NotNull String name;
    private final @NotNull UUID uuid;

    public UserQueryFull(@NotNull String name, @NotNull UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public @NotNull UserQueryType getType() {
        return UserQueryType.UUID_AND_NAME;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull UUID getUUID() {
        return uuid;
    }
}
