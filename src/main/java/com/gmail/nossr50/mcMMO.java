package com.gmail.nossr50;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.commands.skills.*;
import com.gmail.nossr50.commands.spout.*;
import com.gmail.nossr50.commands.mc.*;
import com.gmail.nossr50.commands.party.*;
import com.gmail.nossr50.commands.general.*;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.runnables.*;
import com.gmail.nossr50.util.Database;
import com.gmail.nossr50.util.Leaderboard;
import com.gmail.nossr50.util.Metrics;
import com.gmail.nossr50.util.Users;
import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.listeners.EntityListener;
import com.gmail.nossr50.listeners.HardcoreListener;
import com.gmail.nossr50.listeners.PlayerListener;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class mcMMO extends JavaPlugin {

    private final PlayerListener playerListener = new PlayerListener(this);
    private final BlockListener blockListener = new BlockListener(this);
    private final EntityListener entityListener = new EntityListener(this);
    private final HardcoreListener hardcoreListener = new HardcoreListener();

    public HashMap<String, String> aliasMap = new HashMap<String, String>(); //Alias - Command
    public HashMap<Entity, Integer> arrowTracker = new HashMap<Entity, Integer>();
    public HashMap<Integer, Player> tntTracker = new HashMap<Integer, Player>();

    public static File versionFile;
    public static Database database;
    public static mcMMO p;

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

        if (!Config.getInstance().getUseMySQL()) {
            Users.loadUsers();
        }

        PluginManager pm = getServer().getPluginManager();

        //Register events
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(hardcoreListener, this);

        PluginDescriptionFile pdfFile = this.getDescription();

        //Setup the leaderboards
        if (Config.getInstance().getUseMySQL()) {
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
        scheduler.scheduleSyncRepeatingTask(this, new SaveTimer(this), 0, Config.getInstance().getSaveInterval() * 1200);
        //Regen & Cooldown timer (Runs every second)
        scheduler.scheduleSyncRepeatingTask(this, new SkillMonitor(this), 0, 20);
        //Bleed timer (Runs every two seconds)
        scheduler.scheduleSyncRepeatingTask(this, new BleedTimer(), 0, 40);

        registerCommands();

        if (Config.getInstance().getStatsTrackingEnabled()) {
            try {
                    Metrics metrics = new Metrics(this);
                    metrics.start();
                } catch (IOException e) {
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
        
        //Remove other tasks BEFORE starting the Backup, or we just cancel it straight away.
        
        try {  //Try the backup
			ZipLibrary.mcMMObackup();
		} catch (IOException e)  {
			p.getLogger().severe(e.toString());
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
        aliasMap.put(LocaleLoader.getString("WoodCutting.SkillName").toLowerCase(), "woodcutting");

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
        if (Config.getInstance().getCommandMCRemoveEnabled()) {
            getCommand("mcremove").setExecutor(new McremoveCommand(this));
        }

        if (Config.getInstance().getCommandMCAbilityEnabled()) {
            getCommand("mcability").setExecutor(new McabilityCommand());
        }

        if (Config.getInstance().getCommandMCCEnabled()) {
            getCommand("mcc").setExecutor(new MccCommand());
        }

        if (Config.getInstance().getCommandMCGodEnabled()) {
            getCommand("mcgod").setExecutor(new McgodCommand());
        }

        if (Config.getInstance().getCommandmcMMOEnabled()) {
            getCommand("mcmmo").setExecutor(new McmmoCommand());
        }

        if (Config.getInstance().getCommandMCRefreshEnabled()) {
            getCommand("mcrefresh").setExecutor(new McrefreshCommand(this));
        }

        if (Config.getInstance().getCommandMCTopEnabled()) {
            getCommand("mctop").setExecutor(new MctopCommand());
        }

        if (Config.getInstance().getCommandMCStatsEnabled()) {
            getCommand("mcstats").setExecutor(new McstatsCommand());
        }

        //Party commands
        if (Config.getInstance().getCommandAcceptEnabled()) {
            getCommand("accept").setExecutor(new AcceptCommand(this));
        }

        if (Config.getInstance().getCommandAdminChatAEnabled()) {
            getCommand("a").setExecutor(new ACommand(this));
        }

        if (Config.getInstance().getCommandInviteEnabled()) {
            getCommand("invite").setExecutor(new InviteCommand(this));
        }

        if (Config.getInstance().getCommandPartyEnabled()) {
            getCommand("party").setExecutor(new PartyCommand(this));
        }

        if (Config.getInstance().getCommandPartyChatPEnabled()) {
            getCommand("p").setExecutor(new PCommand(this));
        }

        if (Config.getInstance().getCommandPTPEnabled()) {
            getCommand("ptp").setExecutor(new PtpCommand(this));
        }

        //Other commands
        if (Config.getInstance().getCommandAddXPEnabled()) {
            getCommand("addxp").setExecutor(new AddxpCommand(this));
        }

        if (Config.getInstance().getCommandAddLevelsEnabled()) {
            getCommand("addlevels").setExecutor(new AddlevelsCommand(this));
        }

        if (Config.getInstance().getCommandMmoeditEnabled()) {
            getCommand("mmoedit").setExecutor(new MmoeditCommand(this));
        }

        if (Config.getInstance().getCommandInspectEnabled()) {
            getCommand("inspect").setExecutor(new InspectCommand(this));
        }

        if (Config.getInstance().getCommandXPRateEnabled()) {
            getCommand("xprate").setExecutor(new XprateCommand(this));
        }

        getCommand("mmoupdate").setExecutor(new MmoupdateCommand(this));

        //Spout commands
        if (Config.getInstance().getCommandXPLockEnabled()) {
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
