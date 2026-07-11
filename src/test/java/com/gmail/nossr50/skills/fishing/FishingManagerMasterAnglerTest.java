package com.gmail.nossr50.skills.fishing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerMasterAnglerEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.RankUtils;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

/**
 * Covers the Master Angler wait-time math: the per-rank and boat reductions, the configured
 * lower bounds that keep fish from biting instantly, and the full reduction pipeline applied
 * to a fish hook including the lure conversion that works around the vanilla lure bug.
 */
class FishingManagerMasterAnglerTest extends MMOTestEnvironment {
    private static final Logger LOGGER =
            Logger.getLogger(FishingManagerMasterAnglerTest.class.getName());

    private FishingManager fishingManager;
    private FishHook hook;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(LOGGER);

        when(advancedConfig.getFishingReductionMinWaitCap()).thenReturn(40);
        when(advancedConfig.getFishingReductionMaxWaitCap()).thenReturn(100);
        when(advancedConfig.getFishingReductionMinWaitTicks()).thenReturn(10);
        when(advancedConfig.getFishingReductionMaxWaitTicks()).thenReturn(30);
        when(advancedConfig.getFishingBoatReductionMinWaitTicks()).thenReturn(5);
        when(advancedConfig.getFishingBoatReductionMaxWaitTicks()).thenReturn(15);

        fishingManager = new FishingManager(mmoPlayer);
        hook = mock(FishHook.class);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * Bad configs must not produce negative waits or a max wait below the min wait; the min
     * bound clamps to zero and the max bound always sits at least 40 ticks above the min bound.
     */
    @ParameterizedTest
    @CsvSource({
            "-10, 20, 0, 40",
            "0, 0, 0, 40",
            "60, 80, 60, 100",
            "60, 150, 60, 150",
    })
    void waitLowerBoundsShouldClampBadConfigValues(int minWaitCap, int maxWaitCap,
            int expectedMinBound, int expectedMaxBound) {
        // Given - configured wait caps that may be invalid
        when(advancedConfig.getFishingReductionMinWaitCap()).thenReturn(minWaitCap);
        when(advancedConfig.getFishingReductionMaxWaitCap()).thenReturn(maxWaitCap);

        // When - the manager derives its wait lower bounds
        final FishingManager manager = new FishingManager(mmoPlayer);

        // Then - the bounds are clamped to safe values
        assertThat(manager.getMasterAnglerMinWaitLowerBound()).isEqualTo(expectedMinBound);
        assertThat(manager.getMasterAnglerMaxWaitLowerBound()).isEqualTo(expectedMaxBound);
    }

    @ParameterizedTest
    @CsvSource({
            "100, 30, 20, 70",
            "100, 80, 20, 20",
            "100, 95, 20, 20",
            "50, 0, 20, 50",
    })
    void getReducedTicksShouldNeverDropBelowTheBound(int ticks, int bonus, int bound,
            int expected) {
        // Given - a wait time, a reduction bonus, and a lower bound
        // When - the reduction is applied
        // Then - the result never drops below the bound
        assertThat(fishingManager.getReducedTicks(ticks, bonus, bound)).isEqualTo(expected);
    }

    @Test
    void minWaitReductionShouldScaleWithRankAndBoatBonus() {
        // Given - 10 ticks of reduction per rank and a 5 tick boat bonus
        // When/Then - rank 3 reduces 30 ticks on foot and 35 from a boat
        assertThat(fishingManager.getMasterAnglerTickMinWaitReduction(3, false)).isEqualTo(30);
        assertThat(fishingManager.getMasterAnglerTickMinWaitReduction(3, true)).isEqualTo(35);
    }

    @Test
    void maxWaitReductionShouldStackRankBoatAndLureBonuses() {
        // Given - 30 ticks of reduction per rank, a 15 tick boat bonus, and a converted lure
        // bonus of 200 ticks
        // When/Then - the bonuses stack additively
        assertThat(fishingManager.getMasterAnglerTickMaxWaitReduction(3, false, 0)).isEqualTo(90);
        assertThat(fishingManager.getMasterAnglerTickMaxWaitReduction(3, true, 200))
                .isEqualTo(305);
    }

    @Test
    void isInBoatShouldRequireABoatVehicle() {
        // Given - a player on foot
        // Then - no boat bonus
        assertThat(fishingManager.isInBoat()).isFalse();

        // When - the player rides something that is not a boat
        when(player.isInsideVehicle()).thenReturn(true);
        when(player.getVehicle()).thenReturn(mock(Entity.class));
        // Then - still no boat bonus
        assertThat(fishingManager.isInBoat()).isFalse();

        // When - the player rides a boat
        when(player.getVehicle()).thenReturn(mock(Boat.class));
        // Then - the boat bonus applies
        assertThat(fishingManager.isInBoat()).isTrue();
    }

    @Nested
    class ProcessMasterAngler {
        @BeforeEach
        void setUpHookAndRank() {
            when(RankUtils.getRank(mmoPlayer, SubSkillType.FISHING_MASTER_ANGLER)).thenReturn(2);
            when(hook.getMaxWaitTime()).thenReturn(600);
            when(hook.getMinWaitTime()).thenReturn(200);
        }

        @Test
        void reducesTheHookWaitTimesByTheRankBonuses() {
            // Given - rank 2 grants a 20 tick min reduction and a 60 tick max reduction
            // When - master angler processes a hook waiting 200 to 600 ticks
            fishingManager.processMasterAngler(hook, 0);

            // Then - the hook waits are reduced and vanilla lure handling is left alone
            verify(hook).setMinWaitTime(180);
            verify(hook).setMaxWaitTime(540);
            verify(hook, never()).setApplyLure(anyBoolean());
        }

        @Test
        void lureConvertsToAWaitBonusAndDisablesVanillaLure() {
            // Given - a lure 2 rod, which vanilla mishandles above level 3 when stacked
            // When - master angler processes the hook
            fishingManager.processMasterAngler(hook, 2);

            // Then - vanilla lure is disabled and replaced with a 100 ticks per level bonus
            verify(hook).setApplyLure(false);
            verify(hook).setMinWaitTime(180);
            verify(hook).setMaxWaitTime(340);
        }

        @Test
        void maxWaitIsCorrectedWhenItFallsBelowTheMinWait() {
            // Given - hook waits so close together the reductions would cross them over
            when(hook.getMaxWaitTime()).thenReturn(300);
            when(hook.getMinWaitTime()).thenReturn(290);

            // When - master angler processes the hook
            fishingManager.processMasterAngler(hook, 0);

            // Then - the max wait is corrected to sit 100 ticks above the min wait
            verify(hook).setMinWaitTime(270);
            verify(hook).setMaxWaitTime(370);
        }

        /**
         * Covers the masterAngler entry point end to end: the reduction must not run inline
         * (vanilla applies its lure bonus after the event, so an immediate adjustment would
         * read pre-lure wait times) and the deferred task must carry the lure level through
         * to the reduction.
         */
        @Test
        void masterAnglerRunsTheLureAwareReductionAtTheHookOneTickLater() {
            // Given - a captured scheduler so the deferred task can be run by hand
            final FoliaLib foliaLib = mock(FoliaLib.class);
            final PlatformScheduler scheduler = mock(PlatformScheduler.class);
            when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
            when(foliaLib.getScheduler()).thenReturn(scheduler);

            // When - master angler triggers for a cast with a lure 2 rod
            fishingManager.masterAngler(hook, 2);

            // Then - the reduction is deferred one tick and has not touched the hook yet
            @SuppressWarnings("unchecked")
            final ArgumentCaptor<Consumer<WrappedTask>> scheduledReduction =
                    ArgumentCaptor.forClass(Consumer.class);
            verify(scheduler).runAtEntityLater(eq(hook), scheduledReduction.capture(), eq(1L));
            verify(hook, never()).setMaxWaitTime(anyInt());

            // And - running the scheduled task applies the lure converted reduction
            scheduledReduction.getValue().accept(mock(WrappedTask.class));
            verify(hook).setApplyLure(false);
            verify(hook).setMinWaitTime(180);
            verify(hook).setMaxWaitTime(340);
        }

        @Test
        void cancelledEventLeavesTheHookUntouched() {
            // Given - another plugin cancels the master angler event
            doAnswer(invocation -> {
                final Event event = invocation.getArgument(0);
                if (event instanceof McMMOPlayerMasterAnglerEvent anglerEvent) {
                    anglerEvent.setCancelled(true);
                }
                return null;
            }).when(pluginManager).callEvent(any(Event.class));

            // When - master angler processes the hook
            fishingManager.processMasterAngler(hook, 0);

            // Then - the hook wait times are not modified
            verify(hook, never()).setMinWaitTime(anyInt());
            verify(hook, never()).setMaxWaitTime(anyInt());
        }
    }
}
