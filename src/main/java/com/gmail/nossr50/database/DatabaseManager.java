package com.gmail.nossr50.database;

import java.io.File;
import java.io.IOException;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;

public class DatabaseManager {
    private final mcMMO plugin;
    private final boolean isUsingSQL;
    private File usersFile;

    public DatabaseManager(final mcMMO plugin, final boolean isUsingSQL) {
        this.plugin = plugin;
        this.isUsingSQL = isUsingSQL;

        if (isUsingSQL) {
            SQLDatabaseManager.checkConnected();
            SQLDatabaseManager.createStructure();
        }
        else {
            usersFile = new File(mcMMO.getUsersFilePath());
            createFlatfileDatabase();
            FlatfileDatabaseManager.updateLeaderboards();
        }
    }

    public void purgePowerlessUsers() {
        plugin.getLogger().info("Purging powerless users...");
        plugin.getLogger().info("Purged " + (isUsingSQL ? SQLDatabaseManager.purgePowerlessSQL() : FlatfileDatabaseManager.purgePowerlessFlatfile()) + " users from the database.");
    }

    public void purgeOldUsers() {
        plugin.getLogger().info("Purging old users...");
        plugin.getLogger().info("Purged " + (isUsingSQL ? SQLDatabaseManager.purgeOldSQL() : FlatfileDatabaseManager.removeOldFlatfileUsers()) + " users from the database.");
    }

    public boolean removeUser(String playerName) {
        if (isUsingSQL ? SQLDatabaseManager.removeUserSQL(playerName) : FlatfileDatabaseManager.removeFlatFileUser(playerName)) {
            Misc.profileCleanup(playerName);
            return true;
        }

        return false;
    }

    private void createFlatfileDatabase() {
        if (usersFile.exists()) {
            return;
        }

        usersFile.getParentFile().mkdir();

        try {
            plugin.debug("Creating mcmmo.users file...");
            new File(mcMMO.getUsersFilePath()).createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
