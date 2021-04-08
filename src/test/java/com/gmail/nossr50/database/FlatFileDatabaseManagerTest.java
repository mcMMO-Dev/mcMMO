package com.gmail.nossr50.database;

import com.gmail.nossr50.TestUtil;
import com.gmail.nossr50.config.GeneralConfig;
import com.google.common.io.Files;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.util.logging.Logger;


@RunWith(PowerMockRunner.class)
public class FlatFileDatabaseManagerTest {

    public static final @NotNull String TEST_FILE_NAME = "test.mcmmo.users";
    private static File tempDir;
    private final static @NotNull Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final long PURGE_TIME = 2630000000L;
    private static @Nullable FlatFileDatabaseManager flatFileDatabaseManager;

    @Before
    public void init() {
        tempDir = Files.createTempDir();
        flatFileDatabaseManager = new FlatFileDatabaseManager(tempDir.getPath() + File.separator + TEST_FILE_NAME, logger, PURGE_TIME, 0);
    }

    @After
    public void tearDown() {
        TestUtil.recursiveDelete(tempDir);
        flatFileDatabaseManager = null;
    }

    private static String[] normalDatabaseData = {
            "nossr50:1000:::0:1000:640:1000:1000:1000:1000:1000:1000:1000:1000:16:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:1138:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:",
            "powerless:0:::0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0::0:0:0:0:0:0:0:0:0::0:0:0:0:HEARTS:0:0:e0d07db8-f7e8-43c7-9ded-864dfc6f3b7c:5:1600906906:"
    };

    private static String[] splitDataBadDatabase = {
            //First entry here is missing some values
            "nossr50:1000:0:500:0:0:0:0:0::1000:0:0:0:1593543012:0:0:0:0::1000:0:0:1593806053:HEARTS:1000:0:588fe472-1c82-4c4e-9aa1-7eefccb277e3:0:0:",
            //Second entry here has an integer value replaced by a string
            "mrfloris:2420:::0:2452:0:1983:1937:1790:3042:badvalue:3102:2408:3411:0:0:0:0:0:0:0:0::642:0:1617583171:0:1617165043:0:1617583004:1617563189:1616785408::2184:0:0:1617852413:HEARTS:415:0:631e3896-da2a-4077-974b-d047859d76bc:5:1600906906:"
    };

    @Test
    public void testPurgePowerlessUsers() {
        Assert.assertNotNull(flatFileDatabaseManager);
        addDataToFile(flatFileDatabaseManager, normalDatabaseData);
        int purgeCount = flatFileDatabaseManager.purgePowerlessUsers();
        Assert.assertEquals(purgeCount, 1); //1 User should have been purged
    }

    private void addDataToFile(@NotNull FlatFileDatabaseManager flatFileDatabaseManager, @NotNull String[] dataEntries) {
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
}