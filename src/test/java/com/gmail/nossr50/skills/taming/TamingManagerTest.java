package com.gmail.nossr50.skills.taming;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.events.skills.taming.McMMOPlayerTameEntityEvent;
import com.gmail.nossr50.config.PersistentDataConfig;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.metadata.MobMetaFlagType;
import com.gmail.nossr50.util.AttributeMapper;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.MobMetadataUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class TamingManagerTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(TamingManagerTest.class.getName());

    private static final int COTW_ITEM_COST = 3;
    private static final int COTW_SUMMON_CAP = 2;
    private static final int COTW_LIFESPAN_SECONDS = 240;

    private TamingManager tamingManager;
    private PlatformScheduler scheduler;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);

        final FoliaLib foliaLib = mock(FoliaLib.class);
        scheduler = mock(PlatformScheduler.class);
        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
        when(foliaLib.getScheduler()).thenReturn(scheduler);

        // MobMetadataUtils resolves its namespaced keys and persistence config at class load;
        // bootstrap it here so the summon tests can flag mobs for real
        when(mcMMO.p.getName()).thenReturn("mcMMO");
        try (final MockedStatic<PersistentDataConfig> mockedPersistentData =
                mockStatic(PersistentDataConfig.class)) {
            mockedPersistentData.when(PersistentDataConfig::getInstance)
                    .thenReturn(mock(PersistentDataConfig.class));
            Class.forName("com.gmail.nossr50.util.MobMetadataUtils");
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }

        // The COTW lookup tables are static caches built from the config by the first manager
        // constructed in the JVM; reset them so this class's config wiring always applies
        resetCotwStaticCaches();
        for (final CallOfTheWildType summonType : CallOfTheWildType.values()) {
            final String configEntry = summonType.getConfigEntityTypeEntry();
            when(generalConfig.getTamingCOTWMaterial(configEntry))
                    .thenReturn(summonItemFor(summonType));
            when(generalConfig.getTamingCOTWCost(configEntry)).thenReturn(COTW_ITEM_COST);
            when(generalConfig.getTamingCOTWAmount(configEntry)).thenReturn(1);
            when(generalConfig.getTamingCOTWLength(configEntry))
                    .thenReturn(COTW_LIFESPAN_SECONDS);
            when(generalConfig.getTamingCOTWMaxAmount(configEntry)).thenReturn(COTW_SUMMON_CAP);
        }

        tamingManager = Mockito.spy(new TamingManager(mmoPlayer));
        TamingManager.initStaticCaches();
    }

    @AfterEach
    void tearDown() {
        resetCotwStaticCaches();
        cleanUpStaticMocks();
    }

    private static Material summonItemFor(CallOfTheWildType summonType) {
        return switch (summonType) {
            case CAT -> Material.COD;
            case WOLF -> Material.BONE;
            case HORSE -> Material.APPLE;
        };
    }

    private void resetCotwStaticCaches() {
        try {
            for (final String cacheField : List.of("summoningItems", "cotwSummonDataProperties")) {
                final Field field = TamingManager.class.getDeclaredField(cacheField);
                field.setAccessible(true);
                field.set(null, null);
            }
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    void attackTargetShouldUseTargetNearbyEntitiesAndCommandOnlyEligibleOwnedWolves() {
        // Given - a valid ranged target in the same world with mixed nearby entities.
        final LivingEntity target = mock(LivingEntity.class);
        final Entity nonWolfEntity = mock(Entity.class);
        final Wolf untamedWolf = mock(Wolf.class);
        final Wolf foreignWolf = mock(Wolf.class);
        final Wolf sittingOwnedWolf = mock(Wolf.class);
        final Wolf activeOwnedWolf = mock(Wolf.class);
        final Player otherPlayer = mock(Player.class);

        when(player.getWorld()).thenReturn(world);
        when(target.getWorld()).thenReturn(world);
        when(target.getNearbyEntities(5.0, 5.0, 5.0)).thenReturn(List.of(
                nonWolfEntity,
                untamedWolf,
                foreignWolf,
                sittingOwnedWolf,
                activeOwnedWolf
        ));

        when(nonWolfEntity.getType()).thenReturn(EntityType.ZOMBIE);
        when(untamedWolf.getType()).thenReturn(EntityType.WOLF);
        when(foreignWolf.getType()).thenReturn(EntityType.WOLF);
        when(sittingOwnedWolf.getType()).thenReturn(EntityType.WOLF);
        when(activeOwnedWolf.getType()).thenReturn(EntityType.WOLF);

        when(untamedWolf.isTamed()).thenReturn(false);

        when(foreignWolf.isTamed()).thenReturn(true);
        when(foreignWolf.getOwner()).thenReturn(otherPlayer);

        when(sittingOwnedWolf.isTamed()).thenReturn(true);
        when(sittingOwnedWolf.getOwner()).thenReturn(player);
        when(sittingOwnedWolf.isSitting()).thenReturn(true);

        when(activeOwnedWolf.isTamed()).thenReturn(true);
        when(activeOwnedWolf.getOwner()).thenReturn(player);
        when(activeOwnedWolf.isSitting()).thenReturn(false);

        // When - taming attack assist processing runs for the hit target.
        assertThatCode(() -> tamingManager.attackTarget(target)).doesNotThrowAnyException();

        // Then - lookup should be target-centered and only eligible owned wolves should be commanded.
        verify(target).getNearbyEntities(5.0, 5.0, 5.0);
        verify(player, never()).getNearbyEntities(anyDouble(), anyDouble(), anyDouble());
        verify(activeOwnedWolf).setTarget(target);
        verify(untamedWolf, never()).setTarget(target);
        verify(foreignWolf, never()).setTarget(target);
        verify(sittingOwnedWolf, never()).setTarget(target);
    }

    @Test
    void attackTargetShouldSkipNearbyLookupWhenTargetIsInDifferentWorld() {
        // Given - an attack target in a different world than the attacking player.
        final LivingEntity target = mock(LivingEntity.class);
        final World otherWorld = mock(World.class);

        when(player.getWorld()).thenReturn(world);
        when(target.getWorld()).thenReturn(otherWorld);

        // When - taming attack assist processing runs.
        tamingManager.attackTarget(target);

        // Then - no nearby-entity lookup should run across worlds.
        verify(target, never()).getNearbyEntities(anyDouble(), anyDouble(), anyDouble());
        verify(player, never()).getNearbyEntities(anyDouble(), anyDouble(), anyDouble());
    }

    @Nested
    class AbilityGates {
        /**
         * Holy Hound has its own rank configuration (level 35 Standard / 350 RetroMode) and
         * must gate on its own unlock, not on Environmentally Aware's much earlier one.
         */
        @Test
        void holyHoundShouldStayLockedUntilItsOwnUnlock() {
            // Given - Environmentally Aware is unlocked but Holy Hound is not
            when(RankUtils.hasUnlockedSubskill(player,
                    SubSkillType.TAMING_ENVIRONMENTALLY_AWARE)).thenReturn(true);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.TAMING_HOLY_HOUND))
                    .thenReturn(false);

            // When / Then - Holy Hound is still locked
            assertThat(tamingManager.canUseHolyHound()).isFalse();
        }

        @Test
        void holyHoundShouldUnlockWithItsOwnRank() {
            // Given - Holy Hound is unlocked while Environmentally Aware is not
            when(RankUtils.hasUnlockedSubskill(player,
                    SubSkillType.TAMING_ENVIRONMENTALLY_AWARE)).thenReturn(false);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.TAMING_HOLY_HOUND))
                    .thenReturn(true);

            // When / Then - Holy Hound works
            assertThat(tamingManager.canUseHolyHound()).isTrue();
        }
    }

    @Nested
    class TamingXp {
        @Test
        void tamingShouldPayTheConfiguredXp() {
            // Given - taming a wolf pays 100 XP
            final LivingEntity tamedWolf = mock(LivingEntity.class);
            when(tamedWolf.getType()).thenReturn(EntityType.WOLF);
            when(ExperienceConfig.getInstance().getTamingXP(EntityType.WOLF)).thenReturn(100);
            doNothing().when(tamingManager).applyXpGain(anyFloat(), any(), any());

            // When - the tame is awarded
            tamingManager.awardTamingXP(tamedWolf);

            // Then - the XP lands as self-inflicted PVE XP
            verify(tamingManager).applyXpGain(100f, XPGainReason.PVE, XPGainSource.SELF);
        }

        @Test
        void cancelledTameEventsShouldPayNothing() {
            // Given - another plugin cancels the tame event
            final LivingEntity tamedWolf = mock(LivingEntity.class);
            when(tamedWolf.getType()).thenReturn(EntityType.WOLF);
            when(ExperienceConfig.getInstance().getTamingXP(EntityType.WOLF)).thenReturn(100);
            doAnswer(invocation -> {
                ((McMMOPlayerTameEntityEvent) invocation.getArgument(0)).setCancelled(true);
                return null;
            }).when(pluginManager).callEvent(any(McMMOPlayerTameEntityEvent.class));

            // When - the tame is awarded
            tamingManager.awardTamingXP(tamedWolf);

            // Then - no XP is paid
            verify(tamingManager, never()).applyXpGain(anyFloat(), any(), any());
        }
    }

    @Nested
    class FastFoodService {
        private Wolf wolf;

        @BeforeEach
        void setUpWolf() {
            wolf = mock(Wolf.class);
            when(wolf.getHealth()).thenReturn(10.0);
            when(wolf.getMaxHealth()).thenReturn(20.0);
        }

        @Test
        void failedRollShouldNotHeal() {
            try (final MockedStatic<ProbabilityUtil> ignored =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a Fast Food Service roll that fails
                // When - the wolf absorbs 4 damage
                tamingManager.fastFoodService(wolf, 4.0);

                // Then - no healing happens
                verify(wolf, never()).setHealth(anyDouble());
            }
        }

        @Test
        void successfulRollShouldHealTheDamageDealt() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a Fast Food Service roll that succeeds
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.TAMING_FAST_FOOD_SERVICE, mmoPlayer)).thenReturn(true);

                // When - the wolf absorbs 4 damage
                tamingManager.fastFoodService(wolf, 4.0);

                // Then - the wolf heals by the damage dealt
                verify(wolf).setHealth(14.0);
            }
        }

        @Test
        void healingShouldClampAtMaxHealth() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a nearly full wolf and a successful roll
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.TAMING_FAST_FOOD_SERVICE, mmoPlayer)).thenReturn(true);
                when(wolf.getHealth()).thenReturn(19.0);

                // When - the wolf absorbs 8 damage
                tamingManager.fastFoodService(wolf, 8.0);

                // Then - the healing caps at max health
                verify(wolf).setHealth(20.0);
            }
        }

        @Test
        void fullHealthWolvesShouldBeLeftAlone() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - a wolf already at max health and a successful roll
                mockedProbability.when(() -> ProbabilityUtil.isSkillRNGSuccessful(
                        SubSkillType.TAMING_FAST_FOOD_SERVICE, mmoPlayer)).thenReturn(true);
                when(wolf.getHealth()).thenReturn(20.0);

                // When - the wolf absorbs damage
                tamingManager.fastFoodService(wolf, 4.0);

                // Then - no healing call is made
                verify(wolf, never()).setHealth(anyDouble());
            }
        }
    }

    @Nested
    class DamageAbilities {
        @Test
        void goreShouldReturnTheBonusPortionOfTheModifiedDamage() {
            // Given - a 1.5x gore modifier
            final double originalModifier = Taming.goreModifier;
            Taming.goreModifier = 1.5;
            try {
                // When - gore processes a 10 damage hit
                // Then - the bonus is the extra half
                assertThat(tamingManager.gore(mock(LivingEntity.class), 10.0))
                        .isCloseTo(5.0, within(1e-9));
            } finally {
                Taming.goreModifier = originalModifier;
            }
        }

        @Test
        void sharpenedClawsShouldPayTheConfiguredBonus() {
            // Given - a configured Sharpened Claws bonus
            final double originalBonus = Taming.sharpenedClawsBonusDamage;
            Taming.sharpenedClawsBonusDamage = 4.0;
            try {
                // When / Then - the bonus is the configured value
                assertThat(tamingManager.sharpenedClaws()).isEqualTo(4.0);
            } finally {
                Taming.sharpenedClawsBonusDamage = originalBonus;
            }
        }
    }

    @Nested
    class Pummel {
        private LivingEntity target;
        private Wolf wolf;

        @BeforeEach
        void setUpCombatants() {
            target = mock(LivingEntity.class);
            wolf = mock(Wolf.class);
            when(wolf.getLocation()).thenReturn(new Location(world, 0, 64, 0, 0, 0));
            when(advancedConfig.getPummelChance()).thenReturn(15.0);
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.TAMING_PUMMEL))
                    .thenReturn(true);
        }

        @Test
        void lockedPummelShouldDoNothing() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - Pummel has not been unlocked
                when(RankUtils.hasUnlockedSubskill(player, SubSkillType.TAMING_PUMMEL))
                        .thenReturn(false);

                // When - a wolf hit processes
                tamingManager.pummel(target, wolf);

                // Then - no roll, no knockback
                mockedProbability.verifyNoInteractions();
                verify(target, never()).setVelocity(any());
            }
        }

        @Test
        void failedRollShouldNotKnockBack() {
            try (final MockedStatic<ProbabilityUtil> ignored =
                    mockStatic(ProbabilityUtil.class)) {
                // Given - the Pummel roll fails
                // When - a wolf hit processes
                tamingManager.pummel(target, wolf);

                // Then - no knockback
                verify(target, never()).setVelocity(any());
            }
        }

        @Test
        void successfulPummelShouldKnockTheTargetBack() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class);
                    final MockedStatic<ParticleEffectUtils> mockedParticles =
                            mockStatic(ParticleEffectUtils.class)) {
                // Given - the Pummel roll succeeds at the configured chance
                mockedProbability.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        PrimarySkillType.TAMING, mmoPlayer, 15.0)).thenReturn(true);

                // When - a wolf hit processes
                tamingManager.pummel(target, wolf);

                // Then - the target is shoved along the wolf's facing at 1.5 blocks of force
                mockedParticles.verify(() -> ParticleEffectUtils.playGreaterImpactEffect(target));
                verify(target).setVelocity(new Vector(0, 0, 1.5));
            }
        }

        @Test
        void pummeledPlayersShouldBeToldWhatHitThem() {
            try (final MockedStatic<ProbabilityUtil> mockedProbability =
                    mockStatic(ProbabilityUtil.class);
                    final MockedStatic<ParticleEffectUtils> ignored =
                            mockStatic(ParticleEffectUtils.class)) {
                // Given - a successful pummel against a player who uses notifications
                mockedProbability.when(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                        PrimarySkillType.TAMING, mmoPlayer, 15.0)).thenReturn(true);
                final Player defender = mock(Player.class);
                when(defender.getLocation()).thenReturn(new Location(world, 1, 64, 1));
                when(NotificationManager.doesPlayerUseNotifications(defender)).thenReturn(true);

                // When - the player is pummeled
                tamingManager.pummel(defender, wolf);

                // Then - the defender gets the pummel message
                notificationManager.verify(() -> NotificationManager.sendPlayerInformation(
                        defender, NotificationType.SUBSKILL_MESSAGE,
                        "Taming.SubSkill.Pummel.TargetMessage"));
            }
        }
    }

    @Nested
    class EnvironmentallyAware {
        @Test
        void fatalHazardDamageShouldNotTeleport() {
            // Given - hazard damage that would kill the wolf
            final Wolf wolf = mock(Wolf.class);
            when(wolf.getHealth()).thenReturn(5.0);

            // When - environmentally aware processes 6 damage
            tamingManager.processEnvironmentallyAware(wolf, 6.0);

            // Then - no rescue teleport happens
            verify(scheduler, never()).teleportAsync(any(), any());
        }

        @Test
        void survivableHazardShouldTeleportTheWolfToItsOwner() {
            // Given - hazard damage the wolf can survive
            final Wolf wolf = mock(Wolf.class);
            when(wolf.getHealth()).thenReturn(10.0);
            final Location ownerLocation = new Location(world, 5, 64, 5);
            when(player.getLocation()).thenReturn(ownerLocation);

            // When - environmentally aware processes 4 damage
            tamingManager.processEnvironmentallyAware(wolf, 4.0);

            // Then - the wolf is rescued to its owner and the owner informed
            verify(scheduler).teleportAsync(wolf, ownerLocation);
            notificationManager.verify(() -> NotificationManager.sendPlayerInformation(player,
                    NotificationType.SUBSKILL_MESSAGE, "Taming.Listener.Wolf"));
        }
    }

    @Nested
    class BeastLore {
        private MockedStatic<LocaleLoader> mockedLocaleLoader;

        @BeforeEach
        void setUpLocale() {
            mockedLocaleLoader = mockStatic(LocaleLoader.class);
            mockedLocaleLoader.when(() -> LocaleLoader.getString(anyString(),
                    any(Object[].class))).thenAnswer(invocation -> invocation.getArgument(0));
            mockedLocaleLoader.when(() -> LocaleLoader.getString(anyString()))
                    .thenAnswer(invocation -> invocation.getArgument(0));
        }

        @AfterEach
        void tearDownLocale() {
            mockedLocaleLoader.close();
        }

        private String loreSentTo(LivingEntity beast) {
            tamingManager.beastLore(beast);
            final ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
            verify(player).sendMessage(message.capture());
            return message.getValue();
        }

        @Test
        void wildBeastsShouldShowHealthOnly() {
            // Given - an untamed wolf
            final Wolf beast = mock(Wolf.class);
            when(beast.getHealth()).thenReturn(8.0);
            when(beast.getMaxHealth()).thenReturn(8.0);

            // When - beast lore inspects it
            final String message = loreSentTo(beast);

            // Then - health shows but no owner line
            assertThat(message).contains("Combat.BeastLoreHealth")
                    .doesNotContain("Combat.BeastLoreOwner");
        }

        @Test
        void tamedBeastsShouldNameTheirOwner() {
            // Given - a wolf tamed by another player
            final Wolf beast = mock(Wolf.class);
            when(beast.isTamed()).thenReturn(true);
            final Player owner = mock(Player.class);
            when(owner.getName()).thenReturn("Nossr50");
            when(beast.getOwner()).thenReturn(owner);
            when(beast.getHealth()).thenReturn(8.0);
            when(beast.getMaxHealth()).thenReturn(8.0);

            // When - beast lore inspects it
            final String message = loreSentTo(beast);

            // Then - the owner line is included
            assertThat(message).contains("Combat.BeastLoreOwner");
        }

        @Test
        void horsesShouldShowSpeedAndJumpStrength() {
            // Given - a horse with jump and speed attributes
            final AbstractHorse horse = mock(AbstractHorse.class);
            when(horse.getHealth()).thenReturn(20.0);
            when(horse.getMaxHealth()).thenReturn(30.0);
            final AttributeInstance jumpAttribute = mock(AttributeInstance.class);
            when(jumpAttribute.getValue()).thenReturn(0.7);
            final AttributeInstance speedAttribute = mock(AttributeInstance.class);
            when(speedAttribute.getValue()).thenReturn(0.2);
            when(horse.getAttribute(AttributeMapper.MAPPED_JUMP_STRENGTH))
                    .thenReturn(jumpAttribute);
            when(horse.getAttribute(AttributeMapper.MAPPED_MOVEMENT_SPEED))
                    .thenReturn(speedAttribute);

            // When - beast lore inspects it
            final String message = loreSentTo(horse);

            // Then - the horse stat lines are included
            assertThat(message).contains("Combat.BeastLoreHorseSpeed")
                    .contains("Combat.BeastLoreHorseJumpStrength");
        }

        @Test
        void llamasShouldNotShowHorseStats() {
            // Given - a llama (horse-like, but without meaningful jump stats)
            final Llama llama = mock(Llama.class);
            when(llama.getHealth()).thenReturn(20.0);
            when(llama.getMaxHealth()).thenReturn(30.0);

            // When - beast lore inspects it
            final String message = loreSentTo(llama);

            // Then - no horse stat lines are included
            assertThat(message).doesNotContain("Combat.BeastLoreHorseSpeed")
                    .doesNotContain("Combat.BeastLoreHorseJumpStrength");
        }
    }

    @Nested
    class CallOfTheWild {
        private ItemStack summonItem;
        private Wolf summonedWolf;

        @BeforeEach
        void setUpSummoning() {
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.TAMING_CALL_OF_THE_WILD))
                    .thenReturn(true);
            when(Permissions.callOfTheWild(player, EntityType.WOLF)).thenReturn(true);

            summonItem = mock(ItemStack.class);
            when(summonItem.getType()).thenReturn(Material.BONE);
            when(summonItem.getAmount()).thenReturn(5);
            when(playerInventory.getItemInMainHand()).thenReturn(summonItem);

            when(player.getWorld()).thenReturn(world);
            summonedWolf = mock(Wolf.class);
            when(summonedWolf.getMaxHealth()).thenReturn(20.0);
            when(summonedWolf.isValid()).thenReturn(true);
            when(world.spawnEntity(any(Location.class), eq(EntityType.WOLF)))
                    .thenReturn(summonedWolf);

            mockedMisc.when(() -> Misc.getLocationOffset(any(Location.class), anyDouble()))
                    .thenAnswer(invocation -> invocation.getArgument(0));
        }

        private void resetSummonThrottle() {
            try {
                final Field field = TamingManager.class.getDeclaredField("lastSummonTimeStamp");
                field.setAccessible(true);
                field.set(tamingManager, 0L);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        }

        @Test
        void summonShouldSpawnConfigureTrackAndChargeTheWolf() {
            try (final MockedStatic<LocaleLoader> mockedLocaleLoader =
                    mockStatic(LocaleLoader.class)) {
                mockedLocaleLoader.when(() -> LocaleLoader.getString(anyString(),
                        any(Object[].class))).thenReturn("Summon Name");

                // When - the wolf summon runs
                tamingManager.summonWolf();

                // Then - a wolf spawns, marked as a COTW summon so it pays no XP
                verify(world).spawnEntity(any(Location.class), eq(EntityType.WOLF));
                assertThat(MobMetadataUtils.hasMobFlag(MobMetaFlagType.COTW_SUMMONED_MOB,
                        summonedWolf)).isTrue();

                // And - the wolf belongs to the player and is set up as a healthy adult
                verify(summonedWolf).setOwner(player);
                verify(summonedWolf).setRemoveWhenFarAway(false);
                verify(summonedWolf).setAdult();
                verify(summonedWolf).setMaxHealth(20.0);
                verify(summonedWolf).setHealth(20.0);
                verify(summonedWolf).setCustomName("Summon Name");

                // And - the summon is tracked against the player's cap
                assertThat(transientEntityTracker.getActiveSummonsForPlayerOfType(playerUUID,
                        CallOfTheWildType.WOLF)).isEqualTo(1);

                // And - the player pays the item cost and is informed with the lifespan
                verify(summonItem).setAmount(5 - COTW_ITEM_COST);
                notificationManager.verify(
                        () -> NotificationManager.sendPlayerInformationChatOnly(player,
                                "Taming.Summon.COTW.Success.WithLifespan", "Wolf",
                                String.valueOf(COTW_LIFESPAN_SECONDS)));
                mockedSoundManager.verify(() -> SoundManager.sendSound(eq(player),
                        any(Location.class), eq(SoundType.ABILITY_ACTIVATED_GENERIC)));
            }
        }

        @Test
        void lockedCallOfTheWildShouldNotSummon() {
            // Given - Call of the Wild has not been unlocked
            when(RankUtils.hasUnlockedSubskill(player, SubSkillType.TAMING_CALL_OF_THE_WILD))
                    .thenReturn(false);

            // When - the wolf summon runs
            tamingManager.summonWolf();

            // Then - nothing spawns
            verify(world, never()).spawnEntity(any(Location.class), any(EntityType.class));
        }

        @Test
        void missingPermissionShouldNotSummon() {
            // Given - no COTW wolf permission
            when(Permissions.callOfTheWild(player, EntityType.WOLF)).thenReturn(false);

            // When - the wolf summon runs
            tamingManager.summonWolf();

            // Then - nothing spawns
            verify(world, never()).spawnEntity(any(Location.class), any(EntityType.class));
        }

        @Test
        void holdingANonSummonItemShouldNotSummon() {
            // Given - the player holds an unrelated item
            when(summonItem.getType()).thenReturn(Material.STONE);

            // When - the wolf summon runs
            tamingManager.summonWolf();

            // Then - nothing spawns
            verify(world, never()).spawnEntity(any(Location.class), any(EntityType.class));
        }

        @Test
        void insufficientItemsShouldWarnWithTheMissingAmount() {
            // Given - two bones when three are required
            when(summonItem.getAmount()).thenReturn(2);

            // When - the wolf summon runs
            tamingManager.summonWolf();

            // Then - the player is told how many more items they need
            verify(world, never()).spawnEntity(any(Location.class), any(EntityType.class));
            notificationManager.verify(
                    () -> NotificationManager.sendPlayerInformationChatOnly(player,
                            "Taming.Summon.COTW.NeedMoreItems", "1", "Bone"));
        }

        @Test
        void summonCapShouldBlockFurtherSummonsAndWarn() {
            try (final MockedStatic<LocaleLoader> ignored = mockStatic(LocaleLoader.class)) {
                // Given - the player is already at the wolf summon cap
                for (int i = 0; i < COTW_SUMMON_CAP; i++) {
                    final Wolf existingSummon = mock(Wolf.class);
                    when(existingSummon.isValid()).thenReturn(true);
                    transientEntityTracker.addSummon(playerUUID, new TrackedTamingEntity(
                            existingSummon, CallOfTheWildType.WOLF, player));
                }

                // When - the wolf summon runs
                tamingManager.summonWolf();

                // Then - nothing new spawns, the cap warning is sent, and no cost is paid
                verify(world, never()).spawnEntity(any(Location.class), any(EntityType.class));
                notificationManager.verify(
                        () -> NotificationManager.sendPlayerInformationChatOnly(player,
                                "Taming.Summon.COTW.Limit", String.valueOf(COTW_SUMMON_CAP),
                                "Wolf"));
                verify(summonItem, never()).setAmount(anyInt());
            }
        }

        @Test
        void rapidRepeatSummonsShouldBeThrottled() {
            try (final MockedStatic<LocaleLoader> ignoredLocale =
                    mockStatic(LocaleLoader.class)) {
                // Given - a successful summon just happened
                tamingManager.summonWolf();

                // When - the player immediately triggers another summon
                tamingManager.summonWolf();

                // Then - the second attempt inside the throttle window is swallowed
                verify(world, times(1)).spawnEntity(any(Location.class), eq(EntityType.WOLF));

                // And - after the throttle resets, summoning works again
                resetSummonThrottle();
                tamingManager.summonWolf();
                verify(world, times(2)).spawnEntity(any(Location.class), eq(EntityType.WOLF));
            }
        }
    }

    @Test
    void cotwItemsShouldBeRecognized() {
        // Given - the configured wolf summon item and an unrelated item
        final ItemStack bone = mock(ItemStack.class);
        when(bone.getType()).thenReturn(Material.BONE);
        final ItemStack stone = mock(ItemStack.class);
        when(stone.getType()).thenReturn(Material.STONE);

        // When / Then - only the summon item is recognized
        assertThat(tamingManager.isCOTWItem(bone)).isTrue();
        assertThat(tamingManager.isCOTWItem(stone)).isFalse();
    }
}
