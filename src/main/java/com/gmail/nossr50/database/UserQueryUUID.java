package com.gmail.nossr50.database;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface UserQueryUUID extends UserQuery {

    @NotNull UUID getUUID();

}
