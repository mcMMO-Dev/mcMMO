package com.gmail.nossr50.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerNameAndUUID;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.TestFileCleanup;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.util.upgrade.UpgradeManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.junit.jupiter.api.Tag;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mariadb.MariaDBContainer;
import org.testcontainers.mysql.MySQLContainer;

@Tag("docker")
@TestInstance(Lifecycle.PER_CLASS)
class SQLDatabaseManagerTest {

    private static final @NotNull Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final MySQLContainer MYSQL_CONTAINER = SharedSqlContainers.MYSQL_CONTAINER;
    private static final MariaDBContainer MARIADB_CONTAINER =
            SharedSqlContainers.MARIADB_CONTAINER;

    private static MockedStatic<mcMMO> mockedMcMMO;
    private static MockedStatic<ExperienceConfig> mockedExperienceConfig;
    private static GeneralConfig generalConfig;
    private static AdvancedConfig advancedConfig;
    private static UpgradeManager upgradeManager;
    private static SkillTools skillTools;
    private static MinecraftGameVersion minecraftGameVersion;
    private static File testDataFolder;

    // --- DB flavors you support ---
    enum DbFlavor {
        MYSQL,
        MARIADB
    }

    static Stream<DbFlavor> dbFlavors() {
        return Stream.of(DbFlavor.MYSQL, DbFlavor.MARIADB);
    }

    @BeforeAll
    void setUpAll() {
        // GIVEN a fully mocked mcMMO environment
        minecraftGameVersion = mock(MinecraftGameVersion.class);
        when(minecraftGameVersion.isAtLeast(anyInt(), anyInt(), anyInt())).thenReturn(true);

        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
        mcMMO.p = Mockito.mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);
        try {
            testDataFolder = java.nio.file.Files.createTempDirectory("mcmmo-sql-test-data-").toFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp test data folder", e);
        }
        when(mcMMO.p.getDataFolder()).thenReturn(testDataFolder);
        when(mcMMO.getMinecraftGameVersion()).thenReturn(minecraftGameVersion);

        mockGeneralConfigBase();

        advancedConfig = Mockito.mock(AdvancedConfig.class);
        when(mcMMO.p.getAdvancedConfig()).thenReturn(advancedConfig);
        when(mcMMO.p.getAdvancedConfig().getStartingLevel()).thenReturn(0);

        ExperienceConfig experienceConfig = Mockito.mock(ExperienceConfig.class);
        when(experienceConfig.getDiminishedReturnsEnabled()).thenReturn(false);
        mockedExperienceConfig = Mockito.mockStatic(ExperienceConfig.class);
        mockedExperienceConfig.when(ExperienceConfig::getInstance).thenReturn(experienceConfig);

        skillTools = new SkillTools(mcMMO.p);
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);

        upgradeManager = Mockito.mock(UpgradeManager.class);
        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);
        when(mcMMO.getUpgradeManager().shouldUpgrade(any())).thenReturn(false);

        // Null player lookup, shouldn't affect tests
        Server server = mock(Server.class);
        when(mcMMO.p.getServer()).thenReturn(server);
        when(server.getPlayerExact(anyString()))
                .thenReturn(null);
    }

    @AfterAll
    static void tearDownAll() {
        mockedMcMMO.close();
        mockedExperienceConfig.close();
        if (testDataFolder != null) {
            TestFileCleanup.deleteRecursively(testDataFolder);
        }
    }

    private static void mockGeneralConfigBase() {
        generalConfig = Mockito.mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);

        when(generalConfig.getLocale()).thenReturn("en_US");

        // pool sizes
        when(generalConfig.getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.MISC))
                .thenReturn(10);
        when(generalConfig.getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.LOAD))
                .thenReturn(20);
        when(generalConfig.getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.SAVE))
                .thenReturn(20);

        // max connections
        when(generalConfig.getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.MISC))
                .thenReturn(30);
        when(generalConfig.getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.LOAD))
                .thenReturn(30);
        when(generalConfig.getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.SAVE))
                .thenReturn(30);

        // table prefix
        when(generalConfig.getMySQLTablePrefix()).thenReturn("mcmmo_");

        // public key retrieval
        when(generalConfig.getMySQLPublicKeyRetrieval()).thenReturn(true);

        // use mysql
        when(generalConfig.getUseMySQL()).thenReturn(true);

        // SSL effectively off for >= 1.17
        when(generalConfig.getMySQLSSL()).thenReturn(true);

        // mob health bar default
        when(generalConfig.getMobHealthbarDefault()).thenReturn(MobHealthbarType.HEARTS);
    }

    private JdbcDatabaseContainer<?> containerFor(DbFlavor flavor) {
        return switch (flavor) {
            case MYSQL -> MYSQL_CONTAINER;
            case MARIADB -> MARIADB_CONTAINER;
        };
    }

    /**
     * Wire the mcMMO GeneralConfig mocks to a specific running container,
     * then construct a fresh SQLDatabaseManager using the MySQL driver
     * (also works for MariaDB).
     */
    private SQLDatabaseManager createManagerFor(DbFlavor flavor) {
        return createManagerFor(flavor, logger);
    }

    private SQLDatabaseManager createManagerFor(DbFlavor flavor, Logger managerLogger) {
        JdbcDatabaseContainer<?> container = containerFor(flavor);

        when(generalConfig.getMySQLServerName()).thenReturn(container.getHost());
        when(generalConfig.getMySQLServerPort()).thenReturn(container.getFirstMappedPort());
        when(generalConfig.getMySQLDatabaseName()).thenReturn(container.getDatabaseName());
        when(generalConfig.getMySQLUserName()).thenReturn(container.getUsername());
        when(generalConfig.getMySQLUserPassword()).thenReturn(container.getPassword());

        return new SQLDatabaseManager(managerLogger, "com.mysql.cj.jdbc.Driver");
    }

    /**
     * Helper to wipe all core mcMMO SQL tables for a given DB flavor.
     * This keeps tests isolated.
     */
    private void truncateAllCoreTables(DbFlavor flavor) {
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        try (Connection connection = databaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.MISC);
                Statement statement = connection.createStatement()) {

            // Order matters because of foreign key constraints in some setups
            // noinspection SqlWithoutWhere
            statement.executeUpdate("DELETE FROM mcmmo_cooldowns");
            // noinspection SqlWithoutWhere
            statement.executeUpdate("DELETE FROM mcmmo_experience");
            // noinspection SqlWithoutWhere
            statement.executeUpdate("DELETE FROM mcmmo_huds");
            // noinspection SqlWithoutWhere
            statement.executeUpdate("DELETE FROM mcmmo_skills");
            // noinspection SqlWithoutWhere
            statement.executeUpdate("DELETE FROM mcmmo_users");
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to truncate core tables", exception);
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // Connection / basic wiring
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - getConnection for all pool identifiers")
    @MethodSource("dbFlavors")
    void whenGettingConnectionsForAllPoolsShouldReturnNonNullConnections(DbFlavor flavor) throws Exception {
        // GIVEN a database manager for the selected flavor
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            // WHEN requesting connections for all pool identifiers
            for (SQLDatabaseManager.PoolIdentifier poolIdentifier : SQLDatabaseManager.PoolIdentifier.values()) {
                Connection connection = databaseManager.getConnection(poolIdentifier);

                // THEN each connection should be non-null and open
                assertThat(connection)
                        .as("Connection for pool %s should not be null", poolIdentifier)
                        .isNotNull();
            }
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // New user creation & initialization
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - newUser initializes skill levels and XP")
    @MethodSource("dbFlavors")
    void whenCreatingNewUserShouldInitializeSkillLevelsAndXpToStartingValues(DbFlavor flavor) {
        // GIVEN a new player and database manager
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        Player player = Mockito.mock(Player.class);
        UUID playerUuid = UUID.randomUUID();
        String playerName = "nossr50_" + flavor.name().toLowerCase();

        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(playerName);

        try {
            // WHEN creating a new user
            PlayerProfile playerProfile = databaseManager.newUser(player);

            // THEN the profile should be loaded with all skills and XP at starting values (0)
            assertThat(playerProfile).isNotNull();
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                assertThat(playerProfile.getSkillLevel(primarySkillType))
                        .as("Skill level for %s", primarySkillType)
                        .isZero();
                assertThat(playerProfile.getSkillXpLevel(primarySkillType))
                        .as("XP level for %s", primarySkillType)
                        .isZero();
            }
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // Saving skill levels / XP
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - saveUser persists skill level values")
    @MethodSource("dbFlavors")
    void whenSavingSkillLevelValuesShouldPersistToDatabase(DbFlavor flavor) {
        // GIVEN a new user with modified skill levels
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        Player player = Mockito.mock(Player.class);
        UUID playerUuid = UUID.randomUUID();
        String playerName = "nossr50_levels_" + flavor.name().toLowerCase();

        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(playerName);

        try {
            PlayerProfile playerProfile = databaseManager.newUser(player);

            // AND all XP start at zero
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                assertThat(playerProfile.getSkillXpLevel(primarySkillType))
                        .as("Initial XP for %s", primarySkillType)
                        .isZero();
            }

            // WHEN we modify levels and save
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                playerProfile.modifySkill(primarySkillType, 1 + primarySkillType.ordinal());
            }

            boolean saveSucceeded = databaseManager.saveUser(playerProfile);

            // THEN save should succeed
            assertThat(saveSucceeded).isTrue();

            // AND the retrieved user should have matching levels (except child skills)
            PlayerProfile retrievedUser = databaseManager.loadPlayerProfile(player.getName());
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                if (primarySkillType == PrimarySkillType.SALVAGE
                        || primarySkillType == PrimarySkillType.SMELTING) {
                    continue;
                }

                assertThat(retrievedUser.getSkillLevel(primarySkillType))
                        .as("Saved level for %s", primarySkillType)
                        .isEqualTo(1 + primarySkillType.ordinal());
            }
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - saveUser persists skill XP values")
    @MethodSource("dbFlavors")
    void whenSavingSkillXpValuesShouldPersistToDatabase(DbFlavor flavor) {
        // GIVEN a new user with modified XP levels
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        Player player = Mockito.mock(Player.class);
        UUID playerUuid = UUID.randomUUID();
        String playerName = "nossr50_xp_" + flavor.name().toLowerCase();

        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(playerName);

        try {
            PlayerProfile playerProfile = databaseManager.newUser(player);

            // AND all XP start at zero
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                assertThat(playerProfile.getSkillXpLevel(primarySkillType))
                        .as("Initial XP for %s", primarySkillType)
                        .isZero();
            }

            // WHEN we set XP values and save
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                playerProfile.setSkillXpLevel(primarySkillType, 1 + primarySkillType.ordinal());
            }

            boolean saveSucceeded = databaseManager.saveUser(playerProfile);

            // THEN save should succeed
            assertThat(saveSucceeded).isTrue();

            // AND the retrieved user should have matching XP (except child skills)
            PlayerProfile retrievedUser = databaseManager.loadPlayerProfile(player.getName());
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                if (primarySkillType == PrimarySkillType.SALVAGE
                        || primarySkillType == PrimarySkillType.SMELTING) {
                    continue;
                }

                assertThat(retrievedUser.getSkillXpLevel(primarySkillType))
                        .as("Saved XP for %s", primarySkillType)
                        .isEqualTo(1 + primarySkillType.ordinal());
            }
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // Saving cooldowns
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - saveUser persists EXPLOSIVE_SHOT cooldown")
    @MethodSource("dbFlavors")
    void whenSavingUserShouldPersistExplosiveShotCooldown(DbFlavor flavor) {
        // GIVEN a persisted user with an explosive shot cooldown
        truncateAllCoreTables(flavor);
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);

        final String playerName = "cooldown_" + flavor.name().toLowerCase();
        final UUID uuid = UUID.randomUUID();

        try {
            PlayerProfile profile = Mockito.spy(databaseManager.newUser(playerName, uuid));
            when(profile.getAbilityDATS(SuperAbilityType.EXPLOSIVE_SHOT)).thenReturn(3333L);

            // WHEN saving and reloading the profile
            assertThat(databaseManager.saveUser(profile)).isTrue();
            PlayerProfile loadedProfile = databaseManager.loadPlayerProfile(uuid);

            // THEN the cooldown should be preserved
            assertThat(loadedProfile.isLoaded()).isTrue();
            assertThat(loadedProfile.getAbilityDATS(SuperAbilityType.EXPLOSIVE_SHOT)).isEqualTo(3333);
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // Schema upgrades
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - upgrades legacy schema to add spears columns")
    @MethodSource("dbFlavors")
    void whenUpgradingLegacySchemaShouldAddSpearsColumns(DbFlavor flavor) throws Exception {
        // GIVEN a legacy schema without spears columns
        prepareLegacySchemaWithoutSpears(flavor);

        // AND spears columns do not exist yet
        assertThat(columnExists(flavor, "mcmmo_skills", "spears"))
                .as("Legacy skills table should NOT have spears column")
                .isFalse();
        assertThat(columnExists(flavor, "mcmmo_experience", "spears"))
                .as("Legacy experience table should NOT have spears column")
                .isFalse();
        assertThat(columnExists(flavor, "mcmmo_cooldowns", "spears"))
                .as("Legacy cooldowns table should NOT have spears column")
                .isFalse();

        // WHEN constructing a manager (which runs structure checks + upgrade logic)
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            // THEN spears columns should be added to all core tables
            assertThat(columnExists(flavor, "mcmmo_skills", "spears"))
                    .as("Skills table should have spears after upgrade")
                    .isTrue();
            assertThat(columnExists(flavor, "mcmmo_experience", "spears"))
                    .as("Experience table should have spears after upgrade")
                    .isTrue();
            assertThat(columnExists(flavor, "mcmmo_cooldowns", "spears"))
                    .as("Cooldowns table should have spears after upgrade")
                    .isTrue();
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - SQL_CHARSET_UTF8MB4 upgrade runs without error")
    @MethodSource("dbFlavors")
    void whenCharsetUpgradeIsRequiredShouldUpdateCharacterSet(DbFlavor flavor) {
        // GIVEN
        truncateAllCoreTables(flavor);

        // First reset and restub the upgrade manager so only the charset upgrade runs
        reset(upgradeManager);
        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);
        when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
        when(upgradeManager.shouldUpgrade(UpgradeType.SQL_CHARSET_UTF8MB4)).thenReturn(true);

        // WHEN – constructor will call checkStructure(), which will in turn call updateCharacterSet(...)
        SQLDatabaseManager manager = createManagerFor(flavor);

        // THEN – we at least expect the upgrade to be marked completed
        verify(upgradeManager, atLeastOnce()).setUpgradeCompleted(UpgradeType.SQL_CHARSET_UTF8MB4);

        manager.onDisable();

        // Restore default behavior for other tests: no upgrades
        when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
    }

    @ParameterizedTest(name = "{0} - when all upgrades are required, all upgrade helpers execute")
    @MethodSource("dbFlavors")
    void whenAllUpgradesRequiredShouldExecuteAllUpgradeHelpers(DbFlavor flavor) {
        // GIVEN – clean schema
        truncateAllCoreTables(flavor);

        // GIVEN – every UpgradeType should be considered "needed"
        reset(upgradeManager);
        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);
        when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(true);

        // WHEN – constructor will call checkStructure() which loops all UpgradeType values
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        // THEN – at least one call to mark some upgrades complete (and in practice, many)
        verify(upgradeManager, atLeastOnce()).setUpgradeCompleted(any(UpgradeType.class));

        databaseManager.onDisable();

        // Restore default for other tests
        when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
    }

    // ------------------------------------------------------------------------
    // New user -> rows in all core tables
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - newUser creates rows in all tables")
    @MethodSource("dbFlavors")
    void whenCreatingNewUserShouldCreateRowsInAllCoreTables(DbFlavor flavor) throws Exception {
        // GIVEN a clean database and a new user
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        truncateAllCoreTables(flavor);

        Player player = Mockito.mock(Player.class);
        UUID playerUuid = UUID.randomUUID();
        String playerName = "user_rows_" + flavor.name().toLowerCase();

        when(player.getUniqueId()).thenReturn(playerUuid);
        when(player.getName()).thenReturn(playerName);

        try {
            databaseManager.newUser(player);

            JdbcDatabaseContainer<?> container = containerFor(flavor);
            try (Connection connection = DriverManager.getConnection(
                    container.getJdbcUrl(), container.getUsername(), container.getPassword());
                    Statement statement = connection.createStatement()) {

                // THEN one row exists in mcmmo_users
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT COUNT(*) FROM mcmmo_users WHERE user = '" + playerName + "'")) {
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getInt(1)).isEqualTo(1);
                }

                // AND one row exists in mcmmo_skills
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT COUNT(*) FROM mcmmo_skills s JOIN mcmmo_users u ON s.user_id = u.id " +
                                "WHERE u.user = '" + playerName + "'")) {
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getInt(1)).isEqualTo(1);
                }

                // AND one row exists in mcmmo_experience
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT COUNT(*) FROM mcmmo_experience e JOIN mcmmo_users u ON e.user_id = u.id " +
                                "WHERE u.user = '" + playerName + "'")) {
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getInt(1)).isEqualTo(1);
                }

                // AND one row exists in mcmmo_cooldowns
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT COUNT(*) FROM mcmmo_cooldowns c JOIN mcmmo_users u ON c.user_id = u.id " +
                                "WHERE u.user = '" + playerName + "'")) {
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getInt(1)).isEqualTo(1);
                }

                // AND one row exists in mcmmo_huds
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT COUNT(*) FROM mcmmo_huds h JOIN mcmmo_users u ON h.user_id = u.id " +
                                "WHERE u.user = '" + playerName + "'")) {
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getInt(1)).isEqualTo(1);
                }
            }
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // getStoredUsers
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - getStoredUsers returns usernames")
    @MethodSource("dbFlavors")
    void whenGettingStoredUsersShouldReturnPersistedUsernames(DbFlavor flavor) {
        // GIVEN a number of persisted users
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        truncateAllCoreTables(flavor);

        String baseName = "stored_user_" + flavor.name().toLowerCase();

        try {
            for (int index = 0; index < 3; index++) {
                Player player = Mockito.mock(Player.class);
                when(player.getUniqueId()).thenReturn(UUID.randomUUID());
                when(player.getName()).thenReturn(baseName + "_" + index);
                databaseManager.newUser(player);
            }

            // WHEN retrieving stored users
            var storedUsers = databaseManager.getStoredUsers();

            // THEN all created usernames should be present
            assertThat(storedUsers)
                    .contains(baseName + "_0", baseName + "_1", baseName + "_2");
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // saveUserUUID / saveUserUUIDs
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - saveUserUUID updates uuid column and lookup")
    @MethodSource("dbFlavors")
    void whenSavingSingleUserUuidShouldUpdateUuidColumnAndLookupBehavior(DbFlavor flavor) throws Exception {
        // GIVEN a single persisted user
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        truncateAllCoreTables(flavor);

        String username = "uuid_single_" + flavor.name().toLowerCase();
        Player player = Mockito.mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getName()).thenReturn(username);

        try {
            databaseManager.newUser(player);
            UUID newUuid = UUID.randomUUID();

            // WHEN updating the user's UUID
            boolean updated = databaseManager.saveUserUUID(username, newUuid);

            // THEN the update should succeed
            assertThat(updated).isTrue();

            // AND the UUID column should match in the database
            JdbcDatabaseContainer<?> container = containerFor(flavor);
            try (Connection connection = DriverManager.getConnection(
                    container.getJdbcUrl(), container.getUsername(), container.getPassword());
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(
                            "SELECT uuid FROM mcmmo_users WHERE user = '" + username + "'")) {

                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getString(1)).isEqualTo(newUuid.toString());
            }

            // AND the old UUID should not resolve a profile
            PlayerProfile oldProfile = databaseManager.loadPlayerProfile(UUID.randomUUID());
            assertThat(oldProfile.isLoaded()).isFalse();

            // AND the new UUID should resolve the profile
            PlayerProfile newProfile = databaseManager.loadPlayerProfile(newUuid);
            assertThat(newProfile.isLoaded()).isTrue();
            assertThat(newProfile.getPlayerName()).isEqualTo(username);
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - saveUserUUIDs bulk updates multiple rows")
    @MethodSource("dbFlavors")
    void whenSavingBulkUserUuidsShouldUpdateAllRows(DbFlavor flavor) throws Exception {
        // GIVEN two persisted users
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        truncateAllCoreTables(flavor);

        String firstUsername = "uuid_bulk_1_" + flavor.name().toLowerCase();
        String secondUsername = "uuid_bulk_2_" + flavor.name().toLowerCase();

        Player firstPlayer = Mockito.mock(Player.class);
        when(firstPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
        when(firstPlayer.getName()).thenReturn(firstUsername);
        databaseManager.newUser(firstPlayer);

        Player secondPlayer = Mockito.mock(Player.class);
        when(secondPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
        when(secondPlayer.getName()).thenReturn(secondUsername);
        databaseManager.newUser(secondPlayer);

        Map<String, UUID> uuidUpdates = new HashMap<>();
        UUID firstNewUuid = UUID.randomUUID();
        UUID secondNewUuid = UUID.randomUUID();
        uuidUpdates.put(firstUsername, firstNewUuid);
        uuidUpdates.put(secondUsername, secondNewUuid);

        try {
            // WHEN performing a bulk UUID update
            boolean updateSucceeded = databaseManager.saveUserUUIDs(uuidUpdates);

            // THEN the update should succeed
            assertThat(updateSucceeded).isTrue();

            // AND both rows should reflect the new UUID values
            JdbcDatabaseContainer<?> container = containerFor(flavor);
            try (Connection connection = DriverManager.getConnection(
                    container.getJdbcUrl(), container.getUsername(), container.getPassword());
                    Statement statement = connection.createStatement()) {

                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT user, uuid FROM mcmmo_users WHERE user IN ('" + firstUsername + "','" +
                                secondUsername + "')")) {
                    int rowsSeen = 0;
                    while (resultSet.next()) {
                        String user = resultSet.getString("user");
                        String uuid = resultSet.getString("uuid");
                        if (user.equals(firstUsername)) {
                            assertThat(uuid).isEqualTo(firstNewUuid.toString());
                            rowsSeen++;
                        } else if (user.equals(secondUsername)) {
                            assertThat(uuid).isEqualTo(secondNewUuid.toString());
                            rowsSeen++;
                        }
                    }
                    assertThat(rowsSeen).isEqualTo(2);
                }
            }

            // AND getStoredUsers still contains both names
            var storedUsers = databaseManager.getStoredUsers();
            assertThat(storedUsers).contains(firstUsername, secondUsername);
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // purgePowerlessUsers
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - purgePowerlessUsers removes only zero-skill users")
    @MethodSource("dbFlavors")
    void whenPurgingPowerlessUsersShouldRemoveOnlyZeroSkillUsers(DbFlavor flavor) throws Exception {
        // GIVEN one powerless user (all skills zero) and one powered user
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        truncateAllCoreTables(flavor);

        Player powerlessPlayer = Mockito.mock(Player.class);
        when(powerlessPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
        when(powerlessPlayer.getName()).thenReturn("powerless_" + flavor.name().toLowerCase());
        databaseManager.newUser(powerlessPlayer);

        Player poweredPlayer = Mockito.mock(Player.class);
        UUID poweredUuid = UUID.randomUUID();
        when(poweredPlayer.getUniqueId()).thenReturn(poweredUuid);
        when(poweredPlayer.getName()).thenReturn("powered_" + flavor.name().toLowerCase());
        PlayerProfile poweredProfile = databaseManager.newUser(poweredPlayer);
        poweredProfile.modifySkill(PrimarySkillType.MINING, 10);
        assertThat(databaseManager.saveUser(poweredProfile)).isTrue();

        // WHEN purging powerless users
        int purgedCount = databaseManager.purgePowerlessUsers();

        // THEN exactly one user should be purged
        assertThat(purgedCount)
                .as("Exactly one powerless user should be purged")
                .isEqualTo(1);

        JdbcDatabaseContainer<?> container = containerFor(flavor);
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
                Statement statement = connection.createStatement()) {

            // AND powerless user should be gone
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT COUNT(*) FROM mcmmo_users WHERE user = '" + powerlessPlayer.getName() + "'")) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getInt(1)).isZero();
            }

            // AND powered user should still exist
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT COUNT(*) FROM mcmmo_users WHERE user = '" + poweredPlayer.getName() + "'")) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getInt(1)).isEqualTo(1);
            }
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // purgeOldUsers
    // ------------------------------------------------------------------------

    /**
     * Regression test for GitHub issue #4251: the purge query compared a seconds-based
     * lastlogin delta against a milliseconds-based cutoff, so old users were never purged.
     */
    @ParameterizedTest(name = "{0} - purgeOldUsers removes only users inactive past the cutoff")
    @MethodSource("dbFlavors")
    void whenPurgingOldUsersShouldRemoveOnlyUsersInactiveLongerThanCutoff(DbFlavor flavor)
            throws Exception {
        // GIVEN a six month purge cutoff and one long-inactive user plus one active user
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        truncateAllCoreTables(flavor);

        final long sixMonthsInMillis = 2_630_000_000L * 6L;
        when(mcMMO.p.getPurgeTime()).thenReturn(sixMonthsInMillis);

        Player inactivePlayer = Mockito.mock(Player.class);
        when(inactivePlayer.getUniqueId()).thenReturn(UUID.randomUUID());
        when(inactivePlayer.getName()).thenReturn("inactive_" + flavor.name().toLowerCase());
        databaseManager.newUser(inactivePlayer);

        Player activePlayer = Mockito.mock(Player.class);
        when(activePlayer.getUniqueId()).thenReturn(UUID.randomUUID());
        when(activePlayer.getName()).thenReturn("active_" + flavor.name().toLowerCase());
        databaseManager.newUser(activePlayer);

        // AND the inactive user last logged in one year ago (in unix seconds)
        final long oneYearInSeconds = 365L * 24L * 60L * 60L;
        try (Connection connection =
                databaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.MISC);
                Statement statement = connection.createStatement()) {

            statement.executeUpdate("UPDATE mcmmo_users SET lastlogin = (UNIX_TIMESTAMP() - "
                    + oneYearInSeconds + ") WHERE user = '" + inactivePlayer.getName() + "'");
        }

        // WHEN purging old users
        databaseManager.purgeOldUsers();

        JdbcDatabaseContainer<?> container = containerFor(flavor);
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
                Statement statement = connection.createStatement()) {

            // THEN the long-inactive user should be purged
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT COUNT(*) FROM mcmmo_users WHERE user = '"
                            + inactivePlayer.getName() + "'")) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getInt(1))
                        .as("User inactive longer than the cutoff should be purged")
                        .isZero();
            }

            // AND the recently active user should survive
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT COUNT(*) FROM mcmmo_users WHERE user = '"
                            + activePlayer.getName() + "'")) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getInt(1))
                        .as("Recently active user should not be purged")
                        .isEqualTo(1);
            }
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // Missing user / fallback behavior
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - loadPlayerProfile(missing name) returns empty profile with zero skills")
    @MethodSource("dbFlavors")
    void whenLoadingMissingUserByNameShouldReturnEmptyProfileWithZeroSkills(DbFlavor flavor) {
        // GIVEN an empty database
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        truncateAllCoreTables(flavor);

        String ghostName = "ghost_" + flavor.name().toLowerCase();

        try {
            // WHEN loading a profile by a missing username
            PlayerProfile profile = databaseManager.loadPlayerProfile(ghostName);

            // THEN profile should not be null, and all skill levels should be zero
            assertThat(profile).isNotNull();
            for (PrimarySkillType type : PrimarySkillType.values()) {
                assertThat(profile.getSkillLevel(type))
                        .as("Expected skill level 0 for %s on missing user profile", type)
                        .isZero();
            }
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // Mob health HUD reset
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - resetMobHealthSettings sets mobhealthbar to default for all users")
    @MethodSource("dbFlavors")
    void whenResettingMobHealthSettingsShouldResetAllHudRowsToDefault(DbFlavor flavor) throws Exception {
        // GIVEN multiple users with non-default mobhealthbar values
        SQLDatabaseManager databaseManager = createManagerFor(flavor);
        truncateAllCoreTables(flavor);

        databaseManager.newUser("hudguy1_" + flavor.name().toLowerCase(), UUID.randomUUID());
        databaseManager.newUser("hudguy2_" + flavor.name().toLowerCase(), UUID.randomUUID());

        try (Connection connection = databaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.MISC);
                Statement statement = connection.createStatement()) {

            statement.executeUpdate("UPDATE mcmmo_huds SET mobhealthbar = 'SOMETHING_ELSE'");
        }

        try {
            // WHEN resetMobHealthSettings is invoked
            databaseManager.resetMobHealthSettings();

            // THEN all HUD rows should have the default mobhealthbar type
            try (Connection connection = databaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.MISC);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT DISTINCT mobhealthbar FROM mcmmo_huds")) {

                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getString(1)).isEqualTo(MobHealthbarType.HEARTS.name());
                assertThat(resultSet.next())
                        .as("Only one distinct mobhealthbar value should remain")
                        .isFalse();
            }
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // loadPlayerProfile by name / UUID / Player
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - loadPlayerProfile(name)")
    @MethodSource("dbFlavors")
    void whenLoadingByNameShouldReturnMatchingProfile(DbFlavor flavor) {
        // GIVEN a persisted user
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String playerName = "nossr50_" + flavor.name().toLowerCase() + "_byName";
        UUID uuid = UUID.randomUUID();

        try {
            PlayerProfile createdProfile = databaseManager.newUser(playerName, uuid);
            assertThat(createdProfile.isLoaded()).isTrue();

            // WHEN loading by name
            PlayerProfile loadedProfile = databaseManager.loadPlayerProfile(playerName);

            // THEN the loaded profile should match the persisted data
            assertThat(loadedProfile.isLoaded()).isTrue();
            assertThat(loadedProfile.getPlayerName()).isEqualTo(playerName);
            assertThat(loadedProfile.getUniqueId()).isEqualTo(uuid);
        } finally {
            databaseManager.onDisable();
        }
    }

    /** Names are matched ignoring case, and the profile carries the name as it is stored. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("dbFlavors")
    void whenLoadingByNameInOtherCapitalsShouldReturnTheStoredName(DbFlavor flavor) {
        // Given - a stored player
        truncateAllCoreTables(flavor);
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);
        final String playerName = "stored_" + flavor.name().toLowerCase(Locale.ROOT);
        final UUID uuid = UUID.randomUUID();
        databaseManager.newUser(playerName, uuid);

        try {
            // When - they are loaded by their name in other capitals
            final PlayerProfile profile = databaseManager.loadPlayerProfile(
                    playerName.toUpperCase(Locale.ROOT));

            // Then - the profile carries the name as it is stored
            assertThat(profile.getUniqueId()).isEqualTo(uuid);
            assertThat(profile.getPlayerName()).isEqualTo(playerName);
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - loadPlayerProfile(uuid)")
    @MethodSource("dbFlavors")
    void whenLoadingByUuidShouldReturnMatchingProfileAndUnknownUuidShouldReturnUnloadedProfile(DbFlavor flavor) {
        // GIVEN a persisted user
        truncateAllCoreTables(flavor);
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);

        final String playerName = "nossr50_" + flavor.name().toLowerCase() + "_byUuid";
        final UUID uuid = UUID.randomUUID();

        try {
            PlayerProfile newlyCreatedUser = databaseManager.newUser(playerName, uuid);
            databaseManager.saveUser(newlyCreatedUser);

            // WHEN loading by the correct UUID
            PlayerProfile loadedProfile = databaseManager.loadPlayerProfile(uuid, "tEmPnAmE");

            // THEN the profile should be loaded and match, under the name stored for it
            assertThat(loadedProfile.isLoaded()).isTrue();
            assertThat(loadedProfile.getPlayerName()).isEqualTo(playerName);
            assertThat(loadedProfile.getUniqueId()).isEqualTo(uuid);

            // AND loading by an unknown UUID should return an unloaded profile
            PlayerProfile unknownProfile = databaseManager.loadPlayerProfile(UUID.randomUUID());
            assertThat(unknownProfile.isLoaded()).isFalse();
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - loadPlayerProfile(Player) updates username")
    @MethodSource("dbFlavors")
    void whenLoadingByPlayerShouldUpdateUsernameForExistingUuid(DbFlavor flavor) {
        // GIVEN a user persisted under an original name
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String originalName = "nossr50_original_" + flavor.name().toLowerCase();
        UUID uuid = UUID.randomUUID();

        try {
            databaseManager.newUser(originalName, uuid);

            // AND a Player with the same UUID but an updated name
            String updatedName = "nossr50_updated_" + flavor.name().toLowerCase();
            Player player = onlinePlayer(updatedName, uuid);

            // WHEN loading via Player
            PlayerProfile updatedProfile = databaseManager.loadPlayerProfile(player);

            // THEN the profile should reflect the new name
            assertThat(updatedProfile.isLoaded()).isTrue();
            assertThat(updatedProfile.getPlayerName()).isEqualTo(updatedName);
            assertThat(updatedProfile.getUniqueId()).isEqualTo(uuid);

            // AND loading by new name should work
            PlayerProfile byNewName = databaseManager.loadPlayerProfile(updatedName);
            assertThat(byNewName.isLoaded()).isTrue();
            assertThat(byNewName.getPlayerName()).isEqualTo(updatedName);
            assertThat(byNewName.getUniqueId()).isEqualTo(uuid);

            // AND loading by old name should now return an unloaded profile
            PlayerProfile byOldName = databaseManager.loadPlayerProfile(originalName);
            assertThat(byOldName.isLoaded()).isFalse();
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - loadPlayerProfile(name) data not found")
    @MethodSource("dbFlavors")
    void whenLoadingNonExistentPlayerByNameShouldReturnUnloadedProfile(DbFlavor flavor) {
        // GIVEN an empty database
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            // WHEN loading a non-existent player by name
            PlayerProfile profile = databaseManager.loadPlayerProfile("nonexistent_" + flavor.name().toLowerCase());

            // THEN the profile should not be loaded
            assertThat(profile.isLoaded()).isFalse();
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // removeUser
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - removeUser")
    @MethodSource("dbFlavors")
    void whenRemovingUserShouldDeleteOnlySpecifiedUser(DbFlavor flavor) {
        // GIVEN two persisted users
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String keepName = "keepme_" + flavor.name().toLowerCase();
        UUID keepUuid = UUID.randomUUID();
        databaseManager.newUser(keepName, keepUuid);

        String deleteName = "deleteme_" + flavor.name().toLowerCase();
        UUID deleteUuid = UUID.randomUUID();
        databaseManager.newUser(deleteName, deleteUuid);

        try {
            // AND both users exist
            assertThat(databaseManager.loadPlayerProfile(keepUuid).isLoaded()).isTrue();
            assertThat(databaseManager.loadPlayerProfile(deleteUuid).isLoaded()).isTrue();

            // WHEN removing the delete user
            boolean firstRemovalSucceeded = databaseManager.removeUser(deleteName, deleteUuid);

            // THEN the first removal should succeed and the user should be gone
            assertThat(firstRemovalSucceeded).isTrue();
            assertThat(databaseManager.loadPlayerProfile(deleteUuid).isLoaded()).isFalse();

            // AND a second removal should fail
            boolean secondRemovalSucceeded = databaseManager.removeUser(deleteName, deleteUuid);
            assertThat(secondRemovalSucceeded).isFalse();

            // AND the keep user should still exist
            assertThat(databaseManager.loadPlayerProfile(keepUuid).isLoaded()).isTrue();
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // purgeOldUsers
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - purgeOldUsers")
    @MethodSource("dbFlavors")
    void whenPurgingOldUsersShouldRemoveOnlyOutdatedUsers(DbFlavor flavor) throws Exception {
        // GIVEN one old user and one recent user
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        when(mcMMO.p.getPurgeTime()).thenReturn(10L);

        String oldName = "old_" + flavor.name().toLowerCase();
        UUID oldUuid = UUID.randomUUID();
        databaseManager.newUser(oldName, oldUuid);

        String recentName = "recent_" + flavor.name().toLowerCase();
        UUID recentUuid = UUID.randomUUID();
        databaseManager.newUser(recentName, recentUuid);

        try (Connection connection = databaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.MISC);
                Statement statement = connection.createStatement()) {

            statement.executeUpdate("UPDATE mcmmo_users SET lastlogin = 0 WHERE `user` = '" + oldName + "'");
            statement.executeUpdate(
                    "UPDATE mcmmo_users SET lastlogin = UNIX_TIMESTAMP() WHERE `user` = '" + recentName + "'");
        }

        try {
            // WHEN purgeOldUsers is invoked
            databaseManager.purgeOldUsers();

            // THEN old user should be removed
            PlayerProfile oldProfile = databaseManager.loadPlayerProfile(oldUuid);
            assertThat(oldProfile.isLoaded())
                    .as("Old user should have been purged")
                    .isFalse();

            // AND recent user should remain
            PlayerProfile recentProfile = databaseManager.loadPlayerProfile(recentUuid);
            assertThat(recentProfile.isLoaded())
                    .as("Recent user should remain")
                    .isTrue();
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // readRank
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - readRank")
    @MethodSource("dbFlavors")
    void whenReadingRankShouldReturnExpectedPositions(DbFlavor flavor) {
        // GIVEN two users with different levels
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String rankGirlName = "rankGirl_" + flavor.name().toLowerCase();
        UUID rankGirlUuid = new UUID(1337L, 1337L);

        String rankBoyName = "rankBoy_" + flavor.name().toLowerCase();
        UUID rankBoyUuid = new UUID(7331L, 7331L);

        try {
            databaseManager.newUser(rankGirlName, rankGirlUuid);
            PlayerProfile girlProfile = databaseManager.loadPlayerProfile(rankGirlUuid);
            for (PrimarySkillType type : PrimarySkillType.values()) {
                if (SkillTools.isChildSkill(type)) {
                    continue;
                }
                girlProfile.modifySkill(type, 100);
            }
            assertThat(databaseManager.saveUser(girlProfile)).isTrue();

            databaseManager.newUser(rankBoyName, rankBoyUuid);
            PlayerProfile boyProfile = databaseManager.loadPlayerProfile(rankBoyUuid);
            for (PrimarySkillType type : PrimarySkillType.values()) {
                if (SkillTools.isChildSkill(type)) {
                    continue;
                }
                boyProfile.modifySkill(type, 10);
            }
            assertThat(databaseManager.saveUser(boyProfile)).isTrue();

            // WHEN reading rank for both users
            Map<PrimarySkillType, Integer> girlRanks = databaseManager.readRank(rankGirlName);
            Map<PrimarySkillType, Integer> boyRanks = databaseManager.readRank(rankBoyName);

            // THEN girl should be rank 1, boy rank 2 for all non-child skills
            for (PrimarySkillType type : PrimarySkillType.values()) {
                if (SkillTools.isChildSkill(type)) {
                    assertThat(girlRanks.get(type)).isNull();
                    assertThat(boyRanks.get(type)).isNull();
                } else {
                    assertThat(girlRanks.get(type)).isEqualTo(1);
                    assertThat(boyRanks.get(type)).isEqualTo(2);
                }
            }

            // AND total ranking (null key) should be 1 and 2 respectively
            assertThat(girlRanks.get(null)).isEqualTo(1);
            assertThat(boyRanks.get(null)).isEqualTo(2);
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - readLeaderboard(MINING) returns users in descending order")
    @MethodSource("dbFlavors")
    void whenReadingLeaderboardForMiningShouldReturnUsersOrderedBySkillDescending(DbFlavor flavor) throws Exception {
        // GIVEN
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String topPlayerName = "leader_top_" + flavor.name().toLowerCase();
        UUID topUuid = UUID.randomUUID();
        databaseManager.newUser(topPlayerName, topUuid);

        String lowerPlayerName = "leader_low_" + flavor.name().toLowerCase();
        UUID lowerUuid = UUID.randomUUID();
        databaseManager.newUser(lowerPlayerName, lowerUuid);

        PlayerProfile topProfile = databaseManager.loadPlayerProfile(topUuid);
        PlayerProfile lowerProfile = databaseManager.loadPlayerProfile(lowerUuid);

        // GIVEN – mining levels: top > low
        topProfile.modifySkill(PrimarySkillType.MINING, 200);
        lowerProfile.modifySkill(PrimarySkillType.MINING, 50);

        assertThat(databaseManager.saveUser(topProfile)).isTrue();
        assertThat(databaseManager.saveUser(lowerProfile)).isTrue();

        // WHEN
        List<PlayerStat> miningStats =
                databaseManager.readLeaderboard(PrimarySkillType.MINING, 1, 10);

        // THEN
        assertThat(miningStats)
                .extracting(PlayerStat::playerName)
                .containsExactly(topPlayerName, lowerPlayerName);

        assertThat(miningStats)
                .extracting(PlayerStat::value)
                .containsExactly(200, 50);

        databaseManager.onDisable();
    }

    @ParameterizedTest(name = "{0} - readLeaderboard(null) uses total column")
    @MethodSource("dbFlavors")
    void whenReadingLeaderboardForTotalShouldUseTotalColumn(DbFlavor flavor) throws Exception {
        // GIVEN
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String topPlayerName = "leader_total_top_" + flavor.name().toLowerCase();
        UUID topUuid = UUID.randomUUID();
        databaseManager.newUser(topPlayerName, topUuid);

        String lowerPlayerName = "leader_total_low_" + flavor.name().toLowerCase();
        UUID lowerUuid = UUID.randomUUID();
        databaseManager.newUser(lowerPlayerName, lowerUuid);

        PlayerProfile topProfile = databaseManager.loadPlayerProfile(topUuid);
        PlayerProfile lowerProfile = databaseManager.loadPlayerProfile(lowerUuid);

        // GIVEN – only MINING changed, but total is recomputed in updateSkills()
        topProfile.modifySkill(PrimarySkillType.MINING, 300);
        lowerProfile.modifySkill(PrimarySkillType.MINING, 100);

        assertThat(databaseManager.saveUser(topProfile)).isTrue();
        assertThat(databaseManager.saveUser(lowerProfile)).isTrue();

        // WHEN – null skill → ALL_QUERY_VERSION ("total")
        List<PlayerStat> totalStats = databaseManager.readLeaderboard(null, 1, 10);

        // THEN
        assertThat(totalStats)
                .extracting(PlayerStat::playerName)
                .containsExactly(topPlayerName, lowerPlayerName);

        assertThat(totalStats)
                .extracting(PlayerStat::value)
                .containsExactly(300, 100);

        databaseManager.onDisable();
    }

    @ParameterizedTest(name = "{0} - readLeaderboard(child skill) throws InvalidSkillException")
    @MethodSource("dbFlavors")
    void whenReadingLeaderboardForChildSkillShouldThrowInvalidSkillException(DbFlavor flavor) {
        // GIVEN
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        // WHEN / THEN
        assertThatThrownBy(() ->
                databaseManager.readLeaderboard(PrimarySkillType.SALVAGE, 1, 10))
                .isInstanceOf(InvalidSkillException.class)
                .hasMessageContaining("child skills do not have leaderboards");

        databaseManager.onDisable();
    }

    /**
     * Gotcha coverage: a backend outage must be distinguishable from an empty leaderboard.
     * readLeaderboard swallows SQL errors for the command path, but readLeaderboardSnapshot must
     * propagate them so snapshot-building callers keep their last good data instead of
     * blanking out.
     */
    @ParameterizedTest(name = "{0} - readLeaderboardSnapshot propagates read failures that readLeaderboard swallows")
    @MethodSource("dbFlavors")
    void readLeaderboardSnapshotShouldThrowWhenBackendReadFails(DbFlavor flavor) throws Exception {
        // Given - a manager whose connection acquisition fails, as during a database outage
        truncateAllCoreTables(flavor);
        final SQLDatabaseManager databaseManager = Mockito.spy(createManagerFor(flavor));
        Mockito.doThrow(new SQLException("Simulated database outage"))
                .when(databaseManager).getConnection(any());

        try {
            // When - the snapshot-facing read runs during the outage
            // Then - it surfaces the failure instead of returning empty results
            assertThatThrownBy(() -> databaseManager.readLeaderboardSnapshot(10))
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(SQLException.class);

            // And - the command-facing read keeps its swallow-and-return-empty contract
            assertThat(databaseManager.readLeaderboard(PrimarySkillType.MINING, 1, 10))
                    .isEmpty();
        } finally {
            databaseManager.onDisable();
        }
    }

    /**
     * Every non-child skill column, plus {@code total}, that {@code readLeaderboard} sorts on and
     * therefore must be indexed. Taken straight from production so these tests always cover the
     * exact columns the schema and index migration maintain.
     */
    private static final List<String> LEADERBOARD_INDEX_COLUMNS =
            SQLDatabaseManager.leaderboardIndexColumns();

    /**
     * The derived column list drives both the index migration and the coverage assertions in this
     * class; a skill silently falling out of it would lose its leaderboard index everywhere, so
     * pin the expected contents here.
     */
    @Test
    void leaderboardIndexColumnsShouldContainEveryNonChildSkillPlusTotal() {
        // When - deriving the full set of leaderboard-indexed columns
        final List<String> columns = SQLDatabaseManager.leaderboardIndexColumns();

        // Then - it contains exactly one column per non-child skill plus the total column
        assertThat(columns).containsExactlyInAnyOrder(
                "taming", "mining", "woodcutting", "repair", "unarmed", "herbalism", "excavation",
                "archery", "swords", "axes", "acrobatics", "fishing", "alchemy", "crossbows",
                "tridents", "maces", "spears", "total");
    }

    @ParameterizedTest(name = "{0} - fresh install creates a leaderboard index on every skill column")
    @MethodSource("dbFlavors")
    void whenCreatingSkillsTableFromScratchShouldIndexEveryLeaderboardColumn(DbFlavor flavor)
            throws Exception {
        // Given - a database with no mcMMO tables at all (true fresh install)
        dropAllCoreTables(flavor);

        // And - the index migration is explicitly disabled, so only the CREATE TABLE path can
        //       produce the indexes. This proves fresh installs are covered by the schema itself.
        reset(upgradeManager);
        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);
        when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);

        // When - a manager is constructed, which runs checkStructure() -> ensureSkillsTable()
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            // Then - every leaderboard column has an index from the CREATE TABLE statement alone
            for (final String column : LEADERBOARD_INDEX_COLUMNS) {
                assertThat(leaderboardIndexExists(flavor, column))
                        .as("Fresh install should index leaderboard column '%s'", column)
                        .isTrue();
            }
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - migration re-adds leaderboard indexes on a pre-existing table")
    @MethodSource("dbFlavors")
    void whenLeaderboardIndexesAreMissingShouldAddThemViaMigration(DbFlavor flavor) throws Exception {
        // Given - a normally created schema whose leaderboard indexes are then dropped, simulating
        //         a server that upgraded from a version predating per-skill indexes
        truncateAllCoreTables(flavor);
        dropLeaderboardIndexes(flavor);

        // And - the indexes really are gone before the migration runs
        for (final String column : LEADERBOARD_INDEX_COLUMNS) {
            assertThat(leaderboardIndexExists(flavor, column))
                    .as("Precondition: leaderboard column '%s' should be unindexed", column)
                    .isFalse();
        }

        // And - only the leaderboard-index upgrade is flagged, isolating it from other migrations
        reset(upgradeManager);
        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);
        when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
        when(upgradeManager.shouldUpgrade(UpgradeType.ADD_SKILL_LEADERBOARD_INDEXES))
                .thenReturn(true);

        // When - a manager is constructed, running the ADD_SKILL_LEADERBOARD_INDEXES migration
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            // Then - every leaderboard column is indexed again
            for (final String column : LEADERBOARD_INDEX_COLUMNS) {
                assertThat(leaderboardIndexExists(flavor, column))
                        .as("Migration should index leaderboard column '%s'", column)
                        .isTrue();
            }

            // And - the upgrade is marked complete only because all indexes succeeded
            verify(upgradeManager, atLeastOnce())
                    .setUpgradeCompleted(UpgradeType.ADD_SKILL_LEADERBOARD_INDEXES);
        } finally {
            databaseManager.onDisable();
            // Restore the default no-upgrade behavior for other tests
            when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
        }
    }

    @ParameterizedTest(name = "{0} - re-running the index migration is idempotent")
    @MethodSource("dbFlavors")
    void whenLeaderboardIndexesAlreadyExistShouldBeIdempotent(DbFlavor flavor) throws Exception {
        // Given - a fresh schema that already carries every leaderboard index
        truncateAllCoreTables(flavor);
        for (final String column : LEADERBOARD_INDEX_COLUMNS) {
            assertThat(leaderboardIndexExists(flavor, column))
                    .as("Precondition: leaderboard column '%s' should already be indexed", column)
                    .isTrue();
        }

        // And - the leaderboard-index upgrade is flagged even though nothing needs adding
        reset(upgradeManager);
        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);
        when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
        when(upgradeManager.shouldUpgrade(UpgradeType.ADD_SKILL_LEADERBOARD_INDEXES))
                .thenReturn(true);

        // When - the migration runs against an already-indexed table
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            // Then - the columns remain indexed (probe short-circuits the ALTER TABLE)...
            for (final String column : LEADERBOARD_INDEX_COLUMNS) {
                assertThat(leaderboardIndexExists(flavor, column))
                        .as("Idempotent run should keep leaderboard column '%s' indexed", column)
                        .isTrue();
            }

            // And - ...yet the upgrade is still marked complete, so it won't retry forever
            verify(upgradeManager, atLeastOnce())
                    .setUpgradeCompleted(UpgradeType.ADD_SKILL_LEADERBOARD_INDEXES);
        } finally {
            databaseManager.onDisable();
            when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
        }
    }

    /**
     * Regression coverage for the leaderboard query plan itself: with the per-skill indexes in
     * place, the exact production query must let the engine drive the sort from the index. A
     * mixed-direction ORDER BY (e.g. skill DESC with an implicit-ASC tiebreak) silently degrades
     * to a filesort of every qualifying row on both MySQL and MariaDB, defeating the indexes.
     */
    @ParameterizedTest(name = "{0} - leaderboard query resolves its sort from the index without filesort")
    @MethodSource("dbFlavors")
    void leaderboardQueryShouldAvoidFilesortWhenIndexesArePresent(DbFlavor flavor) throws Exception {
        // Given - an indexed schema with enough rows that the planner's choice is meaningful
        truncateAllCoreTables(flavor);
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            for (int index = 0; index < 60; index++) {
                createUserWithSkills(databaseManager,
                        "explain_user_" + index + "_" + flavor.name().toLowerCase(),
                        UUID.randomUUID(), Map.of(PrimarySkillType.MINING, 1 + index));
            }

            // When - running EXPLAIN against the exact query readLeaderboard executes
            final String explainSql = "EXPLAIN " + databaseManager.leaderboardQuery("mining");
            final List<String> extras = new ArrayList<>();
            try (Connection connection =
                    databaseManager.getConnection(SQLDatabaseManager.PoolIdentifier.MISC);
                    PreparedStatement statement = connection.prepareStatement(explainSql)) {
                final int limitIndex = SQLDatabaseManager.bindInvalidOldUsernames(statement, 1);
                statement.setInt(limitIndex, 0);
                statement.setInt(limitIndex + 1, 10);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        extras.add(String.valueOf(resultSet.getString("Extra")));
                    }
                }
            }

            // Then - no step of the plan performs a filesort; the index drives the sort
            assertThat(extras)
                    .as("EXPLAIN Extra columns for the leaderboard query: %s", extras)
                    .isNotEmpty()
                    .noneMatch(extra -> extra.toLowerCase(Locale.ENGLISH).contains("filesort"));
        } finally {
            databaseManager.onDisable();
        }
    }

    /**
     * Gotcha coverage: a composite index that merely contains a skill column (e.g. an
     * admin-created {@code (user_id, mining)}) cannot serve the leaderboard ORDER BY, so its
     * presence must not stop the migration from creating the dedicated per-skill index.
     */
    @ParameterizedTest(name = "{0} - composite index containing a skill column does not suppress its leaderboard index")
    @MethodSource("dbFlavors")
    void whenCompositeIndexContainsSkillColumnShouldStillAddLeaderboardIndex(DbFlavor flavor)
            throws Exception {
        // Given - a schema without leaderboard indexes, but with a composite index in which
        // mining is a non-leading column
        truncateAllCoreTables(flavor);
        dropLeaderboardIndexes(flavor);

        final JdbcDatabaseContainer<?> container = containerFor(flavor);
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "ALTER TABLE mcmmo_skills ADD INDEX idx_composite_mining (user_id, mining)");
        }

        // And - the composite index alone does not count as a usable leaderboard index
        assertThat(leaderboardIndexExists(flavor, "mining"))
                .as("Precondition: composite index should not satisfy the leading-column probe")
                .isFalse();

        // And - only the leaderboard-index upgrade is flagged
        reset(upgradeManager);
        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);
        when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
        when(upgradeManager.shouldUpgrade(UpgradeType.ADD_SKILL_LEADERBOARD_INDEXES))
                .thenReturn(true);

        // When - a manager is constructed, running the ADD_SKILL_LEADERBOARD_INDEXES migration
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            // Then - a dedicated index with mining as its leading column now exists
            assertThat(leaderboardIndexExists(flavor, "mining"))
                    .as("Migration should add idx_mining despite the composite index")
                    .isTrue();
        } finally {
            try (Connection connection = DriverManager.getConnection(
                    container.getJdbcUrl(), container.getUsername(), container.getPassword());
                    Statement statement = connection.createStatement()) {
                statement.executeUpdate("ALTER TABLE mcmmo_skills DROP INDEX idx_composite_mining");
            }
            databaseManager.onDisable();
            when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
        }
    }

    /**
     * Gotcha coverage: an existing index that occupies the {@code idx_mining} name but leads with
     * a different column makes the ADD INDEX collide by name on every startup. The migration must
     * honor that index as deliberate and still mark itself complete, instead of failing on the
     * same column and re-running forever.
     */
    @ParameterizedTest(name = "{0} - index name taken by another column does not block migration completion")
    @MethodSource("dbFlavors")
    void whenIndexNameIsTakenByAnotherColumnShouldStillCompleteMigration(DbFlavor flavor)
            throws Exception {
        // Given - a schema without leaderboard indexes
        truncateAllCoreTables(flavor);
        dropLeaderboardIndexes(flavor);

        // And - an admin-created index that occupies the idx_mining name on the taming column
        final JdbcDatabaseContainer<?> container = containerFor(flavor);
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE mcmmo_skills ADD INDEX idx_mining (taming)");
        }

        // And - only the leaderboard-index upgrade is flagged
        reset(upgradeManager);
        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);
        when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
        when(upgradeManager.shouldUpgrade(UpgradeType.ADD_SKILL_LEADERBOARD_INDEXES))
                .thenReturn(true);

        // When - a manager is constructed, running the ADD_SKILL_LEADERBOARD_INDEXES migration
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            // Then - every other leaderboard column ends up with a usable leading-column index
            for (final String column : LEADERBOARD_INDEX_COLUMNS) {
                if (column.equals("mining")) {
                    continue;
                }
                assertThat(leaderboardIndexExists(flavor, column))
                        .as("Migration should index leaderboard column '%s'", column)
                        .isTrue();
            }

            // And - the idx_mining name stays on the admin's taming index; mining is left alone
            assertThat(leaderboardIndexExists(flavor, "mining"))
                    .as("The colliding idx_mining name should be honored, not replaced")
                    .isFalse();

            // And - the migration still completes so it does not retry on every startup
            verify(upgradeManager, atLeastOnce())
                    .setUpgradeCompleted(UpgradeType.ADD_SKILL_LEADERBOARD_INDEXES);
        } finally {
            databaseManager.onDisable();
            when(upgradeManager.shouldUpgrade(any(UpgradeType.class))).thenReturn(false);
            // Rebuild a pristine schema so later tests see the standard indexes
            dropAllCoreTables(flavor);
            createManagerFor(flavor).onDisable();
        }
    }

    /**
     * Servers whose skills table predates a skill receive the column through the legacy
     * ALTER TABLE upgrade path. That path must index the new column as it adds it, otherwise
     * leaderboards for every skill introduced after the one-shot index migration ran would
     * filesort forever.
     */
    @ParameterizedTest(name = "{0} - legacy column upgrade also indexes the new skill column")
    @MethodSource("dbFlavors")
    void whenLegacyUpgradeAddsSkillColumnShouldIndexIt(DbFlavor flavor) throws Exception {
        // Given - a legacy schema whose skills table lacks the spears column entirely
        prepareLegacySchemaWithoutSpears(flavor);

        // When - a manager is constructed, which adds missing skill columns on startup
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            // Then - the spears column exists on the skills table
            assertThat(columnExists(flavor, "mcmmo_skills", "spears"))
                    .as("Skills table should have spears after the upgrade")
                    .isTrue();

            // And - it carries a leaderboard index without waiting for a separate migration
            assertThat(leaderboardIndexExists(flavor, "spears"))
                    .as("The freshly added spears column should be indexed")
                    .isTrue();
        } finally {
            databaseManager.onDisable();
            // Rebuild a pristine schema so later tests see the standard indexes
            dropAllCoreTables(flavor);
            createManagerFor(flavor).onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - readRank for unknown user returns empty map")
    @MethodSource("dbFlavors")
    void whenReadingRankForUnknownUserShouldReturnEmptyMap(DbFlavor flavor) {
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            Map<PrimarySkillType, Integer> ranks = databaseManager.readRank("ghost_" + flavor.name().toLowerCase());

            assertThat(ranks)
                    .as("Unknown user should yield an empty rank map")
                    .isEmpty();
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - readRank for zero-skill user returns empty map")
    @MethodSource("dbFlavors")
    void whenReadingRankForZeroSkillUserShouldReturnEmptyMap(DbFlavor flavor) {
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String zeroName = "zeros_" + flavor.name().toLowerCase();
        UUID zeroUuid = UUID.randomUUID();

        try {
            // newUser -> all skills 0, total 0
            databaseManager.newUser(zeroName, zeroUuid);
            PlayerProfile zeroProfile = databaseManager.loadPlayerProfile(zeroUuid);
            assertThat(databaseManager.saveUser(zeroProfile)).isTrue();

            // Also create a powered user for sanity; zero user still should not be ranked
            String poweredName = "nonzero_" + flavor.name().toLowerCase();
            UUID poweredUuid = UUID.randomUUID();
            createUserWithUniformNonChildSkills(databaseManager, poweredName, poweredUuid, 100);

            Map<PrimarySkillType, Integer> ranks = databaseManager.readRank(zeroName);

            assertThat(ranks)
                    .as("User with all skills at 0 should not have any rank entries")
                    .isEmpty();
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - readRank with a single user → rank 1 for all non-child skills")
    @MethodSource("dbFlavors")
    void whenSingleUserShouldBeRankOneForAllNonChildSkills(DbFlavor flavor) {
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String soloName = "solo_" + flavor.name().toLowerCase();
        UUID soloUuid = UUID.randomUUID();

        try {
            // All non-child skills = 50
            createUserWithUniformNonChildSkills(databaseManager, soloName, soloUuid, 50);

            Map<PrimarySkillType, Integer> ranks = databaseManager.readRank(soloName);

            for (PrimarySkillType type : PrimarySkillType.values()) {
                if (SkillTools.isChildSkill(type)) {
                    assertThat(ranks.get(type))
                            .as("Child skill %s should have no rank", type)
                            .isNull();
                } else {
                    assertThat(ranks.get(type))
                            .as("Solo player should be rank 1 for skill %s", type)
                            .isEqualTo(1);
                }
            }

            // Total rank (null key) should also be 1
            assertThat(ranks.get(null))
                    .as("Solo player total rank should be 1")
                    .isEqualTo(1);
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - readRank newest-registered-first tiebreaker with only equal-skill users")
    @MethodSource("dbFlavors")
    void whenEqualSkillUsersOnlyShouldRankNewestRegisteredFirst(DbFlavor flavor) {
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String nameA = "aaa_" + flavor.name().toLowerCase();
        String nameB = "bbb_" + flavor.name().toLowerCase();
        String nameC = "ccc_" + flavor.name().toLowerCase();

        UUID uuidA = UUID.randomUUID();
        UUID uuidB = UUID.randomUUID();
        UUID uuidC = UUID.randomUUID();

        try {
            // For simplicity, set only MINING and let total = mining
            Map<PrimarySkillType, Integer> skillMap = Map.of(PrimarySkillType.MINING, 100);

            createUserWithSkills(databaseManager, nameA, uuidA, skillMap);
            createUserWithSkills(databaseManager, nameB, uuidB, skillMap);
            createUserWithSkills(databaseManager, nameC, uuidC, skillMap);

            Map<PrimarySkillType, Integer> ranksA = databaseManager.readRank(nameA);
            Map<PrimarySkillType, Integer> ranksB = databaseManager.readRank(nameB);
            Map<PrimarySkillType, Integer> ranksC = databaseManager.readRank(nameC);

            // Mining ranks: ties are broken by registration order, newest first, matching the
            // leaderboard's ORDER BY user_id DESC (A was created first -> highest rank number)
            assertThat(ranksC.get(PrimarySkillType.MINING)).isEqualTo(1);
            assertThat(ranksB.get(PrimarySkillType.MINING)).isEqualTo(2);
            assertThat(ranksA.get(PrimarySkillType.MINING)).isEqualTo(3);

            // Total ranks behave the same in this setup (total == mining)
            assertThat(ranksC.get(null)).isEqualTo(1);
            assertThat(ranksB.get(null)).isEqualTo(2);
            assertThat(ranksA.get(null)).isEqualTo(3);
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - readRank tie group is offset by number of higher players")
    @MethodSource("dbFlavors")
    void whenEqualSkillUsersHaveHigherPlayerShouldOffsetByHigherCount(DbFlavor flavor) {
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String higherName = "zoe_" + flavor.name().toLowerCase();
        String nameA = "aaa2_" + flavor.name().toLowerCase();
        String nameB = "bbb2_" + flavor.name().toLowerCase();
        String nameC = "ccc2_" + flavor.name().toLowerCase();

        UUID uuidHigher = UUID.randomUUID();
        UUID uuidA = UUID.randomUUID();
        UUID uuidB = UUID.randomUUID();
        UUID uuidC = UUID.randomUUID();

        try {
            // Higher player
            createUserWithSkills(
                    databaseManager,
                    higherName,
                    uuidHigher,
                    Map.of(PrimarySkillType.MINING, 200)
            );

            // Tie group
            Map<PrimarySkillType, Integer> tieSkills = Map.of(PrimarySkillType.MINING, 100);
            createUserWithSkills(databaseManager, nameA, uuidA, tieSkills);
            createUserWithSkills(databaseManager, nameB, uuidB, tieSkills);
            createUserWithSkills(databaseManager, nameC, uuidC, tieSkills);

            Map<PrimarySkillType, Integer> higherRanks = databaseManager.readRank(higherName);
            Map<PrimarySkillType, Integer> ranksA = databaseManager.readRank(nameA);
            Map<PrimarySkillType, Integer> ranksB = databaseManager.readRank(nameB);
            Map<PrimarySkillType, Integer> ranksC = databaseManager.readRank(nameC);

            // Higher player is rank 1
            assertThat(higherRanks.get(PrimarySkillType.MINING)).isEqualTo(1);

            // Tied players follow newest-registered-first, offset by the higher player
            assertThat(ranksC.get(PrimarySkillType.MINING)).isEqualTo(2);
            assertThat(ranksB.get(PrimarySkillType.MINING)).isEqualTo(3);
            assertThat(ranksA.get(PrimarySkillType.MINING)).isEqualTo(4);
        } finally {
            databaseManager.onDisable();
        }
    }

    @ParameterizedTest(name = "{0} - readRank per-skill vs total ranking can differ")
    @MethodSource("dbFlavors")
    void whenDifferentSkillDistributionsShouldComputePerSkillAndTotalRanksSeparately(DbFlavor flavor) {
        truncateAllCoreTables(flavor);
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        String alphaName = "alpha_" + flavor.name().toLowerCase();
        String bravoName = "bravo_" + flavor.name().toLowerCase();
        String charlieName = "charlie_" + flavor.name().toLowerCase();

        UUID alphaUuid = UUID.randomUUID();
        UUID bravoUuid = UUID.randomUUID();
        UUID charlieUuid = UUID.randomUUID();

        try {
            // alpha: mining 100, fishing 0
            createUserWithSkills(
                    databaseManager,
                    alphaName,
                    alphaUuid,
                    Map.of(PrimarySkillType.MINING, 100)
            );

            // bravo: mining 50, fishing 200
            createUserWithSkills(
                    databaseManager,
                    bravoName,
                    bravoUuid,
                    Map.of(
                            PrimarySkillType.MINING, 50,
                            PrimarySkillType.FISHING, 200
                    )
            );

            // charlie: mining 75, fishing 50
            createUserWithSkills(
                    databaseManager,
                    charlieName,
                    charlieUuid,
                    Map.of(
                            PrimarySkillType.MINING, 75,
                            PrimarySkillType.FISHING, 50
                    )
            );

            Map<PrimarySkillType, Integer> alphaRanks = databaseManager.readRank(alphaName);
            Map<PrimarySkillType, Integer> bravoRanks = databaseManager.readRank(bravoName);
            Map<PrimarySkillType, Integer> charlieRanks = databaseManager.readRank(charlieName);

            // --- Mining (100 > 75 > 50) ---
            assertThat(alphaRanks.get(PrimarySkillType.MINING)).isEqualTo(1);
            assertThat(charlieRanks.get(PrimarySkillType.MINING)).isEqualTo(2);
            assertThat(bravoRanks.get(PrimarySkillType.MINING)).isEqualTo(3);

            // --- Fishing (200 > 50 > 0) ---
            // alpha has 0 -> no rank entry for fishing
            assertThat(alphaRanks.get(PrimarySkillType.FISHING)).isNull();

            assertThat(bravoRanks.get(PrimarySkillType.FISHING)).isEqualTo(1);
            assertThat(charlieRanks.get(PrimarySkillType.FISHING)).isEqualTo(2);

            // --- Total: alpha 100, bravo 250, charlie 125 ---
            assertThat(bravoRanks.get(null)).isEqualTo(1);   // 250 highest
            assertThat(charlieRanks.get(null)).isEqualTo(2); // 125
            assertThat(alphaRanks.get(null)).isEqualTo(3);   // 100
        } finally {
            databaseManager.onDisable();
        }
    }

    /**
     * Rows renamed to the {@code _INVALID_OLD_USERNAME_} placeholder are hidden from the
     * leaderboard, so they must not count toward ranks either - one ghost with a higher level
     * and one tied ghost with a later registration would otherwise push the real player from
     * rank 1 to rank 3 while /mctop still shows them on top.
     */
    @ParameterizedTest(name = "{0} - readRank ignores _INVALID_OLD_USERNAME_ ghost rows")
    @MethodSource("dbFlavors")
    void whenGhostRowsOutrankPlayerShouldRankLikeLeaderboard(DbFlavor flavor) throws Exception {
        // Given - a clean database
        truncateAllCoreTables(flavor);
        final SQLDatabaseManager databaseManager = createManagerFor(flavor);

        final String realName = "rank_real_" + flavor.name().toLowerCase();
        final String ghostHigherName = "rank_ghost_high_" + flavor.name().toLowerCase();
        final String ghostTiedName = "rank_ghost_tie_" + flavor.name().toLowerCase();

        try {
            // And - a real player, a ghost with a strictly higher level, and a ghost tied with
            //       the real player but registered later (winning the user_id tiebreak)
            createUserWithSkills(databaseManager, realName, UUID.randomUUID(),
                    Map.of(PrimarySkillType.MINING, 100));
            createUserWithSkills(databaseManager, ghostHigherName, UUID.randomUUID(),
                    Map.of(PrimarySkillType.MINING, 200));
            createUserWithSkills(databaseManager, ghostTiedName, UUID.randomUUID(),
                    Map.of(PrimarySkillType.MINING, 100));
            renameToInvalidOldUsername(flavor, ghostHigherName);
            renameToInvalidOldUsername(flavor, ghostTiedName);

            // When - reading both the rank and the leaderboard for the same skill
            final Map<PrimarySkillType, Integer> ranks = databaseManager.readRank(realName);
            final List<PlayerStat> leaderboard =
                    databaseManager.readLeaderboard(PrimarySkillType.MINING, 1, 10);

            // Then - the leaderboard hides the ghosts and shows only the real player
            assertThat(leaderboard)
                    .extracting(PlayerStat::playerName)
                    .containsExactly(realName);

            // And - the rank matches the player's leaderboard position exactly
            final int leaderboardPosition = 1 + leaderboard.stream()
                    .map(PlayerStat::playerName)
                    .toList()
                    .indexOf(realName);
            assertThat(ranks.get(PrimarySkillType.MINING))
                    .as("/mcrank position should match /mctop position")
                    .isEqualTo(leaderboardPosition)
                    .isEqualTo(1);

            // And - the total scope ignores the ghost rows the same way
            assertThat(ranks.get(null))
                    .as("Total rank should also ignore ghost rows")
                    .isEqualTo(1);
        } finally {
            databaseManager.onDisable();
        }
    }

    // ------------------------------------------------------------------------
    // getDatabaseType
    // ------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - getDatabaseType")
    @MethodSource("dbFlavors")
    void whenGettingDatabaseTypeShouldReturnSql(DbFlavor flavor) {
        // GIVEN a database manager
        SQLDatabaseManager databaseManager = createManagerFor(flavor);

        try {
            // WHEN retrieving the database type
            DatabaseType databaseType = databaseManager.getDatabaseType();

            // THEN it should be SQL
            assertThat(databaseType).isEqualTo(DatabaseType.SQL);
        } finally {
            databaseManager.onDisable();
        }
    }

    // --------------------------------------------------------------------------
    // Convert Users Tests
    // --------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} - convertUsers migrates all stored users")
    @MethodSource("dbFlavors")
    void whenConvertingUsersShouldSaveEachStoredUserToDestination(DbFlavor flavor) {
        // GIVEN
        truncateAllCoreTables(flavor);
        SQLDatabaseManager sourceManager = createManagerFor(flavor);

        String userA = "convert_user_a_" + flavor.name().toLowerCase();
        String userB = "convert_user_b_" + flavor.name().toLowerCase();
        sourceManager.newUser(userA, new UUID(1L, 2L));
        sourceManager.newUser(userB, new UUID(3L, 4L));

        DatabaseManager destination = mock(DatabaseManager.class);
        when(destination.saveUser(any(PlayerProfile.class))).thenReturn(true);

        // WHEN
        sourceManager.convertUsers(destination);

        // THEN – destination.saveUser(...) called once per stored user
        ArgumentCaptor<PlayerProfile> profileCaptor = ArgumentCaptor.forClass(PlayerProfile.class);
        verify(destination, times(2)).saveUser(profileCaptor.capture());

        assertThat(profileCaptor.getAllValues())
                .extracting(PlayerProfile::getPlayerName)
                .containsExactlyInAnyOrder(userA, userB);

        sourceManager.onDisable();
    }

    @ParameterizedTest(name = "{0} - convertUsers on empty database does nothing")
    @MethodSource("dbFlavors")
    void whenConvertingUsersWithNoStoredUsersShouldNotCallDestination(DbFlavor flavor) {
        // GIVEN
        truncateAllCoreTables(flavor);
        SQLDatabaseManager sourceManager = createManagerFor(flavor);

        DatabaseManager destination = mock(DatabaseManager.class);
        when(destination.saveUser(any(PlayerProfile.class))).thenReturn(true);

        // WHEN
        sourceManager.convertUsers(destination);

        // THEN
        verify(destination, times(0)).saveUser(any(PlayerProfile.class));

        sourceManager.onDisable();
    }


    // ------------------------------------------------------------------------
    // Helpers for legacy schema tests
    // ------------------------------------------------------------------------

    /**
     * Simulate an "old" schema where the spears columns do not exist yet.
     * We drop any existing mcMMO tables and recreate them without spears.
     */
    private void prepareLegacySchemaWithoutSpears(DbFlavor flavor) throws SQLException {
        JdbcDatabaseContainer<?> container = containerFor(flavor);

        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
                Statement statement = connection.createStatement()) {

            // Clean slate
            statement.executeUpdate("DROP TABLE IF EXISTS mcmmo_cooldowns");
            statement.executeUpdate("DROP TABLE IF EXISTS mcmmo_experience");
            statement.executeUpdate("DROP TABLE IF EXISTS mcmmo_skills");
            statement.executeUpdate("DROP TABLE IF EXISTS mcmmo_huds");
            statement.executeUpdate("DROP TABLE IF EXISTS mcmmo_users");

            // Minimal users table
            statement.executeUpdate(
                    "CREATE TABLE mcmmo_users (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "user VARCHAR(40) NOT NULL," +
                            "uuid VARCHAR(36)," +
                            "lastlogin BIGINT NOT NULL" +
                            ")"
            );

            // Minimal huds table
            statement.executeUpdate(
                    "CREATE TABLE mcmmo_huds (" +
                            "user_id INT(10) UNSIGNED NOT NULL," +
                            "mobhealthbar VARCHAR(50) NOT NULL DEFAULT 'HEARTS'," +
                            "scoreboardtips INT(10) NOT NULL DEFAULT 0," +
                            "PRIMARY KEY (user_id)" +
                            ")"
            );

            // LEGACY skills table: everything up to maces, BUT NO spears
            statement.executeUpdate(
                    "CREATE TABLE mcmmo_skills (" +
                            "user_id INT(10) UNSIGNED NOT NULL," +
                            "taming INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "mining INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "woodcutting INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "repair INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "unarmed INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "herbalism INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "excavation INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "archery INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "swords INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "axes INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "acrobatics INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "fishing INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "alchemy INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "crossbows INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "tridents INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "maces INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "total INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "PRIMARY KEY (user_id)" +
                            ")"
            );

            // LEGACY experience table: everything up to maces, BUT NO spears
            statement.executeUpdate(
                    "CREATE TABLE mcmmo_experience (" +
                            "user_id INT(10) UNSIGNED NOT NULL," +
                            "taming INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "mining INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "woodcutting INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "repair INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "unarmed INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "herbalism INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "excavation INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "archery INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "swords INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "axes INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "acrobatics INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "fishing INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "alchemy INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "crossbows INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "tridents INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "maces INT(10) UNSIGNED NOT NULL DEFAULT 0," +
                            "PRIMARY KEY (user_id)" +
                            ")"
            );

            // LEGACY cooldowns table: everything up to maces, BUT NO spears
            statement.executeUpdate(
                    "CREATE TABLE mcmmo_cooldowns (" +
                            "user_id INT(10) UNSIGNED NOT NULL," +
                            "taming INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "mining INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "woodcutting INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "repair INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "unarmed INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "herbalism INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "excavation INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "archery INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "swords INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "axes INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "acrobatics INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "blast_mining INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "chimaera_wing INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "crossbows INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "tridents INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "maces INT(32) UNSIGNED NOT NULL DEFAULT 0," +
                            "PRIMARY KEY (user_id)" +
                            ")"
            );
        }
    }

    private boolean columnExists(DbFlavor flavor, String tableName, String columnName)
            throws SQLException {
        JdbcDatabaseContainer<?> container = containerFor(flavor);
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
                ResultSet resultSet = connection.getMetaData().getColumns(null, null, tableName, columnName)) {
            return resultSet.next();
        }
    }

    /**
     * @return {@code true} if any index exists with the given column as its leading column on the
     * {@code mcmmo_skills} table. Mirrors the INFORMATION_SCHEMA probe used by the production
     * migration: only a leading column can serve the leaderboard ORDER BY.
     */
    private boolean leaderboardIndexExists(DbFlavor flavor, String column) throws SQLException {
        final JdbcDatabaseContainer<?> container = containerFor(flavor);
        final String query = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS "
                + "WHERE table_schema = DATABASE() "
                + "AND table_name = 'mcmmo_skills' "
                + "AND column_name = '" + column + "' "
                + "AND seq_in_index = 1";

        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    /**
     * Drop every leaderboard index from {@code mcmmo_skills}, simulating a server that upgraded
     * from a version predating per-skill indexes. Ignores indexes that are already absent.
     */
    private void dropLeaderboardIndexes(DbFlavor flavor) throws SQLException {
        final JdbcDatabaseContainer<?> container = containerFor(flavor);
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
                Statement statement = connection.createStatement()) {
            for (final String column : LEADERBOARD_INDEX_COLUMNS) {
                if (leaderboardIndexExists(flavor, column)) {
                    statement.executeUpdate("ALTER TABLE mcmmo_skills DROP INDEX idx_" + column);
                }
            }
        }
    }

    /**
     * Drop all core mcMMO tables so the next manager construction performs a true fresh install.
     */
    private void dropAllCoreTables(DbFlavor flavor) throws SQLException {
        final JdbcDatabaseContainer<?> container = containerFor(flavor);
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS mcmmo_cooldowns");
            statement.executeUpdate("DROP TABLE IF EXISTS mcmmo_experience");
            statement.executeUpdate("DROP TABLE IF EXISTS mcmmo_skills");
            statement.executeUpdate("DROP TABLE IF EXISTS mcmmo_huds");
            statement.executeUpdate("DROP TABLE IF EXISTS mcmmo_users");
        }
    }

    // ------------------------------------------------------------------------
    // Helpers for readRank tests
    // ------------------------------------------------------------------------

    private void createUserWithUniformNonChildSkills(SQLDatabaseManager manager,
            String name,
            UUID uuid,
            int level) {
        manager.newUser(name, uuid);
        PlayerProfile profile = manager.loadPlayerProfile(uuid);
        for (PrimarySkillType type : PrimarySkillType.values()) {
            if (SkillTools.isChildSkill(type)) {
                continue;
            }
            profile.modifySkill(type, level);
        }
        assertThat(manager.saveUser(profile)).isTrue();
    }

    /**
     * Renames a user row to the {@code _INVALID_OLD_USERNAME_} placeholder the same way the
     * production rename path does (an UPDATE of the users row's name), turning it into a ghost
     * row that keeps its skills but must be hidden from leaderboards and ranks.
     */
    private void renameToInvalidOldUsername(DbFlavor flavor, String userName) throws SQLException {
        renameStoredUser(flavor, userName, UsernamePlaceholder.INVALID_OLD_USERNAME);
    }

    /**
     * Renames one users row directly, bypassing the manager: to a spelling of the placeholder,
     * or to a name another row already holds, the way earlier versions could leave it.
     */
    private void renameStoredUser(DbFlavor flavor, String userName, String newName)
            throws SQLException {
        final JdbcDatabaseContainer<?> container = containerFor(flavor);
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE mcmmo_users SET `user` = ? WHERE `user` = ?")) {
            statement.setString(1, newName);
            statement.setString(2, userName);
            assertThat(statement.executeUpdate())
                    .as("Exactly one users row should be renamed")
                    .isEqualTo(1);
        }
    }

    /** A player who is online, the only one whose name replaces the stored one. */
    private static Player onlinePlayer(String name, UUID uuid) {
        final Player player = Mockito.mock(Player.class);
        when(player.getName()).thenReturn(name);
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.isOnline()).thenReturn(true);
        return player;
    }

    private void createUserWithSkills(SQLDatabaseManager manager,
            String name,
            UUID uuid,
            Map<PrimarySkillType, Integer> levels) {
        manager.newUser(name, uuid);
        PlayerProfile profile = manager.loadPlayerProfile(uuid);
        for (Map.Entry<PrimarySkillType, Integer> e : levels.entrySet()) {
            // modifySkill adds; starting level is 0 in tests
            profile.modifySkill(e.getKey(), e.getValue());
        }
        assertThat(manager.saveUser(profile)).isTrue();
    }

    /**
     * Players who lost their name to someone else hold the placeholder instead, under the SQL
     * spelling or the older FlatFile one that converted databases carry. It is nobody's name,
     * so a lookup by it must find no one: removeUser by it would delete all of them.
     */
    @Nested
    class PlaceholderNames {
        private static final int NAME_LOST_MINING_LEVEL = 200;
        private static final int NAME_LOST_BEFORE_THE_SPELLING_CHANGED_MINING_LEVEL = 300;
        private static final int NAME_LOST_WITHOUT_A_UUID_MINING_LEVEL = 400;
        private static final int ALSO_LOST_MINING_LEVEL = 250;
        private static final int SAVED_MINING_LEVEL = 500;
        private static final int LEVELS_GAINED = 25;
        private static final String LEGACY_PLAYER_NAME = "legacy_player";

        private final UUID nameLostUuid = UUID.randomUUID();
        private final UUID nameLostBeforeTheSpellingChangedUuid = UUID.randomUUID();

        /** A stored player as the database holds them, read without going through a manager. */
        private record StoredRow(String name, @Nullable String uuid, int miningLevel) {
        }

        static Stream<Arguments> flavorsAndPlaceholderSpellings() {
            return dbFlavors().flatMap(flavor -> UsernamePlaceholderTest.placeholderSpellings()
                    .map(placeholder -> Arguments.of(flavor, placeholder)));
        }

        /** Stores one player who lost their name under each spelling of the placeholder. */
        private SQLDatabaseManager databaseWithPlayersWhoLostTheirNames(DbFlavor flavor)
                throws SQLException {
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager databaseManager = createManagerFor(flavor);
            final String flavorSuffix = flavor.name().toLowerCase(Locale.ROOT);
            createUserWithSkills(databaseManager, "name_lost_" + flavorSuffix, nameLostUuid,
                    Map.of(PrimarySkillType.MINING, NAME_LOST_MINING_LEVEL));
            createUserWithSkills(databaseManager, "name_lost_early_" + flavorSuffix,
                    nameLostBeforeTheSpellingChangedUuid, Map.of(PrimarySkillType.MINING,
                            NAME_LOST_BEFORE_THE_SPELLING_CHANGED_MINING_LEVEL));
            renameStoredUser(flavor, "name_lost_" + flavorSuffix,
                    UsernamePlaceholder.INVALID_OLD_USERNAME);
            renameStoredUser(flavor, "name_lost_early_" + flavorSuffix,
                    UsernamePlaceholder.LEGACY_FLATFILE_INVALID_OLD_USERNAME);
            return databaseManager;
        }

        /** Stores a player from before mcMMO kept UUIDs, who then lost their name. */
        private void storePlayerWhoLostTheirNameWithoutAUuid(DbFlavor flavor,
                SQLDatabaseManager databaseManager) throws SQLException {
            final String playerName = "no_uuid_" + flavor.name().toLowerCase(Locale.ROOT);
            storePlayerWithoutAUuid(flavor, databaseManager, playerName,
                    NAME_LOST_WITHOUT_A_UUID_MINING_LEVEL);
            renameStoredUser(flavor, playerName, UsernamePlaceholder.INVALID_OLD_USERNAME);
        }

        /**
         * Stores a player the way mcMMO did before it kept UUIDs. mcMMO no longer adds or saves a
         * player without a UUID, so the row is stored with one and the column cleared after.
         */
        private void storePlayerWithoutAUuid(DbFlavor flavor, SQLDatabaseManager databaseManager,
                String playerName, int miningLevel) throws SQLException {
            final UUID uuidToClear = UUID.randomUUID();
            createUserWithSkills(databaseManager, playerName, uuidToClear,
                    Map.of(PrimarySkillType.MINING, miningLevel));
            setStoredUuid(flavor, playerName, null);
            databaseManager.cleanupUser(uuidToClear);
        }

        /** Stores users rows alone, without skills, and refreshes the table's statistics. */
        private void storeNamesWithoutAUuid(DbFlavor flavor, int count) throws SQLException {
            final JdbcDatabaseContainer<?> container = containerFor(flavor);
            try (Connection connection = DriverManager.getConnection(
                    container.getJdbcUrl(), container.getUsername(), container.getPassword())) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO mcmmo_users (`user`, uuid, lastlogin) VALUES (?, NULL, 0)")) {
                    for (int index = 0; index < count; index++) {
                        statement.setString(1, "no_uuid_" + index);
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }
                try (Statement statement = connection.createStatement()) {
                    statement.execute("ANALYZE TABLE mcmmo_users");
                }
            }
        }

        private void setLastLogin(DbFlavor flavor, UUID uuid, long lastLoginSeconds)
                throws SQLException {
            final JdbcDatabaseContainer<?> container = containerFor(flavor);
            try (Connection connection = DriverManager.getConnection(
                    container.getJdbcUrl(), container.getUsername(), container.getPassword());
                    PreparedStatement statement = connection.prepareStatement(
                            "UPDATE mcmmo_users SET lastlogin = ? WHERE uuid = ?")) {
                statement.setLong(1, lastLoginSeconds);
                statement.setString(2, uuid.toString());
                assertThat(statement.executeUpdate()).isEqualTo(1);
            }
        }

        /** Loads a player under a name the way everything but their login does. */
        @FunctionalInterface
        private interface LoadWhileNotOnline {
            PlayerProfile load(SQLDatabaseManager databaseManager, UUID uuid, String playerName);
        }

        static Stream<Arguments> flavorsAndLoadsWhileNotOnline() {
            final Map<String, LoadWhileNotOnline> loads = Map.of(
                    "an offline player", (databaseManager, uuid, playerName) -> {
                        final OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
                        when(offlinePlayer.getName()).thenReturn(playerName);
                        when(offlinePlayer.getUniqueId()).thenReturn(uuid);
                        return databaseManager.loadPlayerProfile(offlinePlayer);
                    },
                    "a player who logged out", (databaseManager, uuid, playerName) -> {
                        final Player playerWhoLoggedOut = onlinePlayer(playerName, uuid);
                        when(playerWhoLoggedOut.isOnline()).thenReturn(false);
                        return databaseManager.loadPlayerProfile(playerWhoLoggedOut);
                    },
                    "a UUID and a name", SQLDatabaseManager::loadPlayerProfile);
            return dbFlavors().flatMap(flavor -> loads.entrySet().stream()
                    .map(load -> Arguments.of(flavor, load.getKey(), load.getValue())));
        }

        private StoredRow nameLostRow() {
            return new StoredRow(UsernamePlaceholder.INVALID_OLD_USERNAME, nameLostUuid.toString(),
                    NAME_LOST_MINING_LEVEL);
        }

        private StoredRow nameLostBeforeTheSpellingChangedRow() {
            return new StoredRow(UsernamePlaceholder.LEGACY_FLATFILE_INVALID_OLD_USERNAME,
                    nameLostBeforeTheSpellingChangedUuid.toString(),
                    NAME_LOST_BEFORE_THE_SPELLING_CHANGED_MINING_LEVEL);
        }

        private StoredRow nameLostWithoutAUuidRow() {
            return new StoredRow(UsernamePlaceholder.INVALID_OLD_USERNAME, null,
                    NAME_LOST_WITHOUT_A_UUID_MINING_LEVEL);
        }

        private void assertPlayersWhoLostTheirNamesAreUntouched(DbFlavor flavor)
                throws SQLException {
            assertThat(rowsUnderThePlaceholder(flavor)).containsExactlyInAnyOrder(nameLostRow(),
                    nameLostBeforeTheSpellingChangedRow());
        }

        private List<StoredRow> rowsUnderThePlaceholder(DbFlavor flavor) throws SQLException {
            return storedRows(flavor, " WHERE u.`user` IN (?, ?)",
                    UsernamePlaceholder.INVALID_OLD_USERNAME,
                    UsernamePlaceholder.LEGACY_FLATFILE_INVALID_OLD_USERNAME);
        }

        private List<StoredRow> allStoredRows(DbFlavor flavor) throws SQLException {
            return storedRows(flavor, "");
        }

        /**
         * Reads the tables directly: the manager caches row ids by UUID, so loading by UUID still
         * finds a row whose uuid was overwritten.
         */
        private List<StoredRow> storedRows(DbFlavor flavor, String whereClause,
                String... parameters) throws SQLException {
            final JdbcDatabaseContainer<?> container = containerFor(flavor);
            final List<StoredRow> rows = new ArrayList<>();
            try (Connection connection = DriverManager.getConnection(
                    container.getJdbcUrl(), container.getUsername(), container.getPassword());
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT u.`user`, u.uuid, s.mining FROM mcmmo_users u"
                                    + " JOIN mcmmo_skills s ON s.user_id = u.id" + whereClause)) {
                for (int index = 0; index < parameters.length; index++) {
                    statement.setString(index + 1, parameters[index]);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        rows.add(new StoredRow(resultSet.getString(1), resultSet.getString(2),
                                resultSet.getInt(3)));
                    }
                }
            }
            return rows;
        }

        private static List<StoredRow> flatFileRows(FlatFileDatabaseManager databaseManager)
                throws IOException {
            return java.nio.file.Files.readAllLines(databaseManager.getUsersFile().toPath())
                    .stream()
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.split(":"))
                    .map(fields -> new StoredRow(fields[FlatFileDatabaseManager.USERNAME_INDEX],
                            fields[FlatFileDatabaseManager.UUID_INDEX],
                            Integer.parseInt(fields[FlatFileDatabaseManager.SKILLS_MINING])))
                    .toList();
        }

        private static PlayerProfile profileFor(StoredRow storedRow) {
            final PlayerProfile profile = new PlayerProfile(storedRow.name(),
                    storedRow.uuid() == null ? null : UUID.fromString(storedRow.uuid()), true, 0);
            profile.modifySkill(PrimarySkillType.MINING, storedRow.miningLevel());
            return profile;
        }

        @ParameterizedTest(name = "{0} - looked up as {1}")
        @MethodSource("flavorsAndPlaceholderSpellings")
        void loadingByThePlaceholderShouldFindNoOne(DbFlavor flavor, String placeholder)
                throws SQLException {
            // Given - players who lost their names, under both spellings
            final SQLDatabaseManager databaseManager = databaseWithPlayersWhoLostTheirNames(
                    flavor);

            try {
                // When - a profile is loaded by the placeholder
                final PlayerProfile profile = databaseManager.loadPlayerProfile(placeholder);

                // Then - no one is found
                assertThat(profile.isLoaded()).isFalse();
            } finally {
                databaseManager.onDisable();
            }
        }

        @ParameterizedTest(name = "{0} - removed as {1}")
        @MethodSource("flavorsAndPlaceholderSpellings")
        void removingByThePlaceholderShouldRemoveNoOne(DbFlavor flavor, String placeholder)
                throws SQLException {
            // Given - players who lost their names, under both spellings
            final SQLDatabaseManager databaseManager = databaseWithPlayersWhoLostTheirNames(
                    flavor);

            try {
                // When - a player is removed by the placeholder
                final boolean removed = databaseManager.removeUser(placeholder, null);

                // Then - no one is removed
                assertThat(removed).isFalse();
                assertPlayersWhoLostTheirNamesAreUntouched(flavor);
            } finally {
                databaseManager.onDisable();
            }
        }

        @ParameterizedTest(name = "{0} - ranked as {1}")
        @MethodSource("flavorsAndPlaceholderSpellings")
        void rankingThePlaceholderShouldRankNoOne(DbFlavor flavor, String placeholder)
                throws SQLException {
            // Given - players who lost their names, under both spellings
            final SQLDatabaseManager databaseManager = databaseWithPlayersWhoLostTheirNames(
                    flavor);

            try {
                // When - ranks are read for the placeholder
                final Map<PrimarySkillType, Integer> ranks =
                        databaseManager.readRank(placeholder);

                // Then - there are none
                assertThat(ranks).isEmpty();
            } finally {
                databaseManager.onDisable();
            }
        }

        @ParameterizedTest(name = "{0} - saved as {1}")
        @MethodSource("flavorsAndPlaceholderSpellings")
        void savingAUuidForThePlaceholderShouldChangeNoRow(DbFlavor flavor, String placeholder)
                throws SQLException {
            // Given - players who lost their names, under both spellings
            final SQLDatabaseManager databaseManager = databaseWithPlayersWhoLostTheirNames(
                    flavor);
            final UUID fetchedUuid = UUID.randomUUID();

            try {
                // When - a UUID is saved for the placeholder
                final boolean saved = databaseManager.saveUserUUID(placeholder, fetchedUuid);

                // Then - no row takes it
                assertThat(saved).isFalse();
                assertThat(databaseManager.loadPlayerProfile(fetchedUuid).isLoaded()).isFalse();
                assertPlayersWhoLostTheirNamesAreUntouched(flavor);
            } finally {
                databaseManager.onDisable();
            }
        }

        @ParameterizedTest(name = "{0} - saved as {1}")
        @MethodSource("flavorsAndPlaceholderSpellings")
        void savingUuidsInBulkShouldSkipThePlaceholder(DbFlavor flavor, String placeholder)
                throws SQLException {
            // Given - players who lost their names, and a player with a name
            final SQLDatabaseManager databaseManager = databaseWithPlayersWhoLostTheirNames(
                    flavor);
            final String playerWithAName = "has_a_name_" + flavor.name().toLowerCase(Locale.ROOT);
            databaseManager.newUser(playerWithAName, UUID.randomUUID());
            final UUID fetchedUuid = UUID.randomUUID();

            try {
                // When - UUIDs are saved for the placeholder and the player with a name
                final boolean saved = databaseManager.saveUserUUIDs(new HashMap<>(Map.of(
                        placeholder, UUID.randomUUID(), playerWithAName, fetchedUuid)));

                // Then - only the player with a name gets theirs
                assertThat(saved).isTrue();
                assertThat(databaseManager.loadPlayerProfile(fetchedUuid).getPlayerName())
                        .isEqualTo(playerWithAName);
                assertPlayersWhoLostTheirNamesAreUntouched(flavor);
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * A name belongs to one player at a time. A returning player who logs in under a name
         * other stored players hold takes it, and they keep their progress under the
         * placeholder. Names are matched ignoring case, as Minecraft does.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void returningPlayerTakingANameShouldMoveItsHoldersToThePlaceholder(DbFlavor flavor)
                throws SQLException {
            // Given - two stored players holding a name, one in other capitals, as earlier
            // versions could leave them
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager databaseManager = createManagerFor(flavor);
            final String flavorSuffix = flavor.name().toLowerCase(Locale.ROOT);
            final String takenName = "taken_" + flavorSuffix;
            final UUID holderUuid = UUID.randomUUID();
            final UUID otherHolderUuid = UUID.randomUUID();
            createUserWithSkills(databaseManager, takenName, holderUuid,
                    Map.of(PrimarySkillType.MINING, NAME_LOST_MINING_LEVEL));
            createUserWithSkills(databaseManager, "other_holder_" + flavorSuffix,
                    otherHolderUuid, Map.of(PrimarySkillType.MINING, ALSO_LOST_MINING_LEVEL));
            renameStoredUser(flavor, "other_holder_" + flavorSuffix,
                    takenName.toUpperCase(Locale.ROOT));

            // And - a returning player stored under another name
            final UUID returningPlayerUuid = UUID.randomUUID();
            createUserWithSkills(databaseManager, "returning_" + flavorSuffix,
                    returningPlayerUuid, Map.of(PrimarySkillType.MINING, SAVED_MINING_LEVEL));

            try {
                // When - the returning player logs in under the taken name
                databaseManager.loadPlayerProfile(onlinePlayer(takenName, returningPlayerUuid));

                // Then - the name finds them
                final PlayerProfile foundByName = databaseManager.loadPlayerProfile(takenName);
                assertThat(foundByName.getUniqueId()).isEqualTo(returningPlayerUuid);
                assertThat(foundByName.getSkillLevel(PrimarySkillType.MINING))
                        .isEqualTo(SAVED_MINING_LEVEL);

                // And - the players who held it keep their progress under the placeholder
                assertThat(rowsUnderThePlaceholder(flavor)).containsExactlyInAnyOrder(
                        new StoredRow(UsernamePlaceholder.INVALID_OLD_USERNAME,
                                holderUuid.toString(), NAME_LOST_MINING_LEVEL),
                        new StoredRow(UsernamePlaceholder.INVALID_OLD_USERNAME,
                                otherHolderUuid.toString(), ALSO_LOST_MINING_LEVEL));
            } finally {
                databaseManager.onDisable();
            }
        }

        /** A new player takes a name from whoever is stored under it, as a returning one does. */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void newPlayerTakingANameShouldMoveItsHolderToThePlaceholder(DbFlavor flavor)
                throws SQLException {
            // Given - a stored player holding a name
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager databaseManager = createManagerFor(flavor);
            final String takenName = "taken_" + flavor.name().toLowerCase(Locale.ROOT);
            final UUID holderUuid = UUID.randomUUID();
            createUserWithSkills(databaseManager, takenName, holderUuid,
                    Map.of(PrimarySkillType.MINING, NAME_LOST_MINING_LEVEL));
            final UUID newPlayerUuid = UUID.randomUUID();

            try {
                // When - a new player joins under the name in other capitals
                databaseManager.newUser(takenName.toUpperCase(Locale.ROOT), newPlayerUuid);

                // Then - the name finds the new player, and the holder keeps their progress
                // under the placeholder
                assertThat(databaseManager.loadPlayerProfile(takenName).getUniqueId())
                        .isEqualTo(newPlayerUuid);
                assertThat(rowsUnderThePlaceholder(flavor)).containsExactly(new StoredRow(
                        UsernamePlaceholder.INVALID_OLD_USERNAME, holderUuid.toString(),
                        NAME_LOST_MINING_LEVEL));
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * A player who changes their name gives up the old one without taking it from anyone.
         * Moving whoever else was stored under it to the placeholder dropped them from the
         * leaderboards until they logged in again.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void playerChangingTheirNameShouldLeaveOthersUnderTheirOldNameAlone(DbFlavor flavor)
                throws SQLException {
            // Given - a player, and another player stored under the same name, as earlier
            // versions could leave them
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager databaseManager = createManagerFor(flavor);
            final String flavorSuffix = flavor.name().toLowerCase(Locale.ROOT);
            final String sharedName = "shared_" + flavorSuffix;
            final UUID renamingPlayerUuid = UUID.randomUUID();
            final UUID otherPlayerUuid = UUID.randomUUID();
            createUserWithSkills(databaseManager, sharedName, renamingPlayerUuid,
                    Map.of(PrimarySkillType.MINING, SAVED_MINING_LEVEL));
            createUserWithSkills(databaseManager, "other_" + flavorSuffix, otherPlayerUuid,
                    Map.of(PrimarySkillType.MINING, ALSO_LOST_MINING_LEVEL));
            renameStoredUser(flavor, "other_" + flavorSuffix, sharedName);

            try {
                // When - the first player logs in under a new name
                databaseManager.loadPlayerProfile(
                        onlinePlayer("renamed_" + flavorSuffix, renamingPlayerUuid));

                // Then - the other player keeps the name, and nobody is under the placeholder
                assertThat(databaseManager.loadPlayerProfile(sharedName).getUniqueId())
                        .isEqualTo(otherPlayerUuid);
                assertThat(rowsUnderThePlaceholder(flavor)).isEmpty();
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * A player who lost their name gets a name back when they log in. The placeholder was
         * never theirs to give up, so everyone else under it keeps it as it is spelled.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void loggingInUnderANewNameShouldLeaveTheOthersUnderThePlaceholderAlone(DbFlavor flavor)
                throws SQLException {
            // Given - two players who lost their names under the older spelling
            final SQLDatabaseManager databaseManager = databaseWithPlayersWhoLostTheirNames(
                    flavor);
            final String flavorSuffix = flavor.name().toLowerCase(Locale.ROOT);
            final UUID alsoLostTheirNameUuid = UUID.randomUUID();
            createUserWithSkills(databaseManager, "also_lost_" + flavorSuffix,
                    alsoLostTheirNameUuid, Map.of(PrimarySkillType.MINING, ALSO_LOST_MINING_LEVEL));
            renameStoredUser(flavor, "also_lost_" + flavorSuffix,
                    UsernamePlaceholder.LEGACY_FLATFILE_INVALID_OLD_USERNAME);

            try {
                // When - one of them logs in under a new name
                final PlayerProfile profile = databaseManager.loadPlayerProfile(onlinePlayer(
                        "new_name_" + flavorSuffix, nameLostBeforeTheSpellingChangedUuid));

                // Then - they get the new name, and the others keep the placeholder
                assertThat(profile.getPlayerName()).isEqualTo("new_name_" + flavorSuffix);
                assertThat(rowsUnderThePlaceholder(flavor)).containsExactlyInAnyOrder(
                        nameLostRow(), new StoredRow(
                                UsernamePlaceholder.LEGACY_FLATFILE_INVALID_OLD_USERNAME,
                                alsoLostTheirNameUuid.toString(), ALSO_LOST_MINING_LEVEL));
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * Only a player who is online is known to go by the name they are loaded under. An
         * offline player carries the name they last joined with, so loading them under it wrote
         * that name into their row while the player holding it now kept it too.
         */
        @ParameterizedTest(name = "{0} - loaded as {1}")
        @MethodSource("flavorsAndLoadsWhileNotOnline")
        void loadingAPlayerWhoIsNotOnlineShouldLeaveTheStoredNamesAlone(DbFlavor flavor,
                String description, LoadWhileNotOnline load) throws SQLException {
            // Given - a player who lost their name to the player holding it now
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager databaseManager = createManagerFor(flavor);
            final String takenName = "taken_" + flavor.name().toLowerCase(Locale.ROOT);
            createUserWithSkills(databaseManager, takenName, nameLostUuid,
                    Map.of(PrimarySkillType.MINING, NAME_LOST_MINING_LEVEL));
            final UUID holderUuid = UUID.randomUUID();
            databaseManager.newUser(takenName, holderUuid);

            try {
                // When - the player who lost it is loaded under it without being online
                final PlayerProfile profile = load.load(databaseManager, nameLostUuid, takenName);

                // Then - they are loaded with their progress, under the name stored for them
                assertThat(profile.getPlayerName())
                        .isEqualTo(UsernamePlaceholder.INVALID_OLD_USERNAME);
                assertThat(profile.getSkillLevel(PrimarySkillType.MINING))
                        .isEqualTo(NAME_LOST_MINING_LEVEL);

                // And - the name still finds the player holding it
                assertThat(databaseManager.loadPlayerProfile(takenName).getUniqueId())
                        .isEqualTo(holderUuid);
                assertThat(rowsUnderThePlaceholder(flavor)).containsExactly(nameLostRow());
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * Earlier versions let a returning player take a name without moving whoever held it,
         * so two rows can hold one name. /mcremove on it deleted both. The next login under
         * the name settles who has it.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void loggingInShouldMoveOthersStoredUnderTheirNameToThePlaceholder(DbFlavor flavor)
                throws SQLException {
            // Given - a player, and another player stored under the same name
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager databaseManager = createManagerFor(flavor);
            final String flavorSuffix = flavor.name().toLowerCase(Locale.ROOT);
            final String playerName = "shared_" + flavorSuffix;
            final UUID playerUuid = UUID.randomUUID();
            final UUID otherPlayerUuid = UUID.randomUUID();
            createUserWithSkills(databaseManager, playerName, playerUuid,
                    Map.of(PrimarySkillType.MINING, SAVED_MINING_LEVEL));
            createUserWithSkills(databaseManager, "other_" + flavorSuffix, otherPlayerUuid,
                    Map.of(PrimarySkillType.MINING, ALSO_LOST_MINING_LEVEL));
            renameStoredUser(flavor, "other_" + flavorSuffix, playerName);

            try {
                // When - the first player logs in under the name they are stored under
                final PlayerProfile profile = databaseManager.loadPlayerProfile(
                        onlinePlayer(playerName, playerUuid));

                // Then - they keep the name and their progress
                assertThat(profile.getPlayerName()).isEqualTo(playerName);
                assertThat(profile.getSkillLevel(PrimarySkillType.MINING))
                        .isEqualTo(SAVED_MINING_LEVEL);

                // And - the other player keeps their progress under the placeholder
                assertThat(rowsUnderThePlaceholder(flavor)).containsExactly(new StoredRow(
                        UsernamePlaceholder.INVALID_OLD_USERNAME, otherPlayerUuid.toString(),
                        ALSO_LOST_MINING_LEVEL));
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * A row without a UUID is found by its name, so a player can match their own row and
         * one from before mcMMO kept UUIDs. Their own row is the one to load. With many rows
         * without a UUID, the database reads both matches in the order they were stored.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void loggingInShouldLoadTheRowWithTheirUuidOverOneWithout(DbFlavor flavor)
                throws SQLException {
            // Given - a player stored before mcMMO kept UUIDs
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager databaseManager = createManagerFor(flavor);
            final String flavorSuffix = flavor.name().toLowerCase(Locale.ROOT);
            final String playerName = "legacy_" + flavorSuffix;
            storePlayerWithoutAUuid(flavor, databaseManager, playerName,
                    NAME_LOST_WITHOUT_A_UUID_MINING_LEVEL);

            // And - a player stored with their UUID under the same name, who logged out since
            final UUID playerUuid = UUID.randomUUID();
            createUserWithSkills(databaseManager, "current_" + flavorSuffix, playerUuid,
                    Map.of(PrimarySkillType.MINING, SAVED_MINING_LEVEL));
            renameStoredUser(flavor, "current_" + flavorSuffix, playerName);
            databaseManager.cleanupUser(playerUuid);

            // And - many other players stored before mcMMO kept UUIDs
            storeNamesWithoutAUuid(flavor, 2000);

            try {
                // When - the player with the UUID logs in
                final PlayerProfile profile = databaseManager.loadPlayerProfile(
                        onlinePlayer(playerName, playerUuid));

                // Then - their own progress is loaded
                assertThat(profile.getUniqueId()).isEqualTo(playerUuid);
                assertThat(profile.getSkillLevel(PrimarySkillType.MINING))
                        .isEqualTo(SAVED_MINING_LEVEL);

                // And - the player without a UUID keeps their progress under the placeholder
                assertThat(rowsUnderThePlaceholder(flavor)).containsExactly(
                        nameLostWithoutAUuidRow());
            } finally {
                databaseManager.onDisable();
            }
        }

        static Stream<Arguments> flavorsAndNamesToRejoinUnder() {
            return dbFlavors().flatMap(flavor -> Stream.of(
                    Arguments.of(flavor, LEGACY_PLAYER_NAME),
                    Arguments.of(flavor, "renamed_legacy_player")));
        }

        /**
         * The whole life of a player stored before mcMMO kept UUIDs, from their first login after
         * this update to the one after it. The first login is the last time their row is found
         * by name: it stores their UUID in the row, so every save and login from then on finds
         * it by UUID, and changing their name no longer costs them their progress.
         */
        @ParameterizedTest(name = "{0} - rejoins as {1}")
        @MethodSource("flavorsAndNamesToRejoinUnder")
        void playerStoredWithoutAUuidShouldBeFoundByUuidFromTheirFirstLoginOn(DbFlavor flavor,
                String rejoinName) throws SQLException {
            // Given - a player stored before mcMMO kept UUIDs: their row holds their name and
            // progress but no UUID, since the upgrade that added UUIDs could not look theirs up
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager databaseManager = createManagerFor(flavor);
            storePlayerWithoutAUuid(flavor, databaseManager, LEGACY_PLAYER_NAME,
                    NAME_LOST_WITHOUT_A_UUID_MINING_LEVEL);

            // And - the UUID the server knows them by when they join, which no row holds yet
            final UUID playerUuid = UUID.randomUUID();

            // And - PlayerProfile.save reaches the database through mcMMO.getDatabaseManager(),
            // as autosaves and logouts do on a running server
            mockedMcMMO.when(mcMMO::getDatabaseManager).thenReturn(databaseManager);

            try {
                // When - they join: mcMMO loads the profile of the online Player, as
                // PlayerProfileLoadingTask does after PlayerJoinEvent. No row has their UUID, so
                // the lookup falls back to their name among the rows without a UUID
                final PlayerProfile profileAtFirstJoin = databaseManager.loadPlayerProfile(
                        onlinePlayer(LEGACY_PLAYER_NAME, playerUuid));

                // Then - they get their stored progress instead of a fresh start
                assertThat(profileAtFirstJoin.isLoaded()).isTrue();
                assertThat(profileAtFirstJoin.getSkillLevel(PrimarySkillType.MINING))
                        .isEqualTo(NAME_LOST_WITHOUT_A_UUID_MINING_LEVEL);

                // And - the profile carries their UUID, which saving it requires
                assertThat(profileAtFirstJoin.getUniqueId()).isEqualTo(playerUuid);

                // And - the join itself wrote their UUID into their row, before any save. This
                // reads the table directly, since the manager's UUID cache would hide a miss
                assertThat(allStoredRows(flavor)).containsExactly(new StoredRow(
                        LEGACY_PLAYER_NAME, playerUuid.toString(),
                        NAME_LOST_WITHOUT_A_UUID_MINING_LEVEL));

                // When - they play: gaining Mining levels marks the profile as changed
                profileAtFirstJoin.addLevels(PrimarySkillType.MINING, LEVELS_GAINED);

                // And - mcMMO saves them, as an autosave or their logout would. save(true) runs
                // on this thread and goes through DatabaseManager.saveUser
                profileAtFirstJoin.save(true);

                // Then - the save lands in their own row, found by their UUID
                final int levelAfterPlaying = NAME_LOST_WITHOUT_A_UUID_MINING_LEVEL
                        + LEVELS_GAINED;
                assertThat(allStoredRows(flavor)).containsExactly(new StoredRow(
                        LEGACY_PLAYER_NAME, playerUuid.toString(), levelAfterPlaying));

                // When - they leave: McMMOPlayer.logout calls cleanupUser, which forgets the row
                // cached for their UUID. The profile from this session is dropped as well, so
                // nothing about them is left in memory
                databaseManager.cleanupUser(playerUuid);

                // And - they join again, under the same name or a new one
                final PlayerProfile profileAtRejoin = databaseManager.loadPlayerProfile(
                        onlinePlayer(rejoinName, playerUuid));

                // Then - their row is found by UUID: it has one now, so the fallback to rows
                // without a UUID cannot match it, and under a new name a name lookup finds none
                assertThat(profileAtRejoin.isLoaded()).isTrue();
                assertThat(profileAtRejoin.getUniqueId()).isEqualTo(playerUuid);
                assertThat(profileAtRejoin.getSkillLevel(PrimarySkillType.MINING))
                        .isEqualTo(levelAfterPlaying);

                // And - they still have exactly one row, under the name they joined with
                assertThat(allStoredRows(flavor)).containsExactly(new StoredRow(rejoinName,
                        playerUuid.toString(), levelAfterPlaying));
            } finally {
                mockedMcMMO.when(mcMMO::getDatabaseManager).thenReturn(null);
                databaseManager.onDisable();
            }
        }

        /** The UUID is all that tells apart players who lost their names. */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void storedUsersShouldListEachPlayerWithTheirUuid(DbFlavor flavor) throws SQLException {
            // Given - players who lost their names, one stored before mcMMO kept UUIDs
            final SQLDatabaseManager databaseManager = databaseWithPlayersWhoLostTheirNames(
                    flavor);
            storePlayerWhoLostTheirNameWithoutAUuid(flavor, databaseManager);

            try {
                // When - the stored users are listed with their UUIDs
                final List<PlayerNameAndUUID> storedUsers =
                        databaseManager.getStoredUsersWithUUIDs();

                // Then - each of them is listed once, with their own UUID
                assertThat(storedUsers).containsExactlyInAnyOrder(
                        new PlayerNameAndUUID(UsernamePlaceholder.INVALID_OLD_USERNAME,
                                nameLostUuid),
                        new PlayerNameAndUUID(
                                UsernamePlaceholder.LEGACY_FLATFILE_INVALID_OLD_USERNAME,
                                nameLostBeforeTheSpellingChangedUuid),
                        new PlayerNameAndUUID(UsernamePlaceholder.INVALID_OLD_USERNAME, null));
            } finally {
                databaseManager.onDisable();
            }
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void storedUsersShouldListAMalformedUuidAsNone(DbFlavor flavor) throws SQLException {
            // Given - a player whose uuid column holds something that is not a UUID
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager databaseManager = createManagerFor(flavor);
            final String playerName = "damaged_" + flavor.name().toLowerCase(Locale.ROOT);
            databaseManager.newUser(playerName, UUID.randomUUID());
            setStoredUuid(flavor, playerName, "not-a-uuid");

            try {
                // When - the stored users are listed with their UUIDs
                final List<PlayerNameAndUUID> storedUsers =
                        databaseManager.getStoredUsersWithUUIDs();

                // Then - the player is listed without one
                assertThat(storedUsers).containsExactly(new PlayerNameAndUUID(playerName, null));
            } finally {
                databaseManager.onDisable();
            }
        }

        private void setStoredUuid(DbFlavor flavor, String playerName, String uuid)
                throws SQLException {
            final JdbcDatabaseContainer<?> container = containerFor(flavor);
            try (Connection connection = DriverManager.getConnection(
                    container.getJdbcUrl(), container.getUsername(), container.getPassword());
                    PreparedStatement statement = connection.prepareStatement(
                            "UPDATE mcmmo_users SET uuid = ? WHERE `user` = ?")) {
                statement.setString(1, uuid);
                statement.setString(2, playerName);
                assertThat(statement.executeUpdate()).isEqualTo(1);
            }
        }

        /** A name nobody holds, a legacy player's name, and every spelling of the placeholder. */
        static Stream<Arguments> flavorsAndNamesForAProfileWithoutAUuid() {
            return dbFlavors().flatMap(flavor -> Stream.concat(
                            Stream.of("nobody_holds_this_name", LEGACY_PLAYER_NAME),
                            UsernamePlaceholderTest.placeholderSpellings())
                    .map(savedName -> Arguments.of(flavor, savedName)));
        }

        /**
         * mcMMO only saves players with a UUID. A profile without one, as another plugin might
         * save, used to overwrite the row of a legacy player stored under its name, or get a
         * row of its own that nothing could find again.
         */
        @ParameterizedTest(name = "{0} - saved as {1}")
        @MethodSource("flavorsAndNamesForAProfileWithoutAUuid")
        void profileWithoutAUuidShouldNotBeSaved(DbFlavor flavor, String savedName)
                throws SQLException {
            // Given - players who lost their names, and a player stored before mcMMO kept UUIDs
            final SQLDatabaseManager setUpManager = databaseWithPlayersWhoLostTheirNames(flavor);
            storePlayerWithoutAUuid(flavor, setUpManager, LEGACY_PLAYER_NAME,
                    NAME_LOST_WITHOUT_A_UUID_MINING_LEVEL);
            setUpManager.onDisable();
            final List<StoredRow> rowsBeforeTheSave = allStoredRows(flavor);
            final RecordingHandler logRecords = new RecordingHandler();
            final SQLDatabaseManager databaseManager = createManagerFor(flavor,
                    logRecords.newLogger());

            try {
                // When - a profile without a UUID is saved
                final boolean saved = databaseManager.saveUser(
                        profileFor(new StoredRow(savedName, null, SAVED_MINING_LEVEL)));

                // Then - it is refused, and no stored player changes
                assertThat(saved).isFalse();
                assertThat(allStoredRows(flavor))
                        .containsExactlyInAnyOrderElementsOf(rowsBeforeTheSave);

                // And - the log names the profile that was not saved, and why
                assertThat(logRecords.messagesAt(Level.WARNING)).anySatisfy(
                        message -> assertThat(message).contains("Not saving " + savedName + ",")
                                .contains("UUID"));
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * mcMMO only adds players with a UUID. Adding one without, as another plugin might, used
         * to take the name from the player holding it and store a row nothing could find again.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void newUserWithoutAUuidShouldNotBeAdded(DbFlavor flavor) throws SQLException {
            // Given - a player holding a name
            truncateAllCoreTables(flavor);
            final RecordingHandler logRecords = new RecordingHandler();
            final SQLDatabaseManager databaseManager = createManagerFor(flavor,
                    logRecords.newLogger());
            createUserWithSkills(databaseManager, LEGACY_PLAYER_NAME, UUID.randomUUID(),
                    Map.of(PrimarySkillType.MINING, SAVED_MINING_LEVEL));
            final List<StoredRow> rowsBeforeTheAdd = allStoredRows(flavor);

            try {
                // When - a player without a UUID is added under that name
                final PlayerProfile profile = databaseManager.newUser(LEGACY_PLAYER_NAME, null);

                // Then - nothing is stored, the holder keeps the name, and the profile is
                // unloaded so nothing saves it either
                assertThat(profile.isLoaded()).isFalse();
                assertThat(allStoredRows(flavor)).containsExactlyElementsOf(rowsBeforeTheAdd);

                // And - the log names the player who was not added, and why
                assertThat(logRecords.messagesAt(Level.WARNING)).anySatisfy(
                        message -> assertThat(message)
                                .contains("Not adding " + LEGACY_PLAYER_NAME + ",")
                                .contains("UUID"));
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * A profile under the placeholder is matched by its UUID alone. Matching the name too
         * claimed the row of a player who lost their name before mcMMO kept UUIDs.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void profileUnderThePlaceholderShouldNotClaimARowWithoutAUuid(DbFlavor flavor)
                throws SQLException {
            // Given - a player who lost their name before mcMMO kept UUIDs
            final SQLDatabaseManager databaseManager = databaseWithPlayersWhoLostTheirNames(
                    flavor);
            storePlayerWhoLostTheirNameWithoutAUuid(flavor, databaseManager);
            final StoredRow savedRow = new StoredRow(UsernamePlaceholder.INVALID_OLD_USERNAME,
                    UUID.randomUUID().toString(), SAVED_MINING_LEVEL);

            try {
                // When - a profile under the placeholder, with a UUID no row has, is saved
                final boolean saved = databaseManager.saveUser(profileFor(savedRow));

                // Then - it is stored as one more player, and the others are left alone
                assertThat(saved).isTrue();
                assertThat(rowsUnderThePlaceholder(flavor)).containsExactlyInAnyOrder(
                        nameLostRow(), nameLostBeforeTheSpellingChangedRow(),
                        nameLostWithoutAUuidRow(), savedRow);
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * Rows under the older FlatFile spelling arrive through conversion, and were listed
         * because the leaderboard left out only the SQL spelling.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void leaderboardAndRanksShouldLeaveOutRowsUnderEitherSpelling(DbFlavor flavor)
                throws Exception {
            // Given - players who lost their names, both out-levelling a player with a name
            final SQLDatabaseManager databaseManager = databaseWithPlayersWhoLostTheirNames(
                    flavor);
            final String playerWithAName = "has_a_name_" + flavor.name().toLowerCase(Locale.ROOT);
            createUserWithSkills(databaseManager, playerWithAName, UUID.randomUUID(),
                    Map.of(PrimarySkillType.MINING, 100));

            try {
                // When - the leaderboard and that player's ranks are read
                final List<PlayerStat> mining =
                        databaseManager.readLeaderboard(PrimarySkillType.MINING, 1, 10);
                final List<PlayerStat> powerLevels = databaseManager.readLeaderboard(null, 1, 10);
                final Map<PrimarySkillType, Integer> ranks =
                        databaseManager.readRank(playerWithAName);

                // Then - only the player with a name is listed, and ranks first
                assertThat(mining).extracting(PlayerStat::playerName)
                        .containsExactly(playerWithAName);
                assertThat(powerLevels).extracting(PlayerStat::playerName)
                        .containsExactly(playerWithAName);
                assertThat(ranks.get(PrimarySkillType.MINING)).isEqualTo(1);
                assertThat(ranks.get(null)).isEqualTo(1);
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * Converting to FlatFile loaded players by name, so the placeholder found the same player
         * once for each who lost their name under that spelling, and the rest were left behind.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void convertingToFlatFileShouldCarryOverEachPlayerWhoLostTheirName(DbFlavor flavor,
                @TempDir Path flatFileFolder) throws IOException, SQLException {
            // Given - players who lost their names, two of them under the same spelling
            final SQLDatabaseManager databaseManager = databaseWithPlayersWhoLostTheirNames(
                    flavor);
            final String flavorSuffix = flavor.name().toLowerCase(Locale.ROOT);
            final UUID alsoLostTheirNameUuid = UUID.randomUUID();
            createUserWithSkills(databaseManager, "also_lost_" + flavorSuffix,
                    alsoLostTheirNameUuid, Map.of(PrimarySkillType.MINING, ALSO_LOST_MINING_LEVEL));
            renameStoredUser(flavor, "also_lost_" + flavorSuffix,
                    UsernamePlaceholder.INVALID_OLD_USERNAME);

            // And - a player with a name
            final StoredRow playerWithANameRow = new StoredRow("has_a_name_" + flavorSuffix,
                    UUID.randomUUID().toString(), SAVED_MINING_LEVEL);
            createUserWithSkills(databaseManager, playerWithANameRow.name(),
                    UUID.fromString(playerWithANameRow.uuid()),
                    Map.of(PrimarySkillType.MINING, SAVED_MINING_LEVEL));
            final FlatFileDatabaseManager destination = new FlatFileDatabaseManager(
                    flatFileFolder.resolve("mcmmo.users").toFile(), logger, 0, 0, true);

            try {
                // When - the database is converted to FlatFile
                databaseManager.convertUsers(destination);

                // Then - every player is carried over once, with their own progress
                assertThat(flatFileRows(destination)).containsExactlyInAnyOrder(nameLostRow(),
                        nameLostBeforeTheSpellingChangedRow(),
                        new StoredRow(UsernamePlaceholder.INVALID_OLD_USERNAME,
                                alsoLostTheirNameUuid.toString(), ALSO_LOST_MINING_LEVEL),
                        playerWithANameRow);
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * FlatFile only stores players with a UUID, so converting to it leaves out anyone stored
         * before mcMMO kept UUIDs. The log names each of them and counts them at the end.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void convertingToFlatFileShouldLeaveOutAndLogAPlayerWithoutAUuid(DbFlavor flavor,
                @TempDir Path flatFileFolder) throws IOException, SQLException {
            // Given - a player stored before mcMMO kept UUIDs, and a player with a UUID
            truncateAllCoreTables(flavor);
            final RecordingHandler logRecords = new RecordingHandler();
            final Logger recordingLogger = logRecords.newLogger();
            final SQLDatabaseManager databaseManager = createManagerFor(flavor, recordingLogger);
            final String flavorSuffix = flavor.name().toLowerCase(Locale.ROOT);
            final String playerWithoutAUuid = "legacy_" + flavorSuffix;
            storePlayerWithoutAUuid(flavor, databaseManager, playerWithoutAUuid,
                    SAVED_MINING_LEVEL);
            final StoredRow playerWithAUuidRow = new StoredRow("has_a_uuid_" + flavorSuffix,
                    UUID.randomUUID().toString(), ALSO_LOST_MINING_LEVEL);
            createUserWithSkills(databaseManager, playerWithAUuidRow.name(),
                    UUID.fromString(playerWithAUuidRow.uuid()),
                    Map.of(PrimarySkillType.MINING, ALSO_LOST_MINING_LEVEL));
            final FlatFileDatabaseManager destination = new FlatFileDatabaseManager(
                    flatFileFolder.resolve("mcmmo.users").toFile(), recordingLogger, 0, 0, true);

            try {
                // When - the database is converted to FlatFile
                databaseManager.convertUsers(destination);

                // Then - only the player with a UUID is carried over
                assertThat(flatFileRows(destination)).containsExactly(playerWithAUuidRow);

                // And - the log names the player left out, and counts them
                assertThat(logRecords.messagesAt(Level.WARNING))
                        .anySatisfy(message -> assertThat(message)
                                .contains("Not saving " + playerWithoutAUuid + ","))
                        .anySatisfy(message -> assertThat(message)
                                .contains("Could not convert 1 of 2 users"));
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * FlatFile saves take the name from whoever holds it, so the player saved last keeps a
         * name two rows share. That should be whoever logged in last.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void convertingToFlatFileShouldLeaveASharedNameWithWhoeverLoggedInLast(DbFlavor flavor,
                @TempDir Path flatFileFolder) throws IOException, SQLException {
            // Given - two players stored under the same name, the one stored first having logged
            // in last
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager databaseManager = createManagerFor(flavor);
            final String flavorSuffix = flavor.name().toLowerCase(Locale.ROOT);
            final String sharedName = "shared_" + flavorSuffix;
            final UUID loggedInLastUuid = UUID.randomUUID();
            final UUID otherPlayerUuid = UUID.randomUUID();
            createUserWithSkills(databaseManager, sharedName, loggedInLastUuid,
                    Map.of(PrimarySkillType.MINING, SAVED_MINING_LEVEL));
            createUserWithSkills(databaseManager, "other_" + flavorSuffix, otherPlayerUuid,
                    Map.of(PrimarySkillType.MINING, ALSO_LOST_MINING_LEVEL));
            renameStoredUser(flavor, "other_" + flavorSuffix, sharedName);
            setLastLogin(flavor, loggedInLastUuid, 2_000_000_000L);
            setLastLogin(flavor, otherPlayerUuid, 1_000_000_000L);
            final FlatFileDatabaseManager destination = new FlatFileDatabaseManager(
                    flatFileFolder.resolve("mcmmo.users").toFile(), logger, 0, 0, true);

            try {
                // When - the database is converted to FlatFile
                databaseManager.convertUsers(destination);

                // Then - the name stays with whoever logged in last, and the other player keeps
                // their progress under the placeholder
                assertThat(flatFileRows(destination)).containsExactlyInAnyOrder(
                        new StoredRow(sharedName, loggedInLastUuid.toString(),
                                SAVED_MINING_LEVEL),
                        new StoredRow(UsernamePlaceholder.INVALID_OLD_USERNAME,
                                otherPlayerUuid.toString(), ALSO_LOST_MINING_LEVEL));
            } finally {
                databaseManager.onDisable();
            }
        }

        /**
         * Converting from FlatFile carries over every player who lost their name, each by their
         * UUID. FlatFile removes rows without a UUID when it starts, so none reach a conversion.
         */
        @ParameterizedTest(name = "{0}")
        @MethodSource("com.gmail.nossr50.database.SQLDatabaseManagerTest#dbFlavors")
        void convertingFromFlatFileShouldCarryOverEachPlayerWhoLostTheirName(DbFlavor flavor,
                @TempDir Path flatFileFolder) throws IOException, SQLException {
            // Given - a FlatFile database with players who lost their names, two of them under
            // the same spelling
            final List<StoredRow> flatFileRows = List.of(nameLostRow(),
                    nameLostBeforeTheSpellingChangedRow(),
                    new StoredRow(UsernamePlaceholder.INVALID_OLD_USERNAME,
                            UUID.randomUUID().toString(), ALSO_LOST_MINING_LEVEL));
            final FlatFileDatabaseManager source = new FlatFileDatabaseManager(
                    flatFileFolder.resolve("mcmmo.users").toFile(), logger, 0, 0, true);
            final StringBuilder usersFile = new StringBuilder();
            for (StoredRow flatFileRow : flatFileRows) {
                source.writeUserToLine(profileFor(flatFileRow), usersFile);
            }
            java.nio.file.Files.writeString(source.getUsersFile().toPath(), usersFile);

            // And - an empty SQL database
            truncateAllCoreTables(flavor);
            final SQLDatabaseManager destination = createManagerFor(flavor);

            try {
                // When - the FlatFile database is converted to SQL
                source.convertUsers(destination);

                // Then - each of them is stored once, with their own progress
                assertThat(rowsUnderThePlaceholder(flavor))
                        .containsExactlyInAnyOrderElementsOf(flatFileRows);
            } finally {
                destination.onDisable();
            }
        }
    }

}
