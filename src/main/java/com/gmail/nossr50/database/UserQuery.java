package com.gmail.nossr50.database;

import org.jetbrains.annotations.NotNull;

public interface UserQuery {
    @NotNull UserQueryType getType();
}
