package com.gmail.nossr50.database;

import com.gmail.nossr50.database.flatfile.LeaderboardStatus;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.player.UniqueDataType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.util.skills.SkillTools;
import com.google.common.io.Files;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

//This class uses JUnit5/Jupiter
class FlatFileDatabaseManagerTest {

    public static final @NotNull String TEST_FILE_NAME = "test.mcmmo.users";
    public static final @NotNull String BAD_FILE_LINE_ONE = "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:";
    public static final @NotNull String BAD_DATA_FILE_LINE_TWENTY_THREE = "nossr51:baddata:::baddata:baddata:640:baddata:1000:1000:1000:baddata:baddata:baddata:baddata:16:0:500:20273:0:0:0:0::1000:0:0:baddata:1593543012:0:0:0:0::1000:0:0:baddata:IGNORED:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:1:0:";
    public static final @NotNull String DB_BADDATA = "baddatadb.users";
    public static final @NotNull String DB_HEALTHY = "healthydb.users";
    public static final @NotNull String HEALTHY_DB_LINE_1 = "nossr50:1:IGNORED:IGNORED:10:2:20:3:4:5:6:7:8:9:10:30:40:50:60:70:80:90:100:IGNORED:11:110:111:222:333:444:555:666:777:IGNORED:12:120:888:IGNORED:HEARTS:13:130:588fe472-1c82-4c4e-9aa1-7eefccb277e3:1111:999:2020:";
    public static final @NotNull String HEALTHY_DB_LINE_ONE_UUID_STR = "588fe472-1c82-4c4e-9aa1-7eefccb277e3";
    public static final String DB_MISSING_LAST_LOGIN = "missinglastlogin.users";
    public static final String LINE_TWO_FROM_MISSING_DB = "nossr50:1:IGNORED:IGNORED:10:2:20:3:4:5:6:7:8:9:10:30:40:50:60:70:80:90:100:IGNORED:11:110:111:222:333:444:555:666:777:IGNORED:12:120:888:0:HEARTS:13:130:588fe472-1c82-4c4e-9aa1-7eefccb277e3:1111:999:";
    private static File tempDir;
    private final static @NotNull Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final long PURGE_TIME = 2630000000L;
    private static @Nullable FlatFileDatabaseManager db;

    //Making them all unique makes it easier on us to edit this stuff later
    int expectedLvlMining = 1, expectedLvlWoodcutting = 2, expectedLvlRepair = 3,
            expectedLvlUnarmed = 4, expectedLvlHerbalism = 5, expectedLvlExcavation = 6,
            expectedLvlArchery = 7, expectedLvlSwords = 8, expectedLvlAxes = 9, expectedLvlAcrobatics = 10,
            expectedLvlTaming = 11, expectedLvlFishing = 12, expectedLvlAlchemy = 13;

    float expectedExpMining = 10, expectedExpWoodcutting = 20, expectedExpRepair = 30,
            expectedExpUnarmed = 40, expectedExpHerbalism = 50, expectedExpExcavation = 60,
            expectedExpArchery = 70, expectedExpSwords = 80, expectedExpAxes = 90, expectedExpAcrobatics = 100,
            expectedExpTaming = 110, expectedExpFishing = 120, expectedExpAlchemy = 130;

    long expectedBerserkCd = 111, expectedGigaDrillBreakerCd = 222, expectedTreeFellerCd = 333,
            expectedGreenTerraCd = 444, expectedSerratedStrikesCd = 555, expectedSkullSplitterCd = 666,
            expectedSuperBreakerCd = 777, expectedBlastMiningCd = 888, expectedChimaeraWingCd = 999;

    int expectedScoreboardTips = 1111;
    Long expectedLastLogin = 2020L;

    @BeforeAll
    static void initBeforeAll() {
        logger.setFilter(new DebugFilter());
    }

    @BeforeEach
    void init() {
        assertNull(db);
        //noinspection UnstableApiUsage
        tempDir = Files.createTempDir();
        db = new FlatFileDatabaseManager(new File(getTemporaryUserFilePath()), logger, PURGE_TIME, 0, true);
    }

    private @NotNull String getTemporaryUserFilePath() {
        return tempDir.getPath() + File.separator + TEST_FILE_NAME;
    }

    @AfterEach
    void tearDown() {
        recursiveDelete(tempDir);
        db = null;
    }

    //Nothing wrong with this database
    private static final String[] normalDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "powerless:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:"
    };

    private static final String[] badUUIDDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "z750:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:3:5:1600906906:", //This one has an incorrect UUID representation
            "powerless:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:"
    };

    private static final String[] outdatedDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "electronicboy:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:" //This user is missing data added after UUID index
    };

    private static final String[] emptyLineDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "kashike:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:",
            "" //EMPTY LINE
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
            //First entry here is missing some values
            "nossr50:1000:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            //Second entry here has an integer value replaced by a string
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:badvalue:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:"
    };

    @Test
    void testDefaultInit() {
        db = new FlatFileDatabaseManager(getTemporaryUserFilePath(), logger, PURGE_TIME, 0);
    }

    @Test
    void testUpdateLeaderboards() {
        assertNotNull(db);
        assertEquals(LeaderboardStatus.UPDATED, db.updateLeaderboards());
    }

    @Test
    void testSaveUser() {
        //Make a Profile to save and check to see if it worked
        UUID uuid = UUID.fromString("588fe472-1c82-4c4e-9aa1-7eefccb277e3");
        String playerName = "nossr50";
        PlayerProfile testProfile = new PlayerProfile(playerName, uuid, 0);
        //The above profile should be "zero" initialized

        //Save the zero version and see if it looks correct
        assertNotNull(db);
        assertTrue(db.getUsersFile().exists()); //Users file should have been created from the above com.gmail.nossr50.database.FlatFileDatabaseManager.checkFileHealthAndStructure
        assertNotNull(db.getUsersFile());

        //The DB is empty at this point, add our user
        assertTrue(db.saveUser(testProfile)); //True means we saved the user

        //Check for the empty profile
        PlayerProfile retrievedFromData = db.loadPlayerProfile(uuid);
        assertTrue(retrievedFromData.isLoaded()); //PlayerProfile::isLoaded returns true if the data was created from the file, false if it wasn't found and a dummy profile was returned
        assertEquals(uuid, retrievedFromData.getUniqueId());
        assertEquals(playerName, retrievedFromData.getPlayerName());

        /*
         * Test overwriting names with new names
         */

        String alteredName = "changedmyname";
        PlayerProfile changedNameProfile = new PlayerProfile(alteredName, uuid, 0);
        assertTrue(db.saveUser(changedNameProfile)); //True means we saved the user

        retrievedFromData = db.loadPlayerProfile(uuid);
        assertTrue(retrievedFromData.isLoaded()); //PlayerProfile::isLoaded returns true if the data was created from the file, false if it wasn't found and a dummy profile was returned
        assertEquals(uuid, retrievedFromData.getUniqueId());
        assertEquals(alteredName, retrievedFromData.getPlayerName());
    }

    @Test
    void testAddedMissingLastLoginValues() {
        File dbFile = prepareDatabaseTestResource(DB_MISSING_LAST_LOGIN);

        //This makes sure our private method is working before the tests run afterwards
        ArrayList<String[]> dataFromFile = getSplitDataFromFile(dbFile);
        logger.info("File Path: "+ dbFile.getAbsolutePath());
        assertArrayEquals(LINE_TWO_FROM_MISSING_DB.split(":"), dataFromFile.get(1));
        assertEquals(dataFromFile.get(1)[FlatFileDatabaseManager.UUID_INDEX], HEALTHY_DB_LINE_ONE_UUID_STR);

        db = new FlatFileDatabaseManager(dbFile, logger, PURGE_TIME, 0, true);
        List<FlatFileDataFlag> flagsFound = db.checkFileHealthAndStructure();
        assertNotNull(flagsFound);
        assertTrue(flagsFound.contains(FlatFileDataFlag.LAST_LOGIN_SCHEMA_UPGRADE));

        //Check for the fixed value
        PlayerProfile profile = db.loadPlayerProfile("nossr50");
        assertEquals(-1, (long) profile.getLastLogin());
    }

    @Test
    void testLoadByName() {
        File healthyDB = prepareDatabaseTestResource(DB_HEALTHY);

        /*
         * We have established the files are in good order, so now for the actual testing
         */

        //This makes sure our private method is working before the tests run afterwards
        ArrayList<String[]> dataFromFile = getSplitDataFromFile(healthyDB);
        logger.info("File Path: "+healthyDB.getAbsolutePath());
        assertArrayEquals(HEALTHY_DB_LINE_1.split(":"), dataFromFile.get(0));
        assertEquals(dataFromFile.get(0)[FlatFileDatabaseManager.UUID_INDEX], HEALTHY_DB_LINE_ONE_UUID_STR);
        UUID healthDBEntryOneUUID = UUID.fromString(HEALTHY_DB_LINE_ONE_UUID_STR);

        db = new FlatFileDatabaseManager(healthyDB, logger, PURGE_TIME, 0, true);
        List<FlatFileDataFlag> flagsFound = db.checkFileHealthAndStructure();
        assertNull(flagsFound); //No flags should be found

        /*
         * Once the DB looks fine load the profile
         */

        String playerName = "nossr50";
        UUID uuid = UUID.fromString("588fe472-1c82-4c4e-9aa1-7eefccb277e3");

        PlayerProfile profile = db.loadPlayerProfile(playerName);
        testHealthyDataProfileValues(playerName, uuid, profile);
    }

    @Test
    void testNewUser() {
        //We will test that new user values line up with our expectations
        UUID uuid = new UUID(0, 1);
        String playerName = "nossr50";

        int newUserTestStartingLvl = 1337;
        db = new FlatFileDatabaseManager(new File(tempDir.getPath() + File.separator + TEST_FILE_NAME), logger, PURGE_TIME, newUserTestStartingLvl, true);
        db.checkFileHealthAndStructure();

        PlayerProfile playerProfile = db.newUser(playerName, uuid);

        assertTrue(playerProfile.isLoaded());
        assertEquals(playerName, playerProfile.getPlayerName());
        assertEquals(uuid, playerProfile.getUniqueId());

        PlayerProfile retrievedFromDisk = db.loadPlayerProfile(uuid);
        assertTrue(retrievedFromDisk.isLoaded());
        assertEquals(playerName, retrievedFromDisk.getPlayerName());
        assertEquals(uuid, retrievedFromDisk.getUniqueId());

        //Checking a new user for being "zero" initialized
        checkNewUserValues(playerProfile, newUserTestStartingLvl);
        checkNewUserValues(retrievedFromDisk, newUserTestStartingLvl);

        //TODO: Should we do any dupe checking? Probably not needed as it would be caught on the next load
        db.newUser("disco", new UUID(3, 3));
        db.newUser("dingus", new UUID(3, 4));
        db.newUser("duped_dingus", new UUID(3, 4));

        assertEquals(5, getSplitDataFromFile(db.getUsersFile()).size());
    }

    @Test
    void testAddingUsersToEndOfExistingDB() {
        //We will test that new user values line up with our expectations
        UUID uuid = new UUID(0, 80);
        String playerName = "the_kitty_man";

        File file = prepareDatabaseTestResource(DB_HEALTHY); //Existing DB

        int newUserTestStartingLvl = 1337;
        db = new FlatFileDatabaseManager(file, logger, PURGE_TIME, newUserTestStartingLvl, true);
        db.checkFileHealthAndStructure();

        PlayerProfile playerProfile = db.newUser(playerName, uuid);

        assertTrue(playerProfile.isLoaded());
        assertEquals(playerName, playerProfile.getPlayerName());
        assertEquals(uuid, playerProfile.getUniqueId());

        PlayerProfile retrievedFromDisk = db.loadPlayerProfile(uuid);
        assertTrue(retrievedFromDisk.isLoaded());
        assertEquals(playerName, retrievedFromDisk.getPlayerName());
        assertEquals(uuid, retrievedFromDisk.getUniqueId());

        //Checking a new user for being "zero" initialized
        checkNewUserValues(playerProfile, newUserTestStartingLvl);
        checkNewUserValues(retrievedFromDisk, newUserTestStartingLvl);

        //TODO: Should we do any dupe checking? Probably not needed as it would be caught on the next load
        db.newUser("bidoof", new UUID(3, 3));
        db.newUser("derp", new UUID(3, 4));
        db.newUser("pizza", new UUID(3, 4));

        assertEquals(7, getSplitDataFromFile(db.getUsersFile()).size());

        //Now we *fix* the DB and there should be one less
        db.checkFileHealthAndStructure();
        assertEquals(6, getSplitDataFromFile(db.getUsersFile()).size());
    }

    private void checkNewUserValues(@NotNull PlayerProfile playerProfile, int startingLevel) {
        //Checking a new user for being zero initialized
        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if(SkillTools.isChildSkill(primarySkillType))
                continue;

            assertEquals(startingLevel, playerProfile.getSkillLevel(primarySkillType));
            assertEquals(0, playerProfile.getSkillXpLevelRaw(primarySkillType), 0);
        }

        for(SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            assertEquals(0, playerProfile.getAbilityDATS(superAbilityType));
        }

        assertTrue(playerProfile.getLastLogin() > 0);
        assertEquals(playerProfile.getChimaerWingDATS(), 0);
        assertEquals(playerProfile.getScoreboardTipsShown(), 0);
    }

    @Test
    void testLoadByUUID() {
        File dbFile = prepareDatabaseTestResource(DB_HEALTHY);

        /*
         * We have established the files are in good order, so now for the actual testing
         */

        //This makes sure our private method is working before the tests run afterwards
        ArrayList<String[]> dataFromFile = getSplitDataFromFile(dbFile);
        logger.info("File Path: " + dbFile.getAbsolutePath());
        assertArrayEquals(HEALTHY_DB_LINE_1.split(":"), dataFromFile.get(0));
        assertEquals(dataFromFile.get(0)[FlatFileDatabaseManager.UUID_INDEX], HEALTHY_DB_LINE_ONE_UUID_STR);

        db = new FlatFileDatabaseManager(dbFile, logger, PURGE_TIME, 0, true);
        List<FlatFileDataFlag> flagsFound = db.checkFileHealthAndStructure();
        assertNull(flagsFound); //No flags should be found

        /*
         * Once the DB looks fine load the profile
         */

        String playerName = "nossr50";
        UUID uuid = UUID.fromString("588fe472-1c82-4c4e-9aa1-7eefccb277e3");

        PlayerProfile profile1 = db.loadPlayerProfile(uuid);
        testHealthyDataProfileValues(playerName, uuid, profile1);


        assertFalse(db.loadPlayerProfile(new UUID(0, 1)).isLoaded()); //This profile should not exist and therefor will return unloaded
    }

    @Test
    void testLoadByUUIDAndName() {
        File dbFile = prepareDatabaseTestResource(DB_HEALTHY);

        /*
         * We have established the files are in good order, so now for the actual testing
         */

        //This makes sure our private method is working before the tests run afterwards
        ArrayList<String[]> dataFromFile = getSplitDataFromFile(dbFile);
        logger.info("File Path: " + dbFile.getAbsolutePath());
        assertArrayEquals(HEALTHY_DB_LINE_1.split(":"), dataFromFile.get(0));
        assertEquals(dataFromFile.get(0)[FlatFileDatabaseManager.UUID_INDEX], HEALTHY_DB_LINE_ONE_UUID_STR);

        db = new FlatFileDatabaseManager(dbFile, logger, PURGE_TIME, 0, true);
        List<FlatFileDataFlag> flagsFound = db.checkFileHealthAndStructure();
        assertNull(flagsFound); //No flags should be found

        /*
         * Once the DB looks fine load the profile
         */

        String playerName = "nossr50";
        UUID uuid = UUID.fromString("588fe472-1c82-4c4e-9aa1-7eefccb277e3");

        Player player = initMockPlayer(playerName, uuid);
        PlayerProfile profile1 = db.loadPlayerProfile(player);
        testHealthyDataProfileValues(playerName, uuid, profile1);

        String updatedName = "updatedName";
        Player updatedNamePlayer = initMockPlayer(updatedName, uuid);
        PlayerProfile updatedNameProfile = db.loadPlayerProfile(updatedNamePlayer);
        testHealthyDataProfileValues(updatedName, uuid, updatedNameProfile);

        Player shouldNotExist = initMockPlayer("doesntexist", new UUID(0, 1));
        PlayerProfile profile3 = db.loadPlayerProfile(shouldNotExist);
        assertFalse(profile3.isLoaded());
    }

    private File prepareDatabaseTestResource(@NotNull String dbFileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URI resourceFileURI = null;

        try {
            resourceFileURI = classLoader.getResource(dbFileName).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        assertNotNull(resourceFileURI);
        File fromResourcesFile = new File(resourceFileURI);
        assertNotNull(resourceFileURI);
        File copyOfFile = new File(tempDir.getPath() + File.separator + dbFileName);

        if(copyOfFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            copyOfFile.delete();
        }

        assertTrue(fromResourcesFile.exists());

        try {
            //noinspection UnstableApiUsage
            Files.copy(fromResourcesFile, copyOfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(copyOfFile);
        return copyOfFile;
    }

    private void testHealthyDataProfileValues(@NotNull String playerName, @NotNull UUID uuid, @NotNull PlayerProfile profile) {
        assertTrue(profile.isLoaded()); //PlayerProfile::isLoaded returns true if the data was created from the file, false if it wasn't found and a dummy profile was returned
        assertEquals(uuid, profile.getUniqueId());
        assertEquals(playerName, profile.getPlayerName());

        /*
         * Player is a match and data is loaded, check values
         */

        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if(SkillTools.isChildSkill(primarySkillType))
                continue;

//            logger.info("Checking expected values for: "+primarySkillType);
//            logger.info("Profile Level Value: "+profile.getSkillLevel(primarySkillType));
//            logger.info("Expected Lvl Value: "+getExpectedLevelHealthyDBEntryOne(primarySkillType));
//            logger.info("Profile Exp Value: "+profile.getSkillXpLevelRaw(primarySkillType));
//            logger.info("Expected Exp Value: "+getExpectedExperienceHealthyDBEntryOne(primarySkillType));

            assertEquals(getExpectedLevelHealthyDBEntryOne(primarySkillType), profile.getSkillLevel(primarySkillType));
            assertEquals(getExpectedExperienceHealthyDBEntryOne(primarySkillType), profile.getSkillXpLevelRaw(primarySkillType), 0);
        }

        //Check the other things
        for(SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            assertEquals(getExpectedSuperAbilityDATS(superAbilityType), profile.getAbilityDATS(superAbilityType));
        }

        assertEquals(expectedChimaeraWingCd, profile.getUniqueData(UniqueDataType.CHIMAERA_WING_DATS));
        assertEquals(expectedScoreboardTips, profile.getScoreboardTipsShown());
        assertEquals(expectedLastLogin, profile.getLastLogin());
    }

    private long getExpectedSuperAbilityDATS(@NotNull SuperAbilityType superAbilityType) {
        switch(superAbilityType) {
            case BERSERK:
                return expectedBerserkCd;
            case SUPER_BREAKER:
                return expectedSuperBreakerCd;
            case GIGA_DRILL_BREAKER:
                return expectedGigaDrillBreakerCd;
            case GREEN_TERRA:
                return expectedGreenTerraCd;
            case SKULL_SPLITTER:
                return expectedSkullSplitterCd;
            case TREE_FELLER:
                return expectedTreeFellerCd;
            case SERRATED_STRIKES:
                return expectedSerratedStrikesCd;
            case BLAST_MINING:
                return expectedBlastMiningCd;
        }

        return -1;
    }

    //TODO: Why is this stuff a float?
    private float getExpectedExperienceHealthyDBEntryOne(@NotNull PrimarySkillType primarySkillType) {
        switch(primarySkillType) {
            case ACROBATICS:
                return expectedExpAcrobatics;
            case ALCHEMY:
                return expectedExpAlchemy;
            case ARCHERY:
                return expectedExpArchery;
            case AXES:
                return expectedExpAxes;
            case EXCAVATION:
                return expectedExpExcavation;
            case FISHING:
                return expectedExpFishing;
            case HERBALISM:
                return expectedExpHerbalism;
            case MINING:
                return expectedExpMining;
            case REPAIR:
                return expectedExpRepair;
            case SALVAGE:
            case SMELTING:
                return 0;
            case SWORDS:
                return expectedExpSwords;
            case TAMING:
                return expectedExpTaming;
            case UNARMED:
                return expectedExpUnarmed;
            case WOODCUTTING:
                return expectedExpWoodcutting;
        }

        return -1;
    }

    private int getExpectedLevelHealthyDBEntryOne(@NotNull PrimarySkillType primarySkillType) {
        switch(primarySkillType) {
            case ACROBATICS:
                return expectedLvlAcrobatics;
            case ALCHEMY:
                return expectedLvlAlchemy;
            case ARCHERY:
                return expectedLvlArchery;
            case AXES:
                return expectedLvlAxes;
            case EXCAVATION:
                return expectedLvlExcavation;
            case FISHING:
                return expectedLvlFishing;
            case HERBALISM:
                return expectedLvlHerbalism;
            case MINING:
                return expectedLvlMining;
            case REPAIR:
                return expectedLvlRepair;
            case SALVAGE:
            case SMELTING:
                return 0;
            case SWORDS:
                return expectedLvlSwords;
            case TAMING:
                return expectedLvlTaming;
            case UNARMED:
                return expectedLvlUnarmed;
            case WOODCUTTING:
                return expectedLvlWoodcutting;
        }

        return -1;
    }

    @Test
    void testOverwriteName() {
        overwriteDataAndCheckForFlag(db, duplicateNameDatabaseData, FlatFileDataFlag.DUPLICATE_NAME);
        ArrayList<String[]> splitDataLines = getSplitDataFromFile(db.getUsersFile());
        assertNotEquals(splitDataLines.get(1)[0], splitDataLines.get(0)[0]); //Name comparison
    }

    @Test
    void testDataNotFound() {
        //Save the zero version and see if it looks correct
        assertNotNull(db);
        assertTrue(db.getUsersFile().exists());
        assertNotNull(db.getUsersFile());

        //Check for the "unloaded" profile
        PlayerProfile retrievedFromData = db.loadPlayerProfile("nossr50");
        assertFalse(retrievedFromData.isLoaded()); //PlayerProfile::isLoaded returns false if data doesn't exist for the user
    }

    @Test
    void testPurgePowerlessUsers() {
        replaceDataInFile(db, normalDatabaseData);
        int purgeCount = db.purgePowerlessUsers();
        assertEquals(purgeCount, 1); //1 User should have been purged
    }

    @Test
    void testCheckFileHealthAndStructure() {
        replaceDataInFile(db, badDatabaseData);

        List<FlatFileDataFlag> dataFlags = db.checkFileHealthAndStructure();
        assertNotNull(dataFlags);
        assertNotEquals(dataFlags.size(), 0);
    }

    @Test
    void testFindFixableDuplicateNames() {
        overwriteDataAndCheckForFlag(db, duplicateNameDatabaseData, FlatFileDataFlag.DUPLICATE_NAME);
    }

    @Test
    void testFindDuplicateUUIDs() {
        overwriteDataAndCheckForFlag(db, duplicateUUIDDatabaseData, FlatFileDataFlag.DUPLICATE_UUID);
    }

    @Test()
    void findBadUUIDData() {
        overwriteDataAndCheckForFlag(db, badUUIDDatabaseData, FlatFileDataFlag.BAD_UUID_DATA);
    }

    @Test
    void testFindCorruptData() {
        overwriteDataAndCheckForFlag(db, corruptDatabaseData, FlatFileDataFlag.CORRUPTED_OR_UNRECOGNIZABLE);
    }

    @Test
    void testFindEmptyNames() {
        overwriteDataAndCheckForFlag(db, emptyNameDatabaseData, FlatFileDataFlag.MISSING_NAME);
    }

    @Test
    void testFindBadValues() {
        overwriteDataAndCheckForFlag(db, badDatabaseData, FlatFileDataFlag.BAD_VALUES);
    }

    @Test
    void testFindOutdatedData() {
        overwriteDataAndCheckForFlag(db, outdatedDatabaseData, FlatFileDataFlag.INCOMPLETE);
    }

    @Test
    void testGetDatabaseType() {
        assertNotNull(db);
        assertEquals(db.getDatabaseType(), DatabaseType.FLATFILE);
    }

    @Test
    void testReadRank() {
        //This is an empty DB
        assertNotNull(db);
        String rankBoyName = "rankBoy";
        UUID rankBoyUUID = new UUID(1337, 1337);
        String rankGirlName = "rankGirl";
        UUID rankGirlUUID = new UUID(7331, 7331);

        PlayerProfile rankGirlProfile = addPlayerProfileWithLevelsAndSave(rankGirlName, rankGirlUUID, 100); //Rank 1
        PlayerProfile rankBoyProfile = addPlayerProfileWithLevelsAndSave(rankBoyName, rankBoyUUID, 10); //Rank 2

        assertEquals(LeaderboardStatus.UPDATED, db.updateLeaderboards());
        Map<PrimarySkillType, Integer> rankGirlPositions = db.readRank(rankGirlName);
        Map<PrimarySkillType, Integer> rankBoyPositions = db.readRank(rankBoyName);

        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if(primarySkillType.isChildSkill()) {
                assertNull(rankBoyPositions.get(primarySkillType));
                assertNull(rankGirlPositions.get(primarySkillType));
            } else {
                assertEquals(1, rankGirlPositions.get(primarySkillType));
                assertEquals(2, rankBoyPositions.get(primarySkillType));
            }
        }

        assertEquals(1, db.readRank(rankGirlName).get(null)); //Girl should be position 1
        assertEquals(2, db.readRank(rankBoyName).get(null)); //Boy should be position 2
    }

    @Test
    void testLoadFromFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        URI resourceFileURI = null;

        try {
            resourceFileURI = classLoader.getResource(DB_BADDATA).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        assertNotNull(resourceFileURI);
        File fromResourcesFile = new File(resourceFileURI);
        assertNotNull(resourceFileURI);
        File copyOfFile = new File(tempDir.getPath() + File.separator + DB_BADDATA);

        if(copyOfFile.exists()) {
            copyOfFile.delete();
        }

        assertTrue(fromResourcesFile.exists());

        try {
            Files.copy(fromResourcesFile, copyOfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(copyOfFile);

        //This makes sure our private method is working before the tests run afterwards
        ArrayList<String[]> dataFromFile = getSplitDataFromFile(copyOfFile);
        logger.info("File Path: "+copyOfFile.getAbsolutePath());
        assertArrayEquals(BAD_FILE_LINE_ONE.split(":"), dataFromFile.get(0));
        assertEquals(dataFromFile.get(22)[0], "nossr51");
        assertArrayEquals(BAD_DATA_FILE_LINE_TWENTY_THREE.split(":"), dataFromFile.get(22));

        FlatFileDatabaseManager db_a = new FlatFileDatabaseManager(copyOfFile, logger, PURGE_TIME, 0, true);
        List<FlatFileDataFlag> flagsFound = db_a.checkFileHealthAndStructure();
        assertNotNull(flagsFound);
        assertTrue(flagsFound.contains(FlatFileDataFlag.BAD_VALUES));
    }

    private @NotNull ArrayList<String[]> getSplitDataFromFile(@NotNull File file) {
        ArrayList<String[]> splitDataList = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                String[] splitData = line.split(":");
                splitDataList.add(splitData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return splitDataList;
    }

    private @NotNull PlayerProfile addPlayerProfileWithLevelsAndSave(String playerName, UUID uuid, int levels) {
        assertNotNull(db);
        assertFalse(db.loadPlayerProfile(uuid).isLoaded());

        db.newUser(playerName, uuid);
        PlayerProfile leveledProfile = db.loadPlayerProfile(uuid);

        assertTrue(leveledProfile.isLoaded());
        assertEquals(playerName, leveledProfile.getPlayerName());
        assertEquals(uuid, leveledProfile.getUniqueId());

        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if(SkillTools.isChildSkill(primarySkillType))
                continue;

            leveledProfile.modifySkill(primarySkillType, levels); //TODO: This method also resets XP, not cool
        }

        db.saveUser(leveledProfile);
        leveledProfile = db.loadPlayerProfile(uuid);

        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if(SkillTools.isChildSkill(primarySkillType)) {
                continue;
            }

            assertEquals(levels, leveledProfile.getSkillLevel(primarySkillType));
        }

        return leveledProfile;
    }

    private void replaceDataInFile(@NotNull FlatFileDatabaseManager flatFileDatabaseManager, @NotNull String[] dataEntries) {
        String filePath = flatFileDatabaseManager.getUsersFile().getAbsolutePath();
        BufferedReader in = null;
        FileWriter out = null;

        try {
            StringBuilder writer = new StringBuilder();

            for(String data : dataEntries) {
                writer.append(data).append("\r\n");
            }

            out = new FileWriter(filePath);
            out.write(writer.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    // Ignore
                }
            }
        }

        try {
            logger.info("Added the following lines to the FlatFileDatabase for the purposes of the test...");
            // Open the file
            in = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = in.readLine()) != null) {
                logger.info(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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

    private void overwriteDataAndCheckForFlag(@NotNull FlatFileDatabaseManager targetDatabase, @NotNull String[] data, @NotNull FlatFileDataFlag flag) {
        replaceDataInFile(targetDatabase, data);

        List<FlatFileDataFlag> dataFlags = targetDatabase.checkFileHealthAndStructure();
        assertNotNull(dataFlags);
        assertTrue(dataFlags.contains(flag));
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

}