package com.gmail.nossr50.skills.fishing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerShakeEvent;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Covers the Shake ability: the RNG gate, drop selection, the per-mob special cases (sheep
 * shearing, player heads, inventory stealing), the capped shake damage, and the XP payout.
 */
class FishingManagerShakeTest extends MMOTestEnvironment {
    private static final Logger LOGGER =
            Logger.getLogger(FishingManagerShakeTest.class.getName());

    private static final int SHAKE_XP = 50;

    private FishingManager fishingManager;
    private FishingTreasureConfig treasureConfig;
    private MockedStatic<FishingTreasureConfig> mockedTreasureConfig;
    private ItemStack shakeDrop;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(LOGGER);

        fishingManager = Mockito.spy(new FishingManager(mmoPlayer));
        doNothing().when(fishingManager)
                .applyXpGain(anyFloat(), any(XPGainReason.class), any(XPGainSource.class));

        mockedTreasureConfig = mockStatic(FishingTreasureConfig.class);
        treasureConfig = mock(FishingTreasureConfig.class);
        when(FishingTreasureConfig.getInstance()).thenReturn(treasureConfig);
        treasureConfig.shakeMap = new HashMap<>();

        when(ExperienceConfig.getInstance().getFishingShakeXP()).thenReturn(SHAKE_XP);

        // A mocked Random rolls 0 everywhere, so the first configured drop is always chosen
        when(Misc.getRandom()).thenReturn(mock(Random.class));
    }

    @AfterEach
    void tearDown() {
        if (mockedTreasureConfig != null) {
            mockedTreasureConfig.close();
        }
        cleanUpStaticMocks();
    }

    private void configureShakeDrop(EntityType entityType, Material dropMaterial) {
        shakeDrop = mock(ItemStack.class);
        when(shakeDrop.getType()).thenReturn(dropMaterial);
        when(shakeDrop.clone()).thenReturn(shakeDrop);
        treasureConfig.shakeMap.put(entityType,
                List.of(new ShakeTreasure(shakeDrop, 5, 100.0, 0)));
    }

    private <T extends LivingEntity> T mockShakeTarget(Class<T> entityClass,
            EntityType entityType, double maxHealth) {
        final T target = mock(entityClass);
        when(target.getType()).thenReturn(entityType);
        when(target.getMaxHealth()).thenReturn(maxHealth);
        when(target.getLocation()).thenReturn(mock(Location.class));
        return target;
    }

    private void stubSuccessfulShakeRoll(MockedStatic<ProbabilityUtil> probabilityUtil) {
        probabilityUtil.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                eq(PrimarySkillType.FISHING), any(McMMOPlayer.class), anyDouble()))
                .thenReturn(true);
    }

    @Test
    void failedShakeRollShouldDropNothing() {
        // Given - a shakeable mob with configured drops
        configureShakeDrop(EntityType.COW, Material.LEATHER);
        final LivingEntity target = mockShakeTarget(LivingEntity.class, EntityType.COW, 8);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class)) {
            // When - the shake roll fails (mocked RNG defaults to false)
            fishingManager.shakeCheck(target);
        }

        // Then - no XP is paid out
        verify(fishingManager, never()).applyXpGain(anyFloat(), any(), any());
    }

    @Test
    void mobWithoutConfiguredDropsShouldDropNothing() {
        // Given - a mob with no shake drops configured
        final LivingEntity target = mockShakeTarget(LivingEntity.class, EntityType.COW, 8);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class)) {
            stubSuccessfulShakeRoll(probabilityUtil);

            // When - the shake roll succeeds anyway
            fishingManager.shakeCheck(target);
        }

        // Then - no XP is paid out
        verify(fishingManager, never()).applyXpGain(anyFloat(), any(), any());
    }

    /**
     * Shake damage is a quarter of the mob's max health, floored at 1 and capped at 10 so a
     * mob can be shaken about four times but bosses are not one-shot farms.
     */
    @ParameterizedTest
    @CsvSource({
            "2, 1",
            "8, 2",
            "60, 10",
    })
    void successfulShakeShouldSpawnTheDropDamageTheMobAndPayXp(double maxHealth,
            double expectedDamage) {
        // Given - a shakeable mob with a guaranteed drop
        configureShakeDrop(EntityType.COW, Material.LEATHER);
        final LivingEntity target =
                mockShakeTarget(LivingEntity.class, EntityType.COW, maxHealth);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class);
                MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class);
                MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
            stubSuccessfulShakeRoll(probabilityUtil);

            // When - the shake roll succeeds
            fishingManager.shakeCheck(target);

            // Then - the drop spawns at the mob, the mob takes capped damage, and XP is paid
            itemUtils.verify(() -> ItemUtils.spawnItem(eq(player), any(Location.class),
                    eq(shakeDrop), eq(ItemSpawnReason.FISHING_SHAKE_TREASURE)));
            combatUtils.verify(() -> CombatUtils.safeDealDamage(target, expectedDamage, player));
        }

        verify(fishingManager).applyXpGain(SHAKE_XP, XPGainReason.PVE, XPGainSource.SELF);
    }

    @Test
    void shakingWoolFromAShearedSheepShouldDropNothing() {
        // Given - a sheared sheep whose configured drop is wool
        configureShakeDrop(EntityType.SHEEP, Material.WHITE_WOOL);
        final Sheep sheep = mockShakeTarget(Sheep.class, EntityType.SHEEP, 8);
        when(sheep.isSheared()).thenReturn(true);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class);
                MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class);
                MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
            stubSuccessfulShakeRoll(probabilityUtil);

            // When - the shake roll succeeds
            fishingManager.shakeCheck(sheep);

            // Then - the sheep has no wool to give, so nothing drops and no XP is paid
            itemUtils.verify(() -> ItemUtils.spawnItem(any(), any(), any(), any()), never());
        }

        verify(fishingManager, never()).applyXpGain(anyFloat(), any(), any());
    }

    @Test
    void shakingWoolFromAnUnshearedSheepShouldShearIt() {
        // Given - an unsheared sheep whose configured drop is wool
        configureShakeDrop(EntityType.SHEEP, Material.WHITE_WOOL);
        final Sheep sheep = mockShakeTarget(Sheep.class, EntityType.SHEEP, 8);
        when(sheep.isSheared()).thenReturn(false);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class);
                MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class);
                MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
            stubSuccessfulShakeRoll(probabilityUtil);

            // When - the shake roll succeeds
            fishingManager.shakeCheck(sheep);

            // Then - the wool drops and the sheep is marked sheared so it cannot be farmed
            verify(sheep).setSheared(true);
            itemUtils.verify(() -> ItemUtils.spawnItem(eq(player), any(Location.class),
                    eq(shakeDrop), eq(ItemSpawnReason.FISHING_SHAKE_TREASURE)));
        }
    }

    @Test
    void playerHeadDropShouldBeOwnedByTheHookedPlayer() {
        // Given - a hooked player whose configured drop is a player head
        configureShakeDrop(EntityType.PLAYER, Material.PLAYER_HEAD);
        final Player targetPlayer = mockShakeTarget(Player.class, EntityType.PLAYER, 20);
        final SkullMeta skullMeta = mock(SkullMeta.class);
        when(shakeDrop.getItemMeta()).thenReturn(skullMeta);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class);
                MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class);
                MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
            stubSuccessfulShakeRoll(probabilityUtil);

            // When - the shake roll succeeds
            fishingManager.shakeCheck(targetPlayer);

            // Then - the dropped head belongs to the hooked player
            verify(skullMeta).setOwningPlayer(targetPlayer);
            verify(shakeDrop).setItemMeta(skullMeta);
            itemUtils.verify(() -> ItemUtils.spawnItem(eq(player), any(Location.class),
                    eq(shakeDrop), eq(ItemSpawnReason.FISHING_SHAKE_TREASURE)));
        }
    }

    @Test
    void shakeEventShouldBeFiredForOtherPlugins() {
        // Given - a shakeable mob with a guaranteed drop
        configureShakeDrop(EntityType.COW, Material.LEATHER);
        final LivingEntity target = mockShakeTarget(LivingEntity.class, EntityType.COW, 8);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class);
                MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class);
                MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
            stubSuccessfulShakeRoll(probabilityUtil);

            // When - the shake roll succeeds
            fishingManager.shakeCheck(target);
        }

        // Then - the shake event is dispatched so other plugins can react to it
        verify(pluginManager).callEvent(any(McMMOPlayerShakeEvent.class));
    }

    @Test
    void cancelledShakeEventShouldDropNothing() {
        // Given - another plugin cancels the shake event
        configureShakeDrop(EntityType.COW, Material.LEATHER);
        final LivingEntity target = mockShakeTarget(LivingEntity.class, EntityType.COW, 8);
        doAnswer(invocation -> {
            final Event event = invocation.getArgument(0);
            if (event instanceof McMMOPlayerShakeEvent shakeEvent) {
                shakeEvent.setCancelled(true);
            }
            return null;
        }).when(pluginManager).callEvent(any(Event.class));

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class);
                MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class);
                MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
            stubSuccessfulShakeRoll(probabilityUtil);

            // When - the shake roll succeeds
            fishingManager.shakeCheck(target);

            // Then - nothing drops, the mob takes no damage, and no XP is paid
            itemUtils.verify(() -> ItemUtils.spawnItem(any(), any(), any(), any()), never());
            combatUtils.verify(() -> CombatUtils.safeDealDamage(any(), anyDouble(), any()),
                    never());
        }

        verify(fishingManager, never()).applyXpGain(anyFloat(), any(), any());
    }

    @Test
    void dropReplacedThroughTheShakeEventShouldBeTheOneThatSpawns() {
        // Given - another plugin swaps the shaken drop through the event
        configureShakeDrop(EntityType.COW, Material.LEATHER);
        final LivingEntity target = mockShakeTarget(LivingEntity.class, EntityType.COW, 8);
        final ItemStack replacementDrop = mock(ItemStack.class);
        doAnswer(invocation -> {
            final Event event = invocation.getArgument(0);
            if (event instanceof McMMOPlayerShakeEvent shakeEvent) {
                shakeEvent.setDrop(replacementDrop);
            }
            return null;
        }).when(pluginManager).callEvent(any(Event.class));

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class);
                MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class);
                MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
            stubSuccessfulShakeRoll(probabilityUtil);

            // When - the shake roll succeeds
            fishingManager.shakeCheck(target);

            // Then - the replacement drop spawns instead of the configured one
            itemUtils.verify(() -> ItemUtils.spawnItem(eq(player), any(Location.class),
                    eq(replacementDrop), eq(ItemSpawnReason.FISHING_SHAKE_TREASURE)));
        }
    }

    @Test
    void bedrockDropShouldStealFromTheHookedPlayersInventory() {
        // Given - inventory stealing is enabled and the configured drop is the bedrock marker
        configureShakeDrop(EntityType.PLAYER, Material.BEDROCK);
        when(treasureConfig.getInventoryStealEnabled()).thenReturn(true);
        when(treasureConfig.getInventoryStealStacks()).thenReturn(true);

        final Player targetPlayer = mockShakeTarget(Player.class, EntityType.PLAYER, 20);
        final PlayerInventory targetInventory = mock(PlayerInventory.class);
        when(targetPlayer.getInventory()).thenReturn(targetInventory);
        when(targetInventory.getContents()).thenReturn(new ItemStack[9]);
        // The mocked Random rolls slot 0
        final ItemStack stolenStack = mock(ItemStack.class);
        when(targetInventory.getItem(0)).thenReturn(stolenStack);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class);
                MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class);
                MockedStatic<CombatUtils> combatUtils = mockStatic(CombatUtils.class)) {
            stubSuccessfulShakeRoll(probabilityUtil);

            // When - the shake roll succeeds
            fishingManager.shakeCheck(targetPlayer);

            // Then - the stolen stack leaves the target's inventory and drops for the fisher
            verify(targetInventory).setItem(0, null);
            itemUtils.verify(() -> ItemUtils.spawnItem(eq(player), any(Location.class),
                    eq(stolenStack), eq(ItemSpawnReason.FISHING_SHAKE_TREASURE)));
        }
    }
}
