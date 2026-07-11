package com.gmail.nossr50.datatypes.skills.alchemy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.util.PotionUtil;
import java.util.List;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Covers the potion stage math that decides how much Alchemy XP a brew pays out. The stage of
 * a potion is the sum of its upgrades (effects, strength, duration, splash/lingering), and the
 * input/output pair rules decide which stage is paid for a brew step.
 */
class PotionStageTest {
    private MockedStatic<PotionUtil> mockedPotionUtil;

    @BeforeEach
    void setUp() {
        // All PotionUtil predicates default to false; tests opt in per scenario
        mockedPotionUtil = mockStatic(PotionUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedPotionUtil.close();
    }

    private PotionMeta mockMeta() {
        final PotionMeta potionMeta = mock(PotionMeta.class);
        when(potionMeta.getCustomEffects()).thenReturn(List.of());
        return potionMeta;
    }

    private AlchemyPotion mockPotion(PotionMeta potionMeta) {
        final AlchemyPotion potion = mock(AlchemyPotion.class);
        when(potion.getAlchemyPotionMeta()).thenReturn(potionMeta);
        return potion;
    }

    private PotionEffect mockEffect(int amplifier) {
        final PotionEffect effect = mock(PotionEffect.class);
        when(effect.getAmplifier()).thenReturn(amplifier);
        return effect;
    }

    @Nested
    class SinglePotionStage {
        @Test
        void plainPotionShouldBeStageOne() {
            // Given - a potion with no effects or upgrades of any kind
            final AlchemyPotion potion = mockPotion(mockMeta());

            // When - the stage is computed
            // Then - it is the base stage
            assertThat(PotionStage.getPotionStage(potion)).isEqualTo(PotionStage.ONE);
        }

        @Test
        void basePotionEffectsShouldBeStageTwo() {
            // Given - a potion whose base potion type carries effects
            final PotionMeta potionMeta = mockMeta();
            mockedPotionUtil.when(() -> PotionUtil.hasBasePotionEffects(potionMeta))
                    .thenReturn(true);
            final AlchemyPotion potion = mockPotion(potionMeta);

            // When - the stage is computed
            // Then - the effect counts for one stage
            assertThat(PotionStage.getPotionStage(potion)).isEqualTo(PotionStage.TWO);
        }

        @Test
        void unamplifiedCustomEffectShouldBeStageTwo() {
            // Given - a potion with a custom effect at amplifier zero
            final PotionMeta potionMeta = mockMeta();
            final PotionEffect unamplifiedEffect = mockEffect(0);
            when(potionMeta.getCustomEffects()).thenReturn(List.of(unamplifiedEffect));
            final AlchemyPotion potion = mockPotion(potionMeta);

            // When - the stage is computed
            // Then - the effect counts, but the zero amplifier does not
            assertThat(PotionStage.getPotionStage(potion)).isEqualTo(PotionStage.TWO);
        }

        @Test
        void strongPotionShouldBeStageThree() {
            // Given - a glowstone-amplified potion with base effects
            final PotionMeta potionMeta = mockMeta();
            mockedPotionUtil.when(() -> PotionUtil.hasBasePotionEffects(potionMeta))
                    .thenReturn(true);
            mockedPotionUtil.when(() -> PotionUtil.isStrong(potionMeta)).thenReturn(true);
            final AlchemyPotion potion = mockPotion(potionMeta);

            // When - the stage is computed
            // Then - the amplifier adds a stage on top of the effect stage
            assertThat(PotionStage.getPotionStage(potion)).isEqualTo(PotionStage.THREE);
        }

        @Test
        void amplifiedCustomEffectShouldBeStageThree() {
            // Given - a potion whose custom effect was amplified by mcMMO
            final PotionMeta potionMeta = mockMeta();
            final PotionEffect amplifiedEffect = mockEffect(1);
            when(potionMeta.getCustomEffects()).thenReturn(List.of(amplifiedEffect));
            final AlchemyPotion potion = mockPotion(potionMeta);

            // When - the stage is computed
            // Then - the custom amplifier adds a stage just like glowstone would
            assertThat(PotionStage.getPotionStage(potion)).isEqualTo(PotionStage.THREE);
        }

        @Test
        void strongPotionWithAmplifiedCustomEffectShouldNotDoubleCount() {
            // Given - a potion that is both glowstone-amplified and custom-amplified
            final PotionMeta potionMeta = mockMeta();
            final PotionEffect amplifiedEffect = mockEffect(2);
            when(potionMeta.getCustomEffects()).thenReturn(List.of(amplifiedEffect));
            mockedPotionUtil.when(() -> PotionUtil.isStrong(potionMeta)).thenReturn(true);
            final AlchemyPotion potion = mockPotion(potionMeta);

            // When - the stage is computed
            // Then - the amplifier bump is only applied once
            assertThat(PotionStage.getPotionStage(potion)).isEqualTo(PotionStage.THREE);
        }

        @Test
        void longPotionShouldGainAStage() {
            // Given - a redstone-extended potion with base effects
            final PotionMeta potionMeta = mockMeta();
            mockedPotionUtil.when(() -> PotionUtil.hasBasePotionEffects(potionMeta))
                    .thenReturn(true);
            mockedPotionUtil.when(() -> PotionUtil.isLong(potionMeta)).thenReturn(true);
            final AlchemyPotion potion = mockPotion(potionMeta);

            // When - the stage is computed
            // Then - the extension adds a stage
            assertThat(PotionStage.getPotionStage(potion)).isEqualTo(PotionStage.THREE);
        }

        @Test
        void splashPotionShouldGainAStage() {
            // Given - a splash potion with base effects
            final PotionMeta potionMeta = mockMeta();
            mockedPotionUtil.when(() -> PotionUtil.hasBasePotionEffects(potionMeta))
                    .thenReturn(true);
            final AlchemyPotion potion = mockPotion(potionMeta);
            when(potion.isSplash()).thenReturn(true);

            // When - the stage is computed
            // Then - the gunpowder conversion adds a stage
            assertThat(PotionStage.getPotionStage(potion)).isEqualTo(PotionStage.THREE);
        }

        @Test
        void fullyUpgradedLingeringPotionShouldBeStageFive() {
            // Given - a strong, long, lingering potion with base effects
            final PotionMeta potionMeta = mockMeta();
            mockedPotionUtil.when(() -> PotionUtil.hasBasePotionEffects(potionMeta))
                    .thenReturn(true);
            mockedPotionUtil.when(() -> PotionUtil.isStrong(potionMeta)).thenReturn(true);
            mockedPotionUtil.when(() -> PotionUtil.isLong(potionMeta)).thenReturn(true);
            final AlchemyPotion potion = mockPotion(potionMeta);
            when(potion.isLingering()).thenReturn(true);

            // When - the stage is computed
            // Then - every upgrade counts and the result caps at the top stage
            assertThat(PotionStage.getPotionStage(potion)).isEqualTo(PotionStage.FIVE);
        }
    }

    @Nested
    class BrewPairStage {
        @Test
        void sameStageBrewFromNonWaterInputShouldPayTopStage() {
            // Given - an input and output potion that compute to the same stage, where the
            // input is not a water bottle (e.g. gunpowder side-grades)
            final PotionMeta inputMeta = mockMeta();
            final PotionMeta outputMeta = mockMeta();
            final AlchemyPotion input = mockPotion(inputMeta);
            final AlchemyPotion output = mockPotion(outputMeta);

            // When - the brew pair stage is computed
            // Then - the sidegrade is treated as the top stage
            assertThat(PotionStage.getPotionStage(input, output)).isEqualTo(PotionStage.FIVE);
        }

        @Test
        void sameStageBrewFromWaterBottleShouldKeepOutputStage() {
            // Given - a water bottle input whose output computes to the same stage
            final PotionMeta inputMeta = mockMeta();
            final PotionMeta outputMeta = mockMeta();
            mockedPotionUtil.when(() -> PotionUtil.isPotionTypeWater(inputMeta)).thenReturn(true);
            final AlchemyPotion input = mockPotion(inputMeta);
            final AlchemyPotion output = mockPotion(outputMeta);

            // When - the brew pair stage is computed
            // Then - the water bottle exemption keeps the output's own stage
            assertThat(PotionStage.getPotionStage(input, output)).isEqualTo(PotionStage.ONE);
        }

        @Test
        void upgradeBrewShouldPayTheOutputStage() {
            // Given - an input at a lower stage than its output (a real upgrade)
            final PotionMeta inputMeta = mockMeta();
            final PotionMeta outputMeta = mockMeta();
            mockedPotionUtil.when(() -> PotionUtil.hasBasePotionEffects(outputMeta))
                    .thenReturn(true);
            mockedPotionUtil.when(() -> PotionUtil.isStrong(outputMeta)).thenReturn(true);
            final AlchemyPotion input = mockPotion(inputMeta);
            final AlchemyPotion output = mockPotion(outputMeta);

            // When - the brew pair stage is computed
            // Then - the output's stage is paid
            assertThat(PotionStage.getPotionStage(input, output)).isEqualTo(PotionStage.THREE);
        }
    }
}
