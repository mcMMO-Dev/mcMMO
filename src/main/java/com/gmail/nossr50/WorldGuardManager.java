package com.gmail.nossr50;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class WorldGuardManager {
    // StateFlag with the name "my-custom-flag", which defaults to "allow"
    public static final Flag MCMMO_DISABLE_WG_FLAG = new StateFlag("mcmmo-off", false);
    public static final Flag MCMMO_XPOFF_WG_FLAG = new StateFlag("mcmmo-noxp", false);

    private static WorldGuardPlugin worldGuardPlugin;

    /*public static boolean isWgFlagActive(Location location)
    {
        if(flagsRegistered)
        {
            if(getWorldGuard() != null)
            {
                WorldGuardPlugin worldGuard = getWorldGuard();
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();

                ApplicableRegionSet set = query.getApplicableRegions();

               *//* if (!query.testState(location, (LocalPlayer)null, MCMMO_DISABLE_WG_FLAG)) {
                    // Can't build
                }*//*
            }
        }
    }

    private static WorldGuardPlugin getWorldGuard() {
        if(worldGuardPlugin != null)
            return worldGuardPlugin;

        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        worldGuardPlugin = (WorldGuardPlugin) plugin;
        return worldGuardPlugin;
    }

    private static void registerFlags()
    {
        if(getWorldGuard() == null)
            return;

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            // register our flag with the registry
            registry.register(MCMMO_DISABLE_WG_FLAG);
            flagsRegistered = true;
        } catch (FlagConflictException e) {
            e.printStackTrace();
            // some other plugin registered a flag by the same name already.
            // you may want to re-register with a different name, but this
            // could cause issues with saved flags in region files. it's better
            // to print a message to let the server admin know of the conflict
        }
    }*/
}
