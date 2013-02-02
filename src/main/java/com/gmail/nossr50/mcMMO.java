package com.gmail.nossr50;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.shatteredlands.shatt.backup.ZipLibrary;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManager;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManagerFactory;
import com.gmail.nossr50.chat.commands.ACommand;
import com.gmail.nossr50.chat.commands.PCommand;
import com.gmail.nossr50.commands.CommandRegistrationHelper;
import com.gmail.nossr50.commands.admin.McgodCommand;
import com.gmail.nossr50.commands.admin.McrefreshCommand;
import com.gmail.nossr50.commands.admin.MmoeditCommand;
import com.gmail.nossr50.commands.admin.SkillResetCommand;
import com.gmail.nossr50.commands.admin.XprateCommand;
import com.gmail.nossr50.commands.player.InspectCommand;
import com.gmail.nossr50.commands.player.McabilityCommand;
import com.gmail.nossr50.commands.player.MccCommand;
import com.gmail.nossr50.commands.player.McmmoCommand;
import com.gmail.nossr50.commands.player.McrankCommand;
import com.gmail.nossr50.commands.player.McstatsCommand;
import com.gmail.nossr50.commands.player.MctopCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.config.TreasuresConfig;
import com.gmail.nossr50.database.Database;
import com.gmail.nossr50.database.commands.McpurgeCommand;
import com.gmail.nossr50.database.commands.McremoveCommand;
import com.gmail.nossr50.database.commands.MmoupdateCommand;
import com.gmail.nossr50.database.runnables.UserPurgeTask;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.listeners.EntityListener;
import com.gmail.nossr50.listeners.HardcoreListener;
import com.gmail.nossr50.listeners.InventoryListener;
import com.gmail.nossr50.listeners.PlayerListener;
import com.gmail.nossr50.listeners.WorldListener;
import com.gmail.nossr50.mods.config.CustomArmorConfig;
import com.gmail.nossr50.mods.config.CustomBlocksConfig;
import com.gmail.nossr50.mods.config.CustomToolsConfig;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.party.commands.PartyCommand;
import com.gmail.nossr50.party.commands.PtpCommand;
import com.gmail.nossr50.runnables.MobStoreCleaner;
import com.gmail.nossr50.runnables.SaveTimer;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.repair.RepairManagerFactory;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.skills.repair.config.RepairConfigManager;
import com.gmail.nossr50.skills.runnables.BleedTimer;
import com.gmail.nossr50.skills.runnables.SkillMonitor;
import com.gmail.nossr50.spout.SpoutConfig;
import com.gmail.nossr50.spout.SpoutTools;
import com.gmail.nossr50.spout.commands.MchudCommand;
import com.gmail.nossr50.spout.commands.XplockCommand;
import com.gmail.nossr50.util.Anniversary;
import com.gmail.nossr50.util.Leaderboard;
import com.gmail.nossr50.util.Metrics;
import com.gmail.nossr50.util.Metrics.Graph;
import com.gmail.nossr50.util.Users;

public class mcMMO extends JavaPlugin {
    private final PlayerListener playerListener = new PlayerListener(this);
    private final BlockListener blockListener = new BlockListener(this);
    private final EntityListener entityListener = new EntityListener(this);
    private final InventoryListener inventoryListener = new InventoryListener(this);
    private final WorldListener worldListener = new WorldListener();
    private final HardcoreListener hardcoreListener = new HardcoreListener();

    private HashMap<Integer, String> tntTracker = new HashMap<Integer, String>();
    private HashMap<Block, String> furnaceTracker = new HashMap<Block, String>();

    public static mcMMO p;

    public static ChunkManager placeStore;
    public static RepairManager repairManager;

    // Jar Stuff
    public static File mcmmo;

    // File Paths
    private static String mainDirectory;
    private static String flatFileDirectory;
    private static String usersFile;
    private static String modDirectory;

    // Spout Check
    public static boolean spoutEnabled = false;

    // XP Event Check
    private boolean xpEventEnabled = false;

    /**
     * Things to be run when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        p = this;
        setupFilePaths();

        // Check for Spout
        if (getServer().getPluginManager().isPluginEnabled("Spout")) {
            spoutEnabled = true;

            SpoutConfig.getInstance();
            SpoutTools.setupSpoutConfigs();
            SpoutTools.registerCustomEvent();
            SpoutTools.preCacheFiles();
            SpoutTools.reloadSpoutPlayers(); // Handle spout players after a /reload
        }

        // Force the loading of config files
        Config configInstance = Config.getInstance();
        TreasuresConfig.getInstance();
        HiddenConfig.getInstance();
        AdvancedConfig.getInstance();
        PartyManager.loadParties();

        List<Repairable> repairables = new ArrayList<Repairable>();

        if (configInstance.getToolModsEnabled()) {
            repairables.addAll(CustomToolsConfig.getInstance().getLoadedRepairables());
        }

        if (configInstance.getArmorModsEnabled()) {
            repairables.addAll(CustomArmorConfig.getInstance().getLoadedRepairables());
        }

        if (configInstance.getBlockModsEnabled()) {
            CustomBlocksConfig.getInstance();
        }

        // Load repair configs, make manager, and register them at this time
        RepairConfigManager rManager = new RepairConfigManager(this);
        repairables.addAll(rManager.getLoadedRepairables());
        repairManager = RepairManagerFactory.getRepairManager(repairables.size());
        repairManager.registerRepairables(repairables);

        // Check if Repair Anvil and Salvage Anvil have different itemID's
        if (configInstance.getSalvageAnvilId() == configInstance.getRepairAnvilId()) {
            getLogger().warning("Can't use the same itemID for Repair/Salvage Anvils!");
        }

        if (!configInstance.getUseMySQL()) {
            Users.loadUsers();
        }

        PluginManager pluginManager = getServer().getPluginManager();

        // Register events
        pluginManager.registerEvents(playerListener, this);
        pluginManager.registerEvents(blockListener, this);
        pluginManager.registerEvents(entityListener, this);
        pluginManager.registerEvents(inventoryListener, this);
        pluginManager.registerEvents(worldListener, this);

        if (configInstance.getHardcoreEnabled()) {
            pluginManager.registerEvents(hardcoreListener, this);
        }

        PluginDescriptionFile pdfFile = getDescription();

        // Setup the leader boards
        if (configInstance.getUseMySQL()) {
            // TODO: Why do we have to check for a connection that hasn't be made yet? 
            Database.checkConnected();
            Database.createStructure();
        }
        else {
            Leaderboard.updateLeaderboards();
        }

        for (Player player : getServer().getOnlinePlayers()) {
            Users.addUser(player); // In case of reload add all users back into PlayerProfile
        }

        getLogger().info("Version " + pdfFile.getVersion() + " is enabled!");

        BukkitScheduler scheduler = getServer().getScheduler();

        // Periodic save timer (Saves every 10 minutes by default)
        scheduler.scheduleSyncRepeatingTask(this, new SaveTimer(), 0, configInstance.getSaveInterval() * 1200);
        // Regen & Cooldown timer (Runs every second)
        scheduler.scheduleSyncRepeatingTask(this, new SkillMonitor(), 0, 20);
        // Bleed timer (Runs every two seconds)
        scheduler.scheduleSyncRepeatingTask(this, new BleedTimer(), 0, 40);

        // Old & Powerless User remover
        int purgeInterval = Config.getInstance().getPurgeInterval();

        if (purgeInterval == 0) {
            scheduler.scheduleSyncDelayedTask(this, new UserPurgeTask(), 40); // Start 2 seconds after startup.
        }
        else if (purgeInterval > 0) {
            scheduler.scheduleSyncRepeatingTask(this, new UserPurgeTask(), 0, purgeInterval * 60L * 60L * 20L);
        }

        registerCommands();

        if (configInstance.getStatsTrackingEnabled()) {
            try {
                Metrics metrics = new Metrics(this);
                Graph graph = metrics.createGraph("Percentage of servers using timings");

                if (pluginManager.useTimings()) {
                    graph.addPlotter(new Metrics.Plotter("Enabled") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }
                else {
                    graph.addPlotter(new Metrics.Plotter("Disabled") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }

                metrics.start();
            }
            catch (IOException e) {
                System.out.println("Failed to submit stats.");
            }
        }

        placeStore = ChunkManagerFactory.getChunkManager(); // Get our ChunkletManager

        new MobStoreCleaner(); // Automatically starts and stores itself
        Anniversary.createAnniversaryFile(); // Create Anniversary files
    }

    /**
     * Setup the various storage file paths
     */
    public void setupFilePaths() {
        mcmmo = getFile();
        mainDirectory = getDataFolder().getPath() + File.separator;
        flatFileDirectory = mainDirectory + "FlatFileStuff" + File.separator;
        usersFile = flatFileDirectory + "mcmmo.users";
        modDirectory = mainDirectory + "ModConfigs" + File.separator;
    }

    /**
     * Get profile of the player by name.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName Name of player whose profile to get
     * @return the PlayerProfile object
     */
    public PlayerProfile getPlayerProfile(String playerName) {
        return Users.getProfile(playerName);
    }

    /**
     * Get profile of the player.
     * </br>
     * This function is designed for API usage.
     *
     * @param player player whose profile to get
     * @return the PlayerProfile object
     */
    public PlayerProfile getPlayerProfile(OfflinePlayer player) {
        return Users.getProfile(player);
    }

    /**
     * Get profile of the player.
     * </br>
     * This function is designed for API usage.
     *
     * @param player player whose profile to get
     * @return the PlayerProfile object
     */
    @Deprecated
    public PlayerProfile getPlayerProfile(Player player) {
        return Users.getProfile(player);
    }

    /**
     * Things to be run when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        Users.saveAll(); // Make sure to save player information if the server shuts down
        PartyManager.saveParties();
        getServer().getScheduler().cancelTasks(this); // This removes our tasks
        placeStore.saveAll(); // Save our metadata
        placeStore.cleanUp(); // Cleanup empty metadata stores

        // Remove other tasks BEFORE starting the Backup, or we just cancel it straight away.
        try {
            ZipLibrary.mcMMObackup();
        }
        catch (IOException e) {
            getLogger().severe(e.toString());
        }

        Anniversary.saveAnniversaryFiles();
        getLogger().info("Was disabled."); //How informative!
    }

    /**
     * Register the commands.
     */
    private void registerCommands() {
        CommandRegistrationHelper.registerSkillCommands();
        Config configInstance = Config.getInstance();

        // mc* commands
        getCommand("mcpurge").setExecutor(new McpurgeCommand());
        getCommand("mcremove").setExecutor(new McremoveCommand());
        getCommand("mcability").setExecutor(new McabilityCommand());
        getCommand("mcc").setExecutor(new MccCommand());
        getCommand("mcgod").setExecutor(new McgodCommand());
        getCommand("mcmmo").setExecutor(new McmmoCommand());
        getCommand("mcrefresh").setExecutor(new McrefreshCommand());
        getCommand("mctop").setExecutor(new MctopCommand());
        getCommand("mcrank").setExecutor(new McrankCommand());
        getCommand("mcstats").setExecutor(new McstatsCommand());

        // Party commands
        getCommand("a").setExecutor(new ACommand());
        getCommand("party").setExecutor(new PartyCommand());
        getCommand("p").setExecutor(new PCommand(this));
        getCommand("ptp").setExecutor(new PtpCommand(this));

        // Other commands
        CommandRegistrationHelper.registerAddxpCommand();
        CommandRegistrationHelper.registerAddlevelsCommand();
        getCommand("mmoedit").setExecutor(new MmoeditCommand());
        getCommand("inspect").setExecutor(new InspectCommand());
        getCommand("xprate").setExecutor(new XprateCommand());
        getCommand("mmoupdate").setExecutor(new MmoupdateCommand());
        getCommand("skillreset").setExecutor(new SkillResetCommand());

        // Spout commands
        getCommand("xplock").setExecutor(new XplockCommand());
        getCommand("mchud").setExecutor(new MchudCommand());
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

    public void addToOpenFurnaceTracker(Block furnace, String playerName) {
        furnaceTracker.put(furnace, playerName);
    }

    public boolean furnaceIsTracked(Block furnace) {
        return furnaceTracker.containsKey(furnace);
    }

    public void removeFromFurnaceTracker(Block furnace) {
        furnaceTracker.remove(furnace);
    }

    public Player getFurnacePlayer(Block furnace) {
        return getServer().getPlayer(furnaceTracker.get(furnace));
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
}

