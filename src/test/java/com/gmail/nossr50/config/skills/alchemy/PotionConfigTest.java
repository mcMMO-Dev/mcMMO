package com.gmail.nossr50.config.skills.alchemy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PotionConfigTest {

    private static final Logger LOGGER = Logger.getLogger(PotionConfigTest.class.getName());

    // ---------------------------------------------------------------------------
    // YAML helpers
    // ---------------------------------------------------------------------------

    /**
     * Builds a minimal potions YAML with the four Tricky Trials splash potions at the given
     * duration, and the four lingering potions at a separate given duration.
     */
    private static String allEightTrickyTrialsPotionsYaml(
            final int splashDuration, final int lingeringDuration) {
        return "Potions:\n"
                + "  SPLASH_POTION_OF_INFESTATION:\n"
                + "    Effects:\n"
                + "      - \"INFESTED 0 " + splashDuration + "\"\n"
                + "  SPLASH_POTION_OF_WEAVING:\n"
                + "    Effects:\n"
                + "      - \"WEAVING 0 " + splashDuration + "\"\n"
                + "  SPLASH_POTION_OF_WIND_CHARGING:\n"
                + "    Effects:\n"
                + "      - \"WIND_CHARGED 0 " + splashDuration + "\"\n"
                + "  SPLASH_POTION_OF_OOZING:\n"
                + "    Effects:\n"
                + "      - \"OOZING 0 " + splashDuration + "\"\n"
                + "  LINGERING_POTION_OF_INFESTATION:\n"
                + "    Effects:\n"
                + "      - \"INFESTED 0 " + lingeringDuration + "\"\n"
                + "  LINGERING_POTION_OF_WEAVING:\n"
                + "    Effects:\n"
                + "      - \"WEAVING 0 " + lingeringDuration + "\"\n"
                + "  LINGERING_POTION_WIND_CHARGING:\n"
                + "    Effects:\n"
                + "      - \"WIND_CHARGED 0 " + lingeringDuration + "\"\n"
                + "  LINGERING_POTION_OF_OOZING:\n"
                + "    Effects:\n"
                + "      - \"OOZING 0 " + lingeringDuration + "\"\n";
    }

    /** Parses a YAML string into a {@link YamlConfiguration} without touching the file system. */
    private static YamlConfiguration loadYaml(final String yaml) {
        return YamlConfiguration.loadConfiguration(new StringReader(yaml));
    }

    /**
     * Extracts the integer duration from the first Effects entry of the named potion,
     * splitting the {@code "EFFECT AMPLIFIER DURATION"} string by whitespace.
     */
    private static int parseDurationFromFirstEffect(
            final YamlConfiguration config, final String potionKey) {
        final ConfigurationSection potionSection =
                config.getConfigurationSection("Potions." + potionKey);
        assertThat(potionSection)
                .as("Config section for potion key '%s' should exist", potionKey)
                .isNotNull();
        final List<String> effects = potionSection.getStringList("Effects");
        assertThat(effects)
                .as("Effects list for '%s' should not be empty", potionKey)
                .isNotEmpty();
        final String[] parts = effects.get(0).trim().split("\\s+");
        return Integer.parseInt(parts[2]);
    }

    // ---------------------------------------------------------------------------
    // Migration tests
    // ---------------------------------------------------------------------------

    @Nested
    class TrickyTrialsPotionDurationMigration {

        @Test
        void patchesAllEightPotionsWhenDurationsAreIncorrect() {
            /*
             * Intent: verifies end-to-end that fixTrickyTrialsPotionDurations correctly patches
             * all four splash potions (2500 -> 3600) and all four lingering potions (3000 -> 900).
             * The original mcMMO defaults shipped wrong values before the Warriorrrr fix; this
             * migration corrects existing user installs that still have the old values.
             */

            // Given: config with all eight potions at their respective incorrect durations
            final YamlConfiguration configWithBadDurations = loadYaml(
                    allEightTrickyTrialsPotionsYaml(
                            PotionConfig.TRICKY_TRIALS_SPLASH_INCORRECT_DURATION,
                            PotionConfig.TRICKY_TRIALS_LINGERING_INCORRECT_DURATION));

            // When: migration runs
            final boolean patched = PotionConfig.fixTrickyTrialsPotionDurations(
                    configWithBadDurations, LOGGER);

            // Then: method reports that patches were applied
            assertThat(patched).isTrue();

            // Then: all four splash potions have the correct 3600-tick duration
            for (final String splashKey : PotionConfig.TRICKY_TRIALS_SPLASH_POTION_KEYS) {
                assertThat(parseDurationFromFirstEffect(configWithBadDurations, splashKey))
                        .as("Splash potion '%s' should be patched to %d ticks",
                                splashKey, PotionConfig.TRICKY_TRIALS_SPLASH_CORRECT_DURATION)
                        .isEqualTo(PotionConfig.TRICKY_TRIALS_SPLASH_CORRECT_DURATION);
            }

            // Then: all four lingering potions have the correct 900-tick duration
            for (final String lingeringKey : PotionConfig.TRICKY_TRIALS_LINGERING_POTION_KEYS) {
                assertThat(parseDurationFromFirstEffect(configWithBadDurations, lingeringKey))
                        .as("Lingering potion '%s' should be patched to %d ticks",
                                lingeringKey, PotionConfig.TRICKY_TRIALS_LINGERING_CORRECT_DURATION)
                        .isEqualTo(PotionConfig.TRICKY_TRIALS_LINGERING_CORRECT_DURATION);
            }
        }

        @Test
        void returnsFalseAndLeavesValuesUnchangedWhenAllDurationsAreAlreadyCorrect() {
            // Given: config where all eight potions already have the correct durations
            final YamlConfiguration configWithCorrectDurations = loadYaml(
                    allEightTrickyTrialsPotionsYaml(
                            PotionConfig.TRICKY_TRIALS_SPLASH_CORRECT_DURATION,
                            PotionConfig.TRICKY_TRIALS_LINGERING_CORRECT_DURATION));

            // When: migration runs
            final boolean patched = PotionConfig.fixTrickyTrialsPotionDurations(
                    configWithCorrectDurations, LOGGER);

            // Then: no patches were applied
            assertThat(patched).isFalse();

            // Then: values are unchanged
            for (final String splashKey : PotionConfig.TRICKY_TRIALS_SPLASH_POTION_KEYS) {
                assertThat(parseDurationFromFirstEffect(configWithCorrectDurations, splashKey))
                        .isEqualTo(PotionConfig.TRICKY_TRIALS_SPLASH_CORRECT_DURATION);
            }
            for (final String lingeringKey : PotionConfig.TRICKY_TRIALS_LINGERING_POTION_KEYS) {
                assertThat(parseDurationFromFirstEffect(configWithCorrectDurations, lingeringKey))
                        .isEqualTo(PotionConfig.TRICKY_TRIALS_LINGERING_CORRECT_DURATION);
            }
        }

        @ParameterizedTest(name = "custom splash duration {0} is preserved unchanged")
        @ValueSource(ints = {1200, 1800, 4000, 7200})
        void doesNotModifyUserCustomizedSplashDurations(final int customDuration) {
            // Given: a user has set a custom splash duration (not the bad default 2500)
            final YamlConfiguration config = loadYaml(
                    allEightTrickyTrialsPotionsYaml(
                            customDuration,
                            PotionConfig.TRICKY_TRIALS_LINGERING_CORRECT_DURATION));

            // When
            final boolean patched = PotionConfig.fixTrickyTrialsPotionDurations(config, LOGGER);

            // Then: custom splash values are untouched
            assertThat(patched).isFalse();
            for (final String splashKey : PotionConfig.TRICKY_TRIALS_SPLASH_POTION_KEYS) {
                assertThat(parseDurationFromFirstEffect(config, splashKey))
                        .as("Custom splash duration %d for '%s' should not be modified",
                                customDuration, splashKey)
                        .isEqualTo(customDuration);
            }
        }

        @ParameterizedTest(name = "custom lingering duration {0} is preserved unchanged")
        @ValueSource(ints = {300, 600, 1200, 2000})
        void doesNotModifyUserCustomizedLingeringDurations(final int customDuration) {
            // Given: a user has set a custom lingering duration (not the bad default 3000)
            final YamlConfiguration config = loadYaml(
                    allEightTrickyTrialsPotionsYaml(
                            PotionConfig.TRICKY_TRIALS_SPLASH_CORRECT_DURATION,
                            customDuration));

            // When
            final boolean patched = PotionConfig.fixTrickyTrialsPotionDurations(config, LOGGER);

            // Then: custom lingering values are untouched
            assertThat(patched).isFalse();
            for (final String lingeringKey : PotionConfig.TRICKY_TRIALS_LINGERING_POTION_KEYS) {
                assertThat(parseDurationFromFirstEffect(config, lingeringKey))
                        .as("Custom lingering duration %d for '%s' should not be modified",
                                customDuration, lingeringKey)
                        .isEqualTo(customDuration);
            }
        }

        @Test
        void silentlySkipsMissingPotionKeysAndPatchesPresentOnes() {
            /*
             * Intent: users who have customized potions.yml may have removed some or all of the
             * Tricky Trials potions. The migration must not log warnings or errors for absent
             * keys — it should silently skip them and only patch the keys that are present.
             */

            // Given: only two of the eight Tricky Trials potions are defined, both at bad values
            final String yamlWithTwoPotions = "Potions:\n"
                    + "  SPLASH_POTION_OF_INFESTATION:\n"
                    + "    Effects:\n"
                    + "      - \"INFESTED 0 " + PotionConfig.TRICKY_TRIALS_SPLASH_INCORRECT_DURATION + "\"\n"
                    + "  LINGERING_POTION_OF_OOZING:\n"
                    + "    Effects:\n"
                    + "      - \"OOZING 0 " + PotionConfig.TRICKY_TRIALS_LINGERING_INCORRECT_DURATION + "\"\n";
            final YamlConfiguration partialConfig = loadYaml(yamlWithTwoPotions);

            // When
            final boolean patched = PotionConfig.fixTrickyTrialsPotionDurations(
                    partialConfig, LOGGER);

            // Then: the two present potions are patched
            assertThat(patched).isTrue();
            assertThat(parseDurationFromFirstEffect(partialConfig, "SPLASH_POTION_OF_INFESTATION"))
                    .isEqualTo(PotionConfig.TRICKY_TRIALS_SPLASH_CORRECT_DURATION);
            assertThat(parseDurationFromFirstEffect(partialConfig, "LINGERING_POTION_OF_OOZING"))
                    .isEqualTo(PotionConfig.TRICKY_TRIALS_LINGERING_CORRECT_DURATION);

            // Then: the absent potions were silently skipped (still null)
            assertThat(partialConfig.getConfigurationSection(
                    "Potions.SPLASH_POTION_OF_OOZING")).isNull();
            assertThat(partialConfig.getConfigurationSection(
                    "Potions.LINGERING_POTION_OF_INFESTATION")).isNull();
        }

        @Test
        void returnsFalseWhenNoPotionsSectionExistsInConfig() {
            // Given: a completely empty config with no Potions section
            final YamlConfiguration emptyConfig = loadYaml("SomeOtherSection:\n  key: value\n");

            // When
            final boolean patched =
                    PotionConfig.fixTrickyTrialsPotionDurations(emptyConfig, LOGGER);

            // Then: nothing is patched, method returns false without throwing
            assertThat(patched).isFalse();
        }

        @Test
        void returnsFalseWhenAllTrickyTrialsKeysAreAbsent() {
            // Given: a config with unrelated potions only
            final String yamlWithUnrelatedPotions = "Potions:\n"
                    + "  POTION_OF_WATER_BREATHING:\n"
                    + "    Effects:\n"
                    + "      - \"WATER_BREATHING 0 3600\"\n";
            final YamlConfiguration configWithOnlyUnrelatedPotions =
                    loadYaml(yamlWithUnrelatedPotions);

            // When
            final boolean patched = PotionConfig.fixTrickyTrialsPotionDurations(
                    configWithOnlyUnrelatedPotions, LOGGER);

            // Then
            assertThat(patched).isFalse();
        }
    }
}
