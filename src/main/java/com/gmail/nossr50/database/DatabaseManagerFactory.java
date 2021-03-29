package com.gmail.nossr50.database;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.mcMMO;

public class DatabaseManagerFactory {
    private static Class<? extends DatabaseManager> customManager = null;

    public static DatabaseManager getDatabaseManager() {
        if (customManager != null) {
            try {
                return createDefaultCustomDatabaseManager();
            }
            catch (Exception e) {
                mcMMO.p.debug("Could not create custom database manager");
                e.printStackTrace();
            }
            catch (Throwable e) {
                mcMMO.p.debug("Failed to create custom database manager");
                e.printStackTrace();
            }
            mcMMO.p.debug("Falling back on " + (Config.getInstance().getUseMySQL() ? "SQL" : "Flatfile") + " database");
        }

        return Config.getInstance().getUseMySQL() ? new SQLDatabaseManager() : new FlatFileDatabaseManager();
    }

    /**
     * Sets the custom DatabaseManager class for mcMMO to use. This should be
     * called prior to mcMMO enabling.
     * <p/>
     * The provided class must have an empty constructor, which is the one
     * that will be used.
     * <p/>
     * This method is intended for API use, but it should not be considered
     * stable. This method is subject to change and/or removal in future
     * versions.
     *
     * @param clazz the DatabaseManager class to use
     *
     * @throws IllegalArgumentException if the provided class does not have
     *                                  an empty constructor
     */
    public static void setCustomDatabaseManagerClass(Class<? extends DatabaseManager> clazz) {
        try {
            clazz.getConstructor();
            customManager = clazz;
        }
        catch (Throwable e) {
            throw new IllegalArgumentException("Provided database manager class must have an empty constructor", e);
        }
    }

    public static Class<? extends DatabaseManager> getCustomDatabaseManagerClass() {
        return customManager;
    }

    public static DatabaseManager createDatabaseManager(DatabaseType type) {
        switch (type) {
            case FLATFILE:
                mcMMO.p.getLogger().info("Using FlatFile Database");
                return new FlatFileDatabaseManager();

            case SQL:
                mcMMO.p.getLogger().info("Using SQL Database");
                return new SQLDatabaseManager();

            case CUSTOM:
                try {
                    mcMMO.p.getLogger().info("Attempting to use Custom Database");
                    return createDefaultCustomDatabaseManager();
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }

            default:
                return null;
        }
    }

    public static DatabaseManager createDefaultCustomDatabaseManager() throws Throwable {
        return customManager.getConstructor().newInstance();
    }

    public static DatabaseManager createCustomDatabaseManager(Class<? extends DatabaseManager> clazz) throws Throwable {
        return clazz.getConstructor().newInstance();
    }
}
