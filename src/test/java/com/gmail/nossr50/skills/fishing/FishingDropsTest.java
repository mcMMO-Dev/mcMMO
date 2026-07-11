package com.gmail.nossr50.skills.fishing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;
import com.gmail.nossr50.util.Misc;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

/**
 * Covers the Shake drop selection helpers in {@link Fishing}: the cumulative-chance roll that
 * picks a treasure and the per-mob drop table lookup. The roll semantics matter because drop
 * chances that sum below 100 are meant to leave a "nothing drops" remainder.
 */
class FishingDropsTest extends MMOTestEnvironment {
    private static final Logger LOGGER = Logger.getLogger(FishingDropsTest.class.getName());

    private Random random;
    private MockedStatic<FishingTreasureConfig> mockedTreasureConfig;
    private FishingTreasureConfig treasureConfig;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(LOGGER);

        random = mock(Random.class);
        when(Misc.getRandom()).thenReturn(random);

        mockedTreasureConfig = mockStatic(FishingTreasureConfig.class);
        treasureConfig = mock(FishingTreasureConfig.class);
        when(FishingTreasureConfig.getInstance()).thenReturn(treasureConfig);
        treasureConfig.shakeMap = new HashMap<>();
    }

    @AfterEach
    void tearDown() {
        if (mockedTreasureConfig != null) {
            mockedTreasureConfig.close();
        }
        cleanUpStaticMocks();
    }

    private ShakeTreasure mockTreasure(ItemStack cloneToReturn, double dropChance) {
        final ItemStack drop = mock(ItemStack.class);
        when(drop.clone()).thenReturn(cloneToReturn);
        return new ShakeTreasure(drop, 5, dropChance, 0);
    }

    /**
     * Two treasures at 30% each split the roll range 0-59 between them and leave rolls of 60
     * and above empty-handed, since the chances only sum to 60.
     */
    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "29, 0",
            "30, 1",
            "59, 1",
            "60, -1",
            "99, -1",
    })
    void chooseDropShouldPickTreasuresByCumulativeChance(int roll, int expectedDropIndex) {
        // Given - two treasures with a 30% drop chance each
        final ItemStack firstDrop = mock(ItemStack.class);
        final ItemStack secondDrop = mock(ItemStack.class);
        final List<ShakeTreasure> possibleDrops =
                List.of(mockTreasure(firstDrop, 30), mockTreasure(secondDrop, 30));
        when(random.nextInt(100)).thenReturn(roll);

        // When - a drop is chosen
        final ItemStack chosen = Fishing.chooseDrop(possibleDrops);

        // Then - the roll maps onto the cumulative chance ranges
        if (expectedDropIndex < 0) {
            assertThat(chosen).isNull();
        } else {
            assertThat(chosen).isSameAs(expectedDropIndex == 0 ? firstDrop : secondDrop);
        }
    }

    @Test
    void findPossibleDropsShouldReturnTheConfiguredListForTheMob() {
        // Given - a drop table configured for sheep
        final List<ShakeTreasure> sheepDrops = List.of(mockTreasure(mock(ItemStack.class), 100));
        treasureConfig.shakeMap.put(EntityType.SHEEP, sheepDrops);
        final LivingEntity sheep = mock(LivingEntity.class);
        when(sheep.getType()).thenReturn(EntityType.SHEEP);

        // When/Then - the sheep's drop table is returned
        assertThat(Fishing.findPossibleDrops(sheep)).isSameAs(sheepDrops);
    }

    @Test
    void findPossibleDropsShouldReturnNullForMobsWithoutADropTable() {
        // Given - no drop table configured for cows
        final LivingEntity cow = mock(LivingEntity.class);
        when(cow.getType()).thenReturn(EntityType.COW);

        // When/Then - there is nothing to shake loose
        assertThat(Fishing.findPossibleDrops(cow)).isNull();
    }
}
