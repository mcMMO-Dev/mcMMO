package com.gmail.nossr50.skills.salvage;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.TestRegistryBootstrap;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import java.util.Map;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class SalvageManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = getLogger(
            SalvageManagerTest.class.getName());

    private SalvageManager salvageManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        TestRegistryBootstrap.bootstrap(mockedBukkit);
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

    /**
     * Scrap Collector yield limits: rank 1 pays a single material, later ranks pay twice
     * their rank.
     */
    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "1, 1",
            "2, 4",
            "8, 16",
    })
    void salvageLimitShouldFollowTheScrapCollectorRanks(int rank, int expectedLimit) {
        Mockito.when(RankUtils.getRank(player, SubSkillType.SALVAGE_SCRAP_COLLECTOR))
                .thenReturn(rank);

        assertThat(SalvageManager.getSalvageLimit(player)).isEqualTo(expectedLimit);
    }

    @Test
    void enchantBypassPerkShouldGuaranteeFullExtraction() {
        // Given - the salvage enchant bypass perk
        Mockito.when(Permissions.hasSalvageEnchantBypassPerk(player)).thenReturn(true);

        // When / Then - full extraction is guaranteed regardless of rank
        assertThat(salvageManager.getExtractFullEnchantChance()).isEqualTo(100.0);
    }

    @Test
    void placingAnAnvilShouldInformAndBwongExactlyOnce() {
        // Given - anvil messages and placement sounds are enabled
        Mockito.when(generalConfig.getSalvageAnvilMessagesEnabled()).thenReturn(true);
        Mockito.when(generalConfig.getSalvageAnvilPlaceSoundsEnabled()).thenReturn(true);

        // When - the player places an anvil twice
        salvageManager.placedAnvilCheck();
        salvageManager.placedAnvilCheck();

        // Then - the hint and sound fire only for the first placement
        notificationManager.verify(() -> NotificationManager.sendPlayerInformation(player,
                NotificationType.SUBSKILL_MESSAGE, "Salvage.Listener.Anvil"), times(1));
        mockedSoundManager.verify(() -> SoundManager.sendSound(Mockito.eq(player), Mockito.any(),
                Mockito.eq(SoundType.ANVIL)), times(1));
    }

    @Nested
    class HandleSalvage {
        private ItemStack helmet;
        private Damageable helmetMeta;
        private Salvageable salvageable;
        private Location anvilLocation;

        @BeforeEach
        void wireSalvageableHelmet() {
            helmet = Mockito.mock(ItemStack.class);
            helmetMeta = Mockito.mock(Damageable.class);
            Mockito.when(helmet.getType()).thenReturn(Material.DIAMOND_HELMET);
            Mockito.when(helmet.getItemMeta()).thenReturn(helmetMeta);
            Mockito.when(helmet.getEnchantments()).thenReturn(Map.of());

            final SalvageableManager salvageableManager =
                    Mockito.mock(SalvageableManager.class);
            Mockito.when(mcMMO.getSalvageableManager()).thenReturn(salvageableManager);
            salvageable = Mockito.mock(Salvageable.class);
            Mockito.when(salvageableManager.getSalvageable(Material.DIAMOND_HELMET))
                    .thenReturn(salvageable);
            Mockito.when(salvageable.getSalvageMaterial()).thenReturn(Material.DIAMOND);
            Mockito.when(salvageable.getMaximumDurability()).thenReturn((short) 100);
            Mockito.when(salvageable.getMaximumQuantity()).thenReturn(5);

            Mockito.when(Permissions.salvageItemType(Mockito.eq(player), Mockito.any()))
                    .thenReturn(true);
            Mockito.when(Permissions.salvageMaterialType(Mockito.eq(player), Mockito.any()))
                    .thenReturn(true);
            Mockito.when(RankUtils.getRank(player, SubSkillType.SALVAGE_SCRAP_COLLECTOR))
                    .thenReturn(8);

            anvilLocation = new Location(world, 10, 64, 10);
            Mockito.when(player.getLocation()).thenReturn(new Location(world, 12, 64, 10));
        }

        @Test
        void unbreakableItemsShouldBeRejected() {
            Mockito.when(helmetMeta.isUnbreakable()).thenReturn(true);

            salvageManager.handleSalvage(anvilLocation, helmet);

            notificationManager.verify(() -> NotificationManager.sendPlayerInformation(player,
                    NotificationType.SUBSKILL_MESSAGE_FAILED, "Anvil.Unbreakable"));
        }

        @Test
        void lowSkillShouldBeRejectedWithTheRequiredLevel() {
            Mockito.when(salvageable.getMinimumLevel()).thenReturn(50);

            salvageManager.handleSalvage(anvilLocation, helmet);

            notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                    Mockito.eq(player), Mockito.eq(NotificationType.REQUIREMENTS_NOT_MET),
                    Mockito.eq("Salvage.Skills.Adept.Level"), Mockito.eq("50"),
                    Mockito.anyString()));
        }

        @Test
        void tooDamagedItemsShouldBeRejected() {
            // Given - a helmet so damaged the yield floors to zero
            Mockito.when(helmetMeta.getDamage()).thenReturn(100);

            salvageManager.handleSalvage(anvilLocation, helmet);

            notificationManager.verify(() -> NotificationManager.sendPlayerInformation(player,
                    NotificationType.SUBSKILL_MESSAGE_FAILED, "Salvage.Skills.TooDamaged"));
        }

        @Test
        void successfulSalvageShouldPayMaterialsAndEmptyTheHand() {
            try (MockedStatic<ItemUtils> itemUtils = Mockito.mockStatic(ItemUtils.class);
                    MockedConstruction<ItemStack> constructedStacks =
                            Mockito.mockConstruction(ItemStack.class)) {
                // When - an undamaged helmet is salvaged
                salvageManager.handleSalvage(anvilLocation, helmet);

                // Then - the salvage materials fly toward the player
                itemUtils.verify(() -> ItemUtils.spawnItemTowardsLocation(Mockito.eq(player),
                        Mockito.any(Location.class), Mockito.any(Location.class),
                        Mockito.any(ItemStack.class), Mockito.anyDouble(),
                        Mockito.eq(ItemSpawnReason.SALVAGE_MATERIALS)));

                // And - the salvaged item leaves the player's hand
                Mockito.verify(playerInventory).setItemInMainHand(Mockito.any(ItemStack.class));
                Mockito.verify(player).updateInventory();

                // And - the player is told the yield and the success
                notificationManager.verify(
                        () -> NotificationManager.sendPlayerInformationChatOnly(
                                Mockito.eq(player), Mockito.eq("Salvage.Skills.Lottery.Normal"),
                                Mockito.eq("5"), Mockito.anyString()));
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        player, NotificationType.SUBSKILL_MESSAGE, "Salvage.Skills.Success"));

                // And - the salvage result stack was built with the full yield
                assertThat(constructedStacks.constructed()).isNotEmpty();
            }
        }

        @Test
        void lockedArcaneSalvageShouldLoseTheEnchantsButStillPayMaterials() {
            try (MockedStatic<ItemUtils> itemUtils = Mockito.mockStatic(ItemUtils.class);
                    MockedConstruction<ItemStack> ignored =
                            Mockito.mockConstruction(ItemStack.class)) {
                // Given - an enchanted helmet but no Arcane Salvage unlock
                final Enchantment protection = Mockito.mock(Enchantment.class);
                Mockito.when(helmet.getEnchantments()).thenReturn(Map.of(protection, 3));

                // When - the helmet is salvaged
                salvageManager.handleSalvage(anvilLocation, helmet);

                // Then - the player is told the enchants were lost, but materials still fly
                notificationManager.verify(
                        () -> NotificationManager.sendPlayerInformationChatOnly(player,
                                "Salvage.Skills.ArcaneFailed"));
                itemUtils.verify(() -> ItemUtils.spawnItemTowardsLocation(Mockito.eq(player),
                        Mockito.any(Location.class), Mockito.any(Location.class),
                        Mockito.any(ItemStack.class), Mockito.anyDouble(),
                        Mockito.eq(ItemSpawnReason.SALVAGE_MATERIALS)));
                itemUtils.verify(() -> ItemUtils.spawnItemTowardsLocation(Mockito.any(),
                        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyDouble(),
                        Mockito.eq(ItemSpawnReason.SALVAGE_ENCHANTMENT_BOOK)), never());
            }
        }

        @Test
        void arcaneSalvageShouldExtractEnchantsIntoABook() {
            final EnchantmentStorageMeta bookMeta =
                    Mockito.mock(EnchantmentStorageMeta.class);
            try (MockedStatic<ItemUtils> itemUtils = Mockito.mockStatic(ItemUtils.class);
                    MockedConstruction<ItemStack> constructedStacks = Mockito.mockConstruction(
                            ItemStack.class, (mock, context) -> {
                                if (!context.arguments().isEmpty() && context.arguments()
                                        .get(0) == Material.ENCHANTED_BOOK) {
                                    Mockito.when(mock.getItemMeta()).thenReturn(bookMeta);
                                }
                            })) {
                // Given - an enchanted helmet, Arcane Salvage unlocked, and the bypass perk
                // guaranteeing full extraction
                final Enchantment protection = Mockito.mock(Enchantment.class);
                Mockito.when(helmet.getEnchantments()).thenReturn(Map.of(protection, 3));
                Mockito.when(RankUtils.hasUnlockedSubskill(player,
                        SubSkillType.SALVAGE_ARCANE_SALVAGE)).thenReturn(true);
                Mockito.when(Permissions.arcaneSalvage(player)).thenReturn(true);
                Mockito.when(Permissions.hasSalvageEnchantBypassPerk(player)).thenReturn(true);
                Mockito.when(ExperienceConfig.getInstance().allowUnsafeEnchantments())
                        .thenReturn(true);

                // When - the helmet is salvaged
                salvageManager.handleSalvage(anvilLocation, helmet);

                // Then - the enchant is stored in the book at full level and the book flies
                Mockito.verify(bookMeta).addStoredEnchant(protection, 3, true);
                itemUtils.verify(() -> ItemUtils.spawnItemTowardsLocation(Mockito.eq(player),
                        Mockito.any(Location.class), Mockito.any(Location.class),
                        Mockito.any(ItemStack.class), Mockito.anyDouble(),
                        Mockito.eq(ItemSpawnReason.SALVAGE_ENCHANTMENT_BOOK)));
            }
        }
    }
}
