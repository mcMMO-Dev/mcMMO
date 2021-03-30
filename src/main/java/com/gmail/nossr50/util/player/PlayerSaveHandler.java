package com.gmail.nossr50.util.player;

import com.gmail.nossr50.datatypes.player.MMODataSnapshot;
import com.gmail.nossr50.datatypes.player.PlayerData;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

//TODO: Low priority - Track pending Async saves to avoid data loss during server shutdown
//TODO: T&C Javadocs
public class PlayerSaveHandler {

    private void save(@NotNull MMODataSnapshot mmoDataSnapshot, boolean useSync) {
        boolean saveSuccessful = mcMMO.getDatabaseManager().saveUser(mmoDataSnapshot);

        //Check for failure to save
        if (!saveSuccessful) {
            String playerName = mmoDataSnapshot.getPlayerName();
            String uuidStr = mmoDataSnapshot.getPlayerUUID().toString();
            mcMMO.p.getLogger().severe("PlayerProfile saving failed for player name: " + playerName + " UUID: " + uuidStr);

            if(mmoDataSnapshot.getSaveAttempts() > 0) {
                mcMMO.p.getLogger().severe("Attempted to save profile for player "+playerName
                        + " resulted in failure. " + mmoDataSnapshot.getSaveAttempts() + " have been made so far.");
            }

            if(mmoDataSnapshot.getSaveAttempts() < 10) {
                mmoDataSnapshot.incrementSaveAttempts();

                //Back out of async saving if we detect a server shutdown, this is not always going to be caught
                if(mcMMO.isServerShutdownExecuted() || useSync)
                    scheduleSyncSave(mmoDataSnapshot); //Execute sync saves immediately
                else
                    scheduleAsyncSave(mmoDataSnapshot);

            } else {
                mcMMO.p.getLogger().severe("mcMMO has failed to save the profile for "
                        + playerName + " numerous times." +
                        " mcMMO will now stop attempting to save this profile." +
                        " Check your console for errors and inspect your DB for issues.");
            }
        }
    }

    public void save(@NotNull PlayerData playerData, boolean useSync) {
        //TODO: We no longer check if a profile is loaded or not as it should never be unloaded if a save operation is being called, need to double check this to be true
        if(!playerData.isProfileDirty()) {
            return; //Don't save data that hasn't changed
        }

        MMODataSnapshot mmoDataSnapshot = new MMODataSnapshot(playerData);
        save(mmoDataSnapshot, useSync);
    }

    public void scheduleAsyncSave(@NotNull PlayerData playerData) {
        MMODataSnapshot mmoDataSnapshot = new MMODataSnapshot(playerData);
        scheduleAsyncSave(mmoDataSnapshot);
    }

    public void scheduleAsyncSaveDelay(@NotNull PlayerData playerData) {
        MMODataSnapshot mmoDataSnapshot = new MMODataSnapshot(playerData);
        scheduleAsyncSaveDelay(mmoDataSnapshot);
    }

    public void scheduleSyncSave(@NotNull PlayerData playerData) {
        MMODataSnapshot mmoDataSnapshot = new MMODataSnapshot(playerData);
        scheduleSyncSave(mmoDataSnapshot);
    }

    private void scheduleAsyncSave(@NotNull MMODataSnapshot mmoDataSnapshot) {
        Bukkit.getScheduler().runTaskAsynchronously(mcMMO.p, () ->  save(mmoDataSnapshot, false));
    }

    private void scheduleAsyncSaveDelay(@NotNull MMODataSnapshot mmoDataSnapshot) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(mcMMO.p, () -> save(mmoDataSnapshot, false), 20L);
    }

    private void scheduleSyncSave(@NotNull MMODataSnapshot mmoDataSnapshot) {
        Bukkit.getScheduler().runTask(mcMMO.p, () -> save(mmoDataSnapshot, true));
    }
}
