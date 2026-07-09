package com.gmail.nossr50.skills.repair;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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

    /**
     * A repair changes the item's damage, so the confirmation captured at prompt time no longer
     * matches the repaired item. Partial repairs are the norm (one unit of material restores a
     * fraction of the durability), so without rebinding the confirmation every other click would
     * re-prompt instead of continuing the repair.
     */
    @Test
    void confirmedRepairShouldNotRepromptWhenContinuingToRepairTheSameItem() {
        // Given - confirmations are required and a damaged helmet whose clones snapshot the
        // damage value at clone time, like real ItemStack copies do
        Mockito.when(generalConfig.getRepairConfirmRequired()).thenReturn(true);

        final AtomicInteger damage = new AtomicInteger(60);
        final Damageable helmetMeta = Mockito.mock(Damageable.class);
        Mockito.when(helmetMeta.getDamage()).thenAnswer(invocation -> damage.get());
        Mockito.doAnswer(invocation -> {
            damage.set(invocation.getArgument(0));
            return null;
        }).when(helmetMeta).setDamage(Mockito.anyInt());

        final ItemStack helmet = Mockito.mock(ItemStack.class);
        Mockito.when(helmet.getType()).thenReturn(Material.DIAMOND_HELMET);
        Mockito.when(helmet.getAmount()).thenReturn(1);
        Mockito.when(helmet.getItemMeta()).thenReturn(helmetMeta);
        Mockito.when(helmet.clone()).thenAnswer(invocation -> {
            final int damageSnapshot = damage.get();
            final ItemStack helmetCopy = Mockito.mock(ItemStack.class);
            Mockito.when(helmetCopy.isSimilar(helmet))
                    .thenAnswer(similarity -> damage.get() == damageSnapshot);
            return helmetCopy;
        });

        // And - the helmet is repairable with diamonds the player has, one diamond restoring
        // part of the lost durability
        final RepairableManager repairableManager = Mockito.mock(RepairableManager.class);
        Mockito.when(mcMMO.getRepairableManager()).thenReturn(repairableManager);
        final Repairable repairable = Mockito.mock(Repairable.class);
        Mockito.when(repairableManager.getRepairable(Material.DIAMOND_HELMET))
                .thenReturn(repairable);
        Mockito.when(repairable.getRepairMaterial()).thenReturn(Material.DIAMOND);
        Mockito.when(repairable.getBaseRepairDurability(helmet)).thenReturn((short) 30);
        Mockito.when(repairable.getMaximumDurability()).thenReturn((short) 100);

        Mockito.when(Permissions.repairMaterialType(Mockito.eq(player), Mockito.any()))
                .thenReturn(true);
        Mockito.when(Permissions.repairItemType(Mockito.eq(player), Mockito.any()))
                .thenReturn(true);

        final ItemStack diamonds = Mockito.mock(ItemStack.class);
        Mockito.when(diamonds.clone()).thenReturn(diamonds);
        Mockito.when(diamonds.getEnchantments()).thenReturn(Map.of());
        Mockito.when(playerInventory.contains(Material.DIAMOND)).thenReturn(true);
        Mockito.when(playerInventory.first(Material.DIAMOND)).thenReturn(0);
        Mockito.when(playerInventory.getItem(0)).thenReturn(diamonds);

        // And - the player confirmed the repair prompt for the helmet
        repairManager.checkConfirmation(helmet, true);
        assertThat(repairManager.checkConfirmation(helmet, true)).isTrue();

        // When - the confirmed repair completes, restoring part of the durability
        repairManager.handleRepair(helmet);

        // Then - the repair happened
        assertThat(damage.get()).isEqualTo(30);
        // And - continuing to repair the same helmet within the window does not re-prompt
        assertThat(repairManager.checkConfirmation(helmet, true)).isTrue();
    }
}