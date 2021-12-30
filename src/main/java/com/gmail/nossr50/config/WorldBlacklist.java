package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Blacklist certain features in certain worlds
 */
public class WorldBlacklist {
    private final Set<String> blacklist = new HashSet<>();
    private static WorldBlacklist instance;
    private final mcMMO plugin;
    private final String blackListFileName = "world_blacklist.txt";

    public WorldBlacklist(@NotNull mcMMO plugin) {
        WorldBlacklist.instance = this;
        this.plugin = plugin;
        init();
    }

    public void init() {
        //Make the blacklist file if it doesn't exist
        File blackListFile = new File(plugin.getDataFolder() + File.separator + blackListFileName);

        try {
            if (!blackListFile.exists())
                blackListFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //Load up the blacklist
        try (BufferedReader reader = new BufferedReader(new FileReader(blackListFile))) {
            String currentLine;
            while((currentLine = reader.readLine()) != null)
                if (!currentLine.isEmpty())
                    blacklist.add(currentLine.toLowerCase());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isWorldBlacklisted(@NotNull World world) {
        return isWorldBlacklisted(world.getName());
    }

    public static boolean isWorldBlacklisted(@NotNull String worldName) {
        return instance.blacklist.contains(worldName.toLowerCase(Locale.ROOT));
    }
}
