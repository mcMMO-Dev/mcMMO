package com.gmail.nossr50.skills.fishing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.TestRegistryBootstrap;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.treasure.EnchantmentTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingTreasureEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EnchantmentMapper;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Covers the reward pipeline for a successful catch: skill XP for the caught fish, the
 * Treasure Hunter roll including its Luck of the Sea scaling, the Magic Hunter enchantment
 * roll, the treasure event's veto power, the extra fish drop, and the vanilla XP boost. A
 * regression here silently changes what every fishing catch pays out.
 *
 * <p>Registry-backed enchantments come from {@link TestRegistryBootstrap}.</p>
 */
class FishingManagerProcessFishingTest extends MMOTestEnvironment {
    private static final Logger LOGGER =
            Logger.getLogger(FishingManagerProcessFishingTest.class.getName());

    private static final int FISH_XP = 30;
    private static final int TREASURE_XP = 100;
    private static final int TREASURE_HUNTER_RANK = 2;

    private FishingManager fishingManager;
    private Item fishingCatch;
    private ItemStack caughtStack;
    private ItemStack treasureStack;
    private McMMOPlayerFishingTreasureEvent treasureEvent;
    private MockedStatic<FishingTreasureConfig> mockedTreasureConfig;
    private FishingTreasureConfig treasureConfig;
    private Random random;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(LOGGER);
        TestRegistryBootstrap.bootstrap(mockedBukkit);

        fishingManager = Mockito.spy(new FishingManager(mmoPlayer));
        doNothing().when(fishingManager)
                .applyXpGain(anyFloat(), any(XPGainReason.class), any(XPGainSource.class));

        caughtStack = mock(ItemStack.class);
        when(caughtStack.getType()).thenReturn(Material.SALMON);
        fishingCatch = mock(Item.class);
        when(fishingCatch.getItemStack()).thenReturn(caughtStack);

        when(ExperienceConfig.getInstance().getXp(PrimarySkillType.FISHING, Material.SALMON))
                .thenReturn(FISH_XP);

        mockedEventUtils = mockStatic(EventUtils.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedTreasureConfig != null) {
            mockedTreasureConfig.close();
        }
        cleanUpStaticMocks();
    }

    /**
     * Rigs the treasure roll so the dice always land on a single configured treasure, letting
     * the tests assert on the pipeline around the roll instead of fighting the RNG.
     */
    private void configureGuaranteedTreasure() {
        // Salmon has no durability, keeping the random durability re-roll out of the picture
        configureGuaranteedTreasure(Material.SALMON);
    }

    private void configureGuaranteedTreasure(Material dropMaterial) {
        configureTreasureTable(dropMaterial);
        when(treasureConfig.getItemDropRate(anyInt(), any(Rarity.class))).thenReturn(100.0);
    }

    /**
     * Builds a mocked treasure config holding a single treasure of the given material in
     * every rarity bucket, with a hand-rolled {@link Random} so tests control the dice. Item
     * drop rates stay unstubbed (0%) until the caller decides them.
     */
    private void configureTreasureTable(Material dropMaterial) {
        when(generalConfig.getFishingDropsEnabled()).thenReturn(true);

        treasureStack = mock(ItemStack.class);
        when(treasureStack.getType()).thenReturn(dropMaterial);
        when(treasureStack.clone()).thenReturn(treasureStack);

        mockedTreasureConfig = mockStatic(FishingTreasureConfig.class);
        treasureConfig = mock(FishingTreasureConfig.class);
        when(FishingTreasureConfig.getInstance()).thenReturn(treasureConfig);
        treasureConfig.fishingRewards = new HashMap<>();
        final FishingTreasure treasure = new FishingTreasure(treasureStack, TREASURE_XP);
        for (final Rarity rarity : Rarity.values()) {
            treasureConfig.fishingRewards.put(rarity, List.of(treasure));
        }

        // A mocked Random rolls 0 wherever a test leaves it unstubbed: the dice land in the
        // first rarity bucket and pick the first (only) treasure in it
        random = mock(Random.class);
        when(Misc.getRandom()).thenReturn(random);

        treasureEvent = mock(McMMOPlayerFishingTreasureEvent.class);
        when(treasureEvent.getTreasure()).thenReturn(treasureStack);
        when(treasureEvent.getXp()).thenReturn(TREASURE_XP);
        when(EventUtils.callFishingTreasureEvent(eq(mmoPlayer), any(ItemStack.class), anyInt(),
                any())).thenReturn(treasureEvent);
    }

    @Test
    void handleVanillaXpBoostShouldScaleByTheLootTierModifier() {
        // Given - loot tier 3 grants a x2 vanilla XP modifier
        when(RankUtils.getRank(player, SubSkillType.FISHING_TREASURE_HUNTER)).thenReturn(3);
        when(advancedConfig.getFishingVanillaXPModifier(3)).thenReturn(2);

        // When/Then - the event XP is scaled by the modifier
        assertThat(fishingManager.handleVanillaXpBoost(5)).isEqualTo(10);
    }

    @Test
    void catchShouldPayOnlyFishXpWhenTreasureDropsAreDisabled() {
        // Given - treasure drops are disabled in the config
        when(generalConfig.getFishingDropsEnabled()).thenReturn(false);

        // When - a catch is processed
        fishingManager.processFishing(fishingCatch, null);

        // Then - the player earns the configured fish XP and keeps the original catch
        verify(fishingManager).applyXpGain(FISH_XP, XPGainReason.PVE, XPGainSource.SELF);
        verify(fishingCatch, never()).setItemStack(any());
    }

    @Test
    void treasureCatchShouldPayCombinedXpAndReplaceTheCatch() {
        // Given - the treasure roll is guaranteed to find a treasure
        configureGuaranteedTreasure();
        when(generalConfig.getFishingExtraFish()).thenReturn(false);

        // When - a catch is processed
        fishingManager.processFishing(fishingCatch, null);

        // Then - the caught item becomes the treasure and the XP combines fish and treasure XP
        verify(fishingCatch).setItemStack(treasureStack);
        verify(fishingManager).applyXpGain(FISH_XP + TREASURE_XP, XPGainReason.PVE,
                XPGainSource.SELF);
    }

    @Test
    void cancelledTreasureEventShouldPayOnlyFishXp() {
        // Given - another plugin cancels the treasure event
        configureGuaranteedTreasure();
        when(treasureEvent.isCancelled()).thenReturn(true);

        // When - a catch is processed
        fishingManager.processFishing(fishingCatch, null);

        // Then - the treasure and its XP are vetoed, leaving only the fish XP
        verify(fishingCatch, never()).setItemStack(any());
        verify(fishingManager).applyXpGain(FISH_XP, XPGainReason.PVE, XPGainSource.SELF);
    }

    @Test
    void treasureCatchShouldAlsoSpawnTheOriginalFishWhenExtraFishIsEnabled() {
        // Given - a guaranteed treasure and the extra fish option enabled
        configureGuaranteedTreasure();
        when(generalConfig.getFishingExtraFish()).thenReturn(true);
        final Location eyeLocation = mock(Location.class);
        when(player.getEyeLocation()).thenReturn(eyeLocation);

        try (MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class)) {
            // When - a catch is processed
            fishingManager.processFishing(fishingCatch, null);

            // Then - the original fish drops alongside the treasure
            itemUtils.verify(() -> ItemUtils.spawnItem(player, eyeLocation, caughtStack,
                    ItemSpawnReason.FISHING_EXTRA_FISH));
        }
    }

    /**
     * Luck of the Sea does not subtract from the dice roll (which would give every rod a
     * minimum treasure chance); it scales the roll down by level * Lure_Modifier percent.
     * These tests pin the same 50.0 roll against a 45% top-rarity rate so only the scaling
     * decides whether treasure lands.
     */
    @Nested
    class LuckOfTheSeaScaling {
        private static final double MYTHIC_DROP_RATE = 45.0;

        private ItemStack rod;
        private Enchantment luckOfTheSea;

        @BeforeEach
        void setUpRodAndLuck() {
            configureTreasureTable(Material.SALMON);
            when(RankUtils.getRank(player, SubSkillType.FISHING_TREASURE_HUNTER))
                    .thenReturn(TREASURE_HUNTER_RANK);
            when(treasureConfig.getItemDropRate(TREASURE_HUNTER_RANK, Rarity.MYTHIC))
                    .thenReturn(MYTHIC_DROP_RATE);
            // The dice roll 50.0 sits just above the mythic rate until luck scales it
            when(random.nextDouble()).thenReturn(0.5);

            luckOfTheSea = mock(Enchantment.class);
            final EnchantmentMapper enchantmentMapper = mock(EnchantmentMapper.class);
            when(mcMMO.p.getEnchantmentMapper()).thenReturn(enchantmentMapper);
            when(enchantmentMapper.getLuckOfTheSea()).thenReturn(luckOfTheSea);
            when(generalConfig.getFishingLureModifier()).thenReturn(4.0);

            rod = mock(ItemStack.class);
            when(rod.getType()).thenReturn(Material.FISHING_ROD);
        }

        @Test
        void luckOfTheSeaScalesTheRollIntoTreasureRange() {
            // Given - a Luck of the Sea III rod in the casting hand scales the roll by
            // 1 - 3 * 4 / 100 = 0.88, turning the 50.0 roll into 44.0
            when(playerInventory.getItemInMainHand()).thenReturn(rod);
            when(rod.getEnchantmentLevel(luckOfTheSea)).thenReturn(3);

            // When - the catch is processed with the casting hand known
            fishingManager.processFishing(fishingCatch, EquipmentSlot.HAND);

            // Then - the scaled roll lands under the 45% mythic rate and pays out
            verify(fishingCatch).setItemStack(treasureStack);
        }

        @Test
        void unenchantedRodLeavesTheRollUnscaled() {
            // Given - a rod without Luck of the Sea in the casting hand
            when(playerInventory.getItemInMainHand()).thenReturn(rod);
            when(rod.getEnchantmentLevel(luckOfTheSea)).thenReturn(0);

            // When - the catch is processed
            fishingManager.processFishing(fishingCatch, EquipmentSlot.HAND);

            // Then - the unscaled 50.0 roll stays above the mythic rate and finds nothing
            verify(fishingCatch, never()).setItemStack(any());
        }

        @Test
        void offHandRodsContributeTheirLuck() {
            // Given - the enchanted rod sits in the off hand
            when(playerInventory.getItemInOffHand()).thenReturn(rod);
            when(rod.getEnchantmentLevel(luckOfTheSea)).thenReturn(3);

            // When - the catch is processed for an off hand cast
            fishingManager.processFishing(fishingCatch, EquipmentSlot.OFF_HAND);

            // Then - the off hand rod's luck scales the roll into range
            verify(fishingCatch).setItemStack(treasureStack);
        }

        @Test
        void luckIsIgnoredWhenTheCastingHandIsUnknown() {
            // Given - an enchanted rod in hand but no hand attributed to the catch
            when(playerInventory.getItemInMainHand()).thenReturn(rod);
            when(rod.getEnchantmentLevel(luckOfTheSea)).thenReturn(3);

            // When - the catch is processed without a known casting hand
            fishingManager.processFishing(fishingCatch, null);

            // Then - no luck applies and the roll misses
            verify(fishingCatch, never()).setItemStack(any());
        }
    }

    /**
     * Magic Hunter rolls bonus enchantments onto enchantable treasure. These tests pin the
     * candidate filtering (only enchants valid for the drop), the conflict rule, and the
     * player-facing announcement.
     */
    @Nested
    class MagicHunter {
        private Enchantment firstEnchant;
        private Enchantment secondEnchant;

        /**
         * Guarantees a treasure of the given material and rigs two enchantment candidates in
         * the top rarity bucket. The shuffle over two candidates draws nextInt(2) once, where
         * 1 keeps the declared order; the second candidate's halved odds draw nextInt(2)
         * again, where 0 lets it through.
         */
        private void configureMagicHunter(Material dropMaterial) {
            configureGuaranteedTreasure(dropMaterial);
            when(RankUtils.getRank(player, SubSkillType.FISHING_TREASURE_HUNTER))
                    .thenReturn(TREASURE_HUNTER_RANK);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.FISHING_MAGIC_HUNTER))
                    .thenReturn(true);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.FISHING_TREASURE_HUNTER))
                    .thenReturn(true);
            when(treasureConfig.getEnchantmentDropRate(TREASURE_HUNTER_RANK, Rarity.MYTHIC))
                    .thenReturn(100.0);

            firstEnchant = mock(Enchantment.class);
            secondEnchant = mock(Enchantment.class);
            // Enchantment.values() walks the (empty) registry off-server; seeding the cache
            // keeps the valid-enchantment filtering real without it
            Fishing.ENCHANTABLE_CACHE.put(Material.FISHING_ROD,
                    List.of(firstEnchant, secondEnchant));
            treasureConfig.fishingEnchantments = new HashMap<>();
            treasureConfig.fishingEnchantments.put(Rarity.MYTHIC, List.of(
                    new EnchantmentTreasure(firstEnchant, 1),
                    new EnchantmentTreasure(secondEnchant, 2)));
            when(random.nextInt(2)).thenReturn(1, 0);
        }

        @AfterEach
        void clearEnchantableCache() {
            Fishing.ENCHANTABLE_CACHE.clear();
        }

        @Test
        void rolledEnchantsLandOnTheTreasureAndAreAnnounced() {
            // Given - an enchantable treasure with two non-conflicting candidates
            configureMagicHunter(Material.FISHING_ROD);

            // When - the treasure catch is processed
            fishingManager.processFishing(fishingCatch, null);

            // Then - both rolled enchantments ride the treasure event and land on the drop
            final Map<Enchantment, Integer> expectedEnchants =
                    Map.of(firstEnchant, 1, secondEnchant, 2);
            mockedEventUtils.verify(() -> EventUtils.callFishingTreasureEvent(mmoPlayer,
                    treasureStack, TREASURE_XP, expectedEnchants));
            verify(treasureStack).addUnsafeEnchantments(expectedEnchants);

            // And - the player is told magic was found
            notificationManager.verify(() -> NotificationManager.sendPlayerInformation(player,
                    NotificationType.SUBSKILL_MESSAGE, "Fishing.Ability.TH.MagicFound"));
        }

        @Test
        void conflictingEnchantsAreNotStacked() {
            // Given - the second candidate conflicts with the already-rolled first one
            configureMagicHunter(Material.FISHING_ROD);
            when(firstEnchant.conflictsWith(secondEnchant)).thenReturn(true);

            // When - the treasure catch is processed
            fishingManager.processFishing(fishingCatch, null);

            // Then - only the first enchantment lands
            verify(treasureStack).addUnsafeEnchantments(Map.of(firstEnchant, 1));
        }

        @Test
        void nonEnchantableTreasureSkipsMagicHunter() {
            // Given - Magic Hunter is unlocked but the treasure cannot hold enchantments
            configureMagicHunter(Material.SALMON);

            // When - the treasure catch is processed
            fishingManager.processFishing(fishingCatch, null);

            // Then - the treasure stays unenchanted and no magic message is sent
            verify(treasureStack, never()).addUnsafeEnchantments(any());
            notificationManager.verifyNoInteractions();
        }

        @Test
        void lockedMagicHunterLeavesTreasureUnenchanted() {
            // Given - the treasure is enchantable but Magic Hunter is still locked
            configureMagicHunter(Material.FISHING_ROD);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.FISHING_MAGIC_HUNTER))
                    .thenReturn(false);

            // When - the treasure catch is processed
            fishingManager.processFishing(fishingCatch, null);

            // Then - the treasure stays unenchanted and no magic message is sent
            verify(treasureStack, never()).addUnsafeEnchantments(any());
            notificationManager.verifyNoInteractions();
        }
    }
}
