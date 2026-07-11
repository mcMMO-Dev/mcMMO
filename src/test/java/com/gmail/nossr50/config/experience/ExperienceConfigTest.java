package com.gmail.nossr50.config.experience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link ExperienceConfig} combat XP ceiling settings. These guard against the
 * getter and the shipped experience.yml disagreeing on the config key, which silently ignores
 * whatever value the server admin sets (#5309).
 */
class ExperienceConfigTest {

    /**
     * Creates an {@link ExperienceConfig} whose backing config is the given YAML, without running
     * the file-loading constructor. The getters under test only touch the backing config.
     */
    private static ExperienceConfig experienceConfigBackedBy(final YamlConfiguration yaml) {
        final ExperienceConfig experienceConfig = mock(ExperienceConfig.class, CALLS_REAL_METHODS);
        try {
            final Field configField = BukkitConfig.class.getDeclaredField("config");
            configField.setAccessible(true);
            configField.set(experienceConfig, yaml);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to inject backing config", e);
        }
        return experienceConfig;
    }

    /** Loads the experience.yml bundled with the plugin jar. */
    private static YamlConfiguration shippedExperienceYaml() {
        final InputStream resource =
                ExperienceConfig.class.getClassLoader().getResourceAsStream("experience.yml");
        assertThat(resource).as("bundled experience.yml resource").isNotNull();
        return YamlConfiguration.loadConfiguration(
                new InputStreamReader(resource, StandardCharsets.UTF_8));
    }

    @Nested
    class SkillXpRateOverrides {

        private ExperienceConfig configWithGlobalRate(final double globalRate) {
            final YamlConfiguration yaml = new YamlConfiguration();
            yaml.set("Experience_Formula.Multiplier.Global", globalRate);
            return experienceConfigBackedBy(yaml);
        }

        @Test
        void getExperienceGainsMultiplierShouldReturnGlobalWhenNoOverrideIsSet() {
            // Given - no /xprate per-skill rates are active
            final ExperienceConfig experienceConfig = configWithGlobalRate(2.0);

            // Then - every skill uses the global multiplier
            assertThat(experienceConfig.getExperienceGainsMultiplier(PrimarySkillType.MINING))
                    .isEqualTo(2.0);
        }

        /**
         * Gotcha coverage: /xprate rates never stack with each other — a 2x global event and a
         * 2x mining event must yield 2x mining XP, not 4x, and a lower per-skill rate must not
         * undercut a higher global one.
         */
        @ParameterizedTest(name = "global {0}x + mining {1}x should yield {2}x")
        @CsvSource({
                "2.0, 2.0, 2.0",
                "2.0, 5.3, 5.3",
                "3.0, 1.5, 3.0",
                "1.0, 0.5, 1.0",
        })
        void getExperienceGainsMultiplierShouldUseHigherOfGlobalAndSkillRate(
                final double globalRate, final double skillRate, final double expected) {
            // Given - a global /xprate event and a per-skill /xprate rate at the same time
            final ExperienceConfig experienceConfig = configWithGlobalRate(globalRate);
            experienceConfig.setExperienceGainsSkillMultiplier(PrimarySkillType.MINING,
                    skillRate);

            // Then - the higher of the two rates wins, they never stack
            assertThat(experienceConfig.getExperienceGainsMultiplier(PrimarySkillType.MINING))
                    .isEqualTo(expected);
        }

        @Test
        void setExperienceGainsSkillMultiplierShouldOnlyAffectItsOwnSkill() {
            // Given - a mining-only /xprate rate above the global multiplier
            final ExperienceConfig experienceConfig = configWithGlobalRate(1.0);
            experienceConfig.setExperienceGainsSkillMultiplier(PrimarySkillType.MINING, 5.3);

            // Then - other skills keep using the global multiplier
            assertThat(experienceConfig.getExperienceGainsMultiplier(PrimarySkillType.MINING))
                    .isEqualTo(5.3);
            assertThat(experienceConfig.getExperienceGainsMultiplier(
                    PrimarySkillType.HERBALISM)).isEqualTo(1.0);
        }

        /**
         * Re-running /xprate for a skill must replace its rate, so an active 3x mining rate
         * can be downgraded to 2x without a reset in between.
         */
        @Test
        void setExperienceGainsSkillMultiplierShouldReplacePreviousRateForTheSkill() {
            // Given - a mining rate of 3 over a 1x baseline
            final ExperienceConfig experienceConfig = configWithGlobalRate(1.0);
            experienceConfig.setExperienceGainsSkillMultiplier(PrimarySkillType.MINING, 3.0);

            // When - the rate is set again with a lower value
            experienceConfig.setExperienceGainsSkillMultiplier(PrimarySkillType.MINING, 2.0);

            // Then - the new rate replaces the old one instead of keeping the higher value
            assertThat(experienceConfig.getExperienceGainsMultiplier(PrimarySkillType.MINING))
                    .isEqualTo(2.0);
        }

        @Test
        void clearExperienceGainsSkillMultipliersShouldRestoreGlobalForAllSkills() {
            // Given - several per-skill rates are active
            final ExperienceConfig experienceConfig = configWithGlobalRate(1.0);
            experienceConfig.setExperienceGainsSkillMultiplier(PrimarySkillType.MINING, 5.3);
            experienceConfig.setExperienceGainsSkillMultiplier(PrimarySkillType.HERBALISM, 2.0);

            // When - the rates are cleared (the /xprate reset path)
            experienceConfig.clearExperienceGainsSkillMultipliers();

            // Then - every skill is back on the global multiplier and none is reported active
            assertThat(experienceConfig.getExperienceGainsMultiplier(PrimarySkillType.MINING))
                    .isEqualTo(1.0);
            assertThat(experienceConfig.getExperienceGainsSkillMultiplierOverrides()).isEmpty();
        }

        @Test
        void getExperienceGainsSkillMultiplierOverridesShouldListExactlyTheActiveRates() {
            // Given - two per-skill rates are active
            final ExperienceConfig experienceConfig = configWithGlobalRate(1.0);
            experienceConfig.setExperienceGainsSkillMultiplier(PrimarySkillType.MINING, 5.3);
            experienceConfig.setExperienceGainsSkillMultiplier(PrimarySkillType.HERBALISM, 2.0);

            // Then - the display snapshot lists exactly those skills and rates
            assertThat(experienceConfig.getExperienceGainsSkillMultiplierOverrides())
                    .containsOnly(
                            entry(PrimarySkillType.MINING, 5.3),
                            entry(PrimarySkillType.HERBALISM, 2.0));
        }
    }

    @Nested
    class CombatXpCeiling {

        @Test
        void getCombatHPCeilingShouldReturnShippedDefaultWhenConfigIsUnchanged() {
            // Given - the experience.yml shipped with the plugin, with no admin edits
            final YamlConfiguration shippedYaml = shippedExperienceYaml();
            final ExperienceConfig experienceConfig = experienceConfigBackedBy(shippedYaml);

            // Then - the shipped file defines the ceiling key the plugin documents
            assertThat(shippedYaml.isSet("ExploitFix.Combat.XPCeiling.Damage_Limit"))
                    .as("shipped experience.yml should contain the documented ceiling key")
                    .isTrue();

            // And - the getter reports the shipped value
            assertThat(experienceConfig.getCombatHPCeiling()).isEqualTo(100);
        }

        @ParameterizedTest(name = "customized Damage_Limit {0} should be honored")
        @ValueSource(ints = {1, 50, 250, 5000})
        void getCombatHPCeilingShouldHonorCustomizedDamageLimit(final int customLimit) {
            /*
             * Intent: this is the #5309 regression guard. The getter used to read
             * 'HP_Modifier_Limit', a key that never shipped, so any admin edit to 'Damage_Limit'
             * was silently ignored and the ceiling stayed at the hardcoded 100.
             */

            // Given - a server admin customized the ceiling in their experience.yml
            final YamlConfiguration customizedYaml = shippedExperienceYaml();
            customizedYaml.set("ExploitFix.Combat.XPCeiling.Damage_Limit", customLimit);
            final ExperienceConfig experienceConfig = experienceConfigBackedBy(customizedYaml);

            // When - the ceiling is read
            final int ceiling = experienceConfig.getCombatHPCeiling();

            // Then - the customized value takes effect instead of the hardcoded default
            assertThat(ceiling).isEqualTo(customLimit);
        }

        @Test
        void getCombatHPCeilingShouldFallBackTo100WhenKeyIsMissing() {
            // Given - a config with no XPCeiling section at all (e.g. a stripped-down user file)
            final ExperienceConfig experienceConfig =
                    experienceConfigBackedBy(new YamlConfiguration());

            // Then - the getter falls back to the historical default
            assertThat(experienceConfig.getCombatHPCeiling()).isEqualTo(100);
        }
    }
}
