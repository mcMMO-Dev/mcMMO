package com.gmail.nossr50;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.shatteredlands.shatt.backup.ZipLibrary;

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
import com.gmail.nossr50.config.spout.SpoutConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.LeaderboardManager;
import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.listeners.EntityListener;
import com.gmail.nossr50.listeners.InventoryListener;
import com.gmail.nossr50.listeners.PlayerListener;
import com.gmail.nossr50.listeners.WorldListener;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.metrics.MetricsManager;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.SaveTimerTask;
import com.gmail.nossr50.runnables.database.UserPurgeTask;
import com.gmail.nossr50.runnables.party.PartyAutoKickTask;
import com.gmail.nossr50.runnables.party.PartyLoaderTask;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.runnables.skills.SkillMonitorTask;
import com.gmail.nossr50.skills.child.ChildConfig;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.skills.repair.RepairableManager;
import com.gmail.nossr50.skills.repair.RepairableManagerFactory;
import com.gmail.nossr50.skills.repair.config.RepairConfigManager;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.LogFilter;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.UpdateChecker;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManager;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManagerFactory;
import com.gmail.nossr50.util.commands.CommandRegistrationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.spout.SpoutUtils;

public class mcMMO extends JavaPlugin {
    private final PlayerListener    playerListener    = new PlayerListener(this);
    private final BlockListener     blockListener     = new BlockListener(this);
    private final EntityListener    entityListener    = new EntityListener(this);
    private final InventoryListener inventoryListener = new InventoryListener(this);
    private final WorldListener     worldListener     = new WorldListener();

    private HashMap<Integer, String>    tntTracker     = new HashMap<Integer, String>();

    public static mcMMO p;

    public static ChunkManager  placeStore;
    public static RepairableManager repairableManager;

    // Jar Stuff
    public static File mcmmo;

    // File Paths
    private static String mainDirectory;
    private static String flatFileDirectory;
    private static String usersFile;
    private static String modDirectory;

    // Update Check
    public boolean updateAvailable;

    // Spout Check
    public static boolean spoutEnabled = false;

    // XP Event Check
    private boolean xpEventEnabled = false;

    // Metadata Values
    public static FixedMetadataValue metadataValue;
    public final static String entityMetadataKey = "mcMMO: Spawned Entity";
    public final static String blockMetadataKey  = "mcMMO: Piston Tracking";
    public final static String furnaceMetadataKey  = "mcMMO: Tracked Furnace";

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
            setupSpout();
            loadConfigFiles();

            if (!Config.getInstance().getUseMySQL()) {
                UserManager.loadUsers();
            }

            registerEvents();
            registerCustomRecipes();

            // Setup the leader boards
            if (Config.getInstance().getUseMySQL()) {
                // TODO: Why do we have to check for a connection that hasn't be made yet?
                DatabaseManager.checkConnected();
                DatabaseManager.createStructure();
            }
            else {
                LeaderboardManager.updateLeaderboards();
            }

            for (Player player : getServer().getOnlinePlayers()) {
                UserManager.addUser(player); // In case of reload add all users back into PlayerProfile
            }

            getLogger().info("Version " + getDescription().getVersion() + " is enabled!");

            scheduleTasks();
            registerCommands();

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
            UserManager.saveAll(); // Make sure to save player information if the server shuts down
            PartyManager.saveParties();
            placeStore.saveAll(); // Save our metadata
            placeStore.cleanUp(); // Cleanup empty metadata stores
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

        getLogger().info("Was disabled."); // How informative!
    }

    /**
     * Add a set of values to the TNT tracker.
     *
     * @param tntID The EntityID of the TNT
     * @param playerName The name of the detonating player
     */
    public void addToTNTTracker(int tntID, String playerName) {
        tntTracker.put(tntID, playerName);
    }

    /**
     * Check to see if a given TNT Entity is tracked.
     *
     * @param tntID The EntityID of the TNT
     * @return true if the TNT is being tracked, false otherwise
     */
    public boolean tntIsTracked(int tntID) {
        return tntTracker.containsKey(tntID);
    }

    /**
     * Get the player who detonated the TNT.
     *
     * @param tntID The EntityID of the TNT
     * @return the Player who detonated it
     */
    public Player getTNTPlayer(int tntID) {
        return getServer().getPlayer(tntTracker.get(tntID));
    }

    /**
     * Remove TNT from the tracker after it explodes.
     *
     * @param tntID The EntityID of the TNT
     */
    public void removeFromTNTTracker(int tntID) {
        tntTracker.remove(tntID);
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
        if (Config.getInstance().getUpdateCheckEnabled()) {
            try {
                updateAvailable = UpdateChecker.updateAvailable();
            }
            catch (Exception e) {
                updateAvailable = false;
            }

            if (updateAvailable) {
                getLogger().info(LocaleLoader.getString("UpdateChecker.outdated"));
                getLogger().info(LocaleLoader.getString("UpdateChecker.newavailable"));
            }
        }
    }

    private void loadConfigFiles() {
        // Force the loading of config files
        Config configInstance = Config.getInstance();
        TreasureConfig.getInstance();
        HiddenConfig.getInstance();
        AdvancedConfig.getInstance();
        new ChildConfig();

        List<Repairable> repairables = new ArrayList<Repairable>();

        if (configInstance.getToolModsEnabled()) {
            repairables.addAll(CustomToolConfig.getInstance().getLoadedRepairables());
        }

        if (configInstance.getArmorModsEnabled()) {
            repairables.addAll(CustomArmorConfig.getInstance().getLoadedRepairables());
        }

        if (configInstance.getBlockModsEnabled()) {
            CustomBlockConfig.getInstance();
        }

        if (configInstance.getEntityModsEnabled()) {
            CustomEntityConfig.getInstance();
        }

        // Load repair configs, make manager, and register them at this time
        RepairConfigManager rManager = new RepairConfigManager(this);
        repairables.addAll(rManager.getLoadedRepairables());
        repairableManager = RepairableManagerFactory.getRepairManager(repairables.size());
        repairableManager.registerRepairables(repairables);

        // Check if Repair Anvil and Salvage Anvil have different itemID's
        if (configInstance.getSalvageAnvilId() == configInstance.getRepairAnvilId()) {
            getLogger().warning("Can't use the same itemID for Repair/Salvage Anvils!");
        }
    }

    private void setupSpout() {
        // Check for Spout
        if (getServer().getPluginManager().isPluginEnabled("Spout")) {
            spoutEnabled = true;

            SpoutConfig.getInstance();
            SpoutUtils.setupSpoutConfigs();
            SpoutUtils.registerCustomEvent();
            SpoutUtils.preCacheFiles();
            SpoutUtils.reloadSpoutPlayers(); // Handle spout players after a /reload
        }
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();

        // Register events
        pluginManager.registerEvents(playerListener, this);
        pluginManager.registerEvents(blockListener, this);
        pluginManager.registerEvents(entityListener, this);
        pluginManager.registerEvents(inventoryListener, this);
        pluginManager.registerEvents(worldListener, this);
    }

    /**
     * Register the commands.
     */
    private void registerCommands() {
        CommandRegistrationManager.registerSkillCommands();

        // mc* commands
        CommandRegistrationManager.registerMcpurgeCommand();
        CommandRegistrationManager.registerMcremoveCommand();
        CommandRegistrationManager.registerMcabilityCommand();
        CommandRegistrationManager.registerMcgodCommand();
        CommandRegistrationManager.registerMcmmoCommand();
        CommandRegistrationManager.registerMcrefreshCommand();
        CommandRegistrationManager.registerMctopCommand();
        CommandRegistrationManager.registerMcrankCommand();
        CommandRegistrationManager.registerMcstatsCommand();

        // Party commands
        CommandRegistrationManager.registerAdminChatCommand();
        CommandRegistrationManager.registerPartyCommand();
        CommandRegistrationManager.registerPartyChatCommand();
        CommandRegistrationManager.registerPtpCommand();

        // Other commands
        CommandRegistrationManager.registerAddxpCommand();
        CommandRegistrationManager.registerAddlevelsCommand();
        CommandRegistrationManager.registerMmoeditCommand();
        CommandRegistrationManager.registerInspectCommand();
        CommandRegistrationManager.registerXprateCommand();
        CommandRegistrationManager.registerMmoupdateCommand();
        CommandRegistrationManager.registerSkillresetCommand();
        CommandRegistrationManager.registerHardcoreCommand();
        CommandRegistrationManager.registerVampirismCommand();
        CommandRegistrationManager.registerMcnotifyCommand();

        // Spout commands
        CommandRegistrationManager.registerXplockCommand();
        CommandRegistrationManager.registerMchudCommand();
    }

    private void registerCustomRecipes() {
        if (Config.getInstance().getChimaeraEnabled()) {
            getServer().addRecipe(ChimaeraWing.getChimaeraWingRecipe());
        }
    }

    private void scheduleTasks() {
        // Parties are loaded at the end of first server tick otherwise Server.getOfflinePlayer throws an IndexOutOfBoundsException
        new PartyLoaderTask().runTaskLater(this, 0);

        // Periodic save timer (Saves every 10 minutes by default)
        long saveIntervalTicks = Config.getInstance().getSaveInterval() * 1200;

        new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);

        // Regen & Cooldown timer (Runs every second)
        new SkillMonitorTask().runTaskTimer(this, 20, 20);

        // Bleed timer (Runs every two seconds)
        new BleedTimerTask().runTaskTimer(this, 40, 40);

        // Old & Powerless User remover
        int purgeInterval = Config.getInstance().getPurgeInterval();
        UserPurgeTask userPurgeTask = new UserPurgeTask();

        if (purgeInterval == 0) {
            userPurgeTask.runTaskLater(this, 40);
        }
        else if (purgeInterval > 0) {
            long purgeIntervalTicks = purgeInterval * 60 * 60 * 20;

            userPurgeTask.runTaskTimer(this, purgeIntervalTicks, purgeIntervalTicks);
        }

        // Automatically remove old members from parties
        long kickInterval = Config.getInstance().getAutoPartyKickInterval();
        PartyAutoKickTask partyAutoKickTask = new PartyAutoKickTask();

        if (kickInterval == 0) {
            partyAutoKickTask.runTaskLater(this, 40); // Start 2 seconds after startup.
        }
        else if (kickInterval > 0) {
            long kickIntervalTicks = kickInterval * 60 * 60 * 20;

            partyAutoKickTask.runTaskTimer(this, kickIntervalTicks, kickIntervalTicks);
        }
    }
}
