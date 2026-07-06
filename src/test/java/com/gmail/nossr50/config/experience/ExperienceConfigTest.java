package com.gmail.nossr50.config.experience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

import com.gmail.nossr50.config.BukkitConfig;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
