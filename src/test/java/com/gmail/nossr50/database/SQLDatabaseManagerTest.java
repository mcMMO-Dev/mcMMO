package com.gmail.nossr50.database;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.compat.CompatibilityManager;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import com.gmail.nossr50.util.platform.version.SimpleNumericVersion;
import com.gmail.nossr50.util.upgrade.UpgradeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SQLDatabaseManagerTest {
    private final static @NotNull Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static MockedStatic<mcMMO> mockedMcMMO;
    SQLDatabaseManager sqlDatabaseManager;
    static GeneralConfig generalConfig;
    static AdvancedConfig advancedConfig;
    static UpgradeManager upgradeManager;
    static CompatibilityManager compatibilityManager;

    @BeforeAll
    static void setUpAll() {
        // stub mcMMO.p
        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
        mcMMO.p = Mockito.mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);

        // general config mock
        mockGeneralConfig();

        // advanced config mock
        advancedConfig = Mockito.mock(AdvancedConfig.class);
        when(mcMMO.p.getAdvancedConfig()).thenReturn(advancedConfig);

        // starting level
        when(mcMMO.p.getAdvancedConfig().getStartingLevel()).thenReturn(0);

        // compatibility manager mock
        compatibilityManager = Mockito.mock(CompatibilityManager.class);
        when(mcMMO.getCompatibilityManager()).thenReturn(compatibilityManager);
        when(compatibilityManager.getMinecraftGameVersion()).thenReturn(new MinecraftGameVersion(1, 20, 4));

        // upgrade manager mock
        upgradeManager = Mockito.mock(UpgradeManager.class);
        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);

        // don't trigger upgrades
        when(mcMMO.getUpgradeManager().shouldUpgrade(any())).thenReturn(false);
    }

    private static void mockGeneralConfig() {
        generalConfig = Mockito.mock(GeneralConfig.class);
        when(generalConfig.getLocale()).thenReturn("en_US");
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);

        // max pool size
        when(mcMMO.p.getGeneralConfig().getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.MISC))
                .thenReturn(10);
        when(mcMMO.p.getGeneralConfig().getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.LOAD))
                .thenReturn(20);
        when(mcMMO.p.getGeneralConfig().getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.SAVE))
                .thenReturn(20);

        // max connections
        when(mcMMO.p.getGeneralConfig().getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.MISC))
                .thenReturn(30);
        when(mcMMO.p.getGeneralConfig().getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.LOAD))
                .thenReturn(30);
        when(mcMMO.p.getGeneralConfig().getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.SAVE))
                .thenReturn(30);

        // table prefix
        when(mcMMO.p.getGeneralConfig().getMySQLTablePrefix()).thenReturn("mcmmo_");

        // public key retrieval
        when(mcMMO.p.getGeneralConfig().getMySQLPublicKeyRetrieval()).thenReturn(true);

        // debug
        when(mcMMO.p.getGeneralConfig().getMySQLDebug()).thenReturn(true);

        // use mysql
        when(mcMMO.p.getGeneralConfig().getUseMySQL()).thenReturn(true);

        // use ssl
        when(mcMMO.p.getGeneralConfig().getMySQLSSL()).thenReturn(true);

        // username
        when(mcMMO.p.getGeneralConfig().getMySQLUserName()).thenReturn("sa");

        // password
        when(mcMMO.p.getGeneralConfig().getMySQLUserPassword()).thenReturn("");

        // host
        when(mcMMO.p.getGeneralConfig().getMySQLServerName()).thenReturn("localhost");
    }

    @BeforeEach
    void setUp() {
        assertNull(sqlDatabaseManager);
        sqlDatabaseManager = new SQLDatabaseManager(logger, "org.h2.Driver", true);
    }

    @AfterEach
    void tearDown() {
        sqlDatabaseManager = null;
    }

    @Test
    void testGetConnectionMisc() throws Exception {
        assertNotNull(sqlDatabaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.MISC));
    }

    @Test
    void testGetConnectionLoad() throws Exception {
        assertNotNull(sqlDatabaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.LOAD));
    }

    @Test
    void testGetConnectionSave() throws Exception {
        assertNotNull(sqlDatabaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.SAVE));
    }

    @Test
    void testNewUser() {
        Player player = Mockito.mock(Player.class);
        when(player.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
        when(player.getName()).thenReturn("nossr50");
        sqlDatabaseManager.newUser(player);
    }
}