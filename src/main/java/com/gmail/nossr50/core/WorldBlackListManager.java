package com.gmail.nossr50.core;

import java.util.Collection;
import java.util.HashSet;

/**
 * Performs various duties relating to black listed worlds
 *
 * A world is identified solely by its name
 * The blacklist is simply a hash set of world names
 */
public class WorldBlackListManager {
    private HashSet<String> blackListedWorlds;

    public WorldBlackListManager() {
        this.blackListedWorlds = new HashSet<>();
    }

    /**
     * Add a world to the blacklist
     * @param worldName target world's name
     */
    public void addBlackListedWorld(String worldName) {
        blackListedWorlds.add(worldName);
    }

    /**
     * Add a collection of world names to the black list
     * @param worldNames collection of target world names to black list
     */
    public void addBlackListedWorlds(Collection<String> worldNames) {
        blackListedWorlds.addAll(worldNames);
    }

    /**
     * Check if a world is blacklisted
     * @param worldName target world's name
     * @return true if the world is blacklisted
     */
    public boolean isWorldBlacklisted(String worldName) {
        return blackListedWorlds.contains(worldName);
    }

    /**
     * Get the hash set of blacklisted worlds
     * @return the blacklisted worlds
     */
    public HashSet<String> getBlackListedWorlds() {
        return blackListedWorlds;
    }

    /**
     * Replace the hash set of blacklisted worlds
     * @param blackListedWorlds replacement hashset
     */
    public void setBlackListedWorlds(HashSet<String> blackListedWorlds) {
        this.blackListedWorlds = blackListedWorlds;
    }

    /**
     * Clear worlds from the blacklist
     */
    public void clearBlackList() {
        this.blackListedWorlds.clear();
    }
}
