package com.gmail.nossr50.commands.levelup;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class PowerLevelUpCommandBuilder {
    private Set<Integer> levels = null;
    private LinkedList<String> commands = null;
    private Predicate<Integer> predicate = null;
    private boolean logInfo;

    public PowerLevelUpCommandBuilder() {
        this.logInfo = false;
    }

    public PowerLevelUpCommandBuilder withPredicate(Predicate<Integer> predicate) {
        this.predicate = predicate;
        return this;
    }

    public PowerLevelUpCommandBuilder withLogInfo(boolean logInfo) {
        this.logInfo = logInfo;
        return this;
    }

    public PowerLevelUpCommandBuilder command(@NotNull String command) {
        this.commands = new LinkedList<>();
        this.commands.add(command);
        return this;
    }

    public PowerLevelUpCommandBuilder commands(@NotNull Collection<String> command) {
        this.commands = new LinkedList<>(command);
        return this;
    }

    public PowerLevelUpCommandBuilder withLevels(@NotNull Collection<Integer> levels) {
        requireNonNull(levels, "levels is null!");
        this.levels = Set.copyOf(levels);
        return this;
    }

    public PowerLevelUpCommand build() {
        requireNonNull(commands, "commandStr is null");
        if (predicate == null) {
            requireNonNull(levels, "levels is null");

            return new PowerLevelUpCommand((level) -> levels.contains(level), commands, logInfo);
        }

        return new PowerLevelUpCommand(predicate, commands, logInfo);
    }
}
