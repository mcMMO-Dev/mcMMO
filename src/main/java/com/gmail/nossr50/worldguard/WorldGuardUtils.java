package com.gmail.nossr50;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

public class WorldGuardUtils {
    public static boolean isWorldGuardLoaded()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        try {
            // WorldGuard may not be loaded
            if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
                return false; // Maybe you want throw an exception instead
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Silently Fail
            //mcMMO.p.getLogger().severe("Failed to detect worldguard.");
            return false;
        }

        return true;
    }
}
