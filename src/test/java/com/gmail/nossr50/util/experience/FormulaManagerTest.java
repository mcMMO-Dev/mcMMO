package com.gmail.nossr50.util.experience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.mcMMO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Covers the XP-to-next-level math in {@link FormulaManager}. The level-up loop in
 * McMMOPlayer removes the requirement from the player's banked XP once per gained level; a
 * requirement of zero or less never drains that bank (a negative one grows it), so the loop
 * gains levels nonstop until the level cap, which defaults to Integer.MAX_VALUE. These
 * tests pin the requirement to always be at least 1 and to saturate instead of wrapping
 * when the Standard-mode ten-level sum overflows int.
 */
class FormulaManagerTest {
    private MockedStatic<mcMMO> mockedMcMMO;
    private MockedStatic<ExperienceConfig> mockedExperienceConfig;
    private ExperienceConfig experienceConfig;
    private FormulaManager formulaManager;

    @BeforeEach
    void setUp() {
        mockedMcMMO = mockStatic(mcMMO.class);
        mockedExperienceConfig = mockStatic(ExperienceConfig.class);
        experienceConfig = mock(ExperienceConfig.class);
        when(ExperienceConfig.getInstance()).thenReturn(experienceConfig);
        formulaManager = new FormulaManager(FormulaType.UNKNOWN);
    }

    @AfterEach
    void tearDown() {
        mockedExperienceConfig.close();
        mockedMcMMO.close();
    }

    private void stubExponentialDefaults() {
        when(experienceConfig.getBase(FormulaType.EXPONENTIAL)).thenReturn(2000);
        when(experienceConfig.getMultiplier(FormulaType.EXPONENTIAL)).thenReturn(0.1);
        when(experienceConfig.getExponent(FormulaType.EXPONENTIAL)).thenReturn(1.80);
    }

    private void stubLinearValues(int base, double multiplier) {
        when(experienceConfig.getBase(FormulaType.LINEAR)).thenReturn(base);
        when(experienceConfig.getMultiplier(FormulaType.LINEAR)).thenReturn(multiplier);
    }

    @Nested
    class StandardModeRequirements {
        @BeforeEach
        void standardMode() {
            mockedMcMMO.when(mcMMO::isRetroModeEnabled).thenReturn(false);
        }

        @Test
        void requirementShouldSumTenRetroLevelsWithDefaultExponentialValues() {
            // Given - Standard scaling with the default exponential curve values
            stubExponentialDefaults();

            // When - the XP needed for the first two Standard levels is computed
            final int levelZero = formulaManager.getXPtoNextLevel(0, FormulaType.EXPONENTIAL);
            final int levelOne = formulaManager.getXPtoNextLevel(1, FormulaType.EXPONENTIAL);

            // Then - each Standard level costs the sum of floor(2000 + 0.1 * k^1.8) across
            // the ten Retro levels it represents
            assertThat(levelZero).isEqualTo(20022);
            assertThat(levelOne).isEqualTo(20138);
        }

        @Test
        void requirementShouldClampToIntegerMaxWhenTenLevelSumOverflows() {
            // Given - Standard scaling where the ten-level sum exceeds Integer.MAX_VALUE,
            // which only happens at levels reachable through commands like /mmoedit
            stubExponentialDefaults();

            // When - the requirement is computed for such an extreme level
            final int xpNeeded = formulaManager.getXPtoNextLevel(20_000,
                    FormulaType.EXPONENTIAL);

            // Then - the sum saturates instead of wrapping negative and jamming level-ups
            assertThat(xpNeeded).isEqualTo(Integer.MAX_VALUE);
        }

        @Test
        void requirementShouldNeverDropBelowOneWhenLinearBaseIsNegative() {
            // Given - a broken linear curve whose base pushes low-level requirements negative
            stubLinearValues(-5000, 20);

            // When - the requirement for the first Standard level is computed
            final int xpNeeded = formulaManager.getXPtoNextLevel(0, FormulaType.LINEAR);

            // Then - each of the ten summed Retro levels clamps to 1, so the Standard
            // requirement bottoms out at 10 and gaining XP can still level the player
            assertThat(xpNeeded).isEqualTo(10);
        }
    }

    @Nested
    class RetroModeRequirements {
        @BeforeEach
        void retroMode() {
            mockedMcMMO.when(mcMMO::isRetroModeEnabled).thenReturn(true);
        }

        @Test
        void requirementShouldMatchPerLevelFormulaWithDefaultExponentialValues() {
            // Given - Retro scaling with the default exponential curve values
            stubExponentialDefaults();

            // When/Then - requirements follow floor(2000 + 0.1 * level^1.8) per Retro level
            assertThat(formulaManager.getXPtoNextLevel(0, FormulaType.EXPONENTIAL))
                    .isEqualTo(2000);
            assertThat(formulaManager.getXPtoNextLevel(90, FormulaType.EXPONENTIAL))
                    .isEqualTo(2329);
            assertThat(formulaManager.getXPtoNextLevel(331, FormulaType.EXPONENTIAL))
                    .isEqualTo(5433);
        }

        @Test
        void requirementShouldNeverDropBelowOneWhenLinearBaseIsNegative() {
            // Given - a broken linear curve whose base pushes low-level requirements negative
            stubLinearValues(-5000, 20);

            // When - the requirement for the first Retro level is computed
            final int xpNeeded = formulaManager.getXPtoNextLevel(0, FormulaType.LINEAR);

            // Then - the requirement clamps to 1 so gaining XP can still level the player
            assertThat(xpNeeded).isEqualTo(1);
        }

        @Test
        void requirementShouldBeOneWhenFormulaEvaluatesToZero() {
            // Given - a curve that evaluates to exactly zero XP at level zero
            stubLinearValues(0, 20);

            // When - the requirement for the first Retro level is computed
            final int xpNeeded = formulaManager.getXPtoNextLevel(0, FormulaType.LINEAR);

            // Then - a zero-cost level is bumped to 1 so the level-up loop always consumes XP
            assertThat(xpNeeded).isEqualTo(1);
        }
    }
}
