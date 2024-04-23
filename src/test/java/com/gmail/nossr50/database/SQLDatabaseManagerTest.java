//package com.gmail.nossr50.database;
//
//import com.gmail.nossr50.config.AdvancedConfig;
//import com.gmail.nossr50.config.GeneralConfig;
//import com.gmail.nossr50.datatypes.MobHealthbarType;
//import com.gmail.nossr50.datatypes.player.PlayerProfile;
//import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
//import com.gmail.nossr50.mcMMO;
//import com.gmail.nossr50.util.compat.CompatibilityManager;
//import com.gmail.nossr50.util.platform.MinecraftGameVersion;
//import com.gmail.nossr50.util.skills.SkillTools;
//import com.gmail.nossr50.util.upgrade.UpgradeManager;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//import org.junit.jupiter.api.*;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import java.util.logging.Logger;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//class SQLDatabaseManagerTest {
//    private final static @NotNull Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
//    static MockedStatic<mcMMO> mockedMcMMO;
//    SQLDatabaseManager sqlDatabaseManager;
//    static GeneralConfig generalConfig;
//    static AdvancedConfig advancedConfig;
//    static UpgradeManager upgradeManager;
//    static CompatibilityManager compatibilityManager;
//    static SkillTools skillTools;
//
//    @BeforeAll
//    static void setUpAll() {
//        // stub mcMMO.p
//        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
//        mcMMO.p = Mockito.mock(mcMMO.class);
//        when(mcMMO.p.getLogger()).thenReturn(logger);
//
//        // general config mock
//        mockGeneralConfig();
//
//        // advanced config mock
//        advancedConfig = Mockito.mock(AdvancedConfig.class);
//        when(mcMMO.p.getAdvancedConfig()).thenReturn(advancedConfig);
//
//        // starting level
//        when(mcMMO.p.getAdvancedConfig().getStartingLevel()).thenReturn(0);
//
//        // wire skill tools
//        skillTools = new SkillTools(mcMMO.p);
//        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);
//
//        // compatibility manager mock
//        compatibilityManager = Mockito.mock(CompatibilityManager.class);
//        when(mcMMO.getCompatibilityManager()).thenReturn(compatibilityManager);
//        when(compatibilityManager.getMinecraftGameVersion()).thenReturn(new MinecraftGameVersion(1, 20, 4));
//
//        // upgrade manager mock
//        upgradeManager = Mockito.mock(UpgradeManager.class);
//        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);
//
//        // don't trigger upgrades
//        when(mcMMO.getUpgradeManager().shouldUpgrade(any())).thenReturn(false);
//    }
//
//    private static void mockGeneralConfig() {
//        generalConfig = Mockito.mock(GeneralConfig.class);
//        when(generalConfig.getLocale()).thenReturn("en_US");
//        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
//
//        // max pool size
//        when(mcMMO.p.getGeneralConfig().getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.MISC))
//                .thenReturn(10);
//        when(mcMMO.p.getGeneralConfig().getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.LOAD))
//                .thenReturn(20);
//        when(mcMMO.p.getGeneralConfig().getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.SAVE))
//                .thenReturn(20);
//
//        // max connections
//        when(mcMMO.p.getGeneralConfig().getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.MISC))
//                .thenReturn(30);
//        when(mcMMO.p.getGeneralConfig().getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.LOAD))
//                .thenReturn(30);
//        when(mcMMO.p.getGeneralConfig().getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.SAVE))
//                .thenReturn(30);
//
//        // table prefix
//        when(mcMMO.p.getGeneralConfig().getMySQLTablePrefix()).thenReturn("mcmmo_");
//
//        // public key retrieval
//        when(mcMMO.p.getGeneralConfig().getMySQLPublicKeyRetrieval()).thenReturn(true);
//
//        // debug
//        when(mcMMO.p.getGeneralConfig().getMySQLDebug()).thenReturn(true);
//
//        // use mysql
//        when(mcMMO.p.getGeneralConfig().getUseMySQL()).thenReturn(true);
//
//        // use ssl
//        when(mcMMO.p.getGeneralConfig().getMySQLSSL()).thenReturn(true);
//
//        // username
//        when(mcMMO.p.getGeneralConfig().getMySQLUserName()).thenReturn("sa");
//
//        // password
//        when(mcMMO.p.getGeneralConfig().getMySQLUserPassword()).thenReturn("");
//
//        // host
//        when(mcMMO.p.getGeneralConfig().getMySQLServerName()).thenReturn("localhost");
//
//        // unused mob health bar thingy
//        when(mcMMO.p.getGeneralConfig().getMobHealthbarDefault()).thenReturn(MobHealthbarType.HEARTS);
//    }
//
//    @BeforeEach
//    void setUp() {
//        assertNull(sqlDatabaseManager);
//        sqlDatabaseManager = new SQLDatabaseManager(logger, "org.h2.Driver", true);
//    }
//
//    @AfterEach
//    void tearDown() {
//        sqlDatabaseManager = null;
//    }
//
//    @AfterAll
//    static void tearDownAll() {
//        mockedMcMMO.close();
//    }
//
//    @Test
//    void testGetConnectionMisc() throws Exception {
//        assertNotNull(sqlDatabaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.MISC));
//    }
//
//    @Test
//    void testGetConnectionLoad() throws Exception {
//        assertNotNull(sqlDatabaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.LOAD));
//    }
//
//    @Test
//    void testGetConnectionSave() throws Exception {
//        assertNotNull(sqlDatabaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.SAVE));
//    }
//
//    @Test
//    void testNewUser() {
//        Player player = Mockito.mock(Player.class);
//        when(player.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
//        when(player.getName()).thenReturn("nossr50");
//        sqlDatabaseManager.newUser(player);
//    }
//
//    @Test
//    void testNewUserGetSkillLevel() {
//        Player player = Mockito.mock(Player.class);
//        when(player.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
//        when(player.getName()).thenReturn("nossr50");
//        PlayerProfile playerProfile = sqlDatabaseManager.newUser(player);
//
//        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
//            assertEquals(0, playerProfile.getSkillLevel(primarySkillType));
//        }
//    }
//
//    @Test
//    void testNewUserGetSkillXpLevel() {
//        Player player = Mockito.mock(Player.class);
//        when(player.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
//        when(player.getName()).thenReturn("nossr50");
//        PlayerProfile playerProfile = sqlDatabaseManager.newUser(player);
//
//        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
//            assertEquals(0, playerProfile.getSkillXpLevel(primarySkillType));
//        }
//    }
//
//    @Test
//    void testSaveSkillLevelValues() {
//        Player player = Mockito.mock(Player.class);
//        when(player.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
//        when(player.getName()).thenReturn("nossr50");
//        PlayerProfile playerProfile = sqlDatabaseManager.newUser(player);
//
//        // Validate values are starting from zero
//        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
//            assertEquals(0, playerProfile.getSkillXpLevel(primarySkillType));
//        }
//
//        // Change values
//        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
//            playerProfile.modifySkill(primarySkillType, 1 + primarySkillType.ordinal());
//        }
//
//        boolean saveSuccess = sqlDatabaseManager.saveUser(playerProfile);
//        assertTrue(saveSuccess);
//
//        PlayerProfile retrievedUser = sqlDatabaseManager.loadPlayerProfile(player.getName());
//
//        // Check that values got saved
//        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
//            if (primarySkillType == PrimarySkillType.SALVAGE || primarySkillType == PrimarySkillType.SMELTING) {
//                // Child skills are not saved, but calculated
//                continue;
//            }
//
//            assertEquals(1 + primarySkillType.ordinal(), retrievedUser.getSkillLevel(primarySkillType));
//        }
//    }
//
//    @Test
//    void testSaveSkillXpValues() {
//        Player player = Mockito.mock(Player.class);
//        when(player.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
//        when(player.getName()).thenReturn("nossr50");
//        PlayerProfile playerProfile = sqlDatabaseManager.newUser(player);
//
//        // Validate values are starting from zero
//        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
//            assertEquals(0, playerProfile.getSkillXpLevel(primarySkillType));
//        }
//
//        // Change values
//        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
//            playerProfile.setSkillXpLevel(primarySkillType, 1 + primarySkillType.ordinal());
//        }
//
//        sqlDatabaseManager.saveUser(playerProfile);
//
//        PlayerProfile retrievedUser = sqlDatabaseManager.loadPlayerProfile(player.getName());
//
//        // Check that values got saved
//        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
//            if (primarySkillType == PrimarySkillType.SALVAGE || primarySkillType == PrimarySkillType.SMELTING) {
//                // Child skills are not saved, but calculated
//                continue;
//            }
//
//            assertEquals(1 + primarySkillType.ordinal(), retrievedUser.getSkillXpLevel(primarySkillType));
//        }
//    }
//}
