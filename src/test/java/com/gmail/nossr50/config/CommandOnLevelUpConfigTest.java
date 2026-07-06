package com.gmail.nossr50.config;

import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.HERBALISM;
import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.MINING;
import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.LevelUpCommandAPI;
import com.gmail.nossr50.commands.levelup.LevelUpCommand;
import com.gmail.nossr50.commands.levelup.RegistrationSource;
import com.gmail.nossr50.mcMMO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Parsing tests for {@link CommandOnLevelUpConfig}. The loader is the bridge between what
 * server owners write in levelupcommands.yml and what actually fires, so malformed entries
 * must be skipped with warnings instead of half-registering.
 */
class CommandOnLevelUpConfigTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(CommandOnLevelUpConfigTest.class.getName());

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void validSkillEntryShouldRegisterCommand() throws IOException {
        // Given - a complete entry with two skills, two levels and two commands
        loadConfig("""
                level_up_commands:
                    milestones:
                        skills: [ Mining, Herbalism ]
                        levels: [ 10, 20 ]
                        commands:
                            - "say first"
                            - "say second"
                """);

        // Then - one command registered with everything parsed
        final List<LevelUpCommand> commands = configCommands();
        assertThat(commands).hasSize(1);
        final LevelUpCommand command = commands.get(0);
        assertThat(command.getCondition().getSkills()).containsExactlyInAnyOrder(MINING,
                HERBALISM);
        assertThat(command.getCondition().getLevels()).containsExactlyInAnyOrder(10, 20);
        assertThat(command.getCommands()).containsExactly("say first", "say second");
        assertThat(command.getRunAs()).isEqualTo(LevelUpCommand.RunAs.CONSOLE);
    }

    @Test
    void powerLevelEntryShouldRegisterCommand() throws IOException {
        // Given - an entry with only a power level trigger
        loadConfig("""
                level_up_commands:
                    power_milestones:
                        power_levels: [ 500, 1000 ]
                        commands:
                            - "say power!"
                """);

        // Then - the command registered with the power level condition
        final List<LevelUpCommand> commands = configCommands();
        assertThat(commands).hasSize(1);
        assertThat(commands.get(0).getCondition().getPowerLevels())
                .containsExactlyInAnyOrder(500, 1000);
        assertThat(commands.get(0).getCondition().getSkills()).isEmpty();
    }

    @Test
    void singleStringSkillAndCommandShouldParse() throws IOException {
        // Given - skills and commands written as single strings instead of lists
        loadConfig("""
                level_up_commands:
                    compact:
                        skills: Mining
                        levels: [ 5 ]
                        commands: "say compact"
                """);

        // Then - both single-string forms parsed
        final List<LevelUpCommand> commands = configCommands();
        assertThat(commands).hasSize(1);
        assertThat(commands.get(0).getCondition().getSkills()).containsExactly(MINING);
        assertThat(commands.get(0).getCommands()).containsExactly("say compact");
    }

    @Test
    void allKeywordShouldExpandToEveryNonChildSkill() throws IOException {
        // Given - the 'all' skills keyword
        loadConfig("""
                level_up_commands:
                    mastery:
                        skills: all
                        levels: [ 100 ]
                        commands: "say mastery"
                """);

        // Then - the condition covers every non-child skill
        final List<LevelUpCommand> commands = configCommands();
        assertThat(commands).hasSize(1);
        assertThat(commands.get(0).getCondition().getSkills())
                .containsExactlyInAnyOrderElementsOf(mcMMO.p.getSkillTools().getNonChildSkills());
    }

    @Test
    void unknownSkillShouldBeIgnoredButValidSkillsKept() throws IOException {
        // Given - an entry mixing a valid and an unknown skill name
        loadConfig("""
                level_up_commands:
                    typo_entry:
                        skills: [ Mining, Explodifying ]
                        levels: [ 10 ]
                        commands: "say ok"
                """);

        // Then - the entry registered with only the valid skill
        final List<LevelUpCommand> commands = configCommands();
        assertThat(commands).hasSize(1);
        assertThat(commands.get(0).getCondition().getSkills()).containsExactly(MINING);
    }

    @Test
    void incompleteEntriesShouldBeSkipped() throws IOException {
        // Given - entries missing commands, missing skills, and missing levels
        loadConfig("""
                level_up_commands:
                    no_commands:
                        skills: [ Mining ]
                        levels: [ 10 ]
                    no_skills:
                        levels: [ 10 ]
                        commands: "say orphan levels"
                    no_levels:
                        skills: [ Mining ]
                        commands: "say orphan skills"
                """);

        // Then - none of them registered
        assertThat(configCommands()).isEmpty();
    }

    @Test
    void disabledEntryShouldBeSkipped() throws IOException {
        // Given - a valid but disabled entry
        loadConfig("""
                level_up_commands:
                    sleeping:
                        enabled: false
                        skills: [ Mining ]
                        levels: [ 10 ]
                        commands: "say never"
                """);

        // Then - nothing registered
        assertThat(configCommands()).isEmpty();
    }

    @Test
    void runAsShouldParsePlayerAndFallBackToConsoleOnGarbage() throws IOException {
        // Given - one PLAYER entry and one entry with a bogus run_as value
        loadConfig("""
                level_up_commands:
                    as_player:
                        skills: [ Mining ]
                        levels: [ 10 ]
                        run_as: player
                        commands: "me leveled"
                    as_garbage:
                        skills: [ Herbalism ]
                        levels: [ 10 ]
                        run_as: OPERATOR
                        commands: "say fallback"
                """);

        // Then - PLAYER parsed case-insensitively, garbage fell back to CONSOLE
        final List<LevelUpCommand> commands = configCommands();
        assertThat(commands).hasSize(2);
        assertThat(commands).anySatisfy(command -> {
            assertThat(command.getCondition().getSkills()).containsExactly(MINING);
            assertThat(command.getRunAs()).isEqualTo(LevelUpCommand.RunAs.PLAYER);
        });
        assertThat(commands).anySatisfy(command -> {
            assertThat(command.getCondition().getSkills()).containsExactly(HERBALISM);
            assertThat(command.getRunAs()).isEqualTo(LevelUpCommand.RunAs.CONSOLE);
        });
    }

    @Test
    void nonPositiveLevelsShouldBeIgnored() throws IOException {
        // Given - a level list containing zero and a negative value
        loadConfig("""
                level_up_commands:
                    weird_levels:
                        skills: [ Mining ]
                        levels: [ -5, 0, 10 ]
                        commands: "say ten"
                """);

        // Then - only the positive level survived
        final List<LevelUpCommand> commands = configCommands();
        assertThat(commands).hasSize(1);
        assertThat(commands.get(0).getCondition().getLevels()).containsExactly(10);
    }

    @Test
    void reloadShouldReplaceConfigEntriesAndPreserveApiRegistrations() throws IOException {
        // Given - a loaded config and an API registration made afterwards
        final String yaml = """
                level_up_commands:
                    milestones:
                        skills: [ Mining ]
                        levels: [ 10 ]
                        commands: "say hi"
                """;
        loadConfig(yaml);
        LevelUpCommandAPI.registerHandler((player, skill, levelsGained, powerLevel) -> {
        });
        assertThat(levelUpCommandManager.registrationCount()).isEqualTo(2);

        // When - the config loads again (what a reload does)
        loadConfig(yaml);

        // Then - config entries were replaced, not duplicated, and the API handler survived
        assertThat(configCommands()).hasSize(1);
        assertThat(levelUpCommandManager.registrationCount()).isEqualTo(2);
    }

    private void loadConfig(@NotNull String yaml) throws IOException {
        final File configFile = new File(testDataFolder, "levelupcommands.yml");
        Files.writeString(configFile.toPath(), yaml);
        when(mcMMO.p.getResource("levelupcommands.yml")).thenAnswer(invocation ->
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));
        new CommandOnLevelUpConfig(testDataFolder);
    }

    private @NotNull List<LevelUpCommand> configCommands() {
        return levelUpCommandManager.getCommands(RegistrationSource.CONFIG);
    }
}
