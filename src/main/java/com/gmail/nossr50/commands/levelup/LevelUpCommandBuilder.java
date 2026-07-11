package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * Builds a {@link LevelUpCommand}. Provide at least one command and either skills with levels,
 * power levels, or both.
 */
public final class LevelUpCommandBuilder {
    private final Set<PrimarySkillType> skills = new LinkedHashSet<>();
    private final Set<Integer> levels = new LinkedHashSet<>();
    private final Set<Integer> powerLevels = new LinkedHashSet<>();
    private final List<String> commands = new ArrayList<>();
    private LevelUpCommand.RunAs runAs = LevelUpCommand.RunAs.CONSOLE;

    public @NotNull LevelUpCommandBuilder withSkills(
            @NotNull Collection<PrimarySkillType> skills) {
        this.skills.addAll(skills);
        return this;
    }

    public @NotNull LevelUpCommandBuilder withSkill(@NotNull PrimarySkillType skill) {
        this.skills.add(skill);
        return this;
    }

    public @NotNull LevelUpCommandBuilder withLevels(@NotNull Collection<Integer> levels) {
        this.levels.addAll(levels);
        return this;
    }

    public @NotNull LevelUpCommandBuilder withPowerLevels(
            @NotNull Collection<Integer> powerLevels) {
        this.powerLevels.addAll(powerLevels);
        return this;
    }

    public @NotNull LevelUpCommandBuilder command(@NotNull String command) {
        this.commands.add(command);
        return this;
    }

    public @NotNull LevelUpCommandBuilder commands(@NotNull Collection<String> commands) {
        this.commands.addAll(commands);
        return this;
    }

    public @NotNull LevelUpCommandBuilder runAs(@NotNull LevelUpCommand.RunAs runAs) {
        this.runAs = runAs;
        return this;
    }

    public @NotNull LevelUpCommand build() {
        return new LevelUpCommand(LevelUpCondition.of(skills, levels, powerLevels), commands,
                runAs);
    }
}
