package com.gmail.nossr50.skills.unarmed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Covers the Unarmed combat effects that depend on the committed attack strength of a hit.
 *
 * <p>Regression background: Paper 26.1.2+ resets the attack cooldown ticker before
 * {@code EntityDamageByEntityEvent} fires, so {@code Player#getAttackCooldown()} reads ~0.1
 * mid-event even for a fully charged punch. Berserk and Disarm must use the attack strength
 * scale that CombatUtils back-derives from the event instead of the live cooldown, otherwise
 * Berserk turns into a damage penalty (a 9.0 damage punch collapsed to ~1.35).
 */
class UnarmedManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            UnarmedManagerTest.class.getName());

    private UnarmedManager unarmedManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        unarmedManager = new UnarmedManager(mmoPlayer);

        // Simulate Paper 26.1.2+ during a damage event: the ticker was already reset, so the
        // live cooldown reads ~0.1 for a fist even though the hit was fully charged.
        when(advancedConfig.useAttackCooldown()).thenReturn(true);
        when(player.getAttackCooldown()).thenReturn(0.1f);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void berserkDamageShouldUseCommittedAttackStrengthWhenLiveCooldownIsStale() {
        // Given - a fully charged 9.0 damage punch (1.0 fist + 8.0 Steel Arm Style) while the
        // live cooldown misreports the charge as 0.1

        // When - Berserk computes its bonus from the committed attack strength of the hit
        final double bonus = unarmedManager.berserkDamage(9.0, 1.0);

        // Then - the +50% bonus applies (9.0 -> 13.5) instead of collapsing the hit to 1.35
        assertThat(bonus).isEqualTo(4.5);

        // And - the stale live cooldown is never consulted
        verify(player, never()).getAttackCooldown();
    }

    /**
     * Bonus formula: {@code (damage * 1.5 * attackStrengthScale) - damage}. Weak (uncharged)
     * hits keep their historical penalty: the bonus goes negative below ~2/3 charge.
     */
    @ParameterizedTest(name = "damage={0}, scale={1} -> bonus={2}")
    @CsvSource({
            "9.0,  1.0,  4.5",    // fully charged hit gains the full +50%
            "1.0,  1.0,  0.5",    // bare fist without Steel Arm Style
            "9.0,  0.5,  -2.25",  // half charge keeps its historical spam-click penalty
            "9.0,  0.0,  -9.0",   // zero charge cancels the hit entirely
    })
    void berserkDamageShouldScaleWithCommittedAttackStrength(final double damage,
            final double attackStrengthScale, final double expectedBonus) {
        // Given - a hit committed at the given attack strength

        // When
        final double bonus = unarmedManager.berserkDamage(damage, attackStrengthScale);

        // Then
        assertThat(bonus).isCloseTo(expectedBonus, offset(1e-9));
    }

    @Test
    void disarmCheckShouldRollOddsWithCommittedAttackStrength() {
        // Given - a defender and a hit committed at 3/4 attack strength
        final Player defender = Mockito.mock(Player.class);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class)) {
            // When - the RNG roll fails (mock default) so no disarm side effects run
            unarmedManager.disarmCheck(defender, 0.75);

            // Then - the odds were scaled by the committed attack strength, not the stale cooldown
            probabilityUtil.verify(() -> ProbabilityUtil.isSkillRNGSuccessful(
                    eq(SubSkillType.UNARMED_DISARM), eq(mmoPlayer), eq(0.75)));
            verify(player, never()).getAttackCooldown();
        }
    }

    @Nested
    class AbilityGates {
        @Test
        void disarmShouldRequireAnArmedPlayerTarget() {
            // Given - Disarm is unlocked
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.UNARMED_DISARM))
                    .thenReturn(true);

            // When / Then - an armed player can be disarmed
            final Player armedDefender = Mockito.mock(Player.class);
            final PlayerInventory armedInventory = Mockito.mock(PlayerInventory.class);
            final ItemStack sword = Mockito.mock(ItemStack.class);
            when(sword.getType()).thenReturn(Material.DIAMOND_SWORD);
            when(armedInventory.getItemInMainHand()).thenReturn(sword);
            when(armedDefender.getInventory()).thenReturn(armedInventory);
            assertThat(unarmedManager.canDisarm(armedDefender)).isTrue();

            // And - an empty-handed player cannot
            final ItemStack emptyHand = Mockito.mock(ItemStack.class);
            when(emptyHand.getType()).thenReturn(Material.AIR);
            when(armedInventory.getItemInMainHand()).thenReturn(emptyHand);
            assertThat(unarmedManager.canDisarm(armedDefender)).isFalse();

            // And - mobs cannot be disarmed at all
            assertThat(unarmedManager.canDisarm(Mockito.mock(LivingEntity.class))).isFalse();
        }

        @Test
        void arrowDeflectShouldRequireEmptyFists() {
            // Given - Arrow Deflect is unlocked
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.UNARMED_ARROW_DEFLECT))
                    .thenReturn(true);
            final ItemStack heldItem = Mockito.mock(ItemStack.class);
            when(playerInventory.getItemInMainHand()).thenReturn(heldItem);

            try (MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class)) {
                // When / Then - empty fists deflect, held items do not
                itemUtils.when(() -> ItemUtils.isUnarmed(heldItem)).thenReturn(true);
                assertThat(unarmedManager.canDeflect()).isTrue();
                itemUtils.when(() -> ItemUtils.isUnarmed(heldItem)).thenReturn(false);
                assertThat(unarmedManager.canDeflect()).isFalse();
            }
        }

        @Test
        void berserkUseShouldRequireTheActiveSuperAbility() {
            Mockito.doReturn(false).when(mmoPlayer).getAbilityMode(SuperAbilityType.BERSERK);
            assertThat(unarmedManager.canUseBerserk()).isFalse();

            Mockito.doReturn(true).when(mmoPlayer).getAbilityMode(SuperAbilityType.BERSERK);
            assertThat(unarmedManager.canUseBerserk()).isTrue();
        }

        @Test
        void abilityActivationShouldRequireReadiedFists() {
            when(Permissions.berserk(player)).thenReturn(true);

            Mockito.doReturn(false).when(mmoPlayer).getToolPreparationMode(ToolType.FISTS);
            assertThat(unarmedManager.canActivateAbility()).isFalse();

            Mockito.doReturn(true).when(mmoPlayer).getToolPreparationMode(ToolType.FISTS);
            assertThat(unarmedManager.canActivateAbility()).isTrue();
        }
    }

    @Nested
    class BlockCracker {
        private Block block;

        @BeforeEach
        void setUpBlock() {
            block = Mockito.mock(Block.class);
            when(generalConfig.isBlockCrackerAllowed()).thenReturn(true);
        }

        @ParameterizedTest
        @CsvSource({
                "STONE_BRICKS, CRACKED_STONE_BRICKS",
                "INFESTED_STONE_BRICKS, INFESTED_CRACKED_STONE_BRICKS",
                "DEEPSLATE_BRICKS, CRACKED_DEEPSLATE_BRICKS",
                "DEEPSLATE_TILES, CRACKED_DEEPSLATE_TILES",
                "POLISHED_BLACKSTONE_BRICKS, CRACKED_POLISHED_BLACKSTONE_BRICKS",
                "NETHER_BRICKS, CRACKED_NETHER_BRICKS",
        })
        void crackableBricksShouldCrack(Material intactBrick, Material crackedBrick) {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - the block cracker activation succeeds against a crackable brick
                probabilityUtil.when(() -> ProbabilityUtil.isNonRNGSkillActivationSuccessful(
                        SubSkillType.UNARMED_BLOCK_CRACKER, mmoPlayer)).thenReturn(true);
                when(block.getType()).thenReturn(intactBrick);

                // When - the punch lands
                unarmedManager.blockCrackerCheck(block);

                // Then - the brick cracks
                verify(block).setType(crackedBrick);
            }
        }

        @Test
        void unrelatedBlocksShouldNotChange() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a successful activation against a block with no cracked variant
                probabilityUtil.when(() -> ProbabilityUtil.isNonRNGSkillActivationSuccessful(
                        SubSkillType.UNARMED_BLOCK_CRACKER, mmoPlayer)).thenReturn(true);
                when(block.getType()).thenReturn(Material.STONE);

                // When - the punch lands
                unarmedManager.blockCrackerCheck(block);

                // Then - the block is untouched
                verify(block, never()).setType(any(Material.class));
            }
        }

        @Test
        void disabledBlockCrackerShouldChangeNothing() {
            // Given - the server disabled block cracking
            when(generalConfig.isBlockCrackerAllowed()).thenReturn(false);
            when(block.getType()).thenReturn(Material.STONE_BRICKS);

            // When - the punch lands
            unarmedManager.blockCrackerCheck(block);

            // Then - the block is untouched
            verify(block, never()).setType(any(Material.class));
        }
    }

    @Nested
    class Disarm {
        private Player defender;
        private PlayerInventory defenderInventory;
        private ItemStack defenderWeapon;
        private McMMOPlayer defenderMmoPlayer;

        @BeforeEach
        void setUpDefender() {
            defender = Mockito.mock(Player.class);
            defenderInventory = Mockito.mock(PlayerInventory.class);
            defenderWeapon = Mockito.mock(ItemStack.class);
            when(defender.getInventory()).thenReturn(defenderInventory);
            when(defenderInventory.getItemInMainHand()).thenReturn(defenderWeapon);
            when(defender.getLocation()).thenReturn(new Location(world, 0, 64, 0));

            defenderMmoPlayer = Mockito.mock(McMMOPlayer.class);
            when(UserManager.getPlayer(defender)).thenReturn(defenderMmoPlayer);
            // Iron Grip stays out of the way unless a test opts in
            when(Permissions.isSubSkillEnabled(defender, SubSkillType.UNARMED_IRON_GRIP))
                    .thenReturn(false);
        }

        @Test
        void successfulDisarmShouldDropTheWeaponAndEmptyTheHand() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class);
                    MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class)) {
                // Given - a winning disarm roll, an uncancelled event, and protected drops
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.UNARMED_DISARM, mmoPlayer, 1.0)).thenReturn(true);
                mockedEventUtils = mockStatic(EventUtils.class);
                final McMMOPlayerDisarmEvent disarmEvent =
                        Mockito.mock(McMMOPlayerDisarmEvent.class);
                mockedEventUtils.when(() -> EventUtils.callDisarmEvent(defender))
                        .thenReturn(disarmEvent);
                final Item droppedItem = Mockito.mock(Item.class);
                final Location defenderLocation = defender.getLocation();
                itemUtils.when(() -> ItemUtils.spawnItem(player, defenderLocation,
                        defenderWeapon, ItemSpawnReason.UNARMED_DISARMED_ITEM))
                        .thenReturn(droppedItem);
                when(advancedConfig.getDisarmProtected()).thenReturn(true);
                final FixedMetadataValue defenderMetadata =
                        Mockito.mock(FixedMetadataValue.class);
                when(defenderMmoPlayer.getPlayerMetadata()).thenReturn(defenderMetadata);

                // When - the disarm lands at full strength
                unarmedManager.disarmCheck(defender, 1.0);

                // Then - the weapon drops, protected against pickup by others
                verify(droppedItem).setMetadata(MetadataConstants.METADATA_KEY_DISARMED_ITEM,
                        defenderMetadata);

                // And - the defender's hand empties and they are informed
                final ArgumentCaptor<ItemStack> emptyHand =
                        ArgumentCaptor.forClass(ItemStack.class);
                verify(defenderInventory).setItemInMainHand(emptyHand.capture());
                assertThat(emptyHand.getValue().getType()).isEqualTo(Material.AIR);
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        defender, NotificationType.SUBSKILL_MESSAGE, "Skills.Disarmed"));
            }
        }

        @Test
        void ironGripShouldSaveTheDefender() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class);
                    MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class)) {
                // Given - a winning disarm roll but the defender's iron grip also wins
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.UNARMED_DISARM, mmoPlayer, 1.0)).thenReturn(true);
                when(Permissions.isSubSkillEnabled(defender, SubSkillType.UNARMED_IRON_GRIP))
                        .thenReturn(true);
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.UNARMED_IRON_GRIP, defenderMmoPlayer)).thenReturn(true);

                // When - the disarm lands
                unarmedManager.disarmCheck(defender, 1.0);

                // Then - the weapon stays in hand and both sides are informed
                itemUtils.verifyNoInteractions();
                verify(defenderInventory, never()).setItemInMainHand(any(ItemStack.class));
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        defender, NotificationType.SUBSKILL_MESSAGE,
                        "Unarmed.Ability.IronGrip.Defender"));
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        player, NotificationType.SUBSKILL_MESSAGE,
                        "Unarmed.Ability.IronGrip.Attacker"));
            }
        }

        @Test
        void cancelledDisarmEventsShouldDropNothing() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class);
                    MockedStatic<ItemUtils> itemUtils = mockStatic(ItemUtils.class)) {
                // Given - a winning disarm roll but another plugin cancels the disarm
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.UNARMED_DISARM, mmoPlayer, 1.0)).thenReturn(true);
                mockedEventUtils = mockStatic(EventUtils.class);
                final McMMOPlayerDisarmEvent disarmEvent =
                        Mockito.mock(McMMOPlayerDisarmEvent.class);
                when(disarmEvent.isCancelled()).thenReturn(true);
                mockedEventUtils.when(() -> EventUtils.callDisarmEvent(defender))
                        .thenReturn(disarmEvent);

                // When - the disarm lands
                unarmedManager.disarmCheck(defender, 1.0);

                // Then - nothing drops and the hand is untouched
                itemUtils.verifyNoInteractions();
                verify(defenderInventory, never()).setItemInMainHand(any(ItemStack.class));
            }
        }
    }

    @Nested
    class DeflectAndSteelArm {
        @Test
        void successfulDeflectShouldInformThePlayer() {
            try (MockedStatic<ProbabilityUtil> probabilityUtil =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a winning deflect roll
                probabilityUtil.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.UNARMED_ARROW_DEFLECT, mmoPlayer)).thenReturn(true);

                // When / Then - the arrow bounces and the player is told
                assertThat(unarmedManager.deflectCheck()).isTrue();
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        player, NotificationType.SUBSKILL_MESSAGE, "Combat.ArrowDeflect"));
            }
        }

        @Test
        void failedDeflectShouldLetTheArrowThrough() {
            try (MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                assertThat(unarmedManager.deflectCheck()).isFalse();
            }
        }

        /**
         * The Steel Arm damage curve: 0.5 plus half the rank, with an extra flat bonus that
         * starts at rank 18 and grows by one per rank after that.
         */
        @ParameterizedTest
        @CsvSource({
                "1, 1.0",
                "5, 3.0",
                "17, 9.0",
                "18, 10.5",
                "20, 13.5",
        })
        void steelArmDamageShouldFollowTheRankCurve(int rank, double expectedDamage) {
            // Given - Steel Arm Style at the given rank without a custom override
            Mockito.when(RankUtils.getRank(player, SubSkillType.UNARMED_STEEL_ARM_STYLE))
                    .thenReturn(rank);
            when(advancedConfig.isSteelArmDamageCustom()).thenReturn(false);

            // When / Then - the damage follows the curve
            assertThat(unarmedManager.getSteelArmStyleDamage())
                    .isCloseTo(expectedDamage, offset(1e-9));
        }

        @Test
        void customSteelArmDamageShouldUseTheConfiguredOverride() {
            // Given - a custom override for rank 5
            Mockito.when(RankUtils.getRank(player, SubSkillType.UNARMED_STEEL_ARM_STYLE))
                    .thenReturn(5);
            when(advancedConfig.isSteelArmDamageCustom()).thenReturn(true);
            when(advancedConfig.getSteelArmOverride(5, 3.0)).thenReturn(7.0);

            // When / Then - the override wins
            assertThat(unarmedManager.getSteelArmStyleDamage()).isEqualTo(7.0);
        }

        @Test
        void failedSteelArmActivationShouldAddNothing() {
            try (MockedStatic<ProbabilityUtil> ignored = mockStatic(ProbabilityUtil.class)) {
                assertThat(unarmedManager.calculateSteelArmStyleDamage()).isZero();
            }
        }
    }
}
