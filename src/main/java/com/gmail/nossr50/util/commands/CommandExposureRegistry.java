package com.gmail.nossr50.util.commands;

import com.gmail.nossr50.mcMMO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class CommandExposureRegistry {
    private static final List<String> MANAGED_COMMAND_IDS = List.of(
            "mmoxpbar",
            "mmocompat",
            "mmoinfo",
            "mmodebug",
            "mcability",
            "mcgod",
            "mcchatspy",
            "mcmmo",
            "mcnotify",
            "mcrefresh",
            "mcscoreboard",
            "xprate",
            "mcpurge",
            "mcremove",
            "mmoshowdb",
            "mcconvert",
            "addlevels",
            "addxp",
            "mmoedit",
            "skillreset",
            "party",
            "ptp",
            "inspect",
            "mccooldown",
            "mcrank",
            "mcstats",
            "mctop",
            "acrobatics",
            "alchemy",
            "archery",
            "axes",
            "crossbows",
            "excavation",
            "fishing",
            "herbalism",
            "maces",
            "mining",
            "repair",
            "salvage",
            "smelting",
            "spears",
            "swords",
            "taming",
            "tridents",
            "unarmed",
            "woodcutting",
            "mcmmoreloadlocale"
    );

    private final Map<String, CommandExposureEntry> entries = new LinkedHashMap<>();
    private final Map<String, List<String>> appliedRoots = new LinkedHashMap<>();

    public CommandExposureRegistry(@NotNull YamlConfiguration config) {
        boolean defaultRegisterOriginal = config.getBoolean(
                "commands.defaults.registerOriginalRoot",
                true);
        CommandTabCompleteMode defaultMode = parseMode(
                config.getString("commands.defaults.tabCompleteMode"),
                "commands.defaults.tabCompleteMode");

        for (String commandId : MANAGED_COMMAND_IDS) {
            entries.put(commandId, readEntry(config, commandId, defaultRegisterOriginal,
                    defaultMode));
        }

        logUnknownConfiguredCommands(config);
    }

    public @NotNull CommandExposureEntry getEntry(@NotNull String commandId) {
        return entries.getOrDefault(commandId, CommandExposureEntry.defaults(true,
                CommandTabCompleteMode.BOTH));
    }

    public @NotNull List<String> getManagedCommandIds() {
        return MANAGED_COMMAND_IDS;
    }

    public void setAppliedRoots(@NotNull String commandId, @NotNull List<String> roots) {
        appliedRoots.put(commandId, List.copyOf(roots));
    }

    public @NotNull String getDisplayRoot(@NotNull String commandId) {
        List<String> roots = appliedRoots.get(commandId);
        if (roots != null && !roots.isEmpty()) {
            return roots.get(0);
        }

        CommandExposureEntry entry = getEntry(commandId);
        if (!entry.aliases().isEmpty()) {
            return entry.aliases().get(0);
        }

        return commandId;
    }

    public @NotNull String getPreferredDisplayToken(@NotNull String commandId,
            @NotNull String canonicalToken) {
        List<String> variants = new ArrayList<>();
        CommandExposureEntry entry = getEntry(commandId);

        variants.addAll(entry.subcommands().getOrDefault(canonicalToken, List.of()));
        variants.addAll(entry.arguments().getOrDefault(canonicalToken, List.of()));

        for (String variant : variants) {
            if (!variant.equalsIgnoreCase(canonicalToken)) {
                return variant;
            }
        }

        return canonicalToken;
    }

    public @NotNull String[] normalizeArguments(@NotNull String commandId, @NotNull String[] args) {
        if (args.length == 0) {
            return args;
        }

        String[] normalized = Arrays.copyOf(args, args.length);
        CommandExposureEntry entry = getEntry(commandId);

        switch (commandId) {
            case "mcmmo" -> normalizeToken(entry.subcommands(), normalized, 0);
            case "party" -> normalizePartyArguments(entry, normalized);
            case "ptp" -> normalizeToken(entry.arguments(), normalized, 0);
            case "mcconvert" -> normalizeMcconvertArguments(entry, normalized);
            case "mcscoreboard" -> normalizeToken(entry.arguments(), normalized, 0);
            case "xprate" -> normalizeXprateArguments(entry, normalized);
            case "mmoxpbar" -> normalizeToken(entry.arguments(), normalized, 0);
            case "addlevels", "addxp", "mmoedit" -> normalizeExperienceArguments(entry, normalized);
            case "skillreset" -> normalizeSkillResetArguments(entry, normalized);
            default -> {
            }
        }

        return normalized;
    }

    public @NotNull List<String> transformTabCompletions(@NotNull String commandId,
            @NotNull String[] args,
            @NotNull Collection<String> completions) {
        CommandExposureEntry entry = getEntry(commandId);
        Map<String, List<String>> tokenMap = getCompletionTokenMap(commandId, args);

        if (tokenMap.isEmpty() || entry.tabCompleteMode() == CommandTabCompleteMode.CANONICAL) {
            return List.copyOf(completions);
        }

        LinkedHashSet<String> transformed = new LinkedHashSet<>();
        for (String completion : completions) {
            String canonicalToken = findCanonical(tokenMap, completion);
            List<String> configuredTokens = canonicalToken == null ? null : tokenMap.get(
                    canonicalToken);

            if (entry.tabCompleteMode() == CommandTabCompleteMode.BOTH || configuredTokens == null
                    || configuredTokens.isEmpty()) {
                transformed.add(completion);
            }

            if (configuredTokens != null) {
                boolean addedDistinctToken = false;
                for (String configuredToken : configuredTokens) {
                    if (entry.tabCompleteMode() == CommandTabCompleteMode.TRANSLATED
                            && configuredToken.equalsIgnoreCase(canonicalToken)) {
                        continue;
                    }

                    if (!configuredToken.equalsIgnoreCase(completion)) {
                        transformed.add(configuredToken);
                        addedDistinctToken = true;
                    }
                }

                if (!addedDistinctToken && entry.tabCompleteMode() == CommandTabCompleteMode.TRANSLATED) {
                    transformed.add(completion);
                }
            }
        }

        return List.copyOf(transformed);
    }

    private void normalizePartyArguments(@NotNull CommandExposureEntry entry,
            @NotNull String[] normalized) {
        normalizeToken(entry.subcommands(), normalized, 0);

        if (normalized.length < 2) {
            return;
        }

        String subcommand = normalizeToken(normalized[0]);
        if (Set.of("xpshare", "itemshare", "lock", "chat", "password", "teleport")
                .contains(subcommand)) {
            normalizeToken(entry.arguments(), normalized, 1);
        }
    }

    private void normalizeMcconvertArguments(@NotNull CommandExposureEntry entry,
            @NotNull String[] normalized) {
        normalizeToken(entry.subcommands(), normalized, 0);

        if (normalized.length > 1) {
            normalizeToken(entry.arguments(), normalized, 1);
        }
    }

    private void normalizeXprateArguments(@NotNull CommandExposureEntry entry,
            @NotNull String[] normalized) {
        if (normalized.length == 1) {
            normalizeToken(entry.arguments(), normalized, 0);
            return;
        }

        if (normalized.length > 1) {
            normalizeToken(entry.arguments(), normalized, 1);
        }
    }

    private void normalizeExperienceArguments(@NotNull CommandExposureEntry entry,
            @NotNull String[] normalized) {
        int skillIndex;

        if (normalized.length == 2 || (normalized.length == 3 && isSilentToken(entry, normalized[2]))) {
            skillIndex = 0;
        } else if (normalized.length >= 3) {
            skillIndex = 1;
        } else {
            return;
        }

        normalizeToken(entry.arguments(), normalized, skillIndex);

        if (normalized.length > skillIndex + 2) {
            normalizeToken(entry.arguments(), normalized, normalized.length - 1);
        }
    }

    private void normalizeSkillResetArguments(@NotNull CommandExposureEntry entry,
            @NotNull String[] normalized) {
        int skillIndex = normalized.length == 1 ? 0 : 1;
        normalizeToken(entry.arguments(), normalized, skillIndex);
    }

    private boolean isSilentToken(@NotNull CommandExposureEntry entry, @NotNull String token) {
        String canonicalToken = findCanonical(entry.arguments(), token);
        return "-s".equalsIgnoreCase(canonicalToken) || "-s".equalsIgnoreCase(token);
    }

    private @NotNull Map<String, List<String>> getCompletionTokenMap(@NotNull String commandId,
            @NotNull String[] args) {
        CommandExposureEntry entry = getEntry(commandId);

        return switch (commandId) {
            case "mcmmo" -> args.length == 1 ? entry.subcommands() : Map.of();
            case "party" -> getPartyCompletionTokens(entry, args);
            case "ptp", "mcscoreboard" -> args.length == 1 ? entry.arguments() : Map.of();
            case "xprate" -> args.length == 1 || args.length == 2 ? entry.arguments() : Map.of();
            case "mmoxpbar" -> args.length == 1 ? entry.arguments() : Map.of();
            case "mcconvert" -> args.length == 1 ? entry.subcommands()
                    : args.length == 2 ? entry.arguments() : Map.of();
            case "addlevels", "addxp", "mmoedit" -> getExperienceCompletionTokens(args,
                    entry.arguments());
            case "skillreset" -> args.length == 2 ? entry.arguments() : Map.of();
            default -> Map.of();
        };
    }

    private @NotNull Map<String, List<String>> getExperienceCompletionTokens(@NotNull String[] args,
            @NotNull Map<String, List<String>> tokenMap) {
        return switch (args.length) {
            case 2, 3, 4 -> tokenMap;
            default -> Map.of();
        };
    }

    private @NotNull Map<String, List<String>> getPartyCompletionTokens(
            @NotNull CommandExposureEntry entry,
            @NotNull String[] args) {
        if (args.length == 1) {
            return entry.subcommands();
        }

        if (args.length == 2) {
            String subcommand = findCanonical(entry.subcommands(), args[0]);
            if (subcommand == null) {
                subcommand = normalizeToken(args[0]);
            }

            if (Set.of("xpshare", "itemshare", "lock", "chat", "password", "teleport")
                    .contains(subcommand)) {
                return entry.arguments();
            }
        }

        return Map.of();
    }

    private void normalizeToken(@NotNull Map<String, List<String>> tokenMap,
            @NotNull String[] normalized,
            int index) {
        if (index < 0 || index >= normalized.length) {
            return;
        }

        String canonical = findCanonical(tokenMap, normalized[index]);
        if (canonical != null) {
            normalized[index] = canonical;
        }
    }

    private @NotNull String normalizeToken(@NotNull String token) {
        return token.toLowerCase(Locale.ENGLISH);
    }

    private String findCanonical(@NotNull Map<String, List<String>> tokenMap, @NotNull String token) {
        String normalizedToken = normalizeToken(token);
        for (Map.Entry<String, List<String>> entry : tokenMap.entrySet()) {
            if (normalizeToken(entry.getKey()).equals(normalizedToken)) {
                return entry.getKey();
            }

            for (String variant : entry.getValue()) {
                if (normalizeToken(variant).equals(normalizedToken)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    private @NotNull CommandExposureEntry readEntry(@NotNull YamlConfiguration config,
            @NotNull String commandId,
            boolean defaultRegisterOriginal,
            @NotNull CommandTabCompleteMode defaultMode) {
        String path = "commands." + commandId;

        if (!config.isConfigurationSection(path)) {
            return CommandExposureEntry.defaults(defaultRegisterOriginal, defaultMode);
        }

        boolean enabled = config.getBoolean(path + ".enabled", true);
        boolean registerOriginal = config.getBoolean(path + ".registerOriginalRoot",
                defaultRegisterOriginal);
        String perCommandModeValue = config.getString(path + ".tabCompleteMode");
        CommandTabCompleteMode tabCompleteMode = perCommandModeValue == null
                ? defaultMode
                : parseMode(perCommandModeValue, path + ".tabCompleteMode");

        return new CommandExposureEntry(
                enabled,
                registerOriginal,
                readTokens(config, path + ".aliases", commandId + ".aliases"),
                readTokenSection(config.getConfigurationSection(path + ".subcommands"),
                        commandId + ".subcommands"),
                readTokenSection(config.getConfigurationSection(path + ".arguments"),
                        commandId + ".arguments"),
                tabCompleteMode
        );
    }

    private @NotNull CommandTabCompleteMode parseMode(String value, String path) {
        CommandTabCompleteMode mode = CommandTabCompleteMode.fromConfig(value);
        if (value != null && !mode.name().equalsIgnoreCase(value)) {
            mcMMO.p.getLogger().warning("Invalid tab completion mode at commands.yml path '"
                    + path + "': " + value + ". Falling back to BOTH.");
        }
        return mode;
    }

    private @NotNull Map<String, List<String>> readTokenSection(ConfigurationSection section,
            String path) {
        Map<String, List<String>> tokens = new LinkedHashMap<>();
        Map<String, String> reverseLookup = new LinkedHashMap<>();

        if (section == null) {
            return tokens;
        }

        for (String canonicalToken : section.getKeys(false)) {
            List<String> values = sanitizeTokens(section.getStringList(canonicalToken),
                    path + "." + canonicalToken);
            List<String> acceptedValues = new ArrayList<>();

            for (String value : values) {
                String existingCanonical = reverseLookup.putIfAbsent(normalizeToken(value),
                        canonicalToken);

                if (existingCanonical != null && !existingCanonical.equalsIgnoreCase(
                        canonicalToken)) {
                    mcMMO.p.getLogger().warning("Ignoring conflicting token '" + value
                            + "' in commands.yml at " + path + "." + canonicalToken
                            + " because it is already mapped to '" + existingCanonical + "'.");
                    continue;
                }

                acceptedValues.add(value);
            }

            if (!acceptedValues.isEmpty()) {
                tokens.put(canonicalToken, List.copyOf(acceptedValues));
            }
        }

        return tokens;
    }

    private @NotNull List<String> readTokens(@NotNull YamlConfiguration config, @NotNull String path,
            @NotNull String label) {
        return sanitizeTokens(config.getStringList(path), label);
    }

    private @NotNull List<String> sanitizeTokens(@NotNull List<String> rawTokens,
            @NotNull String label) {
        LinkedHashSet<String> sanitized = new LinkedHashSet<>();

        for (String rawToken : rawTokens) {
            if (rawToken == null) {
                continue;
            }

            String token = rawToken.trim();
            if (token.isBlank()) {
                mcMMO.p.getLogger().warning("Ignoring blank command token in commands.yml at "
                        + label + ".");
                continue;
            }

            if (token.contains(" ") || token.contains("\t")) {
                mcMMO.p.getLogger().warning("Ignoring command token with whitespace in commands.yml at "
                        + label + ": " + token);
                continue;
            }

            if (token.startsWith("/")) {
                mcMMO.p.getLogger().warning("Ignoring command token with leading '/' in commands.yml at "
                        + label + ": " + token);
                continue;
            }

            sanitized.add(token);
        }

        return List.copyOf(sanitized);
    }

    private void logUnknownConfiguredCommands(@NotNull YamlConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("commands");
        if (section == null) {
            return;
        }

        Set<String> knownSections = section.getKeys(false).stream()
                .filter(key -> !"defaults".equalsIgnoreCase(key))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (String configuredCommand : knownSections) {
            if (!MANAGED_COMMAND_IDS.contains(configuredCommand)) {
                mcMMO.p.getLogger().warning("Ignoring unknown Bukkit command in commands.yml: "
                        + configuredCommand);
            }
        }
    }
}
