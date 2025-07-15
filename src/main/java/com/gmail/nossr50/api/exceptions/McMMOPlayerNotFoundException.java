package com.gmail.nossr50.api.exceptions;

import java.io.Serial;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class McMMOPlayerNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 761917904993202836L;

    public McMMOPlayerNotFoundException(@NotNull Player player) {
        super("McMMOPlayer object was not found for [NOTE: This can mean the profile is not loaded yet! : "
                + player.getName() + " " + player.getUniqueId());
    }
}
