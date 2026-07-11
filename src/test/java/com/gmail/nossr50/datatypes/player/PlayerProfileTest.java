package com.gmail.nossr50.datatypes.player;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.skills.SkillTools;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PlayerProfileTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(PlayerProfileTest.class.getName());

    private static final int STARTING_LEVEL = 10;

    private PlayerProfile profile;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        profile = new PlayerProfile("Herb", UUID.randomUUID(), STARTING_LEVEL);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * Regression coverage for adding levels to a child skill: child skills have no level entry
     * of their own, and the read-then-modify in addLevels crashed on the missing entry before
     * the child guard in modifySkill could run. Child levels split across the parents instead,
     * matching how child XP and the offline ExperienceAPI variants behave.
     */
    @Test
    void addLevelsShouldSplitChildSkillLevelsAcrossParents() {
        // Given - the child skill Smelting with its two parents at the starting level
        final var parents = mcMMO.p.getSkillTools()
                .getChildSkillParents(PrimarySkillType.SMELTING);

        // When - levels are added to the child skill
        profile.addLevels(PrimarySkillType.SMELTING, 4);

        // Then - each parent receives an equal share
        for (final PrimarySkillType parent : parents) {
            assertThat(profile.getSkillLevel(parent)).isEqualTo(STARTING_LEVEL + 2);
        }
    }

    /**
     * Regression coverage for the cumulative XP curve on offline-loaded profiles: the curve
     * levels against the player's power level, which was read through UserManager and crashed
     * for profiles without an online player (offline /inspect, ExperienceAPI offline lookups).
     * The profile's own level sum stands in when nobody is online.
     */
    @Test
    void getXpToLevelShouldUseOwnLevelSumForOfflineProfilesWithCumulativeCurve() {
        // Given - the cumulative curve is enabled and this profile has no online player
        when(ExperienceConfig.getInstance().getCumulativeCurveEnabled()).thenReturn(true);
        when(ExperienceConfig.getInstance().getFormulaType()).thenReturn(FormulaType.LINEAR);

        final FormulaManager formulaManager = mock(FormulaManager.class);
        mockedMcMMO.when(mcMMO::getFormulaManager).thenReturn(formulaManager);
        final int levelSum = SkillTools.NON_CHILD_SKILLS.size() * STARTING_LEVEL;
        when(formulaManager.getXPtoNextLevel(levelSum, FormulaType.LINEAR)).thenReturn(4242);

        // When - the XP to the next level is requested
        final int xpToLevel = profile.getXpToLevel(PrimarySkillType.MINING);

        // Then - the curve levels against the profile's own level sum instead of crashing
        assertThat(xpToLevel).isEqualTo(4242);
    }

    @Test
    void getXpToLevelShouldUseTheSkillLevelWhenCumulativeCurveIsDisabled() {
        // Given - the default curve
        when(ExperienceConfig.getInstance().getFormulaType()).thenReturn(FormulaType.LINEAR);
        final FormulaManager formulaManager = mock(FormulaManager.class);
        mockedMcMMO.when(mcMMO::getFormulaManager).thenReturn(formulaManager);
        when(formulaManager.getXPtoNextLevel(STARTING_LEVEL, FormulaType.LINEAR))
                .thenReturn(1000);

        // When - the XP to the next level is requested
        final int xpToLevel = profile.getXpToLevel(PrimarySkillType.MINING);

        // Then - the curve levels against the skill's own level
        assertThat(xpToLevel).isEqualTo(1000);
    }

    @Test
    void addLevelsShouldAddToANonChildSkillDirectly() {
        // Given - a non-child skill at the starting level
        // When - levels are added
        profile.addLevels(PrimarySkillType.MINING, 5);

        // Then - the skill is raised and no other skill is touched
        assertThat(profile.getSkillLevel(PrimarySkillType.MINING))
                .isEqualTo(STARTING_LEVEL + 5);
        assertThat(profile.getSkillLevel(PrimarySkillType.HERBALISM)).isEqualTo(STARTING_LEVEL);
    }

    @Test
    void addXpShouldSplitChildSkillXpAcrossParents() {
        // Given - the child skill Smelting with its two parents at zero XP
        final var parents = mcMMO.p.getSkillTools()
                .getChildSkillParents(PrimarySkillType.SMELTING);

        // When - XP is added to the child skill
        profile.addXp(PrimarySkillType.SMELTING, 10F);

        // Then - each parent receives an equal share
        for (final PrimarySkillType parent : parents) {
            assertThat(profile.getSkillXpLevelRaw(parent)).isEqualTo(5F);
        }
    }

    /**
     * Regression coverage for reading raw XP on a child skill: child skills store no XP of
     * their own and the raw read crashed on the missing entry. Other plugins can reach this
     * through the profile API; zero matches what the non-raw XP read reports.
     */
    @Test
    void getSkillXpLevelRawShouldReturnZeroForChildSkills() {
        // Given - a child skill, which stores no XP of its own
        // When - the raw XP is read
        // Then - zero is returned instead of an error
        assertThat(profile.getSkillXpLevelRaw(PrimarySkillType.SMELTING)).isZero();
    }

    @Test
    void modifySkillShouldClampNegativeLevelsToZeroAndResetXp() {
        // Given - a skill with some XP progress
        profile.setSkillXpLevel(PrimarySkillType.MINING, 250F);

        // When - the skill is modified to a negative level
        profile.modifySkill(PrimarySkillType.MINING, -5);

        // Then - the level clamps to zero and the XP progress resets
        assertThat(profile.getSkillLevel(PrimarySkillType.MINING)).isZero();
        assertThat(profile.getSkillXpLevelRaw(PrimarySkillType.MINING)).isZero();
    }

    /**
     * Covers the save retry ladder: saves only run for dirty loaded profiles, a success clears
     * the dirty flag, and a failure schedules retries that give up after ten attempts instead
     * of retrying forever.
     */
    @Nested
    class SaveBehavior {
        private PlayerProfile loadedProfile;
        private DatabaseManager databaseManager;
        private PlatformScheduler scheduler;

        @BeforeEach
        void setUpSaveCollaborators() {
            loadedProfile = new PlayerProfile("Herb", UUID.randomUUID(), true, STARTING_LEVEL);
            databaseManager = mock(DatabaseManager.class);
            mockedMcMMO.when(mcMMO::getDatabaseManager).thenReturn(databaseManager);
            final FoliaLib foliaLib = mock(FoliaLib.class);
            scheduler = mock(PlatformScheduler.class);
            when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
            when(foliaLib.getScheduler()).thenReturn(scheduler);
        }

        @Test
        void skipsTheDatabaseWhenNothingChanged() {
            // Given - a loaded profile with no changes
            // When - a save runs
            loadedProfile.save(false);

            // Then - the database is never touched
            verifyNoInteractions(databaseManager);
        }

        @Test
        void skipsTheDatabaseForUnloadedProfiles() {
            // Given - a dirty profile whose data never finished loading
            final PlayerProfile unloadedProfile =
                    new PlayerProfile("Herb", UUID.randomUUID(), STARTING_LEVEL);
            unloadedProfile.markProfileDirty();

            // When - a save runs
            unloadedProfile.save(false);

            // Then - the incomplete data is never written over the stored profile
            verifyNoInteractions(databaseManager);
        }

        @Test
        void persistsACopyOnceAndClearsTheDirtyFlag() {
            // Given - a dirty profile and a database accepting the save
            loadedProfile.addLevels(PrimarySkillType.MINING, 5);
            when(databaseManager.saveUser(argThat(saved -> saved.getPlayerName().equals("Herb"))))
                    .thenReturn(true);

            // When - the profile saves twice
            loadedProfile.save(false);
            loadedProfile.save(false);

            // Then - only the first save writes, and the written copy carries the data
            final ArgumentCaptor<PlayerProfile> savedCopy =
                    ArgumentCaptor.forClass(PlayerProfile.class);
            verify(databaseManager).saveUser(savedCopy.capture());
            assertThat(savedCopy.getValue().getSkillLevel(PrimarySkillType.MINING))
                    .isEqualTo(STARTING_LEVEL + 5);
        }

        @Test
        void schedulesAnAsyncRetryWhenTheSaveFails() {
            // Given - a dirty profile and a database rejecting the save
            loadedProfile.markProfileDirty();
            when(databaseManager.saveUser(argThat(saved -> saved.getPlayerName().equals("Herb"))))
                    .thenReturn(false);

            // When - an async save fails
            loadedProfile.save(false);

            // Then - a retry is scheduled off the main thread
            verify(scheduler).runAsync(any(PlayerProfileSaveTask.class));
        }

        @Test
        void retriesSynchronouslyWhenASyncSaveFails() {
            // Given - a dirty profile and a database rejecting the save
            loadedProfile.markProfileDirty();
            when(databaseManager.saveUser(argThat(saved -> saved.getPlayerName().equals("Herb"))))
                    .thenReturn(false);

            // When - a sync save (shutdown path) fails
            loadedProfile.save(true);

            // Then - the retry stays on the next tick instead of going async
            verify(scheduler).runNextTick(any(PlayerProfileSaveTask.class));
            verify(scheduler, never()).runAsync(any(PlayerProfileSaveTask.class));
        }

        /**
         * Regression coverage for the dirty-flag race on async saves: the flag was overwritten
         * unconditionally once the database returned, so a change that landed while the copy
         * was being written was marked clean and silently skipped by later saves.
         */
        @Test
        void changesMadeDuringAnInFlightSaveShouldSurviveForTheNextSave() {
            // Given - a dirty profile and a database write during which another change lands
            loadedProfile.addLevels(PrimarySkillType.MINING, 1);
            when(databaseManager.saveUser(argThat(saved -> saved.getPlayerName().equals("Herb"))))
                    .thenAnswer(invocation -> {
                        loadedProfile.addLevels(PrimarySkillType.MINING, 1);
                        return true;
                    });

            // When - the save completes and a later save runs
            loadedProfile.save(false);
            loadedProfile.save(false);

            // Then - the second save writes the change made during the first save
            final ArgumentCaptor<PlayerProfile> savedCopies =
                    ArgumentCaptor.forClass(PlayerProfile.class);
            verify(databaseManager, times(2)).saveUser(savedCopies.capture());
            assertThat(savedCopies.getAllValues().get(1).getSkillLevel(PrimarySkillType.MINING))
                    .isEqualTo(STARTING_LEVEL + 2);
        }

        @Test
        void givesUpRetryingAfterTenFailedAttempts() {
            // Given - a dirty profile and a database that always rejects the save
            loadedProfile.markProfileDirty();
            when(databaseManager.saveUser(argThat(saved -> saved.getPlayerName().equals("Herb"))))
                    .thenReturn(false);

            // When - the save fails more times than the retry budget allows
            for (int attempt = 0; attempt < 11; attempt++) {
                loadedProfile.save(false);
            }

            // Then - exactly ten retries were scheduled and the eleventh failure gave up
            verify(scheduler, times(10)).runAsync(any(PlayerProfileSaveTask.class));
        }
    }
}
