package com.gmail.nossr50;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.shatteredlands.shatt.backup.ZipLibrary;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.commands.general.AddlevelsCommand;
import com.gmail.nossr50.commands.general.AddxpCommand;
import com.gmail.nossr50.commands.general.InspectCommand;
import com.gmail.nossr50.commands.general.McstatsCommand;
import com.gmail.nossr50.commands.general.MmoeditCommand;
import com.gmail.nossr50.commands.general.MmoupdateCommand;
import com.gmail.nossr50.commands.general.XprateCommand;
import com.gmail.nossr50.commands.mc.McabilityCommand;
import com.gmail.nossr50.commands.mc.MccCommand;
import com.gmail.nossr50.commands.mc.McgodCommand;
import com.gmail.nossr50.commands.mc.McmmoCommand;
import com.gmail.nossr50.commands.mc.McrefreshCommand;
import com.gmail.nossr50.commands.mc.McremoveCommand;
import com.gmail.nossr50.commands.mc.MctopCommand;
import com.gmail.nossr50.commands.party.ACommand;
import com.gmail.nossr50.commands.party.AcceptCommand;
import com.gmail.nossr50.commands.party.InviteCommand;
import com.gmail.nossr50.commands.party.PCommand;
import com.gmail.nossr50.commands.party.PartyCommand;
import com.gmail.nossr50.commands.party.PtpCommand;
import com.gmail.nossr50.commands.skills.AcrobaticsCommand;
import com.gmail.nossr50.commands.skills.ArcheryCommand;
import com.gmail.nossr50.commands.skills.AxesCommand;
import com.gmail.nossr50.commands.skills.ExcavationCommand;
import com.gmail.nossr50.commands.skills.FishingCommand;
import com.gmail.nossr50.commands.skills.HerbalismCommand;
import com.gmail.nossr50.commands.skills.MiningCommand;
import com.gmail.nossr50.commands.skills.RepairCommand;
import com.gmail.nossr50.commands.skills.SwordsCommand;
import com.gmail.nossr50.commands.skills.TamingCommand;
import com.gmail.nossr50.commands.skills.UnarmedCommand;
import com.gmail.nossr50.commands.skills.WoodcuttingCommand;
import com.gmail.nossr50.commands.spout.MchudCommand;
import com.gmail.nossr50.commands.spout.XplockCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.config.TreasuresConfig;
import com.gmail.nossr50.config.mods.CustomArmorConfig;
import com.gmail.nossr50.config.mods.CustomBlocksConfig;
import com.gmail.nossr50.config.mods.CustomToolsConfig;
import com.gmail.nossr50.config.repair.RepairConfigManager;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.listeners.EntityListener;
import com.gmail.nossr50.listeners.HardcoreListener;
import com.gmail.nossr50.listeners.PlayerListener;
import com.gmail.nossr50.listeners.WorldListener;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.runnables.ChunkletUnloader;
import com.gmail.nossr50.runnables.SaveTimer;
import com.gmail.nossr50.runnables.SkillMonitor;
import com.gmail.nossr50.runnables.SpoutStart;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.repair.RepairManagerFactory;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.util.Database;
import com.gmail.nossr50.util.Leaderboard;
import com.gmail.nossr50.util.Metrics;
import com.gmail.nossr50.util.Metrics.Graph;
import com.gmail.nossr50.util.Users;
import com.gmail.nossr50.util.blockmeta.ChunkletManager;
import com.gmail.nossr50.util.blockmeta.ChunkletManagerFactory;

public class mcMMO extends JavaPlugin {

    private final PlayerListener playerListener = new PlayerListener(this);
    private final BlockListener blockListener = new BlockListener(this);
    private final EntityListener entityListener = new EntityListener(this);
    private final WorldListener worldListener = new WorldListener();
    private final HardcoreListener hardcoreListener = new HardcoreListener();

    private HashMap<String, String> aliasMap = new HashMap<String, String>(); //Alias - Command
    private HashMap<Integer, String> tntTracker = new HashMap<Integer, String>();

    private static Database database;
    public static mcMMO p;

    public static ChunkletManager placeStore;
    public static RepairManager repairManager;

    /* Jar Stuff */
    public static File mcmmo;

    //File Paths
    private static String mainDirectory;
    private static String flatFileDirectory;
    private static String usersFile;
    private static String leaderboardDirectory;
    private static String modDirectory;

    //Spout Check
    public static boolean spoutEnabled;

    /**
     * Things to be run when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        p = this;
        setupFilePaths();

        //Force the loading of config files
        Config configInstance = Config.getInstance();
        TreasuresConfig.getInstance();
        HiddenConfig.getInstance();

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

        //Load repair configs, make manager, and register them at this time
        RepairConfigManager rManager = new RepairConfigManager(this);
        repairables.addAll(rManager.getLoadedRepairables());
        repairManager = RepairManagerFactory.getRepairManager(repairables.size());
        repairManager.registerRepairables(repairables);

        if (!configInstance.getUseMySQL()) {
            Users.loadUsers();
        }

        PluginManager pluginManager = getServer().getPluginManager();

        //Register events
        pluginManager.registerEvents(playerListener, this);
        pluginManager.registerEvents(blockListener, this);
        pluginManager.registerEvents(entityListener, this);
        pluginManager.registerEvents(worldListener, this);

        if (configInstance.getHardcoreEnabled()) {
            pluginManager.registerEvents(hardcoreListener, this);
        }

        PluginDescriptionFile pdfFile = getDescription();

        //Setup the leaderboards
        if (configInstance.getUseMySQL()) {
            database = new Database(this);
            database.createStructure();
        }
        else {
            Leaderboard.makeLeaderboards();
        }

        for (Player player : getServer().getOnlinePlayers()) {
            Users.addUser(player); //In case of reload add all users back into PlayerProfile
        }

        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );

        BukkitScheduler scheduler = getServer().getScheduler();

        //Schedule Spout Activation 1 second after start-up
        scheduler.scheduleSyncDelayedTask(this, new SpoutStart(this), 20);
        //Periodic save timer (Saves every 10 minutes by default)
        scheduler.scheduleSyncRepeatingTask(this, new SaveTimer(this), 0, configInstance.getSaveInterval() * 1200);
        //Regen & Cooldown timer (Runs every second)
        scheduler.scheduleSyncRepeatingTask(this, new SkillMonitor(this), 0, 20);
        //Bleed timer (Runs every two seconds)
        scheduler.scheduleSyncRepeatingTask(this, new BleedTimer(), 0, 40);
        //Chunklet unloader (Runs every 20 seconds by default)
        scheduler.scheduleSyncRepeatingTask(this, new ChunkletUnloader(), 0, ChunkletUnloader.RUN_INTERVAL * 20);

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

        // Get our ChunkletManager
        placeStore = ChunkletManagerFactory.getChunkletManager();
    }

    /**
     * Setup the various storage file paths
     */
    public void setupFilePaths() {
        mcmmo = getFile();
        mainDirectory = getDataFolder().getPath() + File.separator;
        flatFileDirectory = mainDirectory + "FlatFileStuff" + File.separator;
        usersFile = flatFileDirectory + "mcmmo.users";
        leaderboardDirectory = flatFileDirectory + "Leaderboards" + File.separator;
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
        Users.saveAll(); //Make sure to save player information if the server shuts down
        PartyManager.getInstance().saveParties();
        getServer().getScheduler().cancelTasks(this); //This removes our tasks
        placeStore.saveAll(); //Save our metadata
        placeStore.cleanUp(); //Cleanup empty metadata stores

        //Remove other tasks BEFORE starting the Backup, or we just cancel it straight away.
        try {
            ZipLibrary.mcMMObackup();
        }
        catch (IOException e) {
            getLogger().severe(e.toString());
        }

        System.out.println("mcMMO was disabled."); //How informative!
    }

    /**
     * Register the commands.
     */
    private void registerCommands() {
        //Register aliases with the aliasmap (used in the playercommandpreprocessevent to ugly alias them to actual commands)
        //Skills commands
        aliasMap.put(LocaleLoader.getString("Acrobatics.SkillName").toLowerCase(), "acrobatics");
        aliasMap.put(LocaleLoader.getString("Archery.SkillName").toLowerCase(), "archery");
        aliasMap.put(LocaleLoader.getString("Axes.SkillName").toLowerCase(), "axes");
        aliasMap.put(LocaleLoader.getString("Excavation.SkillName").toLowerCase(), "excavation");
        aliasMap.put(LocaleLoader.getString("Fishing.SkillName").toLowerCase(), "fishing");
        aliasMap.put(LocaleLoader.getString("Herbalism.SkillName").toLowerCase(), "herbalism");
        aliasMap.put(LocaleLoader.getString("Mining.SkillName").toLowerCase(), "mining");
        aliasMap.put(LocaleLoader.getString("Repair.SkillName").toLowerCase(), "repair");
        aliasMap.put(LocaleLoader.getString("Swords.SkillName").toLowerCase(), "swords");
        aliasMap.put(LocaleLoader.getString("Taming.SkillName").toLowerCase(), "taming");
        aliasMap.put(LocaleLoader.getString("Unarmed.SkillName").toLowerCase(), "unarmed");
        aliasMap.put(LocaleLoader.getString("Woodcutting.SkillName").toLowerCase(), "woodcutting");

        //Register commands
        //Skills commands
        getCommand("acrobatics").setExecutor(new AcrobaticsCommand());
        getCommand("archery").setExecutor(new ArcheryCommand());
        getCommand("axes").setExecutor(new AxesCommand());
        getCommand("excavation").setExecutor(new ExcavationCommand());
        getCommand("fishing").setExecutor(new FishingCommand());
        getCommand("herbalism").setExecutor(new HerbalismCommand());
        getCommand("mining").setExecutor(new MiningCommand());
        getCommand("repair").setExecutor(new RepairCommand());
        getCommand("swords").setExecutor(new SwordsCommand());
        getCommand("taming").setExecutor(new TamingCommand());
        getCommand("unarmed").setExecutor(new UnarmedCommand());
        getCommand("woodcutting").setExecutor(new WoodcuttingCommand());

        Config configInstance = Config.getInstance();

        //mc* commands
        if (configInstance.getCommandMCRemoveEnabled()) {
            getCommand("mcremove").setExecutor(new McremoveCommand(this));
        }

        if (configInstance.getCommandMCAbilityEnabled()) {
            getCommand("mcability").setExecutor(new McabilityCommand());
        }

        if (configInstance.getCommandMCCEnabled()) {
            getCommand("mcc").setExecutor(new MccCommand());
        }

        if (configInstance.getCommandMCGodEnabled()) {
            getCommand("mcgod").setExecutor(new McgodCommand());
        }

        if (configInstance.getCommandmcMMOEnabled()) {
            getCommand("mcmmo").setExecutor(new McmmoCommand());
        }

        if (configInstance.getCommandMCRefreshEnabled()) {
            getCommand("mcrefresh").setExecutor(new McrefreshCommand(this));
        }

        if (configInstance.getCommandMCTopEnabled()) {
            getCommand("mctop").setExecutor(new MctopCommand());
        }

        if (configInstance.getCommandMCStatsEnabled()) {
            getCommand("mcstats").setExecutor(new McstatsCommand());
        }

        //Party commands
        if (configInstance.getCommandAcceptEnabled()) {
            getCommand("accept").setExecutor(new AcceptCommand(this));
        }

        if (configInstance.getCommandAdminChatAEnabled()) {
            getCommand("a").setExecutor(new ACommand(this));
        }

        if (configInstance.getCommandInviteEnabled()) {
            getCommand("invite").setExecutor(new InviteCommand(this));
        }

        if (configInstance.getCommandPartyEnabled()) {
            getCommand("party").setExecutor(new PartyCommand(this));
        }

        if (configInstance.getCommandPartyChatPEnabled()) {
            getCommand("p").setExecutor(new PCommand(this));
        }

        if (configInstance.getCommandPTPEnabled()) {
            getCommand("ptp").setExecutor(new PtpCommand(this));
        }

        //Other commands
        if (configInstance.getCommandAddXPEnabled()) {
            getCommand("addxp").setExecutor(new AddxpCommand(this));
        }

        if (configInstance.getCommandAddLevelsEnabled()) {
            getCommand("addlevels").setExecutor(new AddlevelsCommand(this));
        }

        if (configInstance.getCommandMmoeditEnabled()) {
            getCommand("mmoedit").setExecutor(new MmoeditCommand());
        }

        if (configInstance.getCommandInspectEnabled()) {
            getCommand("inspect").setExecutor(new InspectCommand());
        }

        if (configInstance.getCommandXPRateEnabled()) {
            getCommand("xprate").setExecutor(new XprateCommand(this));
        }

        getCommand("mmoupdate").setExecutor(new MmoupdateCommand(this));

        //Spout commands
        if (configInstance.getCommandXPLockEnabled()) {
            getCommand("xplock").setExecutor(new XplockCommand());
        }

        getCommand("mchud").setExecutor(new MchudCommand());
    }

    /**
     * Checks to see if the alias map contains the given key.
     *
     * @param command The command to check
     * @return true if the command is in the map, false otherwise
     */
    public boolean commandIsAliased(String command) {
        return aliasMap.containsKey(command);
    }

    /**
     * Get the alias of a given command.
     *
     * @param command The command to retrieve the alias of
     * @return the alias of the command
     */
    public String getCommandAlias(String command) {
        return aliasMap.get(command);
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

    public static String getUsersFile() {
        return usersFile;
    }

    public static String getLeaderboardDirectory() {
        return leaderboardDirectory;
    }

    public static String getModDirectory() {
        return modDirectory;
    }

    public static Database getPlayerDatabase() {
        return database;
    }
}

