package com.gmail.nossr50.runnables.database;

import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.util.Misc;

public class UUIDUpdateAsyncTask extends BukkitRunnable {
    private mcMMO plugin;
    private static final int MAX_LOOKUP = HiddenConfig.getInstance().getUUIDConvertAmount();
    private boolean conversionNeeded;

    private DatabaseManager databaseManager;
    private List<String> userNames;
    private int size;
    private int checkedUsers;
    private long startMillis;

    public UUIDUpdateAsyncTask(mcMMO plugin) {
        this.plugin = plugin;
        this.conversionNeeded = !mcMMO.getConvertManager().isUUIDConversionCompleted();

        this.databaseManager = mcMMO.getDatabaseManager();
        this.userNames = databaseManager.getStoredUsers();
        this.size = userNames.size();

        this.checkedUsers = 0;
        this.startMillis = System.currentTimeMillis();

        plugin.getLogger().info("Starting to check and update UUIDs, total amount of users: " + size);
    }

    @Override
    public void run() {
        if (!conversionNeeded) {
            plugin.debug("No need to update database with UUIDs");
            this.cancel();
            return;
        }

        List<String> userNamesSection;

        if (size > MAX_LOOKUP) {
            userNamesSection = userNames.subList(size - MAX_LOOKUP, size);
            size -= MAX_LOOKUP;
        }
        else {
            userNamesSection = userNames.subList(0, size);
            size = 0;
            this.cancel();
            mcMMO.getConvertManager().setUUIDConversionCompleted(true);
            plugin.debug("Database updated with UUIDs!");
        }

        for (String userName : userNamesSection) {
            PlayerProfile profile = databaseManager.loadPlayerProfile(userName, false);

            checkedUsers++;

            if (profile == null || !profile.isLoaded() || profile.getUniqueId() != null) {
                continue;
            }

            new UUIDFetcherRunnable(userName).runTaskAsynchronously(mcMMO.p);
        }

        Misc.printProgress(checkedUsers, DatabaseManager.progressInterval, startMillis);
    }
}
