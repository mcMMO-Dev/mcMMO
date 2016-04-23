package com.gmail.nossr50.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.runnables.database.UUIDUpdateAsyncTask;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.StringUtils;

import org.apache.commons.lang.ArrayUtils;

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

        if (mcMMO.getUpgradeManager().shouldUpgrade(UpgradeType.ADD_UUIDS)) {
            new UUIDUpdateAsyncTask(mcMMO.p, getStoredUsers()).runTaskAsynchronously(mcMMO.p);
        }
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
                String line;

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
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
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
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    String name = character[USERNAME];
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
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
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
                String line;

                while ((line = in.readLine()) != null) {
                    // Write out the same file but when we get to the player we want to remove, we skip his line.
                    if (!worked && line.split(":")[USERNAME].equalsIgnoreCase(playerName)) {
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
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }

        Misc.profileCleanup(playerName);

        return worked;
    }

    public boolean saveUser(PlayerProfile profile) {
        String playerName = profile.getPlayerName();
        UUID uuid = profile.getUniqueId();

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
                    // Read the line in and copy it to the output if it's not the player we want to edit
                    String[] character = line.split(":");
                    if (!(uuid != null && character[UUID_INDEX].equalsIgnoreCase(uuid.toString())) && !character[USERNAME].equalsIgnoreCase(playerName)) {
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
                        writer.append(uuid != null ? uuid.toString() : "NULL").append(":");
                        writer.append(profile.getScoreboardTipsShown()).append(":");
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
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
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

    public void newUser(String playerName, UUID uuid) {
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
                out.append(uuid != null ? uuid.toString() : "NULL").append(":"); // UUID
                out.append("0:"); // Scoreboard tips shown
                // Add more in the same format as the line above

                out.newLine();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }
    }

    @Deprecated
    public PlayerProfile loadPlayerProfile(String playerName, boolean create) {
        return loadPlayerProfile(playerName, null, false);
    }

    public PlayerProfile loadPlayerProfile(UUID uuid) {
        return loadPlayerProfile("", uuid, false);
    }

    public PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean create) {
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

                    // Compare names because we don't have a valid uuid for that player even
                    // if input uuid is not null
                    if (character[UUID_INDEX].equalsIgnoreCase("NULL")) {
                        if (!character[USERNAME].equalsIgnoreCase(playerName)) {
                            continue;
                        }
                    }
                    // If input uuid is not null then we should compare uuids
                    else if ((uuid != null && !character[UUID_INDEX].equalsIgnoreCase(uuid.toString())) || (uuid == null && !character[USERNAME].equalsIgnoreCase(playerName))) {
                        continue;
                    }

                    // Update playerName in database after name change
                    if (!character[USERNAME].equalsIgnoreCase(playerName)) {
                        mcMMO.p.debug("Name change detected: " + character[USERNAME] + " => " + playerName);
                        character[USERNAME] = playerName;
                    }

                    return loadFromLine(character);
                }

                // Didn't find the player, create a new one
                if (create) {
                    if (uuid == null) {
                        newUser(playerName, uuid);
                        return new PlayerProfile(playerName, true);
                    }

                    newUser(playerName, uuid);
                    return new PlayerProfile(playerName, uuid, true);
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
                        // Ignore
                    }
                }
            }
        }

        // Return unloaded profile
        if (uuid == null) {
            return new PlayerProfile(playerName);
        }

        return new PlayerProfile(playerName, uuid);
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
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }
    }

    public boolean saveUserUUID(String userName, UUID uuid) {
        boolean worked = false;

        int i = 0;
        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    if (!worked && character[USERNAME].equalsIgnoreCase(userName)) {
                        if (character.length < 42) {
                            mcMMO.p.getLogger().severe("Could not update UUID for " + userName + "!");
                            mcMMO.p.getLogger().severe("Database entry is invalid.");
                            continue;
                        }

                        line = line.replace(character[UUID_INDEX], uuid.toString());
                        worked = true;
                    }

                    i++;
                    writer.append(line).append("\r\n");
                }

                out = new FileWriter(usersFilePath); // Write out the new file
                out.write(writer.toString());
            }
            catch (Exception e) {
                mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
            }
            finally {
                mcMMO.p.getLogger().info(i + " entries written while saving UUID for " + userName);
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }

        return worked;
    }

    public boolean saveUserUUIDs(Map<String, UUID> fetchedUUIDs) {
        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();
        int i = 0;

        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                while (((line = in.readLine()) != null)) {
                    String[] character = line.split(":");
                    if (!fetchedUUIDs.isEmpty() && fetchedUUIDs.containsKey(character[USERNAME])) {
                        if (character.length < 42) {
                            mcMMO.p.getLogger().severe("Could not update UUID for " + character[USERNAME] + "!");
                            mcMMO.p.getLogger().severe("Database entry is invalid.");
                            continue;
                        }

                        character[UUID_INDEX] = fetchedUUIDs.remove(character[USERNAME]).toString();
                        line = new StringBuilder(org.apache.commons.lang.StringUtils.join(character, ":")).append(":").toString();
                    }

                    i++;
                    writer.append(line).append("\r\n");
                }

                out = new FileWriter(usersFilePath); // Write out the new file
                out.write(writer.toString());
            }
            catch (Exception e) {
                mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
            }
            finally {
                mcMMO.p.getLogger().info(i + " entries written while saving UUID batch");
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }

        return true;
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
                    users.add(character[USERNAME]);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
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
                String line;

                while ((line = in.readLine()) != null) {
                    String[] data = line.split(":");
                    playerName = data[USERNAME];
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
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
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
                    String line;
                    HashSet<String> usernames = new HashSet<String>();
                    HashSet<String> players = new HashSet<String>();

                    while ((line = in.readLine()) != null) {
                        // Remove empty lines from the file
                        if (line.isEmpty()) {
                            continue;
                        }

                        // Length checks depend on last character being ':'
                        if (line.charAt(line.length() - 1) != ':') {
                            line = line.concat(":");
                        }
                        boolean updated = false;
                        String[] character = line.split(":");

                        // Prevent the same username from being present multiple times
                        if (!usernames.add(character[USERNAME])) {
                            character[USERNAME] = "_INVALID_OLD_USERNAME_'";
                            updated = true;
                            if (character.length < UUID_INDEX + 1 || character[UUID_INDEX].equals("NULL")) {
                                continue;
                            }
                        }

                        // Prevent the same player from being present multiple times
                        if (character.length >= 42 && (!character[UUID_INDEX].isEmpty() && !character[UUID_INDEX].equals("NULL") && !players.add(character[UUID_INDEX]))) {
                            continue;
                        }

                        if (character.length < 33) {
                            // Before Version 1.0 - Drop
                            mcMMO.p.getLogger().warning("Dropping malformed or before version 1.0 line from database - " + line);
                            continue;
                        }

                        String oldVersion = null;

                        if (character.length > 33 && !character[33].isEmpty()) {
                            // Removal of Spout Support
                            // Version 1.4.07-dev2
                            // commit 7bac0e2ca5143bce84dc160617fed97f0b1cb968
                            character[33] = "";
                            if (oldVersion == null) {
                                oldVersion = "1.4.07";
                            }
                            updated = true;
                        }

                        if (Config.getInstance().getTruncateSkills()) {
                            for (SkillType skill : SkillType.NON_CHILD_SKILLS) {
                                int index = getSkillIndex(skill);
                                if (index >= character.length) {
                                    continue;
                                }
                                int cap = Config.getInstance().getLevelCap(skill);
                                if (Integer.valueOf(character[index]) > cap) {
                                    mcMMO.p.getLogger().warning("Truncating " + skill.getName() + " to configured max level for player " + character[USERNAME]);
                                    character[index] = cap + "";
                                    updated = true;
                                }
                            }
                        }

                        // If they're valid, rewrite them to the file.
                        if (!updated && character.length == 43) {
                            writer.append(line).append("\r\n");
                            continue;
                        }

                        if (character.length <= 33) {
                            // Introduction of HUDType
                            // Version 1.1.06
                            // commit 78f79213cdd7190cd11ae54526f3b4ea42078e8a
                            character = Arrays.copyOf(character, character.length + 1);
                            character[character.length - 1] = "";
                            oldVersion = "1.1.06";
                        }

                        if (character.length <= 35) {
                            // Introduction of Fishing
                            // Version 1.2.00
                            // commit a814b57311bc7734661109f0e77fc8bab3a0bd29
                            character = Arrays.copyOf(character, character.length + 2);
                            character[character.length - 1] = "0";
                            character[character.length - 2] = "0";
                            if (oldVersion == null) {
                                oldVersion = "1.2.00";
                            }
                        }
                        if (character.length <= 36) {
                            // Introduction of Blast Mining cooldowns
                            // Version 1.3.00-dev
                            // commit fadbaf429d6b4764b8f1ad0efaa524a090e82ef5
                            character = Arrays.copyOf(character, character.length + 1);
                            character[character.length - 1] = "0";
                            if (oldVersion == null) {
                                oldVersion = "1.3.00";
                            }
                        }
                        if (character.length <= 37) {
                            // Making old-purge work with flatfile
                            // Version 1.4.00-dev
                            // commmit 3f6c07ba6aaf44e388cc3b882cac3d8f51d0ac28
                            // XXX Cannot create an OfflinePlayer at startup, use 0 and fix in purge
                            character = Arrays.copyOf(character, character.length + 1);
                            character[character.length - 1] = "0";
                            if (oldVersion == null) {
                                oldVersion = "1.4.00";
                            }
                        }
                        if (character.length <= 38) {
                            // Addition of mob healthbars
                            // Version 1.4.06
                            // commit da29185b7dc7e0d992754bba555576d48fa08aa6
                            character = Arrays.copyOf(character, character.length + 1);
                            character[character.length - 1] = Config.getInstance().getMobHealthbarDefault().toString();
                            if (oldVersion == null) {
                                oldVersion = "1.4.06";
                            }
                        }
                        if (character.length <= 39) {
                            // Addition of Alchemy
                            // Version 1.4.08
                            character = Arrays.copyOf(character, character.length + 2);
                            character[character.length - 1] = "0";
                            character[character.length - 2] = "0";
                            if (oldVersion == null) {
                                oldVersion = "1.4.08";
                            }
                        }
                        if (character.length <= 41) {
                            // Addition of UUIDs
                            // Version 1.5.01
                            // Add a value because otherwise it gets removed
                            character = Arrays.copyOf(character, character.length + 1);
                            character[character.length - 1] = "NULL";
                            if (oldVersion == null) {
                                oldVersion = "1.5.01";
                            }
                        }
                        if (character.length <= 42) {
                            // Addition of scoreboard tips auto disable
                            // Version 1.5.02
                            character = Arrays.copyOf(character, character.length + 1);
                            character[character.length - 1] = "0";
                            if (oldVersion == null) {
                                oldVersion = "1.5.02";
                            }
                        }

                        boolean corrupted = false;

                        for (int i = 0; i < character.length; i++) {
                            if (character[i].isEmpty() && !(i == 2 || i == 3 || i == 23 || i == 33 || i == 41)) {
                                corrupted = true;
                                if (i == 37) {
                                    character[i] = String.valueOf(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
                                }
                                else if (i == 38) {
                                    character[i] = Config.getInstance().getMobHealthbarDefault().toString();
                                }
                                else {
                                    character[i] = "0";
                                }
                            }

                            if (StringUtils.isInt(character[i]) && i == 38) {
                                corrupted = true;
                                character[i] = Config.getInstance().getMobHealthbarDefault().toString();
                            }

                            if (!StringUtils.isInt(character[i]) && !(i == 0 || i == 2 || i == 3 || i == 23 || i == 33 || i == 38 || i == 41)) {
                                corrupted = true;
                                character[i] = "0";
                            }
                        }

                        if (corrupted) {
                            mcMMO.p.debug("Updating corrupted database line for player " + character[USERNAME]);
                        }

                        if (oldVersion != null) {
                            mcMMO.p.debug("Updating database line from before version " + oldVersion + " for player " + character[USERNAME]);
                        }

                        updated |= corrupted;
                        updated |= oldVersion != null;

                        if (Config.getInstance().getTruncateSkills()) {
                            Map<SkillType, Integer> skills = getSkillMapFromLine(character);
                            for (SkillType skill : SkillType.NON_CHILD_SKILLS) {
                                int cap = Config.getInstance().getLevelCap(skill);
                                if (skills.get(skill) > cap) {
                                    updated = true;
                                }
                            }
                        }

                        if (updated) {
                            line = new StringBuilder(org.apache.commons.lang.StringUtils.join(character, ":")).append(":").toString();
                        }

                        writer.append(line).append("\r\n");
                    }

                    // Write the new file
                    out = new FileWriter(usersFilePath);
                    out.write(writer.toString());
                }
                catch (IOException e) {
                    mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (IOException e) {
                            // Ignore
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        }
                        catch (IOException e) {
                            // Ignore
                        }
                    }
                }
            }

            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_FISHING);
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_BLAST_MINING_COOLDOWN);
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_SQL_INDEXES);
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_MOB_HEALTHBARS);
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.DROP_SQL_PARTY_NAMES);
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.DROP_SPOUT);
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_ALCHEMY);
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

    private PlayerProfile loadFromLine(String[] character) {
        Map<SkillType, Integer>   skills     = getSkillMapFromLine(character);      // Skill levels
        Map<SkillType, Float>     skillsXp   = new EnumMap<SkillType, Float>(SkillType.class);     // Skill & XP
        Map<AbilityType, Integer> skillsDATS = new EnumMap<AbilityType, Integer>(AbilityType.class); // Ability & Cooldown
        MobHealthbarType mobHealthbarType;
        int scoreboardTipsShown;

        // TODO on updates, put new values in a try{} ?

        skillsXp.put(SkillType.TAMING, (float) Integer.valueOf(character[EXP_TAMING]));
        skillsXp.put(SkillType.MINING, (float) Integer.valueOf(character[EXP_MINING]));
        skillsXp.put(SkillType.REPAIR, (float) Integer.valueOf(character[EXP_REPAIR]));
        skillsXp.put(SkillType.WOODCUTTING, (float) Integer.valueOf(character[EXP_WOODCUTTING]));
        skillsXp.put(SkillType.UNARMED, (float) Integer.valueOf(character[EXP_UNARMED]));
        skillsXp.put(SkillType.HERBALISM, (float) Integer.valueOf(character[EXP_HERBALISM]));
        skillsXp.put(SkillType.EXCAVATION, (float) Integer.valueOf(character[EXP_EXCAVATION]));
        skillsXp.put(SkillType.ARCHERY, (float) Integer.valueOf(character[EXP_ARCHERY]));
        skillsXp.put(SkillType.SWORDS, (float) Integer.valueOf(character[EXP_SWORDS]));
        skillsXp.put(SkillType.AXES, (float) Integer.valueOf(character[EXP_AXES]));
        skillsXp.put(SkillType.ACROBATICS, (float) Integer.valueOf(character[EXP_ACROBATICS]));
        skillsXp.put(SkillType.FISHING, (float) Integer.valueOf(character[EXP_FISHING]));
        skillsXp.put(SkillType.ALCHEMY, (float) Integer.valueOf(character[EXP_ALCHEMY]));

        // Taming - Unused
        skillsDATS.put(AbilityType.SUPER_BREAKER, Integer.valueOf(character[COOLDOWN_SUPER_BREAKER]));
        // Repair - Unused
        skillsDATS.put(AbilityType.TREE_FELLER, Integer.valueOf(character[COOLDOWN_TREE_FELLER]));
        skillsDATS.put(AbilityType.BERSERK, Integer.valueOf(character[COOLDOWN_BERSERK]));
        skillsDATS.put(AbilityType.GREEN_TERRA, Integer.valueOf(character[COOLDOWN_GREEN_TERRA]));
        skillsDATS.put(AbilityType.GIGA_DRILL_BREAKER, Integer.valueOf(character[COOLDOWN_GIGA_DRILL_BREAKER]));
        // Archery - Unused
        skillsDATS.put(AbilityType.SERRATED_STRIKES, Integer.valueOf(character[COOLDOWN_SERRATED_STRIKES]));
        skillsDATS.put(AbilityType.SKULL_SPLITTER, Integer.valueOf(character[COOLDOWN_SKULL_SPLITTER]));
        // Acrobatics - Unused
        skillsDATS.put(AbilityType.BLAST_MINING, Integer.valueOf(character[COOLDOWN_BLAST_MINING]));

        try {
            mobHealthbarType = MobHealthbarType.valueOf(character[HEALTHBAR]);
        }
        catch (Exception e) {
            mobHealthbarType = Config.getInstance().getMobHealthbarDefault();
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(character[UUID_INDEX]);
        }
        catch (Exception e) {
            uuid = null;
        }

        try {
            scoreboardTipsShown = Integer.valueOf(character[SCOREBOARD_TIPS]);
        }
        catch (Exception e) {
            scoreboardTipsShown = 0;
        }

        return new PlayerProfile(character[USERNAME], uuid, skills, skillsXp, skillsDATS, mobHealthbarType, scoreboardTipsShown);
    }

    private Map<SkillType, Integer> getSkillMapFromLine(String[] character) {
        Map<SkillType, Integer> skills = new EnumMap<SkillType, Integer>(SkillType.class);   // Skill & Level

        skills.put(SkillType.TAMING, Integer.valueOf(character[SKILLS_TAMING]));
        skills.put(SkillType.MINING, Integer.valueOf(character[SKILLS_MINING]));
        skills.put(SkillType.REPAIR, Integer.valueOf(character[SKILLS_REPAIR]));
        skills.put(SkillType.WOODCUTTING, Integer.valueOf(character[SKILLS_WOODCUTTING]));
        skills.put(SkillType.UNARMED, Integer.valueOf(character[SKILLS_UNARMED]));
        skills.put(SkillType.HERBALISM, Integer.valueOf(character[SKILLS_HERBALISM]));
        skills.put(SkillType.EXCAVATION, Integer.valueOf(character[SKILLS_EXCAVATION]));
        skills.put(SkillType.ARCHERY, Integer.valueOf(character[SKILLS_ARCHERY]));
        skills.put(SkillType.SWORDS, Integer.valueOf(character[SKILLS_SWORDS]));
        skills.put(SkillType.AXES, Integer.valueOf(character[SKILLS_AXES]));
        skills.put(SkillType.ACROBATICS, Integer.valueOf(character[SKILLS_ACROBATICS]));
        skills.put(SkillType.FISHING, Integer.valueOf(character[SKILLS_FISHING]));
        skills.put(SkillType.ALCHEMY, Integer.valueOf(character[SKILLS_ALCHEMY]));

        return skills;
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.FLATFILE;
    }

    @Override
    public void onDisable() { }

    private int getSkillIndex(SkillType skill) {
        switch (skill) {
            case ACROBATICS:
                return SKILLS_ACROBATICS;
            case ALCHEMY:
                return SKILLS_ALCHEMY;
            case ARCHERY:
                return SKILLS_ARCHERY;
            case AXES:
                return SKILLS_AXES;
            case EXCAVATION:
                return SKILLS_EXCAVATION;
            case FISHING:
                return SKILLS_FISHING;
            case HERBALISM:
                return SKILLS_HERBALISM;
            case MINING:
                return SKILLS_MINING;
            case REPAIR:
                return SKILLS_REPAIR;
            case SWORDS:
                return SKILLS_SWORDS;
            case TAMING:
                return SKILLS_TAMING;
            case UNARMED:
                return SKILLS_UNARMED;
            case WOODCUTTING:
                return SKILLS_WOODCUTTING;
            default:
                throw new RuntimeException("Primary Skills only");
            
        }
    }
    
    public static int USERNAME = 0;
    public static int SKILLS_MINING = 1;
    public static int EXP_MINING = 4;
    public static int SKILLS_WOODCUTTING = 5;
    public static int EXP_WOODCUTTING = 6;
    public static int SKILLS_REPAIR = 7;
    public static int SKILLS_UNARMED = 8;
    public static int SKILLS_HERBALISM = 9;
    public static int SKILLS_EXCAVATION = 10;
    public static int SKILLS_ARCHERY = 11;
    public static int SKILLS_SWORDS = 12;
    public static int SKILLS_AXES = 13;
    public static int SKILLS_ACROBATICS = 14;
    public static int EXP_REPAIR = 15;
    public static int EXP_UNARMED = 16;
    public static int EXP_HERBALISM = 17;
    public static int EXP_EXCAVATION = 18;
    public static int EXP_ARCHERY = 19;
    public static int EXP_SWORDS = 20;
    public static int EXP_AXES = 21;
    public static int EXP_ACROBATICS = 22;
    public static int SKILLS_TAMING = 24;
    public static int EXP_TAMING = 25;
    public static int COOLDOWN_BERSERK = 26;
    public static int COOLDOWN_GIGA_DRILL_BREAKER = 27;
    public static int COOLDOWN_TREE_FELLER = 28;
    public static int COOLDOWN_GREEN_TERRA = 29;
    public static int COOLDOWN_SERRATED_STRIKES = 30;
    public static int COOLDOWN_SKULL_SPLITTER = 31;
    public static int COOLDOWN_SUPER_BREAKER = 32;
    public static int SKILLS_FISHING = 34;
    public static int EXP_FISHING = 35;
    public static int COOLDOWN_BLAST_MINING = 36;
    public static int LAST_LOGIN = 37;
    public static int HEALTHBAR = 38;
    public static int SKILLS_ALCHEMY = 39;
    public static int EXP_ALCHEMY = 40;
    public static int UUID_INDEX = 41;
    public static int SCOREBOARD_TIPS = 42;

    public void resetMobHealthSettings() {
        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    // Remove empty lines from the file
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] character = line.split(":");
                    
                    character[HEALTHBAR] = Config.getInstance().getMobHealthbarDefault().toString();
                    
                    line = new StringBuilder(org.apache.commons.lang.StringUtils.join(character, ":")).append(":").toString();

                    writer.append(line).append("\r\n");
                }

                // Write the new file
                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
            }
            catch (IOException e) {
                mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }
    }
}
