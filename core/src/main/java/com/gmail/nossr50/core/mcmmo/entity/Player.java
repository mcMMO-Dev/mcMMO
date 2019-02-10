package com.gmail.nossr50.core.mcmmo.entity;

import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.mcmmo.Nameable;

import java.util.UUID;

/**
 * Players
 */
public interface Player extends Living, Nameable {

    /**
     * Players are not always online
     *
     * @return true if the player is online
     */
    Boolean isOnline();

    /**
     * Gets the McMMOPlayer for this Player
     *
     * @return the associated McMMOPlayer, can be null
     */
    McMMOPlayer getMcMMOPlayer();
}
