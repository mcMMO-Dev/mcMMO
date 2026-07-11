package com.gmail.nossr50.listeners;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.TestRegistryBootstrap;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Covers the non-exploit fishing flow of {@link PlayerListener}: the vanilla treasure
 * override, the vanilla XP boost, the Shake wiring for reeled-in entities, the Master Angler
 * cast wiring that feeds the rod's Lure level into the scheduler, and the Ice Fishing
 * wiring including the cancel-and-refund of the fish event. Events are dispatched through
 * every {@link PlayerFishEvent} handler honoring their real {@link EventHandler} settings,
 * because the ice fishing handler cancels the event and later handlers must not run on it.
 * Exploit prevention is disabled throughout; {@link PlayerListenerFishingExploitTest} covers
 * that side.
 */
class PlayerListenerFishingTest extends MMOTestEnvironment {
    private static final Logger logger =
            getLogger(PlayerListenerFishingTest.class.getName());

    private static final int VANILLA_CATCH_XP = 5;

    private PlayerListener playerListener;
    private FishingManager fishingManager;
    private FishHook hook;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        playerListener = new PlayerListener(mcMMO.p);

        when(UserManager.hasPlayerDataKey(player)).thenReturn(true);
        when(Permissions.skillEnabled(player, PrimarySkillType.FISHING)).thenReturn(true);
        when(ExperienceConfig.getInstance().isFishingExploitingPrevented()).thenReturn(false);

        hook = mock(FishHook.class);

        fishingManager = Mockito.spy(new FishingManager(mmoPlayer));
        doReturn(fishingManager).when(mmoPlayer).getFishingManager();
        doNothing().when(fishingManager).processFishing(any(), any());
        doNothing().when(fishingManager).setFishingTarget();
        doNothing().when(fishingManager).shakeCheck(any());
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private Item mockCaughtItem(Material material) {
        final Item caughtItem = mock(Item.class);
        final ItemStack caughtStack = mock(ItemStack.class);
        when(caughtStack.getType()).thenReturn(material);
        when(caughtItem.getItemStack()).thenReturn(caughtStack);
        return caughtItem;
    }

    private PlayerFishEvent dispatchFishEvent(Entity caught, PlayerFishEvent.State state) {
        return dispatchFishEvent(caught, state, EquipmentSlot.HAND);
    }

    private PlayerFishEvent dispatchFishEvent(Entity caught, PlayerFishEvent.State state,
            EquipmentSlot hand) {
        final PlayerFishEvent event = new PlayerFishEvent(player, caught, hook, hand, state);
        event.setExpToDrop(VANILLA_CATCH_XP);
        dispatchLikeEventBus(event);
        return event;
    }

    /**
     * Mirrors the Bukkit event bus contract for the listener's fish handlers: handlers run in
     * priority order and handlers registered with ignoreCancelled are skipped once the event
     * is cancelled.
     */
    private void dispatchLikeEventBus(PlayerFishEvent event) {
        final List<Method> handlers = Arrays.stream(PlayerListener.class.getMethods())
                .filter(method -> method.isAnnotationPresent(EventHandler.class))
                .filter(method -> method.getParameterCount() == 1
                        && method.getParameterTypes()[0] == PlayerFishEvent.class)
                .sorted(Comparator.comparing(
                        method -> method.getAnnotation(EventHandler.class).priority()))
                .toList();

        for (final Method handler : handlers) {
            if (handler.getAnnotation(EventHandler.class).ignoreCancelled()
                    && event.isCancelled()) {
                continue;
            }

            try {
                handler.invoke(playerListener, event);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError("Failed to dispatch to " + handler.getName(), e);
            }
        }
    }

    @Nested
    class VanillaTreasureOverride {
        @BeforeEach
        void enableOverride() {
            when(generalConfig.getFishingOverrideTreasures()).thenReturn(true);
        }

        @Test
        void replacesVanillaTreasureCatchesWithSalmon() {
            // Given - the override is enabled and the catch is vanilla treasure, not a fish
            final Item caughtItem = mockCaughtItem(Material.BOW);

            // When - the catch lands
            dispatchFishEvent(caughtItem, PlayerFishEvent.State.CAUGHT_FISH);

            // Then - the catch is replaced with a salmon so mcMMO's treasure tables stay in
            // charge of treasure drops
            verify(caughtItem).setItemStack(
                    argThat(replacement -> replacement.getType() == Material.SALMON));
        }

        @Test
        void leavesVanillaFishCatchesAlone() {
            // Given - the override is enabled and the catch is an ordinary fish
            final Item caughtItem = mockCaughtItem(Material.COD);

            // When - the catch lands
            dispatchFishEvent(caughtItem, PlayerFishEvent.State.CAUGHT_FISH);

            // Then - the fish is kept as caught
            verify(caughtItem, never()).setItemStack(any());
        }

        @Test
        void leavesCatchesAloneWhenTheOverrideIsDisabled() {
            // Given - the override is disabled
            when(generalConfig.getFishingOverrideTreasures()).thenReturn(false);
            final Item caughtItem = mockCaughtItem(Material.BOW);

            // When - the catch lands
            dispatchFishEvent(caughtItem, PlayerFishEvent.State.CAUGHT_FISH);

            // Then - the vanilla treasure is kept as caught
            verify(caughtItem, never()).setItemStack(any());
        }
    }

    @Nested
    class VanillaXpBoost {
        @Test
        void boostsTheEventXpForPermittedPlayers() {
            // Given - a player with the vanilla XP boost perk at loot tier 3 with a x4 modifier
            when(Permissions.vanillaXpBoost(player, PrimarySkillType.FISHING)).thenReturn(true);
            when(RankUtils.getRank(player, SubSkillType.FISHING_TREASURE_HUNTER)).thenReturn(3);
            when(advancedConfig.getFishingVanillaXPModifier(3)).thenReturn(4);

            // When - a catch worth 5 vanilla XP lands
            final PlayerFishEvent event =
                    dispatchFishEvent(mockCaughtItem(Material.COD),
                            PlayerFishEvent.State.CAUGHT_FISH);

            // Then - the vanilla XP is scaled by the modifier
            assertThat(event.getExpToDrop()).isEqualTo(VANILLA_CATCH_XP * 4);
        }

        @Test
        void leavesTheEventXpAloneWithoutThePerk() {
            // Given - a player without the vanilla XP boost perk
            // When - a catch lands
            final PlayerFishEvent event =
                    dispatchFishEvent(mockCaughtItem(Material.COD),
                            PlayerFishEvent.State.CAUGHT_FISH);

            // Then - the vanilla XP is untouched
            assertThat(event.getExpToDrop()).isEqualTo(VANILLA_CATCH_XP);
        }
    }

    @Nested
    class ShakeWiring {
        @Test
        void reeledInEntitiesAreShakenWhenShakeIsUnlocked() {
            // Given - a player with Shake unlocked reeling in a mob
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.FISHING_SHAKE))
                    .thenReturn(true);
            final LivingEntity target = mock(LivingEntity.class);

            // When - the entity is reeled in
            dispatchFishEvent(target, PlayerFishEvent.State.CAUGHT_ENTITY);

            // Then - the shake check runs against the reeled-in mob
            verify(fishingManager).shakeCheck(target);
            verify(fishingManager).setFishingTarget();
        }

        @Test
        void reeledInEntitiesAreLeftAloneWhileShakeIsLocked() {
            // Given - a player who has not unlocked Shake
            final LivingEntity target = mock(LivingEntity.class);

            // When - the entity is reeled in
            dispatchFishEvent(target, PlayerFishEvent.State.CAUGHT_ENTITY);

            // Then - no shake check runs
            verify(fishingManager, never()).shakeCheck(any());
        }
    }

    @Nested
    class IceFishingWiring {
        private Block targetBlock;

        @BeforeEach
        void setUpTargetBlock() {
            targetBlock = mock(Block.class);
            when(player.getTargetBlock(null, 100)).thenReturn(targetBlock);
        }

        @Test
        void iceFishingCancelsTheEventRefundsTheXpAndRecasts() {
            // Given - the hook landed in ground that qualifies for ice fishing
            doReturn(true).when(fishingManager).canIceFish(targetBlock);
            doNothing().when(fishingManager).iceFishing(any(), any(), any());

            final Location eyeLocation = mock(Location.class);
            when(player.getEyeLocation()).thenReturn(eyeLocation);
            when(player.getWorld()).thenReturn(world);
            final ExperienceOrb experienceOrb = mock(ExperienceOrb.class);
            when(world.spawnEntity(eyeLocation, EntityType.EXPERIENCE_ORB))
                    .thenReturn(experienceOrb);

            // When - the fish event reports the hook stuck in ground
            final PlayerFishEvent event =
                    dispatchFishEvent(null, PlayerFishEvent.State.IN_GROUND);

            // Then - the event is cancelled, the vanilla XP drops as an orb instead, and the
            // hole is opened with a fresh cast
            assertThat(event.isCancelled()).isTrue();
            verify(experienceOrb).setExperience(VANILLA_CATCH_XP);
            verify(fishingManager).iceFishing(hook, targetBlock, EquipmentSlot.HAND);
        }

        @Test
        void ordinaryGroundLeavesTheEventAlone() {
            // Given - the hook landed in ground that does not qualify for ice fishing
            // When - the fish event reports the hook stuck in ground
            final PlayerFishEvent event =
                    dispatchFishEvent(null, PlayerFishEvent.State.IN_GROUND);

            // Then - the event continues as vanilla
            assertThat(event.isCancelled()).isFalse();
            verify(fishingManager, never()).iceFishing(any(), any(), any());
        }
    }

    /**
     * The cast wiring reads the rod's vanilla Lure level off the event hand and hands it to
     * Master Angler, which converts it into a wait-time bonus (vanilla mishandles lure above
     * level 3 when stacked with Master Angler's own reductions).
     */
    @Nested
    class MasterAnglerCastWiring {
        private ItemStack rod;

        @BeforeEach
        void setUpRod() {
            // Enchantment.LURE resolves through the registry during Enchantment class init
            TestRegistryBootstrap.bootstrap(mockedBukkit);

            rod = mock(ItemStack.class);
            when(rod.getType()).thenReturn(Material.FISHING_ROD);

            doReturn(true).when(fishingManager).canMasterAngler();
            doNothing().when(fishingManager).masterAngler(any(FishHook.class), anyInt());
        }

        @Test
        void castsFeedTheRodsLureLevelIntoMasterAngler() {
            // Given - a Lure II rod in the casting hand
            when(playerInventory.getItemInMainHand()).thenReturn(rod);
            when(rod.getEnchantmentLevel(Enchantment.LURE)).thenReturn(2);

            // When - the player casts
            dispatchFishEvent(null, PlayerFishEvent.State.FISHING);

            // Then - master angler is scheduled with the rod's lure level
            verify(fishingManager).masterAngler(hook, 2);
            verify(fishingManager).setFishingTarget();
        }

        @Test
        void offHandCastsReadTheOffHandRod() {
            // Given - a Lure III rod in the off hand
            when(playerInventory.getItemInOffHand()).thenReturn(rod);
            when(rod.getEnchantmentLevel(Enchantment.LURE)).thenReturn(3);

            // When - the player casts with the off hand
            dispatchFishEvent(null, PlayerFishEvent.State.FISHING, EquipmentSlot.OFF_HAND);

            // Then - the off hand rod's lure level reaches master angler
            verify(fishingManager).masterAngler(hook, 3);
        }

        @Test
        void castsWithoutARodDoNotTriggerMasterAngler() {
            // Given - the casting hand holds something that is not a fishing rod
            final ItemStack stick = mock(ItemStack.class);
            when(stick.getType()).thenReturn(Material.STICK);
            when(playerInventory.getItemInMainHand()).thenReturn(stick);

            // When - the fish event fires for the cast
            dispatchFishEvent(null, PlayerFishEvent.State.FISHING);

            // Then - master angler stays out of it
            verify(fishingManager, never()).masterAngler(any(FishHook.class), anyInt());
        }

        @Test
        void castsAreIgnoredWhileMasterAnglerIsLocked() {
            // Given - the player has not unlocked master angler
            doReturn(false).when(fishingManager).canMasterAngler();
            when(playerInventory.getItemInMainHand()).thenReturn(rod);

            // When - the player casts
            dispatchFishEvent(null, PlayerFishEvent.State.FISHING);

            // Then - no master angler scheduling happens
            verify(fishingManager, never()).masterAngler(any(FishHook.class), anyInt());
        }
    }
}
