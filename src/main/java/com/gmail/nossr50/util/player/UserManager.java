package com.gmail.nossr50.util.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.MMODataSnapshot;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PersistentPlayerData;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PersistentPlayerDataSaveTask;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

//TODO: Add per world handling
public final class UserManager {

    private final HashSet<McMMOPlayer> playerDataSet; //Used to track players for sync saves on shutdown

    public UserManager() {
        this.playerDataSet = new HashSet<>();
    }

    /**
     * Track a new user.
     *
     * @param mmoPlayer the player profile to start tracking
     */
    public void track(McMMOPlayer mmoPlayer) {
        mmoPlayer.getPlayer().setMetadata(mcMMO.playerDataKey, new FixedMetadataValue(mcMMO.p, mmoPlayer));

        playerDataSet.add(mmoPlayer); //for sync saves on shutdown
    }

    public void cleanupPlayer(McMMOPlayer mmoPlayer) {
        playerDataSet.remove(mmoPlayer);
    }

    /**
     * Remove a user.
     *
     * @param player The Player object
     */
    public void remove(Player player) {
        McMMOPlayer mmoPlayer = getPlayer(player);
        mmoPlayer.cleanup();
        player.removeMetadata(mcMMO.playerDataKey, mcMMO.p);

        playerDataSet.remove(mmoPlayer); //Clear sync save tracking
    }

    /**
     * Clear all users.
     */
    public void clearAll() {
        for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
            remove(player);
        }

        playerDataSet.clear(); //Clear sync save tracking
    }

    public Collection<McMMOPlayer> getPlayers() {
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
    public McMMOPlayer getPlayer(String playerName) {
        return retrieveMcMMOPlayer(playerName, false);
    }

    public McMMOPlayer getOfflinePlayer(OfflinePlayer player) {
        if (player instanceof Player) {
            return getPlayer((Player) player);
        }

        return retrieveMcMMOPlayer(player.getName(), true);
    }

    public McMMOPlayer getOfflinePlayer(String playerName) {
        return retrieveMcMMOPlayer(playerName, true);
    }

    /**
     * Gets the McMMOPlayer object for a player, this can be null if the player has not yet been loaded.
     * @param player target player
     * @return McMMOPlayer object for this player, null if Player has not been loaded
     */
    public McMMOPlayer getPlayer(Player player) {
        //Avoid Array Index out of bounds
        if(player != null && player.hasMetadata(mcMMO.playerDataKey))
            return (McMMOPlayer) player.getMetadata(mcMMO.playerDataKey).get(0).value();
        else
            return null;
    }

    private McMMOPlayer retrieveMcMMOPlayer(String playerName, boolean offlineValid) {
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player == null) {
            if (!offlineValid) {
                mcMMO.p.getLogger().warning("A valid mmoPlayer object could not be found for " + playerName + ".");
            }

            return null;
        }

        return getPlayer(player);
    }

    public boolean hasPlayerDataKey(Entity entity) {
        return entity != null && entity.hasMetadata(mcMMO.playerDataKey);
    }

    public MMODataSnapshot createPlayerDataSnapshot(PersistentPlayerData persistentPlayerData) {
        return new MMODataSnapshot(persistentPlayerData);
    }

    public void saveUserImmediately(PersistentPlayerData persistentPlayerData, boolean useSync) {
        if(useSync)
            scheduleSyncSave(createPlayerDataSnapshot(persistentPlayerData)); //Execute sync saves immediately
        else
            scheduleAsyncSaveDelay(createPlayerDataSnapshot(persistentPlayerData), 0);
    }

    public void saveUserWithDelay(PersistentPlayerData persistentPlayerData, boolean useSync, int delayTicks) {
        if(useSync)
            scheduleSyncSaveDelay(createPlayerDataSnapshot(persistentPlayerData), delayTicks); //Execute sync saves immediately
        else
            scheduleAsyncSaveDelay(createPlayerDataSnapshot(persistentPlayerData), delayTicks);
    }

//    public void save(boolean useSync) {
//        if (!changed || !loaded) {
//            saveAttempts = 0;
//            return;
//        }
//
//        // TODO should this part be synchronized?
//        PlayerProfile profileCopy = new PlayerProfile(playerName, uuid,
//                experienceManager.copyPrimarySkillLevelsMap(),
//                experienceManager.copyPrimarySkillExperienceValuesMap(),
//                ImmutableMap.copyOf(abilityDATS),
//                mobHealthbarType,
//                scoreboardTipsShown,
//                ImmutableMap.copyOf(uniquePlayerData),
//                ImmutableMap.copyOf(xpBarState));
//
//        changed = !mcMMO.getDatabaseManager().saveUser(profileCopy);
//
//        if (changed) {
//            mcMMO.p.getLogger().severe("PlayerProfile saving failed for player: " + playerName + " " + uuid);
//
//            if(saveAttempts > 0)
//            {
//                mcMMO.p.getLogger().severe("Attempted to save profile for player "+getPlayerName()
//                        + " resulted in failure. "+saveAttempts+" have been made so far.");
//            }
//
//            if(saveAttempts < 10)
//            {
//                saveAttempts++;
//
//                if(useSync)
//                    scheduleSyncSave(); //Execute sync saves immediately
//                else
//                    scheduleAsyncSaveDelay();
//
//            } else {
//                mcMMO.p.getLogger().severe("mcMMO has failed to save the profile for "
//                        +getPlayerName()+" numerous times." +
//                        " mcMMO will now stop attempting to save this profile." +
//                        " Check your console for errors and inspect your DB for issues.");
//            }
//
//        } else {
//            saveAttempts = 0;
//        }
//    }

    /**
     * Save all users ON THIS THREAD.
     */
    public void saveAllSync() {
        ImmutableList<McMMOPlayer> trackedSyncData = ImmutableList.copyOf(playerDataSet);

        mcMMO.p.getLogger().info("Saving player data... (" + trackedSyncData.size() + ")");

        for (McMMOPlayer onlinePlayer : trackedSyncData) {
            try
            {
                mcMMO.p.getLogger().info("Saving data for player: "+onlinePlayer.getPlayerName());
                saveUserImmediately(onlinePlayer.getPersistentPlayerData(), true);
            }
            catch (Exception e)
            {
                mcMMO.p.getLogger().warning("Could not save mcMMO player data for player: " + onlinePlayer.getPlayerName());
            }
        }

        mcMMO.p.getLogger().info("Finished save operation for "+trackedSyncData.size()+" players!");
    }

    /**
     * This method is called by PlayerQuitEvent to tear down the mmoPlayer.
     * If syncSave is true, then saving is executed immediately
     *
     * @param syncSave if true, data is saved synchronously
     */
    public void logout(McMMOPlayer mmoPlayer, boolean syncSave) {
        BleedTimerTask.bleedOut(mmoPlayer.getPlayer());

        //TODO: There is a possibility that async saves don't execute in time if the server is told to shutdown

        if(syncSave) {
            saveUserImmediately(mmoPlayer.getPersistentPlayerData(), true);
        } else {
            saveUserWithDelay(mmoPlayer.getPersistentPlayerData(), false, 20);
        }

        cleanupPlayer(mmoPlayer);

        if(Config.getInstance().getScoreboardsEnabled())
            ScoreboardManager.teardownPlayer(mmoPlayer.getPlayer());

        //Remove user from cache (SQL)
        mcMMO.getDatabaseManager().removeCache(mmoPlayer.getUniqueId());
    }


    public void scheduleAsyncSave(MMODataSnapshot mmoDataSnapshot) {
        new PersistentPlayerDataSaveTask(mmoDataSnapshot).runTaskAsynchronously(mcMMO.p);
    }

    public void scheduleSyncSave(MMODataSnapshot mmoDataSnapshot) {
        new PersistentPlayerDataSaveTask(mmoDataSnapshot).runTask(mcMMO.p);
    }

    public void scheduleAsyncSaveDelay(MMODataSnapshot mmoDataSnapshot, int delayTicks) {
        new PersistentPlayerDataSaveTask(mmoDataSnapshot).runTaskLaterAsynchronously(mcMMO.p, delayTicks);
    }

    public void scheduleSyncSaveDelay(MMODataSnapshot mmoDataSnapshot, int delayTicks) {
        new PersistentPlayerDataSaveTask(mmoDataSnapshot).runTaskLater(mcMMO.p, delayTicks);
    }
}
