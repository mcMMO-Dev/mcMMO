package com.gmail.nossr50.util.commands;

import com.gmail.nossr50.mcMMO;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public final class CommandSyntaxFormatter {
    private CommandSyntaxFormatter() {
    }

    public static @NotNull String command(@NotNull String commandId, @NotNull String... tokens) {
        StringBuilder builder = new StringBuilder("/").append(getRegistry().getDisplayRoot(
                commandId));

        for (String token : tokens) {
            builder.append(' ').append(getRegistry().getPreferredDisplayToken(commandId, token));
        }

        return builder.toString();
    }

    public static @NotNull String transformText(@NotNull String text) {
        String transformed = text;

        for (String commandId : getRegistry().getManagedCommandIds()) {
            String canonicalRoot = "/" + commandId;
            String displayRoot = "/" + getRegistry().getDisplayRoot(commandId);

            transformed = transformed.replace(canonicalRoot, displayRoot);

            for (Map.Entry<String, String> tokenEntry : getDisplayTokens(commandId).entrySet()) {
                transformed = transformed.replace(displayRoot + " " + tokenEntry.getKey(),
                        displayRoot + " " + tokenEntry.getValue());
                transformed = transformed.replace("<" + tokenEntry.getKey() + ">",
                        "<" + tokenEntry.getValue() + ">");
            }
        }

        return transformed;
    }

    private static @NotNull Map<String, String> getDisplayTokens(@NotNull String commandId) {
        Map<String, String> displayTokens = new LinkedHashMap<>();
        CommandExposureEntry entry = getRegistry().getEntry(commandId);

        for (Map.Entry<String, List<String>> tokenEntry : entry.subcommands().entrySet()) {
            displayTokens.put(tokenEntry.getKey(),
                    getRegistry().getPreferredDisplayToken(commandId, tokenEntry.getKey()));
        }

        for (Map.Entry<String, List<String>> tokenEntry : entry.arguments().entrySet()) {
            displayTokens.putIfAbsent(tokenEntry.getKey(),
                    getRegistry().getPreferredDisplayToken(commandId, tokenEntry.getKey()));
        }

        return displayTokens;
    }

    private static @NotNull CommandExposureRegistry getRegistry() {
        return mcMMO.p.getCommandExposureRegistry();
    }
}
