package com.gmail.nossr50;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.commands.skills.*;
import com.gmail.nossr50.commands.spout.*;
import com.gmail.nossr50.commands.mc.*;
import com.gmail.nossr50.commands.party.*;
import com.gmail.nossr50.commands.general.*;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.runnables.*;
import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.listeners.EntityListener;
import com.gmail.nossr50.listeners.PlayerListener;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class mcMMO extends JavaPlugin {

    private final PlayerListener playerListener = new PlayerListener(this);
    private final BlockListener blockListener = new BlockListener(this);
    private final EntityListener entityListener = new EntityListener(this);

    public HashMap<String, String> aliasMap = new HashMap<String, String>(); //Alias - Command
    public HashMap<Entity, Integer> arrowTracker = new HashMap<Entity, Integer>();
    public HashMap<Integer, Player> tntTracker = new HashMap<Integer, Player>();

    public static File versionFile;
    public static Database database;
    public static mcMMO p;

    //Config file stuff
    Config config;
    LoadTreasures config2;

    //Jar stuff
    public static File mcmmo;

    //File Paths
    public static String mainDirectory;
    public static String flatFileDirectory;
    public static String usersFile;
    public static String leaderboardDirectory;

    /**
     * Things to be run when the plugin is enabled.
     */
    public void onEnable() {
        p = this;
        mcmmo = getFile();

        mainDirectory = getDataFolder().getPath() + File.separator;
        flatFileDirectory = mainDirectory + "FlatFileStuff" + File.separator;
        leaderboardDirectory = flatFileDirectory + "Leaderboards" + File.separator;
        usersFile = flatFileDirectory + "mcmmo.users";

        this.config = new Config(this);
        this.config.load();

        this.config2 = new LoadTreasures(this);
        this.config2.load();

        new Party(this).loadParties();

        if (!Config.getUseMySQL()) {
            Users.loadUsers();
        }

        PluginManager pm = getServer().getPluginManager();

        //Register events
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(entityListener, this);

        PluginDescriptionFile pdfFile = this.getDescription();

        //Setup the leaderboards
        if (Config.getUseMySQL()) {
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
        scheduler.scheduleSyncRepeatingTask(this, new mcSaveTimer(this), 0, Config.getSaveInterval() * 1200);
        //Regen & Cooldown timer (Runs every second)
        scheduler.scheduleSyncRepeatingTask(this, new mcTimer(this), 0, 20);
        //Bleed timer (Runs every two seconds)
        scheduler.scheduleSyncRepeatingTask(this, new mcBleedTimer(this), 0, 40);

        registerCommands();

        if (Config.getStatsTrackingEnabled()) {
            try {
                    Metrics metrics = new Metrics(this);
                    metrics.start();
                }
                catch (IOException e) {
                System.out.println("Failed to submit stats.");
            }
        }
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

        this.getServer().getScheduler().cancelTasks(this); //This removes our tasks
        System.out.println("mcMMO was disabled."); //How informative!
    }

    /**
     * Register the commands.
     */
    private void registerCommands() {

        //Register aliases with the aliasmap (used in the playercommandpreprocessevent to ugly alias them to actual commands)
        //Skills commands
        aliasMap.put(mcLocale.getString("Acrobatics.SkillName").toLowerCase(), "acrobatics");
        aliasMap.put(mcLocale.getString("Archery.SkillName").toLowerCase(), "archery");
        aliasMap.put(mcLocale.getString("Axes.SkillName").toLowerCase(), "axes");
        aliasMap.put(mcLocale.getString("Excavation.SkillName").toLowerCase(), "excavation");
        aliasMap.put(mcLocale.getString("Fishing.SkillName").toLowerCase(), "fishing");
        aliasMap.put(mcLocale.getString("Herbalism.SkillName").toLowerCase(), "herbalism");
        aliasMap.put(mcLocale.getString("Mining.SkillName").toLowerCase(), "mining");
        aliasMap.put(mcLocale.getString("Repair.SkillName").toLowerCase(), "repair");
        aliasMap.put(mcLocale.getString("Swords.SkillName").toLowerCase(), "swords");
        aliasMap.put(mcLocale.getString("Taming.SkillName").toLowerCase(), "taming");
        aliasMap.put(mcLocale.getString("Unarmed.SkillName").toLowerCase(), "unarmed");
        aliasMap.put(mcLocale.getString("WoodCutting.SkillName").toLowerCase(), "woodcutting");

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

        //mc* commands
        if (Config.getCommandMCRemoveEnabled()) {
            getCommand("mcremove").setExecutor(new McremoveCommand(this));
        }

        if (Config.getCommandMCAbilityEnabled()) {
            getCommand("mcability").setExecutor(new McabilityCommand());
        }

        if (Config.getCommandMCCEnabled()) {
            getCommand("mcc").setExecutor(new MccCommand());
        }

        if (Config.getCommandMCGodEnabled()) {
            getCommand("mcgod").setExecutor(new McgodCommand());
        }

        if (Config.getCommandmcMMOEnabled()) {
            getCommand("mcmmo").setExecutor(new McmmoCommand());
        }

        if (Config.getCommandMCRefreshEnabled()) {
            getCommand("mcrefresh").setExecutor(new McrefreshCommand(this));
        }

        if (Config.getCommandMCTopEnabled()) {
            getCommand("mctop").setExecutor(new MctopCommand());
        }

        if (Config.getCommandMCStatsEnabled()) {
            getCommand("mcstats").setExecutor(new McstatsCommand());
        }

        //Party commands
        if (Config.getCommandAcceptEnabled()) {
            getCommand("accept").setExecutor(new AcceptCommand(this));
        }

        if (Config.getCommandAdminChatAEnabled()) {
            getCommand("a").setExecutor(new ACommand(this));
        }

        if (Config.getCommandInviteEnabled()) {
            getCommand("invite").setExecutor(new InviteCommand(this));
        }

        if (Config.getCommandPartyEnabled()) {
            getCommand("party").setExecutor(new PartyCommand(this));
        }

        if (Config.getCommandPartyChatPEnabled()) {
            getCommand("p").setExecutor(new PCommand(this));
        }

        if (Config.getCommandPTPEnabled()) {
            getCommand("ptp").setExecutor(new PtpCommand(this));
        }

        //Other commands
        if (Config.getCommandAddXPEnabled()) {
            getCommand("addxp").setExecutor(new AddxpCommand(this));
        }

        if (Config.getCommandAddLevelsEnabled()) {
            getCommand("addlevels").setExecutor(new AddlevelsCommand(this));
        }

        if (Config.getCommandMmoeditEnabled()) {
            getCommand("mmoedit").setExecutor(new MmoeditCommand(this));
        }

        if (Config.getCommandInspectEnabled()) {
            getCommand("inspect").setExecutor(new InspectCommand(this));
        }

        if (Config.getCommandXPRateEnabled()) {
            getCommand("xprate").setExecutor(new XprateCommand(this));
        }

        getCommand("mmoupdate").setExecutor(new MmoupdateCommand(this));

        //Spout commands
        if (Config.getCommandXPLockEnabled()) {
            getCommand("xplock").setExecutor(new XplockCommand());
        }

        getCommand("mchud").setExecutor(new MchudCommand(this));
    }

    /*
     * Boilerplate Custom Config Stuff
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
            this.getLogger().severe("Could not save config to " + treasuresConfigFile + ex.toString());
        }
    }
}
