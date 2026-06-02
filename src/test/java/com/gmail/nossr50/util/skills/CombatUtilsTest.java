package com.gmail.nossr50.util.skills;

import static java.util.logging.Logger.getLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.meta.HealthbarSnapshot;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.AttributeMapper;
import com.gmail.nossr50.util.MetadataConstants;
import java.util.Collections;
import java.util.List;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import org.bukkit.metadata.FixedMetadataValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

class CombatUtilsTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = getLogger(
            CombatUtilsTest.class.getName());

    private LivingEntity targetEntity;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        targetEntity = Mockito.mock(LivingEntity.class);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Nested
    class FixNames {

        @Test
        void restoresNameAndVisibilityFromSnapshot() {
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("Boss Slime", true, 0L);
            final FixedMetadataValue metaValue = new FixedMetadataValue(mcMMO.p, snapshot);

            when(targetEntity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);
            when(targetEntity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(metaValue));

            CombatUtils.fixNames(targetEntity);

            verify(targetEntity).setCustomName("Boss Slime");
            verify(targetEntity).setCustomNameVisible(true);
            verify(targetEntity).removeMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT,
                    mcMMO.p);
        }

        @Test
        void restoresNullNameWhenSnapshotHadNoCustomName() {
            final HealthbarSnapshot snapshot = new HealthbarSnapshot(null, false, 0L);
            final FixedMetadataValue metaValue = new FixedMetadataValue(mcMMO.p, snapshot);

            when(targetEntity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);
            when(targetEntity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(metaValue));

            CombatUtils.fixNames(targetEntity);

            verify(targetEntity).setCustomName((String) null);
            verify(targetEntity).setCustomNameVisible(false);
            verify(targetEntity).removeMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT,
                    mcMMO.p);
        }

        @Test
        void doesNothingWhenNoSnapshotExists() {
            when(targetEntity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(false);
            when(targetEntity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(Collections.emptyList());

            CombatUtils.fixNames(targetEntity);

            verify(targetEntity, never()).setCustomName(Mockito.any());
            verify(targetEntity, never()).setCustomNameVisible(Mockito.anyBoolean());
            verify(targetEntity, never()).removeMetadata(
                    MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT, mcMMO.p);
        }
    }

    @Nested
    class RestoreMobNameIfLethal {

        @Test
        void restoresWhenLethalAndSnapshotPresent() {
            // Given – damage will kill the entity, snapshot is present
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("Named Mob", true, 0L);
            final FixedMetadataValue metaValue = new FixedMetadataValue(mcMMO.p, snapshot);

            final EntityDamageEvent event = Mockito.mock(EntityDamageEvent.class);
            when(event.getEntity()).thenReturn(targetEntity);
            when(event.getFinalDamage()).thenReturn(10.0);
            when(targetEntity.getHealth()).thenReturn(5.0); // damage > health → lethal

            when(targetEntity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);
            when(targetEntity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(metaValue));

            // When
            CombatUtils.restoreMobNameIfLethal(event);

            // Then
            verify(targetEntity).setCustomName("Named Mob");
            verify(targetEntity).setCustomNameVisible(true);
            verify(targetEntity).removeMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT,
                    mcMMO.p);
        }

        @Test
        void skipsWhenNonLethal() {
            // Given – entity survives
            final EntityDamageEvent event = Mockito.mock(EntityDamageEvent.class);
            when(event.getEntity()).thenReturn(targetEntity);
            when(event.getFinalDamage()).thenReturn(3.0);
            when(targetEntity.getHealth()).thenReturn(10.0); // damage < health → survives

            // When
            CombatUtils.restoreMobNameIfLethal(event);

            // Then – snapshot never consulted
            verify(targetEntity, never()).hasMetadata(
                    MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT);
            verify(targetEntity, never()).setCustomName(Mockito.any());
            verify(targetEntity, never()).setCustomNameVisible(Mockito.anyBoolean());
        }

        @Test
        void skipsWhenNoSnapshot() {
            // Given – lethal hit but entity was never given a healthbar
            final EntityDamageEvent event = Mockito.mock(EntityDamageEvent.class);
            when(event.getEntity()).thenReturn(targetEntity);
            when(event.getFinalDamage()).thenReturn(20.0);
            when(targetEntity.getHealth()).thenReturn(5.0); // lethal

            when(targetEntity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(false);

            // When
            CombatUtils.restoreMobNameIfLethal(event);

            // Then
            verify(targetEntity, never()).setCustomName(Mockito.any());
            verify(targetEntity, never()).setCustomNameVisible(Mockito.anyBoolean());
            verify(targetEntity, never()).removeMetadata(
                    MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT, mcMMO.p);
        }

        @Test
        void skipsNonLivingEntity() {
            // Given – event entity is not a LivingEntity
            final EntityDamageEvent event = Mockito.mock(EntityDamageEvent.class);
            final org.bukkit.entity.Entity nonLivingEntity = Mockito.mock(
                    org.bukkit.entity.Entity.class);
            when(event.getEntity()).thenReturn(nonLivingEntity);

            // When
            CombatUtils.restoreMobNameIfLethal(event);

            // Then – no living-entity interaction at all
            verify(targetEntity, never()).setCustomName(Mockito.any());
            verify(targetEntity, never()).setCustomNameVisible(Mockito.anyBoolean());
        }

        @Test
        void restoresNullCustomNameCorrectly() {
            // Given – entity had no custom name before healthbar was applied
            final HealthbarSnapshot snapshot = new HealthbarSnapshot(null, false, 0L);
            final FixedMetadataValue metaValue = new FixedMetadataValue(mcMMO.p, snapshot);

            final EntityDamageEvent event = Mockito.mock(EntityDamageEvent.class);
            when(event.getEntity()).thenReturn(targetEntity);
            when(event.getFinalDamage()).thenReturn(50.0);
            when(targetEntity.getHealth()).thenReturn(1.0); // lethal

            when(targetEntity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);
            when(targetEntity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(metaValue));

            // When
            CombatUtils.restoreMobNameIfLethal(event);

            // Then – null name is restored, not ""
            verify(targetEntity).setCustomName((String) null);
            verify(targetEntity).setCustomNameVisible(false);
            verify(targetEntity).removeMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT,
                    mcMMO.p);
        }

        @Test
        void exactLethalBoundaryTriggersRestore() {
            // Given – final damage exactly equals health (boundary: exactly lethal)
            final HealthbarSnapshot snapshot = new HealthbarSnapshot("Boundary Mob", false, 0L);
            final FixedMetadataValue metaValue = new FixedMetadataValue(mcMMO.p, snapshot);

            final EntityDamageEvent event = Mockito.mock(EntityDamageEvent.class);
            when(event.getEntity()).thenReturn(targetEntity);
            when(event.getFinalDamage()).thenReturn(10.0);
            when(targetEntity.getHealth()).thenReturn(10.0); // exactly equal → lethal

            when(targetEntity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(true);
            when(targetEntity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT))
                    .thenReturn(List.of(metaValue));

            // When
            CombatUtils.restoreMobNameIfLethal(event);

            // Then – should restore (damage >= health)
            verify(targetEntity).setCustomName("Boundary Mob");
            verify(targetEntity).removeMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT,
                    mcMMO.p);
        }
    }

    /**
     * Tests for {@link CombatUtils#computeAttackStrengthScale}.
     *
     * <p>Background: Vanilla multiplies melee BASE damage by the player's current attack strength
     * (0.0 = no charge, 1.0 = full charge). Paper 26.1.2+ (PR #13856) resets the attack ticker
     * <em>before</em> {@code EntityDamageByEntityEvent} fires, so
     * {@code Player.getAttackCooldown()} always returns ~0 during the event. The fix
     * back-derives the committed scale from {@code BASE / attribute.getValue()} instead.
     *
     * <p>Expected invariant: the result is always in {@code [0.0, 1.0]}.
     */
    @Nested
    class ComputeAttackStrengthScale {

        // Tolerance used for floating-point assertEquals comparisons.
        private static final double EPSILON = 1e-9;

        private EntityDamageByEntityEvent damageEvent;
        private AttributeInstance attackDamageAttribute;

        @BeforeEach
        void setUpAttackStrengthScaleTests() {
            // A fresh damage event mock and attribute mock for every test.
            // By default Mockito returns null for getAttribute(), which exercises the null guard.
            damageEvent = Mockito.mock(EntityDamageByEntityEvent.class);
            attackDamageAttribute = Mockito.mock(AttributeInstance.class);

            // Default: cooldown scaling is enabled (the common production configuration).
            when(advancedConfig.useAttackCooldown()).thenReturn(true);

            // Default: normal melee attack (not a sweep).
            when(damageEvent.getCause())
                    .thenReturn(org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        }

        // ------------------------------------------------------------------ early-return guards

        @Test
        void returnsOneWhenAttackCooldownConfigIsDisabled() {
            // Given – server admin has turned off attack-cooldown scaling entirely
            when(advancedConfig.useAttackCooldown()).thenReturn(false);

            // When
            final double scale = CombatUtils.computeAttackStrengthScale(player, damageEvent);

            // Then – bypass all event/attribute reads and return 1.0 unconditionally
            assertEquals(1.0, scale, EPSILON);

            // Confirm we never touch the event or attribute when the feature is off
            verify(damageEvent, never()).getCause();
            verify(player, never()).getAttribute(AttributeMapper.MAPPED_GENERIC_ATTACK_DAMAGE);
        }

        @Test
        void returnsOneForSweepAttack() {
            // Given – the damage was caused by a sweep (AoE arc around the target).
            // Sweep attacks can only happen at full charge, but their BASE value reflects the
            // sweep formula rather than weaponDamage * chargeFraction, so back-derivation would
            // produce a wrong scale.  The method special-cases this and always returns 1.0.
            when(damageEvent.getCause())
                    .thenReturn(org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);

            // When
            final double scale = CombatUtils.computeAttackStrengthScale(player, damageEvent);

            // Then
            assertEquals(1.0, scale, EPSILON);
        }

        @Test
        void returnsOneWhenAttackDamageAttributeIsNull() {
            // Given – player has no GENERIC_ATTACK_DAMAGE attribute (unusual but defensively handled).
            // Mockito returns null by default, so no extra stubbing needed here.
            // Confirm null is indeed what getAttribute returns:
            when(player.getAttribute(AttributeMapper.MAPPED_GENERIC_ATTACK_DAMAGE))
                    .thenReturn(null);

            // When
            final double scale = CombatUtils.computeAttackStrengthScale(player, damageEvent);

            // Then – fall back to full-strength rather than crash
            assertEquals(1.0, scale, EPSILON);
        }

        @Test
        void returnsOneWhenAttributeValueIsZero() {
            // Given – attribute value is 0 (would cause divide-by-zero; must be guarded).
            when(player.getAttribute(AttributeMapper.MAPPED_GENERIC_ATTACK_DAMAGE))
                    .thenReturn(attackDamageAttribute);
            when(attackDamageAttribute.getValue()).thenReturn(0.0);

            // When
            final double scale = CombatUtils.computeAttackStrengthScale(player, damageEvent);

            // Then – guard prevents NaN/Infinity; fall back to 1.0
            assertEquals(1.0, scale, EPSILON);
        }

        @Test
        void returnsOneWhenAttributeValueIsNegative() {
            // Given – negative attribute value (e.g. from a buggy plugin or data corruption).
            // The method guards on <= 0.0 to cover both zero and negative.
            when(player.getAttribute(AttributeMapper.MAPPED_GENERIC_ATTACK_DAMAGE))
                    .thenReturn(attackDamageAttribute);
            when(attackDamageAttribute.getValue()).thenReturn(-5.0);

            // When
            final double scale = CombatUtils.computeAttackStrengthScale(player, damageEvent);

            // Then
            assertEquals(1.0, scale, EPSILON);
        }

        // ------------------------------------------------------------------ scale computation

        /**
         * Parameterized happy-path cases exercising the core formula:
         * {@code scale = clamp(BASE / maxDamage, 0.0, 1.0)}.
         *
         * <pre>
         * baseDamage  | maxDamage | expectedScale | note
         * ------------|-----------|---------------|-------------------------------
         *   8.0       |  8.0      |  1.0          | full charge — common case on Paper 26.1.2+
         *   4.0       |  8.0      |  0.5          | half charge — mid-swing hit
         *   0.0       |  8.0      |  0.0          | zero charge — tap attack
         *   2.0       |  8.0      |  0.25         | quarter charge
         *   6.0       |  8.0      |  0.75         | three-quarter charge
         *   1.0       |  1.0      |  1.0          | fist / bare-hand full charge
         *  10.0       |  8.0      |  1.0          | BASE > max (crit effect) — clamped to 1.0
         *  -1.0       |  8.0      |  0.0          | negative BASE (edge) — clamped to 0.0
         * </pre>
         */
        @ParameterizedTest(name = "BASE={0}, maxDamage={1} → scale={2}")
        @CsvSource({
                "8.0,  8.0,  1.0",   // full charge — the Paper 26.1.2+ regression case
                "4.0,  8.0,  0.5",   // half charge
                "0.0,  8.0,  0.0",   // zero charge (tap attack)
                "2.0,  8.0,  0.25",  // quarter charge
                "6.0,  8.0,  0.75",  // three-quarter charge
                "1.0,  1.0,  1.0",   // bare-hand full charge
                "10.0, 8.0,  1.0",   // BASE exceeds max (crit scenario) — upper clamp
                "-1.0, 8.0,  0.0",   // negative BASE — lower clamp
        })
        void computesScaleFromBaseAndAttributeValue(
                final double baseDamage,
                final double maxDamage,
                final double expectedScale) {
            // Given – normal melee hit; BASE and attribute values are controlled by the test row
            when(player.getAttribute(AttributeMapper.MAPPED_GENERIC_ATTACK_DAMAGE))
                    .thenReturn(attackDamageAttribute);
            when(attackDamageAttribute.getValue()).thenReturn(maxDamage);
            when(damageEvent.getDamage()).thenReturn(baseDamage);

            // When
            final double actualScale = CombatUtils.computeAttackStrengthScale(player, damageEvent);

            // Then
            assertEquals(expectedScale, actualScale, EPSILON);
        }

        @Test
        void fullChargeHitProducesExactlyOneDespiteFloatingPoint() {
            // Given – BASE and the attribute value are identical (full-charge sword hit).
            // This is the exact scenario that was broken on Paper 26.1.2+: the attack ticker was
            // reset before the event fired, so getAttackCooldown() returned ~0 and all bonus
            // damage collapsed to zero.  With the back-derivation approach, BASE / maxDamage = 1.0.
            final double swordDamage = 7.0; // iron sword base attack damage
            when(player.getAttribute(AttributeMapper.MAPPED_GENERIC_ATTACK_DAMAGE))
                    .thenReturn(attackDamageAttribute);
            when(attackDamageAttribute.getValue()).thenReturn(swordDamage);
            when(damageEvent.getDamage()).thenReturn(swordDamage);

            // When
            final double scale = CombatUtils.computeAttackStrengthScale(player, damageEvent);

            // Then – must be exactly 1.0, not 0.999… or NaN
            assertEquals(1.0, scale, EPSILON);
        }

        @Test
        void scaleIsNeverGreaterThanOne() {
            // Given – BASE is slightly above maxDamage (e.g. because of a critical hit
            // adding a DAMAGE_RESISTANCE modifier that interacts with BASE before we read it).
            final double swordDamage = 6.0;
            final double baseDamageWithCrit = 9.0; // ratio = 1.5 → must clamp to 1.0
            when(player.getAttribute(AttributeMapper.MAPPED_GENERIC_ATTACK_DAMAGE))
                    .thenReturn(attackDamageAttribute);
            when(attackDamageAttribute.getValue()).thenReturn(swordDamage);
            when(damageEvent.getDamage()).thenReturn(baseDamageWithCrit);

            // When
            final double scale = CombatUtils.computeAttackStrengthScale(player, damageEvent);

            // Then
            assertEquals(1.0, scale, EPSILON);
        }

        @Test
        void scaleIsNeverLessThanZero() {
            // Given – some unusual event produces a negative BASE value
            final double swordDamage = 6.0;
            final double negativeBas = -3.0; // ratio = -0.5 → must clamp to 0.0
            when(player.getAttribute(AttributeMapper.MAPPED_GENERIC_ATTACK_DAMAGE))
                    .thenReturn(attackDamageAttribute);
            when(attackDamageAttribute.getValue()).thenReturn(swordDamage);
            when(damageEvent.getDamage()).thenReturn(negativeBas);

            // When
            final double scale = CombatUtils.computeAttackStrengthScale(player, damageEvent);

            // Then
            assertEquals(0.0, scale, EPSILON);
        }
    }
}

