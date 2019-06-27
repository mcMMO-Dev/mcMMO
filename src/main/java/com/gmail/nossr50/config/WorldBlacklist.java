package com.gmail.nossr50.config;


import org.bukkit.World;

/**
 * Blacklist certain features in certain worlds
 */
public class WorldBlacklist {

    public static boolean isWorldBlacklisted(World world) {
        for (String s : pluginRef.getConfigManager().getConfigWorldBlacklist().getBlackListedWorlds()) {
            if (world.getName().equalsIgnoreCase(s))
                return true;
        }

        return false;
    }
}
