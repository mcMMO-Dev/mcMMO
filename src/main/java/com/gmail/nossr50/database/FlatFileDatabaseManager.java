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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FlatFileDatabaseManager implements DatabaseManager {

    static final String IGNORED = "IGNORED";
    public static final String LEGACY_INVALID_OLD_USERNAME = "_INVALID_OLD_USERNAME_'";

    private static final Object fileWritingLock = new Object();
    private static final String LINE_ENDING = "\r\n";

    private final @NotNull EnumMap<PrimarySkillType, List<PlayerStat>> leaderboardMap =
            new EnumMap<>(PrimarySkillType.class);

    private @NotNull List<PlayerStat> powerLevels = new ArrayList<>();
    private long lastUpdate = 0L;

    private final @NotNull String usersFilePath;
    private final @NotNull File usersFile;
    private final @NotNull Logger logger;
    private final long purgeTime;
    private final int startingLevel;

    private static final long UPDATE_WAIT_TIME = 600_000L; // 10 minutes

    // Flatfile indices
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
    public static final int EXP_SPEARS = 55;
    public static final int SKILLS_SPEARS = 56;
    public static final int COOLDOWN_SPEARS = 57;

    // Update this everytime new data is added
    public static final int DATA_ENTRY_COUNT = COOLDOWN_SPEARS + 1;

    // Maps for cleaner parsing of skills / XP / cooldowns
    private record SkillIndex(PrimarySkillType type, int index) {}
    private record AbilityIndex(SuperAbilityType type, int index) {}

    // All skill-level columns
    private static final List<SkillIndex> SKILL_LEVEL_INDICES = List.of(
            new SkillIndex(PrimarySkillType.ACROBATICS, SKILLS_ACROBATICS),
            new SkillIndex(PrimarySkillType.TAMING, SKILLS_TAMING),
            new SkillIndex(PrimarySkillType.MINING, SKILLS_MINING),
            new SkillIndex(PrimarySkillType.REPAIR, SKILLS_REPAIR),
            new SkillIndex(PrimarySkillType.WOODCUTTING, SKILLS_WOODCUTTING),
            new SkillIndex(PrimarySkillType.UNARMED, SKILLS_UNARMED),
            new SkillIndex(PrimarySkillType.HERBALISM, SKILLS_HERBALISM),
            new SkillIndex(PrimarySkillType.EXCAVATION, SKILLS_EXCAVATION),
            new SkillIndex(PrimarySkillType.ARCHERY, SKILLS_ARCHERY),
            new SkillIndex(PrimarySkillType.SWORDS, SKILLS_SWORDS),
            new SkillIndex(PrimarySkillType.AXES, SKILLS_AXES),
            new SkillIndex(PrimarySkillType.FISHING, SKILLS_FISHING),
            new SkillIndex(PrimarySkillType.ALCHEMY, SKILLS_ALCHEMY),
            new SkillIndex(PrimarySkillType.CROSSBOWS, SKILLS_CROSSBOWS),
            new SkillIndex(PrimarySkillType.TRIDENTS, SKILLS_TRIDENTS),
            new SkillIndex(PrimarySkillType.MACES, SKILLS_MACES),
            new SkillIndex(PrimarySkillType.SPEARS, SKILLS_SPEARS)
    );

    // All skill XP columns
    private static final List<SkillIndex> SKILL_XP_INDICES = List.of(
            new SkillIndex(PrimarySkillType.TAMING, EXP_TAMING),
            new SkillIndex(PrimarySkillType.MINING, EXP_MINING),
            new SkillIndex(PrimarySkillType.REPAIR, EXP_REPAIR),
            new SkillIndex(PrimarySkillType.WOODCUTTING, EXP_WOODCUTTING),
            new SkillIndex(PrimarySkillType.UNARMED, EXP_UNARMED),
            new SkillIndex(PrimarySkillType.HERBALISM, EXP_HERBALISM),
            new SkillIndex(PrimarySkillType.EXCAVATION, EXP_EXCAVATION),
            new SkillIndex(PrimarySkillType.ARCHERY, EXP_ARCHERY),
            new SkillIndex(PrimarySkillType.SWORDS, EXP_SWORDS),
            new SkillIndex(PrimarySkillType.AXES, EXP_AXES),
            new SkillIndex(PrimarySkillType.ACROBATICS, EXP_ACROBATICS),
            new SkillIndex(PrimarySkillType.FISHING, EXP_FISHING),
            new SkillIndex(PrimarySkillType.ALCHEMY, EXP_ALCHEMY),
            new SkillIndex(PrimarySkillType.CROSSBOWS, EXP_CROSSBOWS),
            new SkillIndex(PrimarySkillType.TRIDENTS, EXP_TRIDENTS),
            new SkillIndex(PrimarySkillType.MACES, EXP_MACES),
            new SkillIndex(PrimarySkillType.SPEARS, EXP_SPEARS)
    );

    // All ability cooldown columns
    private static final List<AbilityIndex> ABILITY_COOLDOWN_INDICES = List.of(
            new AbilityIndex(SuperAbilityType.SUPER_BREAKER, COOLDOWN_SUPER_BREAKER),
            new AbilityIndex(SuperAbilityType.TREE_FELLER, COOLDOWN_TREE_FELLER),
            new AbilityIndex(SuperAbilityType.BERSERK, COOLDOWN_BERSERK),
            new AbilityIndex(SuperAbilityType.GREEN_TERRA, COOLDOWN_GREEN_TERRA),
            new AbilityIndex(SuperAbilityType.GIGA_DRILL_BREAKER, COOLDOWN_GIGA_DRILL_BREAKER),
            new AbilityIndex(SuperAbilityType.EXPLOSIVE_SHOT, COOLDOWN_ARCHERY),
            new AbilityIndex(SuperAbilityType.SERRATED_STRIKES, COOLDOWN_SERRATED_STRIKES),
            new AbilityIndex(SuperAbilityType.SKULL_SPLITTER, COOLDOWN_SKULL_SPLITTER),
            new AbilityIndex(SuperAbilityType.BLAST_MINING, COOLDOWN_BLAST_MINING),
            new AbilityIndex(SuperAbilityType.SUPER_SHOTGUN, COOLDOWN_SUPER_SHOTGUN),
            new AbilityIndex(SuperAbilityType.TRIDENTS_SUPER_ABILITY, COOLDOWN_TRIDENTS),
            new AbilityIndex(SuperAbilityType.MACES_SUPER_ABILITY, COOLDOWN_MACES),
            new AbilityIndex(SuperAbilityType.SPEARS_SUPER_ABILITY, COOLDOWN_SPEARS)
    );

    FlatFileDatabaseManager(@NotNull File usersFile, @NotNull Logger logger, long purgeTime,
            int startingLevel, boolean testing) {
        this.usersFile = usersFile;
        this.usersFilePath = usersFile.getPath();
        this.logger = logger;
        this.purgeTime = purgeTime;
        this.startingLevel = startingLevel;

        if (!usersFile.exists()) {
            initEmptyDB();
        }

        if (!testing) {
            List<FlatFileDataFlag> flatFileDataFlags = checkFileHealthAndStructure();

            if (flatFileDataFlags != null && !flatFileDataFlags.isEmpty()) {
                logger.info("Detected " + flatFileDataFlags.size()
                        + " data entries which need correction.");
            }

            updateLeaderboards();
        }
    }

    FlatFileDatabaseManager(@NotNull String usersFilePath,
            @NotNull Logger logger,
            long purgeTime,
            int startingLevel) {
        this(new File(usersFilePath), logger, purgeTime, startingLevel, false);
    }

    // ------------------------------------------------------------------------
    // Purge & cleanup
    // ------------------------------------------------------------------------

    public int purgePowerlessUsers() {
        int purgedUsers = 0;

        LogUtils.debug(logger, "Purging powerless users...");

        synchronized (fileWritingLock) {
            StringBuilder writer = new StringBuilder();

            try (BufferedReader in = newBufferedReader()) {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");
                    Map<PrimarySkillType, Integer> skills = getSkillMapFromLine(character);

                    boolean powerless = skills.values().stream().allMatch(skill -> skill == 0);

                    if (!powerless) {
                        writer.append(line).append(LINE_ENDING);
                    } else {
                        purgedUsers++;
                    }
                }
            } catch (IOException e) {
                logger.severe("Exception while reading " + usersFilePath
                        + " (Are you sure you formatted it correctly?)" + e);
            }

            writeStringToFileSafely(writer.toString());
        }

        logger.info("Purged " + purgedUsers + " users from the database.");
        return purgedUsers;
    }

    public void purgeOldUsers() {
        int[] removedPlayers = {0};
        long currentTime = System.currentTimeMillis();

        LogUtils.debug(logger, "Purging old users...");

        rewriteUsersFile(line -> {
            final FlatFileRow row = FlatFileRow.parse(line, logger, usersFilePath);
            if (row == null) {
                // Comment / empty / malformed: keep as-is
                return line;
            }

            String[] data = row.fields();
            if (data.length <= UUID_INDEX) {
                // Not enough fields; preserve line
                return line;
            }

            String uuidString = data[UUID_INDEX];
            UUID uuid = parseUuidOrNull(uuidString);

            long lastPlayed = 0L;
            boolean rewrite = false;

            try {
                lastPlayed = Long.parseLong(data[OVERHAUL_LAST_LOGIN]);
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE,
                        "Could not parse last played time for user with UUID " + uuidString
                                + ", attempting to correct...", e);
            }

            if (lastPlayed == -1 && uuid != null) {
                OfflinePlayer player = mcMMO.p.getServer().getOfflinePlayer(uuid);
                if (player.getLastPlayed() != 0) {
                    lastPlayed = player.getLastPlayed();
                    rewrite = true;
                }
            }

            if (lastPlayed < 1 && (currentTime - lastPlayed > purgeTime)) {
                removedPlayers[0]++;
                return null; // drop this user
            }

            if (rewrite) {
                data[OVERHAUL_LAST_LOGIN] = Long.toString(lastPlayed);
                return org.apache.commons.lang3.StringUtils.join(data, ":");
            }

            return line;
        });

        logger.info("Purged " + removedPlayers[0] + " users from the database.");
    }

    public boolean removeUser(String playerName, UUID uuid) {
        // NOTE: UUID is unused for FlatFile for this interface implementation
        final String targetName = playerName;
        final boolean[] worked = {false};

        rewriteUsersFile(line -> {
            FlatFileRow row = FlatFileRow.parse(line, logger, usersFilePath);
            if (row == null) {
                return line; // comments / malformed stay
            }

            if (!worked[0] && row.username().equalsIgnoreCase(targetName)) {
                logger.info("User found, removing...");
                worked[0] = true;
                return null; // drop this line
            }

            return line;
        });

        Misc.profileCleanup(playerName);
        return worked[0];
    }

    @Override
    public void cleanupUser(UUID uuid) {
        // Not used in FlatFile
    }

    // ------------------------------------------------------------------------
    // Save / load users
    // ------------------------------------------------------------------------

    public boolean saveUser(@NotNull PlayerProfile profile) {
        String playerName = profile.getPlayerName();
        UUID uuid = profile.getUniqueId();

        boolean corruptDataFound = false;

        synchronized (fileWritingLock) {
            StringBuilder writer = new StringBuilder();

            try (BufferedReader in = newBufferedReader()) {
                String line;
                boolean wroteUser = false;

                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#")) {
                        writer.append(line).append(LINE_ENDING);
                        continue;
                    }

                    if (!line.contains(":")) {
                        corruptDataFound = logCorruptOnce(corruptDataFound);
                        continue;
                    }

                    String[] splitData = line.split(":");

                    // Not enough entries to even contain a UUID
                    if (splitData.length < UUID_INDEX) {
                        corruptDataFound = logCorruptOnce(corruptDataFound);
                        continue;
                    }

                    boolean uuidMatches = uuid != null
                            && splitData.length > UUID_INDEX
                            && splitData[UUID_INDEX].equalsIgnoreCase(uuid.toString());
                    boolean nameMatches = splitData.length > USERNAME_INDEX
                            && splitData[USERNAME_INDEX].equalsIgnoreCase(playerName);

                    if (!uuidMatches && !nameMatches) {
                        // not the user, keep the line
                        writer.append(line).append(LINE_ENDING);
                    } else {
                        writeUserToLine(profile, writer);
                        wroteUser = true;
                    }
                }

                if (!wroteUser) {
                    writeUserToLine(profile, writer);
                }

                writeStringToFileSafely(writer.toString());
                return true;
            } catch (Exception e) {
                logger.log(Level.SEVERE,
                        "Unexpected Exception while reading " + usersFilePath, e);
                return false;
            }
        }
    }

    private boolean logCorruptOnce(boolean alreadyLogged) {
        if (!alreadyLogged) {
            logger.severe(
                    "mcMMO found some unexpected or corrupted data in mcmmo.users and is removing it, it is possible some data has been lost.");
        }
        return true;
    }

    public void writeUserToLine(@NotNull PlayerProfile profile,
            @NotNull Appendable out) throws IOException {

        // Username
        appendString(out, profile.getPlayerName());

        // MINING level / placeholders / XP
        appendInt(out, profile.getSkillLevel(PrimarySkillType.MINING));
        appendIgnored(out);  // old data
        appendIgnored(out);  // old data
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.MINING));

        // WOODCUTTING
        appendInt(out, profile.getSkillLevel(PrimarySkillType.WOODCUTTING));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.WOODCUTTING));

        // REPAIR / UNARMED / HERBALISM / EXCAVATION / ARCHERY / SWORDS / AXES / ACROBATICS
        appendInt(out, profile.getSkillLevel(PrimarySkillType.REPAIR));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.UNARMED));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.HERBALISM));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.EXCAVATION));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.ARCHERY));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.SWORDS));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.AXES));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.ACROBATICS));

        // XP for same block
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.REPAIR));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.UNARMED));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.HERBALISM));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.EXCAVATION));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.ARCHERY));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.SWORDS));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.AXES));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.ACROBATICS));

        // Placeholder, TAMING, TAMING XP
        appendIgnored(out);
        appendInt(out, profile.getSkillLevel(PrimarySkillType.TAMING));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.TAMING));

        // Ability cooldowns
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.BERSERK));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.GIGA_DRILL_BREAKER));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.TREE_FELLER));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.GREEN_TERRA));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.SERRATED_STRIKES));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.SKULL_SPLITTER));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.SUPER_BREAKER));

        // Placeholder
        appendIgnored(out);

        // FISHING
        appendInt(out, profile.getSkillLevel(PrimarySkillType.FISHING));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.FISHING));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.BLAST_MINING));

        // Legacy / mob health bar
        appendIgnored(out); // Legacy last login
        appendIgnored(out); // Mob health bar

        // ALCHEMY
        appendInt(out, profile.getSkillLevel(PrimarySkillType.ALCHEMY));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.ALCHEMY));

        // UUID
        appendString(out, profile.getUniqueId() != null ? profile.getUniqueId().toString() : "NULL");

        // Misc data
        appendInt(out, profile.getScoreboardTipsShown());
        appendLong(out, profile.getUniqueData(UniqueDataType.CHIMAERA_WING_DATS));
        appendLong(out, profile.getLastLogin());

        // CROSSBOWS / TRIDENTS / MACES / SPEARS XP + levels + ability cooldowns
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.CROSSBOWS));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.CROSSBOWS));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.TRIDENTS));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.TRIDENTS));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.SUPER_SHOTGUN));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.TRIDENTS_SUPER_ABILITY));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.EXPLOSIVE_SHOT));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.MACES));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.MACES));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.MACES_SUPER_ABILITY));
        appendInt(out, profile.getSkillXpLevel(PrimarySkillType.SPEARS));
        appendInt(out, profile.getSkillLevel(PrimarySkillType.SPEARS));
        appendLong(out, profile.getAbilityDATS(SuperAbilityType.SPEARS_SUPER_ABILITY));

        out.append(LINE_ENDING);
    }

    public @NotNull PlayerProfile newUser(@NotNull Player player) {
        return new PlayerProfile(player.getName(), player.getUniqueId(), true, startingLevel);
    }

    public @NotNull PlayerProfile newUser(@NotNull String playerName, @NotNull UUID uuid) {
        PlayerProfile playerProfile = new PlayerProfile(playerName, uuid, true, startingLevel);

        synchronized (fileWritingLock) {
            StringBuilder stringBuilder = new StringBuilder();

            try (BufferedReader bufferedReader = newBufferedReader()) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append(LINE_ENDING);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Unexpected Exception while reading " + usersFilePath, e);
            }

            try (FileWriter fileWriter = new FileWriter(usersFile)) {
                writeUserToLine(playerProfile, stringBuilder);
                fileWriter.write(stringBuilder.toString());
            } catch (Exception e) {
                logger.log(Level.SEVERE,
                        "Unexpected Exception while writing to " + usersFilePath, e);
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

    private @NotNull UserQuery getUserQuery(@Nullable UUID uuid,
            @Nullable String playerName) {
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

    private @NotNull PlayerProfile processUserQuery(@NotNull UserQuery userQuery) {
        return switch (userQuery.getType()) {
            case UUID_AND_NAME -> queryByUUIDAndName((UserQueryFull) userQuery);
            case UUID -> queryByUUID((UserQueryUUID) userQuery);
            case NAME -> queryByName((UserQueryNameImpl) userQuery);
        };
    }

    private @NotNull PlayerProfile queryByName(@NotNull UserQueryName userQuery) {
        String playerName = userQuery.getName();

        synchronized (fileWritingLock) {
            try (BufferedReader in = newBufferedReader()) {
                String line;

                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }

                    String[] rawSplitData = line.split(":");

                    if (rawSplitData.length < (USERNAME_INDEX + 1)) {
                        continue;
                    }

                    if (playerName.equalsIgnoreCase(rawSplitData[USERNAME_INDEX])) {
                        return loadFromLine(rawSplitData);
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE,
                        "Unexpected Exception while reading " + usersFilePath, e);
            }
        }

        return new PlayerProfile(playerName, new UUID(0L, 0L), startingLevel);
    }

    private @NotNull PlayerProfile queryByUUID(@NotNull UserQueryUUID userQuery) {
        UUID uuid = userQuery.getUUID();

        synchronized (fileWritingLock) {
            try (BufferedReader in = newBufferedReader()) {
                String line;

                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }

                    String[] rawSplitData = line.split(":");

                    if (rawSplitData.length < (UUID_INDEX + 1)) {
                        continue;
                    }

                    try {
                        UUID fromDataUUID = UUID.fromString(rawSplitData[UUID_INDEX]);
                        if (fromDataUUID.equals(uuid)) {
                            return loadFromLine(rawSplitData);
                        }
                    } catch (Exception e) {
                        // Ignore malformed UUIDs
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE,
                        "Unexpected Exception while reading " + usersFilePath, e);
            }
        }

        return grabUnloadedProfile(uuid, "Player-Not-Found=" + uuid);
    }

    private @NotNull PlayerProfile queryByUUIDAndName(@NotNull UserQueryFull userQuery) {
        String playerName = userQuery.getName();
        UUID uuid = userQuery.getUUID();

        synchronized (fileWritingLock) {
            try (BufferedReader in = newBufferedReader()) {
                String line;

                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }

                    String[] rawSplitData = line.split(":");

                    if (rawSplitData.length < (UUID_INDEX + 1)) {
                        continue;
                    }

                    try {
                        UUID fromDataUUID = UUID.fromString(rawSplitData[UUID_INDEX]);
                        if (fromDataUUID.equals(uuid)) {
                            String dbPlayerName = rawSplitData[USERNAME_INDEX];
                            boolean matchingName = dbPlayerName.equalsIgnoreCase(playerName);

                            if (!matchingName) {
                                logger.warning(
                                        "When loading user: " + playerName + " with UUID of ("
                                                + uuid + ") we found a mismatched name, the name in the DB will be replaced (DB name: "
                                                + dbPlayerName + ")");
                                rawSplitData[USERNAME_INDEX] = playerName;
                            }

                            return loadFromLine(rawSplitData);
                        }
                    } catch (Exception e) {
                        // Ignore malformed UUIDs
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE,
                        "Unexpected Exception while reading " + usersFilePath, e);
            }
        }

        return grabUnloadedProfile(uuid, playerName);
    }

    private @NotNull PlayerProfile grabUnloadedProfile(@NotNull UUID uuid,
            @Nullable String playerName) {
        String name = (playerName == null) ? "" : playerName;
        return new PlayerProfile(name, uuid, 0);
    }

    // ------------------------------------------------------------------------
    // Conversion / UUID updates
    // ------------------------------------------------------------------------

    public void convertUsers(DatabaseManager destination) {
        int convertedUsers = 0;
        long startMillis = System.currentTimeMillis();

        synchronized (fileWritingLock) {
            try (BufferedReader reader = newBufferedReader()) {
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    String[] character = line.split(":");

                    try {
                        destination.saveUser(loadFromLine(character));
                    } catch (Exception e) {
                        String username = (character.length > USERNAME_INDEX)
                                ? character[USERNAME_INDEX]
                                : "<unknown username>";
                        logger.log(Level.SEVERE,
                                "Could not convert user from FlatFile to SQL DB: " + username, e);
                    }

                    convertedUsers++;
                    Misc.printProgress(convertedUsers, progressInterval, startMillis);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE,
                        "Failed to convert users from FlatFile to SQL DB", e);
            }
        }
    }

    public boolean saveUserUUID(String userName, UUID uuid) {
        boolean worked = false;
        int entriesWritten = 0;

        synchronized (fileWritingLock) {
            StringBuilder writer = new StringBuilder();

            try (BufferedReader in = newBufferedReader()) {
                String line;

                while ((line = in.readLine()) != null) {
                    String[] character = line.split(":");

                    if (!worked && character.length > USERNAME_INDEX
                            && character[USERNAME_INDEX].equalsIgnoreCase(userName)) {
                        if (character.length < 42) {
                            logger.severe("Could not update UUID for " + userName + "!");
                            logger.severe("Database entry is invalid.");
                            // still append original line
                        } else {
                            line = line.replace(character[UUID_INDEX], uuid.toString());
                            worked = true;
                        }
                    }

                    entriesWritten++;
                    writer.append(line).append(LINE_ENDING);
                }
            } catch (Exception e) {
                logger.severe("Exception while reading " + usersFilePath
                        + " (Are you sure you formatted it correctly?)" + e);
            }

            LogUtils.debug(logger,
                    entriesWritten + " entries written while saving UUID for " + userName);
            writeStringToFileSafely(writer.toString());
        }

        return worked;
    }

    public boolean saveUserUUIDs(Map<String, UUID> fetchedUUIDs) {
        int[] entriesWritten = {0};

        rewriteUsersFile(line -> {
            FlatFileRow row = FlatFileRow.parse(line, logger, usersFilePath);
            if (row == null) {
                entriesWritten[0]++;
                return line; // comments / empty / malformed unchanged
            }

            String[] character = row.fields();
            String username = row.username();

            if (username != null && fetchedUUIDs.containsKey(username)) {
                if (character.length < 42) {
                    logger.severe("Could not update UUID for " + username + "!");
                    logger.severe("Database entry is invalid.");
                } else {
                    character[UUID_INDEX] = fetchedUUIDs.remove(username).toString();
                    String updated = org.apache.commons.lang3.StringUtils.join(character, ":") + ":";
                    entriesWritten[0]++;
                    return updated;
                }
            }

            entriesWritten[0]++;
            return line;
        });

        LogUtils.debug(logger,
                entriesWritten[0] + " entries written while saving UUID batch");
        return true;
    }

    public List<String> getStoredUsers() {
        ArrayList<String> users = new ArrayList<>();

        withUsersFileLines(line -> {
            String[] character = line.split(":");
            if (character.length > USERNAME_INDEX) {
                users.add(character[USERNAME_INDEX]);
            }
        });

        return users;
    }

    // ------------------------------------------------------------------------
    // Leaderboards
    // ------------------------------------------------------------------------

    public @NotNull LeaderboardStatus updateLeaderboards() {
        long now = System.currentTimeMillis();
        if (now < lastUpdate + UPDATE_WAIT_TIME) {
            return LeaderboardStatus.TOO_SOON_TO_UPDATE;
        }

        lastUpdate = now;

        // Power level leaderboard
        TreeSet<PlayerStat> powerLevelStats = new TreeSet<>();

        // Per-skill leaderboards
        EnumMap<PrimarySkillType, TreeSet<PlayerStat>> perSkillSets =
                new EnumMap<>(PrimarySkillType.class);

        perSkillSets.put(PrimarySkillType.MINING,        new TreeSet<>());
        perSkillSets.put(PrimarySkillType.WOODCUTTING,   new TreeSet<>());
        perSkillSets.put(PrimarySkillType.REPAIR,        new TreeSet<>());
        perSkillSets.put(PrimarySkillType.UNARMED,       new TreeSet<>());
        perSkillSets.put(PrimarySkillType.HERBALISM,     new TreeSet<>());
        perSkillSets.put(PrimarySkillType.EXCAVATION,    new TreeSet<>());
        perSkillSets.put(PrimarySkillType.ARCHERY,       new TreeSet<>());
        perSkillSets.put(PrimarySkillType.SWORDS,        new TreeSet<>());
        perSkillSets.put(PrimarySkillType.AXES,          new TreeSet<>());
        perSkillSets.put(PrimarySkillType.ACROBATICS,    new TreeSet<>());
        perSkillSets.put(PrimarySkillType.TAMING,        new TreeSet<>());
        perSkillSets.put(PrimarySkillType.FISHING,       new TreeSet<>());
        perSkillSets.put(PrimarySkillType.ALCHEMY,       new TreeSet<>());
        perSkillSets.put(PrimarySkillType.CROSSBOWS,     new TreeSet<>());
        perSkillSets.put(PrimarySkillType.TRIDENTS,      new TreeSet<>());
        perSkillSets.put(PrimarySkillType.MACES,         new TreeSet<>());
        perSkillSets.put(PrimarySkillType.SPEARS,        new TreeSet<>());

        String playerName = null;

        synchronized (fileWritingLock) {
            try (BufferedReader in = newBufferedReader()) {
                String line;
                while ((line = in.readLine()) != null) {
                    FlatFileRow row = FlatFileRow.parse(line, logger, usersFilePath);
                    if (row == null) {
                        continue; // comment / empty / malformed
                    }

                    playerName = row.username();
                    String[] data = row.fields();

                    Map<PrimarySkillType, Integer> skills = getSkillMapFromLine(data);
                    int powerLevel = addAllSkillStats(playerName, skills, perSkillSets);
                    putStat(powerLevelStats, playerName, powerLevel);
                }
            } catch (IOException e) {
                logger.severe("Exception while reading " + usersFilePath + " during user "
                        + playerName + " (Are you sure you formatted it correctly?) " + e);
                return LeaderboardStatus.FAILED;
            }
        }

        // Freeze current leaderboards as immutable lists
        powerLevels = List.copyOf(powerLevelStats);

        for (Map.Entry<PrimarySkillType, TreeSet<PlayerStat>> entry : perSkillSets.entrySet()) {
            leaderboardMap.put(entry.getKey(), List.copyOf(entry.getValue()));
        }

        return LeaderboardStatus.UPDATED;
    }

    private int addAllSkillStats(String playerName,
            Map<PrimarySkillType, Integer> skills,
            Map<PrimarySkillType, TreeSet<PlayerStat>> perSkillSets) {
        int powerLevel = 0;

        for (Map.Entry<PrimarySkillType, TreeSet<PlayerStat>> entry : perSkillSets.entrySet()) {
            PrimarySkillType skill = entry.getKey();
            TreeSet<PlayerStat> set = entry.getValue();

            int value = skills.getOrDefault(skill, 0);
            powerLevel += putStat(set, playerName, value);
        }

        return powerLevel;
    }

    public @NotNull List<PlayerStat> readLeaderboard(@Nullable PrimarySkillType primarySkillType,
            int pageNumber,
            int statsPerPage) throws InvalidSkillException {

        if (primarySkillType != null && SkillTools.isChildSkill(primarySkillType)) {
            logger.severe(
                    "A plugin hooking into mcMMO is being naughty with our database commands, update all plugins that hook into mcMMO and contact their devs!");
            throw new InvalidSkillException(
                    "A plugin hooking into mcMMO that you are using is attempting to read leaderboard skills for child skills, child skills do not have leaderboards! This is NOT an mcMMO error!");
        }

        updateLeaderboards();

        List<PlayerStat> statsList =
                (primarySkillType == null) ? powerLevels : leaderboardMap.get(primarySkillType);

        if (statsList == null) {
            return List.of();
        }

        int fromIndex = (Math.max(pageNumber, 1) - 1) * statsPerPage;
        int start = Math.min(fromIndex, statsList.size());
        int end = Math.min(fromIndex + statsPerPage, statsList.size());

        return statsList.subList(start, end);
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

    // ------------------------------------------------------------------------
    // DB file creation / validation
    // ------------------------------------------------------------------------

    private void initEmptyDB() {
        synchronized (fileWritingLock) {
            try (BufferedWriter bufferedWriter =
                    new BufferedWriter(new FileWriter(usersFilePath, true))) {

                DateTimeFormatter dateTimeFormatter =
                        DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
                LocalDateTime localDateTime = LocalDateTime.now();

                bufferedWriter.append("# mcMMO Database created on ")
                        .append(localDateTime.format(dateTimeFormatter))
                        .append(LINE_ENDING);
            } catch (IOException e) {
                logger.log(Level.SEVERE,
                        "Unexpected Exception while initializing " + usersFilePath, e);
            }
        }
    }

    public @Nullable List<FlatFileDataFlag> checkFileHealthAndStructure() {
        ArrayList<FlatFileDataFlag> flagsFound = null;

        LogUtils.debug(logger, "(" + usersFile.getPath() + ") Validating database file..");

        if (!usersFile.exists()) {
            return null;
        }

        synchronized (fileWritingLock) {
            FlatFileDataProcessor dataProcessor = new FlatFileDataProcessor(logger);

            String dbCommentDate = null;

            try (BufferedReader bufferedReader = newBufferedReader()) {
                String currentLine;

                while ((currentLine = bufferedReader.readLine()) != null) {
                    if (currentLine.startsWith("#") && dbCommentDate == null) {
                        dbCommentDate = currentLine;
                        continue;
                    }

                    if (currentLine.isEmpty()) {
                        continue;
                    }

                    dataProcessor.processData(currentLine);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE,
                        "Unexpected Exception while validating " + usersFilePath, e);
            }

            if (!dataProcessor.getFlatFileDataFlags().isEmpty()) {
                flagsFound = new ArrayList<>(dataProcessor.getFlatFileDataFlags());
                logger.info("Updating FlatFile Database...");

                try (FileWriter fileWriter = new FileWriter(usersFilePath)) {
                    if (dbCommentDate != null) {
                        fileWriter.write(dbCommentDate + LINE_ENDING);
                    }
                    fileWriter.write(dataProcessor.processDataForSave().toString());
                } catch (IOException e) {
                    logger.log(Level.SEVERE,
                            "Unexpected Exception while writing " + usersFilePath, e);
                }
            }
        }

        if (flagsFound == null || flagsFound.isEmpty()) {
            return null;
        }

        return flagsFound;
    }

    // ------------------------------------------------------------------------
    // Line parsing helpers
    // ------------------------------------------------------------------------

    private PlayerProfile loadFromLine(@NotNull String[] character) {
        Map<PrimarySkillType, Integer> skills = getSkillMapFromLine(character);
        Map<PrimarySkillType, Float> skillsXp = new EnumMap<>(PrimarySkillType.class);
        Map<SuperAbilityType, Integer> skillsDATS = new EnumMap<>(SuperAbilityType.class);
        Map<UniqueDataType, Integer> uniquePlayerDataMap = new EnumMap<>(UniqueDataType.class);

        String username = character[USERNAME_INDEX];

        // XP values
        for (SkillIndex skillIndex : SKILL_XP_INDICES) {
            tryLoadSkillFloatValuesFromRawData(
                    skillsXp,
                    character,
                    skillIndex.type(),
                    skillIndex.index(),
                    username
            );
        }

        // Ability cooldowns
        for (AbilityIndex abilityIndex : ABILITY_COOLDOWN_INDICES) {
            tryLoadSkillCooldownFromRawData(skillsDATS, character, abilityIndex.type(),
                    abilityIndex.index(), username);
        }

        UUID uuid = parseUuidOrNull(character[UUID_INDEX]);

        int scoreboardTipsShown;
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

        long lastLogin;
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
            logger.severe("Data corruption when trying to load the value for skill "
                    + primarySkillType + " for player named " + userName + " setting value to zero");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void tryLoadSkillIntValuesFromRawData(@NotNull Map<PrimarySkillType, Integer> skillMap,
            @NotNull String[] character, @NotNull PrimarySkillType primarySkillType, int index,
            @NotNull String userName) {
        try {
            int valueFromString = Integer.parseInt(character[index]);
            skillMap.put(primarySkillType, valueFromString);
        } catch (ArrayIndexOutOfBoundsException e) {
            skillMap.put(primarySkillType, 0);
        } catch (NumberFormatException e) {
            skillMap.put(primarySkillType, 0);
            logger.severe("Data corruption when trying to load the value for skill "
                    + primarySkillType + " for player named " + userName + " setting value to zero");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private @NotNull Map<PrimarySkillType, Integer> getSkillMapFromLine(
            @NotNull String[] character) {

        EnumMap<PrimarySkillType, Integer> skills = new EnumMap<>(PrimarySkillType.class);

        String username = character[USERNAME_INDEX];

        for (SkillIndex skillIndex : SKILL_LEVEL_INDICES) {
            tryLoadSkillIntValuesFromRawData(skills, character, skillIndex.type(),
                    skillIndex.index(), username);
        }

        return skills;
    }

    // ------------------------------------------------------------------------
    // Type / IO helpers
    // ------------------------------------------------------------------------

    private @NotNull BufferedReader newBufferedReader() throws IOException {
        return new BufferedReader(new FileReader(usersFilePath));
    }

    private void writeStringToFileSafely(String contents) {
        try (FileWriter out = new FileWriter(usersFilePath)) {
            out.write(contents);
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Unexpected Exception while writing " + usersFilePath, e);
        }
    }

    private void withUsersFileLines(@NotNull Consumer<String> lineConsumer) {
        synchronized (fileWritingLock) {
            try (BufferedReader in = newBufferedReader()) {
                String line;
                while ((line = in.readLine()) != null) {
                    lineConsumer.accept(line);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE,
                        "Unexpected Exception while reading " + usersFilePath, e);
            }
        }
    }

    private void rewriteUsersFile(@NotNull Function<String, String> lineMapper) {
        synchronized (fileWritingLock) {
            StringBuilder writer = new StringBuilder();

            try (BufferedReader in = newBufferedReader()) {
                String line;
                while ((line = in.readLine()) != null) {
                    String mapped = lineMapper.apply(line);
                    if (mapped != null) {
                        writer.append(mapped).append(LINE_ENDING);
                    }
                }
            } catch (IOException e) {
                logger.severe("Exception while reading " + usersFilePath
                        + " (Are you sure you formatted it correctly?)" + e);
            }

            writeStringToFileSafely(writer.toString());
        }
    }

    private @Nullable UUID parseUuidOrNull(@Nullable String uuidString) {
        if (uuidString == null || uuidString.isEmpty()
                || "NULL".equalsIgnoreCase(uuidString)) {
            return null;
        }

        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // DatabaseManager API
    // ------------------------------------------------------------------------

    public DatabaseType getDatabaseType() {
        return DatabaseType.FLATFILE;
    }

    public @NotNull File getUsersFile() {
        return usersFile;
    }

    @Override
    public void onDisable() {
        // nothing to do
    }

    private void appendInt(@NotNull Appendable out, int value) throws IOException {
        out.append(Integer.toString(value)).append(':');
    }

    private void appendLong(@NotNull Appendable out, long value) throws IOException {
        out.append(Long.toString(value)).append(':');
    }

    private void appendString(@NotNull Appendable out, @NotNull String value) throws IOException {
        out.append(value).append(':');
    }

    private void appendIgnored(@NotNull Appendable out) throws IOException {
        out.append(IGNORED).append(':');
    }

    private record FlatFileRow(String rawLine, String[] fields, String username,
                               @Nullable UUID uuid) {

        static @Nullable FlatFileRow parse(@NotNull String line,
                    @NotNull Logger logger,
                    @NotNull String usersFilePath) {
                String trimmed = line.trim();

                // Skip comments and empty lines
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    return null;
                }

                String[] data = trimmed.split(":");
                if (data.length <= USERNAME_INDEX) {
                    // Not enough data to contain a username; treat as malformed and skip
                    logger.warning("Skipping malformed line in " + usersFilePath + ": " + trimmed);
                    return null;
                }

                String username = data[USERNAME_INDEX];
                UUID uuid = null;
                if (data.length > UUID_INDEX) {
                    try {
                        String uuidString = data[UUID_INDEX];
                        if (!uuidString.isEmpty() && !"NULL".equalsIgnoreCase(uuidString)) {
                            uuid = UUID.fromString(uuidString);
                        }
                    } catch (IllegalArgumentException ignored) {
                        // Malformed UUID; we keep uuid = null
                    }
                }

                return new FlatFileRow(line, data, username, uuid);
            }
        }
}
