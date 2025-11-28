package com.gmail.nossr50.database;

import static com.gmail.nossr50.util.skills.SkillTools.isChildSkill;
import static java.util.UUID.randomUUID;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.database.flatfile.LeaderboardStatus;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.player.UniqueDataType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FlatFileDatabaseManagerTest {

    public static final @NotNull String TEST_FILE_NAME = "test.mcmmo.users";
    public static final @NotNull String BAD_FILE_LINE_ONE = "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:";
    public static final @NotNull String BAD_DATA_FILE_LINE_TWENTY_THREE = "nossr51:baddata:::baddata:baddata:640:baddata:1000:1000:1000:baddata:baddata:baddata:baddata:16:0:500:20273:0:0:0:0::1000:0:0:baddata:1593543012:0:0:0:0::1000:0:0:baddata:IGNORED:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:1:0:";
    public static final @NotNull String DB_BADDATA = "baddatadb.users";
    public static final @NotNull String DB_HEALTHY = "healthydb.users";
    public static final @NotNull String HEALTHY_DB_LINE_ONE_UUID_STR = "588fe472-1c82-4c4e-9aa1-7eefccb277e3";
    public static final @NotNull String DB_MISSING_LAST_LOGIN = "missinglastlogin.users";

    private static File tempDir;
    private static final @NotNull Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

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

        // Given – add a few more new users (including a duplicate UUID)
        databaseManager.newUser("disco", new UUID(3, 3));
        databaseManager.newUser("dingus", new UUID(3, 4));
        databaseManager.newUser("duped_dingus", new UUID(3, 4));

        // Then – there should be 5 lines (1 header + 4 players) in the DB file
        final int lineCount = getSplitDataFromFile(databaseManager.getUsersFile()).size();
        assertEquals(5, lineCount);
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

        // Given – add more users (with duplicate UUID)
        databaseManager.newUser("bidoof", new UUID(3, 3));
        databaseManager.newUser("derp", new UUID(3, 4));
        databaseManager.newUser("pizza", new UUID(3, 4));

        final int originalLineCount = getSplitDataFromFile(databaseManager.getUsersFile()).size();
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
    private Player initMockPlayer(@NotNull String name, @NotNull UUID uuid) {
        Player mockPlayer = mock(Player.class);
        Mockito.when(mockPlayer.getName()).thenReturn(name);
        Mockito.when(mockPlayer.getUniqueId()).thenReturn(uuid);
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

}
