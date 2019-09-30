package com.gmail.nossr50;

import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.config.ConfigManager;
import com.gmail.nossr50.config.database.ConfigSectionCleaning;
import com.gmail.nossr50.config.database.ConfigSectionMySQL;
import com.gmail.nossr50.config.party.ConfigSectionPartyExperienceSharing;
import com.gmail.nossr50.config.party.ConfigSectionPartyLevel;
import com.gmail.nossr50.config.playerleveling.ConfigLeveling;
import com.gmail.nossr50.config.scoreboard.ConfigScoreboard;
import com.gmail.nossr50.core.DynamicSettingsManager;
import com.gmail.nossr50.core.MaterialMapStore;
import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.skills.subskills.acrobatics.Roll;
import com.gmail.nossr50.listeners.*;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.SaveTimerTask;
import com.gmail.nossr50.runnables.backups.CleanBackupFilesTask;
import com.gmail.nossr50.runnables.commands.NotifySquelchReminderTask;
import com.gmail.nossr50.runnables.database.UserPurgeTask;
import com.gmail.nossr50.runnables.party.PartyAutoKickTask;
import com.gmail.nossr50.runnables.player.ClearRegisteredXPGainTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.runnables.player.PowerLevelUpdatingTask;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManager;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManagerFactory;
import com.gmail.nossr50.util.commands.CommandRegistrationManager;
import com.gmail.nossr50.util.commands.CommandTools;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.PlayerLevelTools;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.RandomChanceTools;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.CombatTools;
import com.gmail.nossr50.util.skills.PerkUtils;
import com.gmail.nossr50.util.skills.RankTools;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import net.shatteredlands.shatt.backup.ZipLibrary;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mcMMO extends JavaPlugin {
    /* Managers */
    private ChunkManager placeStore;
    private ConfigManager configManager;
    private DynamicSettingsManager dynamicSettingsManager;
    private DatabaseManager databaseManager;
    private FormulaManager formulaManager;
    private NotificationManager notificationManager;
    private CommandRegistrationManager commandRegistrationManager;
//    private NBTManager nbtManager;
    private PartyManager partyManager;
    private LocaleManager localeManager;
    private ChatManager chatManager;
    private MobHealthBarManager mobHealthBarManager;
    private EventManager eventManager;
    private UserManager userManager;
    private ScoreboardManager scoreboardManager;
    private SoundManager soundManager;
    private HardcoreManager hardcoreManager;

    /* Not-Managers but my naming scheme sucks */
    private DatabaseManagerFactory databaseManagerFactory;
    private ChunkManagerFactory chunkManagerFactory;
    private CommandTools commandTools;
    private SkillTools skillTools; //TODO: Remove once a new skill system is in place
    private BlockTools blockTools;
    private CombatTools combatTools; //TODO: Rewrite this garbo
    private TextComponentFactory textComponentFactory;
    private PlayerLevelTools playerLevelTools;
    private MaterialMapStore materialMapStore;
    private RandomChanceTools randomChanceTools;
    private RankTools rankTools;
    private ItemTools itemTools;
    private PermissionTools permissionTools;
    private WorldGuardUtils worldGuardUtils;
    private MessageOfTheDayUtils messageOfTheDayUtils;
    private MiscTools miscTools;
    private ZipLibrary zipLibrary;
    private PerkUtils perkUtils;

    /* Never-Ending tasks */
    private BleedTimerTask bleedTimerTask;

    /* File Paths */
    private String mainDirectory;
    private String localesDirectory;
    private String flatFileDirectory;
    private String usersFile;
    private String modDirectory;

    /* Plugin Checks */
    private boolean healthBarPluginEnabled;
    // API checks
    private boolean serverAPIOutdated = false;
    // XP Event Check
    private boolean xpEventEnabled;

    /**
     * Things to be run when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        try {
            getLogger().setFilter(new LogFilter(this));

            //TODO: Disgusting...
            MetadataConstants.metadataValue = new FixedMetadataValue(this, true);

            PluginManager pluginManager = getServer().getPluginManager();
            healthBarPluginEnabled = pluginManager.getPlugin("HealthBar") != null;

            //Init Permission Tools
            permissionTools = new PermissionTools(this);

            //upgradeManager = new UpgradeManager();

            setupFilePaths();

            //Init config manager etc
            loadConfigFiles();

            //Init Locale Manager
            localeManager = new LocaleManager(this);

            //Init Skill Tools
            skillTools = new SkillTools(this);

            //Init Item Tools
            itemTools = new ItemTools(this);

            //Misc Tools Init
            miscTools = new MiscTools(this);

            //Init DST
            registerDynamicSettings(); //Do this after configs are loaded

            //Init TextComponentFactory
            textComponentFactory = new TextComponentFactory(this);

            if (healthBarPluginEnabled) {
                getLogger().info("HealthBar plugin found, mcMMO's healthbars are automatically disabled.");
            }

            if (pluginManager.getPlugin("NoCheatPlus") != null && pluginManager.getPlugin("CompatNoCheatPlus") == null) {
                getLogger().warning("NoCheatPlus plugin found, but CompatNoCheatPlus was not found!");
                getLogger().warning("mcMMO will not work properly alongside NoCheatPlus without CompatNoCheatPlus");
            }

            //TODO: Strange design...
            databaseManagerFactory = new DatabaseManagerFactory(this);
            databaseManager = getDatabaseManagerFactory().getDatabaseManager();

            //Check for the newer API and tell them what to do if its missing
            CompatibilityCheck.checkForOutdatedAPI(this, serverAPIOutdated, getServerSoftwareStr());

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
                initParties();

                formulaManager = new FormulaManager(this);

                for (Player player : getServer().getOnlinePlayers()) {
                    new PlayerProfileLoadingTask(this, player).runTaskLaterAsynchronously(this, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
                }

                debug("Version " + getDescription().getVersion() + " is enabled!");

                scheduleTasks();
                commandRegistrationManager = new CommandRegistrationManager(this);
                commandRegistrationManager.registerCommands();

//                nbtManager = new NBTManager();

                //Init Chunk Manager Factory
                chunkManagerFactory = new ChunkManagerFactory(this);
                placeStore = chunkManagerFactory.getChunkManager(); // Get our ChunkletManager

                if (getConfigManager().getConfigParty().getPTP().isPtpWorldBasedPermissions()) {
                    getPermissionTools().generateWorldTeleportPermissions();
                }

                //Init Rank Tools
                rankTools = new RankTools(this);

                //Populate Ranked Skill Maps (DO THIS LAST)
                rankTools.populateRanks();
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

        //TODO: Put all manager init somewhere nice and tidy

        //Init Material Maps
        materialMapStore = new MaterialMapStore();

        //Init player level values
        playerLevelTools = new PlayerLevelTools(this);

        //Init Notification Manager
        notificationManager = new NotificationManager(this);

        //Init Chat Manager
        chatManager = new ChatManager(this);

        //Init Mob Health Bar Manager
        mobHealthBarManager = new MobHealthBarManager(this);

        //Init Event Manager
        eventManager = new EventManager(this);

        //Init Command Tools
        //TODO: Better name?
        commandTools = new CommandTools(this);

        //Init User Manager
        userManager = new UserManager(this);

        //Init Scoreboard Manager
        scoreboardManager = new ScoreboardManager(this);

        //Init Combat Tools
        combatTools = new CombatTools(this);

        //Init Random Chance Tools
        randomChanceTools = new RandomChanceTools(this);

        //Init Block Tools
        blockTools = new BlockTools(this);

        //Init MOTD Utils
        messageOfTheDayUtils = new MessageOfTheDayUtils(this);

        //Init Sound Manager
        soundManager = new SoundManager(this);

        //Init HardcoreManager
        hardcoreManager = new HardcoreManager(this);

        //Init PerkUtils
        perkUtils = new PerkUtils(this);
    }

    @Override
    public void onLoad()
    {
        if(getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuardUtils = new WorldGuardUtils(); //Init WGU

            if(worldGuardUtils.isWorldGuardLoaded()) {
                //Register flags
                System.out.println("[mcMMO - Registering World Guard Flags...]");
                worldGuardUtils.getWorldGuardManager().registerFlags();
            }
        }
    }

    /**
     * Things to be run when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        try {
            userManager.saveAll();      // Make sure to save player information if the server shuts down
            userManager.clearAll();
            partyManager.saveParties(); // Save our parties

            //TODO: Needed?
            if (getScoreboardSettings().getScoreboardsEnabled())
                scoreboardManager.teardownAll();

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
                zipLibrary = new ZipLibrary(this);
                zipLibrary.mcMMOBackup();
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

    private void initParties() {
        partyManager = new PartyManager(this);

        if (getConfigManager().getConfigParty().isPartySystemEnabled())
            getPartyManager().loadParties();
    }

    public PlayerLevelTools getPlayerLevelTools() {
        return playerLevelTools;
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

    public MaterialMapStore getMaterialMapStore() {
        return materialMapStore;
    }

    public String getMainDirectory() {
        return mainDirectory;
    }

    public String getFlatFileDirectory() {
        return flatFileDirectory;
    }

    public String getUsersFilePath() {
        return usersFile;
    }

    public String getModDirectory() {
        return modDirectory;
    }

    public FormulaManager getFormulaManager() {
        return formulaManager;
    }

    public ChunkManager getPlaceStore() {
        return placeStore;
    }

//    public RepairableManager getRepairableManager() {
//        return dynamicSettingsManager.getRepairableManager();
//    }

    public SalvageableManager getSalvageableManager() {
        return dynamicSettingsManager.getSalvageableManager();
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

//    public NBTManager getNbtManager() {
//        return nbtManager;
//    }

    @Deprecated
    public void setDatabaseManager(DatabaseManager newDatabaseManager) {
        databaseManager = newDatabaseManager;
    }

    /**
     * Returns settings for MySQL from the users config
     *
     * @return settings for MySQL from the users config
     */
    public ConfigSectionMySQL getMySQLConfigSettings() {
        return configManager.getConfigDatabase().getConfigSectionMySQL();
    }

    /**
     * Returns settings for Player Leveling from the users config
     *
     * @return settings for Player Leveling from the users config
     */
    public ConfigLeveling getPlayerLevelingSettings() {
        return configManager.getConfigLeveling();
    }

    /**
     * Returns settings for Database cleaning from the users config
     *
     * @return settings for Database cleaning from the users config
     */
    public ConfigSectionCleaning getDatabaseCleaningSettings() {
        return configManager.getConfigDatabase().getConfigSectionCleaning();
    }

    /**
     * Returns settings for Party XP sharing from the users config
     *
     * @return settings for the Party XP sharing from the users config
     */
    public ConfigSectionPartyExperienceSharing getPartyXPShareSettings() {
        return configManager.getConfigParty().getPartyXP().getPartyExperienceSharing();
    }

    /**
     * Returns settings for Party Leveling from the users config
     *
     * @return settings for the Party Leveling from the users config
     */
    public ConfigSectionPartyLevel getPartyLevelSettings() {
        return configManager.getConfigParty().getPartyXP().getPartyLevel();
    }

    /**
     * Returns settings for Scoreboards from the users config
     *
     * @return settings for Scoreboards from the users config
     */
    public ConfigScoreboard getScoreboardSettings() {
        return configManager.getConfigScoreboard();
    }

    public boolean isHealthBarPluginEnabled() {
        return healthBarPluginEnabled;
    }

    /**
     * Checks if this plugin is using retro mode
     * Retro mode is a 0-1000 skill system
     * Standard mode is scaled for 1-100
     *
     * @return true if retro mode is enabled
     */
    public boolean isRetroModeEnabled() {
        return configManager.isRetroMode();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * The directory in which override locales are kept
     *
     * @return the override locale directory
     */
    public String getLocalesDirectory() {
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

    /**
     * Effectively this reloads mcMMO, making it go through the disabled, load, and enable step
     * Used with the new mcmmo-reload command
     */
    public void reload() {
        onDisable();
        onLoad();
        onEnable();
    }

    private void registerDynamicSettings() {
        dynamicSettingsManager = new DynamicSettingsManager(this);
    }

    private void loadConfigFiles() {
        configManager = new ConfigManager(this);
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

        if (configManager.getConfigCoreSkills().isAcrobaticsEnabled()) {
            InteractionManager.initMaps(); //Init maps

            System.out.println("[mcMMO]" + " enabling Acrobatics Skills");

            //TODO: Should do this differently
            if (configManager.getConfigCoreSkills().isRollEnabled()) {
                InteractionManager.registerSubSkill(new Roll(this));
            }
        }
    }

    private void registerCustomRecipes() {
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (configManager.getConfigItems().isChimaeraWingEnabled()) {
                Recipe recipe = getChimaeraWingRecipe();

                if(!getSkillTools().hasRecipeBeenRegistered(recipe))
                    getServer().addRecipe(getChimaeraWingRecipe());
            }
        }, 40);
    }

    private void scheduleTasks() {
        // Periodic save timer (Saves every 10 minutes by default)
        long saveIntervalTicks = Math.max(1200, (getConfigManager().getConfigDatabase().getConfigSectionDatabaseGeneral().getSaveIntervalMinutes() * (20 * 60)));
        new SaveTimerTask(this).runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);

        // Cleanup the backups folder
        new CleanBackupFilesTask(this).runTaskAsynchronously(this);

        // Bleed timer (Runs every 0.5 seconds)
        bleedTimerTask = new BleedTimerTask(this);
        bleedTimerTask.runTaskTimer(this, miscTools.TICK_CONVERSION_FACTOR, (miscTools.TICK_CONVERSION_FACTOR / 2));

        // Old & Powerless User remover
        long purgeIntervalTicks = getConfigManager().getConfigDatabase().getConfigSectionCleaning().getPurgeInterval() * 60L * 60L * miscTools.TICK_CONVERSION_FACTOR;

        if (getDatabaseCleaningSettings().isOnlyPurgeAtStartup()) {
            new UserPurgeTask(this).runTaskLaterAsynchronously(this, 2 * miscTools.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
        } else if (purgeIntervalTicks > 0) {
            new UserPurgeTask(this).runTaskTimerAsynchronously(this, purgeIntervalTicks, purgeIntervalTicks);
        }

        //Party System Stuff
        if (configManager.getConfigParty().isPartySystemEnabled()) {
            // Automatically remove old members from parties
            long kickIntervalTicks = getConfigManager().getConfigParty().getPartyCleanup().getPartyAutoKickHoursInterval() * 60L * 60L * miscTools.TICK_CONVERSION_FACTOR;

            if (kickIntervalTicks == 0) {
                new PartyAutoKickTask(this).runTaskLater(this, 2 * miscTools.TICK_CONVERSION_FACTOR); // Start 2 seconds after startup.
            } else if (kickIntervalTicks > 0) {
                new PartyAutoKickTask(this).runTaskTimer(this, kickIntervalTicks, kickIntervalTicks);
            }
        }

        // Update power level tag scoreboards
        new PowerLevelUpdatingTask(this).runTaskTimer(this, 2 * miscTools.TICK_CONVERSION_FACTOR, 2 * miscTools.TICK_CONVERSION_FACTOR);

        // Clear the registered XP data so players can earn XP again
        if (getConfigManager().getConfigLeveling().getConfigLevelingDiminishedReturns().isDiminishedReturnsEnabled()) {
            new ClearRegisteredXPGainTask(this).runTaskTimer(this, 60, 60);
        }

        if (configManager.getConfigNotifications().getConfigNotificationGeneral().isPlayerTips()) {
            new NotifySquelchReminderTask(this).runTaskTimer(this, 60, ((20 * 60) * 60));
        }
    }

    //TODO: Add this stuff to DSM, this location is temporary
    private ShapelessRecipe getChimaeraWingRecipe() {
        Material ingredient = Material.matchMaterial(configManager.getConfigItems().getChimaeraWingRecipeMats());

        if(ingredient == null)
            ingredient = Material.FEATHER;

        int amount = configManager.getConfigItems().getChimaeraWingUseCost();

        ShapelessRecipe chimaeraWing = new ShapelessRecipe(new NamespacedKey(this, "Chimaera"), getChimaeraWing());
        chimaeraWing.addIngredient(amount, ingredient);
        return chimaeraWing;
    }

    //TODO: Add this stuff to DSM, this location is temporary
    public ItemStack getChimaeraWing() {
        Material ingredient = Material.matchMaterial(configManager.getConfigItems().getChimaeraWingRecipeMats());

        if(ingredient == null)
            ingredient = Material.FEATHER;

        //TODO: Make it so Chimaera wing amounts made is customizeable
        ItemStack itemStack = new ItemStack(ingredient, 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + localeManager.getString("Item.ChimaeraWing.Name"));

        List<String> itemLore = new ArrayList<>();
        itemLore.add("mcMMO Item");
        itemLore.add(localeManager.getString("Item.ChimaeraWing.Lore"));
        itemMeta.setLore(itemLore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public DynamicSettingsManager getDynamicSettingsManager() {
        return dynamicSettingsManager;
    }

    private enum ServerSoftwareType {
        PAPER,
        SPIGOT,
        CRAFTBUKKIT
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public WorldGuardManager getWorldGuardManager() {
        return worldGuardUtils.getWorldGuardManager();
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public MobHealthBarManager getMobHealthBarManager() {
        return mobHealthBarManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public CommandTools getCommandTools() {
        return commandTools;
    }

    public DatabaseManagerFactory getDatabaseManagerFactory() {
        return databaseManagerFactory;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public TextComponentFactory getTextComponentFactory() {
        return textComponentFactory;
    }

    public BleedTimerTask getBleedTimerTask() {
        return bleedTimerTask;
    }

    public SkillTools getSkillTools() {
        return skillTools;
    }

    public CombatTools getCombatTools() {
        return combatTools;
    }

    public RandomChanceTools getRandomChanceTools() {
        return randomChanceTools;
    }

    public RankTools getRankTools() {
        return rankTools;
    }

    public BlockTools getBlockTools() {
        return blockTools;
    }

    public ItemTools getItemTools() {
        return itemTools;
    }

    public PermissionTools getPermissionTools() {
        return permissionTools;
    }

    public WorldGuardUtils getWorldGuardUtils() {
        return worldGuardUtils;
    }

    public MessageOfTheDayUtils getMessageOfTheDayUtils() {
        return messageOfTheDayUtils;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public ChunkManagerFactory getChunkManagerFactory() {
        return chunkManagerFactory;
    }

    public MiscTools getMiscTools() {
        return miscTools;
    }

    public HardcoreManager getHardcoreManager() {
        return hardcoreManager;
    }

    public PerkUtils getPerkUtils() {
        return perkUtils;
    }
}
