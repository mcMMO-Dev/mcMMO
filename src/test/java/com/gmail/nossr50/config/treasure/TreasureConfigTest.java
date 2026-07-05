package com.gmail.nossr50.config.treasure;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link TreasureConfig#classifyExcavationTreasure}. These verify that a treasure
 * config never fails to load an entry with an exception: every entry is classified as loadable,
 * incompatible (harmless — material from a newer MC version), or invalid (misconfigured).
 */
class TreasureConfigTest {

    private static final Logger LOGGER = Logger.getLogger(TreasureConfigTest.class.getName());

    /** Parses a YAML string into a {@link YamlConfiguration} without touching the file system. */
    private static YamlConfiguration loadYaml(final String yaml) {
        return YamlConfiguration.loadConfiguration(new StringReader(yaml));
    }

    @Nested
    class ClassifyExcavationTreasure {

        @Test
        void classifyShouldReturnLoadedWhenEntryIsValid() {
            // Given - a well-formed excavation entry for a material present in this MC version
            final YamlConfiguration config = loadYaml(
                    "Excavation:\n"
                            + "  DIAMOND:\n"
                            + "    Amount: 1\n"
                            + "    XP: 1000\n"
                            + "    Drop_Chance: 0.13\n"
                            + "    Level_Requirement:\n"
                            + "      Standard_Mode: 35\n"
                            + "      Retro_Mode: 350\n");

            // When - the entry is classified in standard mode
            final TreasureLoadResult result = TreasureConfig.classifyExcavationTreasure(
                    config, "Excavation", "DIAMOND", false, LOGGER);

            // Then - it is loadable
            assertThat(result).isEqualTo(TreasureLoadResult.LOADED);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "MUSIC_DISC_FROM_THE_FUTURE", "TOTALLY_FAKE_MATERIAL_XYZ", "NOT_A_REAL_BLOCK_123"})
        void classifyShouldReturnIncompatibleWhenMaterialAbsentFromThisVersion(
                final String material) {
            /*
             * Intent: an entry whose material does not exist in the running (older) MC version — for
             * example a music disc added in a newer game version — must be treated as harmless and
             * skipped, never failing startup.
             */

            // Given - an entry referencing a material this MC version does not know
            final YamlConfiguration config = loadYaml(
                    "Excavation:\n"
                            + "  " + material + ":\n"
                            + "    Amount: 1\n"
                            + "    XP: 3000\n"
                            + "    Drop_Chance: 0.05\n"
                            + "    Level_Requirement:\n"
                            + "      Standard_Mode: 25\n"
                            + "      Retro_Mode: 250\n");

            // When
            final TreasureLoadResult result = TreasureConfig.classifyExcavationTreasure(
                    config, "Excavation", material, false, LOGGER);

            // Then - it is classified as incompatible (harmless), not invalid
            assertThat(result).isEqualTo(TreasureLoadResult.INCOMPATIBLE);
        }

        @Test
        void classifyShouldUseRetroLevelRequirementInRetroMode() {
            /*
             * Intent: the level requirement is read from Retro_Mode or Standard_Mode depending on the
             * server mode. An entry that only defines Retro_Mode should load in retro mode but be
             * invalid in standard mode (its standard requirement is absent).
             */

            // Given - an entry that only defines the Retro_Mode level requirement
            final YamlConfiguration config = loadYaml(
                    "Excavation:\n"
                            + "  DIAMOND:\n"
                            + "    XP: 10\n"
                            + "    Drop_Chance: 1.0\n"
                            + "    Level_Requirement:\n"
                            + "      Retro_Mode: 350\n");

            // When - classified in retro and then standard mode
            final TreasureLoadResult retro = TreasureConfig.classifyExcavationTreasure(
                    config, "Excavation", "DIAMOND", true, LOGGER);
            final TreasureLoadResult standard = TreasureConfig.classifyExcavationTreasure(
                    config, "Excavation", "DIAMOND", false, LOGGER);

            // Then - retro loads, standard is invalid (missing Standard_Mode requirement)
            assertThat(retro).isEqualTo(TreasureLoadResult.LOADED);
            assertThat(standard).isEqualTo(TreasureLoadResult.INVALID);
        }

        @Test
        void classifyShouldReturnInvalidWhenLevelRequirementMissing() {
            // Given - an otherwise valid entry with no Level_Requirement at all
            final YamlConfiguration config = loadYaml(
                    "Excavation:\n"
                            + "  DIAMOND:\n"
                            + "    XP: 10\n"
                            + "    Drop_Chance: 1.0\n");

            // When
            final TreasureLoadResult result = TreasureConfig.classifyExcavationTreasure(
                    config, "Excavation", "DIAMOND", false, LOGGER);

            // Then
            assertThat(result).isEqualTo(TreasureLoadResult.INVALID);
        }

        @Test
        void classifyShouldReturnInvalidWhenXpNegative() {
            // Given - an entry with a negative XP value
            final YamlConfiguration config = loadYaml(
                    "Excavation:\n"
                            + "  DIAMOND:\n"
                            + "    XP: -5\n"
                            + "    Drop_Chance: 1.0\n"
                            + "    Level_Requirement:\n"
                            + "      Standard_Mode: 5\n");

            // When
            final TreasureLoadResult result = TreasureConfig.classifyExcavationTreasure(
                    config, "Excavation", "DIAMOND", false, LOGGER);

            // Then
            assertThat(result).isEqualTo(TreasureLoadResult.INVALID);
        }

        @Test
        void classifyShouldReturnInvalidWhenDropChanceNegative() {
            // Given - an entry with a negative Drop_Chance
            final YamlConfiguration config = loadYaml(
                    "Excavation:\n"
                            + "  DIAMOND:\n"
                            + "    XP: 10\n"
                            + "    Drop_Chance: -1.0\n"
                            + "    Level_Requirement:\n"
                            + "      Standard_Mode: 5\n");

            // When
            final TreasureLoadResult result = TreasureConfig.classifyExcavationTreasure(
                    config, "Excavation", "DIAMOND", false, LOGGER);

            // Then
            assertThat(result).isEqualTo(TreasureLoadResult.INVALID);
        }

        @Test
        void classifyShouldReturnIncompatibleWhenPotionTypeAbsentFromThisVersion() {
            /*
             * Intent: newer game versions add new potion types and shipped default configs may
             * reference them. On an older server such an entry is harmless — it must be skipped
             * quietly as INCOMPATIBLE, never warned about as misconfigured.
             */

            // Given - a potion entry whose PotionData.PotionType this MC version does not know
            final YamlConfiguration config = loadYaml(
                    "Hylian_Luck:\n"
                            + "  POTION:\n"
                            + "    XP: 10\n"
                            + "    Drop_Chance: 1.0\n"
                            + "    Level_Requirement:\n"
                            + "      Standard_Mode: 5\n"
                            + "    PotionData:\n"
                            + "      PotionType: POTION_TYPE_FROM_THE_FUTURE\n");

            // When
            final TreasureLoadResult result = TreasureConfig.classifyExcavationTreasure(
                    config, "Hylian_Luck", "POTION", false, LOGGER);

            // Then - incompatible (harmless), not invalid
            assertThat(result).isEqualTo(TreasureLoadResult.INCOMPATIBLE);
        }

        @ParameterizedTest
        @ValueSource(strings = {"abc", "1.5", "99999"})
        void classifyShouldReturnInvalidWhenDataSuffixNotNumeric(final String suffix) {
            /*
             * Intent: a key like STONE|abc carries a data suffix that cannot parse as a short
             * (non-numeric, fractional, or out of range). Classification must report INVALID
             * naming the bad suffix rather than throwing NumberFormatException — the classify
             * methods are contractually exception-free.
             */

            // Given - an otherwise valid entry whose key carries an unparseable data suffix
            final String treasureName = "STONE|" + suffix;
            final YamlConfiguration config = loadYaml(
                    "Excavation:\n"
                            + "  '" + treasureName + "':\n"
                            + "    XP: 10\n"
                            + "    Drop_Chance: 1.0\n"
                            + "    Level_Requirement:\n"
                            + "      Standard_Mode: 5\n");

            // When
            final TreasureLoadResult result = TreasureConfig.classifyExcavationTreasure(
                    config, "Excavation", treasureName, false, LOGGER);

            // Then - INVALID, not an exception
            assertThat(result).isEqualTo(TreasureLoadResult.INVALID);
        }

        @Test
        void classifyShouldReturnInvalidWhenBlockDataOutOfRange() {
            /*
             * Intent: legacy block data must fit in a signed byte. A block entry with an out-of-range
             * Data value is a misconfiguration and should be reported as invalid, not crash loading.
             */

            // Given - a block material with a Data value outside the signed-byte range
            final YamlConfiguration config = loadYaml(
                    "Excavation:\n"
                            + "  STONE:\n"
                            + "    XP: 10\n"
                            + "    Drop_Chance: 1.0\n"
                            + "    Data: 200\n"
                            + "    Level_Requirement:\n"
                            + "      Standard_Mode: 5\n");

            // When
            final TreasureLoadResult result = TreasureConfig.classifyExcavationTreasure(
                    config, "Excavation", "STONE", false, LOGGER);

            // Then
            assertThat(result).isEqualTo(TreasureLoadResult.INVALID);
        }
    }
}
