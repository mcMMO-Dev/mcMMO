package com.gmail.nossr50.config.treasure;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FishingTreasureConfigTest {

    private static final Logger LOGGER = Logger.getLogger(FishingTreasureConfigTest.class.getName());

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

    // ---------------------------------------------------------------------------
    // Entry classification tests
    // ---------------------------------------------------------------------------

    @Nested
    class ClassifyFishingTreasure {

        @Test
        void classifyShouldReturnLoadedForValidFishingEntry() {
            // Given - a well-formed Fishing entry for a material present in this MC version
            final YamlConfiguration config = loadYaml(
                    "Fishing:\n"
                            + "  COD:\n"
                            + "    Amount: 1\n"
                            + "    XP: 100\n"
                            + "    Drop_Chance: 5.0\n"
                            + "    Drop_Level: 0\n"
                            + "    Rarity: COMMON\n");

            // When
            final TreasureLoadResult result = FishingTreasureConfig.classifyFishingTreasure(
                    config, "Fishing", "COD", true, LOGGER);

            // Then
            assertThat(result).isEqualTo(TreasureLoadResult.LOADED);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "MUSIC_DISC_FROM_THE_FUTURE", "TOTALLY_FAKE_MATERIAL_XYZ", "NOT_A_REAL_ITEM_123"})
        void classifyShouldReturnIncompatibleWhenMaterialAbsentFromThisVersion(
                final String material) {
            /*
             * Intent: an entry whose material does not exist in the running (older) MC version must
             * be treated as harmless and skipped, never failing startup.
             */

            // Given - a Fishing entry referencing a material this MC version does not know
            final YamlConfiguration config = loadYaml(
                    "Fishing:\n"
                            + "  " + material + ":\n"
                            + "    XP: 100\n"
                            + "    Drop_Chance: 5.0\n"
                            + "    Drop_Level: 0\n"
                            + "    Rarity: COMMON\n");

            // When
            final TreasureLoadResult result = FishingTreasureConfig.classifyFishingTreasure(
                    config, "Fishing", material, true, LOGGER);

            // Then - incompatible (harmless), not invalid
            assertThat(result).isEqualTo(TreasureLoadResult.INCOMPATIBLE);
        }

        @Test
        void classifyShouldReturnLoadedForInventoryMagicEntryWithoutMaterial() {
            /*
             * Intent: the special INVENTORY shake entry has no real material; it must be recognized
             * as loadable rather than being treated as an unknown material.
             */

            // Given - the magic INVENTORY entry under a Shake section
            final YamlConfiguration config = loadYaml(
                    "Shake:\n"
                            + "  PLAYER:\n"
                            + "    INVENTORY:\n"
                            + "      Drop_Chance: 5.0\n"
                            + "      Drop_Level: 0\n");

            // When
            final TreasureLoadResult result = FishingTreasureConfig.classifyFishingTreasure(
                    config, "Shake.PLAYER", "INVENTORY", false, LOGGER);

            // Then
            assertThat(result).isEqualTo(TreasureLoadResult.LOADED);
        }

        @Test
        void classifyShouldReturnInvalidWhenFishingEntryMissingRarity() {
            // Given - a Fishing entry with no Rarity (required for Fishing rewards)
            final YamlConfiguration config = loadYaml(
                    "Fishing:\n"
                            + "  COD:\n"
                            + "    XP: 100\n"
                            + "    Drop_Chance: 5.0\n"
                            + "    Drop_Level: 0\n");

            // When
            final TreasureLoadResult result = FishingTreasureConfig.classifyFishingTreasure(
                    config, "Fishing", "COD", true, LOGGER);

            // Then
            assertThat(result).isEqualTo(TreasureLoadResult.INVALID);
        }

        @Test
        void classifyShouldNotRequireRarityForShakeEntries() {
            /*
             * Intent: Rarity is only meaningful for Fishing rewards. A Shake entry without a Rarity
             * must still load.
             */

            // Given - a Shake entry with no Rarity
            final YamlConfiguration config = loadYaml(
                    "Shake:\n"
                            + "  COW:\n"
                            + "    LEATHER:\n"
                            + "      XP: 0\n"
                            + "      Drop_Chance: 5.0\n"
                            + "      Drop_Level: 0\n");

            // When
            final TreasureLoadResult result = FishingTreasureConfig.classifyFishingTreasure(
                    config, "Shake.COW", "LEATHER", false, LOGGER);

            // Then
            assertThat(result).isEqualTo(TreasureLoadResult.LOADED);
        }

        @ParameterizedTest
        @ValueSource(strings = {"XP", "Drop_Chance", "Drop_Level"})
        void classifyShouldReturnInvalidWhenNumericFieldNegative(final String field) {
            // Given - a Fishing entry where exactly one numeric field is negative
            final YamlConfiguration config = loadYaml(
                    "Fishing:\n"
                            + "  COD:\n"
                            + "    XP: " + ("XP".equals(field) ? "-1" : "100") + "\n"
                            + "    Drop_Chance: " + ("Drop_Chance".equals(field) ? "-1.0" : "5.0")
                            + "\n"
                            + "    Drop_Level: " + ("Drop_Level".equals(field) ? "-1" : "0") + "\n"
                            + "    Rarity: COMMON\n");

            // When
            final TreasureLoadResult result = FishingTreasureConfig.classifyFishingTreasure(
                    config, "Fishing", "COD", true, LOGGER);

            // Then
            assertThat(result).isEqualTo(TreasureLoadResult.INVALID);
        }
    }

    // ---------------------------------------------------------------------------
    // Drop-rate validation tests (non-fatal)
    // ---------------------------------------------------------------------------

    @Nested
    class DropRateValidation {

        @Test
        void collectShouldReturnEmptyWhenRatesAreValid() {
            // Given - drop rates within the valid 0..100 range
            final YamlConfiguration config = loadYaml(
                    "Enchantment_Drop_Rates:\n"
                            + "  Tier_1:\n"
                            + "    COMMON: 50.0\n"
                            + "Item_Drop_Rates:\n"
                            + "  Tier_1:\n"
                            + "    COMMON: 50.0\n");

            // When
            final List<String> problems = FishingTreasureConfig.collectDropRateProblems(config);

            // Then
            assertThat(problems).isEmpty();
        }

        @Test
        void collectShouldReturnEmptyWhenSectionAbsent() {
            // Given - a config with no Enchantment_Drop_Rates section at all
            final YamlConfiguration config = loadYaml("Fishing:\n  COD:\n    XP: 100\n");

            // When
            final List<String> problems = FishingTreasureConfig.collectDropRateProblems(config);

            // Then
            assertThat(problems).isEmpty();
        }

        @Test
        void collectShouldFlagEnchantRateAboveOneHundredWithKeyIdentifier() {
            // Given - an enchantment drop rate above 100
            final YamlConfiguration config = loadYaml(
                    "Enchantment_Drop_Rates:\n"
                            + "  Tier_1:\n"
                            + "    COMMON: 150.0\n");

            // When
            final List<String> problems = FishingTreasureConfig.collectDropRateProblems(config);

            // Then - a problem is reported and it names the offending key so admins can find it
            assertThat(problems)
                    .isNotEmpty()
                    .anyMatch(problem -> problem.contains("Enchantment_Drop_Rates.Tier_1.COMMON"));
        }

        @Test
        void collectShouldFlagNegativeItemRate() {
            // Given - a negative item drop rate
            final YamlConfiguration config = loadYaml(
                    "Enchantment_Drop_Rates:\n"
                            + "  Tier_1:\n"
                            + "    COMMON: 10.0\n"
                            + "Item_Drop_Rates:\n"
                            + "  Tier_1:\n"
                            + "    COMMON: -5.0\n");

            // When
            final List<String> problems = FishingTreasureConfig.collectDropRateProblems(config);

            // Then
            assertThat(problems)
                    .isNotEmpty()
                    .anyMatch(problem -> problem.contains("Item_Drop_Rates.Tier_1.COMMON"));
        }
    }
}

