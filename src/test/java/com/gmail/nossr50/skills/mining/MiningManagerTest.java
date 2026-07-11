package com.gmail.nossr50.skills.mining;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.TestRegistryBootstrap;
import com.gmail.nossr50.api.FakeBlockBreakEventType;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.Probability;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class MiningManagerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(MiningManagerTest.class.getName());

    private MiningManager miningManager;
    private ItemStack heldItem;
    private PlatformScheduler scheduler;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        TestRegistryBootstrap.bootstrap(mockedBukkit);

        final FoliaLib foliaLib = mock(FoliaLib.class);
        scheduler = mock(PlatformScheduler.class);
        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
        when(foliaLib.getScheduler()).thenReturn(scheduler);

        heldItem = mock(ItemStack.class);
        when(playerInventory.getItemInMainHand()).thenReturn(heldItem);

        miningManager = Mockito.spy(new MiningManager(mmoPlayer));
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * The remote detonation TNT search must honor the distance configured in advanced.yml
     * instead of a hardcoded maximum, so admins can limit how far away players may detonate.
     */
    @ParameterizedTest(name = "configuredDistance={0}")
    @ValueSource(ints = {1, 5, 30, 100})
    void remoteDetonationShouldScanForTntUpToConfiguredDistance(final int configuredDistance) {
        // Given - an admin configured a maximum remote detonation distance
        when(advancedConfig.getRemoteDetonationDistanceLimit()).thenReturn(configuredDistance);

        // And - the player is aiming at a non-TNT block somewhere within scan range
        final Block targetBlock = mock(Block.class);
        when(targetBlock.getType()).thenReturn(Material.STONE);
        when(player.getTargetBlock(anySet(), anyInt())).thenReturn(targetBlock);

        // When - the player attempts a remote detonation
        miningManager.remoteDetonation();

        // Then - the TNT target search is limited to the configured distance
        verify(player).getTargetBlock(anySet(), eq(configuredDistance));
    }

    @Nested
    class BlockXpAndDrops {
        private Block minedBlock;

        @BeforeEach
        void setUpMinedBlock() {
            minedBlock = mock(Block.class);
            when(minedBlock.getType()).thenReturn(Material.IRON_ORE);
            when(ExperienceConfig.getInstance().getXp(PrimarySkillType.MINING, minedBlock))
                    .thenReturn(400);
            when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.MINING, Material.IRON_ORE))
                    .thenReturn(true);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.MINING_DOUBLE_DROPS))
                    .thenReturn(true);
            // Mother Lode stays out of the way unless a test opts in
            when(Permissions.canUseSubSkill(player, SubSkillType.MINING_MOTHER_LODE))
                    .thenReturn(false);
            doNothing().when(miningManager).applyXpGain(anyFloat(), any(), any());
        }

        @Test
        void miningABlockShouldAlwaysPayXp() {
            try (final MockedStatic<BlockUtils> ignored = mockStatic(BlockUtils.class);
                    final MockedStatic<ProbabilityUtil> ignoredRng =
                            mockStatic(ProbabilityUtil.class)) {
                // When - the mined block is processed
                miningManager.miningBlockCheck(minedBlock);

                // Then - the configured XP lands even if no bonus drops roll
                verify(miningManager).applyXpGain(400f, XPGainReason.PVE, XPGainSource.SELF);
            }
        }

        @Test
        void missingDoubleDropPermissionShouldStopAfterXp() {
            try (final MockedStatic<BlockUtils> mockedBlockUtils =
                    mockStatic(BlockUtils.class)) {
                // Given - no double drop permission
                when(Permissions.isSubSkillEnabled(player, SubSkillType.MINING_DOUBLE_DROPS))
                        .thenReturn(false);

                // When - the mined block is processed
                miningManager.miningBlockCheck(minedBlock);

                // Then - no bonus drops are marked
                mockedBlockUtils.verifyNoInteractions();
            }
        }

        @Test
        void superBreakerShouldWearTheToolDown() {
            try (final MockedStatic<BlockUtils> ignored = mockStatic(BlockUtils.class);
                    final MockedStatic<ProbabilityUtil> ignoredRng =
                            mockStatic(ProbabilityUtil.class);
                    final MockedStatic<SkillUtils> mockedSkillUtils =
                            mockStatic(SkillUtils.class)) {
                // Given - Super Breaker is active with a configured tool damage
                doReturn(true).when(mmoPlayer).getAbilityMode(SuperAbilityType.SUPER_BREAKER);
                when(generalConfig.getAbilityToolDamage()).thenReturn(1);

                // When - the mined block is processed
                miningManager.miningBlockCheck(minedBlock);

                // Then - the pickaxe takes the configured durability hit
                mockedSkillUtils.verify(
                        () -> SkillUtils.handleDurabilityChange(heldItem, 1));
            }
        }

        @Test
        void disabledDoubleDropsForTheBlockShouldNotRoll() {
            try (final MockedStatic<BlockUtils> mockedBlockUtils =
                    mockStatic(BlockUtils.class)) {
                // Given - double drops are disabled for this block in the config
                when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.MINING,
                        Material.IRON_ORE)).thenReturn(false);

                // When - the mined block is processed
                miningManager.miningBlockCheck(minedBlock);

                // Then - no bonus drops are marked
                mockedBlockUtils.verifyNoInteractions();
            }
        }

        @Test
        void silkTouchShouldBlockBonusDropsWhenDisallowed() {
            try (final MockedStatic<BlockUtils> mockedBlockUtils =
                    mockStatic(BlockUtils.class)) {
                // Given - a silk touch pickaxe while silk touch doubles are disabled
                when(heldItem.containsEnchantment(Enchantment.SILK_TOUCH)).thenReturn(true);
                when(advancedConfig.getDoubleDropSilkTouchEnabled()).thenReturn(false);

                // When - the mined block is processed
                miningManager.miningBlockCheck(minedBlock);

                // Then - no bonus drops are marked
                mockedBlockUtils.verifyNoInteractions();
            }
        }

        @Test
        void successfulDoubleDropRollShouldMarkTheBlock() {
            try (final MockedStatic<BlockUtils> mockedBlockUtils =
                    mockStatic(BlockUtils.class);
                    final MockedStatic<ProbabilityUtil> mockedProbability =
                            mockStatic(ProbabilityUtil.class)) {
                // Given - the double drop roll succeeds
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.MINING_DOUBLE_DROPS, mmoPlayer)).thenReturn(true);

                // When - the mined block is processed
                miningManager.miningBlockCheck(minedBlock);

                // Then - the block is marked for one extra drop
                mockedBlockUtils.verify(() -> BlockUtils.markDropsAsBonus(minedBlock, false));
            }
        }

        @Test
        void superBreakerDoubleDropsShouldTripleWhenAllowed() {
            try (final MockedStatic<BlockUtils> mockedBlockUtils =
                    mockStatic(BlockUtils.class);
                    final MockedStatic<ProbabilityUtil> mockedProbability =
                            mockStatic(ProbabilityUtil.class);
                    final MockedStatic<SkillUtils> ignored = mockStatic(SkillUtils.class)) {
                // Given - Super Breaker is active, triple drops are allowed, and the roll wins
                doReturn(true).when(mmoPlayer).getAbilityMode(SuperAbilityType.SUPER_BREAKER);
                when(advancedConfig.getAllowMiningTripleDrops()).thenReturn(true);
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.MINING_DOUBLE_DROPS, mmoPlayer)).thenReturn(true);

                // When - the mined block is processed
                miningManager.miningBlockCheck(minedBlock);

                // Then - the block is marked for triple drops
                mockedBlockUtils.verify(() -> BlockUtils.markDropsAsBonus(minedBlock, true));
            }
        }

        @Test
        void motherLodeShouldPayTripleDropsOnSuccess() {
            try (final MockedStatic<BlockUtils> mockedBlockUtils =
                    mockStatic(BlockUtils.class);
                    final MockedStatic<ProbabilityUtil> mockedProbability =
                            mockStatic(ProbabilityUtil.class)) {
                // Given - Mother Lode is available and its roll succeeds
                when(Permissions.canUseSubSkill(player, SubSkillType.MINING_MOTHER_LODE))
                        .thenReturn(true);
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.MINING_MOTHER_LODE, mmoPlayer)).thenReturn(true);

                // When - the mined block is processed
                miningManager.miningBlockCheck(minedBlock);

                // Then - the block is marked with two bonus drops
                mockedBlockUtils.verify(() -> BlockUtils.markDropsAsBonus(minedBlock, 2));
            }
        }

        @Test
        void failedMotherLodeShouldFallBackToTheDoubleDropRoll() {
            try (final MockedStatic<BlockUtils> mockedBlockUtils =
                    mockStatic(BlockUtils.class);
                    final MockedStatic<ProbabilityUtil> mockedProbability =
                            mockStatic(ProbabilityUtil.class)) {
                // Given - Mother Lode is available but its roll fails, and the double roll wins
                when(Permissions.canUseSubSkill(player, SubSkillType.MINING_MOTHER_LODE))
                        .thenReturn(true);
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.MINING_MOTHER_LODE, mmoPlayer)).thenReturn(false);
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.MINING_DOUBLE_DROPS, mmoPlayer)).thenReturn(true);

                // When - the mined block is processed
                miningManager.miningBlockCheck(minedBlock);

                // Then - the block still gets its regular double drop
                mockedBlockUtils.verify(() -> BlockUtils.markDropsAsBonus(minedBlock, false));
            }
        }
    }

    @Nested
    class DetonationGate {
        @BeforeEach
        void setUpDetonator() {
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.MINING_BLAST_MINING))
                    .thenReturn(true);
            when(player.isSneaking()).thenReturn(true);
            when(Permissions.remoteDetonation(player)).thenReturn(true);
            when(heldItem.getType()).thenReturn(Material.DIAMOND_PICKAXE);
        }

        @Test
        void sneakingWithAPickaxeShouldAllowDetonation() {
            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                mockedItemUtils.when(() -> ItemUtils.isPickaxe(heldItem)).thenReturn(true);

                assertThat(miningManager.canDetonate()).isTrue();
            }
        }

        @Test
        void standingUprightShouldNotDetonate() {
            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                mockedItemUtils.when(() -> ItemUtils.isPickaxe(heldItem)).thenReturn(true);
                when(player.isSneaking()).thenReturn(false);

                assertThat(miningManager.canDetonate()).isFalse();
            }
        }

        @Test
        void theConfiguredDetonatorShouldWorkWithoutAPickaxe() {
            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                mockedItemUtils.when(() -> ItemUtils.isPickaxe(heldItem)).thenReturn(false);
                when(generalConfig.getDetonatorItem()).thenReturn(Material.FLINT_AND_STEEL);
                when(heldItem.getType()).thenReturn(Material.FLINT_AND_STEEL);

                assertThat(miningManager.canDetonate()).isTrue();
            }
        }

        @Test
        void unrelatedItemsShouldNotDetonate() {
            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                mockedItemUtils.when(() -> ItemUtils.isPickaxe(heldItem)).thenReturn(false);
                when(generalConfig.getDetonatorItem()).thenReturn(Material.FLINT_AND_STEEL);
                when(heldItem.getType()).thenReturn(Material.STICK);

                assertThat(miningManager.canDetonate()).isFalse();
            }
        }

        @Test
        void missingPermissionShouldNotDetonate() {
            try (final MockedStatic<ItemUtils> mockedItemUtils = mockStatic(ItemUtils.class)) {
                mockedItemUtils.when(() -> ItemUtils.isPickaxe(heldItem)).thenReturn(true);
                when(Permissions.remoteDetonation(player)).thenReturn(false);

                assertThat(miningManager.canDetonate()).isFalse();
            }
        }
    }

    @Nested
    class RemoteDetonation {
        private Block tntBlock;

        @BeforeEach
        void setUpTntTarget() {
            when(advancedConfig.getRemoteDetonationDistanceLimit()).thenReturn(100);
            tntBlock = mock(Block.class);
            when(tntBlock.getType()).thenReturn(Material.TNT);
            when(tntBlock.getLocation()).thenReturn(new Location(world, 10, 64, 10));
            when(player.getTargetBlock(anySet(), anyInt())).thenReturn(tntBlock);
            when(player.getWorld()).thenReturn(world);
        }

        @Test
        void activeCooldownShouldWarnInsteadOfDetonating() {
            // Given - blast mining is still on cooldown
            doReturn(30).when(mmoPlayer)
                    .calculateTimeRemaining(SuperAbilityType.BLAST_MINING);

            // When - the player attempts a remote detonation
            miningManager.remoteDetonation();

            // Then - the player is told to rest and nothing explodes
            notificationManager.verify(() -> NotificationManager.sendPlayerInformation(player,
                    NotificationType.ABILITY_COOLDOWN, "Skills.TooTired", "30"));
            verify(world, never()).spawn(any(Location.class), eq(TNTPrimed.class));
        }

        @Test
        @SuppressWarnings("unchecked")
        void successfulDetonationShouldPrimeTheTntInstantly() {
            // Given - the cooldown is over and the fake block break is allowed
            doReturn(0).when(mmoPlayer).calculateTimeRemaining(SuperAbilityType.BLAST_MINING);
            mockedEventUtils = mockStatic(EventUtils.class);
            mockedEventUtils.when(() -> EventUtils.simulateBlockBreak(tntBlock, player,
                    FakeBlockBreakEventType.FAKE)).thenReturn(true);
            final TNTPrimed tnt = mock(TNTPrimed.class);
            when(world.spawn(tntBlock.getLocation(), TNTPrimed.class)).thenReturn(tnt);
            final org.bukkit.metadata.MetadataValue playerMetadata =
                    mmoPlayer.getPlayerMetadata();

            // When - the player detonates the TNT remotely
            miningManager.remoteDetonation();

            // Then - the TNT is primed instantly, tracked, and owned by the player
            verify(tnt).setMetadata(MetadataConstants.METADATA_KEY_TRACKED_TNT, playerMetadata);
            verify(tnt).setFuseTicks(0);
            verify(tnt).setSource(player);
            verify(tntBlock).setType(Material.AIR);

            // And - the blast mining cooldown starts ticking
            verify(mmoPlayer).setAbilityDATS(eq(SuperAbilityType.BLAST_MINING), anyLong());
            verify(scheduler).runAtEntityLater(eq(player), any(Consumer.class), anyLong());
        }

        @Test
        void deniedFakeBlockBreakShouldNotDetonate() {
            // Given - the cooldown is over but protection denies the fake break
            doReturn(0).when(mmoPlayer).calculateTimeRemaining(SuperAbilityType.BLAST_MINING);
            mockedEventUtils = mockStatic(EventUtils.class);
            mockedEventUtils.when(() -> EventUtils.simulateBlockBreak(tntBlock, player,
                    FakeBlockBreakEventType.FAKE)).thenReturn(false);

            // When - the player attempts a remote detonation
            miningManager.remoteDetonation();

            // Then - nothing explodes
            verify(world, never()).spawn(any(Location.class), eq(TNTPrimed.class));
        }
    }

    @Nested
    class BlastDropProcessing {
        private EntityExplodeEvent explodeEvent;
        private Block oreBlock;

        @BeforeEach
        void setUpExplosion() {
            explodeEvent = mock(EntityExplodeEvent.class);
            oreBlock = mock(Block.class);
            when(oreBlock.getType()).thenReturn(Material.IRON_ORE);
            when(ExperienceConfig.getInstance().getXp(PrimarySkillType.MINING, oreBlock))
                    .thenReturn(400);
            when(RankUtils.getRank(player, SubSkillType.MINING_BLAST_MINING)).thenReturn(1);
            doNothing().when(miningManager).applyXpGain(anyFloat(), any(), any());
        }

        @Test
        void zeroYieldExplosionsShouldBeIgnored() {
            // When - a zero-yield explosion is processed
            miningManager.blastMiningDropProcessing(0f, explodeEvent);

            // Then - the event is left alone entirely
            verify(explodeEvent, never()).blockList();
            verify(miningManager, never()).applyXpGain(anyFloat(), any(), any());
        }

        @Test
        void oresShouldPayXpAndDropThroughTheYieldRolls() {
            try (final MockedStatic<BlockUtils> mockedBlockUtils = mockStatic(BlockUtils.class);
                    final MockedStatic<Probability> mockedProbability =
                            mockStatic(Probability.class);
                    final MockedStatic<ItemUtils> mockedItemUtils =
                            mockStatic(ItemUtils.class)) {
                // Given - one ore in the blast with a guaranteed drop roll
                when(explodeEvent.blockList()).thenReturn(List.of(oreBlock));
                mockedBlockUtils.when(() -> BlockUtils.isOre(oreBlock)).thenReturn(true);
                final Probability certain = mock(Probability.class);
                when(certain.evaluate()).thenReturn(true);
                mockedProbability.when(() -> Probability.ofValue(anyDouble()))
                        .thenReturn(certain);
                mockedItemUtils.when(() -> ItemUtils.isPickaxe(heldItem)).thenReturn(true);
                final ItemStack oreDrop = mock(ItemStack.class);
                when(oreBlock.getDrops(heldItem)).thenReturn(List.of(oreDrop));

                // When - a full-yield explosion is processed
                miningManager.blastMiningDropProcessing(1.0f, explodeEvent);

                // Then - the ore pays XP and drops its pickaxe drops
                verify(miningManager).applyXpGain(400f, XPGainReason.PVE, XPGainSource.SELF);
                mockedItemUtils.verify(() -> ItemUtils.spawnItems(eq(player),
                        any(Location.class), eq(List.of(oreDrop)), anyCollection(),
                        eq(ItemSpawnReason.BLAST_MINING_ORES)));

                // And - vanilla explosion drops are suppressed in favor of ours
                verify(explodeEvent).setYield(0f);
            }
        }

        @Test
        void bonusDropTiersShouldMultiplyTheOreDrops() {
            try (final MockedStatic<BlockUtils> mockedBlockUtils = mockStatic(BlockUtils.class);
                    final MockedStatic<Probability> mockedProbability =
                            mockStatic(Probability.class);
                    final MockedStatic<ItemUtils> mockedItemUtils =
                            mockStatic(ItemUtils.class)) {
                // Given - blast mining rank 3 (x2 drops) with bonus drops enabled and every
                // roll succeeding
                when(RankUtils.getRank(player, SubSkillType.MINING_BLAST_MINING)).thenReturn(3);
                when(advancedConfig.isBlastMiningBonusDropsEnabled()).thenReturn(true);
                when(explodeEvent.blockList()).thenReturn(List.of(oreBlock));
                mockedBlockUtils.when(() -> BlockUtils.isOre(oreBlock)).thenReturn(true);
                final Probability certain = mock(Probability.class);
                when(certain.evaluate()).thenReturn(true);
                mockedProbability.when(() -> Probability.ofValue(anyDouble()))
                        .thenReturn(certain);
                mockedItemUtils.when(() -> ItemUtils.isPickaxe(heldItem)).thenReturn(true);
                final ItemStack oreDrop = mock(ItemStack.class);
                when(oreBlock.getDrops(heldItem)).thenReturn(List.of(oreDrop));

                // When - the explosion is processed
                miningManager.blastMiningDropProcessing(1.0f, explodeEvent);

                // Then - one bonus drop lands on top of the regular one
                mockedItemUtils.verify(() -> ItemUtils.spawnItems(eq(player),
                        any(Location.class), eq(List.of(oreDrop)), anyCollection(),
                        eq(ItemSpawnReason.BLAST_MINING_ORES_BONUS_DROP)));
            }
        }

        @Test
        void debrisShouldOccasionallyDropTheBlockItself() {
            try (final MockedStatic<BlockUtils> mockedBlockUtils = mockStatic(BlockUtils.class);
                    final MockedStatic<Probability> mockedProbability =
                            mockStatic(Probability.class);
                    final MockedStatic<ItemUtils> mockedItemUtils =
                            mockStatic(ItemUtils.class)) {
                // Given - a non-ore debris block and a winning 10% debris roll
                final Block debrisBlock = mock(Block.class);
                when(debrisBlock.getType()).thenReturn(Material.STONE);
                when(explodeEvent.blockList()).thenReturn(List.of(debrisBlock));
                final Probability certain = mock(Probability.class);
                when(certain.evaluate()).thenReturn(true);
                mockedProbability.when(() -> Probability.ofPercent(10)).thenReturn(certain);

                // When - the explosion is processed
                miningManager.blastMiningDropProcessing(1.0f, explodeEvent);

                // Then - the debris block itself drops
                mockedItemUtils.verify(() -> ItemUtils.spawnItem(eq(player),
                        any(Location.class), any(ItemStack.class),
                        eq(ItemSpawnReason.BLAST_MINING_DEBRIS_NON_ORES)));
            }
        }
    }

    @Nested
    class BlastModifiers {
        @Test
        void biggerBombsShouldWidenTheBlastRadius() {
            // Given - blast mining rank 4 with a 1.5 radius modifier
            when(RankUtils.getRank(player, SubSkillType.MINING_BLAST_MINING)).thenReturn(4);
            when(advancedConfig.getBlastRadiusModifier(4)).thenReturn(1.5);

            // When / Then - the radius grows by the modifier
            assertThat(miningManager.biggerBombs(2.0f)).isCloseTo(3.5f, within(1e-6f));
        }

        @Test
        void demolitionsExpertiseShouldReduceBlastDamage() {
            // Given - blast mining rank 5 with a 25% damage decrease
            when(RankUtils.getRank(player, SubSkillType.MINING_BLAST_MINING)).thenReturn(5);
            when(advancedConfig.getBlastDamageDecrease(5)).thenReturn(25.0);

            // When / Then - the damage shrinks by a quarter
            assertThat(miningManager.processDemolitionsExpertise(40.0))
                    .isCloseTo(30.0, within(1e-9));
        }

        @Test
        void oreBonusShouldConvertThePercentToAFraction() {
            // Given - a 35% ore bonus at rank 2
            when(RankUtils.getRank(player, SubSkillType.MINING_BLAST_MINING)).thenReturn(2);
            when(advancedConfig.getOreBonus(2)).thenReturn(35.0);

            // When / Then - the bonus is expressed as a fraction
            assertThat(miningManager.getOreBonus()).isCloseTo(0.35f, within(1e-6f));
        }

        /**
         * The drop multiplier tiers: ranks 1-2 pay one drop, 3-6 pay two, 7-8 pay three.
         */
        @ParameterizedTest
        @CsvSource({
                "1, 1",
                "2, 1",
                "3, 2",
                "6, 2",
                "7, 3",
                "8, 3",
                "0, 0",
        })
        void dropMultiplierShouldFollowTheRankTiers(int rank, int expectedMultiplier) {
            // Given - bonus drops are enabled at the given rank
            when(advancedConfig.isBlastMiningBonusDropsEnabled()).thenReturn(true);
            when(RankUtils.getRank(player, SubSkillType.MINING_BLAST_MINING)).thenReturn(rank);

            // When / Then - the multiplier follows the tier table
            assertThat(miningManager.getDropMultiplier()).isEqualTo(expectedMultiplier);
        }

        @Test
        void disabledBonusDropsShouldZeroTheMultiplier() {
            // Given - bonus drops are disabled at a high rank
            when(advancedConfig.isBlastMiningBonusDropsEnabled()).thenReturn(false);
            when(RankUtils.getRank(player, SubSkillType.MINING_BLAST_MINING)).thenReturn(8);

            // When / Then - no bonus drops at all
            assertThat(miningManager.getDropMultiplier()).isZero();
        }
    }

    @Nested
    class IllegalDrops {
        @Test
        void protectedBlocksShouldNeverDropFromBlasts() {
            assertThat(miningManager.isDropIllegal(Material.SPAWNER)).isTrue();
            assertThat(miningManager.isDropIllegal(Material.BUDDING_AMETHYST)).isTrue();
            assertThat(miningManager.isDropIllegal(Material.INFESTED_STONE)).isTrue();
        }

        @Test
        void ordinaryBlocksShouldDropNormally() {
            assertThat(miningManager.isDropIllegal(Material.IRON_ORE)).isFalse();
        }
    }
}
