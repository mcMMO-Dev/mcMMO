package com.gmail.nossr50.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.Misc;

public final class FlatfileDatabaseManager implements DatabaseManager {
    private final HashMap<SkillType, List<PlayerStat>> playerStatHash = new HashMap<SkillType, List<PlayerStat>>();
    private final List<PlayerStat> powerLevels = new ArrayList<PlayerStat>();
    private long lastUpdate = 0;

    private final long UPDATE_WAIT_TIME = 600000L; // 10 minutes
    private final File usersFile;
    private static final Object fileWritingLock = new Object();

    protected FlatfileDatabaseManager() {
        usersFile = new File(mcMMO.getUsersFilePath());
        checkStructure();
        updateLeaderboards();
    }

    public void purgePowerlessUsers() {
        int purgedUsers = 0;

        mcMMO.p.getLogger().info("Purging powerless users...");

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        // This code is O(n) instead of O(n²)
        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line = "";

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    Map<SkillType, Integer> skills = getSkillMapFromLine(character);

                    boolean powerless = true;
                    for (int skill : skills.values()) {
                        if (skill != 0) {
                            powerless = false;
                            break;
                        }
                    }

                    // If they're still around, rewrite them to the file.
                    if (!powerless) {
                        writer.append(line).append("\r\n");
                    }
                    else {
                        purgedUsers++;
                        Misc.profileCleanup(character[0]);
                    }
                }

                // Write the new file
                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
            }
            catch (IOException e) {
                mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
            }
            finally {
                tryClose(in);
                tryClose(out);
            }
        }

        mcMMO.p.getLogger().info("Purged " + purgedUsers + " users from the database.");
    }

    public void purgeOldUsers() {
        int removedPlayers = 0;
        long currentTime = System.currentTimeMillis();

        mcMMO.p.getLogger().info("Purging old users...");

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        // This code is O(n) instead of O(n²)
        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line = "";

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    String name = character[0];
                    long lastPlayed = 0;
                    boolean rewrite = false;
                    try {
                        lastPlayed = Long.parseLong(character[37]) * Misc.TIME_CONVERSION_FACTOR;
                    }
                    catch (NumberFormatException e) {
                    }
                    if (lastPlayed == 0) {
                        OfflinePlayer player = mcMMO.p.getServer().getOfflinePlayer(name);
                        lastPlayed = player.getLastPlayed();
                        rewrite = true;
                    }

                    if (currentTime - lastPlayed > PURGE_TIME) {
                        removedPlayers++;
                        Misc.profileCleanup(name);
                    }
                    else {
                        if (rewrite) {
                            // Rewrite their data with a valid time
                            character[37] = Long.toString(lastPlayed);
                            String newLine = org.apache.commons.lang.StringUtils.join(character, ":");
                            writer.append(newLine).append("\r\n");
                        }
                        else {
                            writer.append(line).append("\r\n");
                        }
                    }
                }

                // Write the new file
                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
            }
            catch (IOException e) {
                mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
            }
            finally {
                tryClose(in);
                tryClose(out);
            }
        }

        mcMMO.p.getLogger().info("Purged " + removedPlayers + " users from the database.");
    }

    public boolean removeUser(String playerName) {
        boolean worked = false;

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line = "";

                while ((line = in.readLine()) != null) {
                    // Write out the same file but when we get to the player we want to remove, we skip his line.
                    if (!worked && line.split(":")[0].equalsIgnoreCase(playerName)) {
                        mcMMO.p.getLogger().info("User found, removing...");
                        worked = true;
                        continue; // Skip the player
                    }

                    writer.append(line).append("\r\n");
                }

                out = new FileWriter(usersFilePath); // Write out the new file
                out.write(writer.toString());
            }
            catch (Exception e) {
                mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
            }
            finally {
                tryClose(in);
                tryClose(out);
            }
        }

        Misc.profileCleanup(playerName);

        return worked;
    }

    public boolean saveUser(PlayerProfile profile) {
        String playerName = profile.getPlayerName();

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                // Open the file
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                // While not at the end of the file
                while ((line = in.readLine()) != null) {
                    // Read the line in and copy it to the output it's not the player we want to edit
                    if (!line.split(":")[0].equalsIgnoreCase(playerName)) {
                        writer.append(line).append("\r\n");
                    }
                    else {
                        // Otherwise write the new player information
                        writer.append(playerName).append(":");
                        writer.append(profile.getSkillLevel(SkillType.MINING)).append(":");
                        writer.append(":");
                        writer.append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.MINING)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.WOODCUTTING)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.WOODCUTTING)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.REPAIR)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.UNARMED)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.HERBALISM)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.EXCAVATION)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.ARCHERY)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.SWORDS)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.AXES)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.ACROBATICS)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.REPAIR)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.UNARMED)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.HERBALISM)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.EXCAVATION)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.ARCHERY)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.SWORDS)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.AXES)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.ACROBATICS)).append(":");
                        writer.append(":");
                        writer.append(profile.getSkillLevel(SkillType.TAMING)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.TAMING)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.BERSERK)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.GIGA_DRILL_BREAKER)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.TREE_FELLER)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.GREEN_TERRA)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.SERRATED_STRIKES)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.SKULL_SPLITTER)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.SUPER_BREAKER)).append(":");
                        writer.append(":");
                        writer.append(profile.getSkillLevel(SkillType.FISHING)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.FISHING)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.BLAST_MINING)).append(":");
                        writer.append(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR).append(":");
                        MobHealthbarType mobHealthbarType = profile.getMobHealthbarType();
                        writer.append(mobHealthbarType == null ? Config.getInstance().getMobHealthbarDefault().toString() : mobHealthbarType.toString()).append(":");
                        writer.append(profile.getSkillLevel(SkillType.ALCHEMY)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.ALCHEMY)).append(":");
                        writer.append("\r\n");
                    }
                }

                // Write the new file
                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            finally {
                tryClose(in);
                tryClose(out);
            }
        }
    }

    public List<PlayerStat> readLeaderboard(SkillType skill, int pageNumber, int statsPerPage) {
        updateLeaderboards();
        List<PlayerStat> statsList = skill == null ? powerLevels : playerStatHash.get(skill);
        int fromIndex = (Math.max(pageNumber, 1) - 1) * statsPerPage;

        return statsList.subList(Math.min(fromIndex, statsList.size()), Math.min(fromIndex + statsPerPage, statsList.size()));
    }

    public Map<SkillType, Integer> readRank(String playerName) {
        updateLeaderboards();

        Map<SkillType, Integer> skills = new HashMap<SkillType, Integer>();

        for (SkillType skill : SkillType.NON_CHILD_SKILLS) {
            skills.put(skill, getPlayerRank(playerName, playerStatHash.get(skill)));
        }

        skills.put(null, getPlayerRank(playerName, powerLevels));

        return skills;
    }

    public void newUser(String playerName) {
        BufferedWriter out = null;
        synchronized (fileWritingLock) {
            try {
                // Open the file to write the player
                out = new BufferedWriter(new FileWriter(mcMMO.getUsersFilePath(), true));

                // Add the player to the end
                out.append(playerName).append(":");
                out.append("0:"); // Mining
                out.append(":");
                out.append(":");
                out.append("0:"); // Xp
                out.append("0:"); // Woodcutting
                out.append("0:"); // WoodCuttingXp
                out.append("0:"); // Repair
                out.append("0:"); // Unarmed
                out.append("0:"); // Herbalism
                out.append("0:"); // Excavation
                out.append("0:"); // Archery
                out.append("0:"); // Swords
                out.append("0:"); // Axes
                out.append("0:"); // Acrobatics
                out.append("0:"); // RepairXp
                out.append("0:"); // UnarmedXp
                out.append("0:"); // HerbalismXp
                out.append("0:"); // ExcavationXp
                out.append("0:"); // ArcheryXp
                out.append("0:"); // SwordsXp
                out.append("0:"); // AxesXp
                out.append("0:"); // AcrobaticsXp
                out.append(":");
                out.append("0:"); // Taming
                out.append("0:"); // TamingXp
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append(":");
                out.append("0:"); // Fishing
                out.append("0:"); // FishingXp
                out.append("0:"); // Blast Mining
                out.append(String.valueOf(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR)).append(":"); // LastLogin
                out.append(Config.getInstance().getMobHealthbarDefault().toString()).append(":"); // Mob Healthbar HUD
                out.append("0:"); // Alchemy
                out.append("0:"); // AlchemyXp
                
                // Add more in the same format as the line above

                out.newLine();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                tryClose(out);
            }
        }
    }

    public PlayerProfile loadPlayerProfile(String playerName, boolean create) {
        BufferedReader in = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    // Find if the line contains the player we want.
                    String[] character = line.split(":");

                    if (!character[0].equalsIgnoreCase(playerName)) {
                        continue;
                    }

                    PlayerProfile p = loadFromLine(character);
                    return p;
                }

                // Didn't find the player, create a new one
                if (create) {
                    newUser(playerName);
                    return new PlayerProfile(playerName, true);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                // I have no idea why it's necessary to inline tryClose() here, but it removes
                // a resource leak warning, and I'm trusting the compiler on this one.
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Return unloaded profile
        return new PlayerProfile(playerName);
    }

    public void convertUsers(DatabaseManager destination) {
        BufferedReader in = null;
        String usersFilePath = mcMMO.getUsersFilePath();
        int convertedUsers = 0;
        long startMillis = System.currentTimeMillis();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");

                    try {
                        destination.saveUser(loadFromLine(character));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    convertedUsers++;
                    Misc.printProgress(convertedUsers, progressInterval, startMillis);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                tryClose(in);
            }
        }
    }

    public List<String> getStoredUsers() {
        ArrayList<String> users = new ArrayList<String>();
        BufferedReader in = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    users.add(character[0]);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                tryClose(in);
            }
        }
        return users;
    }

    /**
     * Update the leader boards.
     */
    private void updateLeaderboards() {
        // Only update FFS leaderboards every 10 minutes.. this puts a lot of strain on the server (depending on the size of the database) and should not be done frequently
        if (System.currentTimeMillis() < lastUpdate + UPDATE_WAIT_TIME) {
            return;
        }

        String usersFilePath = mcMMO.getUsersFilePath();
        lastUpdate = System.currentTimeMillis(); // Log when the last update was run
        powerLevels.clear(); // Clear old values from the power levels

        // Initialize lists
        List<PlayerStat> mining = new ArrayList<PlayerStat>();
        List<PlayerStat> woodcutting = new ArrayList<PlayerStat>();
        List<PlayerStat> herbalism = new ArrayList<PlayerStat>();
        List<PlayerStat> excavation = new ArrayList<PlayerStat>();
        List<PlayerStat> acrobatics = new ArrayList<PlayerStat>();
        List<PlayerStat> repair = new ArrayList<PlayerStat>();
        List<PlayerStat> swords = new ArrayList<PlayerStat>();
        List<PlayerStat> axes = new ArrayList<PlayerStat>();
        List<PlayerStat> archery = new ArrayList<PlayerStat>();
        List<PlayerStat> unarmed = new ArrayList<PlayerStat>();
        List<PlayerStat> taming = new ArrayList<PlayerStat>();
        List<PlayerStat> fishing = new ArrayList<PlayerStat>();
        List<PlayerStat> alchemy = new ArrayList<PlayerStat>();

        BufferedReader in = null;
        String playerName = null;
        // Read from the FlatFile database and fill our arrays with information
        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                String line = "";

                while ((line = in.readLine()) != null) {
                    String[] data = line.split(":");
                    playerName = data[0];
                    int powerLevel = 0;

                    Map<SkillType, Integer> skills = getSkillMapFromLine(data);

                    powerLevel += putStat(acrobatics, playerName, skills.get(SkillType.ACROBATICS));
                    powerLevel += putStat(alchemy, playerName, skills.get(SkillType.ALCHEMY));
                    powerLevel += putStat(archery, playerName, skills.get(SkillType.ARCHERY));
                    powerLevel += putStat(axes, playerName, skills.get(SkillType.AXES));
                    powerLevel += putStat(excavation, playerName, skills.get(SkillType.EXCAVATION));
                    powerLevel += putStat(fishing, playerName, skills.get(SkillType.FISHING));
                    powerLevel += putStat(herbalism, playerName, skills.get(SkillType.HERBALISM));
                    powerLevel += putStat(mining, playerName, skills.get(SkillType.MINING));
                    powerLevel += putStat(repair, playerName, skills.get(SkillType.REPAIR));
                    powerLevel += putStat(swords, playerName, skills.get(SkillType.SWORDS));
                    powerLevel += putStat(taming, playerName, skills.get(SkillType.TAMING));
                    powerLevel += putStat(unarmed, playerName, skills.get(SkillType.UNARMED));
                    powerLevel += putStat(woodcutting, playerName, skills.get(SkillType.WOODCUTTING));

                    putStat(powerLevels, playerName, powerLevel);
                }
            }
            catch (Exception e) {
                mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " during user " + playerName + " (Are you sure you formatted it correctly?) " + e.toString());
            }
            finally {
                tryClose(in);
            }
        }

        SkillComparator c = new SkillComparator();

        Collections.sort(mining, c);
        Collections.sort(woodcutting, c);
        Collections.sort(repair, c);
        Collections.sort(unarmed, c);
        Collections.sort(herbalism, c);
        Collections.sort(excavation, c);
        Collections.sort(archery, c);
        Collections.sort(swords, c);
        Collections.sort(axes, c);
        Collections.sort(acrobatics, c);
        Collections.sort(taming, c);
        Collections.sort(fishing, c);
        Collections.sort(alchemy, c);
        Collections.sort(powerLevels, c);

        playerStatHash.put(SkillType.MINING, mining);
        playerStatHash.put(SkillType.WOODCUTTING, woodcutting);
        playerStatHash.put(SkillType.REPAIR, repair);
        playerStatHash.put(SkillType.UNARMED, unarmed);
        playerStatHash.put(SkillType.HERBALISM, herbalism);
        playerStatHash.put(SkillType.EXCAVATION, excavation);
        playerStatHash.put(SkillType.ARCHERY, archery);
        playerStatHash.put(SkillType.SWORDS, swords);
        playerStatHash.put(SkillType.AXES, axes);
        playerStatHash.put(SkillType.ACROBATICS, acrobatics);
        playerStatHash.put(SkillType.TAMING, taming);
        playerStatHash.put(SkillType.FISHING, fishing);
        playerStatHash.put(SkillType.ALCHEMY, alchemy);
    }

    /**
     * Checks that the file is present and valid
     */
    private void checkStructure() {
        if (usersFile.exists()) {
            BufferedReader in = null;
            FileWriter out = null;
            String usersFilePath = mcMMO.getUsersFilePath();

            synchronized (fileWritingLock) {
                try {
                    in = new BufferedReader(new FileReader(usersFilePath));
                    StringBuilder writer = new StringBuilder();
                    String line = "";
                    HashSet<String> players = new HashSet<String>();

                    while ((line = in.readLine()) != null) {
                        // Remove empty lines from the file
                        if (line.isEmpty()) {
                            continue;
                        }

                        // Length checks depend on last character being ':'
                        if (line.charAt(line.length() - 1) != ':') {
                            line = line + ":";
                        }
                        String[] character = line.split(":");

                        // Prevent the same player from being present multiple times
                        if (!players.add(character[0])) {
                            continue;
                        }

                        if (character.length < 33) {
                            // Before Version 1.0 - Drop
                            mcMMO.p.getLogger().warning("Dropping malformed or before version 1.0 line from database - " + line);
                            continue;
                        }

                        String oldVersion = null;

                        if (!character[33].isEmpty()) {
                            // Removal of Spout Support
                            // Version 1.4.07-dev2
                            // commit 7bac0e2ca5143bce84dc160617fed97f0b1cb968
                            line = line.replace(character[33], "");
                            oldVersion = "1.4.07";
                        }

                        // If they're valid, rewrite them to the file.
                        if (character.length > 40) {
                            writer.append(line).append("\r\n");
                            continue;
                        }

                        StringBuilder newLine = new StringBuilder(line);

                        if (character.length <= 33) {
                            // Introduction of HUDType
                            // Version 1.1.06
                            // commit 78f79213cdd7190cd11ae54526f3b4ea42078e8a
                            newLine.append(":");
                            oldVersion = "1.1.06";
                        }
                        if (character.length <= 35) {
                            // Introduction of Fishing
                            // Version 1.2.00
                            // commit a814b57311bc7734661109f0e77fc8bab3a0bd29
                            newLine.append(0).append(":");
                            newLine.append(0).append(":");
                            if (oldVersion == null) {
                                oldVersion = "1.2.00";
                            }
                        }
                        if (character.length <= 36) {
                            // Introduction of Blast Mining cooldowns
                            // Version 1.3.00-dev
                            // commit fadbaf429d6b4764b8f1ad0efaa524a090e82ef5
                            newLine.append(0).append(":");
                            if (oldVersion == null) {
                                oldVersion = "1.3.00";
                            }
                        }
                        if (character.length <= 37) {
                            // Making old-purge work with flatfile
                            // Version 1.4.00-dev
                            // commmit 3f6c07ba6aaf44e388cc3b882cac3d8f51d0ac28
                            // XXX Cannot create an OfflinePlayer at startup, use 0 and fix in purge
                            newLine.append("0").append(":");
                            if (oldVersion == null) {
                                oldVersion = "1.4.00";
                            }
                        }
                        if (character.length <= 38) {
                            // Addition of mob healthbars
                            // Version 1.4.06
                            // commit da29185b7dc7e0d992754bba555576d48fa08aa6
                            newLine.append(Config.getInstance().getMobHealthbarDefault().toString()).append(":");
                            if (oldVersion == null) {
                                oldVersion = "1.4.06";
                            }
                        }
                        if (character.length <= 40) {
                            // Addition of Alchemy
                            // Version 1.4.08
                            newLine.append("0").append(":");
                            newLine.append("0").append(":");
                            if (oldVersion == null) {
                                oldVersion = "1.4.08";
                            }
                        }

                        if (oldVersion != null) {
                            mcMMO.p.debug("Updating database line for player " + character[0] + " from before version " + oldVersion);
                        }

                        writer.append(newLine).append("\r\n");
                    }

                    // Write the new file
                    out = new FileWriter(usersFilePath);
                    out.write(writer.toString());
                }
                catch (IOException e) {
                    mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
                }
                finally {
                    tryClose(in);
                    tryClose(out);
                }
            }
            return;
        }

        usersFile.getParentFile().mkdir();

        try {
            mcMMO.p.debug("Creating mcmmo.users file...");
            new File(mcMMO.getUsersFilePath()).createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryClose(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer getPlayerRank(String playerName, List<PlayerStat> statsList) {
        if (statsList == null) {
            return null;
        }

        int currentPos = 1;

        for (PlayerStat stat : statsList) {
            if (stat.name.equalsIgnoreCase(playerName)) {
                return currentPos;
            }

            currentPos++;
        }

        return null;
    }

    private int putStat(List<PlayerStat> statList, String playerName, int statValue) {
        statList.add(new PlayerStat(playerName, statValue));
        return statValue;
    }

    private class SkillComparator implements Comparator<PlayerStat> {
        @Override
        public int compare(PlayerStat o1, PlayerStat o2) {
            return (o2.statVal - o1.statVal);
        }
    }

    private PlayerProfile loadFromLine(String[] character) throws Exception {
        Map<SkillType, Integer>   skills     = getSkillMapFromLine(character);      // Skill levels
        Map<SkillType, Float>     skillsXp   = new HashMap<SkillType, Float>();     // Skill & XP
        Map<AbilityType, Integer> skillsDATS = new HashMap<AbilityType, Integer>(); // Ability & Cooldown
        MobHealthbarType mobHealthbarType;

        // TODO on updates, put new values in a try{} ?

        skillsXp.put(SkillType.TAMING, (float) Integer.valueOf(character[25]));
        skillsXp.put(SkillType.MINING, (float) Integer.valueOf(character[4]));
        skillsXp.put(SkillType.REPAIR, (float) Integer.valueOf(character[15]));
        skillsXp.put(SkillType.WOODCUTTING, (float) Integer.valueOf(character[6]));
        skillsXp.put(SkillType.UNARMED, (float) Integer.valueOf(character[16]));
        skillsXp.put(SkillType.HERBALISM, (float) Integer.valueOf(character[17]));
        skillsXp.put(SkillType.EXCAVATION, (float) Integer.valueOf(character[18]));
        skillsXp.put(SkillType.ARCHERY, (float) Integer.valueOf(character[19]));
        skillsXp.put(SkillType.SWORDS, (float) Integer.valueOf(character[20]));
        skillsXp.put(SkillType.AXES, (float) Integer.valueOf(character[21]));
        skillsXp.put(SkillType.ACROBATICS, (float) Integer.valueOf(character[22]));
        skillsXp.put(SkillType.FISHING, (float) Integer.valueOf(character[35]));
        skillsXp.put(SkillType.ALCHEMY, (float) Integer.valueOf(character[40]));

        // Taming - Unused
        skillsDATS.put(AbilityType.SUPER_BREAKER, Integer.valueOf(character[32]));
        // Repair - Unused
        skillsDATS.put(AbilityType.TREE_FELLER, Integer.valueOf(character[28]));
        skillsDATS.put(AbilityType.BERSERK, Integer.valueOf(character[26]));
        skillsDATS.put(AbilityType.GREEN_TERRA, Integer.valueOf(character[29]));
        skillsDATS.put(AbilityType.GIGA_DRILL_BREAKER, Integer.valueOf(character[27]));
        // Archery - Unused
        skillsDATS.put(AbilityType.SERRATED_STRIKES, Integer.valueOf(character[30]));
        skillsDATS.put(AbilityType.SKULL_SPLITTER, Integer.valueOf(character[31]));
        // Acrobatics - Unused
        skillsDATS.put(AbilityType.BLAST_MINING, Integer.valueOf(character[36]));

        try {
            mobHealthbarType = MobHealthbarType.valueOf(character[38]);
        }
        catch (Exception e) {
            mobHealthbarType = Config.getInstance().getMobHealthbarDefault();
        }

        return new PlayerProfile(character[0], skills, skillsXp, skillsDATS, mobHealthbarType);
    }

    private Map<SkillType, Integer> getSkillMapFromLine(String[] character) {
        Map<SkillType, Integer> skills = new HashMap<SkillType, Integer>();   // Skill & Level

        skills.put(SkillType.TAMING, Integer.valueOf(character[24]));
        skills.put(SkillType.MINING, Integer.valueOf(character[1]));
        skills.put(SkillType.REPAIR, Integer.valueOf(character[7]));
        skills.put(SkillType.WOODCUTTING, Integer.valueOf(character[5]));
        skills.put(SkillType.UNARMED, Integer.valueOf(character[8]));
        skills.put(SkillType.HERBALISM, Integer.valueOf(character[9]));
        skills.put(SkillType.EXCAVATION, Integer.valueOf(character[10]));
        skills.put(SkillType.ARCHERY, Integer.valueOf(character[11]));
        skills.put(SkillType.SWORDS, Integer.valueOf(character[12]));
        skills.put(SkillType.AXES, Integer.valueOf(character[13]));
        skills.put(SkillType.ACROBATICS, Integer.valueOf(character[14]));
        skills.put(SkillType.FISHING, Integer.valueOf(character[34]));
        skills.put(SkillType.ALCHEMY, Integer.valueOf(character[39]));

        return skills;
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.FLATFILE;
    }
}
