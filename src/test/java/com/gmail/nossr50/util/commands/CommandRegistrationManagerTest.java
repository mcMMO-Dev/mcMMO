package com.gmail.nossr50.util.commands;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

/**
 * Cross-checks the commands declared in plugin.yml against the executors the plugin actually
 * wires up. A command declared without an executor silently does nothing when run, and an
 * executor wired to an undeclared command never registers at all - both drifts have shipped
 * unnoticed before, so this keeps the two sides honest.
 */
class CommandRegistrationManagerTest {

    /**
     * Commands registered through the ACF command framework in CommandManager rather than
     * CommandRegistrationManager.
     */
    private static final Set<String> ACF_REGISTERED_COMMANDS =
            Set.of("mmopower", "adminchat", "partychat");

    @Test
    void everyDeclaredCommandShouldHaveARegisteredExecutor() throws Exception {
        // Given - the command declarations from plugin.yml
        final Set<String> declared = declaredCommands();

        // When - collecting every command name the plugin wires an executor for
        final Set<String> wired = new HashSet<>(CommandRegistrationManager.specCommandNames());
        wired.addAll(CommandRegistrationManager.skillCommandNames());
        wired.addAll(ACF_REGISTERED_COMMANDS);

        // Then - every declared command has an executor...
        final Set<String> declaredWithoutExecutor = new HashSet<>(declared);
        declaredWithoutExecutor.removeAll(wired);
        assertThat(declaredWithoutExecutor)
                .as("commands declared in plugin.yml without a registered executor")
                .isEmpty();

        // And - every wired command is declared in plugin.yml
        final Set<String> wiredWithoutDeclaration = new HashSet<>(wired);
        wiredWithoutDeclaration.removeAll(declared);
        assertThat(wiredWithoutDeclaration)
                .as("commands registered in code but not declared in plugin.yml")
                .isEmpty();
    }

    private static Set<String> declaredCommands() throws Exception {
        try (InputStream in = CommandRegistrationManagerTest.class
                .getResourceAsStream("/plugin.yml")) {
            assertThat(in).as("plugin.yml on the test classpath").isNotNull();
            final YamlConfiguration pluginYml = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            final ConfigurationSection commands = pluginYml.getConfigurationSection("commands");
            assertThat(commands).as("commands section in plugin.yml").isNotNull();
            return commands.getKeys(false);
        }
    }
}
