package com.gmail.nossr50.core.api.exceptions;

import com.gmail.nossr50.core.mcmmo.entity.Player;

public class McMMOPlayerNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 761917904993202836L;

    public McMMOPlayerNotFoundException(Player player) {
        super("McMMOPlayer object was not found for: " + player.getName() + " " + player.getUUID());
    }
}
