package com.gmail.nossr50;

import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.commands.CommandManager;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.CoreSkillsConfig;
import com.gmail.nossr50.config.CustomItemSupportConfig;
import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.config.SoundConfig;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.party.PartyConfig;
import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.config.skills.repair.RepairConfigManager;
import com.gmail.nossr50.config.skills.salvage.SalvageConfigManager;
import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.subskills.acrobatics.Roll;
import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.listeners.ChunkListener;
import com.gmail.nossr50.listeners.EntityListener;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.listeners.InventoryListener;
import com.gmail.nossr50.listeners.PlayerListener;
import com.gmail.nossr50.listeners.SelfListener;
import com.gmail.nossr50.listeners.WorldListener;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.placeholders.PapiExpansion;
import com.gmail.nossr50.runnables.SaveTimerTask;
import com.gmail.nossr50.runnables.backups.CleanBackupsTask;
import com.gmail.nossr50.runnables.commands.NotifySquelchReminderTask;
import com.gmail.nossr50.runnables.database.UserPurgeTask;
import com.gmail.nossr50.runnables.party.PartyAutoKickTask;
import com.gmail.nossr50.runnables.player.ClearRegisteredXPGainTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.runnables.player.PowerLevelUpdatingTask;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableManager;
import com.gmail.nossr50.skills.repair.repairables.SimpleRepairableManager;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableManager;
import com.gmail.nossr50.skills.salvage.salvageables.SimpleSalvageableManager;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.EnchantmentMapper;
import com.gmail.nossr50.util.LogFilter;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.MaterialMapStore;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TransientEntityTracker;
import com.gmail.nossr50.util.TransientMetadataTools;
import com.gmail.nossr50.util.blockmeta.ChunkManager;
import com.gmail.nossr50.util.blockmeta.ChunkManagerFactory;
import com.gmail.nossr50.util.blockmeta.UserBlockTracker;
import com.gmail.nossr50.util.commands.CommandRegistrationManager;
import com.gmail.nossr50.util.compat.CompatibilityManager;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.platform.PlatformManager;
import com.gmail.nossr50.util.platform.ServerSoftwareType;
import com.gmail.nossr50.util.player.PlayerLevelUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.util.upgrade.UpgradeManager;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.tcoded.folialib.FoliaLib;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.shatteredlands.shatt.backup.ZipLibrary;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class mcMMO extends JavaPlugin {
    /* Managers & Services */
    private static PlatformManager platformManager;
    private static ChunkManager chunkManager;
    private static RepairableManager repairableManager;
    private static SalvageableManager salvageableManager;
    private static DatabaseManager databaseManager;
    private static FormulaManager formulaManager;
    private static UpgradeManager upgradeManager;
    private static MaterialMapStore materialMapStore;
    private static PlayerLevelUtils playerLevelUtils;
    private static TransientMetadataTools transientMetadataTools;
    private static ChatManager chatManager;
    private static CommandManager commandManager; //ACF
    private static TransientEntityTracker transientEntityTracker;

    private SkillTools skillTools;

    private static boolean serverShutdownExecuted = false;

    /* Adventure */
    private static BukkitAudiences audiences;

    /* Blacklist */
    private static WorldBlacklist worldBlacklist;

    /* File Paths */
    private static String mainDirectory;
    private static String localesDirectory;
    private static String flatFileDirectory;
    private static String usersFile;
    private static String modDirectory;

    public static mcMMO p;

    // Jar Stuff
    public static File mcmmo;

    /* Plugin Checks */
    private static boolean healthBarPluginEnabled;
    private static boolean projectKorraEnabled;

    // API checks
    private static boolean serverAPIOutdated = false;

    // Config Validation Check
    public boolean noErrorsInConfigFiles = true;

    // XP Event Check
    private boolean xpEventEnabled;

    private static boolean isRetroModeEnabled;

    private long purgeTime = 2630000000L;

    private GeneralConfig generalConfig;
    private AdvancedConfig advancedConfig;
    private PartyConfig partyConfig;
    private PotionConfig potionConfig;
    private CustomItemSupportConfig customItemSupportConfig;
    private EnchantmentMapper enchantmentMapper;

    private FoliaLib foliaLib;
    private PartyManager partyManager;

    public mcMMO() {
        p = this;
    }

    /**
     * Things to be run when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        try {
            //Filter out any debug messages (if debug/verbose logging is not enabled)
            getLogger().setFilter(new LogFilter(this));

            //Platform Manager
            platformManager = new PlatformManager();

            //Folia lib plugin instance
            foliaLib = new FoliaLib(this);
            foliaLib.getOptions().disableNotifications();
            // Performance optimization
            // This makes the scheduler behave differently between Spigot/Legacy-Paper & Folia/Modern-Paper
            foliaLib.getOptions().disableIsValidOnNonFolia();

            setupFilePaths();
            generalConfig = new GeneralConfig(getDataFolder()); //Load before skillTools
            skillTools = new SkillTools(this); //Load after general config

            //Init configs
            advancedConfig = new AdvancedConfig(getDataFolder());
            partyConfig = new PartyConfig(getDataFolder());
            customItemSupportConfig = new CustomItemSupportConfig(getDataFolder());

            //Store this value so other plugins can check it
            isRetroModeEnabled = generalConfig.getIsRetroMode();

            MetadataConstants.MCMMO_METADATA_VALUE = new FixedMetadataValue(this, true);

            PluginManager pluginManager = getServer().getPluginManager();
            healthBarPluginEnabled = pluginManager.getPlugin("HealthBar") != null;
            projectKorraEnabled = pluginManager.getPlugin("ProjectKorra") != null;

            upgradeManager = new UpgradeManager();

            // Init Material Maps
            materialMapStore = new MaterialMapStore();
            // Init compatibility mappers
            enchantmentMapper = new EnchantmentMapper(this);
            loadConfigFiles();

            if (!noErrorsInConfigFiles) {
                return;
            }

            if (getServer().getName().equals("Cauldron") || getServer().getName().equals("MCPC+")) {
                checkModConfigs();
            }

            if (projectKorraEnabled) {
                getLogger().info(
                        "ProjectKorra was detected, this can cause some issues with weakness potions and combat skills for mcMMO");
            }

            if (healthBarPluginEnabled) {
                getLogger().info(
                        "HealthBar plugin found, mcMMO's healthbars are automatically disabled.");
            }

            if (pluginManager.getPlugin("NoCheatPlus") != null && pluginManager.getPlugin(
                    "CompatNoCheatPlus") == null) {
                getLogger().warning(
                        "NoCheatPlus plugin found, but CompatNoCheatPlus was not found!");
                getLogger().warning(
                        "mcMMO will not work properly alongside NoCheatPlus without CompatNoCheatPlus");
            }

            // One month in milliseconds
            this.purgeTime = 2630000000L * generalConfig.getOldUsersCutoff();

            databaseManager = DatabaseManagerFactory.getDatabaseManager(
                    mcMMO.getUsersFilePath(), getLogger(),
                    purgeTime, mcMMO.p.getAdvancedConfig().getStartingLevel());

            //Check for the newer API and tell them what to do if its missing
            checkForOutdatedAPI();

            if (serverAPIOutdated) {
                foliaLib.getScheduler().runTimer(
                        () -> getLogger().severe(
                                "You are running an outdated version of "
                                        + platformManager.getServerSoftware()
                                        + ", mcMMO will not work unless you update to a newer version!"),
                        20, 20 * 60 * 30);

                if (platformManager.getServerSoftware() == ServerSoftwareType.CRAFT_BUKKIT) {
                    foliaLib.getScheduler().runTimer(
                            () -> getLogger().severe(
                                    "We have detected you are using incompatible server software, our best guess is that you are using CraftBukkit. mcMMO requires Spigot or Paper, if you are not using CraftBukkit, you will still need to update your custom server software before mcMMO will work."),
                            20, 20 * 60 * 30);
                }
            } else {
                registerEvents();
                registerCoreSkills();
                registerCustomRecipes();

                if (partyConfig.isPartyEnabled()) {
                    partyManager = new PartyManager(this);
                    partyManager.loadParties();
                }

                formulaManager = new FormulaManager();

                for (Player player : getServer().getOnlinePlayers()) {
                    getFoliaLib().getScheduler().runLaterAsync(
                            new PlayerProfileLoadingTask(player),
                            1); // 1 Tick delay to ensure the player is marked as online before we begin loading
                }

                LogUtils.debug(mcMMO.p.getLogger(),
                        "Version " + getDescription().getVersion() + " is enabled!");

                scheduleTasks();
                CommandRegistrationManager.registerCommands();

                chunkManager = ChunkManagerFactory.getChunkManager(); // Get our ChunkletManager

                if (generalConfig.getPTPCommandWorldPermissions()) {
                    Permissions.generateWorldTeleportPermissions();
                }

                //Populate Ranked Skill Maps (DO THIS LAST)
                RankUtils.populateRanks();
            }

            //If anonymous statistics are enabled then use them
            Metrics metrics;

            if (generalConfig.getIsMetricsEnabled()) {
                metrics = new Metrics(this, 3894);
                metrics.addCustomChart(
                        new SimplePie("version", () -> getDescription().getVersion()));

                if (generalConfig.getIsRetroMode()) {
                    metrics.addCustomChart(new SimplePie("leveling_system", () -> "Retro"));
                } else {
                    metrics.addCustomChart(new SimplePie("leveling_system", () -> "Standard"));
                }
            }
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "There was an error while enabling mcMMO!", t);

            if (t instanceof ExceptionInInitializerError) {
                getLogger().info("Please do not replace the mcMMO jar while the server"
                        + " is running.");
            }

            getServer().getPluginManager().disablePlugin(this);

            //Fixes #4438 - Don't initialize things if we are going to disable mcMMO anyway
            return;
        }

        //Init player level values
        playerLevelUtils = new PlayerLevelUtils();

        //Init the blacklist
        worldBlacklist = new WorldBlacklist(this);

        //Set up Adventure's audiences
        audiences = BukkitAudiences.create(this);

        transientMetadataTools = new TransientMetadataTools(this);

        chatManager = new ChatManager(this);

        commandManager = new CommandManager(this);

        transientEntityTracker = new TransientEntityTracker();
        setServerShutdown(false); //Reset flag, used to make decisions about async saves

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiExpansion().register();
        }
    }

    public static PlayerLevelUtils getPlayerLevelUtils() {
        return playerLevelUtils;
    }

    public static MaterialMapStore getMaterialMapStore() {
        return materialMapStore;
    }

    private void checkForOutdatedAPI() {
        try {
            Class<?> checkForClass = Class.forName("org.bukkit.event.block.BlockDropItemEvent");
            checkForClass.getMethod("getItems");
            Class.forName("net.md_5.bungee.api.chat.BaseComponent");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            serverAPIOutdated = true;
            String software = platformManager.getServerSoftwareStr();
            getLogger().severe(
                    "You are running an older version of " + software
                            + " that is not compatible with mcMMO, update your server software!");
        }
    }

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            WorldGuardManager.getInstance().registerFlags();
        }
    }

    /**
     * Things to be run when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        setServerShutdown(true);
        //TODO: Write code to catch unfinished async save tasks, for now we just hope they finish in time, which they should in most cases
        mcMMO.p.getLogger()
                .info("Server shutdown has been executed, saving and cleaning up data...");

        try {
            UserManager.saveAll();      // Make sure to save player information if the server shuts down
            UserManager.clearAll();
            Alchemy.finishAllBrews();   // Finish all partially complete AlchemyBrewTasks to prevent vanilla brewing continuation on restart
            if (partyConfig.isPartyEnabled()) {
                getPartyManager().saveParties(); // Save our parties
            }

            //TODO: Needed?
            if (generalConfig.getScoreboardsEnabled()) {
                ScoreboardManager.teardownAll();
            }

            formulaManager.saveFormula();
            chunkManager.closeAll();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while disabling mcMMO!", e);
        }

        if (generalConfig.getBackupsEnabled()) {
            // Remove other tasks BEFORE starting the Backup, or we just cancel it straight away.
            try {
                ZipLibrary.mcMMOBackup();
            } catch (NoClassDefFoundError e) {
                getLogger().severe("Backup class not found!");
                getLogger().info(
                        "Please do not replace the mcMMO jar while the server is running.");
            } catch (Throwable e) {
                getLogger().severe(e.toString());
            }
        }

        LogUtils.debug(mcMMO.p.getLogger(), "Canceling all tasks...");
        getFoliaLib().getScheduler().cancelAllTasks(); // This removes our tasks
        LogUtils.debug(mcMMO.p.getLogger(), "Unregister all events...");
        HandlerList.unregisterAll(this); // Cancel event registrations

        databaseManager.onDisable();
        LogUtils.debug(mcMMO.p.getLogger(), "Was disabled."); // How informative!
    }

    public static String getMainDirectory() {
        return mainDirectory;
    }

    public static String getLocalesDirectory() {
        return localesDirectory;
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

    public static FormulaManager getFormulaManager() {
        return formulaManager;
    }

    /**
     * Get the {@link UserBlockTracker}.
     *
     * @return the {@link UserBlockTracker}
     */
    public static UserBlockTracker getUserBlockTracker() {
        return chunkManager;
    }

    /**
     * Get the chunk manager.
     *
     * @return the chunk manager
     */
    public static ChunkManager getChunkManager() {
        return chunkManager;
    }

    /**
     * Get the chunk manager.
     *
     * @return the chunk manager
     * @deprecated Use {@link #getChunkManager()} or {@link #getUserBlockTracker()} instead.
     */
    @Deprecated(since = "2.2.013", forRemoval = true)
    public static ChunkManager getPlaceStore() {
        return chunkManager;
    }

    public static RepairableManager getRepairableManager() {
        return repairableManager;
    }

    public static SalvageableManager getSalvageableManager() {
        return salvageableManager;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    public static @Nullable CompatibilityManager getCompatibilityManager() {
        return platformManager.getCompatibilityManager();
    }

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
        localesDirectory = mainDirectory + "locales" + File.separator;
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

        File oldArmorFile = new File(modDirectory + "armor.yml");
        File oldBlocksFile = new File(modDirectory + "blocks.yml");
        File oldEntitiesFile = new File(modDirectory + "entities.yml");
        File oldToolsFile = new File(modDirectory + "tools.yml");

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
        File localesDirectoryPath = new File(localesDirectory);
        localesDirectoryPath.mkdirs();
    }

    private void loadConfigFiles() {
        // Force the loading of config files
        TreasureConfig.getInstance();
        FishingTreasureConfig.getInstance();
        HiddenConfig.getInstance();

        // init potion config
        potionConfig = new PotionConfig();
        potionConfig.loadPotions();

        CoreSkillsConfig.getInstance();
        SoundConfig.getInstance();
        RankConfig.getInstance();

        // Load repair configs, make manager, and register them at this time
        final List<Repairable> repairables = new ArrayList<>(
                new RepairConfigManager(this).getLoadedRepairables());
        repairableManager = new SimpleRepairableManager(repairables.size());
        repairableManager.registerRepairables(repairables);

        // Load salvage configs, make manager and register them at this time
        SalvageConfigManager sManager = new SalvageConfigManager(this);
        final List<Salvageable> salvageables = sManager.getLoadedSalvageables();
        salvageableManager = new SimpleSalvageableManager(salvageables.size());
        salvageableManager.registerSalvageables(salvageables);
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
        pluginManager.registerEvents(new ChunkListener(), this);
        //        pluginManager.registerEvents(new CommandListener(this), this);
    }

    /**
     * Registers core skills This enables the skills in the new skill system
     */
    private void registerCoreSkills() {
        /*
         * Acrobatics skills
         */

        InteractionManager.initMaps(); //Init maps

        if (CoreSkillsConfig.getInstance().isPrimarySkillEnabled(PrimarySkillType.ACROBATICS)) {
            LogUtils.debug(mcMMO.p.getLogger(), "Enabling Acrobatics Skills");

            //TODO: Should do this differently
            Roll roll = new Roll();
            CoreSkillsConfig.getInstance().isSkillEnabled(roll);
            InteractionManager.registerSubSkill(new Roll());
        }
    }

    private void registerCustomRecipes() {
        getFoliaLib().getScheduler().runLater(
                () -> {
                    if (generalConfig.getChimaeraEnabled()) {
                        getServer().addRecipe(ChimaeraWing.getChimaeraWingRecipe());
                    }
                }, 40);
    }

    private void scheduleTasks() {
        // Periodic save timer (Saves every 10 minutes by default)
        long second = 20;
        long minute = second * 60;

        long saveIntervalTicks = Math.max(minute, generalConfig.getSaveInterval() * minute);

        getFoliaLib().getScheduler()
                .runTimer(new SaveTimerTask(), saveIntervalTicks, saveIntervalTicks);

        // Cleanup the backups folder
        getFoliaLib().getScheduler().runAsync(new CleanBackupsTask());

        // Old & Powerless User remover
        long purgeIntervalTicks =
                generalConfig.getPurgeInterval() * 60L * 60L * Misc.TICK_CONVERSION_FACTOR;

        if (purgeIntervalTicks == 0) {
            getFoliaLib().getScheduler().runLaterAsync(
                    new UserPurgeTask(),
                    2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
        } else if (purgeIntervalTicks > 0) {
            getFoliaLib().getScheduler()
                    .runTimerAsync(new UserPurgeTask(), purgeIntervalTicks, purgeIntervalTicks);
        }

        // Automatically remove old members from parties
        if (partyConfig.isPartyEnabled()) {
            long kickIntervalTicks = generalConfig.getAutoPartyKickInterval() * 60L * 60L
                    * Misc.TICK_CONVERSION_FACTOR;

            if (kickIntervalTicks == 0) {
                getFoliaLib().getScheduler().runLater(
                        new PartyAutoKickTask(),
                        2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
            } else if (kickIntervalTicks > 0) {
                getFoliaLib().getScheduler()
                        .runTimer(new PartyAutoKickTask(), kickIntervalTicks, kickIntervalTicks);
            }
        }

        // Update power level tag scoreboards
        getFoliaLib().getScheduler().runTimer(
                new PowerLevelUpdatingTask(), 2 * Misc.TICK_CONVERSION_FACTOR,
                2 * Misc.TICK_CONVERSION_FACTOR);

        // Clear the registered XP data so players can earn XP again
        if (ExperienceConfig.getInstance().getDiminishedReturnsEnabled()) {
            getFoliaLib().getScheduler().runTimer(new ClearRegisteredXPGainTask(), 60, 60);
        }

        if (mcMMO.p.getAdvancedConfig().allowPlayerTips()) {
            getFoliaLib().getScheduler()
                    .runTimer(new NotifySquelchReminderTask(), 60, ((20 * 60) * 60));
        }
    }

    private void checkModConfigs() {
        if (!generalConfig.getToolModsEnabled()) {
            getLogger().warning(
                    "Cauldron implementation found, but the custom tool config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Tool_Mods_Enabled to TRUE in config.yml.");
        }

        if (!generalConfig.getArmorModsEnabled()) {
            getLogger().warning(
                    "Cauldron implementation found, but the custom armor config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Armor_Mods_Enabled to TRUE in config.yml.");
        }

        if (!generalConfig.getBlockModsEnabled()) {
            getLogger().warning(
                    "Cauldron implementation found, but the custom block config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Block_Mods_Enabled to TRUE in config.yml.");
        }

        if (!generalConfig.getEntityModsEnabled()) {
            getLogger().warning(
                    "Cauldron implementation found, but the custom entity config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Entity_Mods_Enabled to TRUE in config.yml.");
        }
    }

    public @Nullable InputStreamReader getResourceAsReader(@NotNull String fileName) {
        InputStream in = getResource(fileName);
        return in == null ? null : new InputStreamReader(in, StandardCharsets.UTF_8);
    }

    /**
     * Checks if this plugin is using retro mode Retro mode is a 0-1000 skill system Standard mode
     * is scaled for 1-100
     *
     * @return true if retro mode is enabled
     */
    public static boolean isRetroModeEnabled() {
        return isRetroModeEnabled;
    }

    public static WorldBlacklist getWorldBlacklist() {
        return worldBlacklist;
    }

    public static PlatformManager getPlatformManager() {
        return platformManager;
    }

    public static BukkitAudiences getAudiences() {
        return audiences;
    }

    public static boolean isProjectKorraEnabled() {
        return projectKorraEnabled;
    }

    public static TransientMetadataTools getTransientMetadataTools() {
        return transientMetadataTools;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static TransientEntityTracker getTransientEntityTracker() {
        return transientEntityTracker;
    }

    public static synchronized boolean isServerShutdownExecuted() {
        return serverShutdownExecuted;
    }

    private static synchronized void setServerShutdown(boolean bool) {
        serverShutdownExecuted = bool;
    }

    public long getPurgeTime() {
        return purgeTime;
    }

    public @NotNull SkillTools getSkillTools() {
        return skillTools;
    }

    public @NotNull GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public @NotNull AdvancedConfig getAdvancedConfig() {
        return advancedConfig;
    }

    public @NotNull PartyConfig getPartyConfig() {
        return partyConfig;
    }

    /**
     * Check if the party system is enabled
     *
     * @return true if the party system is enabled, false otherwise
     */
    public boolean isPartySystemEnabled() {
        return partyConfig.isPartyEnabled();
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public CustomItemSupportConfig getCustomItemSupportConfig() {
        return customItemSupportConfig;
    }

    public PotionConfig getPotionConfig() {
        return potionConfig;
    }

    public EnchantmentMapper getEnchantmentMapper() {
        return enchantmentMapper;
    }

    public @NotNull FoliaLib getFoliaLib() {
        return foliaLib;
    }
}
