package com.gmail.nossr50.runnables.database;

import static com.gmail.nossr50.database.UsernamePlaceholder.INVALID_OLD_USERNAME;
import static com.gmail.nossr50.database.UsernamePlaceholder.isInvalidOldUsername;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.database.PlayerNameAndUUID;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.player.UserManager;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * /mcconvert experience converts every stored player once. Everyone who lost their name to
 * another player shares the placeholder, so only their UUID finds them.
 */
class FormulaConversionTaskTest extends MMOTestEnvironment {
    private static final Logger logger =
            Logger.getLogger(FormulaConversionTaskTest.class.getName());
    private static final String LEGACY_FLATFILE_PLACEHOLDER = INVALID_OLD_USERNAME + "'";
    private static final int MINING_LEVEL = 10;
    private static final int TOTAL_MINING_XP = 1000;
    private static final int CONVERTED_MINING_LEVEL = 12;

    private DatabaseManager databaseManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        databaseManager = mock(DatabaseManager.class);
        mockedMcMMO.when(mcMMO::getDatabaseManager).thenReturn(databaseManager);

        // Only a mining level of MINING_LEVEL has XP to convert, so a second conversion of the
        // same player would find nothing left to change
        final FormulaManager formulaManager = mock(FormulaManager.class);
        mockedMcMMO.when(mcMMO::getFormulaManager).thenReturn(formulaManager);
        when(experienceConfigInstance.getExpModifier()).thenReturn(1.0);
        when(formulaManager.calculateTotalExperience(MINING_LEVEL, 0)).thenReturn(TOTAL_MINING_XP);
        when(formulaManager.calculateNewLevel(PrimarySkillType.MINING, TOTAL_MINING_XP,
                FormulaType.EXPONENTIAL)).thenReturn(new int[]{CONVERTED_MINING_LEVEL, 0});
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * Converting by name found the first player under the placeholder once for each player who
     * lost their name, converting that one repeatedly and the others never.
     */
    @Test
    void conversionShouldConvertEachStoredPlayerOnce() {
        // Given - players who lost their names, two under the same spelling
        final List<PlayerProfile> storedProfiles = List.of(
                storedPlayer(INVALID_OLD_USERNAME, randomUUID()),
                storedPlayer(LEGACY_FLATFILE_PLACEHOLDER, randomUUID()),
                storedPlayer(INVALID_OLD_USERNAME, randomUUID()),
                storedPlayer("nossr50", randomUUID()),
                storedPlayer("stored_before_uuids", null));
        final List<PlayerNameAndUUID> storedUsers = storedProfiles.stream()
                .map(profile -> new PlayerNameAndUUID(profile.getPlayerName(),
                        profile.getUniqueId()))
                .toList();
        when(databaseManager.getStoredUsersWithUUIDs()).thenReturn(storedUsers);
        when(databaseManager.getStoredUsers()).thenReturn(
                storedUsers.stream().map(PlayerNameAndUUID::playerName).toList());

        // When - the stored players are converted to another XP formula
        new FormulaConversionTask(player, FormulaType.EXPONENTIAL).run();

        // Then - each of them is converted and saved once
        assertThat(storedProfiles).allSatisfy(profile -> {
            assertThat(profile.getSkillLevel(PrimarySkillType.MINING))
                    .isEqualTo(CONVERTED_MINING_LEVEL);
            verify(profile).scheduleAsyncSave();
        });
    }

    /**
     * Nothing tells apart players who lost their names before mcMMO kept UUIDs. A database
     * manager from another plugin may still find someone by the placeholder, so it is never
     * looked up.
     */
    @Test
    void conversionShouldSkipAPlayerUnderThePlaceholderWithoutAUuid() {
        // Given - a player who lost their name before mcMMO kept UUIDs
        when(databaseManager.getStoredUsersWithUUIDs()).thenReturn(
                List.of(new PlayerNameAndUUID(INVALID_OLD_USERNAME, null)));

        // When - the stored players are converted to another XP formula
        new FormulaConversionTask(player, FormulaType.EXPONENTIAL).run();

        // Then - no one is looked up by the placeholder
        verify(databaseManager, never()).loadPlayerProfile(INVALID_OLD_USERNAME);
    }

    /** An online player's loaded profile is converted in place, and saved as they play. */
    @Test
    void conversionShouldConvertAnOnlinePlayerFoundByUuid() {
        // Given - an online player, stored with their UUID
        playerProfile.modifySkill(PrimarySkillType.MINING, MINING_LEVEL);
        when(server.getPlayer(playerUUID)).thenReturn(player);
        when(databaseManager.getStoredUsersWithUUIDs()).thenReturn(
                List.of(new PlayerNameAndUUID(playerProfile.getPlayerName(), playerUUID)));

        // When - the stored players are converted to another XP formula
        new FormulaConversionTask(player, FormulaType.EXPONENTIAL).run();

        // Then - their loaded profile is converted, and nothing is loaded from the database
        assertThat(playerProfile.getSkillLevel(PrimarySkillType.MINING))
                .isEqualTo(CONVERTED_MINING_LEVEL);
        verify(databaseManager, never()).loadPlayerProfile(playerUUID);
    }

    /** Without a UUID, an online player is found by the name they are stored under. */
    @Test
    void conversionShouldConvertAnOnlinePlayerStoredWithoutAUuid() {
        // Given - an online player, stored before mcMMO kept UUIDs
        playerProfile.modifySkill(PrimarySkillType.MINING, MINING_LEVEL);
        final String playerName = playerProfile.getPlayerName();
        when(UserManager.getOfflinePlayer(playerName)).thenReturn(mmoPlayer);
        when(databaseManager.getStoredUsersWithUUIDs()).thenReturn(
                List.of(new PlayerNameAndUUID(playerName, null)));

        // When - the stored players are converted to another XP formula
        new FormulaConversionTask(player, FormulaType.EXPONENTIAL).run();

        // Then - their loaded profile is converted, and nothing is loaded from the database
        assertThat(playerProfile.getSkillLevel(PrimarySkillType.MINING))
                .isEqualTo(CONVERTED_MINING_LEVEL);
        verify(databaseManager, never()).loadPlayerProfile(playerName);
    }

    /** A stored player, found the way the database finds them now. */
    private PlayerProfile storedPlayer(String playerName, @Nullable UUID uuid) {
        final PlayerProfile profile = spy(new PlayerProfile(playerName, uuid, true, 0));
        profile.modifySkill(PrimarySkillType.MINING, MINING_LEVEL);
        doNothing().when(profile).scheduleAsyncSave();

        if (uuid != null) {
            when(databaseManager.loadPlayerProfile(uuid)).thenReturn(profile);
        }

        // The placeholder is nobody's name, so a lookup by it finds no one
        final PlayerProfile foundByName = isInvalidOldUsername(playerName)
                ? new PlayerProfile(playerName, uuid, false, 0) : profile;
        when(databaseManager.loadPlayerProfile(playerName)).thenReturn(foundByName);
        return profile;
    }
}
