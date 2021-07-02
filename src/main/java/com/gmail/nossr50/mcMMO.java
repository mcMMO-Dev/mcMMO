package com.gmail.nossr50;

import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.commands.CommandManager;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.mods.ArmorConfigManager;
import com.gmail.nossr50.config.mods.BlockConfigManager;
import com.gmail.nossr50.config.mods.EntityConfigManager;
import com.gmail.nossr50.config.mods.ToolConfigManager;
import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.config.skills.repair.RepairConfigManager;
import com.gmail.nossr50.config.skills.salvage.SalvageConfigManager;
import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
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
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.child.ChildConfig;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableManager;
import com.gmail.nossr50.skills.repair.repairables.SimpleRepairableManager;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableManager;
import com.gmail.nossr50.skills.salvage.salvageables.SimpleSalvageableManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.blockmeta.ChunkManager;
import com.gmail.nossr50.util.blockmeta.ChunkManagerFactory;
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
import com.gmail.nossr50.util.skills.SmeltingTracker;
import com.gmail.nossr50.util.upgrade.UpgradeManager;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.shatteredlands.shatt.backup.ZipLibrary;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class mcMMO extends JavaPlugin {
    /* Managers */
    private static PlatformManager platformManager;
    private static ChunkManager       placeStore;
    private static RepairableManager  repairableManager;
    private static SalvageableManager salvageableManager;
    private static ModManager         modManager;
    private static DatabaseManager    databaseManager;
    private static FormulaManager     formulaManager;
    private static UpgradeManager     upgradeManager;
    private static MaterialMapStore materialMapStore;
    private static PlayerLevelUtils playerLevelUtils;
    private static SmeltingTracker smeltingTracker;
    private static TransientMetadataTools transientMetadataTools;
    private static ChatManager chatManager;
    private static CommandManager commandManager; //ACF
    private static TransientEntityTracker transientEntityTracker;

    private @NotNull SkillTools skillTools;

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

    /* Metadata Values */
    public static final String REPLANT_META_KEY      = "mcMMO: Recently Replanted";
    public static final String EXPLOSION_FROM_RUPTURE = "mcMMO: Rupture Explosion";
    public static final String RUPTURE_META_KEY      = "mcMMO: RuptureTask";
    public static final String FISH_HOOK_REF_METAKEY = "mcMMO: Fish Hook Tracker";
    public static final String DODGE_TRACKER         = "mcMMO: Dodge Tracker";
    public static final String CUSTOM_DAMAGE_METAKEY = "mcMMO: Custom Damage";
    public static final String travelingBlock        = "mcMMO: Traveling Block";
    public static final String blockMetadataKey      = "mcMMO: Piston Tracking";
    public static final String tntMetadataKey        = "mcMMO: Tracked TNT";
    public static final String customNameKey         = "mcMMO: Custom Name";
    public static final String customVisibleKey      = "mcMMO: Name Visibility";
    public static final String droppedItemKey        = "mcMMO: Tracked Item";
    public static final String infiniteArrowKey      = "mcMMO: Infinite Arrow";
    public static final String trackedArrow          = "mcMMO: Tracked Arrow";
    public static final String bowForceKey           = "mcMMO: Bow Force";
    public static final String arrowDistanceKey      = "mcMMO: Arrow Distance";
    public static final String BONUS_DROPS_METAKEY   = "mcMMO: Double Drops";
    public static final String disarmedItemKey       = "mcMMO: Disarmed Item";
    public static final String playerDataKey         = "mcMMO: Player Data";
    public static final String databaseCommandKey    = "mcMMO: Processing Database Command";

    public static FixedMetadataValue metadataValue;
    private long purgeTime = 2630000000L;

    private GeneralConfig generalConfig;
    private AdvancedConfig advancedConfig;
//    private RepairConfig repairConfig;
//    private SalvageConfig salvageConfig;
//    private PersistentDataConfig persistentDataConfig;
//    private ChatConfig chatConfig;
//    private CoreSkillsConfig coreSkillsConfig;
//    private RankConfig rankConfig;
//    private TreasureConfig treasureConfig;
//    private FishingTreasureConfig fishingTreasureConfig;
//    private SoundConfig soundConfig;

    public mcMMO() {
        p = this;
    }


    protected mcMMO(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }


    /**
     * Things to be run when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        try {
            setupFilePaths();
            generalConfig = new GeneralConfig(getDataFolder()); //Load before skillTools
            skillTools = new SkillTools(this); //Load after general config

            //Init configs
            advancedConfig = new AdvancedConfig(getDataFolder());

            //Store this value so other plugins can check it
            isRetroModeEnabled = generalConfig.getIsRetroMode();

            //Platform Manager
            platformManager = new PlatformManager();

            //Filter out any debug messages (if debug/verbose logging is not enabled)
            getLogger().setFilter(new LogFilter(this));

            metadataValue = new FixedMetadataValue(this, true);

            PluginManager pluginManager = getServer().getPluginManager();
            healthBarPluginEnabled = pluginManager.getPlugin("HealthBar") != null;
            projectKorraEnabled = pluginManager.getPlugin("ProjectKorra") != null;

            upgradeManager = new UpgradeManager();


            modManager = new ModManager();

            //Init Material Maps
            materialMapStore = new MaterialMapStore();

            loadConfigFiles();

            if (!noErrorsInConfigFiles) {
                return;
            }

            if (getServer().getName().equals("Cauldron") || getServer().getName().equals("MCPC+")) {
                checkModConfigs();
            }

            if(projectKorraEnabled) {
                getLogger().info("ProjectKorra was detected, this can cause some issues with weakness potions and combat skills for mcMMO");
            }

            if (healthBarPluginEnabled) {
                getLogger().info("HealthBar plugin found, mcMMO's healthbars are automatically disabled.");
            }

            if (pluginManager.getPlugin("NoCheatPlus") != null && pluginManager.getPlugin("CompatNoCheatPlus") == null) {
                getLogger().warning("NoCheatPlus plugin found, but CompatNoCheatPlus was not found!");
                getLogger().warning("mcMMO will not work properly alongside NoCheatPlus without CompatNoCheatPlus");
            }

            // One month in milliseconds
            this.purgeTime = 2630000000L * generalConfig.getOldUsersCutoff();

            databaseManager = DatabaseManagerFactory.getDatabaseManager(mcMMO.getUsersFilePath(), getLogger(), purgeTime, mcMMO.p.getAdvancedConfig().getStartingLevel());

            //Check for the newer API and tell them what to do if its missing
            checkForOutdatedAPI();

            if(serverAPIOutdated)
            {
                Bukkit
                        .getScheduler()
                        .scheduleSyncRepeatingTask(this,
                                () -> getLogger().severe("You are running an outdated version of "+platformManager.getServerSoftware()+", mcMMO will not work unless you update to a newer version!"),
                        20, 20*60*30);

                if(platformManager.getServerSoftware() == ServerSoftwareType.CRAFT_BUKKIT)
                {
                    Bukkit.getScheduler()
                            .scheduleSyncRepeatingTask(this,
                                    () -> getLogger().severe("We have detected you are using incompatible server software, our best guess is that you are using CraftBukkit. mcMMO requires Spigot or Paper, if you are not using CraftBukkit, you will still need to update your custom server software before mcMMO will work."),
                    20, 20*60*30);
                }
            } else {
                registerEvents();
                registerCoreSkills();
                registerCustomRecipes();

                PartyManager.loadParties();

                formulaManager = new FormulaManager();

                for (Player player : getServer().getOnlinePlayers()) {
                    new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(mcMMO.p, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
                }

                debug("Version " + getDescription().getVersion() + " is enabled!");

                scheduleTasks();
                CommandRegistrationManager.registerCommands();

                placeStore = ChunkManagerFactory.getChunkManager(); // Get our ChunkletManager

                if (generalConfig.getPTPCommandWorldPermissions()) {
                    Permissions.generateWorldTeleportPermissions();
                }

                //Populate Ranked Skill Maps (DO THIS LAST)
                RankUtils.populateRanks();
            }

            //If anonymous statistics are enabled then use them
            Metrics metrics;

            if(generalConfig.getIsMetricsEnabled()) {
                metrics = new Metrics(this, 3894);
                metrics.addCustomChart(new SimplePie("version", () -> getDescription().getVersion()));

                if(generalConfig.getIsRetroMode())
                    metrics.addCustomChart(new SimplePie("leveling_system", () -> "Retro"));
                else
                    metrics.addCustomChart(new SimplePie("leveling_system", () -> "Standard"));
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

            //Fixes #4438 - Don't initialize things if we are going to disable mcMMO anyway
            return;
        }

        //Init player level values
        playerLevelUtils = new PlayerLevelUtils();

        //Init the blacklist
        worldBlacklist = new WorldBlacklist(this);

        //Init smelting tracker
        smeltingTracker = new SmeltingTracker();

        //Set up Adventure's audiences
        audiences = BukkitAudiences.create(this);

        transientMetadataTools = new TransientMetadataTools(this);

        chatManager = new ChatManager(this);

        commandManager = new CommandManager(this);

        transientEntityTracker = new TransientEntityTracker();
        setServerShutdown(false); //Reset flag, used to make decisions about async saves
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
            getLogger().severe("You are running an older version of " + software + " that is not compatible with mcMMO, update your server software!");
        }
    }

    @Override
    public void onLoad()
    {
        if(getServer().getPluginManager().getPlugin("WorldGuard") != null) {
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
        mcMMO.p.getLogger().info("Server shutdown has been executed, saving and cleaning up data...");

        try {
            UserManager.saveAll();      // Make sure to save player information if the server shuts down
            UserManager.clearAll();
            Alchemy.finishAllBrews();   // Finish all partially complete AlchemyBrewTasks to prevent vanilla brewing continuation on restart
            PartyManager.saveParties(); // Save our parties

            //TODO: Needed?
            if(generalConfig.getScoreboardsEnabled())
                ScoreboardManager.teardownAll();

            formulaManager.saveFormula();
            placeStore.closeAll();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (generalConfig.getBackupsEnabled()) {
            // Remove other tasks BEFORE starting the Backup, or we just cancel it straight away.
            try {
                ZipLibrary.mcMMOBackup();
            }
            catch (IOException e) {
                getLogger().severe(e.toString());
            }
            catch(NoClassDefFoundError e) {
                getLogger().severe("Backup class not found!");
                getLogger().info("Please do not replace the mcMMO jar while the server is running."); 
            }
            catch (Throwable e) {
                getLogger().severe(e.toString());
            }
        }

        debug("Canceling all tasks...");
        getServer().getScheduler().cancelTasks(this); // This removes our tasks
        debug("Unregister all events...");
        HandlerList.unregisterAll(this); // Cancel event registrations

        databaseManager.onDisable();
        debug("Was disabled."); // How informative!
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

    public static SalvageableManager getSalvageableManager() {
        return salvageableManager;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static ModManager getModManager() {
        return modManager;
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
        File localesDirectoryPath = new File(localesDirectory);
        localesDirectoryPath.mkdirs();
    }

    private void loadConfigFiles() {
        // Force the loading of config files
        TreasureConfig.getInstance();
        FishingTreasureConfig.getInstance();
        HiddenConfig.getInstance();
        mcMMO.p.getAdvancedConfig();
        PotionConfig.getInstance();
        CoreSkillsConfig.getInstance();
        SoundConfig.getInstance();
        RankConfig.getInstance();

        new ChildConfig();

        List<Repairable> repairables = new ArrayList<>();

        if (generalConfig.getToolModsEnabled()) {
            new ToolConfigManager(this);
        }

        if (generalConfig.getArmorModsEnabled()) {
            new ArmorConfigManager(this);
        }

        if (generalConfig.getBlockModsEnabled()) {
            new BlockConfigManager(this);
        }

        if (generalConfig.getEntityModsEnabled()) {
            new EntityConfigManager(this);
        }

        // Load repair configs, make manager, and register them at this time
        repairables.addAll(new RepairConfigManager(this).getLoadedRepairables());
        repairables.addAll(modManager.getLoadedRepairables());
        repairableManager = new SimpleRepairableManager(repairables.size());
        repairableManager.registerRepairables(repairables);

        // Load salvage configs, make manager and register them at this time
        SalvageConfigManager sManager = new SalvageConfigManager(this);
        List<Salvageable> salvageables = new ArrayList<>(sManager.getLoadedSalvageables());
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
     * Registers core skills
     * This enables the skills in the new skill system
     */
    private void registerCoreSkills() {
        /*
         * Acrobatics skills
         */

        InteractionManager.initMaps(); //Init maps

        if(CoreSkillsConfig.getInstance().isPrimarySkillEnabled(PrimarySkillType.ACROBATICS))
        {
            getLogger().info("Enabling Acrobatics Skills");

            //TODO: Should do this differently
            Roll roll = new Roll();
            CoreSkillsConfig.getInstance().isSkillEnabled(roll);
            InteractionManager.registerSubSkill(new Roll());
        }
    }

    private void registerCustomRecipes() {
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
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

        new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);

        // Cleanup the backups folder
        new CleanBackupsTask().runTaskAsynchronously(mcMMO.p);

        // Old & Powerless User remover
        long purgeIntervalTicks = generalConfig.getPurgeInterval() * 60L * 60L * Misc.TICK_CONVERSION_FACTOR;

        if (purgeIntervalTicks == 0) {
            new UserPurgeTask().runTaskLaterAsynchronously(this, 2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
        }
        else if (purgeIntervalTicks > 0) {
            new UserPurgeTask().runTaskTimerAsynchronously(this, purgeIntervalTicks, purgeIntervalTicks);
        }

        // Automatically remove old members from parties
        long kickIntervalTicks = generalConfig.getAutoPartyKickInterval() * 60L * 60L * Misc.TICK_CONVERSION_FACTOR;

        if (kickIntervalTicks == 0) {
            new PartyAutoKickTask().runTaskLater(this, 2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
        }
        else if (kickIntervalTicks > 0) {
            new PartyAutoKickTask().runTaskTimer(this, kickIntervalTicks, kickIntervalTicks);
        }

        // Update power level tag scoreboards
        new PowerLevelUpdatingTask().runTaskTimer(this, 2 * Misc.TICK_CONVERSION_FACTOR, 2 * Misc.TICK_CONVERSION_FACTOR);

        // Clear the registered XP data so players can earn XP again
        if (ExperienceConfig.getInstance().getDiminishedReturnsEnabled()) {
            new ClearRegisteredXPGainTask().runTaskTimer(this, 60, 60);
        }

        if(mcMMO.p.getAdvancedConfig().allowPlayerTips())
        {
            new NotifySquelchReminderTask().runTaskTimer(this, 60, ((20 * 60) * 60));
        }
    }

    private void checkModConfigs() {
        if (!generalConfig.getToolModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom tool config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Tool_Mods_Enabled to TRUE in config.yml.");
        }

        if (!generalConfig.getArmorModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom armor config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Armor_Mods_Enabled to TRUE in config.yml.");
        }

        if (!generalConfig.getBlockModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom block config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Block_Mods_Enabled to TRUE in config.yml.");
        }

        if (!generalConfig.getEntityModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom entity config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Entity_Mods_Enabled to TRUE in config.yml.");
        }
    }

    public @Nullable InputStreamReader getResourceAsReader(@NotNull String fileName) {
        InputStream in = getResource(fileName);
        return in == null ? null : new InputStreamReader(in, StandardCharsets.UTF_8);
    }

    /**
     * Checks if this plugin is using retro mode
     * Retro mode is a 0-1000 skill system
     * Standard mode is scaled for 1-100
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

    public static SmeltingTracker getSmeltingTracker() {
        return smeltingTracker;
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
}
