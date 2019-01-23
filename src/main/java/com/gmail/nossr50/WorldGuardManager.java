package com.gmail.nossr50;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

public class WorldGuardManager {

    private static WorldGuardManager instance;
    private Plugin worldGuardPluginRef;
    private boolean flagsRegistered = false;

    public WorldGuardManager()
    {
        instance = this;

        init();
    }

    public void init()
    {
        if(getWorldGuard() != null)
        {
            worldGuardPluginRef = getWorldGuard();
            registerFlags();
        }
    }

    public boolean hasMainFlag(Player player)
    {
        //Not sure when we're supposed to know when WorldGuard is loaded
        if(!flagsRegistered)
            registerFlags();

        BukkitPlayer localPlayer = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.util.Location loc = localPlayer.getLocation();

        if(flagsRegistered)
        {
            if(getWorldGuard() != null)
            {
                //WorldGuardPlugin worldGuard = getWorldGuard();
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();

                //ApplicableRegionSet set = query.getApplicableRegions(loc);

                return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player), WorldGuardFlags.MCMMO_ENABLE_WG_FLAG);
            }
        }
        //Return False if these fail
        mcMMO.p.getLogger().severe("Failed to detect WG flags, is WG running properly?");
        return false;
    }

    public boolean hasXPFlag(Player player)
    {
        //Not sure when we're supposed to know when WorldGuard is loaded
        if(!flagsRegistered)
            registerFlags();

        BukkitPlayer localPlayer = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.util.Location loc = localPlayer.getLocation();

        if(flagsRegistered)
        {
            if(getWorldGuard() != null)
            {
                //WorldGuardPlugin worldGuard = getWorldGuard();
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();

                //ApplicableRegionSet set = query.getApplicableRegions(loc);

                return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player), WorldGuardFlags.MCMMO_XP_WG_FLAG);
            }
        }
        //Return False if these fail
        mcMMO.p.getLogger().severe("Failed to detect WG flags, is WG running properly?");
        return false;
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }

    private void registerFlags()
    {
        if(getWorldGuard() == null)
            return;

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            // register our flag with the registry
            registry.register(WorldGuardFlags.MCMMO_ENABLE_WG_FLAG);
            registry.register(WorldGuardFlags.MCMMO_XP_WG_FLAG);
            flagsRegistered = true;
        } catch (FlagConflictException e) {
            e.printStackTrace();
            // some other plugin registered a flag by the same name already.
            // you may want to re-register with a different name, but this
            // could cause issues with saved flags in region files. it's better
            // to print a message to let the server admin know of the conflict
        }
    }

    public static WorldGuardManager getInstance() {
        return instance;
    }
}
