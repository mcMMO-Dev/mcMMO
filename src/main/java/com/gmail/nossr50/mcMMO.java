package com.gmail.nossr50;

import com.gmail.nossr50.config.ConfigManager;
import com.gmail.nossr50.config.CoreSkillsConfig;
import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.hocon.database.ConfigSectionCleaning;
import com.gmail.nossr50.config.hocon.database.ConfigSectionMySQL;
import com.gmail.nossr50.config.hocon.party.ConfigSectionPartyExperienceSharing;
import com.gmail.nossr50.config.hocon.party.ConfigSectionPartyLevel;
import com.gmail.nossr50.config.hocon.playerleveling.ConfigLeveling;
import com.gmail.nossr50.config.hocon.scoreboard.ConfigScoreboard;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.subskills.acrobatics.Roll;
import com.gmail.nossr50.listeners.*;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.CheckDateTask;
import com.gmail.nossr50.runnables.SaveTimerTask;
import com.gmail.nossr50.runnables.backups.CleanBackupsTask;
import com.gmail.nossr50.runnables.database.UserPurgeTask;
import com.gmail.nossr50.runnables.party.PartyAutoKickTask;
import com.gmail.nossr50.runnables.player.ClearRegisteredXPGainTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.runnables.player.PowerLevelUpdatingTask;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.repair.repairables.RepairableManager;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManager;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManagerFactory;
import com.gmail.nossr50.util.commands.CommandRegistrationManager;
import com.gmail.nossr50.util.experience.FormulaManager;
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
    /* Managers */
    private static ChunkManager       placeStore;
    private static ConfigManager      configManager;
    //private static ModManager         modManager;
    private static DatabaseManager    databaseManager;
    private static FormulaManager     formulaManager;
    private static HolidayManager     holidayManager;
    //private static UpgradeManager     upgradeManager;

    /* File Paths */
    private static String mainDirectory;
    private static String flatFileDirectory;
    private static String usersFile;
    private static String modDirectory;

    public static mcMMO p;

    // Jar Stuff
    public static File mcmmo;

    /* Plugin Checks */
    private static boolean healthBarPluginEnabled;

    // XP Event Check
    private boolean xpEventEnabled;

    /* Metadata Values */
    public static final String FISH_HOOK_REF_METAKEY = "mcMMO: Fish Hook Tracker";
    public static final String CUSTOM_DAMAGE_METAKEY = "mcMMO: Custom Damage";
    public final static String entityMetadataKey   = "mcMMO: Spawned Entity";
    public final static String blockMetadataKey    = "mcMMO: Piston Tracking";
    public final static String furnaceMetadataKey  = "mcMMO: Tracked Furnace";
    public final static String tntMetadataKey      = "mcMMO: Tracked TNT";
    public final static String funfettiMetadataKey = "mcMMO: Funfetti";
    public final static String tntsafeMetadataKey  = "mcMMO: Safe TNT";
    public final static String customNameKey       = "mcMMO: Custom Name";
    public final static String customVisibleKey    = "mcMMO: Name Visibility";
    public final static String droppedItemKey      = "mcMMO: Tracked Item";
    public final static String infiniteArrowKey    = "mcMMO: Infinite Arrow";
    public final static String bowForceKey         = "mcMMO: Bow Force";
    public final static String arrowDistanceKey    = "mcMMO: Arrow Distance";
    //public final static String customDamageKey     = "mcMMO: Custom Damage";
    public final static String disarmedItemKey     = "mcMMO: Disarmed Item";
    public final static String playerDataKey       = "mcMMO: Player Data";
    public final static String greenThumbDataKey   = "mcMMO: Green Thumb";
    public final static String doubleDropKey       = "mcMMO: Double Drop";
    public final static String tripleDropKey       = "mcMMO: Triple Drop";
    public final static String databaseCommandKey  = "mcMMO: Processing Database Command";
    public final static String bredMetadataKey     = "mcMMO: Bred Animal";

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

            PluginManager pluginManager = getServer().getPluginManager();
            healthBarPluginEnabled = pluginManager.getPlugin("HealthBar") != null;

            //upgradeManager = new UpgradeManager();

            setupFilePaths();

            //modManager = new ModManager();

            loadConfigFiles();

            if (healthBarPluginEnabled) {
                getLogger().info("HealthBar plugin found, mcMMO's healthbars are automatically disabled.");
            }

            if (pluginManager.getPlugin("NoCheatPlus") != null && pluginManager.getPlugin("CompatNoCheatPlus") == null) {
                getLogger().warning("NoCheatPlus plugin found, but CompatNoCheatPlus was not found!");
                getLogger().warning("mcMMO will not work properly alongside NoCheatPlus without CompatNoCheatPlus");
            }

            databaseManager = DatabaseManagerFactory.getDatabaseManager();

            registerEvents();
            registerCoreSkills();
            registerCustomRecipes();

            if(getConfigManager().getConfigParty().isPartySystemEnabled())
                PartyManager.loadParties();

            formulaManager = new FormulaManager();
            holidayManager = new HolidayManager();

            for (Player player : getServer().getOnlinePlayers()) {
                new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(mcMMO.p, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
            }

            debug("Version " + getDescription().getVersion() + " is enabled!");

            scheduleTasks();
            CommandRegistrationManager.registerCommands();

            placeStore = ChunkManagerFactory.getChunkManager(); // Get our ChunkletManager

            if (mcMMO.getConfigManager().getConfigParty().getPTP().isPtpWorldBasedPermissions()) {
                Permissions.generateWorldTeleportPermissions();
            }

            //Populate Ranked Skill Maps (DO THIS LAST)
            RankUtils.populateRanks();

            //If anonymous statistics are enabled then use them
            if(getConfigManager().getConfigMetrics().isAllowAnonymousUsageStatistics()) {
                Metrics metrics;

                metrics = new Metrics(this);
                metrics.addCustomChart(new Metrics.SimplePie("version", () -> getDescription().getVersion()));

                if(!configManager.getConfigLeveling().getConfigSectionLevelingGeneral().getConfigSectionLevelScaling().isRetroModeEnabled())
                    metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Standard"));
                else
                    metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Retro"));

                //Simplified Server Software Tracking
                if(Bukkit.getServer().getBukkitVersion().contains("paper"))
                    metrics.addCustomChart(new Metrics.SimplePie("Server_software", () -> "Paper"));
                else if(Bukkit.getServer().getBukkitVersion().contains("spigot"))
                    metrics.addCustomChart(new Metrics.SimplePie("Server_software", () -> "Spigot"));
                else if(Bukkit.getServer().getBukkitVersion().contains("bukkit"))
                    metrics.addCustomChart(new Metrics.SimplePie("Server_software", () -> "CraftBukkit"));
                else
                    metrics.addCustomChart(new Metrics.SimplePie("Server_software", () -> "Unknown"));
            }
        }
        catch (Throwable t) {
            getLogger().severe("There was an error while enabling mcMMO!");
            t.printStackTrace();
            getLogger().severe("End of error report for mcMMO");
            getLogger().info("Please do not replace the mcMMO jar while the server is running.");

            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onLoad()
    {
        if(getServer().getPluginManager().getPlugin("WorldGuard") != null)
            WorldGuardManager.getInstance().registerFlags();
    }

    /**
     * Things to be run when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        try {
            Alchemy.finishAllBrews();   // Finish all partially complete AlchemyBrewTasks to prevent vanilla brewing continuation on restart
            UserManager.saveAll();      // Make sure to save player information if the server shuts down
            UserManager.clearAll();
            PartyManager.saveParties(); // Save our parties

            //TODO: Needed?
            if(mcMMO.getScoreboardSettings().getScoreboardsEnabled())
                ScoreboardManager.teardownAll();

            formulaManager.saveFormula();
            holidayManager.saveAnniversaryFiles();
            placeStore.saveAll();       // Save our metadata
            placeStore.cleanUp();       // Cleanup empty metadata stores
        }
        catch (NullPointerException e) { e.printStackTrace(); }

        debug("Canceling all tasks...");
        getServer().getScheduler().cancelTasks(this); // This removes our tasks
        debug("Unregister all events...");
        HandlerList.unregisterAll(this); // Cancel event registrations

        if (mcMMO.getConfigManager().getConfigAutomatedBackups().isZipBackupsEnabled()) {
            // Remove other tasks BEFORE starting the Backup, or we just cancel it straight away.
            try {
                ZipLibrary.mcMMOBackup();
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

        databaseManager.onDisable();

        //Unload configs last
        configManager.unloadAllConfigsAndRegisters();


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

    public static HolidayManager getHolidayManager() {
        return holidayManager;
    }

    public static ChunkManager getPlaceStore() {
        return placeStore;
    }

    public static RepairableManager getRepairableManager() {
        return configManager.getSimpleRepairableManager();
    }

    public static SalvageableManager getSalvageableManager() {
        return configManager.getSimpleSalvageableManager();
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * Returns settings for MySQL from the users config
     * @return settings for MySQL from the users config
     */
    public static ConfigSectionMySQL getMySQLConfigSettings()
    {
        return configManager.getConfigDatabase().getConfigSectionMySQL();
    }

    /**
     * Returns settings for Player Leveling from the users config
     * @return settings for Player Leveling from the users config
     */
    public static ConfigLeveling getPlayerLevelingSettings()
    {
        return configManager.getConfigLeveling();
    }

    /**
     * Returns settings for Database cleaning from the users config
     * @return settings for Database cleaning from the users config
     */
    public static ConfigSectionCleaning getDatabaseCleaningSettings()
    {
        return configManager.getConfigDatabase().getConfigSectionCleaning();
    }

    /**
     * Returns settings for Party XP sharing from the users config
     * @return settings for the Party XP sharing from the users config
     */
    public static ConfigSectionPartyExperienceSharing getPartyXPShareSettings()
    {
        return configManager.getConfigParty().getPartyXP().getPartyExperienceSharing();
    }

    /**
     * Returns settings for Party Leveling from the users config
     * @return settings for the Party Leveling from the users config
     */
    public static ConfigSectionPartyLevel getPartyLevelSettings()
    {
        return configManager.getConfigParty().getPartyXP().getPartyLevel();
    }

    /**
     * Returns settings for Scoreboards from the users config
     * @return settings for Scoreboards from the users config
     */
    public static ConfigScoreboard getScoreboardSettings()
    {
        return configManager.getConfigScoreboard();
    }

    /*public static ModManager getModManager() {
        return modManager;
    }*/

    /*public static UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }*/

    @Deprecated
    public static void setDatabaseManager(DatabaseManager databaseManager) {
        mcMMO.databaseManager = databaseManager;
    }

    public static boolean isHealthBarPluginEnabled() {
        return healthBarPluginEnabled;
    }

    /**
     * Setup the various storage file paths
     */
    private void setupFilePaths() {
        mcmmo = getFile();
        mainDirectory = getDataFolder().getPath() + File.separator;
        flatFileDirectory = mainDirectory + "flatfile" + File.separator;
        usersFile = flatFileDirectory + "mcmmo.users";
        modDirectory = mainDirectory + "mods" + File.separator;
        fixFilePaths();
    }

    private void fixFilePaths() {
        File oldFlatfilePath = new File(mainDirectory + "FlatFileStuff" + File.separator);
        File oldModPath = new File(mainDirectory + "ModConfigs" + File.separator);

        if (oldFlatfilePath.exists()) {
            if (!oldFlatfilePath.renameTo(new File(flatFileDirectory))) {
                getLogger().warning("Failed to rename FlatFileStuff to flatfile!");
            }
        }

        if (oldModPath.exists()) {
            if (!oldModPath.renameTo(new File(modDirectory))) {
                getLogger().warning("Failed to rename ModConfigs to mods!");
            }
        }

        File oldArmorFile    = new File(modDirectory + "armor.yml");
        File oldBlocksFile   = new File(modDirectory + "blocks.yml");
        File oldEntitiesFile = new File(modDirectory + "entities.yml");
        File oldToolsFile    = new File(modDirectory + "tools.yml");

        if (oldArmorFile.exists()) {
            if (!oldArmorFile.renameTo(new File(modDirectory + "armor.default.yml"))) {
                getLogger().warning("Failed to rename armor.yml to armor.default.yml!");
            }
        }

        if (oldBlocksFile.exists()) {
            if (!oldBlocksFile.renameTo(new File(modDirectory + "blocks.default.yml"))) {
                getLogger().warning("Failed to rename blocks.yml to blocks.default.yml!");
            }
        }

        if (oldEntitiesFile.exists()) {
            if (!oldEntitiesFile.renameTo(new File(modDirectory + "entities.default.yml"))) {
                getLogger().warning("Failed to rename entities.yml to entities.default.yml!");
            }
        }

        if (oldToolsFile.exists()) {
            if (!oldToolsFile.renameTo(new File(modDirectory + "tools.default.yml"))) {
                getLogger().warning("Failed to rename tools.yml to tools.default.yml!");
            }
        }

        File currentFlatfilePath = new File(flatFileDirectory);
        currentFlatfilePath.mkdirs();
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

        if(CoreSkillsConfig.getInstance().isPrimarySkillEnabled(PrimarySkillType.ACROBATICS))
        {
            System.out.println("[mcMMO]" + " enabling Acrobatics Skills");

            //TODO: Should do this differently
            Roll roll = new Roll();
            CoreSkillsConfig.getInstance().isSkillEnabled(roll);
            InteractionManager.registerSubSkill(new Roll());
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
        long saveIntervalTicks = Math.max(1200, (getConfigManager().getConfigAutomatedBackups().getSaveIntervalMinutes() * 1200));
        new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);

        // Cleanup the backups folder
        new CleanBackupsTask().runTaskAsynchronously(mcMMO.p);

        // Bleed timer (Runs every 0.5 seconds)
        new BleedTimerTask().runTaskTimer(this, 1 * Misc.TICK_CONVERSION_FACTOR, 1 * (Misc.TICK_CONVERSION_FACTOR / 2));

        // Old & Powerless User remover
        long purgeIntervalTicks = getConfigManager().getConfigDatabase().getConfigSectionCleaning().getPurgeInterval() * 60L * 60L * Misc.TICK_CONVERSION_FACTOR;

        if (mcMMO.getDatabaseCleaningSettings().isOnlyPurgeAtStartup()) {
            new UserPurgeTask().runTaskLaterAsynchronously(this, 2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
        }
        else if (purgeIntervalTicks > 0) {
            new UserPurgeTask().runTaskTimerAsynchronously(this, purgeIntervalTicks, purgeIntervalTicks);
        }

        //Party System Stuff
        if(mcMMO.configManager.getConfigParty().isPartySystemEnabled())
        {
            // Automatically remove old members from parties
            long kickIntervalTicks = getConfigManager().getConfigParty().getPartyCleanup().getPartyAutoKickHoursInterval() * 60L * 60L * Misc.TICK_CONVERSION_FACTOR;

            if (kickIntervalTicks == 0) {
                new PartyAutoKickTask().runTaskLater(this, 2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
            }
            else if (kickIntervalTicks > 0) {
                new PartyAutoKickTask().runTaskTimer(this, kickIntervalTicks, kickIntervalTicks);
            }
        }


        // Update power level tag scoreboards
        new PowerLevelUpdatingTask().runTaskTimer(this, 2 * Misc.TICK_CONVERSION_FACTOR, 2 * Misc.TICK_CONVERSION_FACTOR);

        if (getHolidayManager().nearingAprilFirst()) {
            new CheckDateTask().runTaskTimer(this, 10L * Misc.TICK_CONVERSION_FACTOR, 1L * 60L * 60L * Misc.TICK_CONVERSION_FACTOR);
        }

        // Clear the registered XP data so players can earn XP again
        if (ExperienceConfig.getInstance().getDiminishedReturnsEnabled()) {
            new ClearRegisteredXPGainTask().runTaskTimer(this, 60, 60);
        }
    }

    /*private void checkModConfigs() {
        if (!MainConfig.getInstance().getToolModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom tool config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Tool_Mods_Enabled to TRUE in config.yml.");
        }

        if (!MainConfig.getInstance().getArmorModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom armor config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Armor_Mods_Enabled to TRUE in config.yml.");
        }

        if (!MainConfig.getInstance().getBlockModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom block config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Block_Mods_Enabled to TRUE in config.yml.");
        }

        if (!MainConfig.getInstance().getEntityModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom entity config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Entity_Mods_Enabled to TRUE in config.yml.");
        }
    }*/

    /**
     * Checks if this plugin is using retro mode
     * Retro mode is a 0-1000 skill system
     * Standard mode is scaled for 1-100
     * @return true if retro mode is enabled
     */
    public static boolean isRetroModeEnabled() {
        return getPlayerLevelingSettings().getConfigSectionLevelingGeneral().getConfigSectionLevelScaling().isRetroModeEnabled();
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
}
