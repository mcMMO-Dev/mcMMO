package com.gmail.nossr50.runnables.player;

import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_BERSERK;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.UUID_INDEX;
import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import com.tcoded.folialib.impl.PlatformScheduler;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

/**
 * Runs the login profile load against a real FlatFile database. When a player's profile fails
 * to load they must not be handed a new one: its first save would overwrite their stored row
 * with starting levels.
 */
class PlayerProfileLoadingTaskTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(PlayerProfileLoadingTaskTest.class.getName());
    private static final String PLAYER_NAME = "nossr50";
    private static final int STORED_MINING_LEVEL = 42;
    /** A failed first attempt retries after 100 ticks plus 100 per attempt made. */
    private static final long FIRST_RETRY_DELAY_TICKS = 200L;

    /** JUnit deletes this folder, and the users file inside it, after each test. */
    @TempDir
    Path flatFileDirectory;

    private Path usersFile;
    private DatabaseManager databaseManager;
    private PlatformScheduler scheduler;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        when(player.getName()).thenReturn(PLAYER_NAME);
        scheduler = foliaLib.getScheduler();

        usersFile = flatFileDirectory.resolve("mcmmo.users");
        databaseManager = DatabaseManagerFactory.createDatabaseManager(DatabaseType.FLATFILE,
                usersFile.toString(), logger, Long.MAX_VALUE, 0);
        when(mcMMO.getDatabaseManager()).thenReturn(databaseManager);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private void storePlayerWithMiningLevel(int miningLevel) {
        databaseManager.newUser(PLAYER_NAME, playerUUID);
        final PlayerProfile storedProfile = databaseManager.loadPlayerProfile(playerUUID);
        storedProfile.modifySkill(PrimarySkillType.MINING, miningLevel);
        assertThat(databaseManager.saveUser(storedProfile)).isTrue();
    }

    /** Replaces one field of the player's row, the way a damaged file would have it. */
    private void damageStoredRow(int fieldIndex, String value) throws IOException {
        final List<String> damagedRows = Files.readAllLines(usersFile, Charset.defaultCharset())
                .stream()
                .map(row -> {
                    final String[] fields = row.split(":");
                    if (fields.length <= UUID_INDEX
                            || !fields[UUID_INDEX].equals(playerUUID.toString())) {
                        return row;
                    }
                    fields[fieldIndex] = value;
                    return String.join(":", fields) + ":";
                })
                .toList();
        Files.write(usersFile, damagedRows, Charset.defaultCharset());
    }

    private PlayerProfileLoadingTask verifyRetryScheduled() {
        final ArgumentCaptor<PlayerProfileLoadingTask> retry =
                ArgumentCaptor.forClass(PlayerProfileLoadingTask.class);
        verify(scheduler).runLaterAsync(retry.capture(), eq(FIRST_RETRY_DELAY_TICKS));
        return retry.getValue();
    }

    @SuppressWarnings("unchecked")
    private void verifyNoRetryScheduled() {
        verify(scheduler, never()).runLaterAsync(any(Consumer.class), anyLong());
    }

    private void verifyNoProfileApplied() {
        mockedUserManager.verify(() -> UserManager.track(any(McMMOPlayer.class)), never());
    }

    private PlayerProfile verifyProfileApplied() {
        final ArgumentCaptor<McMMOPlayer> trackedPlayer =
                ArgumentCaptor.forClass(McMMOPlayer.class);
        mockedUserManager.verify(() -> UserManager.track(trackedPlayer.capture()));
        return trackedPlayer.getValue().getProfile();
    }

    /** Saves whatever profiles the player was handed, as the autosave and logout would. */
    private void saveAppliedProfiles() {
        final ArgumentCaptor<McMMOPlayer> trackedPlayers =
                ArgumentCaptor.forClass(McMMOPlayer.class);
        mockedUserManager.verify(() -> UserManager.track(trackedPlayers.capture()), atLeast(0));
        for (McMMOPlayer trackedPlayer : trackedPlayers.getAllValues()) {
            final PlayerProfile appliedProfile = trackedPlayer.getProfile();
            appliedProfile.markProfileDirty();
            appliedProfile.save(true);
        }
    }

    @Test
    void storedPlayerShouldGetTheirStoredProfile() {
        // Given - a player stored with some progress
        storePlayerWithMiningLevel(STORED_MINING_LEVEL);

        // When - they log in
        new PlayerProfileLoadingTask(player).run();

        // Then - their stored profile is applied
        final PlayerProfile appliedProfile = verifyProfileApplied();
        assertThat(appliedProfile.getSkillLevel(PrimarySkillType.MINING))
                .isEqualTo(STORED_MINING_LEVEL);
        verifyNoRetryScheduled();
    }

    @Test
    void newPlayerShouldGetAFreshProfile() {
        // Given - a player who has never joined

        // When - they log in
        new PlayerProfileLoadingTask(player).run();

        // Then - a fresh profile at the starting level is applied
        final PlayerProfile appliedProfile = verifyProfileApplied();
        assertThat(appliedProfile.isLoaded()).isTrue();
        assertThat(appliedProfile.getSkillLevel(PrimarySkillType.MINING)).isZero();
        verifyNoRetryScheduled();
    }

    /**
     * A users file that cannot be read, here because it went missing, used to make every
     * player joining at that moment look new.
     */
    @Test
    void unreadableUsersFileShouldRetryInsteadOfStartingThePlayerOver() throws IOException {
        // Given - a stored player, and a users file that cannot be read when they log in
        storePlayerWithMiningLevel(STORED_MINING_LEVEL);
        final byte[] storedBytes = Files.readAllBytes(usersFile);
        Files.delete(usersFile);

        // When - they log in, the file comes back, and anything they were handed is saved
        new PlayerProfileLoadingTask(player).run();
        Files.write(usersFile, storedBytes);
        saveAppliedProfiles();

        // Then - their stored progress is intact
        assertThat(databaseManager.loadPlayerProfile(playerUUID)
                .getSkillLevel(PrimarySkillType.MINING)).isEqualTo(STORED_MINING_LEVEL);

        // And - no profile was applied, and loading is retried later
        verifyNoProfileApplied();
        final PlayerProfileLoadingTask retry = verifyRetryScheduled();

        // When - the retry runs
        retry.run();

        // Then - their stored profile is applied
        final PlayerProfile appliedProfile = verifyProfileApplied();
        assertThat(appliedProfile.getSkillLevel(PrimarySkillType.MINING))
                .isEqualTo(STORED_MINING_LEVEL);
    }

    @Test
    void damagedRowShouldRetryInsteadOfStartingThePlayerOver() throws IOException {
        // Given - a stored player whose row was damaged while the server ran
        storePlayerWithMiningLevel(STORED_MINING_LEVEL);
        damageStoredRow(COOLDOWN_BERSERK, "garbage");
        final byte[] damagedBytes = Files.readAllBytes(usersFile);

        // When - they log in, and anything they were handed is saved
        new PlayerProfileLoadingTask(player).run();
        saveAppliedProfiles();

        // Then - their row is kept as it was, for the startup health check to repair
        assertThat(usersFile).hasBinaryContent(damagedBytes);

        // And - no profile was applied, and loading is retried later
        verifyNoProfileApplied();
        verifyRetryScheduled();
    }
}
