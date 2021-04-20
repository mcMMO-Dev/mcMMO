package com.gmail.nossr50.database;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface UserQueryUUID extends UserQuery {

    @NotNull UUID getUUID();

}
