package com.gmail.nossr50.util.commands;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class CommandExposureRegistryTest {
    @Test
    void normalizesConfiguredPartyAliasesToCanonicalTokens() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("commands.defaults.registerOriginalRoot", true);
        config.set("commands.defaults.tabCompleteMode", "both");
        config.set("commands.party.subcommands.join", List.of("join", "entrar"));
        config.set("commands.party.arguments.accept", List.of("accept", "aceitar"));

        CommandExposureRegistry registry = new CommandExposureRegistry(config);

        assertArrayEquals(new String[]{"join", "Steve"},
                registry.normalizeArguments("party", new String[]{"entrar", "Steve"}));
        assertArrayEquals(new String[]{"teleport", "accept"},
                registry.normalizeArguments("party", new String[]{"teleport", "aceitar"}));
    }

    @Test
    void translatedTabCompletionModePrefersConfiguredTokens() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("commands.defaults.registerOriginalRoot", true);
        config.set("commands.defaults.tabCompleteMode", "translated");
        config.set("commands.party.subcommands.join", List.of("join", "entrar"));

        CommandExposureRegistry registry = new CommandExposureRegistry(config);

        assertEquals("entrar", registry.getPreferredDisplayToken("party", "join"));
        assertIterableEquals(List.of("entrar"),
                registry.transformTabCompletions("party", new String[]{""}, List.of("join")));
        assertIterableEquals(List.of("entrar"),
                registry.transformTabCompletions("party", new String[]{""},
                        List.of("join", "entrar")));
    }

    @Test
    void translatedTabCompletionRetainsCanonicalWhenNoDistinctTranslationExists() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("commands.defaults.registerOriginalRoot", true);
        config.set("commands.defaults.tabCompleteMode", "translated");
        config.set("commands.party.subcommands.join", List.of("join"));

        CommandExposureRegistry registry = new CommandExposureRegistry(config);

        assertIterableEquals(List.of("join"),
                registry.transformTabCompletions("party", new String[]{""}, List.of("join")));
    }
}
