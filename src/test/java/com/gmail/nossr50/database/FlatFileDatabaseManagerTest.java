package com.gmail.nossr50.database;

import com.gmail.nossr50.TestUtil;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.google.common.io.Files;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;


@RunWith(PowerMockRunner.class)
public class FlatFileDatabaseManagerTest {

    public static final @NotNull String TEST_FILE_NAME = "test.mcmmo.users";
    public static final int HEALTHY_RETURN_CODE = 0;
    private static File tempDir;
    private final static @NotNull Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final long PURGE_TIME = 2630000000L;
    private static @Nullable FlatFileDatabaseManager db;

    @Before
    public void init() {
        assertNull(db);
        tempDir = Files.createTempDir();
        db = new FlatFileDatabaseManager(tempDir.getPath() + File.separator + TEST_FILE_NAME, logger, PURGE_TIME, 0);
    }

    @After
    public void tearDown() {
        TestUtil.recursiveDelete(tempDir);
        db = null;
    }

    //Nothing wrong with this database
    private static final String[] normalDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "powerless:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:"
    };

    private static final String[] outdatedDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "powerless:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:" //This user is missing data added after UUID index
    };

    private static final String[] emptyLineDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "powerless:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:",
            "" //EMPTY LINE
    };

    private static final String[] emptyNameDatabaseData = {
            ":1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "powerless:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:"
    };

    private static final String[] duplicateNameDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:631e3896-da2a-4077-974b-d047859d76bc:0:0:",
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
    public void testPurgePowerlessUsers() {
        replaceDataInFile(db, normalDatabaseData);
        int purgeCount = db.purgePowerlessUsers();
        assertEquals(purgeCount, 1); //1 User should have been purged
    }

    @Test
    public void testCheckFileHealthAndStructure() {
        replaceDataInFile(db, badDatabaseData);

        List<FlatFileDataFlag> dataFlags = db.checkFileHealthAndStructure();
        assertNotNull(dataFlags);
        assertNotEquals(dataFlags.size(), 0);
    }

    @Test
    public void testFindDuplicateNames() {
        addDataAndCheckForFlag(db, duplicateNameDatabaseData, FlatFileDataFlag.DUPLICATE_NAME_FIXABLE);
    }

    @Test
    public void testFindDuplicateUUIDs() {
        addDataAndCheckForFlag(db, duplicateUUIDDatabaseData, FlatFileDataFlag.DUPLICATE_UUID);
    }

    @Test
    public void testFindCorruptData() {
        addDataAndCheckForFlag(db, corruptDatabaseData, FlatFileDataFlag.JUNK);
    }

    @Test
    public void testFindEmptyNames() {
        addDataAndCheckForFlag(db, emptyNameDatabaseData, FlatFileDataFlag.MISSING_NAME);
    }

    @Test
    public void testFindEmptyLine() {
        addDataAndCheckForFlag(db, emptyLineDatabaseData, FlatFileDataFlag.EMPTY_LINE);
    }

    @Test
    public void testFindBadValues() {
        addDataAndCheckForFlag(db, badDatabaseData, FlatFileDataFlag.BAD_VALUES);
    }

    @Test
    public void testFindOutdatedData() {
        addDataAndCheckForFlag(db, outdatedDatabaseData, FlatFileDataFlag.INCOMPLETE);
    }

    @Test
    public void testGetDatabaseType() {
        assertNotNull(db);
        assertEquals(db.getDatabaseType(), DatabaseType.FLATFILE);
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
            System.out.println("File not found");
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
            System.out.println("Added the following lines to the FlatFileDatabase for the purposes of the test...");
            // Open the file
            in = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
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

    private void addDataAndCheckForFlag(@NotNull FlatFileDatabaseManager targetDatabase, @NotNull String[] data, @NotNull FlatFileDataFlag flag) {
        replaceDataInFile(targetDatabase, data);

        List<FlatFileDataFlag> dataFlags = targetDatabase.checkFileHealthAndStructure();
        assertNotNull(dataFlags);
        assertTrue(dataFlags.contains(flag));
    }
}