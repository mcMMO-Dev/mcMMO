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
    private mcMMO plugin;

    private final String blackListFileName = "world_blacklist.txt";

    public WorldBlacklist(mcMMO plugin)
    {
        this.plugin = plugin;
        blacklist = new ArrayList<>();
        init();
    }

    public void init()
    {
        //Make the blacklist file if it doesn't exist
        File blackListFile = new File(plugin.getDataFolder() + File.separator + blackListFileName);

        try {
            if(!blackListFile.exists())
                blackListFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Load up the blacklist
        loadBlacklist(blackListFile);
        //registerFlags();
    }

    private void loadBlacklist(File blackListFile) {
        try {
            FileReader fileReader = new FileReader(blackListFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String currentLine;

            while((currentLine = bufferedReader.readLine()) != null)
            {
                if(currentLine.length() == 0)
                    continue;

                if(!blacklist.contains(currentLine))
                    blacklist.add(currentLine);
            }

            //Close readers
            bufferedReader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        plugin.getLogger().info(blacklist.size()+" entries in mcMMO World Blacklist");
    }

    public static boolean isWorldBlacklisted(World world)
    {

        for(String s : blacklist)
        {
            if(world.getName().equalsIgnoreCase(s))
                return true;
        }

        return false;
    }
}
