package com.gmail.nossr50;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.commands.skills.*;
import com.gmail.nossr50.commands.spout.*;
import com.gmail.nossr50.commands.mc.*;
import com.gmail.nossr50.commands.party.*;
import com.gmail.nossr50.commands.general.*;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.LoadTreasures;
import com.gmail.nossr50.config.mods.LoadCustomArmor;
import com.gmail.nossr50.config.mods.LoadCustomTools;
import com.gmail.nossr50.runnables.*;
import com.gmail.nossr50.util.Database;
import com.gmail.nossr50.util.Leaderboard;
import com.gmail.nossr50.util.Metrics;
import com.gmail.nossr50.util.Users;
import com.gmail.nossr50.util.blockmeta.ChunkletManager;
import com.gmail.nossr50.util.blockmeta.ChunkletManagerFactory;
import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.listeners.EntityListener;
import com.gmail.nossr50.listeners.HardcoreListener;
import com.gmail.nossr50.listeners.PlayerListener;
import com.gmail.nossr50.listeners.WorldListener;
import com.gmail.nossr50.locale.LocaleLoader;

import net.shatteredlands.shatt.backup.ZipLibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class mcMMO extends JavaPlugin {

    private final PlayerListener playerListener = new PlayerListener(this);
    private final BlockListener blockListener = new BlockListener(this);
    private final EntityListener entityListener = new EntityListener(this);
    private final WorldListener worldListener = new WorldListener();
    private final HardcoreListener hardcoreListener = new HardcoreListener();

    public HashMap<String, String> aliasMap = new HashMap<String, String>(); //Alias - Command
    public HashMap<Integer, Player> tntTracker = new HashMap<Integer, Player>();

    public static File versionFile;
    public static Database database;
    public static mcMMO p;

    public static ChunkletManager placeStore;

    /* Jar Stuff */
    public File mcmmo;

    //File Paths
    public String mainDirectory, flatFileDirectory, usersFile, leaderboardDirectory, modDirectory;

    /**
     * Things to be run when the plugin is enabled.
     */
    public void onEnable() {
        p = this;
        setupFilePaths();

        //Force the loading of config files
        Config configInstance = Config.getInstance();
        LoadTreasures.getInstance();

        if (configInstance.getToolModsEnabled()) {
            LoadCustomTools.getInstance().load();
        }

        if (configInstance.getArmorModsEnabled()) {
            LoadCustomArmor.getInstance().load();
        }

        if (!configInstance.getUseMySQL()) {
            Users.loadUsers();
        }

        PluginManager pm = getServer().getPluginManager();

        //Register events
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(worldListener, this);

        if (configInstance.getHardcoreEnabled()) {
            pm.registerEvents(hardcoreListener, this);
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
        //Periodic save timer (Saves every 10 minutes)
        scheduler.scheduleSyncRepeatingTask(this, new SaveTimer(this), 0, configInstance.getSaveInterval() * 1200);
        //Regen & Cooldown timer (Runs every second)
        scheduler.scheduleSyncRepeatingTask(this, new SkillMonitor(this), 0, 20);
        //Bleed timer (Runs every two seconds)
        scheduler.scheduleSyncRepeatingTask(this, new BleedTimer(), 0, 40);

        registerCommands();

        if (configInstance.getStatsTrackingEnabled()) {
            try {
                Metrics metrics = new Metrics(this);
                metrics.start();
            }
            catch (IOException e) {
                System.out.println("Failed to submit stats.");
            }
        }

        // Get our ChunkletManager
        placeStore = ChunkletManagerFactory.getChunkletManager();
    }
    
    public void setupFilePaths() {
        mcmmo = getFile();
        mainDirectory = getDataFolder().getPath() + File.separator;
        flatFileDirectory = mainDirectory + "FlatFileStuff" + File.separator;
        usersFile = flatFileDirectory + "mcmmo.users";
        leaderboardDirectory = flatFileDirectory + "Leaderboards" + File.separator;
        modDirectory = mainDirectory + "ModConfigs" + File.separator;
    }

    /**
     * Get profile of the player.
     * </br>
     * This function is designed for API usage.
     *
     * @param player Player whose profile to get
     * @return the PlayerProfile object
     */
    public PlayerProfile getPlayerProfile(Player player) {
        return Users.getProfile(player);
    }

    /**
     * Get profile of the player by name.
     * </br>
     * This function is designed for API usage.
     *
     * @param playerName Name of player whose profile to get
     * @return the PlayerProfile object
     */
    public PlayerProfile getPlayerProfileByName(String playerName) {
        return Users.getProfileByName(playerName);
    }

    /**
     * Get profile of the offline player.
     * </br>
     * This function is designed for API usage.
     *
     * @param player Offline player whose profile to get
     * @return the PlayerProfile object
     */
    public PlayerProfile getOfflinePlayerProfile(OfflinePlayer player) {
        return Users.getProfile(player);
    }

    /**
     * Things to be run when the plugin is disabled.
     */
    public void onDisable() {
        //Make sure to save player information if the server shuts down
        for (PlayerProfile x : Users.getProfiles().values()) {
            x.save();
        }

        getServer().getScheduler().cancelTasks(this); //This removes our tasks

        //Save our metadata
        placeStore.saveAll();

        //Cleanup empty metadata stores
        placeStore.cleanUp();

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
            getCommand("mmoedit").setExecutor(new MmoeditCommand(this));
        }

        if (configInstance.getCommandInspectEnabled()) {
            getCommand("inspect").setExecutor(new InspectCommand(this));
        }

        if (configInstance.getCommandXPRateEnabled()) {
            getCommand("xprate").setExecutor(new XprateCommand(this));
        }

        getCommand("mmoupdate").setExecutor(new MmoupdateCommand(this));

        //Spout commands
        if (configInstance.getCommandXPLockEnabled()) {
            getCommand("xplock").setExecutor(new XplockCommand());
        }

        getCommand("mchud").setExecutor(new MchudCommand(this));
    }

    /*
     * Boilerplate Custom Config Stuff (Treasures)
     */

    private FileConfiguration treasuresConfig = null;
    private File treasuresConfigFile = null;

    /**
     * Reload the Treasures.yml file.
     */
    public void reloadTreasuresConfig() {
        if (treasuresConfigFile == null) {
            treasuresConfigFile = new File(getDataFolder(), "treasures.yml");
        }

        treasuresConfig = YamlConfiguration.loadConfiguration(treasuresConfigFile);
        InputStream defConfigStream = getResource("treasures.yml"); // Look for defaults in the jar

        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            treasuresConfig.setDefaults(defConfig);
        }
    }

    /**
     * Get the Treasures config information.
     *
     * @return the configuration object for treasures.yml
     */
    public FileConfiguration getTreasuresConfig() {
        if (treasuresConfig == null) {
            reloadTreasuresConfig();
        }

        return treasuresConfig;
    }

    /**
     * Save the Treasures config informtion.
     */
    public void saveTreasuresConfig() {
        if (treasuresConfig == null || treasuresConfigFile == null) {
            return;
        }

        try {
            treasuresConfig.save(treasuresConfigFile);
        }
        catch (IOException ex) {
            getLogger().severe("Could not save config to " + treasuresConfigFile + ex.toString());
        }
    }

    /*
     * Boilerplate Custom Config Stuff (Tools)
     */

    private FileConfiguration toolsConfig = null;
    private File toolsConfigFile = null;

    /**
     * Reload the Tools.yml file.
     */
    public void reloadToolsConfig() {
        if (toolsConfigFile == null) {
            toolsConfigFile = new File(modDirectory, "tools.yml");
        }

        toolsConfig = YamlConfiguration.loadConfiguration(toolsConfigFile);
        InputStream defConfigStream = getResource("tools.yml"); // Look for defaults in the jar

        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            toolsConfig.setDefaults(defConfig);
        }
    }

    /**
     * Get the Tools config information.
     *
     * @return the configuration object for tools.yml
     */
    public FileConfiguration getToolsConfig() {
        if (toolsConfig == null) {
            reloadToolsConfig();
        }

        return toolsConfig;
    }

    /**
     * Save the Tools config informtion.
     */
    public void saveToolsConfig() {
        if (toolsConfig == null || toolsConfigFile == null) {
            return;
        }

        try {
            toolsConfig.save(toolsConfigFile);
        }
        catch (IOException ex) {
            getLogger().severe("Could not save config to " + toolsConfigFile + ex.toString());
        }
    }

    /*
     * Boilerplate Custom Config Stuff (Armor)
     */

    private FileConfiguration armorConfig = null;
    private File armorConfigFile = null;

    /**
     * Reload the Armor.yml file.
     */
    public void reloadArmorConfig() {
        if (armorConfigFile == null) {
            armorConfigFile = new File(modDirectory, "armor.yml");
        }

        armorConfig = YamlConfiguration.loadConfiguration(armorConfigFile);
        InputStream defConfigStream = getResource("armor.yml"); // Look for defaults in the jar

        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            armorConfig.setDefaults(defConfig);
        }
    }

    /**
     * Get the Armor config information.
     *
     * @return the configuration object for armor.yml
     */
    public FileConfiguration getArmorConfig() {
        if (armorConfig == null) {
            reloadArmorConfig();
        }

        return armorConfig;
    }

    /**
     * Save the Armor config informtion.
     */
    public void saveArmorConfig() {
        if (armorConfig == null || armorConfigFile == null) {
            return;
        }

        try {
            armorConfig.save(armorConfigFile);
        }
        catch (IOException ex) {
            getLogger().severe("Could not save config to " + armorConfigFile + ex.toString());
        }
    }

    /*
     * Boilerplate Custom Config Stuff (Blocks)
     */

    private FileConfiguration blocksConfig = null;
    private File blocksConfigFile = null;

    /**
     * Reload the Blocks.yml file.
     */
    public void reloadBlocksConfig() {
        if (blocksConfigFile == null) {
            blocksConfigFile = new File(modDirectory, "blocks.yml");
        }

        blocksConfig = YamlConfiguration.loadConfiguration(blocksConfigFile);
        InputStream defConfigStream = getResource("blocks.yml"); // Look for defaults in the jar

        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            blocksConfig.setDefaults(defConfig);
        }
    }

    /**
     * Get the Blocks config information.
     *
     * @return the configuration object for blocks.yml
     */
    public FileConfiguration getBlocksConfig() {
        if (blocksConfig == null) {
            reloadBlocksConfig();
        }

        return blocksConfig;
    }

    /**
     * Save the Blocks config informtion.
     */
    public void saveBlocksConfig() {
        if (blocksConfig == null || blocksConfigFile == null) {
            return;
        }

        try {
            blocksConfig.save(blocksConfigFile);
        }
        catch (IOException ex) {
            getLogger().severe("Could not save config to " + blocksConfigFile + ex.toString());
        }
    }
}
