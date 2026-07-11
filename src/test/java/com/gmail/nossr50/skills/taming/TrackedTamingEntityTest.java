package com.gmail.nossr50.skills.taming;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Covers the Call of the Wild summon lifecycle tracker: finite-lifespan summons schedule
 * their own expiration, permanent summons do not, and an expired summon is killed, its owner
 * informed, and the tracker cleaned.
 */
class TrackedTamingEntityTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(TrackedTamingEntityTest.class.getName());

    private static final int SUMMON_LIFESPAN_SECONDS = 240;

    private PlatformScheduler scheduler;
    private Wolf wolf;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);

        final FoliaLib foliaLib = mock(FoliaLib.class);
        scheduler = mock(PlatformScheduler.class);
        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
        when(foliaLib.getScheduler()).thenReturn(scheduler);

        wolf = mock(Wolf.class);
        when(wolf.getType()).thenReturn(EntityType.WOLF);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private void wireSummonLifespan(int seconds) {
        when(generalConfig.getTamingCOTWLength(
                CallOfTheWildType.WOLF.getConfigEntityTypeEntry())).thenReturn(seconds);
    }

    @Test
    @SuppressWarnings("unchecked")
    void finiteLifespanSummonsShouldScheduleTheirExpiration() {
        // Given - a 240 second summon lifespan
        wireSummonLifespan(SUMMON_LIFESPAN_SECONDS);

        // When - the summon is tracked
        final TrackedTamingEntity trackedWolf =
                new TrackedTamingEntity(wolf, CallOfTheWildType.WOLF, player);

        // Then - the expiration is scheduled in ticks on the summon itself
        verify(scheduler).runAtEntityLater(eq(wolf), eq(trackedWolf),
                eq(SUMMON_LIFESPAN_SECONDS * 20L));
    }

    @Test
    @SuppressWarnings("unchecked")
    void permanentSummonsShouldNotSchedule() {
        // Given - a summon with no configured lifespan
        wireSummonLifespan(0);

        // When - the summon is tracked
        new TrackedTamingEntity(wolf, CallOfTheWildType.WOLF, player);

        // Then - no expiration is scheduled
        verify(scheduler, never()).runAtEntityLater(any(), any(Consumer.class), anyLong());
    }

    @Test
    void expiredSummonsShouldBeKilledAndUntracked() {
        try (final MockedStatic<ParticleEffectUtils> ignored =
                mockStatic(ParticleEffectUtils.class)) {
            // Given - a live tracked summon whose owner is online
            wireSummonLifespan(SUMMON_LIFESPAN_SECONDS);
            when(wolf.isValid()).thenReturn(true);
            when(wolf.getLocation()).thenReturn(new Location(world, 0, 64, 0));
            final TrackedTamingEntity trackedWolf =
                    new TrackedTamingEntity(wolf, CallOfTheWildType.WOLF, player);
            transientEntityTracker.addSummon(playerUUID, trackedWolf);
            assertThat(transientEntityTracker.getActiveSummonsForPlayerOfType(playerUUID,
                    CallOfTheWildType.WOLF)).isEqualTo(1);

            // When - the summon expires
            trackedWolf.run();

            // Then - the wolf is killed and removed
            verify(wolf).setHealth(0);
            verify(wolf).remove();

            // And - the owner is told the summon timed out
            notificationManager.verify(() -> NotificationManager.sendPlayerInformationChatOnly(
                    player, "Taming.Summon.COTW.TimeExpired", "Wolf"));

            // And - the tracker forgets the summon
            assertThat(transientEntityTracker.getActiveSummonsForPlayerOfType(playerUUID,
                    CallOfTheWildType.WOLF)).isZero();
        }
    }
}
