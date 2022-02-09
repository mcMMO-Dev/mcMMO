package com.gmail.nossr50.database;

import org.jetbrains.annotations.NotNull;

public class UserQueryNameImpl implements UserQueryName {
    private final @NotNull String name;

    public UserQueryNameImpl(@NotNull String name) {
        this.name = name;
    }

    @Override
    public @NotNull UserQueryType getType() {
        return UserQueryType.NAME;
    }

    public @NotNull String getName() {
        return name;
    }
}
