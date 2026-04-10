package com.gmail.nossr50.util.commands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public record CommandExposureEntry(
        boolean enabled,
        boolean registerOriginal,
        @NotNull List<String> aliases,
        @NotNull Map<String, List<String>> subcommands,
        @NotNull Map<String, List<String>> arguments,
        @NotNull CommandTabCompleteMode tabCompleteMode
) {
    public static @NotNull CommandExposureEntry defaults(boolean registerOriginal,
            @NotNull CommandTabCompleteMode tabCompleteMode) {
        return new CommandExposureEntry(
                true,
                registerOriginal,
                List.of(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                tabCompleteMode
        );
    }
}
