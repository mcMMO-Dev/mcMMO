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
     * Broadcasts a message only to players with the appropriate permission node
     * @param msg the message to broadcast
     * @param permission the permission node required to hear the message
     */
    void broadcast(String msg, String permission);

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
