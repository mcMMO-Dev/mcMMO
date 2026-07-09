package com.gmail.nossr50.listeners;

import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.ACROBATICS;
import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.PlayerLevelUtils;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class SelfListenerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(SelfListenerTest.class.getName());

    private MockedStatic<WorldGuardUtils> worldGuardUtilsMock;
    private MockedStatic<WorldGuardManager> worldGuardManagerMock;
    private MockedStatic<PlayerLevelUtils> playerLevelUtilsMock;
    private SelfListener selfListener;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        worldGuardUtilsMock = mockStatic(WorldGuardUtils.class);
        worldGuardManagerMock = mockStatic(WorldGuardManager.class);
        playerLevelUtilsMock = mockStatic(PlayerLevelUtils.class);
        selfListener = new SelfListener(mcMMO.p);
    }

    @AfterEach
    void tearDown() {
        worldGuardUtilsMock.close();
        worldGuardManagerMock.close();
        playerLevelUtilsMock.close();
        cleanUpStaticMocks();
    }

    /**
     * Regression coverage for WorldGuard XP denial: the handler previously kept processing after
     * cancelling the XP event, letting the early game boost hand XP back to the cancelled event.
     */
    @Test
    void worldGuardDeniedXpShouldStayZeroWhenEarlyGameBoostApplies() {
        // Given - the player's profile is fully loaded
        final PlayerProfile loadedProfile = spy(playerProfile);
        doReturn(true).when(loadedProfile).isLoaded();
        doReturn(loadedProfile).when(mmoPlayer).getProfile();

        // And - WorldGuard is loaded and denies the XP flag for the player
        worldGuardUtilsMock.when(WorldGuardUtils::isWorldGuardLoaded).thenReturn(true);
        final WorldGuardManager worldGuardManager = mock(WorldGuardManager.class);
        worldGuardManagerMock.when(WorldGuardManager::getInstance).thenReturn(worldGuardManager);
        when(worldGuardManager.hasXPFlag(player)).thenReturn(false);

        // And - the early game boost would add XP for this low-level player
        when(ExperienceConfig.getInstance().isEarlyGameBoostEnabled()).thenReturn(true);
        playerLevelUtilsMock.when(
                () -> PlayerLevelUtils.qualifiesForEarlyGameBoost(mmoPlayer, ACROBATICS))
                .thenReturn(true);
        doReturn(1000).when(mmoPlayer).getXpToLevel(ACROBATICS);

        // And - a PVE XP gain that WorldGuard should fully deny
        final McMMOPlayerXpGainEvent event = new McMMOPlayerXpGainEvent(player, ACROBATICS, 10F,
                XPGainReason.PVE);

        // When - the XP gain is handled
        selfListener.onPlayerXpGain(event);

        // Then - the event stays cancelled with zero XP, nothing re-adds XP afterwards
        assertThat(event.isCancelled()).isTrue();
        assertThat(event.getRawXpGained()).isZero();
    }
}
