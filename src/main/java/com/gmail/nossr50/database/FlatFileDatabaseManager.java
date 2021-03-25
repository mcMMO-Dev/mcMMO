package com.gmail.nossr50.database;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.player.UniqueDataType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

public final class FlatFileDatabaseManager implements DatabaseManager {
    private final HashMap<PrimarySkillType, List<PlayerStat>> playerStatHash = new HashMap<>();
    private final List<PlayerStat> powerLevels = new ArrayList<>();
    private long lastUpdate = 0;

    private final long UPDATE_WAIT_TIME = 600000L; // 10 minutes
    private final File usersFile;
    private static final Object fileWritingLock = new Object();

    public static int USERNAME_INDEX = 0;
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
    public static int COOLDOWN_CHIMAERA_WING = 43;

    public static int DATA_ENTRY_COUNT = COOLDOWN_CHIMAERA_WING + 1; //Update this everytime new data is added

    protected FlatFileDatabaseManager() {
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
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    Map<PrimarySkillType, Integer> skills = getSkillMapFromLine(character);

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
                    String name = character[USERNAME_INDEX];
                    long lastPlayed = 0;
                    boolean rewrite = false;
                    try {
                        lastPlayed = Long.parseLong(character[37]) * Misc.TIME_CONVERSION_FACTOR;
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
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

    public boolean removeUser(String playerName, UUID uuid) {
        //NOTE: UUID is unused for FlatFile for this interface implementation
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
                    if (!worked && line.split(":")[USERNAME_INDEX].equalsIgnoreCase(playerName)) {
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

    @Override
    public void cleanupUser(UUID uuid) {
        //Not used in FlatFile
    }

    public boolean saveUser(PlayerProfile profile) {
        String playerName = profile.getPlayerName();
        UUID uuid = profile.getUniqueId();

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();
        boolean corruptDataFound = false;

        synchronized (fileWritingLock) {
            try {
                // Open the file
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                boolean wroteUser = false;
                // While not at the end of the file
                while ((line = in.readLine()) != null) {
                    //Check for incomplete or corrupted data
                    if(!line.contains(":")) {

                        if(!corruptDataFound) {
                            mcMMO.p.getLogger().severe("mcMMO found some unexpected or corrupted data in mcmmo.users and is removing it, it is possible some data has been lost.");
                            corruptDataFound = true;
                        }

                        continue;
                    }

                    String[] splitData = line.split(":");

                    //This would be rare, but check the splitData for having enough entries to contain a username
                    if(splitData.length < USERNAME_INDEX) { //UUID have been in mcMMO DB for a very long time so any user without
                        //Something is wrong if we don't have enough split data to have an entry for a username

                        if(!corruptDataFound) {
                            mcMMO.p.getLogger().severe("mcMMO found some unexpected or corrupted data in mcmmo.users and is removing it, it is possible some data has been lost.");
                            corruptDataFound = true;
                        }

                        continue;
                    }

                    if (!(uuid != null
                                    && splitData[UUID_INDEX].equalsIgnoreCase(uuid.toString()))
                                    && !splitData[USERNAME_INDEX].equalsIgnoreCase(playerName)) {
                        writer.append(line).append("\r\n"); //Not the user so write it to file and move on
                    } else {
                        //User found
                        writeUserToLine(profile, playerName, uuid, writer);
                        wroteUser = true;
                    }
                }

                /*
                 * If we couldn't find the user in the DB we need to add him
                 */
                if(!wroteUser) {
                    writeUserToLine(profile, playerName, uuid, writer);
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

    private void writeUserToLine(PlayerProfile profile, String playerName, @Nullable UUID uuid, StringBuilder writer) {
        writer.append(playerName).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.MINING)).append(":");
        writer.append(":");
        writer.append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.MINING)).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.WOODCUTTING)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.WOODCUTTING)).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.REPAIR)).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.UNARMED)).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.HERBALISM)).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.EXCAVATION)).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.ARCHERY)).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.SWORDS)).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.AXES)).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.ACROBATICS)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.REPAIR)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.UNARMED)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.HERBALISM)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.EXCAVATION)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.ARCHERY)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.SWORDS)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.AXES)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.ACROBATICS)).append(":");
        writer.append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.TAMING)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.TAMING)).append(":");
        writer.append((int) profile.getAbilityDATS(SuperAbilityType.BERSERK)).append(":");
        writer.append((int) profile.getAbilityDATS(SuperAbilityType.GIGA_DRILL_BREAKER)).append(":");
        writer.append((int) profile.getAbilityDATS(SuperAbilityType.TREE_FELLER)).append(":");
        writer.append((int) profile.getAbilityDATS(SuperAbilityType.GREEN_TERRA)).append(":");
        writer.append((int) profile.getAbilityDATS(SuperAbilityType.SERRATED_STRIKES)).append(":");
        writer.append((int) profile.getAbilityDATS(SuperAbilityType.SKULL_SPLITTER)).append(":");
        writer.append((int) profile.getAbilityDATS(SuperAbilityType.SUPER_BREAKER)).append(":");
        writer.append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.FISHING)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.FISHING)).append(":");
        writer.append((int) profile.getAbilityDATS(SuperAbilityType.BLAST_MINING)).append(":");
        writer.append(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR).append(":");
        MobHealthbarType mobHealthbarType = profile.getMobHealthbarType();
        writer.append(mobHealthbarType == null ? Config.getInstance().getMobHealthbarDefault().toString() : mobHealthbarType.toString()).append(":");
        writer.append(profile.getSkillLevel(PrimarySkillType.ALCHEMY)).append(":");
        writer.append(profile.getSkillXpLevel(PrimarySkillType.ALCHEMY)).append(":");
        writer.append(uuid != null ? uuid.toString() : "NULL").append(":");
        writer.append(profile.getScoreboardTipsShown()).append(":");
        writer.append(profile.getUniqueData(UniqueDataType.CHIMAERA_WING_DATS)).append(":");
        writer.append("\r\n");
    }

    public @NotNull List<PlayerStat> readLeaderboard(@Nullable PrimarySkillType skill, int pageNumber, int statsPerPage) throws InvalidSkillException {
        //Fix for a plugin that people are using that is throwing SQL errors
        if(skill != null && skill.isChildSkill()) {
            mcMMO.p.getLogger().severe("A plugin hooking into mcMMO is being naughty with our database commands, update all plugins that hook into mcMMO and contact their devs!");
            throw new InvalidSkillException("A plugin hooking into mcMMO that you are using is attempting to read leaderboard skills for child skills, child skills do not have leaderboards! This is NOT an mcMMO error!");
        }

        updateLeaderboards();
        List<PlayerStat> statsList = skill == null ? powerLevels : playerStatHash.get(skill);
        int fromIndex = (Math.max(pageNumber, 1) - 1) * statsPerPage;

        return statsList.subList(Math.min(fromIndex, statsList.size()), Math.min(fromIndex + statsPerPage, statsList.size()));
    }

    public Map<PrimarySkillType, Integer> readRank(String playerName) {
        updateLeaderboards();

        Map<PrimarySkillType, Integer> skills = new HashMap<>();

        for (PrimarySkillType skill : PrimarySkillType.NON_CHILD_SKILLS) {
            skills.put(skill, getPlayerRank(playerName, playerStatHash.get(skill)));
        }

        skills.put(null, getPlayerRank(playerName, powerLevels));

        return skills;
    }

    public @NotNull PlayerProfile newUser(@NotNull Player player) {
        newUser(player.getName(), player.getUniqueId());
        return new PlayerProfile(player.getName(), player.getUniqueId(), true);
    }

    public void newUser(String playerName, UUID uuid) {
        BufferedWriter out = null;
        synchronized (fileWritingLock) {
            try {
                // Open the file to write the player
                out = new BufferedWriter(new FileWriter(mcMMO.getUsersFilePath(), true));

                String startingLevel = AdvancedConfig.getInstance().getStartingLevel() + ":";

                // Add the player to the end
                out.append(playerName).append(":");
                out.append(startingLevel); // Mining
                out.append(":");
                out.append(":");
                out.append("0:"); // Xp
                out.append(startingLevel); // Woodcutting
                out.append("0:"); // WoodCuttingXp
                out.append(startingLevel); // Repair
                out.append(startingLevel); // Unarmed
                out.append(startingLevel); // Herbalism
                out.append(startingLevel); // Excavation
                out.append(startingLevel); // Archery
                out.append(startingLevel); // Swords
                out.append(startingLevel); // Axes
                out.append(startingLevel); // Acrobatics
                out.append("0:"); // RepairXp
                out.append("0:"); // UnarmedXp
                out.append("0:"); // HerbalismXp
                out.append("0:"); // ExcavationXp
                out.append("0:"); // ArcheryXp
                out.append("0:"); // SwordsXp
                out.append("0:"); // AxesXp
                out.append("0:"); // AcrobaticsXp
                out.append(":");
                out.append(startingLevel); // Taming
                out.append("0:"); // TamingXp
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append("0:"); // DATS
                out.append(":");
                out.append(startingLevel); // Fishing
                out.append("0:"); // FishingXp
                out.append("0:"); // Blast Mining
                out.append(String.valueOf(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR)).append(":"); // LastLogin
                out.append(Config.getInstance().getMobHealthbarDefault().toString()).append(":"); // Mob Healthbar HUD
                out.append(startingLevel); // Alchemy
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

    public @NotNull PlayerProfile loadPlayerProfile(@NotNull String playerName) {
        return loadPlayerByName(playerName);
    }

    public @NotNull PlayerProfile loadPlayerProfile(@NotNull UUID uuid, @Nullable String playerName) {
        return loadPlayerByUUID(uuid, playerName);
    }

    private @NotNull PlayerProfile loadPlayerByUUID(@NotNull UUID uuid, @Nullable String playerName) {
        BufferedReader in = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    // Find if the line contains the player we want.
                    String[] rawSplitData = line.split(":");

                    /* Don't read corrupt data */
                    if(rawSplitData.length < (UUID_INDEX + 1)) {
                        continue;
                    }

                    /* Does this entry have a UUID? */
                    if (rawSplitData[UUID_INDEX].equalsIgnoreCase("NULL")
                            || rawSplitData[UUID_INDEX].isEmpty()
                            || rawSplitData[UUID_INDEX].equalsIgnoreCase("")) {
                        continue; //No UUID entry found for this data in the DB, go to next entry
                    }

                    // Compare provided UUID to DB
                    if (!rawSplitData[UUID_INDEX].equalsIgnoreCase(uuid.toString())) {
                        continue; //Doesn't match, go to the next entry
                    }

                    /*
                     * UUID Matched!
                     * Making it this far means the current data line is considered a match
                     */


                    /* Check for nickname changes and update since we are here anyways */
                    if (!rawSplitData[USERNAME_INDEX].equalsIgnoreCase(playerName)) {
                        //mcMMO.p.getLogger().info("Name updated for player: " + rawSplitData[USERNAME_INDEX] + " => " + playerName);
                        rawSplitData[USERNAME_INDEX] = playerName;
                    }

                    return loadFromLine(rawSplitData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // I have no idea why it's necessary to inline tryClose() here, but it removes
                // a resource leak warning, and I'm trusting the compiler on this one.
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }

        /*
         * No match was found in the file
         */

        return grabUnloadedProfile(uuid, playerName); //Create an empty new profile and return
    }

    private @NotNull PlayerProfile loadPlayerByName(@NotNull String playerName) {
        BufferedReader in = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    // Find if the line contains the player we want.
                    String[] rawSplitData = line.split(":");

                    /* Don't read corrupt data */
                    if(rawSplitData.length < (USERNAME_INDEX + 1)) {
                        continue;
                    }

                    //If we couldn't find anyone
                    if(playerName.equalsIgnoreCase(rawSplitData[USERNAME_INDEX])) {
                        return loadFromLine(rawSplitData);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // I have no idea why it's necessary to inline tryClose() here, but it removes
                // a resource leak warning, and I'm trusting the compiler on this one.
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }

        //Return a new blank profile
        return new PlayerProfile(playerName, null);
    }

    private @NotNull PlayerProfile grabUnloadedProfile(@NotNull UUID uuid, @Nullable String playerName) {
        if(playerName == null) {
            playerName = ""; //No name for you boy!
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
                    if (!worked && character[USERNAME_INDEX].equalsIgnoreCase(userName)) {
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
                    if (!fetchedUUIDs.isEmpty() && fetchedUUIDs.containsKey(character[USERNAME_INDEX])) {
                        if (character.length < 42) {
                            mcMMO.p.getLogger().severe("Could not update UUID for " + character[USERNAME_INDEX] + "!");
                            mcMMO.p.getLogger().severe("Database entry is invalid.");
                            continue;
                        }

                        character[UUID_INDEX] = fetchedUUIDs.remove(character[USERNAME_INDEX]).toString();
                        line = org.apache.commons.lang.StringUtils.join(character, ":") + ":";
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
        ArrayList<String> users = new ArrayList<>();
        BufferedReader in = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    users.add(character[USERNAME_INDEX]);
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
        List<PlayerStat> mining = new ArrayList<>();
        List<PlayerStat> woodcutting = new ArrayList<>();
        List<PlayerStat> herbalism = new ArrayList<>();
        List<PlayerStat> excavation = new ArrayList<>();
        List<PlayerStat> acrobatics = new ArrayList<>();
        List<PlayerStat> repair = new ArrayList<>();
        List<PlayerStat> swords = new ArrayList<>();
        List<PlayerStat> axes = new ArrayList<>();
        List<PlayerStat> archery = new ArrayList<>();
        List<PlayerStat> unarmed = new ArrayList<>();
        List<PlayerStat> taming = new ArrayList<>();
        List<PlayerStat> fishing = new ArrayList<>();
        List<PlayerStat> alchemy = new ArrayList<>();

        BufferedReader in = null;
        String playerName = null;
        // Read from the FlatFile database and fill our arrays with information
        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    String[] data = line.split(":");
                    playerName = data[USERNAME_INDEX];
                    int powerLevel = 0;

                    Map<PrimarySkillType, Integer> skills = getSkillMapFromLine(data);

                    powerLevel += putStat(acrobatics, playerName, skills.get(PrimarySkillType.ACROBATICS));
                    powerLevel += putStat(alchemy, playerName, skills.get(PrimarySkillType.ALCHEMY));
                    powerLevel += putStat(archery, playerName, skills.get(PrimarySkillType.ARCHERY));
                    powerLevel += putStat(axes, playerName, skills.get(PrimarySkillType.AXES));
                    powerLevel += putStat(excavation, playerName, skills.get(PrimarySkillType.EXCAVATION));
                    powerLevel += putStat(fishing, playerName, skills.get(PrimarySkillType.FISHING));
                    powerLevel += putStat(herbalism, playerName, skills.get(PrimarySkillType.HERBALISM));
                    powerLevel += putStat(mining, playerName, skills.get(PrimarySkillType.MINING));
                    powerLevel += putStat(repair, playerName, skills.get(PrimarySkillType.REPAIR));
                    powerLevel += putStat(swords, playerName, skills.get(PrimarySkillType.SWORDS));
                    powerLevel += putStat(taming, playerName, skills.get(PrimarySkillType.TAMING));
                    powerLevel += putStat(unarmed, playerName, skills.get(PrimarySkillType.UNARMED));
                    powerLevel += putStat(woodcutting, playerName, skills.get(PrimarySkillType.WOODCUTTING));

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

        mining.sort(c);
        woodcutting.sort(c);
        repair.sort(c);
        unarmed.sort(c);
        herbalism.sort(c);
        excavation.sort(c);
        archery.sort(c);
        swords.sort(c);
        axes.sort(c);
        acrobatics.sort(c);
        taming.sort(c);
        fishing.sort(c);
        alchemy.sort(c);
        powerLevels.sort(c);

        playerStatHash.put(PrimarySkillType.MINING, mining);
        playerStatHash.put(PrimarySkillType.WOODCUTTING, woodcutting);
        playerStatHash.put(PrimarySkillType.REPAIR, repair);
        playerStatHash.put(PrimarySkillType.UNARMED, unarmed);
        playerStatHash.put(PrimarySkillType.HERBALISM, herbalism);
        playerStatHash.put(PrimarySkillType.EXCAVATION, excavation);
        playerStatHash.put(PrimarySkillType.ARCHERY, archery);
        playerStatHash.put(PrimarySkillType.SWORDS, swords);
        playerStatHash.put(PrimarySkillType.AXES, axes);
        playerStatHash.put(PrimarySkillType.ACROBATICS, acrobatics);
        playerStatHash.put(PrimarySkillType.TAMING, taming);
        playerStatHash.put(PrimarySkillType.FISHING, fishing);
        playerStatHash.put(PrimarySkillType.ALCHEMY, alchemy);
    }

    /**
     * Checks that the file is present and valid
     */
    private void checkStructure() {
        boolean corruptDataFound = false;

        if (usersFile.exists()) {
            BufferedReader in = null;
            FileWriter out = null;
            String usersFilePath = mcMMO.getUsersFilePath();

            synchronized (fileWritingLock) {
                try {

                    in = new BufferedReader(new FileReader(usersFilePath));
                    StringBuilder writer = new StringBuilder();
                    String line;
                    HashSet<String> usernames = new HashSet<>();
                    HashSet<String> players = new HashSet<>();

                    while ((line = in.readLine()) != null) {
                        // Remove empty lines from the file
                        if (line.isEmpty()) {
                            continue;
                        }

                        // Length checks depend on last rawSplitData being ':'
                        if (line.charAt(line.length() - 1) != ':') {
                            line = line.concat(":");
                        }

                        String[] rawSplitData = line.split(":");

                        //Not enough data found to be considered a user reliably (NOTE: not foolproof)
                        if(rawSplitData.length < (UUID_INDEX + 1)) {
                            if(!corruptDataFound) {
                                mcMMO.p.getLogger().severe("Some corrupt data was found in mcmmo.users and has been repaired, it is possible that some player data has been lost in this process.");
                                corruptDataFound = true;
                            }

                            if(rawSplitData.length >= 10 //The value here is kind of arbitrary, it shouldn't be too low to avoid false positives, but also we aren't really going to correctly identify when player data has been corrupted or not with 100% accuracy ever
                                    && rawSplitData[0] != null && !rawSplitData[0].isEmpty()) {
                                if(rawSplitData[0].length() <= 16 && rawSplitData[0].length() >= 3) {
                                    mcMMO.p.getLogger().severe("Not enough data found to recover corrupted player data for user: "+rawSplitData[0]);
                                }
                            }
                            //This user may have had a name so declare it

                            continue;
                        }

                        // Prevent the same username from being present multiple times
                        if (!usernames.add(rawSplitData[USERNAME_INDEX])) {
                            //TODO: Check if the commented out code was even necessary
                            rawSplitData[USERNAME_INDEX] = "_INVALID_OLD_USERNAME_'";
                            if (rawSplitData.length < UUID_INDEX + 1 || rawSplitData[UUID_INDEX].equals("NULL")) {
                                mcMMO.p.getLogger().severe("Fixing duplicate player names found in mcmmo.users");
                                continue;
                            }
                        }

                        // Prevent the same player from being present multiple times
                        if (rawSplitData.length >= (UUID_INDEX + 1) //TODO: Test this condition
                                && (!rawSplitData[UUID_INDEX].isEmpty()
                                && !rawSplitData[UUID_INDEX].equals("NULL") && !players.add(rawSplitData[UUID_INDEX]))) {

                            mcMMO.p.getLogger().severe("Removing duplicate player data from mcmmo.users");
                            mcMMO.p.getLogger().info("Duplicate Data: "+line);
                            continue;
                        }

                        //Correctly size the data (null entries for missing values)
                        if(line.length() < DATA_ENTRY_COUNT) { //TODO: Test this condition
                            String[] correctSizeSplitData = Arrays.copyOf(rawSplitData, DATA_ENTRY_COUNT);
                            line = org.apache.commons.lang.StringUtils.join(correctSizeSplitData, ":") + ":";
                            rawSplitData = line.split(":");
                            PlayerProfile temporaryProfile = loadFromLine(rawSplitData);
                            writeUserToLine(temporaryProfile, rawSplitData[USERNAME_INDEX], temporaryProfile.getUniqueId(), writer);
                        } else {
                            writer.append(line).append("\r\n");
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
                            e.printStackTrace();
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if(corruptDataFound)
                mcMMO.p.getLogger().info("Corrupt data was found and removed, everything should be working fine. It is possible some player data was lost.");

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

    private static class SkillComparator implements Comparator<PlayerStat> {
        @Override
        public int compare(PlayerStat o1, PlayerStat o2) {
            return (o2.statVal - o1.statVal);
        }
    }

    private PlayerProfile loadFromLine(@NotNull String[] character) {
        Map<PrimarySkillType, Integer>   skills     = getSkillMapFromLine(character);      // Skill levels
        Map<PrimarySkillType, Float>     skillsXp   = new EnumMap<>(PrimarySkillType.class);     // Skill & XP
        Map<SuperAbilityType, Integer> skillsDATS = new EnumMap<>(SuperAbilityType.class); // Ability & Cooldown
        Map<UniqueDataType, Integer> uniquePlayerDataMap = new EnumMap<>(UniqueDataType.class);
        MobHealthbarType mobHealthbarType;
        int scoreboardTipsShown;

        String username = character[USERNAME_INDEX];

        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.TAMING, EXP_TAMING, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.MINING, EXP_MINING, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.REPAIR, EXP_REPAIR, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.WOODCUTTING, EXP_WOODCUTTING, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.UNARMED, EXP_UNARMED, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.HERBALISM, EXP_HERBALISM, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.EXCAVATION, EXP_EXCAVATION, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.ARCHERY, EXP_ARCHERY, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.SWORDS, EXP_SWORDS, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.AXES, EXP_AXES, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.ACROBATICS, EXP_ACROBATICS, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.FISHING, EXP_FISHING, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.ALCHEMY, EXP_ALCHEMY, username);

        // Taming - Unused
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.SUPER_BREAKER, COOLDOWN_SUPER_BREAKER, username);
        // Repair - Unused
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.TREE_FELLER, COOLDOWN_TREE_FELLER, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.BERSERK, COOLDOWN_BERSERK, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.GREEN_TERRA, COOLDOWN_GREEN_TERRA, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.GIGA_DRILL_BREAKER, COOLDOWN_GIGA_DRILL_BREAKER, username);
        // Archery - Unused
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.SERRATED_STRIKES, COOLDOWN_SERRATED_STRIKES, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.SKULL_SPLITTER, COOLDOWN_SKULL_SPLITTER, username);
        // Acrobatics - Unused
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.BLAST_MINING, COOLDOWN_BLAST_MINING, username);

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
            scoreboardTipsShown = Integer.parseInt(character[SCOREBOARD_TIPS]);
        }
        catch (Exception e) {
            scoreboardTipsShown = 0;
        }

        try {
            uniquePlayerDataMap.put(UniqueDataType.CHIMAERA_WING_DATS, Integer.valueOf(character[COOLDOWN_CHIMAERA_WING]));
        }
        catch (Exception e) {
            uniquePlayerDataMap.put(UniqueDataType.CHIMAERA_WING_DATS, 0);
        }

        return new PlayerProfile(character[USERNAME_INDEX], uuid, skills, skillsXp, skillsDATS, mobHealthbarType, scoreboardTipsShown, uniquePlayerDataMap);
    }

    private void tryLoadSkillCooldownFromRawData(@NotNull Map<SuperAbilityType, Integer> cooldownMap, @NotNull String[] character, @NotNull SuperAbilityType superAbilityType, int cooldownSuperBreaker, @NotNull String userName) {
        try {
            cooldownMap.put(superAbilityType, Integer.valueOf(character[cooldownSuperBreaker]));
        } catch (NumberFormatException e) {
            mcMMO.p.getLogger().severe("Data corruption when trying to load the value for skill "+superAbilityType.toString()+" for player named " + userName+ " setting value to zero");
            e.printStackTrace();
        }
    }

    private void tryLoadSkillFloatValuesFromRawData(@NotNull Map<PrimarySkillType, Float> skillMap, @NotNull String[] character, @NotNull PrimarySkillType primarySkillType, int index, @NotNull String userName) {
        try {
            float valueFromString = Integer.parseInt(character[index]);
            skillMap.put(primarySkillType, valueFromString);
        } catch (NumberFormatException e) {
            skillMap.put(primarySkillType, 0F);
            mcMMO.p.getLogger().severe("Data corruption when trying to load the value for skill "+primarySkillType.toString()+" for player named " + userName+ " setting value to zero");
            e.printStackTrace();
        }
    }

    private void tryLoadSkillIntValuesFromRawData(@NotNull Map<PrimarySkillType, Integer> skillMap, @NotNull String[] character, @NotNull PrimarySkillType primarySkillType, int index, @NotNull String userName) {
        try {
            int valueFromString = Integer.parseInt(character[index]);
            skillMap.put(primarySkillType, valueFromString);
        } catch (NumberFormatException e) {
            skillMap.put(primarySkillType, 0);
            mcMMO.p.getLogger().severe("Data corruption when trying to load the value for skill "+primarySkillType.toString()+" for player named " + userName+ " setting value to zero");
            e.printStackTrace();
        }
    }

    private @NotNull Map<PrimarySkillType, Integer> getSkillMapFromLine(@NotNull String[] character) {
        Map<PrimarySkillType, Integer> skills = new EnumMap<>(PrimarySkillType.class);   // Skill & Level
        String username = character[USERNAME_INDEX];

        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.TAMING, SKILLS_TAMING, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.MINING, SKILLS_MINING, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.REPAIR, SKILLS_REPAIR, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.WOODCUTTING, SKILLS_WOODCUTTING, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.UNARMED, SKILLS_UNARMED, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.HERBALISM, SKILLS_HERBALISM, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.EXCAVATION, SKILLS_EXCAVATION, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.ARCHERY, SKILLS_ARCHERY, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.SWORDS, SKILLS_SWORDS, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.AXES, SKILLS_AXES, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.ACROBATICS, SKILLS_ACROBATICS, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.FISHING, SKILLS_FISHING, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.ALCHEMY, SKILLS_ALCHEMY, username);

        return skills;
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.FLATFILE;
    }

    @Override
    public void onDisable() { }

    private int getSkillIndex(PrimarySkillType skill) {
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
                    
                    line = org.apache.commons.lang.StringUtils.join(character, ":") + ":";

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
