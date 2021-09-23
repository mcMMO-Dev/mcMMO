package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import org.bukkit.World;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Blacklist certain features in certain worlds
 */
public class WorldBlacklist {
    private static final Set<String> blacklist = new HashSet<>();
    private final mcMMO plugin;

    public WorldBlacklist(mcMMO plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
        //Make the blacklist file if it doesn't exist
        String blackListFileName = "world_blacklist.txt";
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

    public static boolean isWorldBlacklisted(World world) {
        return blacklist.contains(world.getName().toLowerCase());
    }

    public static boolean isWorldBlacklisted(String worldName) {
        return blacklist.contains(worldName.toLowerCase());
    }
}
