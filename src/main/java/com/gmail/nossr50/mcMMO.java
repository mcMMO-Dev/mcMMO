package com.gmail.nossr50;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.commands.skills.*;
import com.gmail.nossr50.commands.spout.*;
import com.gmail.nossr50.commands.mc.*;
import com.gmail.nossr50.commands.party.*;
import com.gmail.nossr50.commands.general.*;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.runnables.*;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.listeners.mcBlockListener;
import com.gmail.nossr50.listeners.mcEntityListener;
import com.gmail.nossr50.listeners.mcPlayerListener;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.FileManager;

public class mcMMO extends JavaPlugin {

    public static String maindirectory = "plugins" + File.separator + "mcMMO";
    public static File file = new File(maindirectory + File.separator + "config.yml");
    public static File versionFile = new File(maindirectory + File.separator + "VERSION");

    private final mcPlayerListener playerListener = new mcPlayerListener(this);
    private final mcBlockListener blockListener = new mcBlockListener(this);
    private final mcEntityListener entityListener = new mcEntityListener(this);

    //Alias - Command
    public HashMap<String, String> aliasMap = new HashMap<String, String>();
    public HashMap<Entity, Integer> arrowTracker = new HashMap<Entity, Integer>();
    public HashMap<Integer, Player> tntTracker = new HashMap<Integer, Player>();

    public static Database database = null;

    //Config file stuff
    LoadProperties config;
    LoadTreasures config2;

    //Jar stuff
    public static File mcmmo;

    /**
     * Things to be run when the plugin is enabled.
     */
    public void onEnable() {
        final Plugin thisPlugin = this;
        mcmmo = this.getFile();
        new File(maindirectory).mkdir();

        if (!versionFile.exists()) {
            updateVersion();
        }
        else {
            String vnum = readVersion();

            //This will be changed to whatever version preceded when we actually need updater code.
            //Version 1.0.48 is the first to implement this, no checking before that version can be done.
            if (vnum.equalsIgnoreCase("1.0.48")) {
                updateFrom(1);
            }

            //Just add in more else if blocks for versions that need updater code.  Increment the updateFrom age int as we do so.
            //Catch all for versions not matching and no specific code being needed
            else if (!vnum.equalsIgnoreCase(this.getDescription().getVersion())) {
                updateFrom(-1);
            }
        }

        this.config = new LoadProperties(this);
        this.config.load();

        this.config2 = new LoadTreasures(this);
        this.config2.load();

        Party.getInstance().loadParties();
        new Party(this);

        if (!LoadProperties.useMySQL) {
            Users.getInstance().loadUsers();
        }

        PluginManager pm = getServer().getPluginManager();

        if (pm.getPlugin("Spout") != null) {
            LoadProperties.spoutEnabled = true;
        }
        else {
            LoadProperties.spoutEnabled = false;
        }

        //Register events
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(entityListener, this);

        PluginDescriptionFile pdfFile = this.getDescription();

        //Setup the leaderboards
        if (LoadProperties.useMySQL) {
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

        //Periodic save timer (Saves every 10 minutes)
        scheduler.scheduleSyncRepeatingTask(this, new mcSaveTimer(this), 0, LoadProperties.saveInterval * 1200);
        //Regen & Cooldown timer (Runs every second)
        scheduler.scheduleSyncRepeatingTask(this, new mcTimer(this), 0, 20);
        //Bleed timer (Runs every two seconds)
        scheduler.scheduleSyncRepeatingTask(this, new mcBleedTimer(this), 0, 40);

        registerCommands();

        //Spout Stuff
        if (LoadProperties.spoutEnabled) {
            SpoutStuff.setupSpoutConfigs();
            SpoutStuff.registerCustomEvent();
            SpoutStuff.extractFiles(); //Extract source materials

            FileManager FM = SpoutManager.getFileManager();
            FM.addToPreLoginCache(this, SpoutStuff.getFiles());
        }

        if (LoadProperties.statsTracking) {
            //Plugin Metrics running in a new thread
            new Thread(new Runnable() {
                public void run() {
                    try {
                        // create a new metrics object
                        Metrics metrics = new Metrics();

                        // 'this' in this context is the Plugin object
                        metrics.beginMeasuringPlugin(thisPlugin);
                    }
                    catch (IOException e) {
                        System.out.println("Failed to submit stats.");
                    }
                }
            }).start();
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
     * Check the XP of a player.
     * </br>
     * This function is designed for API usage.
     *
     * @param player
     * @param skillType
     */
    public void checkXp(Player player, SkillType skillType) {
        if (skillType == SkillType.ALL) {
            Skills.XpCheckAll(player);
        }
        else {
            Skills.XpCheckSkill(skillType, player);
        }
    }

    /**
     * Check if two players are in the same party.
     * </br>
     * This function is designed for API usage.
     *
     * @param playera The first player to check
     * @param playerb The second player to check
     * @return true if the two players are in the same party, false otherwise
     */
    public boolean inSameParty(Player playera, Player playerb) {
        if (Users.getProfile(playera).inParty() && Users.getProfile(playerb).inParty()) {
            if (Users.getProfile(playera).getParty().equals(Users.getProfile(playerb).getParty())) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * Get a list of all current party names.
     * </br>
     * This function is designed for API usage.
     *
     * @return the list of parties.
     */
    public ArrayList<String> getParties() {
        String location = "plugins/mcMMO/mcmmo.users";
        ArrayList<String> parties = new ArrayList<String>();

        try {

            //Open the users file
            FileReader file = new FileReader(location);
            BufferedReader in = new BufferedReader(file);
            String line = "";

            while((line = in.readLine()) != null) {
                String[] character = line.split(":");
                String theparty = null;

                //Party
                if (character.length > 3) {
                    theparty = character[3];
                }

                if (!parties.contains(theparty)) {
                    parties.add(theparty);
                }
            }
            in.close();
        }
        catch (Exception e) {
            Bukkit.getLogger().severe("Exception while reading " + location + " (Are you sure you formatted it correctly?)" + e.toString());
        }
        return parties;
    }

    /**
     * Get the name of the party a player is in.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check the party name of
     * @return the name of the player's party
     */
    public static String getPartyName(Player player) {
        PlayerProfile PP = Users.getProfile(player);
        return PP.getParty();
    }

    /**
     * Checks if a player is in a party.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The player to check
     * @return true if the player is in a party, false otherwise
     */
    public static boolean inParty(Player player) {
        PlayerProfile PP = Users.getProfile(player);
        return PP.inParty();
    }

    /**
     * Things to be run when the plugin is disabled.
     */
    public void onDisable() {

        //Make sure to save player information if the server shuts down
        for (PlayerProfile x : Users.getProfiles().values()) {
            x.save();
        }

        Bukkit.getServer().getScheduler().cancelTasks(this); //This removes our tasks
        System.out.println("mcMMO was disabled."); //How informative!
    }

    /**
     * Register the commands.
     */
    private void registerCommands() {

        //Register aliases with the aliasmap (used in the playercommandpreprocessevent to ugly alias them to actual commands)
        //Skills commands
        aliasMap.put(mcLocale.getString("m.SkillAcrobatics").toLowerCase(), "acrobatics");
        aliasMap.put(mcLocale.getString("m.SkillArchery").toLowerCase(), "archery");
        aliasMap.put(mcLocale.getString("m.SkillAxes").toLowerCase(), "axes");
        aliasMap.put(mcLocale.getString("m.SkillExcavation").toLowerCase(), "excavation");
        aliasMap.put(mcLocale.getString("m.SkillFishing").toLowerCase(), "fishing");
        aliasMap.put(mcLocale.getString("m.SkillHerbalism").toLowerCase(), "herbalism");
        aliasMap.put(mcLocale.getString("m.SkillMining").toLowerCase(), "mining");
        aliasMap.put(mcLocale.getString("m.SkillRepair").toLowerCase(), "repair");
        aliasMap.put(mcLocale.getString("m.SkillSwords").toLowerCase(), "swords");
        aliasMap.put(mcLocale.getString("m.SkillTaming").toLowerCase(), "taming");
        aliasMap.put(mcLocale.getString("m.SkillUnarmed").toLowerCase(), "unarmed");
        aliasMap.put(mcLocale.getString("m.SkillWoodCutting").toLowerCase(), "woodcutting");

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
        if (LoadProperties.mcremoveEnable) {
            getCommand("mcremove").setExecutor(new McremoveCommand());
        }

        if (LoadProperties.mcabilityEnable) {
            getCommand("mcability").setExecutor(new McabilityCommand());
        }

        if (LoadProperties.mccEnable) {
            getCommand("mcc").setExecutor(new MccCommand());
        }

        if (LoadProperties.mcgodEnable) {
            getCommand("mcgod").setExecutor(new McgodCommand());
        }

        if (LoadProperties.mcmmoEnable) {
            getCommand("mcmmo").setExecutor(new McmmoCommand());
        }

        if (LoadProperties.mcrefreshEnable) {
            getCommand("mcrefresh").setExecutor(new McrefreshCommand(this));
        }

        if (LoadProperties.mctopEnable) {
            getCommand("mctop").setExecutor(new MctopCommand());
        }

        if (LoadProperties.mcstatsEnable) {
            getCommand("mcstats").setExecutor(new McstatsCommand());
        }

        //Party commands
        if (LoadProperties.acceptEnable) {
            getCommand("accept").setExecutor(new AcceptCommand());
        }

        if (LoadProperties.aEnable) {
            getCommand("a").setExecutor(new ACommand());
        }

        if (LoadProperties.inviteEnable) {
            getCommand("invite").setExecutor(new InviteCommand(this));
        }

        if (LoadProperties.partyEnable) {
            getCommand("party").setExecutor(new PartyCommand());
        }

        if (LoadProperties.pEnable) {
            getCommand("p").setExecutor(new PCommand());
        }

        if (LoadProperties.ptpEnable) {
            getCommand("ptp").setExecutor(new PtpCommand(this));
        }

        //Other commands
        if (LoadProperties.addxpEnable) {
            getCommand("addxp").setExecutor(new AddxpCommand(this));
        }

        if (LoadProperties.addlevelsEnable) {
            getCommand("addlevels").setExecutor(new AddlevelsCommand(this));
        }

        if (LoadProperties.mmoeditEnable) {
            getCommand("mmoedit").setExecutor(new MmoeditCommand());
        }

        if (LoadProperties.inspectEnable) {
            getCommand("inspect").setExecutor(new InspectCommand(this));
        }

        if (LoadProperties.xprateEnable) {
            getCommand("xprate").setExecutor(new XprateCommand());
        }

        getCommand("mmoupdate").setExecutor(new MmoupdateCommand());

        //Spout commands
        if (LoadProperties.xplockEnable) {
            getCommand("xplock").setExecutor(new XplockCommand());
        }

        getCommand("mchud").setExecutor(new MchudCommand());
    }

    /**
     * Update mcMMO from a given version
     * </p>
     * It is important to always assume that you are updating from the lowest possible version.
     * Thus, every block of updater code should be complete and self-contained; finishing all 
     * SQL transactions and closing all file handlers, such that the next block of updater code
     * if called will handle updating as expected.
     *
     * @param age Specifies which updater code to run
     */
    public void updateFrom(int age) {

        //No updater code needed, just update the version.
        if (age == -1) {
            updateVersion();
            return;
        }

        //Updater code from age 1 goes here
        if (age <= 1) {
            //Since age 1 is an example for now, we will just let it do nothing.
        }

        //If we are updating from age 1 but we need more to reach age 2, this will run too.
        if (age <= 2) {

        }
        updateVersion();
    }

    /**
     * Update the version file.
     */
    public void updateVersion() {
        try {
            versionFile.createNewFile();
            BufferedWriter vout = new BufferedWriter(new FileWriter(versionFile));
            vout.write(this.getDescription().getVersion());
            vout.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the current mcMMO version.
     *
     * @return a String representing the current mcMMO version
     */
    public String readVersion() {
        byte[] buffer = new byte[(int) versionFile.length()];
        BufferedInputStream f = null;

        try {
            f = new BufferedInputStream(new FileInputStream(versionFile));
            f.read(buffer);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            if (f != null) {
                try {
                    f.close();
                    }
                catch (IOException ignored) {}
            }
        }
        return new String(buffer);
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
            Bukkit.getLogger().severe("Could not save config to " + treasuresConfigFile + ex.toString());
        }
    }
}
