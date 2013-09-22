package com.gmail.nossr50;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.config.mods.CustomArmorConfig;
import com.gmail.nossr50.config.mods.CustomBlockConfig;
import com.gmail.nossr50.config.mods.CustomEntityConfig;
import com.gmail.nossr50.config.mods.CustomToolConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.listeners.EntityListener;
import com.gmail.nossr50.listeners.InventoryListener;
import com.gmail.nossr50.listeners.PlayerListener;
import com.gmail.nossr50.listeners.SelfListener;
import com.gmail.nossr50.listeners.WorldListener;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.metrics.MetricsManager;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.SaveTimerTask;
import com.gmail.nossr50.runnables.database.UserPurgeTask;
import com.gmail.nossr50.runnables.party.PartyAutoKickTask;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.child.ChildConfig;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.skills.repair.RepairableManager;
import com.gmail.nossr50.skills.repair.RepairableManagerFactory;
import com.gmail.nossr50.skills.repair.config.RepairConfigManager;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.LogFilter;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManager;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManagerFactory;
import com.gmail.nossr50.util.commands.CommandRegistrationManager;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.player.UserManager;

import net.h31ix.updater.Updater;
import net.h31ix.updater.Updater.UpdateResult;
import net.h31ix.updater.Updater.UpdateType;
import net.shatteredlands.shatt.backup.ZipLibrary;

public class mcMMO extends JavaPlugin {
    /* Managers */
    private static ChunkManager      placeStore;
    private static RepairableManager repairableManager;
    private static DatabaseManager   databaseManager;
    private static FormulaManager    formulaManager;

    /* File Paths */
    private static String mainDirectory;
    private static String flatFileDirectory;
    private static String usersFile;
    private static String modDirectory;

    public static mcMMO p;

    // Jar Stuff
    public static File mcmmo;

    // Update Check
    private boolean updateAvailable;

    /* Plugin Checks */
    private static boolean combatTagEnabled;
    private static boolean healthBarEnabled;

    // Config Validation Check
    public boolean noErrorsInConfigFiles = true;

    // XP Event Check
    private boolean xpEventEnabled;

    /* Metadata Values */
    public final static String entityMetadataKey   = "mcMMO: Spawned Entity";
    public final static String blockMetadataKey    = "mcMMO: Piston Tracking";
    public final static String furnaceMetadataKey  = "mcMMO: Tracked Furnace";
    public final static String tntMetadataKey      = "mcMMO: Tracked TNT";
    public final static String customNameKey       = "mcMMO: Custom Name";
    public final static String customVisibleKey    = "mcMMO: Name Visibility";
    public final static String droppedItemKey      = "mcMMO: Tracked Item";
    public final static String infiniteArrowKey    = "mcMMO: Infinite Arrow";
    public final static String bowForceKey         = "mcMMO: Bow Force";
    public final static String arrowDistanceKey    = "mcMMO: Arrow Distance";
    public final static String customDamageKey     = "mcMMO: Custom Damage";

    public static FixedMetadataValue metadataValue;

    /**
     * Things to be run when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        try {
            p = this;
            getLogger().setFilter(new LogFilter(this));
            metadataValue = new FixedMetadataValue(this, true);

            setupFilePaths();
            loadConfigFiles();

            if (!noErrorsInConfigFiles) {
                return;
            }

            combatTagEnabled = getServer().getPluginManager().getPlugin("CombatTag") != null;
            healthBarEnabled = getServer().getPluginManager().getPlugin("HealthBar") != null;

            if (healthBarEnabled) {
                getLogger().info("HealthBar plugin found, mcMMO's healthbars are automatically disabled.");
            }

            databaseManager = DatabaseManagerFactory.getDatabaseManager();

            registerEvents();
            registerCustomRecipes();

            PartyManager.loadParties();

            formulaManager = new FormulaManager();

            for (Player player : getServer().getOnlinePlayers()) {
                UserManager.addUser(player); // In case of reload add all users back into UserManager
            }

            debug("Version " + getDescription().getVersion() + " is enabled!");

            scheduleTasks();
            CommandRegistrationManager.registerCommands();

            MetricsManager.setup();

            placeStore = ChunkManagerFactory.getChunkManager(); // Get our ChunkletManager

            checkForUpdates();

            if (Config.getInstance().getPTPCommandWorldPermissions()) {
                Permissions.generateWorldTeleportPermissions();
            }
        }
        catch (Throwable t) {
            getLogger().severe("There was an error while enabling mcMMO!");

            if (!(t instanceof ExceptionInInitializerError)) {
                t.printStackTrace();
            }
            else {
                getLogger().info("Please do not replace the mcMMO jar while the server is running.");
            }

            getServer().getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Things to be run when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        try {
            UserManager.saveAll();      // Make sure to save player information if the server shuts down
            PartyManager.saveParties(); // Save our parties
            formulaManager.saveFormula();
            placeStore.saveAll();       // Save our metadata
            placeStore.cleanUp();       // Cleanup empty metadata stores
        }
        catch (NullPointerException e) {}

        getServer().getScheduler().cancelTasks(this); // This removes our tasks
        HandlerList.unregisterAll(this); // Cancel event registrations

        if (Config.getInstance().getBackupsEnabled()) {
            // Remove other tasks BEFORE starting the Backup, or we just cancel it straight away.
            try {
                ZipLibrary.mcMMObackup();
            }
            catch (IOException e) {
                getLogger().severe(e.toString());
            }
            catch (Throwable e) {
                if (e instanceof NoClassDefFoundError) {
                    getLogger().severe("Backup class not found!");
                    getLogger().info("Please do not replace the mcMMO jar while the server is running.");
                }
                else {
                    getLogger().severe(e.toString());
                }
            }
        }

        debug("Was disabled."); // How informative!
    }

    public static String getMainDirectory() {
        return mainDirectory;
    }

    public static String getFlatFileDirectory() {
        return flatFileDirectory;
    }

    public static String getUsersFilePath() {
        return usersFile;
    }

    public static String getModDirectory() {
        return modDirectory;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public boolean isXPEventEnabled() {
        return xpEventEnabled;
    }

    public void setXPEventEnabled(boolean enabled) {
        this.xpEventEnabled = enabled;
    }

    public void toggleXpEventEnabled() {
        xpEventEnabled = !xpEventEnabled;
    }

    public void debug(String message) {
        getLogger().info("[Debug] " + message);
    }

    public static FormulaManager getFormulaManager() {
        return formulaManager;
    }

    public static ChunkManager getPlaceStore() {
        return placeStore;
    }

    public static RepairableManager getRepairableManager() {
        return repairableManager;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Deprecated
    public static void setDatabaseManager(DatabaseManager databaseManager) {
        mcMMO.databaseManager = databaseManager;
    }

    public static boolean isCombatTagEnabled() {
        return combatTagEnabled;
    }

    public static boolean isHealthBarEnabled() {
        return healthBarEnabled;
    }

    /**
     * Setup the various storage file paths
     */
    private void setupFilePaths() {
        mcmmo = getFile();
        mainDirectory = getDataFolder().getPath() + File.separator;
        flatFileDirectory = mainDirectory + "FlatFileStuff" + File.separator;
        usersFile = flatFileDirectory + "mcmmo.users";
        modDirectory = mainDirectory + "ModConfigs" + File.separator;
    }

    private void checkForUpdates() {
        if (!Config.getInstance().getUpdateCheckEnabled()) {
            return;
        }

        Updater updater = new Updater(this, "mcmmo", mcmmo, UpdateType.NO_DOWNLOAD, false);

        if (updater.getResult() != UpdateResult.UPDATE_AVAILABLE) {
            this.updateAvailable = false;
            return;
        }

        if (updater.getLatestVersionString().contains("-beta") && !Config.getInstance().getPreferBeta()) {
            this.updateAvailable = false;
            return;
        }

        this.updateAvailable = true;
        getLogger().info(LocaleLoader.getString("UpdateChecker.outdated"));
        getLogger().info(LocaleLoader.getString("UpdateChecker.newavailable"));
    }

    private void loadConfigFiles() {
        // Force the loading of config files
        TreasureConfig.getInstance();
        HiddenConfig.getInstance();
        AdvancedConfig.getInstance();
        new ChildConfig();

        List<Repairable> repairables = new ArrayList<Repairable>();

        if (Config.getInstance().getToolModsEnabled()) {
            repairables.addAll(CustomToolConfig.getInstance().getLoadedRepairables());
        }

        if (Config.getInstance().getArmorModsEnabled()) {
            repairables.addAll(CustomArmorConfig.getInstance().getLoadedRepairables());
        }

        if (Config.getInstance().getBlockModsEnabled()) {
            CustomBlockConfig.getInstance();
        }

        if (Config.getInstance().getEntityModsEnabled()) {
            CustomEntityConfig.getInstance();
        }

        // Load repair configs, make manager, and register them at this time
        RepairConfigManager rManager = new RepairConfigManager(this);
        repairables.addAll(rManager.getLoadedRepairables());
        repairableManager = RepairableManagerFactory.getRepairManager(repairables.size());
        repairableManager.registerRepairables(repairables);
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();

        // Register events
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new EntityListener(this), this);
        pluginManager.registerEvents(new InventoryListener(this), this);
        pluginManager.registerEvents(new SelfListener(), this);
        pluginManager.registerEvents(new WorldListener(this), this);
    }

    private void registerCustomRecipes() {
        if (Config.getInstance().getChimaeraEnabled()) {
            getServer().addRecipe(ChimaeraWing.getChimaeraWingRecipe());
        }
    }

    private void scheduleTasks() {
        // Periodic save timer (Saves every 10 minutes by default)
        long saveIntervalTicks = Config.getInstance().getSaveInterval() * 1200;
        new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);

        // Bleed timer (Runs every two seconds)
        new BleedTimerTask().runTaskTimer(this, 2 * Misc.TICK_CONVERSION_FACTOR, 2 * Misc.TICK_CONVERSION_FACTOR);

        // Old & Powerless User remover
        long purgeIntervalTicks = Config.getInstance().getPurgeInterval() * 60 * 60 * Misc.TICK_CONVERSION_FACTOR;

        if (purgeIntervalTicks == 0) {
            new UserPurgeTask().runTaskLater(this, 2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
        }
        else if (purgeIntervalTicks > 0) {
            new UserPurgeTask().runTaskTimer(this, purgeIntervalTicks, purgeIntervalTicks);
        }

        // Automatically remove old members from parties
        long kickIntervalTicks = Config.getInstance().getAutoPartyKickInterval() * 60 * 60 * Misc.TICK_CONVERSION_FACTOR;

        if (kickIntervalTicks == 0) {
            new PartyAutoKickTask().runTaskLater(this, 2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
        }
        else if (kickIntervalTicks > 0) {
            new PartyAutoKickTask().runTaskTimer(this, kickIntervalTicks, kickIntervalTicks);
        }
    }
}
