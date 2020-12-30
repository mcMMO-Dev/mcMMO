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
import com.gmail.nossr50.runnables.CheckDateTask;
import com.gmail.nossr50.runnables.SaveTimerTask;
import com.gmail.nossr50.runnables.backups.CleanBackupsTask;
import com.gmail.nossr50.runnables.commands.NotifySquelchReminderTask;
import com.gmail.nossr50.runnables.database.UserPurgeTask;
import com.gmail.nossr50.runnables.party.PartyAutoKickTask;
import com.gmail.nossr50.runnables.player.ClearRegisteredXPGainTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.runnables.player.PowerLevelUpdatingTask;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
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
import com.gmail.nossr50.util.skills.SmeltingTracker;
import com.gmail.nossr50.util.upgrade.UpgradeManager;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.google.common.base.Charsets;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
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
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static HolidayManager     holidayManager;
    private static UpgradeManager     upgradeManager;
    private static MaterialMapStore materialMapStore;
    private static PlayerLevelUtils playerLevelUtils;
    private static SmeltingTracker smeltingTracker;
    private static TransientMetadataTools transientMetadataTools;
    private static ChatManager chatManager;
    private static CommandManager commandManager; //ACF

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
    public final static String REPLANT_META_KEY = "mcMMO: Recently Replanted";
    public static final String FISH_HOOK_REF_METAKEY = "mcMMO: Fish Hook Tracker";
    public static final String DODGE_TRACKER        = "mcMMO: Dodge Tracker";
    public static final String CUSTOM_DAMAGE_METAKEY = "mcMMO: Custom Damage";
    public final static String travelingBlock      = "mcMMO: Traveling Block";
    public final static String blockMetadataKey    = "mcMMO: Piston Tracking";
    public final static String tntMetadataKey      = "mcMMO: Tracked TNT";
    public final static String customNameKey       = "mcMMO: Custom Name";
    public final static String customVisibleKey    = "mcMMO: Name Visibility";
    public final static String droppedItemKey      = "mcMMO: Tracked Item";
    public final static String infiniteArrowKey    = "mcMMO: Infinite Arrow";
    public final static String trackedArrow        = "mcMMO: Tracked Arrow";
    public final static String bowForceKey         = "mcMMO: Bow Force";
    public final static String arrowDistanceKey    = "mcMMO: Arrow Distance";
    public final static String BONUS_DROPS_METAKEY = "mcMMO: Double Drops";
    public final static String disarmedItemKey     = "mcMMO: Disarmed Item";
    public final static String playerDataKey       = "mcMMO: Player Data";
    public final static String databaseCommandKey  = "mcMMO: Processing Database Command";

    public static FixedMetadataValue metadataValue;

    public mcMMO() {
        p = this;
    }

    /**
     * Things to be run when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        try {
            //Platform Manager
            platformManager = new PlatformManager();

            getLogger().setFilter(new LogFilter(this));
            metadataValue = new FixedMetadataValue(this, true);

            PluginManager pluginManager = getServer().getPluginManager();
            healthBarPluginEnabled = pluginManager.getPlugin("HealthBar") != null;
            projectKorraEnabled = pluginManager.getPlugin("ProjectKorra") != null;

            upgradeManager = new UpgradeManager();

            setupFilePaths();

            modManager = new ModManager();

            //Init Material Maps
            materialMapStore = new MaterialMapStore();

            loadConfigFiles();

            if (!noErrorsInConfigFiles) {
                return;
            }

            //Store this value so other plugins can check it
            isRetroModeEnabled = Config.getInstance().getIsRetroMode();

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

            databaseManager = DatabaseManagerFactory.getDatabaseManager();

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
                holidayManager = new HolidayManager();

                for (Player player : getServer().getOnlinePlayers()) {
                    new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(mcMMO.p, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
                }

                debug("Version " + getDescription().getVersion() + " is enabled!");

                scheduleTasks();
                CommandRegistrationManager.registerCommands();

                placeStore = ChunkManagerFactory.getChunkManager(); // Get our ChunkletManager

                if (Config.getInstance().getPTPCommandWorldPermissions()) {
                    Permissions.generateWorldTeleportPermissions();
                }

                //Populate Ranked Skill Maps (DO THIS LAST)
                RankUtils.populateRanks();
            }

            //If anonymous statistics are enabled then use them
            Metrics metrics;

            if(Config.getInstance().getIsMetricsEnabled()) {
                metrics = new Metrics(this);
                metrics.addCustomChart(new Metrics.SimplePie("version", () -> getDescription().getVersion()));

                if(Config.getInstance().getIsRetroMode())
                    metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Standard"));
                else
                    metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Retro"));
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

        //Init player level values
        playerLevelUtils = new PlayerLevelUtils();

        //Init the blacklist
        worldBlacklist = new WorldBlacklist(this);

        //Init smelting tracker
        smeltingTracker = new SmeltingTracker();

        audiences = BukkitAudiences.create(this);

        transientMetadataTools = new TransientMetadataTools(this);

        chatManager = new ChatManager(this);

        commandManager = new CommandManager(this);
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
        try {
            UserManager.saveAll();      // Make sure to save player information if the server shuts down
            UserManager.clearAll();
            Alchemy.finishAllBrews();   // Finish all partially complete AlchemyBrewTasks to prevent vanilla brewing continuation on restart
            PartyManager.saveParties(); // Save our parties

            //TODO: Needed?
            if(Config.getInstance().getScoreboardsEnabled())
                ScoreboardManager.teardownAll();

            formulaManager.saveFormula();
            holidayManager.saveAnniversaryFiles();
            placeStore.cleanUp();       // Cleanup empty metadata stores
            placeStore.closeAll();
        }

        catch (Exception e) { e.printStackTrace(); }

        debug("Canceling all tasks...");
        getServer().getScheduler().cancelTasks(this); // This removes our tasks
        debug("Unregister all events...");
        HandlerList.unregisterAll(this); // Cancel event registrations

        if (Config.getInstance().getBackupsEnabled()) {
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

    public static HolidayManager getHolidayManager() {
        return holidayManager;
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

    public static CompatibilityManager getCompatibilityManager() {
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
        AdvancedConfig.getInstance();
        PotionConfig.getInstance();
        CoreSkillsConfig.getInstance();
        SoundConfig.getInstance();
        RankConfig.getInstance();

        new ChildConfig();

        List<Repairable> repairables = new ArrayList<>();

        if (Config.getInstance().getToolModsEnabled()) {
            new ToolConfigManager(this);
        }

        if (Config.getInstance().getArmorModsEnabled()) {
            new ArmorConfigManager(this);
        }

        if (Config.getInstance().getBlockModsEnabled()) {
            new BlockConfigManager(this);
        }

        if (Config.getInstance().getEntityModsEnabled()) {
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
            if (Config.getInstance().getChimaeraEnabled()) {
                getServer().addRecipe(ChimaeraWing.getChimaeraWingRecipe());
            }
        }, 40);
    }

    private void scheduleTasks() {
        // Periodic save timer (Saves every 10 minutes by default)
        long saveIntervalTicks = Config.getInstance().getSaveInterval() * 1200;
        new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);

        // Cleanup the backups folder
        new CleanBackupsTask().runTaskAsynchronously(mcMMO.p);

        // Bleed timer (Runs every 0.5 seconds)
        new BleedTimerTask().runTaskTimer(this, Misc.TICK_CONVERSION_FACTOR, (Misc.TICK_CONVERSION_FACTOR / 2));

        // Old & Powerless User remover
        long purgeIntervalTicks = Config.getInstance().getPurgeInterval() * 60L * 60L * Misc.TICK_CONVERSION_FACTOR;

        if (purgeIntervalTicks == 0) {
            new UserPurgeTask().runTaskLaterAsynchronously(this, 2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
        }
        else if (purgeIntervalTicks > 0) {
            new UserPurgeTask().runTaskTimerAsynchronously(this, purgeIntervalTicks, purgeIntervalTicks);
        }

        // Automatically remove old members from parties
        long kickIntervalTicks = Config.getInstance().getAutoPartyKickInterval() * 60L * 60L * Misc.TICK_CONVERSION_FACTOR;

        if (kickIntervalTicks == 0) {
            new PartyAutoKickTask().runTaskLater(this, 2 * Misc.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
        }
        else if (kickIntervalTicks > 0) {
            new PartyAutoKickTask().runTaskTimer(this, kickIntervalTicks, kickIntervalTicks);
        }

        // Update power level tag scoreboards
        new PowerLevelUpdatingTask().runTaskTimer(this, 2 * Misc.TICK_CONVERSION_FACTOR, 2 * Misc.TICK_CONVERSION_FACTOR);

        if (getHolidayManager().nearingAprilFirst()) {
            new CheckDateTask().runTaskTimer(this, 10L * Misc.TICK_CONVERSION_FACTOR, 60L * 60L * Misc.TICK_CONVERSION_FACTOR);
        }

        // Clear the registered XP data so players can earn XP again
        if (ExperienceConfig.getInstance().getDiminishedReturnsEnabled()) {
            new ClearRegisteredXPGainTask().runTaskTimer(this, 60, 60);
        }

        if(AdvancedConfig.getInstance().allowPlayerTips())
        {
            new NotifySquelchReminderTask().runTaskTimer(this, 60, ((20 * 60) * 60));
        }
    }

    private void checkModConfigs() {
        if (!Config.getInstance().getToolModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom tool config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Tool_Mods_Enabled to TRUE in config.yml.");
        }

        if (!Config.getInstance().getArmorModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom armor config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Armor_Mods_Enabled to TRUE in config.yml.");
        }

        if (!Config.getInstance().getBlockModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom block config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Block_Mods_Enabled to TRUE in config.yml.");
        }

        if (!Config.getInstance().getEntityModsEnabled()) {
            getLogger().warning("Cauldron implementation found, but the custom entity config for mcMMO is disabled!");
            getLogger().info("To enable, set Mods.Entity_Mods_Enabled to TRUE in config.yml.");
        }
    }

    public InputStreamReader getResourceAsReader(String fileName) {
        InputStream in = getResource(fileName);
        return in == null ? null : new InputStreamReader(in, Charsets.UTF_8);
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
}
