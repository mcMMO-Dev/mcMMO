package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.BiPredicate;

import static java.util.Objects.requireNonNull;

public class SkillLevelUpCommandBuilder {
    private Set<PrimarySkillType> skillFilter = null;
    private Set<Integer> levels = null;
    private LinkedList<String> commands = null;
    private BiPredicate<PrimarySkillType, Integer> predicate = null;
    private boolean logInfo;

    public SkillLevelUpCommandBuilder() {
        this.logInfo = false;
    }

    public SkillLevelUpCommandBuilder withPredicate(BiPredicate<PrimarySkillType, Integer> predicate) {
        this.predicate = predicate;
        return this;
    }

    public SkillLevelUpCommandBuilder withLogInfo(boolean logInfo) {
        this.logInfo = logInfo;
        return this;
    }

    public SkillLevelUpCommandBuilder command(@NotNull String command) {
        this.commands = new LinkedList<>();
        this.commands.add(command);
        return this;
    }

    public SkillLevelUpCommandBuilder commands(@NotNull Collection<String> command) {
        this.commands = new LinkedList<>(command);
        return this;
    }

    public SkillLevelUpCommandBuilder withLevels(@NotNull Collection<Integer> levels) {
        requireNonNull(levels, "levels is null!");
        this.levels = Set.copyOf(levels);
        return this;
    }

    public SkillLevelUpCommandBuilder withSkillFilter(@NotNull Set<PrimarySkillType> skillFilter) {
        requireNonNull(skillFilter, "skillFilter is null!");
        if (skillFilter.isEmpty()) {
            throw new IllegalArgumentException("skillFilter is empty");
        }
        this.skillFilter = skillFilter;
        return this;
    }

    public SkillLevelUpCommandBuilder withSkillFilter(@NotNull PrimarySkillType skill) {
        requireNonNull(skill, "skill is null!");
        this.skillFilter = Set.of(skill);
        return this;
    }

    public SkillLevelUpCommand build() {
        requireNonNull(commands, "commandStr is null");
        if (predicate == null) {
            requireNonNull(levels, "levels is null");

            return new SkillLevelUpCommand((skill, level) -> {
                if (skillFilter == null) {
                    return levels.contains(level);
                } else {
                    return skillFilter.contains(skill) && levels.contains(level);
                }
            }, commands, logInfo);
        }

        return new SkillLevelUpCommand(predicate, commands, logInfo);
    }
}
