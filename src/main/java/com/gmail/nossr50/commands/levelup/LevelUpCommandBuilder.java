package com.gmail.nossr50.commands.levelup;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public class LevelUpCommandBuilder {
    private LinkedList<String> commands = null;
    private List<BiPredicate<PrimarySkillType, Integer>> conditions = null;
    private Predicate<Integer> powerLevelCondition = null;
    private boolean logInfo;

    public LevelUpCommandBuilder() {
        this.logInfo = false;
    }

    public LevelUpCommandBuilder withPredicate(BiPredicate<PrimarySkillType, Integer> condition) {
        if (this.conditions == null) {
            this.conditions = new LinkedList<>();
        }

        conditions.add(condition);
        return this;
    }

    public LevelUpCommandBuilder withPowerLevelCondition(Predicate<Integer> powerLevelCondition) {
        if (this.powerLevelCondition != null) {
            throw new IllegalStateException("power level condition already set");
        }

        this.powerLevelCondition = powerLevelCondition;
        return this;
    }

    public LevelUpCommandBuilder withConditions(
            @NotNull Collection<BiPredicate<PrimarySkillType, Integer>> conditions) {
        if (this.conditions == null) {
            this.conditions = new LinkedList<>();
        } else {
            throw new IllegalStateException("conditions already set");
        }

        this.conditions.addAll(conditions);
        return this;
    }

    public LevelUpCommandBuilder withLogInfo(boolean logInfo) {
        this.logInfo = logInfo;
        return this;
    }

    public LevelUpCommandBuilder command(@NotNull String command) {
        this.commands = new LinkedList<>();
        this.commands.add(command);
        return this;
    }

    public LevelUpCommandBuilder commands(@NotNull Collection<String> command) {
        this.commands = new LinkedList<>(command);
        return this;
    }

    public LevelUpCommand build() {
        if (conditions == null && powerLevelCondition == null) {
            throw new IllegalStateException("no conditions found for level up command");
        }
        requireNonNull(commands, "no commands found for level up command");

        return new LevelUpCommand(conditions, powerLevelCondition, commands, logInfo);
    }
}
