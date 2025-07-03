package com.gmail.nossr50.worldguard;

import static org.bukkit.Bukkit.getServer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import java.util.ArrayList;
import org.bukkit.plugin.Plugin;

public class WorldGuardUtils {
    private static WorldGuardPlugin worldGuardPluginRef;
    private static boolean isLoaded = false;
    private static boolean detectedIncompatibleWG = false;
    private static final ArrayList<String> WGClassList;

    static {
        /*
            These are the classes mcMMO tries to hook into for WG support, if any of them are missing it is safe to consider WG is not compatible
            com.sk89q.worldedit.bukkit.BukkitAdapter
            com.sk89q.worldedit.bukkit.BukkitPlayer
            com.sk89q.worldguard.WorldGuard
            com.sk89q.worldguard.bukkit.WorldGuardPlugin
            com.sk89q.worldguard.protection.flags.registry.FlagConflictException
            com.sk89q.worldguard.protection.flags.registry.FlagRegistry
            com.sk89q.worldguard.protection.regions.RegionContainer
            com.sk89q.worldguard.protection.regions.RegionQuery
         */

        WGClassList = new ArrayList<>();
        WGClassList.add("com.sk89q.worldedit.bukkit.BukkitAdapter");
        WGClassList.add("com.sk89q.worldedit.bukkit.BukkitPlayer");
        WGClassList.add("com.sk89q.worldguard.WorldGuard");
        WGClassList.add("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
        WGClassList.add("com.sk89q.worldguard.protection.flags.registry.FlagConflictException");
        WGClassList.add("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
        WGClassList.add("com.sk89q.worldguard.protection.regions.RegionContainer");
        WGClassList.add("com.sk89q.worldguard.protection.regions.RegionQuery");
    }

    public static boolean isWorldGuardLoaded() {
        if (detectedIncompatibleWG) {
            return false;
        }

        worldGuardPluginRef = getWorldGuard();

        return isLoaded;
    }

    /**
     * Gets the instance of the WG plugin if its compatible Results are cached
     *
     * @return the instance of WG plugin, null if its not compatible or isn't present
     */
    private static WorldGuardPlugin getWorldGuard() {
        //WG plugin reference is already cached so just return it
        if (isLoaded) {
            return worldGuardPluginRef;
        }

        //Grab WG if it exists
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin == null) {
            //WG is not present
            detectedIncompatibleWG = true;
            LogUtils.debug(mcMMO.p.getLogger(), "WorldGuard was not detected.");
        } else {
            //Check that its actually of class WorldGuardPlugin
            if (plugin instanceof WorldGuardPlugin) {
                if (isCompatibleVersion(plugin)) {
                    worldGuardPluginRef = (WorldGuardPlugin) plugin;
                    isLoaded = true;
                }
            } else {
                //Plugin is not of the expected type
                markWGIncompatible();
            }
        }

        return worldGuardPluginRef;
    }

    /**
     * Checks to make sure the version of WG installed is compatible Does this by checking for
     * necessary WG classes via Reflection This does not guarantee compatibility, but it should help
     * reduce the chance that mcMMO tries to hook into WG and its not compatible
     *
     * @return true if the version of WG appears to be compatible
     */
    private static boolean isCompatibleVersion(Plugin plugin) {
        //Check that the version of WG is at least version 7.xx
        boolean allClassesFound = true;
        if (detectedIncompatibleWG) {
            return false;
        }

        if (!plugin.getDescription().getVersion().startsWith("7")) {
            markWGIncompatible();
        } else {
            //Use Reflection to check for a class not present in all versions of WG7
            for (String classString : WGClassList) {
                try {
                    Class<?> checkForClass = Class.forName(classString);
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    allClassesFound = false;
                    mcMMO.p.getLogger().severe("Missing WorldGuard class - " + classString);
                    markWGIncompatible();
                }
            }

            /*
             * If WG appears to have all of its classes we can then check to see if its been initialized properly
             */
            try {
                if (allClassesFound) {
                    if (!((SimpleFlagRegistry) WorldGuard.getInstance()
                            .getFlagRegistry()).isInitialized()) {
                        markWGIncompatible();
                        mcMMO.p.getLogger()
                                .severe("WG did not initialize properly, this can cause errors with mcMMO so mcMMO is disabling certain features.");
                    }
                }
            } catch (Exception e) {
                markWGIncompatible();
                e.printStackTrace();
            }
        }

        return !detectedIncompatibleWG;
    }

    /**
     * Mark WG as being incompatible to avoid unnecessary operations
     */
    private static void markWGIncompatible() {
        mcMMO.p.getLogger()
                .severe("You are using a version of WG that is not compatible with mcMMO, " +
                        "WG features for mcMMO will be disabled. mcMMO requires you to be using a new version of WG7 "
                        +
                        "in order for it to use WG features. Not all versions of WG7 are compatible.");
        mcMMO.p.getLogger()
                .severe("mcMMO will continue to function normally, but if you wish to use WG support you must use a compatible version.");
        detectedIncompatibleWG = true;
    }
}
