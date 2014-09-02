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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
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
                    String name = character[getNameIndexFromLine(character)].split(",")[1];
                    long lastPlayed = 0;
                    boolean rewrite = false;
                    int lastPlayedIndex = getTimeIndexFromLine(character);
                    try {
                        lastPlayed = Long.parseLong(character[lastPlayedIndex].split(",")[1]) * Misc.TIME_CONVERSION_FACTOR;
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
                            character[lastPlayedIndex] = "LastLogin," + Long.toString(lastPlayed);
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
                	String[] character = line.split(":");
                    if (!worked && character[getNameIndexFromLine(character)].split(",")[1].equalsIgnoreCase(playerName)) {
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

                    if (!isPlayer(character, uuid.toString(), playerName)) {
                        writer.append(line).append("\r\n");
                    }
                    else {
                        // Otherwise write the new player information
                        writer.append("Player,").append(playerName).append(":");
                        for(SkillType skill : SkillType.getNonChildSkills()) {
                        	writer.append(skill.getName()).append("LVL,").append(profile.getSkillLevel(skill)).append(":");
                        	writer.append(skill.getName()).append("XP,").append(profile.getSkillXpLevel(skill)).append(":");
                        }
                        for(AbilityType ability : AbilityType.getAbilities()) {
                        	writer.append(ability.getUnprettyName()).append(",").append((int) profile.getAbilityDATS(ability)).append(":");
                        }
                        writer.append("LastLogin,").append(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR).append(":");
                        MobHealthbarType mobHealthbarType = profile.getMobHealthbarType();
                        writer.append("MobHealthBar,").append(mobHealthbarType == null ? Config.getInstance().getMobHealthbarDefault().toString() : mobHealthbarType.toString()).append(":");
                        writer.append("UUID,").append(uuid != null ? uuid.toString() : "NULL").append(":");
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
                out.append("Player,").append(playerName).append(":");
                for(SkillType skill : SkillType.getNonChildSkills()) {
                	out.append(skill.getName()).append("LVL,0:"); //Skill Levels
                	out.append(skill.getName()).append("XP,0:"); //Skill XP
                }
                for(String ability : AbilityType.getAbilitieNames()) {
                	out.append(ability).append(",0:"); //DATS
                }
                out.append("LastLogin,").append(String.valueOf(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR)).append(":"); // LastLogin
                out.append("MobHealthBar,").append(Config.getInstance().getMobHealthbarDefault().toString()).append(":"); // Mob Healthbar HUD
                out.append("UUID,").append(uuid != null ? uuid.toString() : "NULL").append(":"); // UUID

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
                    int uuidIndex = getUUIDIndexFromLine(character);
                    int nameIndex = getNameIndexFromLine(character);
                    if (uuidIndex == -1 && nameIndex == -1) {
                    	continue;
                    }
                    else if(uuidIndex != -1) {
	                    if ((uuid != null && (!character[uuidIndex].split(",")[1].equalsIgnoreCase(uuid.toString()) && !character[uuidIndex].split(",")[1].equalsIgnoreCase("NULL")))) {
	                        continue;
	                    }
                    }
                    else if(nameIndex != -1) {
                    	if (uuid == null && !character[nameIndex].split(",")[1].equalsIgnoreCase(playerName)) {
                    		continue;
                    	}
                    }

                    // Update playerName in database after name change
                    if (!character[nameIndex].split(",")[1].equalsIgnoreCase(playerName)) {
                        mcMMO.p.debug("Name change detected: " + character[nameIndex].split(",")[1] + " => " + playerName);
                        character[nameIndex] = "Player," + playerName;
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
                    if (!worked && character[getNameIndexFromLine(character)].split(",")[1].equalsIgnoreCase(userName)) {
                    	int uuidIndex = getUUIDIndexFromLine(character);
                        if (uuidIndex == -1) {
                            mcMMO.p.getLogger().severe("Could not update UUID for " + userName + "!");
                            mcMMO.p.getLogger().severe("Database entry is invalid.");
                            break;
                        }

                        line = line.replace(character[uuidIndex], "UUID," + uuid.toString());
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
                    int nameIndex = getNameIndexFromLine(character);
                    if (fetchedUUIDs.containsKey(character[nameIndex].split(",")[1])) {
                    	int uuidIndex = getUUIDIndexFromLine(character);
                    	if (uuidIndex == -1) {
                            mcMMO.p.getLogger().severe("Could not update UUID for " + character[nameIndex].split(",")[1] + "!");
                            mcMMO.p.getLogger().severe("Database entry is invalid.");
                            continue;
                        }

                        character[uuidIndex] = "UUID," + fetchedUUIDs.remove(character[nameIndex].split(",")[1]).toString();
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
                    int nameIndexFromLine = getNameIndexFromLine(character);
                    if(nameIndexFromLine != -1) {
                    	users.add(character[nameIndexFromLine].split(",")[1]);
                    }
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
        Map<SkillType, List<PlayerStat>> stats = new HashMap<SkillType, List<PlayerStat>>();

        BufferedReader in = null;
        String playerName = null;
        // Read from the FlatFile database and fill our arrays with information
        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    String[] data = line.split(":");
                    int nameIndex = getNameIndexFromLine(data);
                    playerName = data[nameIndex].split(",")[1];
                    int powerLevel = 0;

                    Map<SkillType, Integer> skills = getSkillMapFromLine(data);

                    List<PlayerStat> stat;
                    for(SkillType skill : SkillType.getNonChildSkills()) {
						stat = new ArrayList<PlayerStat>();
						powerLevel += putStat(stat, playerName, skills.containsKey(skill) ? skills.get(skill) : 0);
                    	stats.put(skill, stat);
                    }

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

        for(Map.Entry<SkillType, List<PlayerStat>> pair : stats.entrySet()) {
        	Collections.sort(pair.getValue(), c);
        	playerStatHash.put(pair.getKey(), pair.getValue());
        }
        Collections.sort(powerLevels, c);
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
                        int testIndex = getNameIndexFromLine(character);
                        
                        if(testIndex == -1) {
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

	                    	
	                        StringBuilder newLine = new StringBuilder(line);
	                        String[] newCharacter;
	                        boolean corrupted = false;
	                        
	                        // If they're valid, rewrite them to the file.
	                        if (character.length != 42) {
		
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
		                        newCharacter = newLine.toString().split(":");
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
	                        }
	                        else {
	                        	newCharacter = newLine.toString().split(":");
	                        }
	                        
	                        // Adding index names to make it expandable (ile123ile)
		                    newLine = new StringBuilder(addIndexNames(newCharacter));
	                        writer.append(newLine);
                        }
                        else {
                        	writer.append(line);
                        }
                        writer.append("\r\n");
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
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_FLATFILE_INDEX_NAMES);
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.CHANGE_SQL_COOLDOWN_NAMES);
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
    
    private static String addIndexNames(String[] character) {
    	String toRet = "";
    	toRet += "Player,"				+ character[0]  + ":";
    	toRet += "MININGLVL,"			+ character[1]  + ":";
    	toRet += "MININGXP,"			+ character[4]  + ":";
    	toRet += "WOODCUTTINGLVL,"		+ character[5]  + ":";
    	toRet += "WOODCUTTINGXP,"		+ character[6]  + ":";
    	toRet += "REPAIRLVL,"			+ character[7]  + ":";
    	toRet += "UNARMEDLVL,"			+ character[8]  + ":";
    	toRet += "HERBALISMLVL,"		+ character[9]  + ":";
    	toRet += "EXCAVATIONLVL,"		+ character[10] + ":";
    	toRet += "ARCHERYLVL,"			+ character[11] + ":";
    	toRet += "SWORDSLVL,"			+ character[12] + ":";
    	toRet += "AXESLVL,"				+ character[13] + ":";
    	toRet += "ACROBATICSLVL,"		+ character[14] + ":";
    	toRet += "REPAIRXP,"			+ character[15] + ":";
    	toRet += "UNARMEDXP,"			+ character[16] + ":";
    	toRet += "HERBALISMXP,"			+ character[17] + ":";
    	toRet += "EXCAVATIONXP,"		+ character[18] + ":";
    	toRet += "ARCHERYXP,"			+ character[19] + ":";
    	toRet += "SWORDSXP,"			+ character[20] + ":";
    	toRet += "AXESXP,"				+ character[21] + ":";
    	toRet += "ACROBATICSXP,"		+ character[22] + ":";
    	toRet += "TAMINGLVL,"			+ character[24] + ":";
    	toRet += "TAMINGXP,"			+ character[25] + ":";
    	toRet += "BERSERK,"				+ character[26] + ":";
    	toRet += "GIGA_DRILL_BREAKER,"	+ character[27] + ":";
    	toRet += "TREE_FELLER,"			+ character[28] + ":";
    	toRet += "GREEN_TERRA,"			+ character[29] + ":";
    	toRet += "SERRATED_STRIKES,"	+ character[30] + ":";
    	toRet += "SKULL_SPLITTER,"		+ character[31] + ":";
    	toRet += "SUPER_BREAKER,"		+ character[32] + ":";
    	toRet += "FISHINGLVL,"			+ character[34] + ":";
    	toRet += "FISHINGXP,"			+ character[35] + ":";
    	toRet += "BLAST_MINING,"		+ character[36] + ":";
    	toRet += "LastLogin,"			+ character[37] + ":";
    	toRet += "MobHealthBar,"		+ character[38] + ":";
    	toRet += "ALCHEMYLVL,"			+ character[39] + ":";
    	toRet += "ALCHEMYXP,"			+ character[40] + ":";
    	toRet += "UUID,"				+ character[41] + ":";
    	return toRet;
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

        int skillIndex;
        for(SkillType skill : SkillType.getNonChildSkills()) {
        	skillIndex = getIndexFromLine(skill.getName() + "XP", character);
        	skillsXp.put(skill, (float) Integer.valueOf(character[skillIndex].split(",")[1]));
        }

        int abilityIndex;
        for(AbilityType ability : AbilityType.getAbilities()) {
        	abilityIndex = getIndexFromLine(ability.getUnprettyName(), character);
        	skillsDATS.put(ability, Integer.valueOf(character[abilityIndex].split(",")[1]));
        }

        try {
            mobHealthbarType = MobHealthbarType.valueOf(character[getIndexFromLine("MobHealthBar", character)].split(",")[1]);
        }
        catch (Exception e) {
            mobHealthbarType = Config.getInstance().getMobHealthbarDefault();
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(character[getUUIDIndexFromLine(character)].split(",")[1]);
        }
        catch (Exception e) {
            uuid = null;
        }

        return new PlayerProfile(character[getNameIndexFromLine(character)].split(",")[1], uuid, skills, skillsXp, skillsDATS, mobHealthbarType);
    }

    private Map<SkillType, Integer> getSkillMapFromLine(String[] character) {
        Map<SkillType, Integer> skills = new HashMap<SkillType, Integer>();   // Skill & Level

        int skillIndex;
        for(SkillType skill : SkillType.getNonChildSkills()) {
        	skillIndex = getIndexFromLine(skill.getName() + "LVL", character);
        	if(skillIndex != -1) {
        		skills.put(skill, Integer.valueOf(character[skillIndex].split(",")[1]));
        	}
        }

        return skills;
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.FLATFILE;
    }

    @Override
    public void onDisable() { }
    
    private static int getNameIndexFromLine(String[] line) {
    	return getIndexFromLine("Player", line);
    }
    
    private static int getUUIDIndexFromLine(String[] line) {
    	int uuidIndex = getIndexFromLine("UUID", line);
		return uuidIndex;
    }
    
    private static int getTimeIndexFromLine(String[] line) {
    	return getIndexFromLine("LastLogin", line);
    }
    
    private static int getIndexFromLine(String string, String[] line) {
    	for(int i = 0; i < line.length; i++) {
    		if(line[i].split(",")[0].equalsIgnoreCase(string)) {
    			return i;
    		}
    	}
    	return -1;
    }
    
    private static boolean isPlayer(String[] character, String uuid, String playerName) {
    	int nameIndexFromLine = getNameIndexFromLine(character);
		if(nameIndexFromLine != -1) {
            if (character[nameIndexFromLine].split(",")[1].equalsIgnoreCase(playerName)) {
            	return true;
            }
    	}
		int uuidIndexFromLine = getUUIDIndexFromLine(character);
    	if(uuidIndexFromLine != -1) {
    		if(character[uuidIndexFromLine].split(",")[1].equalsIgnoreCase(playerName)) {
    			return true;
    		}
    	}
    	return false;
    }
}
