package com.gmail.nossr50.core.worldguard;

public class WorldGuardUtils {
    private static WorldGuardPlugin worldGuardPluginRef;
    private static boolean isLoaded = false;
    private static boolean hasWarned = false;

    public static boolean isWorldGuardLoaded() {
        WorldGuardPlugin plugin = getWorldGuard();

        try {
            // WorldGuard may not be loaded
            if (plugin == null) {
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

    private static WorldGuardPlugin getWorldGuard() {
        if (isLoaded)
            return worldGuardPluginRef;

        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin instanceof WorldGuardPlugin) {
            if (plugin.getDescription().getVersion().startsWith("7")) {
                worldGuardPluginRef = (WorldGuardPlugin) plugin;

                if (worldGuardPluginRef != null)
                    isLoaded = true;

            } else {
                if (!hasWarned) {
                    mcMMO.p.getLogger().severe("mcMMO only supports WorldGuard version 7! Make sure you have WG 7! This warning will not appear again.");
                    hasWarned = true;
                }
            }
        }

        return worldGuardPluginRef;
    }
}
