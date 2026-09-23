package com.gmail.nossr50.commands.skills;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * /smelting shows the Vanilla XP multiplier that a player's Understanding The Art rank earns.
 * These tests load a real advanced.yml, edited the way a server owner edits it, so a value lost
 * anywhere between the file and the chat line fails here.
 */
class SmeltingCommandTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(SmeltingCommandTest.class.getName());
    private static final String SMELTING_MULTIPLIER_COMMENT =
            "# VanillaXPMultiplier: Vanilla XP gained from smelting ores";
    private static final String MULTIPLIER_STAT_LABEL = "Vanilla XP Multiplier: ";

    /** JUnit deletes this folder, and the advanced.yml written into it, after each test. */
    @TempDir
    File dataFolder;

    private SmeltingCommand smeltingCommand;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        // Only the Understanding The Art line is under test
        when(Permissions.canUseSubSkill(player, SubSkillType.SMELTING_FUEL_EFFICIENCY))
                .thenReturn(false);
        when(Permissions.canUseSubSkill(player, SubSkillType.SMELTING_SECOND_SMELT))
                .thenReturn(false);
        when(Permissions.vanillaXpBoost(player, PrimarySkillType.SMELTING)).thenReturn(true);
        when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SMELTING_UNDERSTANDING_THE_ART))
                .thenReturn(true);

        smeltingCommand = new SmeltingCommand();
        smeltingCommand.mmoPlayer = mmoPlayer;
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private static InputStream shippedAdvancedYml() {
        return SmeltingCommandTest.class.getClassLoader().getResourceAsStream("advanced.yml");
    }

    /**
     * Writes the shipped advanced.yml into the data folder with the Smelting multipliers
     * replaced, then loads it through the real config loader.
     */
    private void loadAdvancedYmlWithSmeltingMultipliers(List<String> rankMultipliers)
            throws IOException {
        final String shippedText;
        try (InputStream shipped = shippedAdvancedYml()) {
            assertThat(shipped).as("advanced.yml on the test classpath").isNotNull();
            shippedText = new String(shipped.readAllBytes(), StandardCharsets.UTF_8);
        }

        final int smeltingBlockStart = shippedText.indexOf(SMELTING_MULTIPLIER_COMMENT);
        assertThat(smeltingBlockStart).as("Smelting multiplier block").isNotNegative();
        // The first Rank_N lines after the comment are the Smelting ones
        String smeltingOnward = shippedText.substring(smeltingBlockStart);
        for (int rank = 1; rank <= rankMultipliers.size(); rank++) {
            smeltingOnward = smeltingOnward.replaceFirst("Rank_" + rank + ": [^\\r\\n]*",
                    "Rank_" + rank + ": " + rankMultipliers.get(rank - 1));
        }
        Files.writeString(new File(dataFolder, "advanced.yml").toPath(),
                shippedText.substring(0, smeltingBlockStart) + smeltingOnward);

        when(mcMMO.p.getResource("advanced.yml")).thenAnswer(invocation -> shippedAdvancedYml());
        final AdvancedConfig loadedConfig = new AdvancedConfig(dataFolder);
        when(mcMMO.p.getAdvancedConfig()).thenReturn(loadedConfig);
    }

    /** Runs /smelting's stat steps for a player at this rank and returns the lines, uncolored. */
    private List<String> statLinesAtRank(int rank) {
        when(RankUtils.getRank(player, SubSkillType.SMELTING_UNDERSTANDING_THE_ART))
                .thenReturn(rank);

        smeltingCommand.permissionsCheck(player);
        smeltingCommand.dataCalculations(player, 0);
        return smeltingCommand.statsDisplay(player, 0, false, false).stream()
                .map(ChatColor::stripColor)
                .toList();
    }

    @Nested
    class VanillaXpMultiplierLine {
        /** Differs from both the rank number and the shipped default at every rank. */
        private static final List<String> WHOLE_NUMBER_MULTIPLIERS =
                List.of("3", "4", "6", "7", "9", "10", "12", "15");
        /** A server owner's curve that was reported as never showing up in game. */
        private static final List<String> REPORTED_DECIMAL_MULTIPLIERS =
                List.of("1", "1.05", "1.1", "1.15", "1.2", "1.3", "1.4", "1.5");

        @ParameterizedTest(name = "Rank_{0}")
        @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8})
        void shouldShowTheWholeNumberSetInAdvancedYml(int rank) throws IOException {
            // Given - advanced.yml with every Smelting multiplier changed from the default
            loadAdvancedYmlWithSmeltingMultipliers(WHOLE_NUMBER_MULTIPLIERS);

            // When - a player at this Understanding The Art rank runs /smelting
            final List<String> statLines = statLinesAtRank(rank);

            // Then - the line shows the value from the file
            assertThat(statLines).containsExactly(
                    MULTIPLIER_STAT_LABEL + WHOLE_NUMBER_MULTIPLIERS.get(rank - 1) + "x");
        }

        /**
         * The multiplier is read as a whole number, since vanilla XP orbs are whole numbers.
         * The fraction is cut off, so a curve from 1 to 1.5 shows 1x at every rank.
         */
        @ParameterizedTest(name = "Rank_{0}")
        @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8})
        void shouldShowOneAtEveryRankForTheReportedDecimalCurve(int rank) throws IOException {
            // Given - advanced.yml with the reported decimal curve
            loadAdvancedYmlWithSmeltingMultipliers(REPORTED_DECIMAL_MULTIPLIERS);

            // When - a player at this Understanding The Art rank runs /smelting
            final List<String> statLines = statLinesAtRank(rank);

            // Then - the fraction is dropped
            assertThat(statLines).containsExactly(MULTIPLIER_STAT_LABEL + "1x");
        }

        /** Rounds down rather than to the nearest, and never shows less than 1x. */
        @ParameterizedTest(name = "{0} -> {1}x")
        @CsvSource({
                "2.9, 2",
                "4.5, 4",
                "0.5, 1",
        })
        void shouldShowADecimalRoundedDownButNotBelowOne(String configuredMultiplier,
                int shownMultiplier) throws IOException {
            // Given - advanced.yml with the same decimal at every rank
            loadAdvancedYmlWithSmeltingMultipliers(Collections.nCopies(8, configuredMultiplier));

            // When - a player at the top rank runs /smelting
            final List<String> statLines = statLinesAtRank(8);

            // Then - the whole-number part is shown
            assertThat(statLines).containsExactly(MULTIPLIER_STAT_LABEL + shownMultiplier + "x");
        }

        @Test
        void shouldBeHiddenUntilUnderstandingTheArtUnlocks() throws IOException {
            // Given - a configured curve and a player who has not unlocked the subskill
            loadAdvancedYmlWithSmeltingMultipliers(WHOLE_NUMBER_MULTIPLIERS);
            when(RankUtils.hasUnlockedSubskill(player,
                    SubSkillType.SMELTING_UNDERSTANDING_THE_ART)).thenReturn(false);

            // When - the player runs /smelting
            final List<String> statLines = statLinesAtRank(0);

            // Then - no multiplier line is shown
            assertThat(statLines).isEmpty();
        }
    }
}
