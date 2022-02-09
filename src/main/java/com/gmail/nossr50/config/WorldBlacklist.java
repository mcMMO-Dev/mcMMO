package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import org.bukkit.World;

import java.io.*;
import java.util.ArrayList;

/**
 * Blacklist certain features in certain worlds
 */
public class WorldBlacklist {
    private static ArrayList<String> blacklist;
    private final mcMMO plugin;

    private final String blackListFileName = "world_blacklist.txt";

    public WorldBlacklist(mcMMO plugin) {
        this.plugin = plugin;
        blacklist = new ArrayList<>();
        init();
    }

    public static boolean isWorldBlacklisted(World world) {

        for (String s : blacklist) {
            if (world.getName().equalsIgnoreCase(s))
                return true;
        }

        return false;
    }

    public void init() {
        //Make the blacklist file if it doesn't exist
        File blackListFile = new File(plugin.getDataFolder() + File.separator + blackListFileName);

        try {
            if (!blackListFile.exists())
                blackListFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Load up the blacklist
        loadBlacklist(blackListFile);
        //registerFlags();
    }

    private void loadBlacklist(File blackListFile) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(blackListFile);
            bufferedReader = new BufferedReader(fileReader);

            String currentLine;

            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.length() == 0)
                    continue;

                if (!blacklist.contains(currentLine))
                    blacklist.add(currentLine);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Close readers
            closeRead(bufferedReader);
            closeRead(fileReader);
        }

        plugin.getLogger().info(blacklist.size() + " entries in mcMMO World Blacklist");
    }

    private void closeRead(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
