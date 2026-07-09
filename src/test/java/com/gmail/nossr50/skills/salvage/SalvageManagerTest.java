package com.gmail.nossr50.skills.salvage;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.util.Misc;
import java.util.stream.Stream;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class SalvageManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = getLogger(
            SalvageManagerTest.class.getName());

    private SalvageManager salvageManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        Mockito.when(advancedConfig.getArcaneSalvageMaxEnchantLevel()).thenReturn(5);
        salvageManager = new SalvageManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @ParameterizedTest(name = "cap={0}, enchantLevel={1} -> {2}")
    @MethodSource("arcaneSalvageCapCases")
    void arcaneSalvageShouldRespectConfiguredCap(int maxEnchantLevel, int enchantLevel,
            int expectedLevel) {
        Mockito.when(advancedConfig.getArcaneSalvageMaxEnchantLevel()).thenReturn(maxEnchantLevel);
        Mockito.when(ExperienceConfig.getInstance().allowUnsafeEnchantments()).thenReturn(false);

        assertEquals(expectedLevel, salvageManager.getArcaneSalvageEnchantLevel(enchantLevel));
    }

    @ParameterizedTest(name = "unsafe={0}, cap={1}, enchantLevel={2} -> {3}")
    @MethodSource("arcaneSalvageUnsafeCases")
    void arcaneSalvageShouldIgnoreConfiguredCapWhenUnsafeEnchantmentsAreAllowed(
            boolean unsafeEnchantments, int maxEnchantLevel, int enchantLevel,
            int expectedLevel) {
        Mockito.when(advancedConfig.getArcaneSalvageMaxEnchantLevel()).thenReturn(maxEnchantLevel);
        Mockito.when(ExperienceConfig.getInstance().allowUnsafeEnchantments())
                .thenReturn(unsafeEnchantments);

        assertEquals(expectedLevel, salvageManager.getArcaneSalvageEnchantLevel(enchantLevel));
    }

    private static Stream<Arguments> arcaneSalvageCapCases() {
        return Stream.of(
                Arguments.of(5, 1, 1),
                Arguments.of(5, 5, 5),
                Arguments.of(5, 10, 5),
                Arguments.of(10, 10, 10),
                Arguments.of(10, 15, 10),
                Arguments.of(0, 10, 0)
        );
    }

    private static Stream<Arguments> arcaneSalvageUnsafeCases() {
        return Stream.of(
                Arguments.of(true, 5, 10, 10),
                Arguments.of(true, 0, 15, 15),
                Arguments.of(false, 10, 15, 10)
        );
    }

    private ItemStack mockConfirmableItem() {
        final ItemStack item = Mockito.mock(ItemStack.class);
        Mockito.when(item.clone()).thenReturn(item);
        Mockito.when(item.isSimilar(item)).thenReturn(true);
        return item;
    }

    @Test
    void checkConfirmationShouldPromptWhenNoConfirmationIsPending() {
        // Given - confirmations are required and the player has not been prompted yet
        Mockito.when(generalConfig.getSalvageConfirmRequired()).thenReturn(true);
        final ItemStack helmet = mockConfirmableItem();

        // When - the player attempts to salvage the helmet
        final boolean confirmed = salvageManager.checkConfirmation(helmet, true);

        // Then - the salvage is not confirmed and a confirmation is now pending for the helmet
        assertThat(confirmed).isFalse();
        assertThat(salvageManager.isAwaitingConfirmation(helmet)).isTrue();
    }

    @Test
    void checkConfirmationShouldConfirmWhenSameItemIsUsedAgainWithinWindow() {
        // Given - the player was prompted to confirm salvaging the helmet
        Mockito.when(generalConfig.getSalvageConfirmRequired()).thenReturn(true);
        final ItemStack helmet = mockConfirmableItem();
        salvageManager.checkConfirmation(helmet, true);

        // When - the player attempts to salvage the same helmet within the window
        final boolean confirmed = salvageManager.checkConfirmation(helmet, true);

        // Then - the salvage is confirmed
        assertThat(confirmed).isTrue();
    }

    @Test
    void checkConfirmationShouldRepromptWhenHeldItemChangedWithinWindow() {
        // Given - the player was prompted to confirm salvaging the helmet
        Mockito.when(generalConfig.getSalvageConfirmRequired()).thenReturn(true);
        final ItemStack helmet = mockConfirmableItem();
        final ItemStack chestplate = mockConfirmableItem();
        salvageManager.checkConfirmation(helmet, true);

        // When - the player attempts to salvage a different item within the window, as happens
        // when vanilla armor quick-equipping swapped the held item with worn armor
        final boolean confirmed = salvageManager.checkConfirmation(chestplate, true);

        // Then - the different item is not salvaged and a new confirmation is bound to it
        assertThat(confirmed).isFalse();
        assertThat(salvageManager.isAwaitingConfirmation(chestplate)).isTrue();
        // And - the original helmet confirmation is no longer active
        assertThat(salvageManager.isAwaitingConfirmation(helmet)).isFalse();
    }

    @Test
    void checkConfirmationShouldConfirmImmediatelyWhenConfirmationIsDisabled() {
        // Given - salvage confirmations are disabled in the config
        Mockito.when(generalConfig.getSalvageConfirmRequired()).thenReturn(false);
        final ItemStack helmet = mockConfirmableItem();

        // When - the player attempts to salvage the helmet
        final boolean confirmed = salvageManager.checkConfirmation(helmet, true);

        // Then - the salvage is confirmed without a prompt
        assertThat(confirmed).isTrue();
        assertThat(salvageManager.isAwaitingConfirmation(helmet)).isFalse();
    }

    @Test
    void isAwaitingConfirmationShouldExpireAfterConfirmationWindow() {
        // Given - the player was prompted to confirm salvaging the helmet
        Mockito.when(generalConfig.getSalvageConfirmRequired()).thenReturn(true);
        final ItemStack helmet = mockConfirmableItem();
        salvageManager.checkConfirmation(helmet, true);

        // When - the confirmation window has passed
        salvageManager.setLastAnvilUse(
                (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR) - 60);

        // Then - the pending confirmation no longer applies
        assertThat(salvageManager.isAwaitingConfirmation(helmet)).isFalse();
    }

    @Test
    void isAwaitingConfirmationShouldBeFalseForNullItem() {
        // Given - the player was prompted to confirm salvaging the helmet
        Mockito.when(generalConfig.getSalvageConfirmRequired()).thenReturn(true);
        salvageManager.checkConfirmation(mockConfirmableItem(), true);

        // When / Then - a null item never matches the pending confirmation
        assertThat(salvageManager.isAwaitingConfirmation(null)).isFalse();
    }
}
