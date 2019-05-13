package com.gmail.nossr50;

import com.gmail.nossr50.config.ConfigManager;
import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.config.hocon.database.ConfigSectionCleaning;
import com.gmail.nossr50.config.hocon.database.ConfigSectionMySQL;
import com.gmail.nossr50.config.hocon.party.ConfigSectionPartyExperienceSharing;
import com.gmail.nossr50.config.hocon.party.ConfigSectionPartyLevel;
import com.gmail.nossr50.config.hocon.playerleveling.ConfigLeveling;
import com.gmail.nossr50.config.hocon.scoreboard.ConfigScoreboard;
import com.gmail.nossr50.core.DynamicSettingsManager;
import com.gmail.nossr50.core.MaterialMapStore;
import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.skills.subskills.acrobatics.Roll;
import com.gmail.nossr50.listeners.*;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.SaveTimerTask;
import com.gmail.nossr50.runnables.backups.CleanBackupsTask;
import com.gmail.nossr50.runnables.commands.NotifySquelchReminderTask;
import com.gmail.nossr50.runnables.database.UserPurgeTask;
import com.gmail.nossr50.runnables.party.PartyAutoKickTask;
import com.gmail.nossr50.runnables.player.ClearRegisteredXPGainTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.runnables.player.PowerLevelUpdatingTask;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.repair.repairables.RepairableManager;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManager;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManagerFactory;
import com.gmail.nossr50.util.commands.CommandRegistrationManager;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.player.PlayerLevelUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import net.shatteredlands.shatt.backup.ZipLibrary;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class mcMMO extends JavaPlugin {
    public static mcMMO p;
    // Jar Stuff
    public static File mcMMOFile;
    /* Managers */
    private static ChunkManager placeStore;
    private static ConfigManager configManager;
    private static DynamicSettingsManager dynamicSettingsManager;
    private static DatabaseManager databaseManager;
    private static FormulaManager formulaManager;
    private static MaterialMapStore materialMapStore;
    private static PlayerLevelUtils playerLevelUtils;

    /* File Paths */
    private static String mainDirectory;
    private static String localesDirectory;
    private static String flatFileDirectory;
    private static String usersFile;
    private static String modDirectory;

    /* Plugin Checks */
    private static boolean healthBarPluginEnabled;
    // API checks
    private static boolean serverAPIOutdated = false;
    // XP Event Check
    private boolean xpEventEnabled;

    /**
     * Things to be run when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        try {
            p = this;
            getLogger().setFilter(new LogFilter(this));

            //DEBUG
            /*getLogger().info(Bukkit.getBukkitVersion());
            getLogger().info(Bukkit.getVersion());*/

            MetadataConstants.metadataValue = new FixedMetadataValue(this, true);

            PluginManager pluginManager = getServer().getPluginManager();
            healthBarPluginEnabled = pluginManager.getPlugin("HealthBar") != null;

            //upgradeManager = new UpgradeManager();

            setupFilePaths();

            //modManager = new ModManager();

            loadConfigFiles();
            registerDynamicSettings(); //Do this after configs are loaded

            if (healthBarPluginEnabled) {
                getLogger().info("HealthBar plugin found, mcMMO's healthbars are automatically disabled.");
            }

            if (pluginManager.getPlugin("NoCheatPlus") != null && pluginManager.getPlugin("CompatNoCheatPlus") == null) {
                getLogger().warning("NoCheatPlus plugin found, but CompatNoCheatPlus was not found!");
                getLogger().warning("mcMMO will not work properly alongside NoCheatPlus without CompatNoCheatPlus");
            }

            databaseManager = DatabaseManagerFactory.getDatabaseManager();

            //Check for the newer API and tell them what to do if its missing
            CompatibilityCheck.checkForOutdatedAPI(serverAPIOutdated, getServerSoftwareStr());

            if (serverAPIOutdated) {
                Bukkit
                        .getScheduler()
                        .scheduleSyncRepeatingTask(this,
                                () -> getLogger().severe("You are running an outdated version of " + getServerSoftware() + ", mcMMO will not work unless you update to a newer version!"),
                                20, 20 * 60 * 30);

                if (getServerSoftware() == ServerSoftwareType.CRAFTBUKKIT) {
                    Bukkit.getScheduler()
                            .scheduleSyncRepeatingTask(this,
                                    () -> getLogger().severe("We have detected you are using incompatible server software, our best guess is that you are using CraftBukkit. mcMMO requires Spigot or Paper, if you are not using CraftBukkit, you will still need to update your custom server software before mcMMO will work."),
                                    20, 20 * 60 * 30);
                }
            } else {
                registerEvents();
                registerCoreSkills();
                registerCustomRecipes();

                if (getConfigManager().getConfigParty().isPartySystemEnabled())
                    PartyManager.loadParties();

                formulaManager = new FormulaManager();

                for (Player player : getServer().getOnlinePlayers()) {
                    new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(this, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
                }

                debug("Version " + getDescription().getVersion() + " is enabled!");

                scheduleTasks();
                CommandRegistrationManager.registerCommands();

                placeStore = ChunkManagerFactory.getChunkManager(); // Get our ChunkletManager

                if (getConfigManager().getConfigParty().getPTP().isPtpWorldBasedPermissions()) {
                    Permissions.generateWorldTeleportPermissions();
                }

                //Populate Ranked Skill Maps (DO THIS LAST)
                RankUtils.populateRanks();
            }

            //If anonymous statistics are enabled then use them
            if (getConfigManager().getConfigMetrics().isAllowAnonymousUsageStatistics()) {
                Metrics metrics;
                metrics = new Metrics(this);
                metrics.addCustomChart(new Metrics.SimplePie("version", () -> getDescription().getVersion()));

                if (!configManager.getConfigLeveling().getConfigSectionLevelingGeneral().getConfigSectionLevelScaling().isRetroModeEnabled())
                    metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Standard"));
                else
                    metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Retro"));
            }
        } catch (Throwable t) {
            getLogger().severe("There was an error while enabling mcMMO!");
            t.printStackTrace();
            getLogger().severe("End of error report for mcMMO");
            getLogger().info("Please do not replace the mcMMO jar while the server is running.");
        }

        //Init Material Maps
        materialMapStore = new MaterialMapStore();

        //Init player level values
        playerLevelUtils = new PlayerLevelUtils();
    }

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null)
            WorldGuardManager.getInstance().registerFlags();
    }

    /**
     * Things to be run when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        try {
//            Alchemy.finishAllBrews();   // Finish all partially complete AlchemyBrewTasks to prevent vanilla brewing continuation on restart
            UserManager.saveAll();      // Make sure to save player information if the server shuts down
            UserManager.clearAll();
            PartyManager.saveParties(); // Save our parties

            //TODO: Needed?
            if (getScoreboardSettings().getScoreboardsEnabled())
                ScoreboardManager.teardownAll();

            formulaManager.saveFormula();
            /*holidayManager.saveAnniversaryFiles();*/
            placeStore.saveAll();       // Save our metadata
            placeStore.cleanUp();       // Cleanup empty metadata stores
        } catch (Exception e) {
            e.printStackTrace();
        }

        debug("Canceling all tasks...");
        getServer().getScheduler().cancelTasks(this); // This removes our tasks
        debug("Unregister all events...");
        HandlerList.unregisterAll(this); // Cancel event registrations

        if (getConfigManager().getConfigAutomatedBackups().isZipBackupsEnabled()) {
            // Remove other tasks BEFORE starting the Backup, or we just cancel it straight away.
            try {
                ZipLibrary.mcMMOBackup();
            } catch (IOException e) {
                getLogger().severe(e.toString());
            } catch (Throwable e) {
                if (e instanceof NoClassDefFoundError) {
                    getLogger().severe("Backup class not found!");
                    getLogger().info("Please do not replace the mcMMO jar while the server is running.");
                } else {
                    getLogger().severe(e.toString());
                }
            }
        }

        databaseManager.onDisable();

        debug("Was disabled."); // How informative!
    }

    public static PlayerLevelUtils getPlayerLevelUtils() {
        return playerLevelUtils;
    }

    /**
     * Returns a ServerSoftwareType based on version strings
     * Custom software is returned as CRAFTBUKKIT
     *
     * @return the ServerSoftwareType which likely matches the server
     */
    private ServerSoftwareType getServerSoftware() {
        if (Bukkit.getVersion().toLowerCase().contains("paper"))
            return ServerSoftwareType.PAPER;
        else if (Bukkit.getVersion().toLowerCase().contains("spigot"))
            return ServerSoftwareType.SPIGOT;
        else
            return ServerSoftwareType.CRAFTBUKKIT;
    }

    /**
     * Gets a string version of ServerSoftwareType
     *
     * @return Formatted String of ServerSoftwareType
     */
    private String getServerSoftwareStr() {
        switch (getServerSoftware()) {
            case PAPER:
                return "Paper";
            case SPIGOT:
                return "Spigot";
            default:
                return "CraftBukkit";
        }
    }

    public static MaterialMapStore getMaterialMapStore() {
        return materialMapStore;
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

    public static FormulaManager getFormulaManager() {
        return formulaManager;
    }

    public static ChunkManager getPlaceStore() {
        return placeStore;
    }

    public static RepairableManager getRepairableManager() {
        return dynamicSettingsManager.getRepairableManager();
    }

    public static SalvageableManager getSalvageableManager() {
        return dynamicSettingsManager.getSalvageableManager();
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Deprecated
    public static void setDatabaseManager(DatabaseManager newDatabaseManager) {
        databaseManager = newDatabaseManager;
    }

    /**
     * Returns settings for MySQL from the users config
     *
     * @return settings for MySQL from the users config
     */
    public static ConfigSectionMySQL getMySQLConfigSettings() {
        return configManager.getConfigDatabase().getConfigSectionMySQL();
    }

    /**
     * Returns settings for Player Leveling from the users config
     *
     * @return settings for Player Leveling from the users config
     */
    public static ConfigLeveling getPlayerLevelingSettings() {
        return configManager.getConfigLeveling();
    }

    /**
     * Returns settings for Database cleaning from the users config
     *
     * @return settings for Database cleaning from the users config
     */
    public static ConfigSectionCleaning getDatabaseCleaningSettings() {
        return configManager.getConfigDatabase().getConfigSectionCleaning();
    }

    /**
     * Returns settings for Party XP sharing from the users config
     *
     * @return settings for the Party XP sharing from the users config
     */
    public static ConfigSectionPartyExperienceSharing getPartyXPShareSettings() {
        return configManager.getConfigParty().getPartyXP().getPartyExperienceSharing();
    }

    /**
     * Returns settings for Party Leveling from the users config
     *
     * @return settings for the Party Leveling from the users config
     */
    public static ConfigSectionPartyLevel getPartyLevelSettings() {
        return configManager.getConfigParty().getPartyXP().getPartyLevel();
    }

    /**
     * Returns settings for Scoreboards from the users config
     *
     * @return settings for Scoreboards from the users config
     */
    public static ConfigScoreboard getScoreboardSettings() {
        return configManager.getConfigScoreboard();
    }

    /*public static HolidayManager getHolidayManager() {
        return holidayManager;
    }*/

    public static boolean isHealthBarPluginEnabled() {
        return healthBarPluginEnabled;
    }

    /**
     * Checks if this plugin is using retro mode
     * Retro mode is a 0-1000 skill system
     * Standard mode is scaled for 1-100
     *
     * @return true if retro mode is enabled
     */
    public static boolean isRetroModeEnabled() {
        return configManager.isRetroMode();
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * The directory in which override locales are kept
     *
     * @return the override locale directory
     */
    public static String getLocalesDirectory() {
        return localesDirectory;
    }

    /**
     * If an XP rate event is currently in place
     *
     * @return
     */
    public boolean isXPEventEnabled() {
        return xpEventEnabled;
    }

    /*public static ModManager getModManager() {
        return modManager;
    }*/

    /*public static UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }*/

    /**
     * Sets the xpEventEnabled boolean
     *
     * @param enabled the new boolean state
     */
    public void setXPEventEnabled(boolean enabled) {
        this.xpEventEnabled = enabled;
    }

    /**
     * Flips the XP events boolean
     */
    public void toggleXpEventEnabled() {
        xpEventEnabled = !xpEventEnabled;
    }

    /**
     * Debug helper method
     * Prefixes log entries with [Debug]
     *
     * @param message the message to log with a Debug prefix
     */
    public void debug(String message) {
        getLogger().info("[Debug] " + message);
    }

    /**
     * Setup the various storage file paths
     */
    private void setupFilePaths() {
        mcMMOFile = getFile();
        mainDirectory = getDataFolder().getPath() + File.separator;
        localesDirectory = mainDirectory + "locales" + File.separator;
        flatFileDirectory = mainDirectory + "flatfile" + File.separator;
        usersFile = flatFileDirectory + "mcmmo.users";
        modDirectory = mainDirectory + "mods" + File.separator;
        fixFilePaths();
    }

    private void fixFilePaths() {
        File oldFlatfilePath = new File(mainDirectory + "FlatFileStuff" + File.separator);

        if (oldFlatfilePath.exists()) {
            if (!oldFlatfilePath.renameTo(new File(flatFileDirectory))) {
                getLogger().warning("Failed to rename FlatFileStuff to flatfile!");
            }
        }

        File currentFlatfilePath = new File(flatFileDirectory);
        currentFlatfilePath.mkdirs();
        File localesDirectoryPath = new File(localesDirectory);
        localesDirectoryPath.mkdirs();
    }

    private void registerDynamicSettings() {
        dynamicSettingsManager = new DynamicSettingsManager();
    }

    private void loadConfigFiles() {
        configManager = new ConfigManager();
        configManager.loadConfigs();
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();

        // Register events
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new EntityListener(this), this);
        pluginManager.registerEvents(new InventoryListener(this), this);
        pluginManager.registerEvents(new SelfListener(this), this);
        pluginManager.registerEvents(new WorldListener(this), this);
    }

    /**
     * Registers core skills
     * This enables the skills in the new skill system
     */
    private void registerCoreSkills() {
        /*
         * Acrobatics skills
         */

        if (mcMMO.getConfigManager().getConfigCoreSkills().isAcrobaticsEnabled()) {
            System.out.println("[mcMMO]" + " enabling Acrobatics Skills");

            //TODO: Should do this differently
            if(mcMMO.getConfigManager().getConfigCoreSkills().isRollEnabled())
            {
                InteractionManager.registerSubSkill(new Roll());
            }
        }
    }

    private void registerCustomRecipes() {
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (MainConfig.getInstance().getChimaeraEnabled()) {
                getServer().addRecipe(ChimaeraWing.getChimaeraWingRecipe());
            }
        }, 40);
    }

    private void scheduleTasks() {
        // Periodic save timer (Saves every 10 minutes by default)
        long saveIntervalTicks = Math.max(1200, (getConfigManager().getConfigDatabase().getConfigSectionDatabaseGeneral().getSaveIntervalMinutes() * (20 * 60)));
        new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);

        // Cleanup the backups folder
        new CleanBackupsTask().runTaskAsynchronously(this);

        // Bleed timer (Runs every 0.5 seconds)
        new BleedTimerTask().runTaskTimer(this, Misc.TICK_CONVERSION_FACTOR, (Misc.TICK_CONVERSION_FACTOR / 2));

        // Old & Powerless User remover
        long purgeIntervalTicks = getConfigManager().getConfigDatabase().getConfigSectionCleaning().getPurgeInterval() * 60L * 60L * Misc.TICK_CONVERSION_FACTOR;

        if (getDatabaseCleaningSettings().isOnlyPurgeAtStartup()) {
            new UserPurgeTask().runTaskLaterAsynchronously(this, 2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
        } else if (purgeIntervalTicks > 0) {
            new UserPurgeTask().runTaskTimerAsynchronously(this, purgeIntervalTicks, purgeIntervalTicks);
        }

        //Party System Stuff
        if (configManager.getConfigParty().isPartySystemEnabled()) {
            // Automatically remove old members from parties
            long kickIntervalTicks = getConfigManager().getConfigParty().getPartyCleanup().getPartyAutoKickHoursInterval() * 60L * 60L * Misc.TICK_CONVERSION_FACTOR;

            if (kickIntervalTicks == 0) {
                new PartyAutoKickTask().runTaskLater(this, 2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
            } else if (kickIntervalTicks > 0) {
                new PartyAutoKickTask().runTaskTimer(this, kickIntervalTicks, kickIntervalTicks);
            }
        }

        // Update power level tag scoreboards
        new PowerLevelUpdatingTask().runTaskTimer(this, 2 * Misc.TICK_CONVERSION_FACTOR, 2 * Misc.TICK_CONVERSION_FACTOR);

        // Clear the registered XP data so players can earn XP again
        if (getConfigManager().getConfigLeveling().getConfigLevelingDiminishedReturns().isDiminishedReturnsEnabled()) {
            new ClearRegisteredXPGainTask().runTaskTimer(this, 60, 60);
        }

        if (configManager.getConfigNotifications().getConfigNotificationGeneral().isPlayerTips()) {
            new NotifySquelchReminderTask().runTaskTimer(this, 60, ((20 * 60) * 60));
        }
    }

    public static DynamicSettingsManager getDynamicSettingsManager() {
        return dynamicSettingsManager;
    }

    private enum ServerSoftwareType {
        PAPER,
        SPIGOT,
        CRAFTBUKKIT
    }
}