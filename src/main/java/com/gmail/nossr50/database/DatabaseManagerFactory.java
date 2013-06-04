package com.gmail.nossr50.database;

import com.gmail.nossr50.config.Config;

public class DatabaseManagerFactory {
    public static DatabaseManager getDatabaseManager() {
        return Config.getInstance().getUseMySQL() ? new SQLDatabaseManager() : new FlatfileDatabaseManager();
    }
}
