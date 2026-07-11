package com.gmail.nossr50.skills.taming;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import java.util.logging.Logger;
import org.bukkit.EntityEffect;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Covers the static Taming helpers: the wolf damage-prevention gate and the defensive
 * abilities that shave, extinguish, or heal a tamed wolf.
 */
class TamingTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(TamingTest.class.getName());

    private double originalThickFurModifier;
    private double originalShockProofModifier;

    private Wolf wolf;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);

        originalThickFurModifier = Taming.thickFurModifier;
        originalShockProofModifier = Taming.shockProofModifier;
        Taming.thickFurModifier = 2.0;
        Taming.shockProofModifier = 6.0;

        wolf = mock(Wolf.class);
    }

    @AfterEach
    void tearDown() {
        Taming.thickFurModifier = originalThickFurModifier;
        Taming.shockProofModifier = originalShockProofModifier;
        cleanUpStaticMocks();
    }

    @Nested
    class DamagePreventionGate {
        @Test
        void tamedWolvesWithPlayerOwnersShouldBeProtected() {
            // Given - a tamed wolf owned by a player
            when(wolf.isTamed()).thenReturn(true);

            // When / Then - pet damage prevention applies
            assertThat(Taming.canPreventDamage(wolf, player)).isTrue();
        }

        @Test
        void untamedWolvesShouldNotBeProtected() {
            // Given - a wild wolf
            when(wolf.isTamed()).thenReturn(false);

            // When / Then - no protection
            assertThat(Taming.canPreventDamage(wolf, player)).isFalse();
        }

        @Test
        void nonPlayerOwnersShouldNotProtectThePet() {
            // Given - a wolf tamed by something that is not a player
            when(wolf.isTamed()).thenReturn(true);

            // When / Then - no protection
            assertThat(Taming.canPreventDamage(wolf, mock(AnimalTamer.class))).isFalse();
        }

        @Test
        void tamedNonWolvesShouldNotBeProtected() {
            // Given - a tamed pet that is not a wolf
            final Tameable cat = mock(Tameable.class);
            when(cat.isTamed()).thenReturn(true);

            // When / Then - no protection
            assertThat(Taming.canPreventDamage(cat, player)).isFalse();
        }
    }

    @Nested
    class DefensiveAbilities {
        @Test
        void thickFurShouldDivideDamageAndShake() {
            // Given - a wolf with Thick Fur at a 2.0 damage divisor
            // When - 10 damage comes in
            final double reducedDamage = Taming.processThickFur(wolf, 10.0);

            // Then - the damage is halved and the wolf shakes it off
            assertThat(reducedDamage).isCloseTo(5.0, within(1e-9));
            verify(wolf).playEffect(EntityEffect.WOLF_SHAKE);
        }

        @Test
        void thickFurShouldSnuffOutFire() {
            // Given - a burning wolf with Thick Fur
            // When - the fire protection processes
            Taming.processThickFurFire(wolf);

            // Then - the fire goes out with a puff of smoke
            verify(wolf).setFireTicks(0);
            verify(wolf).playEffect(EntityEffect.WOLF_SMOKE);
        }

        @Test
        void shockProofShouldDivideExplosionDamage() {
            // Given - a wolf with Shock Proof at a 6.0 damage divisor
            // When - 12 explosion damage comes in
            final double reducedDamage = Taming.processShockProof(wolf, 12.0);

            // Then - the damage shrinks accordingly
            assertThat(reducedDamage).isCloseTo(2.0, within(1e-9));
            verify(wolf).playEffect(EntityEffect.WOLF_SHAKE);
        }

        @Test
        void holyHoundShouldHealTheDamageTaken() {
            // Given - a hurt wolf healed by magic damage
            when(wolf.getHealth()).thenReturn(10.0);
            when(wolf.getMaxHealth()).thenReturn(20.0);

            // When - 4 magic damage converts to healing
            Taming.processHolyHound(wolf, 4.0);

            // Then - the wolf heals by the damage amount with hearts
            verify(wolf).setHealth(14.0);
            verify(wolf).playEffect(EntityEffect.WOLF_HEARTS);
        }

        @Test
        void holyHoundHealingShouldClampAtMaxHealth() {
            // Given - a nearly full wolf
            when(wolf.getHealth()).thenReturn(18.0);
            when(wolf.getMaxHealth()).thenReturn(20.0);

            // When - 5 magic damage converts to healing
            Taming.processHolyHound(wolf, 5.0);

            // Then - the healing caps at max health
            verify(wolf).setHealth(20.0);
        }
    }

    @Nested
    class SummonFailureMessages {
        @Test
        void knownSummonTypesShouldHaveTheirOwnFailureMessage() {
            assertThat(Taming.getCallOfTheWildFailureMessage(EntityType.OCELOT))
                    .isEqualTo("Taming.Summon.Fail.Ocelot");
            assertThat(Taming.getCallOfTheWildFailureMessage(EntityType.WOLF))
                    .isEqualTo("Taming.Summon.Fail.Wolf");
            assertThat(Taming.getCallOfTheWildFailureMessage(EntityType.HORSE))
                    .isEqualTo("Taming.Summon.Fail.Horse");
        }

        @Test
        void unknownTypesShouldHaveNoMessage() {
            assertThat(Taming.getCallOfTheWildFailureMessage(EntityType.COW)).isEmpty();
        }
    }
}
