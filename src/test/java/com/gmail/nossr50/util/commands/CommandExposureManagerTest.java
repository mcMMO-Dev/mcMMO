package com.gmail.nossr50.util.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class CommandExposureManagerTest {
    @Test
    void disabledCommandsAreUnregisteredAndNotReRegistered() {
        CommandMap commandMap = mock(CommandMap.class);
        Logger logger = mock(Logger.class);
        Runnable syncCommands = mock(Runnable.class);
        PluginCommand woodcutting = mockCommand("woodcutting");

        Map<String, PluginCommand> managedCommands = Map.of("woodcutting", woodcutting);
        Map<String, Command> knownCommands = new LinkedHashMap<>();
        knownCommands.put("woodcutting", woodcutting);

        YamlConfiguration config = new YamlConfiguration();
        config.set("commands.woodcutting.enabled", false);
        CommandExposureRegistry registry = new CommandExposureRegistry(config);

        CommandExposureManager.applyConfiguredExposure(commandMap, knownCommands, registry,
                managedCommands, logger, syncCommands);

        verify(woodcutting, atLeastOnce()).unregister(commandMap);
        verify(commandMap, never()).register(eq("woodcutting"), any(Command.class));
        verify(syncCommands).run();
        assertEquals("woodcutting", registry.getDisplayRoot("woodcutting"));
        assertTrue(knownCommands.isEmpty());
    }

    @Test
    void customRootReplacesOriginalWhenRegisterOriginalRootIsDisabled() {
        CommandMap commandMap = mock(CommandMap.class);
        Logger logger = mock(Logger.class);
        Runnable syncCommands = mock(Runnable.class);
        PluginCommand party = mockCommand("party");

        Map<String, PluginCommand> managedCommands = Map.of("party", party);
        Map<String, Command> knownCommands = new LinkedHashMap<>();
        knownCommands.put("party", party);

        YamlConfiguration config = new YamlConfiguration();
        config.set("commands.party.registerOriginalRoot", false);
        config.set("commands.party.aliases", List.of("grupo"));
        CommandExposureRegistry registry = new CommandExposureRegistry(config);

        CommandExposureManager.applyConfiguredExposure(commandMap, knownCommands, registry,
                managedCommands, logger, syncCommands);

        verify(party).setName("grupo");
        verify(party).setLabel("grupo");
        verify(party).setAliases(List.of());
        verify(commandMap).register("party", party);
        verify(syncCommands).run();
        assertEquals("grupo", registry.getDisplayRoot("party"));
        assertTrue(knownCommands.isEmpty());
    }

    @Test
    void conflictingAliasesAreSkippedPerAliasInsteadOfDisablingBothCommands() {
        CommandMap commandMap = mock(CommandMap.class);
        Logger logger = mock(Logger.class);
        Runnable syncCommands = mock(Runnable.class);
        PluginCommand party = mockCommand("party");
        PluginCommand ptp = mockCommand("ptp");

        Map<String, PluginCommand> managedCommands = new LinkedHashMap<>();
        managedCommands.put("party", party);
        managedCommands.put("ptp", ptp);

        Map<String, Command> knownCommands = new LinkedHashMap<>();
        knownCommands.put("party", party);
        knownCommands.put("ptp", ptp);

        YamlConfiguration config = new YamlConfiguration();
        config.set("commands.party.registerOriginalRoot", false);
        config.set("commands.party.aliases", List.of("shared", "grupo"));
        config.set("commands.ptp.registerOriginalRoot", false);
        config.set("commands.ptp.aliases", List.of("shared", "tele"));
        CommandExposureRegistry registry = new CommandExposureRegistry(config);

        CommandExposureManager.applyConfiguredExposure(commandMap, knownCommands, registry,
                managedCommands, logger, syncCommands);

        verify(party).setName("grupo");
        verify(party).setAliases(List.of());
        verify(ptp).setName("tele");
        verify(ptp).setAliases(List.of());
        verify(commandMap).register("party", party);
        verify(commandMap).register("ptp", ptp);
        verify(logger).warning("Skipping conflicting alias 'shared' for party, ptp because it is configured for multiple mcMMO commands.");
        assertEquals("grupo", registry.getDisplayRoot("party"));
        assertEquals("tele", registry.getDisplayRoot("ptp"));
    }

    private PluginCommand mockCommand(String name) {
        PluginCommand command = mock(PluginCommand.class);
        CommandExecutor executor = mock(CommandExecutor.class);

        when(command.getName()).thenReturn(name);
        when(command.getAliases()).thenReturn(List.of());
        when(command.getExecutor()).thenReturn(executor);
        when(command.getUsage()).thenReturn(null);

        return command;
    }
}
