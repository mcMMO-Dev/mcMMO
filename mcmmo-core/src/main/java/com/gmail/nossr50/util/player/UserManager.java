package com.gmail.nossr50.util.player;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public final class UserManager {

    private final mcMMO pluginRef;

    public UserManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }
    
    private HashSet<BukkitMMOPlayer> playerDataSet; //Used to track players for sync saves on shutdown


    /**
     * Track a new user.
     *
     * @param mcMMOPlayer the player profile to start tracking
     */
    public void track(BukkitMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.getNative().setMetadata(MetadataConstants.PLAYER_DATA_METAKEY, new FixedMetadataValue(pluginRef, mcMMOPlayer));

        if(playerDataSet == null)
            playerDataSet = new HashSet<>();

        playerDataSet.add(mcMMOPlayer); //for sync saves on shutdown
    }

    public void cleanupPlayer(BukkitMMOPlayer mcMMOPlayer) {
        if(playerDataSet != null && playerDataSet.contains(mcMMOPlayer))
            playerDataSet.remove(mcMMOPlayer);
    }

    /**
     * Remove a user.
     *
     * @param player The Player object
     */
    public void remove(Player player) {
        BukkitMMOPlayer mcMMOPlayer = getPlayer(player);
        player.removeMetadata(MetadataConstants.PLAYER_DATA_METAKEY, pluginRef);

        if(mcMMOPlayer != null)
            mcMMOPlayer.cleanup();

        if(playerDataSet != null) {
            playerDataSet.remove(mcMMOPlayer); //Clear sync save tracking
        }
    }

    /**
     * Clear all users.
     */
    public void clearAll() {
        for (Player player : pluginRef.getServer().getOnlinePlayers()) {
            remove(player);
        }

        if(playerDataSet != null)
            playerDataSet.clear(); //Clear sync save tracking
    }

    /**
     * Save all users ON THIS THREAD.
     */
    public void saveAll() {
        if(playerDataSet == null)
            return;

        ImmutableList<BukkitMMOPlayer> trackedSyncData = ImmutableList.copyOf(playerDataSet);

        pluginRef.getLogger().info("Saving mcMMOPlayers... (" + trackedSyncData.size() + ")");

        for (BukkitMMOPlayer playerData : trackedSyncData) {
            try
            {
                pluginRef.getLogger().info("Saving data for player: "+playerData.getPlayerName());
                playerData.getProfile().save(true);
            }
            catch (Exception e)
            {
                pluginRef.getLogger().warning("Could not save mcMMO player data for player: " + playerData.getPlayerName());
            }
        }

        pluginRef.getLogger().info("Finished save operation for "+trackedSyncData.size()+" players!");
    }

    public Collection<BukkitMMOPlayer> getPlayers() {
        Collection<BukkitMMOPlayer> playerCollection = new ArrayList<>();

        for (Player player : pluginRef.getServer().getOnlinePlayers()) {
            if (hasPlayerDataKey(player)) {
                playerCollection.add(getPlayer(player));
            }
        }

        return playerCollection;
    }

    /**
     * Get the McMMOPlayer of a player by name.
     *
     * @param playerName The name of the player whose McMMOPlayer to retrieve
     * @return the player's McMMOPlayer object
     */
    @Nullable
    public BukkitMMOPlayer getPlayer(String playerName) {
        return retrieveMcMMOPlayer(playerName, false);
    }

    @Nullable
    public BukkitMMOPlayer getOfflinePlayer(OfflinePlayer player) {
        if (player instanceof Player) {
            return getPlayer((Player) player);
        }

        return retrieveMcMMOPlayer(player.getName(), true);
    }

    public BukkitMMOPlayer getOfflinePlayer(String playerName) {
        return retrieveMcMMOPlayer(playerName, true);
    }

    /**
     * Gets the McMMOPlayer object for a player, this can be null if the player has not yet been loaded.
     *
     * @param player target player
     * @return McMMOPlayer object for this player, null if Player has not been loaded
     */
    @Nullable
    public BukkitMMOPlayer getPlayer(Player player) {
        //Avoid Array Index out of bounds
        if (player != null && player.hasMetadata(MetadataConstants.PLAYER_DATA_METAKEY))
            return (BukkitMMOPlayer) player.getMetadata(MetadataConstants.PLAYER_DATA_METAKEY).get(0).value();
        else
            return null;
    }

    @Nullable
    private BukkitMMOPlayer retrieveMcMMOPlayer(String playerName, boolean offlineValid) {
        Player player = pluginRef.getServer().getPlayerExact(playerName);

        if (player == null) {
            if (!offlineValid) {
                pluginRef.getLogger().warning("A valid mcMMOPlayer object could not be found for " + playerName + ".");
            }

            return null;
        }

        return getPlayer(player);
    }

    public boolean hasPlayerDataKey(Entity entity) {
        return entity != null && entity.hasMetadata(MetadataConstants.PLAYER_DATA_METAKEY);
    }
}
