package com.gmail.nossr50.placeholders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.database.SQLDatabaseManager;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.util.upgrade.UpgradeManager;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;
import org.testcontainers.mysql.MySQLContainer;

@Tag("docker")
@Testcontainers
class LeaderboardPlaceholderSqlIntegrationTest {
    private static final @NotNull Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Container
    private static final MySQLContainer MYSQL_CONTAINER =
            new MySQLContainer("mysql:8.0")
                    .withDatabaseName("mcmmo")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    private static final MariaDBContainer MARIADB_CONTAINER =
            new MariaDBContainer("mariadb:10.11")
                    .withDatabaseName("mcmmo")
                    .withUsername("test")
                    .withPassword("test");

    private static MockedStatic<mcMMO> mockedMcMMO;
    private static MockedStatic<ExperienceConfig> mockedExperienceConfig;
    private static GeneralConfig generalConfig;
    private static File testDataFolder;

    private enum DbFlavor {
        MYSQL,
        MARIADB
    }

    @BeforeAll
    static void setUpClass() {
        MinecraftGameVersion minecraftGameVersion = mock(MinecraftGameVersion.class);
        when(minecraftGameVersion.isAtLeast(anyInt(), anyInt(), anyInt())).thenReturn(true);

        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
        mcMMO.p = Mockito.mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(LOGGER);

        try {
            testDataFolder = java.nio.file.Files.createTempDirectory("mcmmo-sql-placeholder-it-")
                    .toFile();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to create temp SQL integration test data folder",
                    exception);
        }
        when(mcMMO.p.getDataFolder()).thenReturn(testDataFolder);
        when(mcMMO.getMinecraftGameVersion()).thenReturn(minecraftGameVersion);

        generalConfig = Mockito.mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
        when(generalConfig.getLocale()).thenReturn("en_US");
        when(generalConfig.getMySQLTablePrefix()).thenReturn("mcmmo_");
        when(generalConfig.getMySQLPublicKeyRetrieval()).thenReturn(true);
        when(generalConfig.getUseMySQL()).thenReturn(true);
        when(generalConfig.getMySQLSSL()).thenReturn(true);
        when(generalConfig.getMobHealthbarDefault()).thenReturn(MobHealthbarType.HEARTS);

        when(generalConfig.getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.MISC)).thenReturn(10);
        when(generalConfig.getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.LOAD)).thenReturn(20);
        when(generalConfig.getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier.SAVE)).thenReturn(20);

        when(generalConfig.getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.MISC))
                .thenReturn(30);
        when(generalConfig.getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.LOAD))
                .thenReturn(30);
        when(generalConfig.getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier.SAVE))
                .thenReturn(30);

        AdvancedConfig advancedConfig = Mockito.mock(AdvancedConfig.class);
        when(mcMMO.p.getAdvancedConfig()).thenReturn(advancedConfig);
        when(advancedConfig.getStartingLevel()).thenReturn(0);

        ExperienceConfig experienceConfig = Mockito.mock(ExperienceConfig.class);
        when(experienceConfig.getDiminishedReturnsEnabled()).thenReturn(false);
        mockedExperienceConfig = Mockito.mockStatic(ExperienceConfig.class);
        mockedExperienceConfig.when(ExperienceConfig::getInstance).thenReturn(experienceConfig);

        SkillTools skillTools = new SkillTools(mcMMO.p);
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);

        UpgradeManager upgradeManager = Mockito.mock(UpgradeManager.class);
        when(mcMMO.getUpgradeManager()).thenReturn(upgradeManager);
        when(upgradeManager.shouldUpgrade(any())).thenReturn(false);
    }

    @AfterAll
    static void tearDownClass() {
        if (mockedMcMMO != null) {
            mockedMcMMO.close();
        }
        if (mockedExperienceConfig != null) {
            mockedExperienceConfig.close();
        }
        if (testDataFolder != null) {
            deleteRecursively(testDataFolder);
        }
    }

    @Test
    void placeholdersShouldResolveSkillAndOverallRanksWhenUsingSqlBackends() throws Exception {
        // Given - pseudo-real SQL backends (MySQL and MariaDB) with deterministic seeded users.
        for (DbFlavor flavor : DbFlavor.values()) {
            truncateAllCoreTables(flavor);
            SQLDatabaseManager databaseManager = createManagerFor(flavor);

            try {
                seedUserWithMiningLevel(databaseManager, "sql_top_" + flavor.name().toLowerCase(),
                        600);
                seedUserWithMiningLevel(databaseManager, "sql_low_" + flavor.name().toLowerCase(),
                        300);

                // When - refreshing the cache and resolving skill/overall placeholders.
                AtomicInteger readCount = new AtomicInteger(0);
                LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(
                        5,
                        (skill, pageNumber, statsPerPage) -> {
                            readCount.incrementAndGet();
                            return databaseManager.readLeaderboard(skill, pageNumber, statsPerPage);
                        },
                        LOGGER
                );

                // Then - placeholders should return expected ranked values and avoid post-refresh DB reads.
                assertThat(cache.refreshNow()).isTrue();
                int readsAfterRefresh = readCount.get();
                assertThat(readsAfterRefresh).isGreaterThan(0);

                McTopNamePlaceholder miningName = new McTopNamePlaceholder(PrimarySkillType.MINING,
                        cache);
                McTopPositionPlaceholder miningValue = new McTopPositionPlaceholder(
                        PrimarySkillType.MINING, cache);
                McTopNamePlaceholder overallName = new McTopNamePlaceholder(null, cache);
                McTopPositionPlaceholder overallValue = new McTopPositionPlaceholder(null, cache);

                assertThat(miningName.process(null, "1")).isEqualTo("sql_top_"
                        + flavor.name().toLowerCase());
                assertThat(miningValue.process(null, "1")).isEqualTo("600");
                assertThat(overallName.process(null, "2")).isEqualTo("sql_low_"
                        + flavor.name().toLowerCase());
                assertThat(overallValue.process(null, "2")).isEqualTo("300");

                assertThat(readCount.get()).isEqualTo(readsAfterRefresh);
            } finally {
                databaseManager.onDisable();
            }
        }
    }

    @ParameterizedTest(name = "overall alias {0} at position {1} should match canonical overall")
    @MethodSource("aliasAndPositionProvider")
    void aliasesShouldMatchCanonicalOverallWhenUsingSqlBackends(String aliasToken, String position)
            throws Exception {
        // Given - each SQL backend with seeded leaderboard rows.
        for (DbFlavor flavor : DbFlavor.values()) {
            truncateAllCoreTables(flavor);
            SQLDatabaseManager databaseManager = createManagerFor(flavor);

            try {
                seedUserWithMiningLevel(databaseManager, "sql_alias_top_"
                        + flavor.name().toLowerCase(), 900);
                seedUserWithMiningLevel(databaseManager, "sql_alias_second_"
                        + flavor.name().toLowerCase(), 800);
                seedUserWithMiningLevel(databaseManager, "sql_alias_third_"
                        + flavor.name().toLowerCase(), 700);

                // When - resolving canonical overall and alias placeholders for the same position.
                LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(
                        5,
                        databaseManager::readLeaderboard,
                        LOGGER
                );
                assertThat(cache.refreshNow()).isTrue();

                McTopNamePlaceholder canonicalName = new McTopNamePlaceholder(null, cache);
                McTopPositionPlaceholder canonicalValue = new McTopPositionPlaceholder(null, cache);
                McTopNamePlaceholder aliasName = new McTopNamePlaceholder(null, aliasToken, cache);
                McTopPositionPlaceholder aliasValue = new McTopPositionPlaceholder(
                        null, aliasToken, cache);

                // Then - alias output should match canonical output.
                assertThat(aliasName.process(null, position)).isEqualTo(
                        canonicalName.process(null, position));
                assertThat(aliasValue.process(null, position)).isEqualTo(
                        canonicalValue.process(null, position));
            } finally {
                databaseManager.onDisable();
            }
        }
    }

    /**
     * Gotcha coverage: tied leaderboard values can appear in backend-specific order.
     * This test asserts stable value agreement and membership, not strict name ordering.
     */
    @Test
    void nameAndValueLookupsShouldStayConsistentWhenSqlLeaderboardValuesAreTied() throws Exception {
        // Given - each SQL backend with tied MINING values at the top of the leaderboard.
        for (DbFlavor flavor : DbFlavor.values()) {
            truncateAllCoreTables(flavor);
            SQLDatabaseManager databaseManager = createManagerFor(flavor);

            try {
                String firstName = "sql_tie_a_" + flavor.name().toLowerCase();
                String secondName = "sql_tie_b_" + flavor.name().toLowerCase();
                seedUserWithMiningLevel(databaseManager, firstName, 450);
                seedUserWithMiningLevel(databaseManager, secondName, 450);

                // When - resolving top rank name/value placeholders.
                LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(
                        2,
                        databaseManager::readLeaderboard,
                        LOGGER
                );
                assertThat(cache.refreshNow()).isTrue();

                McTopNamePlaceholder namePlaceholder = new McTopNamePlaceholder(
                        PrimarySkillType.MINING, cache);
                McTopPositionPlaceholder valuePlaceholder = new McTopPositionPlaceholder(
                        PrimarySkillType.MINING, cache);

                String rankOneName = namePlaceholder.process(null, "1");
                String rankTwoName = namePlaceholder.process(null, "2");

                // Then - both values should remain correct and both tied names should be represented.
                assertThat(valuePlaceholder.process(null, "1")).isEqualTo("450");
                assertThat(valuePlaceholder.process(null, "2")).isEqualTo("450");
                assertThat(Set.of(rankOneName, rankTwoName)).containsExactlyInAnyOrder(
                        firstName, secondName);
            } finally {
                databaseManager.onDisable();
            }
        }
    }

    private static Stream<Arguments> aliasAndPositionProvider() {
        return Stream.of(
                Arguments.of("overall", "1"),
                Arguments.of("all", "2"),
                Arguments.of("powerlevel", "3")
        );
    }

    private static SQLDatabaseManager createManagerFor(DbFlavor flavor) throws Exception {
        JdbcDatabaseContainer<?> container = containerFor(flavor);

        when(generalConfig.getMySQLServerName()).thenReturn(container.getHost());
        when(generalConfig.getMySQLServerPort()).thenReturn(container.getFirstMappedPort());
        when(generalConfig.getMySQLDatabaseName()).thenReturn(container.getDatabaseName());
        when(generalConfig.getMySQLUserName()).thenReturn(container.getUsername());
        when(generalConfig.getMySQLUserPassword()).thenReturn(container.getPassword());

        Constructor<SQLDatabaseManager> constructor = SQLDatabaseManager.class
                .getDeclaredConstructor(Logger.class, String.class);
        constructor.setAccessible(true);

        return constructor.newInstance(LOGGER, "com.mysql.cj.jdbc.Driver");
    }

    private static JdbcDatabaseContainer<?> containerFor(DbFlavor flavor) {
        return switch (flavor) {
            case MYSQL -> MYSQL_CONTAINER;
            case MARIADB -> MARIADB_CONTAINER;
        };
    }

    private static void truncateAllCoreTables(DbFlavor flavor) throws Exception {
        // Ensure mcMMO schema/tables are initialized for this container before cleanup.
        SQLDatabaseManager schemaInitializer = createManagerFor(flavor);
        schemaInitializer.onDisable();

        JdbcDatabaseContainer<?> container = containerFor(flavor);
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword());
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM mcmmo_cooldowns");
            statement.executeUpdate("DELETE FROM mcmmo_experience");
            statement.executeUpdate("DELETE FROM mcmmo_huds");
            statement.executeUpdate("DELETE FROM mcmmo_skills");
            statement.executeUpdate("DELETE FROM mcmmo_users");
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to truncate SQL core tables", exception);
        }
    }

    private static void seedUserWithMiningLevel(SQLDatabaseManager databaseManager,
            String playerName, int miningLevel) {
        UUID uuid = UUID.randomUUID();
        databaseManager.newUser(playerName, uuid);
        PlayerProfile profile = databaseManager.loadPlayerProfile(uuid);
        profile.modifySkill(PrimarySkillType.MINING, miningLevel);
        assertThat(databaseManager.saveUser(profile)).isTrue();
    }

    private static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }
}
