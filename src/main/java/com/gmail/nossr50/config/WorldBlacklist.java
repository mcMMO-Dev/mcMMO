package com.gmail.nossr50.config;


import com.gmail.nossr50.mcMMO;
import org.bukkit.World;

import java.io.*;
import java.util.ArrayList;

/**
 * Blacklist certain features in certain worlds
 */
public class WorldBlacklist {

    public static boolean isWorldBlacklisted(World world) {

        for (String s : mcMMO.getConfigManager().getConfigWorldBlacklist().getBlackListedWorlds()) {
            if (world.getName().equalsIgnoreCase(s))
                return true;
        }

        return false;
    }
}
