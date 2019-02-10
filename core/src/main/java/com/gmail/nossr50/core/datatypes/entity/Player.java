package com.gmail.nossr50.core.datatypes.entity;

import com.gmail.nossr50.core.datatypes.Nameable;

/**
 * Players
 */
public interface Player extends Living, Nameable {

    /**
     * Players are not always online
     * @return true if the player is online
     */
    Boolean isOnline();

    /**
     * Gets the McMMOPlayer for this Player
     * @return the associated McMMOPlayer, can be null
     */
    McMMOPlayer getMcMMOPlayer();
}
