package com.gmail.nossr50.skills.repair;

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

class RepairManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = getLogger(
            RepairManagerTest.class.getName());

    private RepairManager repairManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        Mockito.when(advancedConfig.getArcaneForgingMaxEnchantLevel()).thenReturn(5);
        repairManager = new RepairManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @ParameterizedTest(name = "cap={0}, enchantLevel={1} -> {2}")
    @MethodSource("arcaneForgingCapCases")
    void arcaneForgingShouldRespectConfiguredCap(int maxEnchantLevel, int enchantLevel,
            int expectedLevel) {
        Mockito.when(advancedConfig.getArcaneForgingMaxEnchantLevel()).thenReturn(maxEnchantLevel);
        Mockito.when(ExperienceConfig.getInstance().allowUnsafeEnchantments()).thenReturn(false);

        assertEquals(expectedLevel, repairManager.getArcaneForgingEnchantLevel(enchantLevel));
    }

    @ParameterizedTest(name = "unsafe={0}, cap={1}, enchantLevel={2} -> {3}")
    @MethodSource("arcaneForgingUnsafeCases")
    void arcaneForgingShouldIgnoreConfiguredCapWhenUnsafeEnchantmentsAreAllowed(
            boolean unsafeEnchantments, int maxEnchantLevel, int enchantLevel,
            int expectedLevel) {
        Mockito.when(advancedConfig.getArcaneForgingMaxEnchantLevel()).thenReturn(maxEnchantLevel);
        Mockito.when(ExperienceConfig.getInstance().allowUnsafeEnchantments())
                .thenReturn(unsafeEnchantments);

        assertEquals(expectedLevel, repairManager.getArcaneForgingEnchantLevel(enchantLevel));
    }

    private static Stream<Arguments> arcaneForgingCapCases() {
        return Stream.of(
                Arguments.of(5, 1, 1),
                Arguments.of(5, 5, 5),
                Arguments.of(5, 10, 5),
                Arguments.of(10, 10, 10),
                Arguments.of(10, 15, 10),
                Arguments.of(0, 10, 0)
        );
    }

    private static Stream<Arguments> arcaneForgingUnsafeCases() {
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
        Mockito.when(generalConfig.getRepairConfirmRequired()).thenReturn(true);
        final ItemStack pickaxe = mockConfirmableItem();

        // When - the player attempts to repair the pickaxe
        final boolean confirmed = repairManager.checkConfirmation(pickaxe, true);

        // Then - the repair is not confirmed and a confirmation is now pending for the pickaxe
        assertThat(confirmed).isFalse();
        assertThat(repairManager.isAwaitingConfirmation(pickaxe)).isTrue();
    }

    @Test
    void checkConfirmationShouldConfirmWhenSameItemIsUsedAgainWithinWindow() {
        // Given - the player was prompted to confirm repairing the pickaxe
        Mockito.when(generalConfig.getRepairConfirmRequired()).thenReturn(true);
        final ItemStack pickaxe = mockConfirmableItem();
        repairManager.checkConfirmation(pickaxe, true);

        // When - the player attempts to repair the same pickaxe within the window
        final boolean confirmed = repairManager.checkConfirmation(pickaxe, true);

        // Then - the repair is confirmed
        assertThat(confirmed).isTrue();
    }

    @Test
    void checkConfirmationShouldRepromptWhenHeldItemChangedWithinWindow() {
        // Given - the player was prompted to confirm repairing the pickaxe
        Mockito.when(generalConfig.getRepairConfirmRequired()).thenReturn(true);
        final ItemStack pickaxe = mockConfirmableItem();
        final ItemStack chestplate = mockConfirmableItem();
        repairManager.checkConfirmation(pickaxe, true);

        // When - the player attempts to repair a different item within the window, as happens
        // when vanilla armor quick-equipping swapped the held item with worn armor
        final boolean confirmed = repairManager.checkConfirmation(chestplate, true);

        // Then - the different item is not repaired and a new confirmation is bound to it
        assertThat(confirmed).isFalse();
        assertThat(repairManager.isAwaitingConfirmation(chestplate)).isTrue();
        // And - the original pickaxe confirmation is no longer active
        assertThat(repairManager.isAwaitingConfirmation(pickaxe)).isFalse();
    }

    @Test
    void checkConfirmationShouldConfirmImmediatelyWhenConfirmationIsDisabled() {
        // Given - repair confirmations are disabled in the config
        Mockito.when(generalConfig.getRepairConfirmRequired()).thenReturn(false);
        final ItemStack pickaxe = mockConfirmableItem();

        // When - the player attempts to repair the pickaxe
        final boolean confirmed = repairManager.checkConfirmation(pickaxe, true);

        // Then - the repair is confirmed without a prompt
        assertThat(confirmed).isTrue();
        assertThat(repairManager.isAwaitingConfirmation(pickaxe)).isFalse();
    }

    @Test
    void isAwaitingConfirmationShouldExpireAfterConfirmationWindow() {
        // Given - the player was prompted to confirm repairing the pickaxe
        Mockito.when(generalConfig.getRepairConfirmRequired()).thenReturn(true);
        final ItemStack pickaxe = mockConfirmableItem();
        repairManager.checkConfirmation(pickaxe, true);

        // When - the confirmation window has passed
        repairManager.setLastAnvilUse(
                (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR) - 60);

        // Then - the pending confirmation no longer applies
        assertThat(repairManager.isAwaitingConfirmation(pickaxe)).isFalse();
    }

    @Test
    void isAwaitingConfirmationShouldBeFalseForNullItem() {
        // Given - the player was prompted to confirm repairing the pickaxe
        Mockito.when(generalConfig.getRepairConfirmRequired()).thenReturn(true);
        repairManager.checkConfirmation(mockConfirmableItem(), true);

        // When / Then - a null item never matches the pending confirmation
        assertThat(repairManager.isAwaitingConfirmation(null)).isFalse();
    }
}