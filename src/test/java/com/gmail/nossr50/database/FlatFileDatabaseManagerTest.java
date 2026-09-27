package com.gmail.nossr50.database;

import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_BERSERK;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_SPEARS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.OVERHAUL_LAST_LOGIN;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.USERNAME_INDEX;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.UUID_INDEX;
import static com.gmail.nossr50.database.UsernamePlaceholder.INVALID_OLD_USERNAME;
import static com.gmail.nossr50.database.UsernamePlaceholder.LEGACY_FLATFILE_INVALID_OLD_USERNAME;
import static com.gmail.nossr50.util.skills.SkillTools.isChildSkill;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.database.flatfile.LeaderboardStatus;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerNameAndUUID;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.player.UniqueDataType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillTools;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@Tag("docker")
class FlatFileDatabaseManagerTest {

    private static File testDataFolder;
    private static MockedStatic<ExperienceConfig> mockedExperienceConfig;

    public static final @NotNull String TEST_FILE_NAME = "test.mcmmo.users";
    public static final @NotNull String BAD_FILE_LINE_ONE = "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:";
    public static final @NotNull String BAD_DATA_FILE_LINE_TWENTY_THREE = "nossr51:baddata:::baddata:baddata:640:baddata:1000:1000:1000:baddata:baddata:baddata:baddata:16:0:500:20273:0:0:0:0::1000:0:0:baddata:1593543012:0:0:0:0::1000:0:0:baddata:IGNORED:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:1:0:";
    public static final @NotNull String DB_BADDATA = "baddatadb.users";
    public static final @NotNull String DB_HEALTHY = "healthydb.users";
    public static final @NotNull String HEALTHY_DB_LINE_ONE_UUID_STR = "588fe472-1c82-4c4e-9aa1-7eefccb277e3";
    public static final @NotNull String DB_MISSING_LAST_LOGIN = "missinglastlogin.users";

    private static File tempDir;
    private static final @NotNull Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String EXISTING_PLAYER = "nossr50";
    private static final UUID EXISTING_PLAYER_UUID = UUID.fromString(HEALTHY_DB_LINE_ONE_UUID_STR);

    private final long PURGE_TIME = 2_630_000_000L; // ~30 days in ms

    // Making them all unique makes it easier on us to edit this stuff later
    int expectedLvlMining = 1, expectedLvlWoodcutting = 2, expectedLvlRepair = 3,
            expectedLvlUnarmed = 4, expectedLvlHerbalism = 5, expectedLvlExcavation = 6,
            expectedLvlArchery = 7, expectedLvlSwords = 8, expectedLvlAxes = 9,
            expectedLvlAcrobatics = 10, expectedLvlTaming = 11, expectedLvlFishing = 12,
            expectedLvlAlchemy = 13, expectedLvlCrossbows = 14, expectedLvlTridents = 15,
            expectedLvlMaces = 16, expectedLvlSpears = 17;

    float expectedExpMining = 10, expectedExpWoodcutting = 20, expectedExpRepair = 30,
            expectedExpUnarmed = 40, expectedExpHerbalism = 50, expectedExpExcavation = 60,
            expectedExpArchery = 70, expectedExpSwords = 80, expectedExpAxes = 90,
            expectedExpAcrobatics = 100, expectedExpTaming = 110, expectedExpFishing = 120,
            expectedExpAlchemy = 130, expectedExpCrossbows = 140, expectedExpTridents = 150,
            expectedExpMaces = 160, expectedExpSpears = 170;

    long expectedBerserkCd = 111, expectedGigaDrillBreakerCd = 222, expectedTreeFellerCd = 333,
            expectedGreenTerraCd = 444, expectedSerratedStrikesCd = 555,
            expectedSkullSplitterCd = 666, expectedSuperBreakerCd = 777,
            expectedBlastMiningCd = 888, expectedChimaeraWingCd = 999,
            expectedSuperShotgunCd = 1111, expectedTridentSuperCd = 2222,
            expectedExplosiveShotCd = 3333, expectedMacesSuperCd = 4444,
            expectedSpearsSuperCd = 5555;

    int expectedScoreboardTips = 1111;
    Long expectedLastLogin = 2020L;

    @BeforeAll
    static void initBeforeAll() {
        logger.setFilter(new DebugFilter());
        // GIVEN a fully mocked mcMMO environment
        mcMMO.p = Mockito.mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);
        try {
            testDataFolder = java.nio.file.Files.createTempDirectory("mcmmo-flatfile-test-data-").toFile();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to create temp test data folder", e);
        }
        when(mcMMO.p.getDataFolder()).thenReturn(testDataFolder);

        ExperienceConfig experienceConfig = Mockito.mock(ExperienceConfig.class);
        when(experienceConfig.getDiminishedReturnsEnabled()).thenReturn(false);
        mockedExperienceConfig = Mockito.mockStatic(ExperienceConfig.class);
        mockedExperienceConfig.when(ExperienceConfig::getInstance).thenReturn(experienceConfig);

        // Null player lookup, shouldn't affect tests
        Server server = mock(Server.class);
        when(mcMMO.p.getServer()).thenReturn(server);
        when(server.getPlayerExact(anyString()))
                .thenReturn(null);
    }

    @BeforeEach
    void initEachTest() {
        //noinspection UnstableApiUsage
        tempDir = Files.createTempDir();
    }

    private @NotNull String getTemporaryUserFilePath() {
        return tempDir.getPath() + File.separator + TEST_FILE_NAME;
    }

    @AfterAll
    static void tearDownAll() {
        if (mockedExperienceConfig != null) {
            mockedExperienceConfig.close();
        }
        if (testDataFolder != null) {
            recursiveDelete(testDataFolder);
        }
    }

    @AfterEach
    void tearDown() {
        recursiveDelete(tempDir);
    }

    // Nothing wrong with this database
    private static final String[] normalDatabaseData = {
            "nossr50:1:IGNORED:IGNORED:10:2:20:3:4:5:6:7:8:9:10:30:40:50:60:70:80:90:100:IGNORED:11:110:111:222:333:444:555:666:777:IGNORED:12:120:888:IGNORED:HEARTS:13:130:588fe472-1c82-4c4e-9aa1-7eefccb277e3:1111:999:2020:140:14:150:15:1111:2222:3333:160:16:4444:170:17:5555:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:3030:0:0:0:0:0:0:0:0:0:0:0:0:0:",
            "powerless:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:1337:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:4040:0:0:0:0:0:0:0:0:0:0:0:0:0:"
    };

    private static final String[] badUUIDDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "z750:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:3:5:1600906906:",
            // This one has an incorrect UUID representation
            "powerless:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:"
    };

    private static final String[] outdatedDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "electronicboy:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:"
            // This user is missing data added after UUID index
    };

    private static final String[] emptyLineDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "kashike:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:",
            "" // EMPTY LINE
    };

    private static final String[] emptyNameDatabaseData = {
            ":1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "aikar:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:"
    };

    private static final String[] duplicateNameDatabaseData = {
            "mochi:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mochi:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:631e3896-da2a-4077-974b-d047859d76bc:0:0:",
    };

    private static final String[] duplicateUUIDDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
    };

    private static final String[] corruptDatabaseData = {
            "nossr50:1000:::0:100:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "corruptdataboy:の:::ののの0:2452:0:1983:1937:1790:3042ののののの:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617のののののの583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:d20c6e8d-5615-4284-b8d1-e20b92011530:5:1600906906:",
            "のjapaneseuserの:333:::0:2452:0:444:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:25870f0e-7558-4659-9f60-417e24cb3332:5:1600906906:",
            "sameUUIDasjapaneseuser:333:::0:442:0:544:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:25870f0e-7558-4659-9f60-417e24cb3332:5:1600906906:",
    };

    private static final String[] badDatabaseData = {
            // First entry here is missing some values
            "nossr50:1000:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            // Second entry here has an integer value replaced by a string
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:badvalue:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:"
    };

    // ------------------------------------------------------------------------
    // Core initialization / smoke tests
    // ------------------------------------------------------------------------

    @Test
    void defaultInitCreatesDatabaseManagerAndUserFile() {
        // Given + When
        var databaseManager = new FlatFileDatabaseManager(getTemporaryUserFilePath(), logger, PURGE_TIME, 0);

        // Then
        assertNotNull(databaseManager);
        assertTrue(databaseManager.getUsersFile().exists());
    }

    @Test
    void updateLeaderboardsOnEmptyFileReturnsUpdated() {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When
        var status = databaseManager.updateLeaderboards();

        // Then
        assertEquals(LeaderboardStatus.UPDATED, status);
    }

    @Test
    void updateLeaderboardsCalledTwiceSecondCallReturnsTooSoon() {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When
        var firstStatus = databaseManager.updateLeaderboards();
        var secondStatus = databaseManager.updateLeaderboards();

        // Then
        assertEquals(LeaderboardStatus.UPDATED, firstStatus);
        assertEquals(LeaderboardStatus.TOO_SOON_TO_UPDATE, secondStatus);
    }

    @Test
    void updateLeaderboardsShouldThrottleWhenRefreshIntervalBelowFloor() {
        // Given - a refresh interval below the one-minute floor (0ms would disable throttling)
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true, 0L);

        // When - two rebuilds are requested back to back
        var firstStatus = databaseManager.updateLeaderboards();
        var secondStatus = databaseManager.updateLeaderboards();

        // Then - the floor is enforced, so the second rebuild is rejected as too soon
        assertThat(firstStatus).isEqualTo(LeaderboardStatus.UPDATED);
        assertThat(secondStatus).isEqualTo(LeaderboardStatus.TOO_SOON_TO_UPDATE);
    }

    // ------------------------------------------------------------------------
    // Save / load user tests
    // ------------------------------------------------------------------------

    @Test
    void saveUserPersistsUserAndOverwritesNameOnSecondSave() {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);
        UUID uuid = UUID.fromString(HEALTHY_DB_LINE_ONE_UUID_STR);
        String originalName = "nossr50";
        var originalProfile = new PlayerProfile(originalName, uuid, 0);

        // When – initial save
        assertTrue(databaseManager.getUsersFile().exists());
        assertTrue(databaseManager.saveUser(originalProfile));

        // Then – initial load
        var loadedProfile = databaseManager.loadPlayerProfile(uuid);
        assertTrue(loadedProfile.isLoaded());
        assertEquals(uuid, loadedProfile.getUniqueId());
        assertEquals(originalName, loadedProfile.getPlayerName());

        // Given – updated name
        String updatedName = "changedmyname";
        var updatedProfile = new PlayerProfile(updatedName, uuid, 0);

        // When – overwrite
        assertTrue(databaseManager.saveUser(updatedProfile));

        // Then – load again should reflect updated name
        var reloadedProfile = databaseManager.loadPlayerProfile(uuid);
        assertTrue(reloadedProfile.isLoaded());
        assertEquals(uuid, reloadedProfile.getUniqueId());
        assertEquals(updatedName, reloadedProfile.getPlayerName());
    }

    @Test
    void addedMissingLastLoginValuesAreSchemaUpgradedAndSetToMinusOne() {
        // Given
        File dbFile = prepareDatabaseTestResource(DB_MISSING_LAST_LOGIN);
        var databaseManager = new FlatFileDatabaseManager(dbFile, logger, PURGE_TIME, 0, true);

        // When
        List<FlatFileDataFlag> flagsFound = databaseManager.checkFileHealthAndStructure();

        // Then
        assertNotNull(flagsFound);
        assertTrue(flagsFound.contains(FlatFileDataFlag.LAST_LOGIN_SCHEMA_UPGRADE));

        // And – profile last login is set to -1
        var profile = databaseManager.loadPlayerProfile("nossr50");
        assertEquals(-1, (long) profile.getLastLogin());
    }

    @Test
    void loadByNameOnHealthyDatabasePopulatesAllExpectedValues() {
        // Given
        File healthyDbFile = prepareDatabaseTestResource(DB_HEALTHY);
        var databaseManager = new FlatFileDatabaseManager(healthyDbFile, logger, PURGE_TIME, 0, true);

        // When
        List<FlatFileDataFlag> flagsFound = databaseManager.checkFileHealthAndStructure();

        // Then
        assertNull(flagsFound); // No flags should be found

        String playerName = "nossr50";
        UUID uuid = UUID.fromString(HEALTHY_DB_LINE_ONE_UUID_STR);

        // And – loaded profile has all expected values
        var profile = databaseManager.loadPlayerProfile(playerName);
        assertHealthyDataProfileValues(playerName, uuid, profile);
    }

    @Test
    void newUserCreatesZeroInitializedProfileAndPersistsToFile() throws IOException {
        // Given
        UUID uuid = new UUID(0, 1);
        String playerName = "nossr50";
        int startingLevel = 1337;
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, startingLevel, true);
        databaseManager.checkFileHealthAndStructure();

        // When – create and persist new user
        var playerProfile = databaseManager.newUser(playerName, uuid);

        // Then – in-memory profile
        assertTrue(playerProfile.isLoaded());
        assertEquals(playerName, playerProfile.getPlayerName());
        assertEquals(uuid, playerProfile.getUniqueId());

        // And – from disk
        var profileFromDisk = databaseManager.loadPlayerProfile(uuid);
        assertTrue(profileFromDisk.isLoaded());
        assertEquals(playerName, profileFromDisk.getPlayerName());
        assertEquals(uuid, profileFromDisk.getUniqueId());

        // And – values zero-initialized except level
        checkNewUserValues(playerProfile, startingLevel);
        checkNewUserValues(profileFromDisk, startingLevel);

        // Given - add a few more new users, one reusing a stored UUID
        databaseManager.newUser("disco", new UUID(3, 3));
        databaseManager.newUser("dingus", new UUID(3, 4));
        final var dupedProfile = databaseManager.newUser("duped_dingus", new UUID(3, 4));

        // Then - the duplicate is refused, leaving 4 lines (1 header + 3 players) in the DB file
        assertFalse(dupedProfile.isLoaded());
        final int lineCount = getSplitDataFromFile(databaseManager.getUsersFile()).size();
        assertEquals(4, lineCount);
    }

    @Test
    void addingUsersToEndOfExistingDatabaseKeepsExistingDataAndAppendsNewUsers() throws IOException {
        // Given
        UUID uuid = new UUID(0, 80);
        String playerName = "the_kitty_man";
        File file = prepareDatabaseTestResource(DB_HEALTHY);
        int startingLevel = 1337;
        var databaseManager = new FlatFileDatabaseManager(file, logger, PURGE_TIME, startingLevel, true);
        databaseManager.checkFileHealthAndStructure();

        // When – create new user against existing DB
        var playerProfile = databaseManager.newUser(playerName, uuid);

        // Then
        assertTrue(playerProfile.isLoaded());
        assertEquals(playerName, playerProfile.getPlayerName());
        assertEquals(uuid, playerProfile.getUniqueId());

        var profileFromDisk = databaseManager.loadPlayerProfile(uuid);
        assertTrue(profileFromDisk.isLoaded());
        assertEquals(playerName, profileFromDisk.getPlayerName());
        assertEquals(uuid, profileFromDisk.getUniqueId());

        checkNewUserValues(playerProfile, startingLevel);
        checkNewUserValues(profileFromDisk, startingLevel);

        // Given - add more users, one reusing a stored UUID
        databaseManager.newUser("bidoof", new UUID(3, 3));
        databaseManager.newUser("derp", new UUID(3, 4));
        final var duplicateProfile = databaseManager.newUser("pizza", new UUID(3, 4));

        // Then - the duplicate is refused instead of appended
        assertFalse(duplicateProfile.isLoaded());
        final File usersFile = databaseManager.getUsersFile();
        assertEquals(6, getSplitDataFromFile(usersFile).size());

        // Given - a duplicate UUID row already in the file, as older versions could append
        final String derpRow = java.nio.file.Files.readAllLines(usersFile.toPath()).stream()
                .filter(row -> row.startsWith("derp:"))
                .findFirst()
                .orElseThrow();
        java.nio.file.Files.writeString(usersFile.toPath(),
                derpRow.replaceFirst("derp", "pizza") + "\n", StandardOpenOption.APPEND);
        final int originalLineCount = getSplitDataFromFile(usersFile).size();
        assertEquals(7, originalLineCount);

        // When – run health checker to fix duplicates
        databaseManager.checkFileHealthAndStructure();

        // Then – one of the duplicates should be removed
        final int lineCountAfterFix = getSplitDataFromFile(databaseManager.getUsersFile()).size();
        assertEquals(6, lineCountAfterFix);
    }

    @Test
    void readLeaderboardForPowerLevelsReturnsCorrectPagedResults() throws InvalidSkillException {
        // Given
        var databaseManager = createDatabaseWithTwoRankedUsers();

        // When – page 1 (top player only)
        // Gherkin: Given a leaderboard with two users
        //          When we read page 1 with 1 stat per page
        //          Then we see the top user "leader"
        List<PlayerStat> firstPage = databaseManager.readLeaderboard(null, 1, 1);

        // When – page 2 (second player only)
        List<PlayerStat> secondPage = databaseManager.readLeaderboard(null, 2, 1);

        // When – page 3 (out of range)
        List<PlayerStat> thirdPage = databaseManager.readLeaderboard(null, 3, 1);

        // When – page 0 (should behave like page 1 due to Math.max)
        List<PlayerStat> pageZero = databaseManager.readLeaderboard(null, 0, 10);

        // Then
        assertEquals(1, firstPage.size());
        assertEquals("leader", firstPage.get(0).playerName());

        assertEquals(1, secondPage.size());
        assertEquals("follower", secondPage.get(0).playerName());

        assertTrue(thirdPage.isEmpty(), "Out-of-range page should be empty");

        assertFalse(pageZero.isEmpty());
        assertEquals("leader", pageZero.get(0).playerName());
    }

    @Test
    void saveUserUuidUpdatesMatchingUserAndReturnsTrue() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);
        replaceDataInFile(databaseManager, normalDatabaseData);

        String targetUser = "nossr50";
        UUID newUuid = randomUUID();

        // When
        // Gherkin: Given a valid flatfile entry for "nossr50"
        //          When we update the UUID
        //          Then the UUID in the file is replaced and the method returns true
        boolean worked = databaseManager.saveUserUUID(targetUser, newUuid);

        // Then
        assertTrue(worked);

        var lines = getSplitDataFromFile(databaseManager.getUsersFile());
        boolean foundNewUuid = false;
        boolean oldUuidStillPresent = false;

        for (String[] split : lines) {
            if (split.length > FlatFileDatabaseManager.UUID_INDEX &&
                    targetUser.equalsIgnoreCase(split[FlatFileDatabaseManager.USERNAME_INDEX])) {
                if (split[FlatFileDatabaseManager.UUID_INDEX].equals(newUuid.toString())) {
                    foundNewUuid = true;
                }
                if (split[FlatFileDatabaseManager.UUID_INDEX].equals(HEALTHY_DB_LINE_ONE_UUID_STR)) {
                    oldUuidStillPresent = true;
                }
            }
        }

        assertTrue(foundNewUuid, "New UUID must be written for target user");
        assertFalse(oldUuidStillPresent, "Old UUID must not remain for target user");
    }

    @Test
    void saveUserUuidWithShortEntryDoesNotModifyDataAndReturnsFalse() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        String shortLine = "shortUser:1:2:3"; // very few fields => character.length < 42
        String header = "# test header";
        replaceDataInFile(databaseManager, new String[]{header, shortLine});

        UUID newUuid = randomUUID();

        // When
        // Gherkin: Given an invalid short database entry
        //          When we attempt to update its UUID
        //          Then the method returns false and the line stays unchanged
        boolean worked = databaseManager.saveUserUUID("shortUser", newUuid);

        // Then
        assertFalse(worked);

        try (BufferedReader reader = new BufferedReader(
                new FileReader(databaseManager.getUsersFile()))) {
            assertEquals(header, reader.readLine());
            assertEquals(shortLine, reader.readLine());
            assertNull(reader.readLine());
        }
    }

    @Test
    void convertUsersCopiesAllNonCommentLinesToDestination() throws IOException {
        // Given
        var sourceDatabase = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        String lineOne = "# mcMMO header";
        String lineTwo = "";
        String lineThree = normalDatabaseData[0];
        String lineFour = normalDatabaseData[1];

        replaceDataInFile(sourceDatabase, new String[]{lineOne, lineTwo, lineThree, lineFour});

        DatabaseManager destination = mock(DatabaseManager.class);

        // When
        // Gherkin: Given a flatfile with comments, empty lines, and two users
        //          When we convert users into another DatabaseManager
        //          Then saveUser is called once for each user line
        sourceDatabase.convertUsers(destination);

        // Then
        verify(destination, times(2)).saveUser(any(PlayerProfile.class));
    }

    @Test
    void convertUsersContinuesWhenDestinationSaveThrowsException() throws IOException {
        // Given
        var sourceDatabase = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        String header = "# mcMMO header";
        String userLine1 = normalDatabaseData[0];
        String userLine2 = normalDatabaseData[1];

        replaceDataInFile(sourceDatabase, new String[]{header, userLine1, userLine2});

        DatabaseManager destination = mock(DatabaseManager.class);

        // First call throws, second call succeeds
        Mockito.doThrow(new RuntimeException("boom"))
                .when(destination)
                .saveUser(any(PlayerProfile.class));

        // When
        // Gherkin: Given a destination that sometimes throws on save
        //          When we convert users
        //          Then conversion does not fail and all users are attempted
        sourceDatabase.convertUsers(destination);

        // Then
        verify(destination, times(2))
                .saveUser(any(PlayerProfile.class));
    }

    private String lineWithLastLogin(String baseLine, long lastLogin) {
        String[] data = baseLine.split(":");
        data[FlatFileDatabaseManager.OVERHAUL_LAST_LOGIN] = Long.toString(lastLogin);
        return String.join(":", data) + ":"; // keep trailing colon similarity
    }

    @Test
    void purgeOldUsersRemovesOnlyEntriesOlderThanPurgeTime() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        long now = System.currentTimeMillis();

        // Old user: lastLogin = 0 (definitely older than PURGE_TIME)
        String veryOldUser = lineWithLastLogin(normalDatabaseData[0], 0L); // username: nossr50

        // Recent user: lastLogin ~ now (definitely NOT older than PURGE_TIME)
        String recentUser = lineWithLastLogin(normalDatabaseData[1], now); // username: mrfloris

        // Short line – not enough fields, should be preserved
        String shortLine = "shortUser:1:2";

        String header = "# purgeOldUsers header";

        replaceDataInFile(databaseManager, new String[]{header, veryOldUser, recentUser, shortLine});

        // When
        // Gherkin: Given a mix of old users, recent users, comments and short lines
        //          When we purge old users
        //          Then only the truly old users are removed
        databaseManager.purgeOldUsers();

        // Then
        List<String[]> remaining = getSplitDataFromFile(databaseManager.getUsersFile());
        List<String> remainingNames = new ArrayList<>();

        for (String[] split : remaining) {
            if (split.length > FlatFileDatabaseManager.USERNAME_INDEX) {
                remainingNames.add(split[FlatFileDatabaseManager.USERNAME_INDEX]);
            }
        }

        assertTrue(remainingNames.contains("mrfloris"), "Recent user must be kept");
        assertTrue(remainingNames.contains("shortUser"), "Short line must be preserved");
        assertFalse(remainingNames.contains("nossr50"), "Very old user must be purged");
    }

    /**
     * Regression test for GitHub issue #4251: users with a real last-login timestamp older
     * than the cutoff were never purged because the purge condition only matched users whose
     * last login was unknown (0 or -1).
     */
    @Test
    void purgeOldUsersShouldRemoveUsersWithRealLastLoginOlderThanCutoff() throws IOException {
        // Given - a database with one user whose last login is a real timestamp older than
        // the cutoff, one recently active user, and one user with an unknown last login (-1)
        final var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        final long now = System.currentTimeMillis();
        // nossr50 - last seen twice the purge window ago
        final String inactiveUser = lineWithLastLogin(normalDatabaseData[0], now - PURGE_TIME * 2);
        // mrfloris - last seen just now
        final String activeUser = lineWithLastLogin(normalDatabaseData[1], now);
        // powerless - last login unknown (-1)
        final String unknownLoginUser = lineWithLastLogin(normalDatabaseData[2], -1L);

        // And - the unknown-login user cannot be resolved through the server's offline data
        final OfflinePlayer unresolvedPlayer = mock(OfflinePlayer.class);
        when(unresolvedPlayer.getLastPlayed()).thenReturn(0L);
        when(mcMMO.p.getServer().getOfflinePlayer(any(UUID.class))).thenReturn(unresolvedPlayer);

        replaceDataInFile(databaseManager,
                new String[]{inactiveUser, activeUser, unknownLoginUser});

        // When - purging old users
        databaseManager.purgeOldUsers();

        // Then - only the genuinely inactive user is removed; the active user survives and
        // the unknown-login user is kept rather than being purged on missing data
        final List<String> remainingNames = new ArrayList<>();
        for (String[] split : getSplitDataFromFile(databaseManager.getUsersFile())) {
            if (split.length > FlatFileDatabaseManager.USERNAME_INDEX) {
                remainingNames.add(split[FlatFileDatabaseManager.USERNAME_INDEX]);
            }
        }

        assertThat(remainingNames)
                .contains("mrfloris", "powerless")
                .doesNotContain("nossr50");
    }

    /**
     * A last-login field that fails to parse means the last login can't be determined, so the
     * user must be treated like an unknown last login (-1) and kept instead of being purged.
     */
    @Test
    void purgeOldUsersShouldKeepUsersWithUnparseableLastLogin() throws IOException {
        // Given - a database with one user whose last-login field is corrupt and one recently
        // active user
        final var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // nossr50 - last-login field holds junk that cannot be parsed as a number
        final String corruptLoginUser = corruptLastLoginLine(normalDatabaseData[0]);
        // mrfloris - last seen just now
        final String activeUser = lineWithLastLogin(normalDatabaseData[1],
                System.currentTimeMillis());

        // And - the corrupt user cannot be resolved through the server's offline data
        final OfflinePlayer unresolvedPlayer = mock(OfflinePlayer.class);
        when(unresolvedPlayer.getLastPlayed()).thenReturn(0L);
        when(mcMMO.p.getServer().getOfflinePlayer(any(UUID.class))).thenReturn(unresolvedPlayer);

        replaceDataInFile(databaseManager, new String[]{corruptLoginUser, activeUser});

        // When - purging old users
        databaseManager.purgeOldUsers();

        // Then - the corrupt-login user is kept because their last login can't be determined
        final List<String> remainingNames = new ArrayList<>();
        for (String[] split : getSplitDataFromFile(databaseManager.getUsersFile())) {
            if (split.length > FlatFileDatabaseManager.USERNAME_INDEX) {
                remainingNames.add(split[FlatFileDatabaseManager.USERNAME_INDEX]);
            }
        }

        assertThat(remainingNames).contains("nossr50", "mrfloris");
    }

    private String corruptLastLoginLine(String baseLine) {
        final String[] data = baseLine.split(":");
        data[FlatFileDatabaseManager.OVERHAUL_LAST_LOGIN] = "notanumber";
        return String.join(":", data) + ":";
    }

    @Test
    void removeUserWhenUserExistsRemovesLineAndReturnsTrue() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        String header = "# removeUser header";
        replaceDataInFile(databaseManager, new String[]{header,
                normalDatabaseData[0], // nossr50
                normalDatabaseData[1], // mrfloris
                normalDatabaseData[2]  // powerless
        });

        // When
        // Gherkin: Given a database containing a user named powerless
        //          When we remove that user
        //          Then the user entry disappears from the file and the method returns true
        boolean worked = databaseManager.removeUser("powerless", randomUUID());

        // Then
        assertTrue(worked);

        List<String[]> remaining = getSplitDataFromFile(databaseManager.getUsersFile());
        List<String> remainingNames = new ArrayList<>();
        for (String[] split : remaining) {
            remainingNames.add(split[FlatFileDatabaseManager.USERNAME_INDEX]);
        }

        assertTrue(remainingNames.contains("nossr50"));
        assertTrue(remainingNames.contains("mrfloris"));
        assertFalse(remainingNames.contains("powerless"));
    }

    @Test
    void removeUserWhenUserDoesNotExistReturnsFalseAndLeavesFileUnchanged() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        replaceDataInFile(databaseManager, normalDatabaseData);
        List<String[]> before = getSplitDataFromFile(databaseManager.getUsersFile());

        // When
        // Gherkin: Given a database that does not contain user ghostUser
        //          When we attempt to remove ghostUser
        //          Then the method returns false and the file contents are unchanged
        boolean worked = databaseManager.removeUser("ghostUser", randomUUID());

        // Then
        assertFalse(worked);

        List<String[]> after = getSplitDataFromFile(databaseManager.getUsersFile());
        assertEquals(before.size(), after.size());

        for (int i = 0; i < before.size(); i++) {
            assertArrayEquals(before.get(i), after.get(i));
        }
    }

    private void checkNewUserValues(@NotNull PlayerProfile playerProfile, int startingLevel) {
        // Given / Then – new user should be zero-initialized
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (isChildSkill(primarySkillType)) {
                continue;
            }

            assertEquals(startingLevel, playerProfile.getSkillLevel(primarySkillType));
            assertEquals(0, playerProfile.getSkillXpLevelRaw(primarySkillType), 0);
        }

        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            assertEquals(0, playerProfile.getAbilityDATS(superAbilityType));
        }

        assertTrue(playerProfile.getLastLogin() > 0);
        assertEquals(0, playerProfile.getChimaerWingDATS());
        assertEquals(0, playerProfile.getScoreboardTipsShown());
    }

    @Test
    void loadByUUIDOnHealthyDatabaseReturnsExpectedProfile() {
        // Given
        File dbFile = prepareDatabaseTestResource(DB_HEALTHY);
        var databaseManager = new FlatFileDatabaseManager(dbFile, logger, PURGE_TIME, 0, true);

        // When
        var flagsFound = databaseManager.checkFileHealthAndStructure();

        // Then
        assertNull(flagsFound); // No flags should be found

        String playerName = "nossr50";
        UUID uuid = UUID.fromString(HEALTHY_DB_LINE_ONE_UUID_STR);

        var loadedProfile = databaseManager.loadPlayerProfile(uuid);
        assertHealthyDataProfileValues(playerName, uuid, loadedProfile);

        // And – unknown UUID should return an unloaded profile
        assertFalse(databaseManager.loadPlayerProfile(new UUID(0, 1)).isLoaded());
    }

    @Test
    void loadByUUIDAndNameRenamesProfileWhenNameHasChanged() {
        // Given
        File dbFile = prepareDatabaseTestResource(DB_HEALTHY);
        var databaseManager = new FlatFileDatabaseManager(dbFile, logger, PURGE_TIME, 0, true);
        List<FlatFileDataFlag> flagsFound = databaseManager.checkFileHealthAndStructure();
        assertNull(flagsFound);

        String originalName = "nossr50";
        UUID uuid = UUID.fromString(HEALTHY_DB_LINE_ONE_UUID_STR);
        Player originalPlayer = initMockPlayer(originalName, uuid);

        // When – load with original name
        var originalProfile = databaseManager.loadPlayerProfile(originalPlayer);

        // Then
        assertHealthyDataProfileValues(originalName, uuid, originalProfile);

        // Given – same UUID but new name
        String updatedName = "updatedName";
        Player updatedPlayer = initMockPlayer(updatedName, uuid);

        // When – load again
        var updatedProfile = databaseManager.loadPlayerProfile(updatedPlayer);

        // Then – database name should be updated to new value
        assertHealthyDataProfileValues(updatedName, uuid, updatedProfile);

        // And – unknown player returns unloaded profile
        Player missingPlayer = initMockPlayer("doesntexist", new UUID(0, 1));
        var missingProfile = databaseManager.loadPlayerProfile(missingPlayer);
        assertFalse(missingProfile.isLoaded());
    }

    private File prepareDatabaseTestResource(@NotNull String dbFileName) {
        // Given
        var classLoader = getClass().getClassLoader();
        URI resourceFileURI;

        try {
            resourceFileURI = classLoader.getResource(dbFileName).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Then – resource exists
        assertNotNull(resourceFileURI);
        File fromResourcesFile = new File(resourceFileURI);
        File copyOfFile = new File(tempDir.getPath() + File.separator + dbFileName);

        if (copyOfFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            copyOfFile.delete();
        }

        assertTrue(fromResourcesFile.exists());

        try {
            //noinspection UnstableApiUsage
            Files.copy(fromResourcesFile, copyOfFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(copyOfFile);
        return copyOfFile;
    }

    private void assertHealthyDataProfileValues(@NotNull String expectedPlayerName,
            @NotNull UUID expectedUuid,
            @NotNull PlayerProfile profile) {
        // Given / Then – profile is loaded and matches basic identity
        assertTrue(profile.isLoaded());
        assertEquals(expectedUuid, profile.getUniqueId());
        assertEquals(expectedPlayerName, profile.getPlayerName());

        // And – skill levels & XP match expected values
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (isChildSkill(primarySkillType)) {
                continue;
            }

            int expectedLevel = getExpectedLevelHealthyDBEntryOne(primarySkillType);
            int actualLevel = profile.getSkillLevel(primarySkillType);
            assertEquals(expectedLevel, actualLevel);

            float expectedExperience = getExpectedExperienceHealthyDBEntryOne(primarySkillType);
            float actualExperience = profile.getSkillXpLevelRaw(primarySkillType);
            assertEquals(expectedExperience, actualExperience, 0);
        }

        // And – super ability cooldowns match expected values
        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            assertEquals(getExpectedSuperAbilityDATS(superAbilityType),
                    profile.getAbilityDATS(superAbilityType));
        }

        assertEquals(expectedChimaeraWingCd,
                profile.getUniqueData(UniqueDataType.CHIMAERA_WING_DATS));
        assertEquals(expectedScoreboardTips, profile.getScoreboardTipsShown());
        assertEquals(expectedLastLogin, profile.getLastLogin());
    }

    private long getExpectedSuperAbilityDATS(@NotNull SuperAbilityType superAbilityType) {
        return switch (superAbilityType) {
            case BERSERK -> expectedBerserkCd;
            case SUPER_BREAKER -> expectedSuperBreakerCd;
            case GIGA_DRILL_BREAKER -> expectedGigaDrillBreakerCd;
            case GREEN_TERRA -> expectedGreenTerraCd;
            case SKULL_SPLITTER -> expectedSkullSplitterCd;
            case SUPER_SHOTGUN -> expectedSuperShotgunCd;
            case TREE_FELLER -> expectedTreeFellerCd;
            case SERRATED_STRIKES -> expectedSerratedStrikesCd;
            case BLAST_MINING -> expectedBlastMiningCd;
            case TRIDENTS_SUPER_ABILITY -> expectedTridentSuperCd;
            case EXPLOSIVE_SHOT -> expectedExplosiveShotCd;
            case MACES_SUPER_ABILITY -> expectedMacesSuperCd;
            case SPEARS_SUPER_ABILITY -> expectedSpearsSuperCd;
            default -> throw new RuntimeException(
                    "Values not defined for super ability, please add " + superAbilityType);
        };
    }

    private float getExpectedExperienceHealthyDBEntryOne(@NotNull PrimarySkillType primarySkillType) {
        return switch (primarySkillType) {
            case ACROBATICS -> expectedExpAcrobatics;
            case ALCHEMY -> expectedExpAlchemy;
            case ARCHERY -> expectedExpArchery;
            case AXES -> expectedExpAxes;
            case CROSSBOWS -> expectedExpCrossbows;
            case EXCAVATION -> expectedExpExcavation;
            case FISHING -> expectedExpFishing;
            case HERBALISM -> expectedExpHerbalism;
            case MINING -> expectedExpMining;
            case REPAIR -> expectedExpRepair;
            case SALVAGE, SMELTING -> 0;
            case SWORDS -> expectedExpSwords;
            case TAMING -> expectedExpTaming;
            case TRIDENTS -> expectedExpTridents;
            case UNARMED -> expectedExpUnarmed;
            case WOODCUTTING -> expectedExpWoodcutting;
            case MACES -> expectedExpMaces;
            case SPEARS -> expectedExpSpears;
            default -> throw new RuntimeException(
                    "Values for skill not defined, please add values for " + primarySkillType);
        };
    }

    private int getExpectedLevelHealthyDBEntryOne(@NotNull PrimarySkillType primarySkillType) {
        return switch (primarySkillType) {
            case ACROBATICS -> expectedLvlAcrobatics;
            case ALCHEMY -> expectedLvlAlchemy;
            case ARCHERY -> expectedLvlArchery;
            case AXES -> expectedLvlAxes;
            case CROSSBOWS -> expectedLvlCrossbows;
            case EXCAVATION -> expectedLvlExcavation;
            case FISHING -> expectedLvlFishing;
            case HERBALISM -> expectedLvlHerbalism;
            case MINING -> expectedLvlMining;
            case REPAIR -> expectedLvlRepair;
            case SALVAGE, SMELTING -> 0;
            case SWORDS -> expectedLvlSwords;
            case TAMING -> expectedLvlTaming;
            case TRIDENTS -> expectedLvlTridents;
            case UNARMED -> expectedLvlUnarmed;
            case WOODCUTTING -> expectedLvlWoodcutting;
            case MACES -> expectedLvlMaces;
            case SPEARS -> expectedLvlSpears;
            default -> throw new RuntimeException(
                    "Values for skill not defined, please add values for " + primarySkillType);
        };
    }

    // ------------------------------------------------------------------------
    // File health & structure tests
    // ------------------------------------------------------------------------

    @Test
    void overwriteName_whenDuplicateNamesExist_rewritesSecondName() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When – overwrite with duplicate name database and fix
        overwriteDataAndCheckForFlag(databaseManager, duplicateNameDatabaseData,
                FlatFileDataFlag.DUPLICATE_NAME);

        // Then – names should no longer be equal
        var splitDataLines = getSplitDataFromFile(databaseManager.getUsersFile());
        assertNotEquals(splitDataLines.get(1)[0], splitDataLines.get(0)[0]);
    }

    @Test
    void loadPlayerProfileOnMissingData_returnsUnloadedProfile() {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When
        var retrievedProfile = databaseManager.loadPlayerProfile("nossr50");

        // Then
        assertFalse(retrievedProfile.isLoaded());
    }

    @Test
    void purgePowerlessUsersRemovesOnlyUsersWithAllZeroSkills() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);
        replaceDataInFile(databaseManager, normalDatabaseData);

        // When
        int purgedCount = databaseManager.purgePowerlessUsers();

        // Then
        assertEquals(1, purgedCount); // 1 user should have been purged
    }

    /**
     * The users file starts with a generated comment header, and purging powerless users must
     * not treat it as a user: comments have no skill data, so before this guard they looked
     * "powerless", got deleted, and inflated the purge count.
     */
    @Test
    void purgePowerlessUsersShouldPreserveCommentLinesAndNotCountThem() throws IOException {
        // Given - a database with a comment header, a powerless user, and a normal user
        final var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);
        final String header = "# mcMMO Database created on 01/01/2020 00:00";
        replaceDataInFile(databaseManager, new String[]{
                header,
                normalDatabaseData[0], // nossr50, has skills
                normalDatabaseData[2]  // powerless, all skills zero
        });

        // When - purging powerless users
        final int purgedCount = databaseManager.purgePowerlessUsers();

        // Then - only the powerless user is counted and removed; the header survives
        assertEquals(1, purgedCount);
        final List<String> remainingLines = new ArrayList<>();
        try (var reader = new BufferedReader(new FileReader(databaseManager.getUsersFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                remainingLines.add(line);
            }
        }
        assertThat(remainingLines)
                .contains(header)
                .anyMatch(line -> line.startsWith("nossr50:"))
                .noneMatch(line -> line.startsWith("powerless:"));
    }

    @Test
    void checkFileHealthAndStructureOnBadDatabaseReturnsNonEmptyFlags() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);
        replaceDataInFile(databaseManager, badDatabaseData);

        // When
        var dataFlags = databaseManager.checkFileHealthAndStructure();

        // Then
        assertNotNull(dataFlags);
        assertNotEquals(0, dataFlags.size());
    }

    @Test
    void findFixableDuplicateNamesDetectsDuplicateNameFlag() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When / Then
        overwriteDataAndCheckForFlag(databaseManager, duplicateNameDatabaseData,
                FlatFileDataFlag.DUPLICATE_NAME);
    }

    @Test
    void findDuplicateUUIDsDetectsDuplicateUuidFlag() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When / Then
        overwriteDataAndCheckForFlag(databaseManager, duplicateUUIDDatabaseData,
                FlatFileDataFlag.DUPLICATE_UUID);
    }

    @Test
    void findBadUUIDDataSetsBadUuidDataFlag() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When / Then
        overwriteDataAndCheckForFlag(databaseManager, badUUIDDatabaseData,
                FlatFileDataFlag.BAD_UUID_DATA);
    }

    @Test
    void findCorruptDataSetsCorruptedOrUnrecognizableFlag() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When / Then
        overwriteDataAndCheckForFlag(databaseManager, corruptDatabaseData,
                FlatFileDataFlag.CORRUPTED_OR_UNRECOGNIZABLE);
    }

    @Test
    void findEmptyNamesSetsMissingNameFlag() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When / Then
        overwriteDataAndCheckForFlag(databaseManager, emptyNameDatabaseData,
                FlatFileDataFlag.MISSING_NAME);
    }

    @Test
    void findBadValuesSetsBadValuesFlag() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When / Then
        overwriteDataAndCheckForFlag(databaseManager, badDatabaseData,
                FlatFileDataFlag.BAD_VALUES);
    }

    @Test
    void findOutdatedDataSetsIncompleteFlag() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When / Then
        overwriteDataAndCheckForFlag(databaseManager, outdatedDatabaseData,
                FlatFileDataFlag.INCOMPLETE);
    }

    @Test
    void getDatabaseTypeReturnsFlatFileType() {
        // Given / When
        DatabaseManager databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // Then
        assertEquals(DatabaseType.FLATFILE, databaseManager.getDatabaseType());
    }

    // ------------------------------------------------------------------------
    // Leaderboards & ranks
    // ------------------------------------------------------------------------

    @Test
    void readRankReturnsRanksForAllSkillsAndPowerLevel() {
        // Given – empty DB and two users with different levels
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);
        final String rankGirlName = "rankGirl";
        final UUID rankGirlUUID = randomUUID();
        final String rankBoyName = "rankBoy";
        final UUID rankBoyUUID = randomUUID();

        // Rank 1
        addPlayerProfileWithLevelsAndSave(databaseManager, rankGirlName, rankGirlUUID, 100);
        // Rank 2
        addPlayerProfileWithLevelsAndSave(databaseManager, rankBoyName, rankBoyUUID, 10);

        // When
        assertEquals(LeaderboardStatus.UPDATED, databaseManager.updateLeaderboards());
        final Map<PrimarySkillType, Integer> rankGirlPositions =
                databaseManager.readRank(rankGirlName);
        final Map<PrimarySkillType, Integer> rankBoyPositions =
                databaseManager.readRank(rankBoyName);

        // Then – skill ranks
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (isChildSkill(primarySkillType)) {
                assertNull(rankBoyPositions.get(primarySkillType));
                assertNull(rankGirlPositions.get(primarySkillType));
            } else {
                assertEquals(1, rankGirlPositions.get(primarySkillType));
                assertEquals(2, rankBoyPositions.get(primarySkillType));
            }
        }

        // And – power level rank (null key)
        assertEquals(1, databaseManager.readRank(rankGirlName).get(null));
        assertEquals(2, databaseManager.readRank(rankBoyName).get(null));
    }

    @Test
    void readLeaderboardChildSkillThrowsInvalidSkillException() {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // When / Then
        assertThrows(InvalidSkillException.class, () ->
                databaseManager.readLeaderboard(PrimarySkillType.SALVAGE, 1, 10));
    }

    @Test
    void getStoredUsersReturnsAllUsernamesFromFlatFile() throws IOException {
        // Given
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);
        replaceDataInFile(databaseManager, normalDatabaseData);

        // When
        List<String> storedUsers = databaseManager.getStoredUsers();

        // Then
        assertEquals(List.of("nossr50", "mrfloris", "powerless"), storedUsers);
    }

    @Test
    void loadFromFileWithBadDataFileSetsBadValuesFlag() throws URISyntaxException, IOException {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        URI resourceFileURI = classLoader.getResource(DB_BADDATA).toURI();
        File fromResourcesFile = new File(resourceFileURI);
        File copyOfFile = new File(tempDir.getPath() + File.separator + DB_BADDATA);

        if (copyOfFile.exists()) {
            copyOfFile.delete();
        }

        assertTrue(fromResourcesFile.exists());
        Files.copy(fromResourcesFile, copyOfFile);

        // When – read file via helper
        ArrayList<String[]> dataFromFile = getSplitDataFromFile(copyOfFile);

        // Then – sanity check the file contents
        logger.info("File Path: " + copyOfFile.getAbsolutePath());
        assertArrayEquals(BAD_FILE_LINE_ONE.split(":"), dataFromFile.get(0));
        assertEquals("nossr51", dataFromFile.get(22)[0]);
        assertArrayEquals(BAD_DATA_FILE_LINE_TWENTY_THREE.split(":"), dataFromFile.get(22));

        // And – health check should contain BAD_VALUES flag
        var databaseManager = new FlatFileDatabaseManager(copyOfFile, logger, PURGE_TIME, 0, true);
        List<FlatFileDataFlag> flagsFound = databaseManager.checkFileHealthAndStructure();
        assertNotNull(flagsFound);
        assertTrue(flagsFound.contains(FlatFileDataFlag.BAD_VALUES));
    }

    private @NotNull ArrayList<String[]> getSplitDataFromFile(@NotNull File file)
            throws IOException {
        ArrayList<String[]> splitDataList = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                String[] splitData = line.split(":");
                splitDataList.add(splitData);
            }

        } catch (FileNotFoundException e) {
            logger.info("File not found");
            throw e;
        } catch (IOException e) {
            logger.info("IOException reading file");
            throw e;
        }

        return splitDataList;
    }

    private @NotNull PlayerProfile addPlayerProfileWithLevelsAndSave(
            FlatFileDatabaseManager databaseManager,
            String playerName,
            UUID uuid,
            int levels) {

        // Given – DB should not already contain this profile
        assertFalse(databaseManager.loadPlayerProfile(uuid).isLoaded());

        // When – create new user and level them
        databaseManager.newUser(playerName, uuid);
        PlayerProfile leveledProfile = databaseManager.loadPlayerProfile(uuid);

        assertTrue(leveledProfile.isLoaded());
        assertEquals(playerName, leveledProfile.getPlayerName());
        assertEquals(uuid, leveledProfile.getUniqueId());

        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (isChildSkill(primarySkillType)) {
                continue;
            }

            // Note: this also resets XP
            leveledProfile.modifySkill(primarySkillType, levels);
        }

        databaseManager.saveUser(leveledProfile);
        leveledProfile = databaseManager.loadPlayerProfile(uuid);

        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (isChildSkill(primarySkillType)) {
                continue;
            }

            assertEquals(levels, leveledProfile.getSkillLevel(primarySkillType));
        }

        return leveledProfile;
    }

    private void replaceDataInFile(@NotNull FlatFileDatabaseManager databaseManager,
            @NotNull String[] dataEntries) throws IOException {
        String filePath = databaseManager.getUsersFile().getAbsolutePath();

        // Given / When – overwrite file contents with provided entries
        try (FileWriter out = new FileWriter(filePath)) {
            StringBuilder writer = new StringBuilder();
            for (String data : dataEntries) {
                writer.append(data).append("\r\n");
            }
            out.write(writer.toString());
        }

        // Then – log resulting contents for debug visibility
        try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
            logger.info("Added the following lines to the FlatFileDatabase for the purposes of the test...");
            String line;
            while ((line = in.readLine()) != null) {
                logger.info(line);
            }
        }
    }

    private void overwriteDataAndCheckForFlag(@NotNull FlatFileDatabaseManager targetDatabase,
            @NotNull String[] data,
            @NotNull FlatFileDataFlag expectedFlag) throws IOException {
        // Given
        replaceDataInFile(targetDatabase, data);

        // When
        List<FlatFileDataFlag> dataFlags = targetDatabase.checkFileHealthAndStructure();

        // Then
        assertNotNull(dataFlags);
        assertTrue(dataFlags.contains(expectedFlag));
    }

    @NotNull
    private static Player initMockPlayer(@NotNull String name, @NotNull UUID uuid) {
        Player mockPlayer = mock(Player.class);
        Mockito.when(mockPlayer.getName()).thenReturn(name);
        Mockito.when(mockPlayer.getUniqueId()).thenReturn(uuid);
        Mockito.when(mockPlayer.isOnline()).thenReturn(true);
        return mockPlayer;
    }

    private static class DebugFilter implements Filter {
        @Override
        public boolean isLoggable(LogRecord record) {
            return false;
        }
    }

    public static void recursiveDelete(@NotNull File directoryToBeDeleted) {
        if (directoryToBeDeleted.isDirectory()) {
            for (File file : directoryToBeDeleted.listFiles()) {
                recursiveDelete(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    /**
     * Concurrency regression: forced rebuilds and reads share the leaderboard maps, so readers
     * must always observe complete, correctly ordered snapshots — never a partially built or
     * torn leaderboard.
     */
    @Test
    void readLeaderboardShouldReturnCompleteSnapshotsWhileRebuildsRunConcurrently()
            throws Exception {
        // Given - a database with two ranked users and warmed leaderboards
        var databaseManager = createDatabaseWithTwoRankedUsers();
        databaseManager.readLeaderboardSnapshot(10);

        final int forcedRebuilds = 100;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final AtomicBoolean rebuilding = new AtomicBoolean(true);
        final ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            // When - one thread forces rebuilds while another reads pages and ranks
            final Future<?> rebuilder = executor.submit(() -> {
                startLatch.await();
                try {
                    for (int i = 0; i < forcedRebuilds; i++) {
                        databaseManager.readLeaderboardSnapshot(10);
                    }
                } finally {
                    rebuilding.set(false);
                }
                return null;
            });

            final Future<?> reader = executor.submit(() -> {
                startLatch.await();
                while (rebuilding.get()) {
                    // Then - every observed page is complete and ordered leader-first
                    final List<PlayerStat> page =
                            databaseManager.readLeaderboard(PrimarySkillType.MINING, 1, 10);
                    assertThat(page).hasSize(2);
                    assertThat(page.get(0).playerName()).isEqualTo("leader");
                    assertThat(page.get(1).playerName()).isEqualTo("follower");

                    // And - rank lookups agree with the page ordering
                    assertThat(databaseManager.readRank("leader").get(PrimarySkillType.MINING))
                            .isEqualTo(1);
                }
                return null;
            });

            startLatch.countDown();
            rebuilder.get(60, TimeUnit.SECONDS);
            reader.get(60, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * The bulk snapshot path exists so cache rebuilds always observe current file contents; it
     * must not be subject to the wall-clock throttle that spaces out command-triggered rebuilds.
     */
    @Test
    void readLeaderboardSnapshotShouldObserveNewDataWhenThrottleWouldServeStaleData()
            throws Exception {
        // Given - a database with two ranked users whose leaderboards were just rebuilt,
        // putting the throttle in its "too soon to update" window
        var databaseManager = createDatabaseWithTwoRankedUsers();
        databaseManager.readLeaderboardSnapshot(10);

        // And - a third, higher-level user saved after that rebuild
        final UUID topUuid = randomUUID();
        databaseManager.newUser("topdog", topUuid);
        final PlayerProfile topProfile = databaseManager.loadPlayerProfile(topUuid);
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (isChildSkill(primarySkillType)) {
                continue;
            }
            topProfile.modifySkill(primarySkillType, 500);
        }
        databaseManager.saveUser(topProfile);

        // When - reading through the throttled path and the forced bulk snapshot path
        final List<PlayerStat> throttledPage =
                databaseManager.readLeaderboard(PrimarySkillType.MINING, 1, 10);
        final List<PlayerStat> snapshotPage = databaseManager.readLeaderboardSnapshot(10)
                .skillLeaderboards().get(PrimarySkillType.MINING);

        // Then - the throttled path still serves the pre-save snapshot
        assertThat(throttledPage).extracting(PlayerStat::playerName)
                .containsExactly("leader", "follower");

        // And - the forced path observes the new top player immediately
        assertThat(snapshotPage).extracting(PlayerStat::playerName)
                .containsExactly("topdog", "leader", "follower");
    }

    /**
     * A failed rebuild must not consume the wall-clock throttle window: the next caller retries
     * immediately instead of serving stale leaderboards until the window expires.
     */
    @Test
    void updateLeaderboardsShouldAllowImmediateRetryWhenRebuildFails() throws Exception {
        // Given - a database with two ranked users and successfully built leaderboards
        var databaseManager = createDatabaseWithTwoRankedUsers();
        assertThat(databaseManager.updateLeaderboards()).isEqualTo(LeaderboardStatus.UPDATED);

        // And - the throttle window has elapsed
        resetLeaderboardThrottle(databaseManager);

        // And - the users file is unreadable, so the next rebuild fails
        final File usersFile = databaseManager.getUsersFile();
        final byte[] savedContent = java.nio.file.Files.readAllBytes(usersFile.toPath());
        assertThat(usersFile.delete()).isTrue();

        // When - a rebuild is attempted against the unreadable file
        assertThat(databaseManager.updateLeaderboards()).isEqualTo(LeaderboardStatus.FAILED);

        // Then - the last good leaderboards are still served instead of empty results
        assertThat(databaseManager.readLeaderboard(PrimarySkillType.MINING, 1, 10))
                .extracting(PlayerStat::playerName)
                .containsExactly("leader", "follower");

        // When - the file is restored and a retry happens right away
        java.nio.file.Files.write(usersFile.toPath(), savedContent);

        // Then - the retry rebuilds immediately because the failure did not arm the throttle
        assertThat(databaseManager.updateLeaderboards()).isEqualTo(LeaderboardStatus.UPDATED);
    }

    /**
     * Gotcha coverage: a failed bulk snapshot read must not arm the throttle either — otherwise
     * a cold start whose first read fails would serve empty leaderboards for a full throttle
     * window with no retry allowed.
     */
    @Test
    void readLeaderboardSnapshotFailureShouldNotBlockLaterRebuilds() throws Exception {
        // Given - a database with two ranked users whose leaderboards were never built
        var databaseManager = createDatabaseWithTwoRankedUsers();

        // And - the users file is unreadable, so the bulk snapshot read fails
        final File usersFile = databaseManager.getUsersFile();
        final byte[] savedContent = java.nio.file.Files.readAllBytes(usersFile.toPath());
        assertThat(usersFile.delete()).isTrue();
        assertThatThrownBy(() -> databaseManager.readLeaderboardSnapshot(10))
                .isInstanceOf(RuntimeException.class);

        // When - the file is restored and the throttled path runs immediately afterwards
        java.nio.file.Files.write(usersFile.toPath(), savedContent);
        final LeaderboardStatus status = databaseManager.updateLeaderboards();

        // Then - the rebuild proceeds and readers see data instead of an empty leaderboard
        assertThat(status).isEqualTo(LeaderboardStatus.UPDATED);
        assertThat(databaseManager.readLeaderboard(PrimarySkillType.MINING, 1, 10))
                .extracting(PlayerStat::playerName)
                .containsExactly("leader", "follower");
    }

    /**
     * The bulk snapshot must include every non-child skill scope plus overall, sliced from a
     * single rebuild generation.
     */
    @Test
    void readLeaderboardSnapshotShouldIncludeEveryScopeFromOneRebuild() {
        // Given - a database with two ranked users
        var databaseManager = createDatabaseWithTwoRankedUsers();

        // When - reading the bulk snapshot
        final var snapshot = databaseManager.readLeaderboardSnapshot(10);

        // Then - every non-child skill scope is present and ordered leader-first
        assertThat(snapshot.skillLeaderboards().keySet())
                .containsExactlyInAnyOrderElementsOf(SkillTools.NON_CHILD_SKILLS);
        for (List<PlayerStat> scope : snapshot.skillLeaderboards().values()) {
            assertThat(scope).extracting(PlayerStat::playerName)
                    .containsExactly("leader", "follower");
        }

        // And - the overall scope is present and ordered leader-first
        assertThat(snapshot.powerLevels()).extracting(PlayerStat::playerName)
                .containsExactly("leader", "follower");
    }

    private static void resetLeaderboardThrottle(FlatFileDatabaseManager databaseManager)
            throws Exception {
        final Field lastUpdateField = FlatFileDatabaseManager.class.getDeclaredField("lastUpdate");
        lastUpdateField.setAccessible(true);
        ((AtomicLong) lastUpdateField.get(databaseManager)).set(0L);
    }

    private FlatFileDatabaseManager createDatabaseWithTwoRankedUsers() {
        // Given – a fresh FlatFile DB
        var databaseManager = new FlatFileDatabaseManager(
                new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);

        // Given – two users with different levels
        UUID leaderUuid = randomUUID();
        UUID followerUuid = randomUUID();

        databaseManager.newUser("leader", leaderUuid);
        databaseManager.newUser("follower", followerUuid);

        var leaderProfile = databaseManager.loadPlayerProfile(leaderUuid);
        var followerProfile = databaseManager.loadPlayerProfile(followerUuid);

        // Given – leader has higher levels in all non-child skills
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (isChildSkill(primarySkillType)) {
                continue;
            }
            leaderProfile.modifySkill(primarySkillType, 100);
            followerProfile.modifySkill(primarySkillType, 10);
        }

        // When – save changes back to disk
        databaseManager.saveUser(leaderProfile);
        databaseManager.saveUser(followerProfile);

        return databaseManager;
    }

    /**
     * Every rewrite of mcmmo.users reads the whole file into memory first. Before these guards a
     * read that failed partway fell through to the write and replaced the file with whatever had
     * been buffered, so one I/O error during a purge could delete most of the database.
     */
    @Nested
    class UsersFileFailures {
        /** Runs one database operation and returns what it reports, for comparison. */
        @FunctionalInterface
        interface UsersFileOperation {
            @Nullable Object runOn(@NotNull FlatFileDatabaseManager databaseManager);
        }

        static Stream<Arguments> operationsThatReadTheUsersFile() {
            return Stream.of(
                    Arguments.of("newUser(String, UUID)",
                            (UsersFileOperation) databaseManager -> databaseManager
                                    .newUser("newPlayer", randomUUID()).isLoaded(),
                            false),
                    Arguments.of("newUser(Player)",
                            (UsersFileOperation) databaseManager -> databaseManager
                                    .newUser(initMockPlayer("newPlayer", randomUUID())).isLoaded(),
                            false),
                    Arguments.of("purgePowerlessUsers",
                            (UsersFileOperation) FlatFileDatabaseManager::purgePowerlessUsers, 0),
                    Arguments.of("purgeOldUsers", (UsersFileOperation) databaseManager -> {
                        databaseManager.purgeOldUsers();
                        return null;
                    }, null),
                    Arguments.of("saveUser",
                            (UsersFileOperation) databaseManager -> databaseManager.saveUser(
                                    new PlayerProfile(EXISTING_PLAYER, randomUUID(), true, 0)),
                            false),
                    Arguments.of("saveUserUUID",
                            (UsersFileOperation) databaseManager -> databaseManager
                                    .saveUserUUID(EXISTING_PLAYER, randomUUID()),
                            false),
                    Arguments.of("saveUserUUIDs",
                            (UsersFileOperation) databaseManager -> databaseManager
                                    .saveUserUUIDs(new HashMap<>(
                                            Map.of(EXISTING_PLAYER, randomUUID()))),
                            false),
                    Arguments.of("removeUser",
                            (UsersFileOperation) databaseManager -> databaseManager
                                    .removeUser(EXISTING_PLAYER, randomUUID()),
                            false)
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("operationsThatReadTheUsersFile")
        void readFailureShouldLeaveTheUsersFileUntouched(String operationName,
                UsersFileOperation operation, @Nullable Object expectedResult)
                throws IOException {
            // Given - a populated users file whose second line cannot be read
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    normalDatabaseData);
            final File usersFile = databaseManager.getUsersFile();
            final byte[] originalBytes = java.nio.file.Files.readAllBytes(usersFile.toPath());
            Mockito.doAnswer(invocation -> createFailingReader(usersFile, 2))
                    .when(databaseManager).newBufferedReader();

            // When - the operation runs
            final Object result = operation.runOn(databaseManager);

            // Then - it reports failure and the file is byte for byte what it was
            assertThat(result).isEqualTo(expectedResult);
            assertThat(usersFile).hasBinaryContent(originalBytes);
        }

        /**
         * The write reopens mcmmo.users in truncate mode. When that fails, the operation has
         * changed nothing and must say so, or callers such as the UUID upgrade treat the batch
         * as saved.
         */
        static Stream<Arguments> operationsThatRewriteTheUsersFile() {
            return Stream.of(
                    Arguments.of("purgePowerlessUsers",
                            (UsersFileOperation) FlatFileDatabaseManager::purgePowerlessUsers, 0),
                    Arguments.of("saveUser",
                            (UsersFileOperation) databaseManager -> databaseManager.saveUser(
                                    new PlayerProfile(EXISTING_PLAYER, randomUUID(), true, 0)),
                            false),
                    Arguments.of("saveUserUUID",
                            (UsersFileOperation) databaseManager -> databaseManager
                                    .saveUserUUID(EXISTING_PLAYER, randomUUID()),
                            false),
                    Arguments.of("saveUserUUIDs",
                            (UsersFileOperation) databaseManager -> databaseManager
                                    .saveUserUUIDs(new HashMap<>(
                                            Map.of(EXISTING_PLAYER, randomUUID()))),
                            false),
                    Arguments.of("removeUser",
                            (UsersFileOperation) databaseManager -> databaseManager
                                    .removeUser(EXISTING_PLAYER, randomUUID()),
                            false)
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("operationsThatRewriteTheUsersFile")
        void writeFailureShouldBeReportedAsFailure(String operationName,
                UsersFileOperation operation, @Nullable Object expectedResult)
                throws IOException {
            // Given - a users file, including a powerless player, that cannot be written
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    normalDatabaseData);
            Mockito.doThrow(new IOException("Simulated full disk"))
                    .when(databaseManager).newUsersFileWriter();

            // When - the operation runs
            final Object result = operation.runOn(databaseManager);

            // Then - it reports that nothing was saved
            assertThat(result).isEqualTo(expectedResult);
        }

        /**
         * The startup health check rewrites the file only after it has flagged a bad row, so
         * the seed data starts with one. Healthy seed data would pass without the guard.
         */
        @Test
        void healthCheckShouldNotRewriteTheUsersFileWhenReadFails() throws IOException {
            // Given - a users file with a flagged row followed by a line that cannot be read
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(badDatabaseData);
            final File usersFile = databaseManager.getUsersFile();
            final byte[] originalBytes = java.nio.file.Files.readAllBytes(usersFile.toPath());
            Mockito.doAnswer(invocation -> createFailingReader(usersFile, 2))
                    .when(databaseManager).newBufferedReader();

            // When - the health check runs
            final List<FlatFileDataFlag> flags = databaseManager.checkFileHealthAndStructure();

            // Then - it reports nothing and the file is byte for byte what it was
            assertThat(flags).isNull();
            assertThat(usersFile).hasBinaryContent(originalBytes);
        }
    }

    /**
     * A player whose profile does not load is given a new one, and its first save overwrites
     * their row in mcmmo.users with starting levels. That is only safe when the row really is
     * missing; when the file cannot be read or the row cannot be parsed, the player stays
     * unloaded and loading is retried.
     */
    @Nested
    class FirstLoginProfiles {
        /** Asks the database for a new player's profile, by either of the newUser overloads. */
        @FunctionalInterface
        interface NewUserRequest {
            @NotNull PlayerProfile request(@NotNull FlatFileDatabaseManager databaseManager,
                    @NotNull String playerName, @NotNull UUID uuid);
        }

        /** Rows the loader cannot parse. The startup health check resets these values to 0. */
        static Stream<Arguments> rowsThatFailToLoad() {
            return Stream.of(
                    Arguments.of("a Berserk cooldown that is not a number",
                            existingPlayerRowWith(COOLDOWN_BERSERK, "garbage")),
                    Arguments.of("a Spears cooldown that is not a number",
                            existingPlayerRowWith(COOLDOWN_SPEARS, "garbage"))
            );
        }

        static Stream<Arguments> loadsOfTheExistingPlayer() {
            return Stream.of(
                    Arguments.of("by UUID and name",
                            (Function<FlatFileDatabaseManager, PlayerProfile>) databaseManager ->
                                    databaseManager.loadPlayerProfile(initMockPlayer(
                                            EXISTING_PLAYER, EXISTING_PLAYER_UUID))),
                    Arguments.of("by UUID",
                            (Function<FlatFileDatabaseManager, PlayerProfile>) databaseManager ->
                                    databaseManager.loadPlayerProfile(EXISTING_PLAYER_UUID))
            );
        }

        static Stream<Arguments> brokenRowLoads() {
            return rowsThatFailToLoad().flatMap(row -> loadsOfTheExistingPlayer().map(
                    load -> Arguments.of(row.get()[0], row.get()[1], load.get()[0],
                            load.get()[1])));
        }

        /** The error used to be swallowed, so the player reset with no trace of why. */
        @ParameterizedTest(name = "{0}, loaded {2}")
        @MethodSource("brokenRowLoads")
        void rowThatFailsToLoadShouldBeUnloadedAndLogged(String rowProblem, String brokenRow,
                String lookup, Function<FlatFileDatabaseManager, PlayerProfile> load)
                throws IOException {
            // Given - the player's row cannot be parsed
            final RecordingHandler logRecords = new RecordingHandler();
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    new String[]{brokenRow, normalDatabaseData[1]}, logRecords.newLogger());

            // When - their profile is loaded
            final PlayerProfile profile = load.apply(databaseManager);

            // Then - it is not loaded, and the log names them
            assertThat(profile.isLoaded()).isFalse();
            assertThat(logRecords.messagesAt(Level.SEVERE))
                    .anySatisfy(message -> assertThat(message).contains(EXISTING_PLAYER)
                            .contains(HEALTHY_DB_LINE_ONE_UUID_STR));
        }

        /**
         * The login retries a failed load for as long as the player stays online, and plugins
         * may look the player up repeatedly, so the error is reported once.
         */
        @Test
        void rowThatFailsToLoadShouldBeLoggedOnce() throws IOException {
            // Given - the player's row cannot be parsed
            final RecordingHandler logRecords = new RecordingHandler();
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    new String[]{existingPlayerRowWith(COOLDOWN_BERSERK, "garbage")},
                    logRecords.newLogger());
            final Player player = initMockPlayer(EXISTING_PLAYER, EXISTING_PLAYER_UUID);

            // When - their profile is loaded several times, by login retries and a UUID lookup
            databaseManager.loadPlayerProfile(player);
            databaseManager.loadPlayerProfile(player);
            databaseManager.loadPlayerProfile(EXISTING_PLAYER_UUID);

            // Then - one error names them
            assertThat(logRecords.messagesAt(Level.SEVERE))
                    .filteredOn(message -> message.contains(HEALTHY_DB_LINE_ONE_UUID_STR))
                    .hasSize(1);
        }

        /**
         * A row from before newer skills existed is padded with zeros by the startup health
         * check. Loading it the same way means the player does not have to wait for a restart.
         */
        @ParameterizedTest(name = "loaded {0}")
        @MethodSource("loadsOfTheExistingPlayer")
        void rowFromAnOlderMcMMOShouldLoadWithTheMissingColumnsAtZero(String lookup,
                Function<FlatFileDatabaseManager, PlayerProfile> load) throws IOException {
            // Given - the player's row ends after the last login column
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    existingPlayerRowCutAfter(OVERHAUL_LAST_LOGIN + 1)});

            // When - their profile is loaded
            final PlayerProfile profile = load.apply(databaseManager);

            // Then - the stored values load, and the missing skills start at zero
            assertThat(profile.isLoaded()).isTrue();
            assertThat(profile.getSkillLevel(PrimarySkillType.MINING)).isEqualTo(1);
            assertThat(profile.getSkillXpLevel(PrimarySkillType.MINING)).isEqualTo(10);
            assertThat(profile.getSkillLevel(PrimarySkillType.CROSSBOWS)).isZero();
            assertThat(profile.getSkillXpLevel(PrimarySkillType.CROSSBOWS)).isZero();
        }

        @ParameterizedTest(name = "loaded {0}")
        @MethodSource("loadsOfTheExistingPlayer")
        void loadShouldUseALaterRowForTheSamePlayerWhenTheFirstIsBroken(String lookup,
                Function<FlatFileDatabaseManager, PlayerProfile> load) throws IOException {
            // Given - a broken row for the player, followed by a good one
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    existingPlayerRowWith(COOLDOWN_BERSERK, "garbage"), normalDatabaseData[0]});

            // When - their profile is loaded
            final PlayerProfile profile = load.apply(databaseManager);

            // Then - the good row is used
            assertThat(profile.isLoaded()).isTrue();
            assertThat(profile.getSkillLevel(PrimarySkillType.MINING)).isEqualTo(1);
        }

        /** Only the player's own row is parsed, so other broken rows cannot hold them up. */
        @Test
        void otherBrokenRowsShouldNotStopAPlayerLoading() throws IOException {
            // Given - another player's broken row and a row with a malformed UUID come first
            final RecordingHandler logRecords = new RecordingHandler();
            final String otherPlayersBrokenRow = normalDatabaseData[1].replace(":1617583171:",
                    ":garbage:");
            final String malformedUuidRow = normalDatabaseData[2].replace(
                    "e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c", "not-a-uuid");
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    otherPlayersBrokenRow, malformedUuidRow, normalDatabaseData[0]},
                    logRecords.newLogger());

            // When - the player's profile is loaded
            final PlayerProfile profile = databaseManager.loadPlayerProfile(
                    initMockPlayer(EXISTING_PLAYER, EXISTING_PLAYER_UUID));

            // Then - it loads, and nothing is reported
            assertThat(profile.isLoaded()).isTrue();
            assertThat(logRecords.messagesAt(Level.SEVERE)).isEmpty();
        }

        static Stream<Arguments> newUserRequests() {
            return Stream.of(
                    Arguments.of("newUser(Player)",
                            (NewUserRequest) (databaseManager, playerName, uuid) ->
                                    databaseManager.newUser(initMockPlayer(playerName, uuid))),
                    Arguments.of("newUser(String, UUID)",
                            (NewUserRequest) (databaseManager, playerName, uuid) ->
                                    databaseManager.newUser(playerName, uuid))
            );
        }

        static Stream<Arguments> storedRowsOfTheExistingPlayer() {
            return Stream.of(
                    Arguments.of("a row that loads", normalDatabaseData[0]),
                    Arguments.of("a row with the UUID in capitals",
                            existingPlayerRowWith(UUID_INDEX,
                                    HEALTHY_DB_LINE_ONE_UUID_STR.toUpperCase(Locale.ROOT))),
                    Arguments.of("a row that fails to load",
                            existingPlayerRowWith(COOLDOWN_BERSERK, "garbage"))
            );
        }

        static Stream<Arguments> newUserRequestsForStoredRows() {
            return newUserRequests().flatMap(request -> storedRowsOfTheExistingPlayer().map(
                    row -> Arguments.of(request.get()[0], request.get()[1], row.get()[0],
                            row.get()[1])));
        }

        /**
         * A new profile for a stored player would be saved over their row. newUser(Player) is
         * reached when their load failed, and both overloads are public API.
         */
        @ParameterizedTest(name = "{0}, {2}")
        @MethodSource("newUserRequestsForStoredRows")
        void newUserShouldNotStartOverAStoredPlayer(String requestName, NewUserRequest newUser,
                String rowDescription, String storedRow) throws IOException {
            // Given - the player has a row
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    new String[]{storedRow, normalDatabaseData[1]});
            final File usersFile = databaseManager.getUsersFile();
            final byte[] originalBytes = java.nio.file.Files.readAllBytes(usersFile.toPath());

            // When - a new profile is requested for them
            final PlayerProfile newProfile = newUser.request(databaseManager, EXISTING_PLAYER,
                    EXISTING_PLAYER_UUID);

            // Then - it is not loaded, and no second row was added for them
            assertThat(newProfile.isLoaded()).isFalse();
            assertThat(usersFile).hasBinaryContent(originalBytes);
        }

        /** A player who has never joined is not in the file, and starts fresh. */
        @ParameterizedTest(name = "{0}")
        @MethodSource("newUserRequests")
        void newUserShouldStartANewPlayerFresh(String requestName, NewUserRequest newUser)
                throws IOException {
            // Given - a users file without the player
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    normalDatabaseData);

            // When - a new profile is requested for them
            final PlayerProfile newProfile = newUser.request(databaseManager, "newPlayer",
                    randomUUID());

            // Then - it is loaded at the starting level
            assertThat(newProfile.isLoaded()).isTrue();
            assertThat(newProfile.getSkillLevel(PrimarySkillType.MINING)).isZero();
        }

        /**
         * Names change hands, so a stored name alone does not block a new player. Once they are
         * saved the name finds them, and the player who had it keeps their data under the
         * placeholder.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("newUserRequests")
        void newUserShouldStartFreshAndTakeTheNameWhenOnlyTheNameIsTaken(String requestName,
                NewUserRequest newUser) throws IOException {
            // Given - a users file with a different player under the same name
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    normalDatabaseData);
            final UUID newOwnerUuid = randomUUID();

            // When - a new profile is requested for the new owner of the name, and saved
            final PlayerProfile newProfile = newUser.request(databaseManager, EXISTING_PLAYER,
                    newOwnerUuid);
            final boolean saved = databaseManager.saveUser(newProfile);

            // Then - it is loaded and saved, and the name finds the new owner
            assertThat(newProfile.isLoaded()).isTrue();
            assertThat(saved).isTrue();
            assertThat(databaseManager.loadPlayerProfile(EXISTING_PLAYER).getUniqueId())
                    .isEqualTo(newOwnerUuid);

            // And - the previous owner keeps their data under the placeholder
            assertThat(usersFileLines(databaseManager))
                    .contains(existingPlayerRowWith(USERNAME_INDEX, INVALID_OLD_USERNAME));
        }

        /**
         * newUser(String, UUID) stores the player at once, so it takes the name then, as SQL
         * does. Leaving the name on both rows let it find the previous owner, and the startup
         * check renamed the new player instead.
         */
        @Test
        void newUserByNameShouldTakeTheNameFromTheRowHoldingIt() throws IOException {
            // Given - a stored player
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    normalDatabaseData);
            final UUID newOwnerUuid = randomUUID();

            // When - a new player is added under their name in other capitals
            final PlayerProfile newProfile = databaseManager.newUser("NOSSR50", newOwnerUuid);

            // Then - the name finds the new player
            assertThat(newProfile.isLoaded()).isTrue();
            assertThat(databaseManager.loadPlayerProfile(EXISTING_PLAYER).getUniqueId())
                    .isEqualTo(newOwnerUuid);

            // And - the previous owner keeps their data under the placeholder
            assertThat(usersFileLines(databaseManager))
                    .contains(existingPlayerRowWith(USERNAME_INDEX, INVALID_OLD_USERNAME));
        }

        /**
         * FlatFile only stores players with a UUID. Adding one without, as another plugin might,
         * read the file until it failed on the first row.
         */
        @Test
        void newUserWithoutAUuidShouldNotBeAdded() throws IOException {
            // Given - a users file with a player holding a name
            final RecordingHandler logRecords = new RecordingHandler();
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    normalDatabaseData, logRecords.newLogger());
            final File usersFile = databaseManager.getUsersFile();
            final byte[] originalBytes = java.nio.file.Files.readAllBytes(usersFile.toPath());

            // When - a player without a UUID is added under that name
            final PlayerProfile newProfile = databaseManager.newUser(EXISTING_PLAYER, null);

            // Then - nothing is written, and the profile is unloaded so nothing saves it
            assertThat(newProfile.isLoaded()).isFalse();
            assertThat(usersFile).hasBinaryContent(originalBytes);

            // And - the log names the player who was not added, and why
            assertThat(logRecords.messagesAt(Level.WARNING)).singleElement()
                    .satisfies(message -> assertThat(message)
                            .contains("Not adding " + EXISTING_PLAYER + ",")
                            .contains("UUID"));
        }

        /** Lines starting with # are comments to every reader of the file. */
        @ParameterizedTest(name = "{0}")
        @MethodSource("newUserRequests")
        void newUserShouldIgnoreACommentedOutRow(String requestName, NewUserRequest newUser)
                throws IOException {
            // Given - the player's row is commented out
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    "#" + normalDatabaseData[0], normalDatabaseData[1]});

            // When - a new profile is requested for them
            final PlayerProfile newProfile = newUser.request(databaseManager, EXISTING_PLAYER,
                    EXISTING_PLAYER_UUID);

            // Then - it is loaded
            assertThat(newProfile.isLoaded()).isTrue();
        }
    }

    /**
     * Rows are identified by UUID, since names change hands. A profile saved under a name that
     * belongs to someone else must never replace their row, only take the name from it.
     */
    @Nested
    class SavingByUuid {
        private static final int SAVED_MINING_LEVEL = 7;

        /** Rows that have no UUID to identify them by. */
        static Stream<Arguments> rowsWithoutAUuid() {
            return Stream.of(
                    Arguments.of("an empty UUID", existingPlayerRowWith(UUID_INDEX, "")),
                    Arguments.of("a NULL UUID", existingPlayerRowWith(UUID_INDEX, "NULL")),
                    Arguments.of("a malformed UUID",
                            existingPlayerRowWith(UUID_INDEX, "not-a-uuid")),
                    Arguments.of("no UUID column", existingPlayerRowCutAfter(UUID_INDEX))
            );
        }

        private static PlayerProfile profileWithSavedProgress(String playerName,
                @Nullable UUID uuid) {
            final PlayerProfile profile = new PlayerProfile(playerName, uuid, true, 0);
            profile.modifySkill(PrimarySkillType.MINING, SAVED_MINING_LEVEL);
            return profile;
        }

        /**
         * The first save of a new player who took a stored name used to wipe the old owner.
         * A name belongs to one player at a time, so the old owner keeps their progress and
         * gives up only the name. Names are matched ignoring case, as Minecraft does.
         */
        @ParameterizedTest(name = "new owner saved as {0}")
        @ValueSource(strings = {EXISTING_PLAYER, "NOSSR50"})
        void newOwnerOfANameShouldTakeOnlyTheNameFromThePreviousOwner(String newOwnerName)
                throws IOException {
            // Given - a stored player, and a different player now using their name
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    normalDatabaseData);
            final UUID newOwnerUuid = randomUUID();

            // When - the new owner is saved
            final boolean saved = databaseManager.saveUser(
                    profileWithSavedProgress(newOwnerName, newOwnerUuid));

            // Then - the previous owner's row is kept, with the placeholder for a name
            assertThat(saved).isTrue();
            assertThat(usersFileLines(databaseManager))
                    .hasSize(normalDatabaseData.length + 1)
                    .contains(existingPlayerRowWith(USERNAME_INDEX, INVALID_OLD_USERNAME),
                            normalDatabaseData[1], normalDatabaseData[2]);

            // And - the name now finds the new owner, with their own progress
            final PlayerProfile foundByName = databaseManager.loadPlayerProfile(EXISTING_PLAYER);
            assertThat(foundByName.getUniqueId()).isEqualTo(newOwnerUuid);
            assertThat(foundByName.getSkillLevel(PrimarySkillType.MINING))
                    .isEqualTo(SAVED_MINING_LEVEL);
        }

        /**
         * Offline-mode servers give "Steve" and "steve" different UUIDs, and both can play.
         * Names are matched ignoring case, so each takes the name from the other when saved,
         * and neither loses their progress.
         */
        @Test
        void caseVariantNamesShouldTakeTheNameInTurnWithoutLosingProgress() throws IOException {
            // Given - a player stored as Steve
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    existingPlayerRowWith(USERNAME_INDEX, "Steve")});
            final UUID lowerCaseSteveUuid = randomUUID();

            // When - steve is saved
            final boolean lowerCaseSaved = databaseManager.saveUser(
                    profileWithSavedProgress("steve", lowerCaseSteveUuid));

            // Then - steve has the name, and Steve keeps their progress under the placeholder
            assertThat(lowerCaseSaved).isTrue();
            assertThat(databaseManager.loadPlayerProfile(lowerCaseSteveUuid).getPlayerName())
                    .isEqualTo("steve");
            assertThat(databaseManager.loadPlayerProfile(EXISTING_PLAYER_UUID))
                    .extracting(PlayerProfile::getPlayerName,
                            profile -> profile.getSkillLevel(PrimarySkillType.MINING))
                    .containsExactly(INVALID_OLD_USERNAME, 1);

            // When - Steve logs in and is saved
            final boolean upperCaseSaved = databaseManager.saveUser(databaseManager
                    .loadPlayerProfile(initMockPlayer("Steve", EXISTING_PLAYER_UUID)));

            // Then - Steve has the name back, and steve keeps their progress under the
            // placeholder
            assertThat(upperCaseSaved).isTrue();
            assertThat(databaseManager.loadPlayerProfile(EXISTING_PLAYER_UUID).getPlayerName())
                    .isEqualTo("Steve");
            assertThat(databaseManager.loadPlayerProfile(lowerCaseSteveUuid))
                    .extracting(PlayerProfile::getPlayerName,
                            profile -> profile.getSkillLevel(PrimarySkillType.MINING))
                    .containsExactly(INVALID_OLD_USERNAME, SAVED_MINING_LEVEL);
        }

        /**
         * A player who lost their name can come back under a name another stored player now
         * holds. Their own row takes it, with their progress, and the other player keeps theirs
         * under the placeholder.
         */
        @Test
        void playerUnderThePlaceholderShouldTakeANameBackFromAnotherRow() throws IOException {
            // Given - a player who lost their name, and another player stored under the name
            // they come back with
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    existingPlayerRowWith(USERNAME_INDEX, INVALID_OLD_USERNAME),
                    normalDatabaseData[1]});

            // When - they log in under that name and are saved
            final PlayerProfile profile = databaseManager.loadPlayerProfile(
                    initMockPlayer("mrfloris", EXISTING_PLAYER_UUID));
            profile.modifySkill(PrimarySkillType.MINING, SAVED_MINING_LEVEL);
            final boolean saved = databaseManager.saveUser(profile);

            // Then - their row has the name and their progress
            assertThat(saved).isTrue();
            final PlayerProfile foundByName = databaseManager.loadPlayerProfile("mrfloris");
            assertThat(foundByName.getUniqueId()).isEqualTo(EXISTING_PLAYER_UUID);
            assertThat(foundByName.getSkillLevel(PrimarySkillType.MINING))
                    .isEqualTo(SAVED_MINING_LEVEL);

            // And - the other player is left under the placeholder with their own progress
            assertThat(usersFileLines(databaseManager))
                    .contains(rowWithName(normalDatabaseData[1], INVALID_OLD_USERNAME));
        }

        static Stream<Arguments> playersWhoAreNotOnline() {
            final OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
            when(offlinePlayer.getName()).thenReturn("mrfloris");
            when(offlinePlayer.getUniqueId()).thenReturn(EXISTING_PLAYER_UUID);
            final Player playerWhoLoggedOut = initMockPlayer("mrfloris", EXISTING_PLAYER_UUID);
            when(playerWhoLoggedOut.isOnline()).thenReturn(false);
            return Stream.of(
                    Arguments.of("an offline player", offlinePlayer),
                    Arguments.of("a player who logged out", playerWhoLoggedOut)
            );
        }

        /**
         * Only a player who is online is known to go by the name they are loaded under. An
         * offline player carries the name they last joined with, so saving them under it wrote
         * over the row of the player holding it now.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("playersWhoAreNotOnline")
        void playerWhoIsNotOnlineShouldNotTakeTheirOldNameBack(String description,
                OfflinePlayer nameLostPlayer) throws IOException {
            // Given - a player who lost their name, and the player holding it now
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    existingPlayerRowWith(USERNAME_INDEX, INVALID_OLD_USERNAME),
                    normalDatabaseData[1]});

            // When - the player who lost it is loaded under it without being online, and saved
            final PlayerProfile profile = databaseManager.loadPlayerProfile(nameLostPlayer);
            profile.modifySkill(PrimarySkillType.MINING, SAVED_MINING_LEVEL);
            final boolean saved = databaseManager.saveUser(profile);

            // Then - the player holding the name keeps it
            assertThat(saved).isTrue();
            assertThat(usersFileLines(databaseManager)).contains(normalDatabaseData[1]);

            // And - the player who lost it keeps the placeholder, with their progress
            assertThat(databaseManager.loadPlayerProfile(EXISTING_PLAYER_UUID))
                    .extracting(PlayerProfile::getPlayerName,
                            loaded -> loaded.getSkillLevel(PrimarySkillType.MINING))
                    .containsExactly(INVALID_OLD_USERNAME, SAVED_MINING_LEVEL);
        }

        /**
         * The previous owner's row is found by UUID when they log in under their new name, and
         * losing the name is expected, so it is not logged as a problem.
         */
        @Test
        void previousOwnerOfANameShouldGetTheirProgressUnderTheirNewName() throws IOException {
            // Given - a stored player whose name a new player has taken and saved under
            final RecordingHandler logRecords = new RecordingHandler();
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    normalDatabaseData, logRecords.newLogger());
            databaseManager.saveUser(profileWithSavedProgress(EXISTING_PLAYER, randomUUID()));

            // When - the previous owner logs in under a new name
            final PlayerProfile previousOwner = databaseManager.loadPlayerProfile(
                    initMockPlayer("nossr51", EXISTING_PLAYER_UUID));

            // Then - they have their progress under the new name
            assertThat(previousOwner.isLoaded()).isTrue();
            assertThat(previousOwner.getPlayerName()).isEqualTo("nossr51");
            assertThat(previousOwner.getSkillLevel(PrimarySkillType.MINING)).isEqualTo(1);

            // And - nothing is logged about the name that was in their row
            assertThat(logRecords.messagesAt(Level.WARNING)).isEmpty();
        }

        static Stream<Arguments> storedNamesOfAPlayerLoggingInUnderANewName() {
            return Stream.of(
                    Arguments.of("the placeholder", INVALID_OLD_USERNAME),
                    Arguments.of("their old name", EXISTING_PLAYER)
            );
        }

        /**
         * mcMMO only saves a profile that changed, so a name taken at login has to count as a
         * change. Otherwise the row kept its stored name, the placeholder included, for as long
         * as the player gained nothing.
         */
        @ParameterizedTest(name = "stored under {0}")
        @MethodSource("storedNamesOfAPlayerLoggingInUnderANewName")
        void nameTakenAtLoginShouldBeWrittenByTheNextSave(String description, String storedName)
                throws IOException {
            // Given - a stored player, who logs in under a new name
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    existingPlayerRowWith(USERNAME_INDEX, storedName)});
            final PlayerProfile profile = databaseManager.loadPlayerProfile(
                    initMockPlayer("nossr51", EXISTING_PLAYER_UUID));

            // When - their profile is saved without any other change
            saveTheWayMcMMODoes(databaseManager, profile);

            // Then - their row carries the new name
            assertThat(databaseManager.loadPlayerProfile(EXISTING_PLAYER_UUID).getPlayerName())
                    .isEqualTo("nossr51");
        }

        /** Each save rewrites mcmmo.users, so a login that changes nothing must not cause one. */
        @Test
        void loginUnderTheStoredNameShouldNotBeSavedWithoutAChange() throws IOException {
            // Given - a stored player, who logs in under the name stored for them
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    normalDatabaseData);
            final PlayerProfile profile = databaseManager.loadPlayerProfile(
                    initMockPlayer(EXISTING_PLAYER, EXISTING_PLAYER_UUID));

            // When - their profile is saved without any change
            saveTheWayMcMMODoes(databaseManager, profile);

            // Then - nothing is written
            verify(databaseManager, never()).saveUser(any(PlayerProfile.class));
        }

        /** Saves the way PlayerProfile.save does, which skips a profile that has not changed. */
        private static void saveTheWayMcMMODoes(FlatFileDatabaseManager databaseManager,
                PlayerProfile profile) {
            try (MockedStatic<mcMMO> mockedMcMMO = Mockito.mockStatic(mcMMO.class)) {
                mockedMcMMO.when(mcMMO::getDatabaseManager).thenReturn(databaseManager);
                profile.save(true);
            }
        }

        /**
         * No player holds the placeholder, so a profile saved under it, such as a player who
         * lost their name loaded by UUID, has no name to take from anyone.
         */
        @Test
        void profileUnderThePlaceholderShouldLeaveOtherPlaceholderRowsAlone() throws IOException {
            // Given - two players who lost their names before the spelling changed
            final String otherPlayerRow = rowWithName(normalDatabaseData[1],
                    LEGACY_FLATFILE_INVALID_OLD_USERNAME);
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    existingPlayerRowWith(USERNAME_INDEX, LEGACY_FLATFILE_INVALID_OLD_USERNAME),
                    otherPlayerRow});
            final PlayerProfile loadedByUuid = databaseManager.loadPlayerProfile(
                    EXISTING_PLAYER_UUID);
            loadedByUuid.modifySkill(PrimarySkillType.MINING, SAVED_MINING_LEVEL);

            // When - one of them is saved under the name in their row
            final boolean saved = databaseManager.saveUser(loadedByUuid);

            // Then - the other one's row is untouched
            assertThat(saved).isTrue();
            assertThat(usersFileLines(databaseManager)).hasSize(2).contains(otherPlayerRow);
        }

        @Test
        void renamedPlayerShouldSaveOverTheirOwnRow() throws IOException {
            // Given - a stored player who has changed their name since
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    normalDatabaseData);
            final PlayerProfile renamedPlayer = databaseManager.loadPlayerProfile(
                    initMockPlayer("nossr51", EXISTING_PLAYER_UUID));
            renamedPlayer.modifySkill(PrimarySkillType.MINING, SAVED_MINING_LEVEL);

            // When - they are saved
            final boolean saved = databaseManager.saveUser(renamedPlayer);

            // Then - their row carries the new name and progress, and no row was added
            assertThat(saved).isTrue();
            assertThat(usersFileLines(databaseManager)).hasSameSizeAs(normalDatabaseData)
                    .filteredOn(line -> line.contains(HEALTHY_DB_LINE_ONE_UUID_STR))
                    .singleElement()
                    .satisfies(line -> assertThat(line).startsWith("nossr51:"));
            assertThat(databaseManager.loadPlayerProfile(EXISTING_PLAYER_UUID)
                    .getSkillLevel(PrimarySkillType.MINING)).isEqualTo(SAVED_MINING_LEVEL);
        }

        /** Loading parses the UUID, so saving has to match it the same way. */
        @Test
        void playerShouldSaveOverTheirRowWhenItsUuidIsInCapitals() throws IOException {
            // Given - the player's row has their UUID in capitals
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    existingPlayerRowWith(UUID_INDEX,
                            HEALTHY_DB_LINE_ONE_UUID_STR.toUpperCase(Locale.ROOT))});

            // When - they are saved
            final boolean saved = databaseManager.saveUser(
                    profileWithSavedProgress(EXISTING_PLAYER, EXISTING_PLAYER_UUID));

            // Then - their row is replaced rather than a second one added
            assertThat(saved).isTrue();
            assertThat(usersFileLines(databaseManager)).hasSize(1);
            assertThat(databaseManager.loadPlayerProfile(EXISTING_PLAYER_UUID)
                    .getSkillLevel(PrimarySkillType.MINING)).isEqualTo(SAVED_MINING_LEVEL);
        }

        /** Every row with the UUID is replaced, so a stale duplicate cannot load later. */
        @Test
        void playerShouldSaveOverEveryRowWithTheirUuid() throws IOException {
            // Given - two rows with the player's UUID, around another player's row
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    normalDatabaseData[0], normalDatabaseData[1],
                    existingPlayerRowWith(COOLDOWN_BERSERK, "garbage")});

            // When - they are saved
            final boolean saved = databaseManager.saveUser(
                    profileWithSavedProgress(EXISTING_PLAYER, EXISTING_PLAYER_UUID));

            // Then - both of their rows hold the saved progress, and the other row is kept
            assertThat(saved).isTrue();
            assertThat(usersFileLines(databaseManager)).hasSize(3)
                    .contains(normalDatabaseData[1])
                    .filteredOn(line -> line.contains(HEALTHY_DB_LINE_ONE_UUID_STR))
                    .hasSize(2)
                    .allSatisfy(line -> assertThat(line)
                            .startsWith(EXISTING_PLAYER + ":" + SAVED_MINING_LEVEL + ":"));
        }

        /**
         * FlatFile only keeps players with a UUID, so a row without one is a leftover that
         * nothing ties to a player. It gives up the name like any other row holding it, or a
         * lookup by the name could find it instead of the player.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("rowsWithoutAUuid")
        void playerShouldTakeTheirNameFromARowWithoutAUuid(String rowProblem,
                String rowWithoutUuid) throws IOException {
            // Given - a row with the player's name but no UUID, ahead of other rows
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    new String[]{rowWithoutUuid, normalDatabaseData[1]});
            final UUID playerUuid = randomUUID();

            // When - the player is saved
            final boolean saved = databaseManager.saveUser(
                    profileWithSavedProgress(EXISTING_PLAYER, playerUuid));

            // Then - the name finds the player, with their progress
            assertThat(saved).isTrue();
            final PlayerProfile foundByName = databaseManager.loadPlayerProfile(EXISTING_PLAYER);
            assertThat(foundByName.getUniqueId()).isEqualTo(playerUuid);
            assertThat(foundByName.getSkillLevel(PrimarySkillType.MINING))
                    .isEqualTo(SAVED_MINING_LEVEL);

            // And - the row without a UUID is kept under the placeholder
            assertThat(usersFileLines(databaseManager)).hasSize(3)
                    .contains(rowWithName(rowWithoutUuid, INVALID_OLD_USERNAME));
        }

        /** A users file, and the name a profile without a UUID is saved under. */
        static Stream<Arguments> namesForAProfileWithoutAUuid() {
            return Stream.of(
                    Arguments.of("a free name", normalDatabaseData, "newPlayer"),
                    Arguments.of("a player's name", normalDatabaseData, EXISTING_PLAYER),
                    Arguments.of("a player's name in other capitals", normalDatabaseData,
                            "NOSSR50"),
                    Arguments.of("the name of a row without a UUID",
                            new String[]{existingPlayerRowWith(UUID_INDEX, "NULL"),
                                    normalDatabaseData[1]},
                            EXISTING_PLAYER),
                    Arguments.of("the placeholder", new String[]{
                            existingPlayerRowWith(USERNAME_INDEX, INVALID_OLD_USERNAME)},
                            INVALID_OLD_USERNAME)
            );
        }

        /**
         * FlatFile only stores players with a UUID, and drops rows without one when it starts.
         * Nothing ties a profile without one to a player, so it is refused instead of written
         * over whoever holds its name or kept until the next start.
         */
        @ParameterizedTest(name = "saved under {0}")
        @MethodSource("namesForAProfileWithoutAUuid")
        void profileWithoutAUuidShouldNotBeSaved(String nameDescription, String[] seedData,
                String savedName) throws IOException {
            // Given - a users file, and a profile without a UUID
            final RecordingHandler logRecords = new RecordingHandler();
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(seedData,
                    logRecords.newLogger());
            final File usersFile = databaseManager.getUsersFile();
            final byte[] originalBytes = java.nio.file.Files.readAllBytes(usersFile.toPath());

            // When - the profile is saved
            final boolean saved = databaseManager.saveUser(
                    profileWithSavedProgress(savedName, null));

            // Then - nothing is written, and the log names the profile that was not saved
            assertThat(saved).isFalse();
            assertThat(usersFile).hasBinaryContent(originalBytes);
            assertThat(logRecords.messagesAt(Level.WARNING)).singleElement()
                    .satisfies(message -> assertThat(message)
                            .contains("Not saving " + savedName + ",")
                            .contains("UUID"));
        }
    }

    /**
     * Players who lost their name to someone else hold the placeholder instead, under the
     * current spelling or the one FlatFile used to write. It is nobody's name, so a lookup by it
     * must find no one: a removal by it would otherwise delete whichever of them comes first.
     */
    @Nested
    class PlaceholderNames {
        private static final String NAME_LOST_ROW =
                existingPlayerRowWith(USERNAME_INDEX, INVALID_OLD_USERNAME);
        private static final String NAME_LOST_BEFORE_THE_SPELLING_CHANGED_ROW =
                rowWithName(normalDatabaseData[1], LEGACY_FLATFILE_INVALID_OLD_USERNAME);
        private static final String PLAYER_WITH_A_NAME = "powerless";
        /** A row without a UUID, which the startup check drops. */
        private static final String NAME_LOST_WITHOUT_A_UUID_ROW =
                rowWithName(existingPlayerRowWith(UUID_INDEX, ""), INVALID_OLD_USERNAME);
        private static final int SAVED_MINING_LEVEL = 7;

        static Stream<String> placeholderSpellings() {
            return UsernamePlaceholderTest.placeholderSpellings();
        }

        private FlatFileDatabaseManager databaseWithPlayersWhoLostTheirNames()
                throws IOException {
            return spyOnSeededDatabase(new String[]{NAME_LOST_ROW,
                    NAME_LOST_BEFORE_THE_SPELLING_CHANGED_ROW, normalDatabaseData[2]});
        }

        @ParameterizedTest(name = "looked up as {0}")
        @MethodSource("placeholderSpellings")
        void loadingByThePlaceholderShouldFindNoOne(String placeholder) throws IOException {
            // Given - players who lost their names, under both spellings
            final FlatFileDatabaseManager databaseManager =
                    databaseWithPlayersWhoLostTheirNames();

            // When - a profile is loaded by the placeholder
            final PlayerProfile profile = databaseManager.loadPlayerProfile(placeholder);

            // Then - no one is found
            assertThat(profile.isLoaded()).isFalse();
        }

        /**
         * The placeholder is nobody's name, so a player added under it takes it from no one. The
         * rows under it keep the spelling they were stored with.
         */
        @ParameterizedTest(name = "added as {0}")
        @MethodSource("placeholderSpellings")
        void newUserUnderThePlaceholderShouldTakeNoOnesName(String placeholder)
                throws IOException {
            // Given - players who lost their names, under both spellings
            final FlatFileDatabaseManager databaseManager =
                    databaseWithPlayersWhoLostTheirNames();

            // When - a player is added under the placeholder
            final PlayerProfile newProfile = databaseManager.newUser(placeholder, randomUUID());

            // Then - they are added, and the players who lost their names are untouched
            assertThat(newProfile.isLoaded()).isTrue();
            assertThat(usersFileLines(databaseManager)).contains(NAME_LOST_ROW,
                    NAME_LOST_BEFORE_THE_SPELLING_CHANGED_ROW);
        }

        @ParameterizedTest(name = "removed as {0}")
        @MethodSource("placeholderSpellings")
        void removingByThePlaceholderShouldRemoveNoOne(String placeholder) throws IOException {
            // Given - players who lost their names, under both spellings
            final FlatFileDatabaseManager databaseManager =
                    databaseWithPlayersWhoLostTheirNames();
            final File usersFile = databaseManager.getUsersFile();
            final byte[] originalBytes = java.nio.file.Files.readAllBytes(usersFile.toPath());

            // When - a player is removed by the placeholder
            final boolean removed = databaseManager.removeUser(placeholder, null);

            // Then - no one is removed
            assertThat(removed).isFalse();
            assertThat(usersFile).hasBinaryContent(originalBytes);
        }

        @ParameterizedTest(name = "ranked as {0}")
        @MethodSource("placeholderSpellings")
        void rankingThePlaceholderShouldRankNoOne(String placeholder) throws IOException {
            // Given - players who lost their names, under both spellings
            final FlatFileDatabaseManager databaseManager =
                    databaseWithPlayersWhoLostTheirNames();

            // When - ranks are read for the placeholder
            final Map<PrimarySkillType, Integer> ranks = databaseManager.readRank(placeholder);

            // Then - there are none
            assertThat(ranks).isEmpty();
        }

        @ParameterizedTest(name = "saved as {0}")
        @MethodSource("placeholderSpellings")
        void savingAUuidForThePlaceholderShouldChangeNoRow(String placeholder)
                throws IOException {
            // Given - players who lost their names, under both spellings
            final FlatFileDatabaseManager databaseManager =
                    databaseWithPlayersWhoLostTheirNames();
            final File usersFile = databaseManager.getUsersFile();
            final byte[] originalBytes = java.nio.file.Files.readAllBytes(usersFile.toPath());

            // When - a UUID is saved for the placeholder
            final boolean saved = databaseManager.saveUserUUID(placeholder, randomUUID());

            // Then - no row takes it
            assertThat(saved).isFalse();
            assertThat(usersFile).hasBinaryContent(originalBytes);
        }

        /** Bulk saves match names exactly, so only the spellings stored in rows can match one. */
        @ParameterizedTest(name = "saved as {0}")
        @ValueSource(strings = {INVALID_OLD_USERNAME, LEGACY_FLATFILE_INVALID_OLD_USERNAME})
        void savingUuidsInBulkShouldSkipThePlaceholder(String placeholder) throws IOException {
            // Given - players who lost their names, and a player with a name
            final FlatFileDatabaseManager databaseManager =
                    databaseWithPlayersWhoLostTheirNames();
            final UUID fetchedUuid = randomUUID();

            // When - UUIDs are saved for the placeholder and the player with a name
            final boolean saved = databaseManager.saveUserUUIDs(new HashMap<>(Map.of(
                    placeholder, randomUUID(), PLAYER_WITH_A_NAME, fetchedUuid)));

            // Then - only the player with a name gets theirs
            assertThat(saved).isTrue();
            assertThat(usersFileLines(databaseManager)).hasSize(3)
                    .contains(NAME_LOST_ROW, NAME_LOST_BEFORE_THE_SPELLING_CHANGED_ROW);
            assertThat(databaseManager.loadPlayerProfile(fetchedUuid).getPlayerName())
                    .isEqualTo(PLAYER_WITH_A_NAME);
        }

        /**
         * The UUID is all that tells apart players who lost their names. A comment line is not
         * a player.
         */
        @Test
        void storedUsersShouldListEachPlayerWithTheUuidInTheirRow() throws IOException {
            // Given - players who lost their names, one in a row without a UUID, and a player
            // with a name, after a comment line
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(new String[]{
                    "# mcMMO Database created on 09/23/2026 18:00", NAME_LOST_ROW,
                    NAME_LOST_BEFORE_THE_SPELLING_CHANGED_ROW, NAME_LOST_WITHOUT_A_UUID_ROW,
                    normalDatabaseData[2]});

            // When - the stored users are listed with their UUIDs
            final List<PlayerNameAndUUID> storedUsers = databaseManager.getStoredUsersWithUUIDs();

            // Then - each row is listed once, with the UUID stored in it
            assertThat(storedUsers).containsExactly(
                    new PlayerNameAndUUID(INVALID_OLD_USERNAME, EXISTING_PLAYER_UUID),
                    new PlayerNameAndUUID(LEGACY_FLATFILE_INVALID_OLD_USERNAME,
                            UUID.fromString("631e3896-da2a-4077-974b-d047859d76bc")),
                    new PlayerNameAndUUID(INVALID_OLD_USERNAME, null),
                    new PlayerNameAndUUID(PLAYER_WITH_A_NAME,
                            UUID.fromString("e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c")));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.FlatFileDatabaseManagerTest$SavingByUuid"
                + "#rowsWithoutAUuid")
        void storedUsersShouldListARowWithoutAUsableUuidWithoutOne(String description,
                String row) throws IOException {
            // Given - a player whose row has no UUID that can be read
            final FlatFileDatabaseManager databaseManager =
                    spyOnSeededDatabase(new String[]{row});

            // When - the stored users are listed with their UUIDs
            final List<PlayerNameAndUUID> storedUsers =
                    databaseManager.getStoredUsersWithUUIDs();

            // Then - the player is listed without one
            assertThat(storedUsers).containsExactly(new PlayerNameAndUUID(EXISTING_PLAYER, null));
        }

        /** SQL already left these rows out, and the ranks follow the leaderboards. */
        @Test
        void leaderboardsAndRanksShouldLeaveOutPlayersWhoLostTheirNames() throws Exception {
            // Given - players who lost their names, both out-levelling a player with a name
            final FlatFileDatabaseManager databaseManager =
                    databaseWithPlayersWhoLostTheirNames();

            // When - the leaderboards and that player's ranks are read
            final List<PlayerStat> powerLevels = databaseManager.readLeaderboard(null, 1, 10);
            final List<PlayerStat> mining =
                    databaseManager.readLeaderboard(PrimarySkillType.MINING, 1, 10);
            final List<PlayerStat> snapshotPowerLevels =
                    databaseManager.readLeaderboardSnapshot(10).powerLevels();
            final Map<PrimarySkillType, Integer> ranks =
                    databaseManager.readRank(PLAYER_WITH_A_NAME);

            // Then - only the player with a name is listed, and ranks first
            assertThat(powerLevels).extracting(PlayerStat::playerName)
                    .containsExactly(PLAYER_WITH_A_NAME);
            assertThat(mining).extracting(PlayerStat::playerName)
                    .containsExactly(PLAYER_WITH_A_NAME);
            assertThat(snapshotPowerLevels).extracting(PlayerStat::playerName)
                    .containsExactly(PLAYER_WITH_A_NAME);
            assertThat(ranks.get(PrimarySkillType.MINING)).isEqualTo(1);
            assertThat(ranks.get(null)).isEqualTo(1);
        }

        /**
         * Any number of players can lose their name, so the placeholder is not a duplicate
         * name. Flagging it made the startup check rewrite the file on every start.
         */
        @Test
        void healthCheckShouldLeaveSeveralPlayersWhoLostTheirNamesAlone() throws IOException {
            // Given - two players under each spelling of the placeholder
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(Stream.of(
                            INVALID_OLD_USERNAME, INVALID_OLD_USERNAME,
                            LEGACY_FLATFILE_INVALID_OLD_USERNAME,
                            LEGACY_FLATFILE_INVALID_OLD_USERNAME)
                    .map(placeholder -> rowWithName(
                            existingPlayerRowWith(UUID_INDEX, randomUUID().toString()),
                            placeholder))
                    .toArray(String[]::new));
            final File usersFile = databaseManager.getUsersFile();
            final byte[] originalBytes = java.nio.file.Files.readAllBytes(usersFile.toPath());

            // When - the startup check runs
            final List<FlatFileDataFlag> flags = databaseManager.checkFileHealthAndStructure();

            // Then - nothing is flagged, and the file is left as it was
            assertThat(flags).isNull();
            assertThat(usersFile).hasBinaryContent(originalBytes);
        }

        /** FlatFile writes the spelling SQL uses from now on. */
        @Test
        void healthCheckShouldRenameALaterDuplicateNameToThePlaceholder() throws IOException {
            // Given - two players stored under the same name
            final FlatFileDatabaseManager databaseManager = spyOnSeededDatabase(
                    duplicateNameDatabaseData);

            // When - the startup check runs
            databaseManager.checkFileHealthAndStructure();

            // Then - the later one is renamed to the placeholder
            assertThat(usersFileLines(databaseManager))
                    .filteredOn(line -> !line.startsWith("#"))
                    .extracting(line -> line.split(":")[USERNAME_INDEX])
                    .containsExactly("mochi", INVALID_OLD_USERNAME);
        }
    }

    private static List<String> usersFileLines(FlatFileDatabaseManager databaseManager)
            throws IOException {
        return java.nio.file.Files.readAllLines(databaseManager.getUsersFile().toPath());
    }

    /** A row with its name replaced. */
    private static String rowWithName(String row, String playerName) {
        return playerName + row.substring(row.indexOf(':'));
    }

    /** nossr50's row with one field replaced. */
    private static String existingPlayerRowWith(int fieldIndex, String value) {
        final String[] fields = normalDatabaseData[0].split(":");
        fields[fieldIndex] = value;
        return String.join(":", fields) + ":";
    }

    /** nossr50's row cut short, the way a row written by an older mcMMO looks. */
    private static String existingPlayerRowCutAfter(int fieldCount) {
        final String[] fields = normalDatabaseData[0].split(":");
        return String.join(":", Arrays.copyOf(fields, fieldCount)) + ":";
    }

    private FlatFileDatabaseManager spyOnSeededDatabase(@NotNull String[] seedData)
            throws IOException {
        return spyOnSeededDatabase(seedData, logger);
    }

    private FlatFileDatabaseManager spyOnSeededDatabase(@NotNull String[] seedData,
            @NotNull Logger databaseLogger) throws IOException {
        final File usersFile = new File(getTemporaryUserFilePath());
        final FlatFileDatabaseManager databaseManager = Mockito.spy(
                new FlatFileDatabaseManager(usersFile, databaseLogger, PURGE_TIME, 0, true));
        replaceDataInFile(databaseManager, seedData);
        return databaseManager;
    }

    private static @NotNull BufferedReader createFailingReader(File file, int failOnReadLineCall)
            throws IOException {
        return new BufferedReader(new FileReader(file)) {
            private int readCount = 0;

            @Override
            public String readLine() throws IOException {
                readCount++;
                if (readCount == failOnReadLineCall) {
                    throw new IOException("Simulated mid-read I/O error on line " + readCount);
                }
                return super.readLine();
            }
        };
    }

}
