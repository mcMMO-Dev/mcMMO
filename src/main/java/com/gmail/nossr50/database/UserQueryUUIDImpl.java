package com.gmail.nossr50.database;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class UserQueryUUIDImpl implements UserQueryUUID {
    private final @NotNull UUID uuid;

    public UserQueryUUIDImpl(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public @NotNull UserQueryType getType() {
        return UserQueryType.UUID;
    }

    @Override
    public @NotNull UUID getUUID() {
        return uuid;
    }
}
