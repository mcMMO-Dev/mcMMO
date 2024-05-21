package com.gmail.nossr50.util.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public final class UserManager {

    private static HashSet<McMMOPlayer> playerDataSet; //Used to track players for sync saves on shutdown

    private UserManager() {}

    /**
     * Track a new user.
     *
     * @param mcMMOPlayer the player profile to start tracking
     */
    public static void track(@NotNull McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.getPlayer().setMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA, new FixedMetadataValue(mcMMO.p, mcMMOPlayer));

        if (playerDataSet == null)
            playerDataSet = new HashSet<>();

        playerDataSet.add(mcMMOPlayer); //for sync saves on shutdown
    }

    public static void cleanupPlayer(McMMOPlayer mcMMOPlayer) {
        if (playerDataSet != null)
            playerDataSet.remove(mcMMOPlayer);
    }

    /**
     * Remove a user.
     *
     * @param player The Player object
     */
    public static void remove(@NotNull Player player) {
        McMMOPlayer mcMMOPlayer = getPlayer(player);

        if (mcMMOPlayer == null)
            return;

        mcMMOPlayer.cleanup();
        player.removeMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA, mcMMO.p);

        if (playerDataSet != null) {
            playerDataSet.remove(mcMMOPlayer); //Clear sync save tracking
        }
    }

    /**
     * Clear all users.
     */
    public static void clearAll() {
        for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
            remove(player);
        }

        if (playerDataSet != null)
            playerDataSet.clear(); //Clear sync save tracking
    }

    /**
     * Save all users ON THIS THREAD.
     */
    public static void saveAll() {
        if (playerDataSet == null)
            return;

        ImmutableList<McMMOPlayer> trackedSyncData = ImmutableList.copyOf(playerDataSet);

        mcMMO.p.getLogger().info("Saving mcMMOPlayers... (" + trackedSyncData.size() + ")");

        for (McMMOPlayer playerData : trackedSyncData) {
            try {
                LogUtils.debug(mcMMO.p.getLogger(), "Saving data for player: "+playerData.getPlayerName());
                playerData.getProfile().save(true);
            }
            catch (Exception e) {
                mcMMO.p.getLogger().warning("Could not save mcMMO player data for player: " + playerData.getPlayerName());
            }
        }

        mcMMO.p.getLogger().info("Finished save operation for "+trackedSyncData.size()+" players!");
    }

    public static @NotNull Collection<McMMOPlayer> getPlayers() {
        Collection<McMMOPlayer> playerCollection = new ArrayList<>();

        for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
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
    public static @Nullable McMMOPlayer getPlayer(String playerName) {
        return retrieveMcMMOPlayer(playerName, false);
    }

    public static @Nullable McMMOPlayer getOfflinePlayer(OfflinePlayer player) {
        if (player instanceof Player) {
            return getPlayer((Player) player);
        }

        return retrieveMcMMOPlayer(player.getName(), true);
    }

    public static @Nullable McMMOPlayer getOfflinePlayer(String playerName) {
        return retrieveMcMMOPlayer(playerName, true);
    }

    /**
     * Gets the McMMOPlayer object for a player, this can be null if the player has not yet been loaded.
     * @param player target player
     * @return McMMOPlayer object for this player, null if Player has not been loaded
     */
    public static @Nullable McMMOPlayer getPlayer(@Nullable Player player) {
        //Avoid Array Index out of bounds
        if (player != null && player.hasMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA))
            return (McMMOPlayer) player.getMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA).get(0).value();
        else
            return null;
    }

    private static @Nullable McMMOPlayer retrieveMcMMOPlayer(@Nullable String playerName, boolean offlineValid) {
        if (playerName == null)
            return null;

        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player == null) {
            if (!offlineValid) {
                mcMMO.p.getLogger().warning("A valid mcMMOPlayer object could not be found for " + playerName + ".");
            }

            return null;
        }

        return getPlayer(player);
    }

    public static boolean hasPlayerDataKey(@Nullable Entity entity) {
        return entity != null && entity.hasMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA);
    }
}
