package com.gmail.nossr50.config.treasure;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FishingTreasureConfigTest {

    // ---------------------------------------------------------------------------
    // YAML helpers
    // ---------------------------------------------------------------------------

    /** Builds a minimal Shake YAML containing only the legacy MUSHROOM_COW section. */
    private static String yamlWithMushroomCow() {
        return "Shake:\n"
                + "  MUSHROOM_COW:\n"
                + "    MILK_BUCKET:\n"
                + "      Amount: 1\n"
                + "      XP: 0\n"
                + "      Drop_Chance: 5.0\n"
                + "      Drop_Level: 0\n"
                + "    MUSHROOM_STEW:\n"
                + "      Amount: 1\n"
                + "      XP: 0\n"
                + "      Drop_Chance: 5.0\n"
                + "      Drop_Level: 0\n";
    }

    /** Parses a YAML string into a {@link YamlConfiguration} without touching the file system. */
    private static YamlConfiguration loadYaml(final String yaml) {
        return YamlConfiguration.loadConfiguration(new StringReader(yaml));
    }

    // ---------------------------------------------------------------------------
    // Migration tests
    // ---------------------------------------------------------------------------

    @Nested
    class MooshroomEntityIdMigration {

        @Test
        void renamesMushRoomCowToMooshroom() {
            /*
             * Intent: verifies that fixMooshroomEntityId renames Shake.MUSHROOM_COW to
             * Shake.MOOSHROOM so the section is picked up by the EntityType iteration loop.
             */

            // Given: config with the legacy MUSHROOM_COW key
            final YamlConfiguration config = loadYaml(yamlWithMushroomCow());

            // When
            final boolean patched = FishingTreasureConfig.fixMooshroomEntityId(config);

            // Then: method reports that a patch was applied
            assertThat(patched).isTrue();

            // Then: MOOSHROOM section now exists with the original treasure entries
            assertThat(config.getConfigurationSection("Shake.MOOSHROOM"))
                    .as("Shake.MOOSHROOM section should exist after migration")
                    .isNotNull();
            assertThat(config.contains("Shake.MOOSHROOM.MILK_BUCKET"))
                    .as("MILK_BUCKET entry should be present under Shake.MOOSHROOM")
                    .isTrue();
            assertThat(config.contains("Shake.MOOSHROOM.MUSHROOM_STEW"))
                    .as("MUSHROOM_STEW entry should be present under Shake.MOOSHROOM")
                    .isTrue();
        }

        @Test
        void removesLegacyMushroomCowKeyAfterRename() {
            // Given: config with the legacy MUSHROOM_COW key
            final YamlConfiguration config = loadYaml(yamlWithMushroomCow());

            // When
            FishingTreasureConfig.fixMooshroomEntityId(config);

            // Then: the old MUSHROOM_COW section is gone
            assertThat(config.getConfigurationSection("Shake.MUSHROOM_COW"))
                    .as("Shake.MUSHROOM_COW section should be removed after migration")
                    .isNull();
        }

        @Test
        void returnsFalseWhenMushroomCowKeyIsAbsent() {
            /*
             * Intent: servers that already have the correct MOOSHROOM key (or never had the bad
             * key) should not trigger a patch.
             */

            // Given: config that already has MOOSHROOM (no legacy key)
            final String yaml = "Shake:\n"
                    + "  MOOSHROOM:\n"
                    + "    MILK_BUCKET:\n"
                    + "      Amount: 1\n"
                    + "      XP: 0\n"
                    + "      Drop_Chance: 5.0\n"
                    + "      Drop_Level: 0\n";
            final YamlConfiguration config = loadYaml(yaml);

            // When
            final boolean patched = FishingTreasureConfig.fixMooshroomEntityId(config);

            // Then
            assertThat(patched).isFalse();
        }

        @Test
        void returnsFalseWhenShakeSectionIsAbsent() {
            // Given: a config with no Shake section at all
            final YamlConfiguration config = loadYaml("Fishing:\n  SALMON:\n    Amount: 1\n");

            // When
            final boolean patched = FishingTreasureConfig.fixMooshroomEntityId(config);

            // Then
            assertThat(patched).isFalse();
        }

        @Test
        void doesNotOverwriteExistingMooshroomSectionWhenBothKeysPresent() {
            /*
             * Intent: if a user somehow has both MUSHROOM_COW and MOOSHROOM defined (e.g. manual
             * copy-paste error), we should not overwrite the existing MOOSHROOM data — just remove
             * the legacy key.
             */

            // Given: config with both the legacy and correct keys
            final String yaml = "Shake:\n"
                    + "  MUSHROOM_COW:\n"
                    + "    MILK_BUCKET:\n"
                    + "      Drop_Chance: 99.0\n"
                    + "  MOOSHROOM:\n"
                    + "    MUSHROOM_STEW:\n"
                    + "      Drop_Chance: 5.0\n";
            final YamlConfiguration config = loadYaml(yaml);

            // When
            final boolean patched = FishingTreasureConfig.fixMooshroomEntityId(config);

            // Then: MUSHROOM_COW is removed but MOOSHROOM is preserved unchanged
            assertThat(patched).isTrue();
            assertThat(config.getConfigurationSection("Shake.MUSHROOM_COW")).isNull();
            assertThat(config.getDouble("Shake.MOOSHROOM.MUSHROOM_STEW.Drop_Chance"))
                    .as("Existing MOOSHROOM data should not be overwritten")
                    .isEqualTo(5.0);
            // The MILK_BUCKET from the legacy section should NOT have been copied over
            assertThat(config.contains("Shake.MOOSHROOM.MILK_BUCKET"))
                    .as("MILK_BUCKET from MUSHROOM_COW should not overwrite MOOSHROOM")
                    .isFalse();
        }

        @Test
        void preservesDropChanceAndOtherFieldsAfterRename() {
            // Given
            final YamlConfiguration config = loadYaml(yamlWithMushroomCow());

            // When
            FishingTreasureConfig.fixMooshroomEntityId(config);

            // Then: MILK_BUCKET Drop_Chance is preserved under the new key
            assertThat(config.getDouble("Shake.MOOSHROOM.MILK_BUCKET.Drop_Chance"))
                    .as("Drop_Chance should be copied faithfully to MOOSHROOM")
                    .isEqualTo(5.0);
            assertThat(config.getInt("Shake.MOOSHROOM.MILK_BUCKET.Amount"))
                    .as("Amount should be copied faithfully to MOOSHROOM")
                    .isEqualTo(1);
        }
    }
}

