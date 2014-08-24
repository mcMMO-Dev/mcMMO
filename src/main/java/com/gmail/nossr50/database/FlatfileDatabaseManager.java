package com.gmail.nossr50.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
                    if (!character[41].equalsIgnoreCase(uuid.toString()) && !character[0].equalsIgnoreCase(playerName)) {
                        writer.append(line).append("\r\n");
                    }
                    else {
                        // Otherwise write the new player information
                        writer.append(playerName).append(":");
                        writer.append(profile.getSkillLevel(SkillType.mining)).append(":");
                        writer.append(":");
                        writer.append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.mining)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.woodcutting)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.woodcutting)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.repair)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.unarmed)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.herbalism)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.excavation)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.archery)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.swords)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.axes)).append(":");
                        writer.append(profile.getSkillLevel(SkillType.acrobatics)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.repair)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.unarmed)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.herbalism)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.excavation)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.archery)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.swords)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.axes)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.acrobatics)).append(":");
                        writer.append(":");
                        writer.append(profile.getSkillLevel(SkillType.taming)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.taming)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.berserk)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.gigaDrillBreaker)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.treeFeller)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.greenTerra)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.serratedStrikes)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.skullSplitter)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.superBreaker)).append(":");
                        writer.append(":");
                        writer.append(profile.getSkillLevel(SkillType.fishing)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.fishing)).append(":");
                        writer.append((int) profile.getAbilityDATS(AbilityType.blastMining)).append(":");
                        writer.append(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR).append(":");
                        MobHealthbarType mobHealthbarType = profile.getMobHealthbarType();
                        writer.append(mobHealthbarType == null ? Config.getInstance().getMobHealthbarDefault().toString() : mobHealthbarType.toString()).append(":");
                        writer.append(profile.getSkillLevel(SkillType.alchemy)).append(":");
                        writer.append(profile.getSkillXpLevel(SkillType.alchemy)).append(":");
                        writer.append(uuid.toString()).append(":");
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

        for (SkillType skill : SkillType.getNonChildSkills()) {
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

                    if ((uuid != null && (!character[41].equalsIgnoreCase(uuid.toString()) && !character[41].equalsIgnoreCase("NULL"))) || (uuid == null && !character[0].equalsIgnoreCase(playerName))) {
                        continue;
                    }

                    // Update playerName in database after name change
                    if (!character[0].equalsIgnoreCase(playerName)) {
                        mcMMO.p.debug("Name change detected: " + character[0] + " => " + playerName);
                        character[0] = playerName;
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
                    if (!worked && character[0].equalsIgnoreCase(userName)) {
                        if (character.length < 42) {
                            mcMMO.p.getLogger().severe("Could not update UUID for " + userName + "!");
                            mcMMO.p.getLogger().severe("Database entry is invalid.");
                            break;
                        }

                        line = line.replace(character[41], uuid.toString());
                        worked = true;
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

        return worked;
    }

    public boolean saveUserUUIDs(Map<String, UUID> fetchedUUIDs) {
        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                while (((line = in.readLine()) != null) && !fetchedUUIDs.isEmpty()) {
                    String[] character = line.split(":");
                    if (fetchedUUIDs.containsKey(character[0])) {
                        if (character.length < 42) {
                            mcMMO.p.getLogger().severe("Could not update UUID for " + character[0] + "!");
                            mcMMO.p.getLogger().severe("Database entry is invalid.");
                            continue;
                        }

                        character[41] = fetchedUUIDs.remove(character[0]).toString();
                        line = new StringBuilder(org.apache.commons.lang.StringUtils.join(character, ":")).append(":").toString();
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
                    users.add(character[0]);
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
                    playerName = data[0];
                    int powerLevel = 0;

                    Map<SkillType, Integer> skills = getSkillMapFromLine(data);

                    powerLevel += putStat(acrobatics, playerName, skills.get(SkillType.acrobatics));
                    powerLevel += putStat(alchemy, playerName, skills.get(SkillType.alchemy));
                    powerLevel += putStat(archery, playerName, skills.get(SkillType.archery));
                    powerLevel += putStat(axes, playerName, skills.get(SkillType.axes));
                    powerLevel += putStat(excavation, playerName, skills.get(SkillType.excavation));
                    powerLevel += putStat(fishing, playerName, skills.get(SkillType.fishing));
                    powerLevel += putStat(herbalism, playerName, skills.get(SkillType.herbalism));
                    powerLevel += putStat(mining, playerName, skills.get(SkillType.mining));
                    powerLevel += putStat(repair, playerName, skills.get(SkillType.repair));
                    powerLevel += putStat(swords, playerName, skills.get(SkillType.swords));
                    powerLevel += putStat(taming, playerName, skills.get(SkillType.taming));
                    powerLevel += putStat(unarmed, playerName, skills.get(SkillType.unarmed));
                    powerLevel += putStat(woodcutting, playerName, skills.get(SkillType.woodcutting));

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

        playerStatHash.put(SkillType.mining, mining);
        playerStatHash.put(SkillType.woodcutting, woodcutting);
        playerStatHash.put(SkillType.repair, repair);
        playerStatHash.put(SkillType.unarmed, unarmed);
        playerStatHash.put(SkillType.herbalism, herbalism);
        playerStatHash.put(SkillType.excavation, excavation);
        playerStatHash.put(SkillType.archery, archery);
        playerStatHash.put(SkillType.swords, swords);
        playerStatHash.put(SkillType.axes, axes);
        playerStatHash.put(SkillType.acrobatics, acrobatics);
        playerStatHash.put(SkillType.taming, taming);
        playerStatHash.put(SkillType.fishing, fishing);
        playerStatHash.put(SkillType.alchemy, alchemy);
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
                        String[] character = line.split(":");

                        // Prevent the same username from being present multiple times
                        if (!usernames.add(character[0])) {
                            continue;
                        }

                        // Prevent the same player from being present multiple times
                        if (character.length == 42 && (!character[41].isEmpty() && !players.add(character[41]))) {
                            continue;
                        }

                        if (character.length < 33) {
                            // Before Version 1.0 - Drop
                            mcMMO.p.getLogger().warning("Dropping malformed or before version 1.0 line from database - " + line);
                            continue;
                        }

                        String oldVersion = null;

                        if (character.length <= 33) {
                            // Introduction of HUDType
                            // Version 1.1.06
                            // commit 78f79213cdd7190cd11ae54526f3b4ea42078e8a
                            line = line.concat(" :");
                            character = line.split(":");
                            oldVersion = "1.1.06";
                        }

                        if (!character[33].isEmpty()) {
                            // Removal of Spout Support
                            // Version 1.4.07-dev2
                            // commit 7bac0e2ca5143bce84dc160617fed97f0b1cb968
                            line = line.replace(character[33], "");
                            if (oldVersion == null) {
                                oldVersion = "1.4.07";
                            }
                        }

                        // If they're valid, rewrite them to the file.
                        if (character.length == 42) {
                            writer.append(line).append("\r\n");
                            continue;
                        }

                        StringBuilder newLine = new StringBuilder(line);

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
                        if (character.length <= 39) {
                            // Addition of Alchemy
                            // Version 1.4.08
                            newLine.append("0").append(":");
                            newLine.append("0").append(":");
                            if (oldVersion == null) {
                                oldVersion = "1.4.08";
                            }
                        }
                        if (character.length <= 41) {
                            // Addition of UUIDs
                            // Version 1.5.01
                            // Add a value because otherwise it gets removed
                            newLine.append("NULL:");
                            if (oldVersion == null) {
                                oldVersion = "1.5.01";
                            }
                        }

                        // Remove any blanks that shouldn't be there, and validate the other fields
                        String[] newCharacter = newLine.toString().split(":");
                        boolean corrupted = false;

                        for (int i = 0; i < newCharacter.length; i++) {
                            if (newCharacter[i].isEmpty() && !(i == 2 || i == 3 || i == 23 || i == 33 || i == 41)) {
                                corrupted = true;

                                if (newCharacter.length != 42) {
                                    newCharacter = (String[]) ArrayUtils.remove(newCharacter, i);
                                }
                                else {
                                    if (i == 37) {
                                        newCharacter[i] = String.valueOf(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
                                    }
                                    else if (i == 38) {
                                        newCharacter[i] = Config.getInstance().getMobHealthbarDefault().toString();
                                    }
                                    else {
                                        newCharacter[i] = "0";
                                    }
                                }
                            }

                            if (StringUtils.isInt(newCharacter[i]) && i == 38) {
                                corrupted = true;
                                newCharacter[i] = Config.getInstance().getMobHealthbarDefault().toString();
                            }

                            if (!StringUtils.isInt(newCharacter[i]) && !(i == 0 || i == 2 || i == 3 || i == 23 || i == 33 || i == 38 || i == 41)) {
                                corrupted = true;
                                newCharacter[i] = "0";
                            }
                        }

                        if (corrupted) {
                            mcMMO.p.debug("Updating corrupted database line for player " + newCharacter[0]);
                        }

                        if (oldVersion != null) {
                            mcMMO.p.debug("Updating database line from before version " + oldVersion + " for player " + character[0]);
                        }

                        if (corrupted || oldVersion != null) {
                            newLine = new StringBuilder(org.apache.commons.lang.StringUtils.join(newCharacter, ":"));
                            newLine = newLine.append(":");
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
        Map<SkillType, Float>     skillsXp   = new HashMap<SkillType, Float>();     // Skill & XP
        Map<AbilityType, Integer> skillsDATS = new HashMap<AbilityType, Integer>(); // Ability & Cooldown
        MobHealthbarType mobHealthbarType;

        // TODO on updates, put new values in a try{} ?

        skillsXp.put(SkillType.taming, (float) Integer.valueOf(character[25]));
        skillsXp.put(SkillType.mining, (float) Integer.valueOf(character[4]));
        skillsXp.put(SkillType.repair, (float) Integer.valueOf(character[15]));
        skillsXp.put(SkillType.woodcutting, (float) Integer.valueOf(character[6]));
        skillsXp.put(SkillType.unarmed, (float) Integer.valueOf(character[16]));
        skillsXp.put(SkillType.herbalism, (float) Integer.valueOf(character[17]));
        skillsXp.put(SkillType.excavation, (float) Integer.valueOf(character[18]));
        skillsXp.put(SkillType.archery, (float) Integer.valueOf(character[19]));
        skillsXp.put(SkillType.swords, (float) Integer.valueOf(character[20]));
        skillsXp.put(SkillType.axes, (float) Integer.valueOf(character[21]));
        skillsXp.put(SkillType.acrobatics, (float) Integer.valueOf(character[22]));
        skillsXp.put(SkillType.fishing, (float) Integer.valueOf(character[35]));
        skillsXp.put(SkillType.alchemy, (float) Integer.valueOf(character[40]));

        // Taming - Unused
        skillsDATS.put(AbilityType.superBreaker, Integer.valueOf(character[32]));
        // Repair - Unused
        skillsDATS.put(AbilityType.treeFeller, Integer.valueOf(character[28]));
        skillsDATS.put(AbilityType.berserk, Integer.valueOf(character[26]));
        skillsDATS.put(AbilityType.greenTerra, Integer.valueOf(character[29]));
        skillsDATS.put(AbilityType.gigaDrillBreaker, Integer.valueOf(character[27]));
        // Archery - Unused
        skillsDATS.put(AbilityType.serratedStrikes, Integer.valueOf(character[30]));
        skillsDATS.put(AbilityType.skullSplitter, Integer.valueOf(character[31]));
        // Acrobatics - Unused
        skillsDATS.put(AbilityType.blastMining, Integer.valueOf(character[36]));

        try {
            mobHealthbarType = MobHealthbarType.valueOf(character[38]);
        }
        catch (Exception e) {
            mobHealthbarType = Config.getInstance().getMobHealthbarDefault();
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(character[41]);
        }
        catch (Exception e) {
            uuid = null;
        }

        return new PlayerProfile(character[0], uuid, skills, skillsXp, skillsDATS, mobHealthbarType);
    }

    private Map<SkillType, Integer> getSkillMapFromLine(String[] character) {
        Map<SkillType, Integer> skills = new HashMap<SkillType, Integer>();   // Skill & Level

        skills.put(SkillType.taming, Integer.valueOf(character[24]));
        skills.put(SkillType.mining, Integer.valueOf(character[1]));
        skills.put(SkillType.repair, Integer.valueOf(character[7]));
        skills.put(SkillType.woodcutting, Integer.valueOf(character[5]));
        skills.put(SkillType.unarmed, Integer.valueOf(character[8]));
        skills.put(SkillType.herbalism, Integer.valueOf(character[9]));
        skills.put(SkillType.excavation, Integer.valueOf(character[10]));
        skills.put(SkillType.archery, Integer.valueOf(character[11]));
        skills.put(SkillType.swords, Integer.valueOf(character[12]));
        skills.put(SkillType.axes, Integer.valueOf(character[13]));
        skills.put(SkillType.acrobatics, Integer.valueOf(character[14]));
        skills.put(SkillType.fishing, Integer.valueOf(character[34]));
        skills.put(SkillType.alchemy, Integer.valueOf(character[39]));

        return skills;
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.FLATFILE;
    }

    @Override
    public void onDisable() { }
}
