package com.gmail.nossr50.database;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.database.flatfile.LeaderboardStatus;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.player.UniqueDataType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.SkillTools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FlatFileDatabaseManager implements DatabaseManager {
    public static final String IGNORED = "IGNORED";
    public static final String LEGACY_INVALID_OLD_USERNAME = "_INVALID_OLD_USERNAME_'";
    private final @NotNull EnumMap<PrimarySkillType, List<PlayerStat>> leaderboardMap = new EnumMap<>(
            PrimarySkillType.class);
    private @NotNull List<PlayerStat> powerLevels = new ArrayList<>();
    private long lastUpdate = 0;
    private final @NotNull String usersFilePath;
    private final @NotNull Logger logger;
    private final long purgeTime;
    private final int startingLevel;
    private final boolean testing;

    private final long UPDATE_WAIT_TIME = 600000L; // 10 minutes
    private final @NotNull File usersFile;
    private static final Object fileWritingLock = new Object();

    public static final int USERNAME_INDEX = 0;
    public static final int SKILLS_MINING = 1;
    public static final int EXP_MINING = 4;
    public static final int SKILLS_WOODCUTTING = 5;
    public static final int EXP_WOODCUTTING = 6;
    public static final int SKILLS_REPAIR = 7;
    public static final int SKILLS_UNARMED = 8;
    public static final int SKILLS_HERBALISM = 9;
    public static final int SKILLS_EXCAVATION = 10;
    public static final int SKILLS_ARCHERY = 11;
    public static final int SKILLS_SWORDS = 12;
    public static final int SKILLS_AXES = 13;
    public static final int SKILLS_ACROBATICS = 14;
    public static final int EXP_REPAIR = 15;
    public static final int EXP_UNARMED = 16;
    public static final int EXP_HERBALISM = 17;
    public static final int EXP_EXCAVATION = 18;
    public static final int EXP_ARCHERY = 19;
    public static final int EXP_SWORDS = 20;
    public static final int EXP_AXES = 21;
    public static final int EXP_ACROBATICS = 22;
    public static final int SKILLS_TAMING = 24;
    public static final int EXP_TAMING = 25;
    public static final int COOLDOWN_BERSERK = 26;
    public static final int COOLDOWN_GIGA_DRILL_BREAKER = 27;
    public static final int COOLDOWN_TREE_FELLER = 28;
    public static final int COOLDOWN_GREEN_TERRA = 29;
    public static final int COOLDOWN_SERRATED_STRIKES = 30;
    public static final int COOLDOWN_SKULL_SPLITTER = 31;
    public static final int COOLDOWN_SUPER_BREAKER = 32;
    public static final int SKILLS_FISHING = 34;
    public static final int EXP_FISHING = 35;
    public static final int COOLDOWN_BLAST_MINING = 36;
    public static final int LEGACY_LAST_LOGIN = 37;
    public static final int HEALTHBAR = 38;
    public static final int SKILLS_ALCHEMY = 39;
    public static final int EXP_ALCHEMY = 40;
    public static final int UUID_INDEX = 41;
    public static final int SCOREBOARD_TIPS = 42;
    public static final int COOLDOWN_CHIMAERA_WING = 43;
    public static final int OVERHAUL_LAST_LOGIN = 44;
    public static final int EXP_CROSSBOWS = 45;
    public static final int SKILLS_CROSSBOWS = 46;
    public static final int EXP_TRIDENTS = 47;
    public static final int SKILLS_TRIDENTS = 48;
    public static final int COOLDOWN_SUPER_SHOTGUN = 49;
    public static final int COOLDOWN_TRIDENTS = 50;
    public static final int COOLDOWN_ARCHERY = 51;
    public static final int EXP_MACES = 52;
    public static final int SKILLS_MACES = 53;
    public static final int COOLDOWN_MACES = 54;
    //Update this everytime new data is added
    public static final int DATA_ENTRY_COUNT = COOLDOWN_MACES + 1;

    FlatFileDatabaseManager(@NotNull File usersFile, @NotNull Logger logger, long purgeTime,
            int startingLevel, boolean testing) {
        this.usersFile = usersFile;
        this.usersFilePath = usersFile.getPath();
        this.logger = logger;
        this.purgeTime = purgeTime;
        this.startingLevel = startingLevel;
        this.testing = testing;

        if (!usersFile.exists()) {
            initEmptyDB();
        }

        if (!testing) {
            List<FlatFileDataFlag> flatFileDataFlags = checkFileHealthAndStructure();

            if (flatFileDataFlags != null) {
                if (!flatFileDataFlags.isEmpty()) {
                    logger.info("Detected " + flatFileDataFlags.size()
                            + " data entries which need correction.");
                }
            }

            updateLeaderboards();
        }
    }

    FlatFileDatabaseManager(@NotNull String usersFilePath, @NotNull Logger logger, long purgeTime,
            int startingLevel) {
        this(new File(usersFilePath), logger, purgeTime, startingLevel, false);
    }


    public int purgePowerlessUsers() {
        int purgedUsers = 0;

        LogUtils.debug(logger, "Purging powerless users...");

        BufferedReader in = null;
        FileWriter out = null;

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
                    } else {
                        purgedUsers++;
                    }
                }

                // Write the new file
                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
            } catch (IOException e) {
                logger.severe("Exception while reading " + usersFilePath
                        + " (Are you sure you formatted it correctly?)" + e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }

        logger.info("Purged " + purgedUsers + " users from the database.");
        return purgedUsers;
    }

    //TODO: Test this
    public void purgeOldUsers() {
        int removedPlayers = 0;
        long currentTime = System.currentTimeMillis();

        LogUtils.debug(logger, "Purging old users...");

        BufferedReader in = null;
        FileWriter out = null;

        // This code is O(n) instead of O(n²)
        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    String uuidString = character[UUID_INDEX];
                    UUID uuid = UUID.fromString(uuidString);
                    long lastPlayed = 0;
                    boolean rewrite = false;

                    try {
                        lastPlayed = Long.parseLong(character[OVERHAUL_LAST_LOGIN]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    if (lastPlayed == -1) {
                        OfflinePlayer player = mcMMO.p.getServer().getOfflinePlayer(uuid);

                        if (player.getLastPlayed() != 0) {
                            lastPlayed = player.getLastPlayed();
                            rewrite = true;
                        }
                    }

                    if (lastPlayed < 1 && (currentTime - lastPlayed > purgeTime)) {
                        removedPlayers++;
                    } else {
                        if (rewrite) {
                            // Rewrite their data with a valid time
                            character[OVERHAUL_LAST_LOGIN] = Long.toString(lastPlayed);
                            String newLine = org.apache.commons.lang3.StringUtils.join(character,
                                    ":");
                            writer.append(newLine).append("\r\n");
                        } else {
                            writer.append(line).append("\r\n");
                        }
                    }
                }

                // Write the new file
                out = new FileWriter(usersFilePath);
                out.write(writer.toString());

                if (testing) {
                    System.out.println(writer);
                }
            } catch (IOException e) {
                logger.severe("Exception while reading " + usersFilePath
                        + " (Are you sure you formatted it correctly?)" + e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }

        logger.info("Purged " + removedPlayers + " users from the database.");
    }

    public boolean removeUser(String playerName, UUID uuid) {
        //NOTE: UUID is unused for FlatFile for this interface implementation
        boolean worked = false;

        BufferedReader in = null;
        FileWriter out = null;

        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    // Write out the same file but when we get to the player we want to remove, we skip his line.
                    if (!worked && line.split(":")[USERNAME_INDEX].equalsIgnoreCase(playerName)) {
                        logger.info("User found, removing...");
                        worked = true;
                        continue; // Skip the player
                    }

                    writer.append(line).append("\r\n");
                }

                out = new FileWriter(usersFilePath); // Write out the new file
                out.write(writer.toString());
            } catch (Exception e) {
                logger.severe("Exception while reading " + usersFilePath
                        + " (Are you sure you formatted it correctly?)" + e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
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

    public boolean saveUser(@NotNull PlayerProfile profile) {
        String playerName = profile.getPlayerName();
        UUID uuid = profile.getUniqueId();

        BufferedReader in = null;
        FileWriter out = null;
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
                    if (line.startsWith("#")) {
                        writer.append(line).append("\r\n");
                        continue;
                    }

                    //Check for incomplete or corrupted data
                    if (!line.contains(":")) {

                        if (!corruptDataFound) {
                            logger.severe(
                                    "mcMMO found some unexpected or corrupted data in mcmmo.users and is removing it, it is possible some data has been lost.");
                            corruptDataFound = true;
                        }

                        continue;
                    }

                    String[] splitData = line.split(":");

                    //This would be rare, but check the splitData for having enough entries to contain a UUID
                    if (splitData.length
                            < UUID_INDEX) { //UUID have been in mcMMO DB for a very long time so any user without

                        if (!corruptDataFound) {
                            logger.severe(
                                    "mcMMO found some unexpected or corrupted data in mcmmo.users and is removing it, it is possible some data has been lost.");
                            corruptDataFound = true;
                        }

                        continue;
                    }

                    if (!(uuid != null
                            && splitData[UUID_INDEX].equalsIgnoreCase(uuid.toString()))
                            && !splitData[USERNAME_INDEX].equalsIgnoreCase(playerName)) {
                        writer.append(line)
                                .append("\r\n"); //Not the user so write it to file and move on
                    } else {
                        //User found
                        writeUserToLine(profile, writer);
                        wroteUser = true;
                    }
                }

                /*
                 * If we couldn't find the user in the DB we need to add him
                 */
                if (!wroteUser) {
                    writeUserToLine(profile, writer);
                }

                // Write the new file
                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }
    }

    public void writeUserToLine(@NotNull PlayerProfile profile, @NotNull Appendable appendable)
            throws IOException {
        appendable.append(profile.getPlayerName()).append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.MINING)))
                .append(":");
        appendable.append(IGNORED).append(":");
        appendable.append(IGNORED).append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.MINING)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.WOODCUTTING)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.WOODCUTTING)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.REPAIR)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.UNARMED)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.HERBALISM)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.EXCAVATION)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.ARCHERY)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.SWORDS)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.AXES))).append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.ACROBATICS)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.REPAIR)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.UNARMED)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.HERBALISM)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.EXCAVATION)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.ARCHERY)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.SWORDS)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.AXES)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.ACROBATICS)))
                .append(":");
        appendable.append(IGNORED).append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.TAMING)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.TAMING)))
                .append(":");
        appendable.append(String.valueOf(profile.getAbilityDATS(SuperAbilityType.BERSERK)))
                .append(":");
        appendable.append(
                        String.valueOf(profile.getAbilityDATS(SuperAbilityType.GIGA_DRILL_BREAKER)))
                .append(":");
        appendable.append(String.valueOf(profile.getAbilityDATS(SuperAbilityType.TREE_FELLER)))
                .append(":");
        appendable.append(String.valueOf(profile.getAbilityDATS(SuperAbilityType.GREEN_TERRA)))
                .append(":");
        appendable.append(String.valueOf(profile.getAbilityDATS(SuperAbilityType.SERRATED_STRIKES)))
                .append(":");
        appendable.append(String.valueOf(profile.getAbilityDATS(SuperAbilityType.SKULL_SPLITTER)))
                .append(":");
        appendable.append(String.valueOf(profile.getAbilityDATS(SuperAbilityType.SUPER_BREAKER)))
                .append(":");
        appendable.append(IGNORED).append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.FISHING)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.FISHING)))
                .append(":");
        appendable.append(String.valueOf(profile.getAbilityDATS(SuperAbilityType.BLAST_MINING)))
                .append(":");
        appendable.append(IGNORED).append(":"); //Legacy last login
        appendable.append(IGNORED).append(":"); //mob health bar
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.ALCHEMY)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.ALCHEMY)))
                .append(":");
        appendable.append(profile.getUniqueId() != null ? profile.getUniqueId().toString() : "NULL")
                .append(":");
        appendable.append(String.valueOf(profile.getScoreboardTipsShown())).append(":");
        appendable.append(String.valueOf(profile.getUniqueData(UniqueDataType.CHIMAERA_WING_DATS)))
                .append(":");
        appendable.append(String.valueOf(profile.getLastLogin())).append(":"); //overhaul last login
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.CROSSBOWS)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.CROSSBOWS)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.TRIDENTS)))
                .append(":");
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.TRIDENTS)))
                .append(":");
        //     public static final int COOLDOWN_SUPER_SHOTGUN = 49;
        appendable.append(String.valueOf(profile.getAbilityDATS(SuperAbilityType.SUPER_SHOTGUN)))
                .append(":");
        //    public static final int COOLDOWN_TRIDENTS = 50;
        appendable.append(
                        String.valueOf(profile.getAbilityDATS(SuperAbilityType.TRIDENTS_SUPER_ABILITY)))
                .append(":");
        //    public static final int COOLDOWN_ARCHERY = 51;
        appendable.append(String.valueOf(profile.getAbilityDATS(SuperAbilityType.EXPLOSIVE_SHOT)))
                .append(":");
        //    public static final int EXP_MACES = 52;
        appendable.append(String.valueOf(profile.getSkillXpLevel(PrimarySkillType.MACES)))
                .append(":");
        //    public static final int SKILLS_MACES = 53;
        appendable.append(String.valueOf(profile.getSkillLevel(PrimarySkillType.MACES)))
                .append(":");
        //    public static final int COOLDOWN_MACES = 54;
        appendable.append(
                        String.valueOf(profile.getAbilityDATS(SuperAbilityType.MACES_SUPER_ABILITY)))
                .append(":");
        appendable.append("\r\n");
    }

    public @NotNull List<PlayerStat> readLeaderboard(@Nullable PrimarySkillType primarySkillType,
            int pageNumber, int statsPerPage) throws InvalidSkillException {
        //Fix for a plugin that people are using that is throwing SQL errors
        if (primarySkillType != null && SkillTools.isChildSkill(primarySkillType)) {
            logger.severe(
                    "A plugin hooking into mcMMO is being naughty with our database commands, update all plugins that hook into mcMMO and contact their devs!");
            throw new InvalidSkillException(
                    "A plugin hooking into mcMMO that you are using is attempting to read leaderboard skills for child skills, child skills do not have leaderboards! This is NOT an mcMMO error!");
        }

        updateLeaderboards();
        List<PlayerStat> statsList =
                primarySkillType == null ? powerLevels : leaderboardMap.get(primarySkillType);
        int fromIndex = (Math.max(pageNumber, 1) - 1) * statsPerPage;

        return statsList.subList(Math.min(fromIndex, statsList.size()),
                Math.min(fromIndex + statsPerPage, statsList.size()));
    }

    public @NotNull HashMap<PrimarySkillType, Integer> readRank(String playerName) {
        updateLeaderboards();

        HashMap<PrimarySkillType, Integer> skills = new HashMap<>();

        for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) {
            skills.put(skill, getPlayerRank(playerName, leaderboardMap.get(skill)));
        }

        skills.put(null, getPlayerRank(playerName, powerLevels));

        return skills;
    }

    public @NotNull PlayerProfile newUser(@NotNull Player player) {
        return new PlayerProfile(player.getName(), player.getUniqueId(), true, startingLevel);
    }

    public @NotNull PlayerProfile newUser(@NotNull String playerName, @NotNull UUID uuid) {
        PlayerProfile playerProfile = new PlayerProfile(playerName, uuid, true, startingLevel);

        synchronized (fileWritingLock) {
            try (BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(usersFilePath))) {
                StringBuilder stringBuilder = new StringBuilder();

                String line;

                //Build up the file
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\r\n");
                }

                try (FileWriter fileWriter = new FileWriter(usersFile)) {
                    writeUserToLine(playerProfile, stringBuilder);
                    fileWriter.write(stringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return playerProfile;
    }

    public @NotNull PlayerProfile loadPlayerProfile(@NotNull OfflinePlayer offlinePlayer) {
        return processUserQuery(getUserQuery(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
    }

    public @NotNull PlayerProfile loadPlayerProfile(@NotNull String playerName) {
        return processUserQuery(getUserQuery(null, playerName));
    }

    public @NotNull PlayerProfile loadPlayerProfile(@NotNull UUID uuid) {
        return processUserQuery(getUserQuery(uuid, null));
    }

    private @NotNull UserQuery getUserQuery(@Nullable UUID uuid, @Nullable String playerName)
            throws NullPointerException {
        boolean hasName = playerName != null && !playerName.equalsIgnoreCase("null");

        if (hasName && uuid != null) {
            return new UserQueryFull(playerName, uuid);
        } else if (uuid != null) {
            return new UserQueryUUIDImpl(uuid);
        } else if (hasName) {
            return new UserQueryNameImpl(playerName);
        } else {
            throw new NullPointerException(
                    "Both name and UUID cannot be null, at least one must be non-null!");
        }
    }

    /**
     * Find and load a player by UUID/Name If the name isn't null and doesn't match the name in the
     * DB, the players name is then replaced/updated
     *
     * @param userQuery the query
     * @return a profile with the targets data or an unloaded profile if no data was found
     */
    private @NotNull PlayerProfile processUserQuery(@NotNull UserQuery userQuery)
            throws RuntimeException {
        return switch (userQuery.getType()) {
            case UUID_AND_NAME -> queryByUUIDAndName((UserQueryFull) userQuery);
            case UUID -> queryByUUID((UserQueryUUID) userQuery);
            case NAME -> queryByName((UserQueryNameImpl) userQuery);
        };
    }

    private @NotNull PlayerProfile queryByName(@NotNull UserQueryName userQuery) {
        String playerName = userQuery.getName();
        BufferedReader in = null;

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }

                    // Find if the line contains the player we want.
                    String[] rawSplitData = line.split(":");


                    /* Don't read corrupt data */
                    if (rawSplitData.length < (USERNAME_INDEX + 1)) {
                        continue;
                    }

                    // we found the player
                    if (playerName.equalsIgnoreCase(rawSplitData[USERNAME_INDEX])) {
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
        return new PlayerProfile(playerName, new UUID(0, 0), startingLevel);
    }

    private @NotNull PlayerProfile queryByUUID(@NotNull UserQueryUUID userQuery) {
        BufferedReader in = null;
        UUID uuid = userQuery.getUUID();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    // Find if the line contains the player we want.
                    String[] rawSplitData = line.split(":");

                    /* Don't read corrupt data */
                    if (rawSplitData.length < (UUID_INDEX + 1)) {
                        continue;
                    }

                    try {
                        UUID fromDataUUID = UUID.fromString(rawSplitData[UUID_INDEX]);
                        if (fromDataUUID.equals(uuid)) {
                            return loadFromLine(rawSplitData);
                        }
                    } catch (Exception e) {
                        if (testing) {
                            e.printStackTrace();
                        }
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

        /*
         * No match was found in the file
         */

        return grabUnloadedProfile(uuid, "Player-Not-Found=" + uuid);
    }

    private @NotNull PlayerProfile queryByUUIDAndName(@NotNull UserQueryFull userQuery) {
        BufferedReader in = null;
        String playerName = userQuery.getName();
        UUID uuid = userQuery.getUUID();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    // Find if the line contains the player we want.
                    String[] rawSplitData = line.split(":");

                    /* Don't read corrupt data */
                    if (rawSplitData.length < (UUID_INDEX + 1)) {
                        continue;
                    }

                    try {
                        UUID fromDataUUID = UUID.fromString(rawSplitData[UUID_INDEX]);
                        if (fromDataUUID.equals(uuid)) {
                            //Matched UUID, now check if name matches
                            String dbPlayerName = rawSplitData[USERNAME_INDEX];

                            boolean matchingName = dbPlayerName.equalsIgnoreCase(playerName);

                            if (!matchingName) {
                                logger.warning(
                                        "When loading user: " + playerName + " with UUID of ("
                                                + uuid
                                                + ") we found a mismatched name, the name in the DB will be replaced (DB name: "
                                                + dbPlayerName + ")");
                                //logger.info("Name updated for player: " + rawSplitData[USERNAME_INDEX] + " => " + playerName);
                                rawSplitData[USERNAME_INDEX] = playerName;
                            }

                            //TODO: Logic to replace name here
                            return loadFromLine(rawSplitData);
                        }
                    } catch (Exception e) {
                        if (testing) {
                            e.printStackTrace();
                        }
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

        /*
         * No match was found in the file
         */

        return grabUnloadedProfile(uuid, playerName); //Create an empty new profile and return
    }

    private @NotNull PlayerProfile grabUnloadedProfile(@NotNull UUID uuid,
            @Nullable String playerName) {
        if (playerName == null) {
            playerName = ""; //No name for you boy!
        }

        return new PlayerProfile(playerName, uuid, 0);
    }

    public void convertUsers(DatabaseManager destination) {
        BufferedReader in = null;
        int convertedUsers = 0;
        long startMillis = System.currentTimeMillis();

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }

                    String[] character = line.split(":");

                    try {
                        destination.saveUser(loadFromLine(character));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    convertedUsers++;
                    Misc.printProgress(convertedUsers, progressInterval, startMillis);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
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

        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    if (!worked && character[USERNAME_INDEX].equalsIgnoreCase(userName)) {
                        if (character.length < 42) {
                            logger.severe("Could not update UUID for " + userName + "!");
                            logger.severe("Database entry is invalid.");
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
            } catch (Exception e) {
                logger.severe("Exception while reading " + usersFilePath
                        + " (Are you sure you formatted it correctly?)" + e);
            } finally {
                LogUtils.debug(logger, i + " entries written while saving UUID for " + userName);
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
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
        int i = 0;

        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;

                while (((line = in.readLine()) != null)) {
                    String[] character = line.split(":");
                    if (!fetchedUUIDs.isEmpty() && fetchedUUIDs.containsKey(
                            character[USERNAME_INDEX])) {
                        if (character.length < 42) {
                            logger.severe(
                                    "Could not update UUID for " + character[USERNAME_INDEX] + "!");
                            logger.severe("Database entry is invalid.");
                            continue;
                        }

                        character[UUID_INDEX] = fetchedUUIDs.remove(character[USERNAME_INDEX])
                                .toString();
                        line = org.apache.commons.lang3.StringUtils.join(character, ":") + ":";
                    }

                    i++;
                    writer.append(line).append("\r\n");
                }

                out = new FileWriter(usersFilePath); // Write out the new file
                out.write(writer.toString());
            } catch (Exception e) {
                logger.severe("Exception while reading " + usersFilePath
                        + " (Are you sure you formatted it correctly?)" + e);
            } finally {
                LogUtils.debug(logger, i + " entries written while saving UUID batch");
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
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

        synchronized (fileWritingLock) {
            try {
                // Open the user file
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    users.add(character[USERNAME_INDEX]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
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
    public @NotNull LeaderboardStatus updateLeaderboards() {
        // Only update FFS leaderboards every 10 minutes.. this puts a lot of strain on the server (depending on the size of the database) and should not be done frequently
        if (System.currentTimeMillis() < lastUpdate + UPDATE_WAIT_TIME) {
            return LeaderboardStatus.TOO_SOON_TO_UPDATE;
        }

        lastUpdate = System.currentTimeMillis(); // Log when the last update was run

        TreeSet<PlayerStat> powerLevelStats = new TreeSet<>();
        TreeSet<PlayerStat> mining = new TreeSet<>();
        TreeSet<PlayerStat> woodcutting = new TreeSet<>();
        TreeSet<PlayerStat> herbalism = new TreeSet<>();
        TreeSet<PlayerStat> excavation = new TreeSet<>();
        TreeSet<PlayerStat> acrobatics = new TreeSet<>();
        TreeSet<PlayerStat> repair = new TreeSet<>();
        TreeSet<PlayerStat> swords = new TreeSet<>();
        TreeSet<PlayerStat> axes = new TreeSet<>();
        TreeSet<PlayerStat> archery = new TreeSet<>();
        TreeSet<PlayerStat> unarmed = new TreeSet<>();
        TreeSet<PlayerStat> taming = new TreeSet<>();
        TreeSet<PlayerStat> fishing = new TreeSet<>();
        TreeSet<PlayerStat> alchemy = new TreeSet<>();
        TreeSet<PlayerStat> crossbows = new TreeSet<>();
        TreeSet<PlayerStat> tridents = new TreeSet<>();
        TreeSet<PlayerStat> maces = new TreeSet<>();

        BufferedReader in = null;
        String playerName = null;
        // Read from the FlatFile database and fill our arrays with information
        synchronized (fileWritingLock) {
            try {
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;

                while ((line = in.readLine()) != null) {

                    if (line.startsWith("#")) {
                        continue;
                    }

                    String[] data = line.split(":");
                    playerName = data[USERNAME_INDEX];
                    int powerLevel = 0;

                    Map<PrimarySkillType, Integer> skills = getSkillMapFromLine(data);

                    powerLevel += putStat(acrobatics, playerName,
                            skills.get(PrimarySkillType.ACROBATICS));
                    powerLevel += putStat(alchemy, playerName,
                            skills.get(PrimarySkillType.ALCHEMY));
                    powerLevel += putStat(archery, playerName,
                            skills.get(PrimarySkillType.ARCHERY));
                    powerLevel += putStat(axes, playerName, skills.get(PrimarySkillType.AXES));
                    powerLevel += putStat(excavation, playerName,
                            skills.get(PrimarySkillType.EXCAVATION));
                    powerLevel += putStat(fishing, playerName,
                            skills.get(PrimarySkillType.FISHING));
                    powerLevel += putStat(herbalism, playerName,
                            skills.get(PrimarySkillType.HERBALISM));
                    powerLevel += putStat(mining, playerName, skills.get(PrimarySkillType.MINING));
                    powerLevel += putStat(repair, playerName, skills.get(PrimarySkillType.REPAIR));
                    powerLevel += putStat(swords, playerName, skills.get(PrimarySkillType.SWORDS));
                    powerLevel += putStat(taming, playerName, skills.get(PrimarySkillType.TAMING));
                    powerLevel += putStat(unarmed, playerName,
                            skills.get(PrimarySkillType.UNARMED));
                    powerLevel += putStat(woodcutting, playerName,
                            skills.get(PrimarySkillType.WOODCUTTING));
                    powerLevel += putStat(crossbows, playerName,
                            skills.get(PrimarySkillType.CROSSBOWS));
                    powerLevel += putStat(tridents, playerName,
                            skills.get(PrimarySkillType.TRIDENTS));
                    powerLevel += putStat(maces, playerName, skills.get(PrimarySkillType.MACES));

                    putStat(powerLevelStats, playerName, powerLevel);
                }
            } catch (Exception e) {
                logger.severe(
                        "Exception while reading " + usersFilePath + " during user " + playerName
                                + " (Are you sure you formatted it correctly?) " + e);
                return LeaderboardStatus.FAILED;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }

        }

        powerLevels = List.copyOf(powerLevelStats);
        leaderboardMap.put(PrimarySkillType.MINING, List.copyOf(mining));
        leaderboardMap.put(PrimarySkillType.WOODCUTTING, List.copyOf(woodcutting));
        leaderboardMap.put(PrimarySkillType.REPAIR, List.copyOf(repair));
        leaderboardMap.put(PrimarySkillType.UNARMED, List.copyOf(unarmed));
        leaderboardMap.put(PrimarySkillType.HERBALISM, List.copyOf(herbalism));
        leaderboardMap.put(PrimarySkillType.EXCAVATION, List.copyOf(excavation));
        leaderboardMap.put(PrimarySkillType.ARCHERY, List.copyOf(archery));
        leaderboardMap.put(PrimarySkillType.SWORDS, List.copyOf(swords));
        leaderboardMap.put(PrimarySkillType.AXES, List.copyOf(axes));
        leaderboardMap.put(PrimarySkillType.ACROBATICS, List.copyOf(acrobatics));
        leaderboardMap.put(PrimarySkillType.TAMING, List.copyOf(taming));
        leaderboardMap.put(PrimarySkillType.FISHING, List.copyOf(fishing));
        leaderboardMap.put(PrimarySkillType.ALCHEMY, List.copyOf(alchemy));
        leaderboardMap.put(PrimarySkillType.CROSSBOWS, List.copyOf(crossbows));
        leaderboardMap.put(PrimarySkillType.TRIDENTS, List.copyOf(tridents));
        leaderboardMap.put(PrimarySkillType.MACES, List.copyOf(maces));

        return LeaderboardStatus.UPDATED;
    }

    private void initEmptyDB() {
        BufferedWriter bufferedWriter = null;
        synchronized (fileWritingLock) {
            try {
                // Open the file to write the player
                bufferedWriter = new BufferedWriter(new FileWriter(usersFilePath, true));
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
                        "MM/dd/yyyy HH:mm");
                LocalDateTime localDateTime = LocalDateTime.now();
                bufferedWriter.append("# mcMMO Database created on ")
                        .append(localDateTime.format(dateTimeFormatter))
                        .append("\r\n"); //Empty file
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }
    }

    public @Nullable List<FlatFileDataFlag> checkFileHealthAndStructure() {
        ArrayList<FlatFileDataFlag> flagsFound = null;
        LogUtils.debug(logger, "(" + usersFile.getPath() + ") Validating database file..");
        FlatFileDataProcessor dataProcessor;

        if (usersFile.exists()) {
            BufferedReader bufferedReader = null;
            FileWriter fileWriter = null;

            synchronized (fileWritingLock) {

                dataProcessor = new FlatFileDataProcessor(logger);

                try {
                    String currentLine;
                    String dbCommentDate = null;

                    bufferedReader = new BufferedReader(new FileReader(usersFilePath));

                    //Analyze the data
                    while ((currentLine = bufferedReader.readLine()) != null) {
                        //Commented lines
                        if (currentLine.startsWith("#") && dbCommentDate
                                == null) { //The first commented line in the file is likely to be our note about when the file was created
                            dbCommentDate = currentLine;
                            continue;
                        }

                        if (currentLine.isEmpty()) {
                            continue;
                        }

                        //TODO: We are never passing empty lines, should we remove the flag for them?
                        dataProcessor.processData(currentLine);
                    }

                    //Only update the file if needed
                    if (!dataProcessor.getFlatFileDataFlags().isEmpty()) {
                        flagsFound = new ArrayList<>(dataProcessor.getFlatFileDataFlags());
                        logger.info("Updating FlatFile Database...");
                        fileWriter = new FileWriter(usersFilePath);
                        //Write data to file
                        if (dbCommentDate != null) {
                            fileWriter.write(dbCommentDate + "\r\n");
                        }

                        fileWriter.write(dataProcessor.processDataForSave().toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeResources(bufferedReader, fileWriter);
                }
            }
        }

        if (flagsFound == null || flagsFound.isEmpty()) {
            return null;
        } else {
            return flagsFound;
        }
    }

    private void closeResources(BufferedReader bufferedReader, FileWriter fileWriter) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Integer getPlayerRank(String playerName, List<PlayerStat> statsList) {
        if (statsList == null) {
            return null;
        }

        int currentPos = 1;

        for (PlayerStat stat : statsList) {
            if (stat.playerName().equalsIgnoreCase(playerName)) {
                return currentPos;
            }

            currentPos++;
        }

        return null;
    }

    private int putStat(TreeSet<PlayerStat> statList, String playerName, int statValue) {
        statList.add(new PlayerStat(playerName, statValue));
        return statValue;
    }

    private PlayerProfile loadFromLine(@NotNull String[] character) {
        Map<PrimarySkillType, Integer> skills = getSkillMapFromLine(character);      // Skill levels
        Map<PrimarySkillType, Float> skillsXp = new EnumMap<>(
                PrimarySkillType.class);     // Skill & XP
        Map<SuperAbilityType, Integer> skillsDATS = new EnumMap<>(
                SuperAbilityType.class); // Ability & Cooldown
        Map<UniqueDataType, Integer> uniquePlayerDataMap = new EnumMap<>(UniqueDataType.class);
        int scoreboardTipsShown;
        long lastLogin;

        String username = character[USERNAME_INDEX];

        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.TAMING, EXP_TAMING,
                username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.MINING, EXP_MINING,
                username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.REPAIR, EXP_REPAIR,
                username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.WOODCUTTING,
                EXP_WOODCUTTING, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.UNARMED,
                EXP_UNARMED, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.HERBALISM,
                EXP_HERBALISM, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.EXCAVATION,
                EXP_EXCAVATION, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.ARCHERY,
                EXP_ARCHERY, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.SWORDS, EXP_SWORDS,
                username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.AXES, EXP_AXES,
                username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.ACROBATICS,
                EXP_ACROBATICS, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.FISHING,
                EXP_FISHING, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.ALCHEMY,
                EXP_ALCHEMY, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.CROSSBOWS,
                EXP_CROSSBOWS, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.TRIDENTS,
                EXP_TRIDENTS, username);
        tryLoadSkillFloatValuesFromRawData(skillsXp, character, PrimarySkillType.MACES, EXP_MACES,
                username);

        // Taming - Unused
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.SUPER_BREAKER,
                COOLDOWN_SUPER_BREAKER, username);
        // Repair - Unused
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.TREE_FELLER,
                COOLDOWN_TREE_FELLER, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.BERSERK,
                COOLDOWN_BERSERK, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.GREEN_TERRA,
                COOLDOWN_GREEN_TERRA, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.GIGA_DRILL_BREAKER,
                COOLDOWN_GIGA_DRILL_BREAKER, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.EXPLOSIVE_SHOT,
                COOLDOWN_ARCHERY, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.SERRATED_STRIKES,
                COOLDOWN_SERRATED_STRIKES, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.SKULL_SPLITTER,
                COOLDOWN_SKULL_SPLITTER, username);
        // Acrobatics - Unused
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.BLAST_MINING,
                COOLDOWN_BLAST_MINING, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.SUPER_SHOTGUN,
                COOLDOWN_SUPER_SHOTGUN, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character,
                SuperAbilityType.TRIDENTS_SUPER_ABILITY, COOLDOWN_TRIDENTS, username);
        tryLoadSkillCooldownFromRawData(skillsDATS, character, SuperAbilityType.MACES_SUPER_ABILITY,
                COOLDOWN_MACES, username);

        UUID uuid;
        try {
            uuid = UUID.fromString(character[UUID_INDEX]);
        } catch (Exception e) {
            uuid = null;
        }

        try {
            scoreboardTipsShown = Integer.parseInt(character[SCOREBOARD_TIPS]);
        } catch (Exception e) {
            scoreboardTipsShown = 0;
        }

        try {
            uniquePlayerDataMap.put(UniqueDataType.CHIMAERA_WING_DATS,
                    Integer.valueOf(character[COOLDOWN_CHIMAERA_WING]));
        } catch (Exception e) {
            uniquePlayerDataMap.put(UniqueDataType.CHIMAERA_WING_DATS, 0);
        }

        try {
            lastLogin = Long.parseLong(character[OVERHAUL_LAST_LOGIN]);
        } catch (Exception e) {
            lastLogin = -1;
        }

        return new PlayerProfile(username, uuid, skills, skillsXp, skillsDATS, scoreboardTipsShown,
                uniquePlayerDataMap, lastLogin);
    }

    private void tryLoadSkillCooldownFromRawData(
            @NotNull Map<SuperAbilityType, Integer> cooldownMap, @NotNull String[] splitData,
            @NotNull SuperAbilityType superAbilityType, int index, @NotNull String userName) {
        try {
            cooldownMap.put(superAbilityType, Integer.valueOf(splitData[index]));
        } catch (IndexOutOfBoundsException e) {
            // TODO: Add debug message
            // set to 0 when data not found
            cooldownMap.put(superAbilityType, 0);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(
                    "Data corruption when trying to load the cooldown for skill " + superAbilityType
                            + " for player named " + userName);
        }
    }

    private void tryLoadSkillFloatValuesFromRawData(@NotNull Map<PrimarySkillType, Float> skillMap,
            @NotNull String[] character, @NotNull PrimarySkillType primarySkillType, int index,
            @NotNull String userName) {
        try {
            float valueFromString = Integer.parseInt(character[index]);
            skillMap.put(primarySkillType, valueFromString);
        } catch (NumberFormatException e) {
            skillMap.put(primarySkillType, 0F);
            logger.severe(
                    "Data corruption when trying to load the value for skill " + primarySkillType
                            + " for player named " + userName + " setting value to zero");
            e.printStackTrace();
        }
    }

    private void tryLoadSkillIntValuesFromRawData(@NotNull Map<PrimarySkillType, Integer> skillMap,
            @NotNull String[] character, @NotNull PrimarySkillType primarySkillType, int index,
            @NotNull String userName) {
        try {
            int valueFromString = Integer.parseInt(character[index]);
            skillMap.put(primarySkillType, valueFromString);
        } catch (ArrayIndexOutOfBoundsException e) {
            // TODO: Add debug message
            // set to 0 when data not found
            skillMap.put(primarySkillType, 0);
        } catch (NumberFormatException e) {
            skillMap.put(primarySkillType, 0);
            logger.severe(
                    "Data corruption when trying to load the value for skill " + primarySkillType
                            + " for player named " + userName + " setting value to zero");
            e.printStackTrace();
        }
    }

    private @NotNull Map<PrimarySkillType, Integer> getSkillMapFromLine(
            @NotNull String[] character) {
        EnumMap<PrimarySkillType, Integer> skills = new EnumMap<>(
                PrimarySkillType.class);   // Skill & Level
        String username = character[USERNAME_INDEX];

        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.ACROBATICS,
                SKILLS_ACROBATICS, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.TAMING, SKILLS_TAMING,
                username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.MINING, SKILLS_MINING,
                username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.REPAIR, SKILLS_REPAIR,
                username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.WOODCUTTING,
                SKILLS_WOODCUTTING, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.UNARMED,
                SKILLS_UNARMED, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.HERBALISM,
                SKILLS_HERBALISM, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.EXCAVATION,
                SKILLS_EXCAVATION, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.ARCHERY,
                SKILLS_ARCHERY, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.SWORDS, SKILLS_SWORDS,
                username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.AXES, SKILLS_AXES,
                username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.FISHING,
                SKILLS_FISHING, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.ALCHEMY,
                SKILLS_ALCHEMY, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.CROSSBOWS,
                SKILLS_CROSSBOWS, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.TRIDENTS,
                SKILLS_TRIDENTS, username);
        tryLoadSkillIntValuesFromRawData(skills, character, PrimarySkillType.MACES, SKILLS_MACES,
                username);

        return skills;
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.FLATFILE;
    }

    public @NotNull File getUsersFile() {
        return usersFile;
    }

    @Override
    public void onDisable() {
    }
}
