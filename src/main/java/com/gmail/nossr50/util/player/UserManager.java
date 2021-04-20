package com.gmail.nossr50.util.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public final class UserManager {

    private static HashSet<McMMOPlayer> playerDataSet; //Used to track players for sync saves on shutdown
    private @NotNull static final PlayerSaveHandler playerSaveHandler = new PlayerSaveHandler();

    private UserManager() {}

    /**
     * Track a new user.
     *
     * @param mcMMOPlayer the player profile to start tracking
     */
    public static void track(McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.getPlayer().setMetadata(mcMMO.playerDataKey, new FixedMetadataValue(mcMMO.p, mcMMOPlayer));

        if(playerDataSet == null)
            playerDataSet = new HashSet<>();

        playerDataSet.add(mcMMOPlayer); //for sync saves on shutdown
    }

    public static void cleanupPlayer(McMMOPlayer mcMMOPlayer) {
        if(playerDataSet != null)
            playerDataSet.remove(mcMMOPlayer);
    }

    /**
     * Remove a user.
     *
     * @param player The Player object
     */
    public static void remove(Player player) {
        McMMOPlayer mcMMOPlayer = getPlayer(player);
        mcMMOPlayer.cleanup();
        player.removeMetadata(mcMMO.playerDataKey, mcMMO.p);

        if(playerDataSet != null) {
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

        if(playerDataSet != null)
            playerDataSet.clear(); //Clear sync save tracking
    }

    /**
     * Save all users on main thread
     */
    public static void saveAll() {
        if(playerDataSet == null)
            return;

        ImmutableList<McMMOPlayer> trackedSyncData = ImmutableList.copyOf(playerDataSet);

        mcMMO.p.getLogger().info("Saving mcMMOPlayers... (" + trackedSyncData.size() + ")");

        for (McMMOPlayer mmoPlayer : trackedSyncData) {
            try {
                mcMMO.p.getLogger().info("Saving data for player: "+mmoPlayer.getPlayerName());
                getPlayerSaveHandler().save(mmoPlayer.getPlayerData(), true);
            } catch (Exception e) {
                mcMMO.p.getLogger().severe("Could not save mcMMO player data for player: " + mmoPlayer.getPlayerName());
            }
        }

        mcMMO.p.getLogger().info("Finished save operation for "+trackedSyncData.size()+" players!");
    }

    public static Collection<McMMOPlayer> getPlayers() {
        Collection<McMMOPlayer> playerCollection = new ArrayList<>();

        for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
            if (hasPlayerDataKey(player)) {
                playerCollection.add(getPlayer(player));
            }
        }

        return playerCollection;
    }

    public static void logout(@NotNull McMMOPlayer mmoPlayer, boolean syncSave) {
        //TODO: T&C copy impl again from master
        Player targetPlayer = mmoPlayer.getPlayer();
        BleedTimerTask.bleedOut(targetPlayer);

        //Cleanup
        mmoPlayer.resetAbilityMode(); //TODO: T&C Wire this up, see master branch com.gmail.nossr50.datatypes.player.McMMOPlayer#resetAbilityMode for example
        mmoPlayer.getTamingManager().cleanupAllSummons();

        if (syncSave) {
            getPlayerSaveHandler().save(mmoPlayer.getPlayerData(), true); //TODO: T&C Wire this up, see master branch com.gmail.nossr50.datatypes.player.PlayerProfile#save
        } else {
            getPlayerSaveHandler().scheduleAsyncSave(mmoPlayer.getPlayerData()); //TODO: T&C Wire this up, see master branch com.gmail.nossr50.datatypes.player.PlayerProfile#scheduleAsyncSave
        }

        UserManager.remove(targetPlayer);

        if(Config.getInstance().getScoreboardsEnabled())
            ScoreboardManager.teardownPlayer(targetPlayer);

        if (inParty()) { //TODO: T&C Wire this up
            party.removeOnlineMember(targetPlayer); //TODO: T&C Wire this up
        }

        //Remove user from cache
        mcMMO.getDatabaseManager().cleanupUser(targetPlayer.getUniqueId());
    }

    @Deprecated
    public static @NotNull McMMOPlayer getPlayer(@NotNull String playerName) {
        return retrieveMcMMOPlayer(playerName, false);
    }

    public static @NotNull McMMOPlayer queryPlayer(@NotNull String playerName) {
        return retrieveMcMMOPlayer(playerName, false);
    }

    public static @NotNull McMMOPlayer getOfflinePlayer(@NotNull OfflinePlayer player) {
        if (player instanceof Player) {
            return getPlayer((Player) player);
        }

        return retrieveMcMMOPlayer(player.getName(), true);
    }

    public static @NotNull McMMOPlayer getOfflinePlayer(@NotNull String playerName) {
        return retrieveMcMMOPlayer(playerName, true);
    }

    public static @NotNull McMMOPlayer queryPlayer(@NotNull Player player) {
        //Avoid Array Index out of bounds
        if(player != null && player.hasMetadata(mcMMO.playerDataKey))
            return (McMMOPlayer) player.getMetadata(mcMMO.playerDataKey).get(0).value();
        else
            return null;
    }

    public static @NotNull McMMOPlayer getPlayer(@NotNull Player player) {
        //Avoid Array Index out of bounds
        if(player != null && player.hasMetadata(mcMMO.playerDataKey))
            return (McMMOPlayer) player.getMetadata(mcMMO.playerDataKey).get(0).value();
        else
            return null;
    }

    private static @NotNull McMMOPlayer retrieveMcMMOPlayer(@NotNull String playerName, boolean offlineValid) {
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player == null) {
            if (!offlineValid) {
                mcMMO.p.getLogger().warning("A valid mcMMOPlayer object could not be found for " + playerName + ".");
            }

            return null;
        }

        return getPlayer(player);
    }

    public static boolean hasPlayerDataKey(Entity entity) {
        return entity != null && entity.hasMetadata(mcMMO.playerDataKey);
    }

    public static @NotNull PlayerSaveHandler getPlayerSaveHandler() {
        return playerSaveHandler;
    }

}
