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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManager;
import com.gmail.nossr50.util.blockmeta.chunkmeta.ChunkManagerFactory;
import com.gmail.nossr50.util.metrics.MetricsManager;
import com.gmail.nossr50.commands.CommandRegistrationHelper;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.config.TreasuresConfig;
import com.gmail.nossr50.database.Database;
import com.gmail.nossr50.database.Leaderboard;
import com.gmail.nossr50.database.runnables.UserPurgeTask;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.listeners.EntityListener;
import com.gmail.nossr50.listeners.InventoryListener;
import com.gmail.nossr50.listeners.PlayerListener;
import com.gmail.nossr50.listeners.WorldListener;
import com.gmail.nossr50.mods.config.CustomArmorConfig;
import com.gmail.nossr50.mods.config.CustomBlocksConfig;
import com.gmail.nossr50.mods.config.CustomEntityConfig;
import com.gmail.nossr50.mods.config.CustomToolsConfig;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.party.runnables.PartiesLoader;
import com.gmail.nossr50.party.runnables.PartyAutoKick;
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
import com.gmail.nossr50.util.Users;

public class mcMMO extends JavaPlugin {
    private final PlayerListener playerListener = new PlayerListener(this);
    private final BlockListener blockListener = new BlockListener(this);
    private final EntityListener entityListener = new EntityListener(this);
    private final InventoryListener inventoryListener = new InventoryListener(this);
    private final WorldListener worldListener = new WorldListener();

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
        setupSpout();
        loadConfigFiles();

        if (!Config.getInstance().getUseMySQL()) {
            Users.loadUsers();
        }

        registerEvents();

        // Setup the leader boards
        if (Config.getInstance().getUseMySQL()) {
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

        getLogger().info("Version " + getDescription().getVersion() + " is enabled!");

        scheduleTasks();
        registerCommands();

        MetricsManager.setup();

        placeStore = ChunkManagerFactory.getChunkManager(); // Get our ChunkletManager

        new MobStoreCleaner(); // Automatically starts and stores itself
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

    /**
     * Get profile of the player by name.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName Name of player whose profile to get
     * @return the PlayerProfile object
     */
    public PlayerProfile getPlayerProfile(String playerName) {
        return Users.getPlayer(playerName).getProfile();
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
        return Users.getPlayer(player.getName()).getProfile();
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

        if (Config.getInstance().getBackupsEnabled()) {
            // Remove other tasks BEFORE starting the Backup, or we just cancel it straight away.
            try {
                ZipLibrary.mcMMObackup();
            }
            catch (IOException e) {
                getLogger().severe(e.toString());
            }
        }

        getLogger().info("Was disabled."); //How informative!
    }

    private void loadConfigFiles() {
        // Force the loading of config files
        Config configInstance = Config.getInstance();
        TreasuresConfig.getInstance();
        HiddenConfig.getInstance();
        AdvancedConfig.getInstance();

        

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

        if (configInstance.getEntityModsEnabled()) {
            CustomEntityConfig.getInstance();
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
    }

    private void setupSpout() {
        // Check for Spout
        if (getServer().getPluginManager().isPluginEnabled("Spout")) {
            spoutEnabled = true;

            SpoutConfig.getInstance();
            SpoutTools.setupSpoutConfigs();
            SpoutTools.registerCustomEvent();
            SpoutTools.preCacheFiles();
            SpoutTools.reloadSpoutPlayers(); // Handle spout players after a /reload
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
        CommandRegistrationHelper.registerSkillCommands();

        // mc* commands
        CommandRegistrationHelper.registerMcpurgeCommand();
        CommandRegistrationHelper.registerMcremoveCommand();
        CommandRegistrationHelper.registerMcabilityCommand();
        CommandRegistrationHelper.registerMcgodCommand();
        CommandRegistrationHelper.registerMcmmoCommand();
        CommandRegistrationHelper.registerMcrefreshCommand();
        CommandRegistrationHelper.registerMctopCommand();
        CommandRegistrationHelper.registerMcrankCommand();
        CommandRegistrationHelper.registerMcstatsCommand();

        // Party commands
        CommandRegistrationHelper.registerAdminChatCommand();
        CommandRegistrationHelper.registerPartyCommand();
        CommandRegistrationHelper.registerPartyChatCommand();
        CommandRegistrationHelper.registerPtpCommand();

        // Other commands
        CommandRegistrationHelper.registerAddxpCommand();
        CommandRegistrationHelper.registerAddlevelsCommand();
        CommandRegistrationHelper.registerMmoeditCommand();
        CommandRegistrationHelper.registerInspectCommand();
        CommandRegistrationHelper.registerXprateCommand();
        CommandRegistrationHelper.registerMmoupdateCommand();
        CommandRegistrationHelper.registerSkillresetCommand();
        CommandRegistrationHelper.registerHardcoreCommand();
        CommandRegistrationHelper.registerVampirismCommand();

        // Spout commands
        CommandRegistrationHelper.registerXplockCommand();
        CommandRegistrationHelper.registerMchudCommand();
    }

    private void scheduleTasks() {
        BukkitScheduler scheduler = getServer().getScheduler();

        // Parties are loaded at the end of first server tick otherwise Server.getOfflinePlayer throws an IndexOutOfBoundsException
        scheduler.scheduleSyncDelayedTask(this, new PartiesLoader(), 0);

        // Periodic save timer (Saves every 10 minutes by default)
        long saveIntervalTicks = Config.getInstance().getSaveInterval() * 1200;

        scheduler.scheduleSyncRepeatingTask(this, new SaveTimer(), saveIntervalTicks, saveIntervalTicks);
        // Regen & Cooldown timer (Runs every second)
        scheduler.scheduleSyncRepeatingTask(this, new SkillMonitor(), 20, 20);
        // Bleed timer (Runs every two seconds)
        scheduler.scheduleSyncRepeatingTask(this, new BleedTimer(), 40, 40);

        // Old & Powerless User remover
        int purgeInterval = Config.getInstance().getPurgeInterval();

        if (purgeInterval == 0) {
            scheduler.scheduleSyncDelayedTask(this, new UserPurgeTask(), 40); // Start 2 seconds after startup.
        }
        else if (purgeInterval > 0) {
            long purgeIntervalTicks = purgeInterval * 60 * 60 * 20;

            scheduler.scheduleSyncRepeatingTask(this, new UserPurgeTask(), purgeIntervalTicks, purgeIntervalTicks);
        }

        // Automatically remove old members from parties
        long kickInterval = Config.getInstance().getAutoPartyKickInterval();

        if (kickInterval == 0) {
            scheduler.scheduleSyncDelayedTask(this, new PartyAutoKick(), 40); // Start 2 seconds after startup.
        }
        else if (kickInterval > 0) {
            long kickIntervalTicks = kickInterval * 60 * 60 * 20;

            scheduler.scheduleSyncRepeatingTask(this, new PartyAutoKick(), kickIntervalTicks, kickIntervalTicks);
        }
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

    public void toggleXpEventEnabled() {
        xpEventEnabled = !xpEventEnabled;
    }
}
