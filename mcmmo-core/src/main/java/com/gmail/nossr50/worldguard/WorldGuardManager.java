package com.gmail.nossr50.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;

public class WorldGuardManager {

    public boolean hasMainFlag(Player player)
    {
        if(player == null)
            return false;

        BukkitPlayer localPlayer = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.util.Location loc = localPlayer.getLocation();

        //WorldGuardPlugin worldGuard = getWorldGuard();
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();

        //ApplicableRegionSet set = query.getApplicableRegions(loc);

        return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player), WorldGuardFlags.MCMMO_ENABLE_WG_FLAG);
    }

    public boolean hasXPFlag(Player player) {
        if (player == null)
            return false;

        BukkitPlayer localPlayer = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.util.Location loc = localPlayer.getLocation();

        //WorldGuardPlugin worldGuard = getWorldGuard();
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();

        //ApplicableRegionSet set = query.getApplicableRegions(loc);

        return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player), WorldGuardFlags.MCMMO_XP_WG_FLAG);
    }

    public boolean hasHardcoreFlag(Player player)
    {
        if(player == null)
            return false;

        BukkitPlayer localPlayer = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.util.Location loc = localPlayer.getLocation();

        //WorldGuardPlugin worldGuard = getWorldGuard();
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();

        //ApplicableRegionSet set = query.getApplicableRegions(loc);

        return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player), WorldGuardFlags.MCMMO_HARDCORE_WG_FLAG);
    }

    public void registerFlags()
    {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

            try {
                // register our flag with the registry
                registry.register(WorldGuardFlags.MCMMO_ENABLE_WG_FLAG);
                registry.register(WorldGuardFlags.MCMMO_XP_WG_FLAG);
                registry.register(WorldGuardFlags.MCMMO_HARDCORE_WG_FLAG);
                System.out.println("mcMMO has registered WG flags successfully!");
            } catch (FlagConflictException e) {
                e.printStackTrace();
                System.out.println("mcMMO has failed to register WG flags!");
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
