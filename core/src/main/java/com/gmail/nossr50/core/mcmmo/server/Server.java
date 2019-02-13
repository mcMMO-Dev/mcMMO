package com.gmail.nossr50.core.mcmmo.server;

import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.mcmmo.world.World;

/**
 * Represents the server and its state
 */
public interface Server {
    /**
     * Broadcasts a msg to every player on the server
     * @param msg the message to broadcast
     */
    void broadcast(String msg);

    /**
     * Gets the online players for this server
     * @return the online players for this server
     */
    Player[] getOnlinePlayers();

    /**
     * Gets the worlds for this server
     * @return the worlds for this server
     */
    World[] getWorlds();
}
