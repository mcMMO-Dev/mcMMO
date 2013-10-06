package com.gmail.nossr50.runnables.database;

import java.lang.ref.WeakReference;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.database.SQLDatabaseManager;

/**
 * This task is in charge of sending a MySQL ping over the MySQL connection
 * every hour to prevent the connection from timing out and losing players'
 * data when they join.
 * <p/>
 * A WeakReference is used to keep the database instance, because
 * {@link com.gmail.nossr50.commands.database.ConvertDatabaseCommand database
 * conversion} may create a SQLDatabaseManager that will be thrown out. If a
 * normal reference was used, the conversion would cause a combined data and
 * resource leak through this task.
 */
public class SQLDatabaseKeepaliveTask extends BukkitRunnable {
    WeakReference<SQLDatabaseManager> databaseInstance;

    public SQLDatabaseKeepaliveTask(SQLDatabaseManager dbman) {
        databaseInstance = new WeakReference<SQLDatabaseManager>(dbman);
    }

    public void run() {
        SQLDatabaseManager dbman = databaseInstance.get();
        if (dbman != null) {
            dbman.checkConnected();
        }
        else {
            // This happens when the database was started for a conversion,
            // or discarded by its creator for any other reason. If this code
            // was not present, we would leak the connection resources.
            this.cancel();
        }
    }
}
