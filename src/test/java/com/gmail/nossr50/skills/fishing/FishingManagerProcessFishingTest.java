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
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingTreasureEvent;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Covers the reward pipeline for a successful catch: skill XP for the caught fish, the
 * Treasure Hunter roll, the treasure event's veto power, the extra fish drop, and the vanilla
 * XP boost. A regression here silently changes what every fishing catch pays out.
 */
class FishingManagerProcessFishingTest extends MMOTestEnvironment {
    private static final Logger LOGGER =
            Logger.getLogger(FishingManagerProcessFishingTest.class.getName());

    private static final int FISH_XP = 30;
    private static final int TREASURE_XP = 100;

    private FishingManager fishingManager;
    private Item fishingCatch;
    private ItemStack caughtStack;
    private ItemStack treasureStack;
    private McMMOPlayerFishingTreasureEvent treasureEvent;
    private MockedStatic<FishingTreasureConfig> mockedTreasureConfig;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(LOGGER);

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
        when(generalConfig.getFishingDropsEnabled()).thenReturn(true);

        treasureStack = mock(ItemStack.class);
        // Salmon has no durability, keeping the random durability re-roll out of the picture
        when(treasureStack.getType()).thenReturn(Material.SALMON);
        when(treasureStack.clone()).thenReturn(treasureStack);

        mockedTreasureConfig = mockStatic(FishingTreasureConfig.class);
        final FishingTreasureConfig treasureConfig = mock(FishingTreasureConfig.class);
        when(FishingTreasureConfig.getInstance()).thenReturn(treasureConfig);
        when(treasureConfig.getItemDropRate(anyInt(), any(Rarity.class))).thenReturn(100.0);
        treasureConfig.fishingRewards = new HashMap<>();
        final FishingTreasure treasure = new FishingTreasure(treasureStack, TREASURE_XP);
        for (final Rarity rarity : Rarity.values()) {
            treasureConfig.fishingRewards.put(rarity, List.of(treasure));
        }

        // A mocked Random rolls 0 everywhere: the dice land in the first rarity bucket and
        // pick the first (only) treasure in it
        when(Misc.getRandom()).thenReturn(mock(Random.class));

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
}
