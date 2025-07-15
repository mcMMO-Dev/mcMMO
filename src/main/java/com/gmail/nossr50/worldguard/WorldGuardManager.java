package com.gmail.nossr50.worldguard;

import static org.bukkit.Bukkit.getServer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldGuardManager {
    private static WorldGuardManager instance;
    private WorldGuardPlugin worldGuardPluginRef;

    public static WorldGuardManager getInstance() {
        if (instance == null) {
            instance = new WorldGuardManager();
        }

        return instance;
    }

    public boolean hasMainFlag(Player player) {
        if (player == null) {
            return false;
        }

        BukkitPlayer localPlayer = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.util.Location loc = localPlayer.getLocation();

        //WorldGuardPlugin worldGuard = getWorldGuard();
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .createQuery();

        //ApplicableRegionSet set = query.getApplicableRegions(loc);

        return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player),
                WorldGuardFlags.MCMMO_ENABLE_WG_FLAG);
    }

    public boolean hasXPFlag(Player player) {
        if (player == null) {
            return false;
        }

        BukkitPlayer localPlayer = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.util.Location loc = localPlayer.getLocation();

        //WorldGuardPlugin worldGuard = getWorldGuard();
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .createQuery();

        //ApplicableRegionSet set = query.getApplicableRegions(loc);

        return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player),
                WorldGuardFlags.MCMMO_XP_WG_FLAG);
    }

    public boolean hasHardcoreFlag(Player player) {
        if (player == null) {
            return false;
        }

        BukkitPlayer localPlayer = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.util.Location loc = localPlayer.getLocation();

        //WorldGuardPlugin worldGuard = getWorldGuard();
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .createQuery();

        //ApplicableRegionSet set = query.getApplicableRegions(loc);

        return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player),
                WorldGuardFlags.MCMMO_HARDCORE_WG_FLAG);
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (!(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        worldGuardPluginRef = (WorldGuardPlugin) plugin;
        return worldGuardPluginRef;
    }

    public void registerFlags() {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

            try {
                // register our flag with the registry
                registry.register(WorldGuardFlags.MCMMO_ENABLE_WG_FLAG);
                registry.register(WorldGuardFlags.MCMMO_XP_WG_FLAG);
                registry.register(WorldGuardFlags.MCMMO_HARDCORE_WG_FLAG);
                LogUtils.debug(mcMMO.p.getLogger(), "Registered WG flags successfully!");
            } catch (FlagConflictException e) {
                e.printStackTrace();
                mcMMO.p.getLogger().warning("Failed to register WG flags!");
                // some other plugin registered a flag by the same name already.
                // you may want to re-register with a different name, but this
                // could cause issues with saved flags in region files. it's better
                // to print a message to let the server admin know of the conflict
            }
        } catch (NoClassDefFoundError e) {
            System.out.println("[mcMMO] Could not register WG Flags!"); //Don't use the Logger here
        }
    }


}
