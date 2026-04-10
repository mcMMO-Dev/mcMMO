package com.gmail.nossr50.util.commands;

import com.gmail.nossr50.mcMMO;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;

public final class CommandExposureManager {
    private CommandExposureManager() {
    }

    public static void applyConfiguredExposure() {
        CommandMap commandMap;
        Map<String, Command> knownCommands;

        try {
            commandMap = getCommandMap();
            knownCommands = getKnownCommands(commandMap);
        } catch (IllegalStateException exception) {
            mcMMO.p.getLogger().warning("Command exposure config could not access Bukkit internals"
                    + " on this server implementation. Falling back to legacy command registration.");
            mcMMO.p.getLogger().warning(exception.getMessage());
            return;
        }

        CommandExposureRegistry registry = mcMMO.p.getCommandExposureRegistry();
        Map<String, PluginCommand> managedCommands = collectManagedCommands(registry);
        applyConfiguredExposure(commandMap, knownCommands, registry, managedCommands,
                mcMMO.p.getLogger(), CommandExposureManager::syncCommands);
    }

    static void applyConfiguredExposure(@NotNull CommandMap commandMap,
            @NotNull Map<String, Command> knownCommands,
            @NotNull CommandExposureRegistry registry,
            @NotNull Map<String, PluginCommand> managedCommands,
            @NotNull Logger logger,
            @NotNull Runnable syncCommandsAction) {
        Map<String, List<String>> requestedRoots = new LinkedHashMap<>();

        for (Map.Entry<String, PluginCommand> entry : managedCommands.entrySet()) {
            String commandId = entry.getKey();
            CommandExposureEntry exposureEntry = registry.getEntry(commandId);

            if (!exposureEntry.enabled()) {
                unregister(entry.getValue(), commandMap, knownCommands);
                registry.setAppliedRoots(commandId, List.of());
                continue;
            }

            requestedRoots.put(commandId, computeRequestedRoots(entry.getValue(), exposureEntry));
        }

        Map<String, Set<String>> rejectedRoots = collectRejectedRoots(requestedRoots, managedCommands,
                knownCommands, logger);

        pruneRejectedRoots(requestedRoots, rejectedRoots);

        for (PluginCommand command : managedCommands.values()) {
            unregister(command, commandMap, knownCommands);
        }

        for (Map.Entry<String, PluginCommand> entry : managedCommands.entrySet()) {
            String commandId = entry.getKey();
            CommandExposureEntry exposureEntry = registry.getEntry(commandId);
            if (!exposureEntry.enabled()) {
                continue;
            }

            PluginCommand command = entry.getValue();
            List<String> roots = requestedRoots.get(commandId);
            String canonicalName = commandId;
            if (roots == null || roots.isEmpty()) {
                logger.warning("Disabling command '" + commandId
                        + "' because commands.yml leaves it with no registered root or alias.");
                registry.setAppliedRoots(commandId, List.of());
                continue;
            }

            applyWrappers(commandId, command);
            command.setName(roots.get(0));
            command.setLabel(roots.get(0));
            command.setAliases(roots.subList(1, roots.size()));
            if (command.getUsage() != null) {
                command.setUsage(CommandSyntaxFormatter.transformText(command.getUsage()));
            }
            registry.setAppliedRoots(commandId, roots);
            commandMap.register(canonicalName, command);
        }

        syncCommandsAction.run();
    }

    private static void applyWrappers(@NotNull String commandId, @NotNull PluginCommand command) {
        CommandExecutor originalExecutor = command.getExecutor();
        if (originalExecutor == null) {
            return;
        }

        TabCompleter originalTabCompleter = originalExecutor instanceof TabCompleter
                ? (TabCompleter) originalExecutor
                : command.getTabCompleter();

        ConfiguredCommandAdapter adapter = new ConfiguredCommandAdapter(commandId, originalExecutor,
                originalTabCompleter);
        command.setExecutor(adapter);
        command.setTabCompleter(adapter);
    }

    private static @NotNull Map<String, PluginCommand> collectManagedCommands(
            @NotNull CommandExposureRegistry registry) {
        Map<String, PluginCommand> commands = new LinkedHashMap<>();

        for (String commandId : registry.getManagedCommandIds()) {
            PluginCommand command = mcMMO.p.getCommand(commandId);
            if (command == null || command.getExecutor() == null) {
                continue;
            }
            commands.put(commandId, command);
        }

        return commands;
    }

    private static @NotNull List<String> computeRequestedRoots(@NotNull PluginCommand command,
            @NotNull CommandExposureEntry entry) {
        LinkedHashSet<String> roots = new LinkedHashSet<>();

        if (entry.registerOriginal()) {
            roots.add(command.getName());
            roots.addAll(command.getAliases());
        }

        roots.addAll(entry.aliases());
        return List.copyOf(roots);
    }

    private static @NotNull Map<String, Set<String>> collectRejectedRoots(
            @NotNull Map<String, List<String>> requestedRoots,
            @NotNull Map<String, PluginCommand> managedCommands,
            @NotNull Map<String, Command> knownCommands,
            @NotNull Logger logger) {
        Map<String, Set<String>> rejectedRoots = new LinkedHashMap<>();

        collectInternalConflicts(requestedRoots, rejectedRoots, logger);
        collectExternalConflicts(requestedRoots, managedCommands, knownCommands, rejectedRoots,
                logger);

        return rejectedRoots;
    }

    private static void collectInternalConflicts(@NotNull Map<String, List<String>> requestedRoots,
            @NotNull Map<String, Set<String>> rejectedRoots,
            @NotNull Logger logger) {
        Map<String, List<String>> rootsToCommands = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : requestedRoots.entrySet()) {
            for (String root : entry.getValue()) {
                rootsToCommands.computeIfAbsent(root.toLowerCase(), ignored -> new ArrayList<>())
                        .add(entry.getKey());
            }
        }

        for (Map.Entry<String, List<String>> entry : rootsToCommands.entrySet()) {
            if (entry.getValue().size() > 1) {
                for (String commandId : entry.getValue()) {
                    rejectedRoots.computeIfAbsent(commandId, ignored -> new LinkedHashSet<>())
                            .add(entry.getKey());
                }

                logger.warning("Skipping conflicting alias '" + entry.getKey()
                        + "' for "
                        + String.join(", ", entry.getValue())
                        + " because it is configured for multiple mcMMO commands.");
            }
        }
    }

    private static void collectExternalConflicts(@NotNull Map<String, List<String>> requestedRoots,
            @NotNull Map<String, PluginCommand> managedCommands,
            @NotNull Map<String, Command> knownCommands,
            @NotNull Map<String, Set<String>> rejectedRoots,
            @NotNull Logger logger) {
        Collection<PluginCommand> managedValues = managedCommands.values();

        for (Map.Entry<String, List<String>> entry : requestedRoots.entrySet()) {
            String commandId = entry.getKey();

            for (String root : entry.getValue()) {
                Command existing = knownCommands.get(root.toLowerCase());
                if (existing == null || managedValues.contains(existing)) {
                    continue;
                }

                rejectedRoots.computeIfAbsent(commandId, ignored -> new LinkedHashSet<>())
                        .add(root.toLowerCase());
                logger.warning("Skipping conflicting alias '" + root + "' for '"
                        + commandId
                        + "' because it is already registered by another Bukkit command.");
            }
        }
    }

    private static void pruneRejectedRoots(@NotNull Map<String, List<String>> requestedRoots,
            @NotNull Map<String, Set<String>> rejectedRoots) {
        for (Map.Entry<String, List<String>> entry : requestedRoots.entrySet()) {
            Set<String> commandRejectedRoots = rejectedRoots.getOrDefault(entry.getKey(),
                    Collections.emptySet());

            if (commandRejectedRoots.isEmpty()) {
                continue;
            }

            List<String> filteredRoots = entry.getValue().stream()
                    .filter(root -> !commandRejectedRoots.contains(root.toLowerCase()))
                    .toList();
            requestedRoots.put(entry.getKey(), filteredRoots);
        }
    }

    private static void unregister(@NotNull PluginCommand command, @NotNull CommandMap commandMap,
            @NotNull Map<String, Command> knownCommands) {
        command.unregister(commandMap);

        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
            if (entry.getValue() == command) {
                keysToRemove.add(entry.getKey());
            }
        }

        for (String key : keysToRemove) {
            knownCommands.remove(key);
        }
    }

    @SuppressWarnings("unchecked")
    private static @NotNull Map<String, Command> getKnownCommands(@NotNull CommandMap commandMap) {
        if (!(commandMap instanceof SimpleCommandMap simpleCommandMap)) {
            throw new IllegalStateException("Unsupported CommandMap implementation: " + commandMap);
        }

        try {
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            return (Map<String, Command>) knownCommandsField.get(simpleCommandMap);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to access Bukkit known commands map.",
                    exception);
        }
    }

    private static @NotNull CommandMap getCommandMap() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        if (!(pluginManager instanceof SimplePluginManager simplePluginManager)) {
            throw new IllegalStateException(
                    "Unsupported PluginManager implementation: " + pluginManager.getClass());
        }

        try {
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (CommandMap) commandMapField.get(simplePluginManager);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to access Bukkit command map.", exception);
        }
    }

    private static final class ConfiguredCommandAdapter implements TabExecutor {
        private final String commandId;
        private final CommandExecutor delegate;
        private final TabCompleter tabCompleter;

        private ConfiguredCommandAdapter(@NotNull String commandId, @NotNull CommandExecutor delegate,
                TabCompleter tabCompleter) {
            this.commandId = commandId;
            this.delegate = delegate;
            this.tabCompleter = tabCompleter;
        }

        @Override
        public boolean onCommand(@NotNull org.bukkit.command.CommandSender sender,
                @NotNull Command command,
                @NotNull String label,
                @NotNull String[] args) {
            String[] normalizedArgs = mcMMO.p.getCommandExposureRegistry().normalizeArguments(
                    commandId, args);
            return delegate.onCommand(sender, command, label, normalizedArgs);
        }

        @Override
        public List<String> onTabComplete(@NotNull org.bukkit.command.CommandSender sender,
                @NotNull Command command,
                @NotNull String alias,
                @NotNull String[] args) {
            if (tabCompleter == null) {
                return List.of();
            }

            String[] normalizedArgs = mcMMO.p.getCommandExposureRegistry().normalizeArguments(
                    commandId, args);
            List<String> completions = tabCompleter.onTabComplete(sender, command, alias,
                    normalizedArgs);

            if (completions == null) {
                return List.of();
            }

            return mcMMO.p.getCommandExposureRegistry().transformTabCompletions(commandId,
                    normalizedArgs, completions);
        }
    }

    private static void syncCommands() {
        try {
            Method syncCommands = Bukkit.getServer().getClass().getMethod("syncCommands");
            syncCommands.invoke(Bukkit.getServer());
        } catch (ReflectiveOperationException exception) {
            mcMMO.p.getLogger().warning("Command exposure config could not sync the server command"
                    + " tree after applying commands.yml changes. Client-side command suggestions"
                    + " may still show stale roots on this server implementation.");
        }
    }
}
