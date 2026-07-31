package com.gmail.nossr50.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Covers persistence of the offline ExperienceAPI mutators. These load a detached
 * PlayerProfile from the database, so any change that is not followed by a save is silently
 * thrown away when the profile goes out of scope; callers get no error and the database
 * keeps the old values. Their add-counterparts (addOfflineXP, addLevelOffline) already
 * save, and these tests pin the set/remove methods to do the same.
 */
class ExperienceAPIOfflinePersistenceTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(
            ExperienceAPIOfflinePersistenceTest.class.getName());
    private static final String TARGET_NAME = "offlineTarget";
    private static final UUID TARGET_UUID = UUID.randomUUID();

    private PlayerProfile offlineProfile;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);

        offlineProfile = spy(new PlayerProfile(TARGET_NAME, TARGET_UUID, 0));
        when(offlineProfile.isLoaded()).thenReturn(true);
        // The scheduling would touch the scheduler; only the fact that a save was requested
        // matters here
        doNothing().when(offlineProfile).scheduleAsyncSave();

        final DatabaseManager databaseManager = mock(DatabaseManager.class);
        when(databaseManager.loadPlayerProfile(TARGET_NAME)).thenReturn(offlineProfile);
        when(databaseManager.loadPlayerProfile(TARGET_UUID)).thenReturn(offlineProfile);
        when(mcMMO.getDatabaseManager()).thenReturn(databaseManager);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Nested
    class SetXpOffline {
        @Test
        @SuppressWarnings("deprecation")
        void byNameShouldApplyAndSave() {
            // Given - an offline profile freshly loaded from the database
            // When - the XP of a skill is set by player name
            ExperienceAPI.setXPOffline(TARGET_NAME, "MINING", 500);

            // Then - the profile holds the new XP and a save is scheduled
            assertThat(offlineProfile.getSkillXpLevel(PrimarySkillType.MINING)).isEqualTo(500);
            verify(offlineProfile).scheduleAsyncSave();
        }

        @Test
        void byUuidShouldApplyAndSave() {
            // Given - an offline profile freshly loaded from the database
            // When - the XP of a skill is set by player UUID
            ExperienceAPI.setXPOffline(TARGET_UUID, "MINING", 500);

            // Then - the profile holds the new XP and a save is scheduled
            assertThat(offlineProfile.getSkillXpLevel(PrimarySkillType.MINING)).isEqualTo(500);
            verify(offlineProfile).scheduleAsyncSave();
        }
    }

    @Nested
    class SetLevelOffline {
        @Test
        @SuppressWarnings("deprecation")
        void byNameShouldApplyAndSave() {
            // Given - an offline profile freshly loaded from the database
            // When - the level of a skill is set by player name
            ExperienceAPI.setLevelOffline(TARGET_NAME, "MINING", 42);

            // Then - the profile holds the new level and a save is scheduled
            assertThat(offlineProfile.getSkillLevel(PrimarySkillType.MINING)).isEqualTo(42);
            verify(offlineProfile).scheduleAsyncSave();
        }

        @Test
        void byUuidShouldApplyAndSave() {
            // Given - an offline profile freshly loaded from the database
            // When - the level of a skill is set by player UUID
            ExperienceAPI.setLevelOffline(TARGET_UUID, "MINING", 42);

            // Then - the profile holds the new level and a save is scheduled
            assertThat(offlineProfile.getSkillLevel(PrimarySkillType.MINING)).isEqualTo(42);
            verify(offlineProfile).scheduleAsyncSave();
        }
    }

    @Nested
    class RemoveXpOffline {
        @Test
        @SuppressWarnings("deprecation")
        void byNameShouldApplyAndSave() {
            // Given - an offline profile with banked XP in a skill
            offlineProfile.addXp(PrimarySkillType.MINING, 300F);

            // When - part of that XP is removed by player name
            ExperienceAPI.removeXPOffline(TARGET_NAME, "MINING", 100);

            // Then - the profile holds the reduced XP and a save is scheduled
            assertThat(offlineProfile.getSkillXpLevel(PrimarySkillType.MINING)).isEqualTo(200);
            verify(offlineProfile).scheduleAsyncSave();
        }

        @Test
        void byUuidShouldApplyAndSave() {
            // Given - an offline profile with banked XP in a skill
            offlineProfile.addXp(PrimarySkillType.MINING, 300F);

            // When - part of that XP is removed by player UUID
            ExperienceAPI.removeXPOffline(TARGET_UUID, "MINING", 100);

            // Then - the profile holds the reduced XP and a save is scheduled
            assertThat(offlineProfile.getSkillXpLevel(PrimarySkillType.MINING)).isEqualTo(200);
            verify(offlineProfile).scheduleAsyncSave();
        }
    }
}
