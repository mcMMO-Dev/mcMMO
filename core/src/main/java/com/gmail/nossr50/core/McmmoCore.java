package com.gmail.nossr50.core;

import com.gmail.nossr50.core.data.database.DatabaseManager;
import com.gmail.nossr50.core.mcmmo.event.EventCommander;
import com.gmail.nossr50.core.mcmmo.plugin.Plugin;
import com.gmail.nossr50.core.mcmmo.server.Server;
import com.gmail.nossr50.core.mcmmo.tasks.TaskScheduler;
import com.gmail.nossr50.core.platform.Platform;
import com.gmail.nossr50.core.util.ModManager;
import com.gmail.nossr50.core.util.experience.FormulaManager;
import com.gmail.nossr50.core.util.upgrade.UpgradeManager;

import java.io.File;
import java.util.logging.Logger;

public class McmmoCore {
    //TODO: Wire all this stuff
    public static Plugin p;
    private static EventCommander eventCommander;
    private static Logger logger;
    private static Platform platform;
    private static boolean retroModeEnabled;

    //Why do all these things need to be here? Sigh...
    private static DatabaseManager databaseManager;
    private static UpgradeManager upgradeManager; //TODO: I can't even remember what this one did
    private static FormulaManager formulaManager;
    private static ModManager modManager; //TODO: Probably need to rewrite this

    /**
     * Returns our Logger
     * @return the logger
     */
    public static Logger getLogger()
    {
        return logger;
    }

    public static EventCommander getEventCommander() {
        return eventCommander;
    }

    public static Server getServer() {
        return platform.getServer();
    }

    public static TaskScheduler getTaskScheduler()
    {
        return platform.getScheduler();
    }

    public static java.io.InputStream getResource(String path)
    {
        return platform.getResource(path);
    }

    public static File getDataFolderPath()
    {
        return platform.getDataFolderPath();
    }

    public static DatabaseManager getDatabaseManager() { return databaseManager; }

    public static UpgradeManager getUpgradeManager() { return upgradeManager; }

    public static FormulaManager getFormulaManager() { return formulaManager; }

    public static boolean isRetroModeEnabled() { return retroModeEnabled; }

    public static ModManager getModManager() { return modManager; }

    public static String getModDataFolderPath() { return platform.getModDataFolderPath(); }
}
