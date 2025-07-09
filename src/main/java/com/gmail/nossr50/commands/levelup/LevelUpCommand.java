package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class LevelUpCommand implements CommandsOnLevel {
    private final @Nullable List<BiPredicate<PrimarySkillType, Integer>> conditions;
    private final @Nullable Predicate<Integer> powerLevelCondition;
    private final boolean logInfo;
    private final @NotNull LinkedList<String> commands;

    public LevelUpCommand(@Nullable List<BiPredicate<PrimarySkillType, Integer>> conditions,
            @Nullable Predicate<Integer> powerLevelCondition,
            @NotNull LinkedList<String> commands, boolean logInfo) {
        this.conditions = conditions;
        this.powerLevelCondition = powerLevelCondition;
        if (conditions == null && powerLevelCondition == null) {
            throw new IllegalArgumentException("At least one condition must be set");
        }
        this.commands = commands;
        this.logInfo = logInfo;
    }

    public void process(@NotNull McMMOPlayer player, @NotNull PrimarySkillType primarySkillType,
            @NotNull Set<Integer> levelsGained,
            @NotNull Set<Integer> powerLevelsGained) {
        // each predicate has to pass at least once
        // we check the predicates against all levels gained to see if they pass at least once
        // if all predicates pass at least once, we execute the command
        boolean allConditionsPass = (conditions == null) || conditions.stream().allMatch(
                predicate -> levelsGained.stream()
                        .anyMatch(level -> predicate.test(primarySkillType, level)));
        // we also check the power level predicate to see if it passes at least once, if this predicate is null, we mark it as passed
        boolean powerLevelConditionPass =
                (powerLevelCondition == null) || powerLevelsGained.stream()
                        .anyMatch(powerLevelCondition);
        if (allConditionsPass && powerLevelConditionPass) {
            executeCommand(player);
        }
    }

    @VisibleForTesting
    void executeCommand(@NotNull McMMOPlayer player) {
        LogUtils.debug(mcMMO.p.getLogger(), "Executing level up commands: " + commands);
        for (String command : commands) {
            LogUtils.debug(mcMMO.p.getLogger(), "Executing command: " + command);
            String injectedCommand = injectedCommand(command, player);
            if (!injectedCommand.equalsIgnoreCase(command)) {
                LogUtils.debug(mcMMO.p.getLogger(),
                        ("Command has been injected with new values: " + injectedCommand));
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), injectedCommand);
        }
    }

    @VisibleForTesting
    String injectedCommand(String command, McMMOPlayer player) {
        // TODO: unit tests
        StringBuilder commandBuilder = new StringBuilder(command);

        // Replace %player% with player name
        replaceAll(commandBuilder, "{@player}", player.getPlayer().getName());

        // Replace each skill level
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (primarySkillType == PrimarySkillType.SMELTING
                    || primarySkillType == PrimarySkillType.SALVAGE) {
                continue;
            }
            replaceAll(commandBuilder, "{@" + primarySkillType.name().toLowerCase() + "_level}",
                    String.valueOf(player.getSkillLevel(primarySkillType)));
        }

        // Replace power level
        replaceAll(commandBuilder, "{@power_level}", String.valueOf(player.getPowerLevel()));

        return commandBuilder.toString();
    }

    private void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index = builder.indexOf(from, index + to.length());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LevelUpCommand that = (LevelUpCommand) o;
        return logInfo == that.logInfo && Objects.equals(conditions, that.conditions)
                && Objects.equals(commands, that.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditions, logInfo, commands);
    }

    @Override
    public String toString() {
        return "SkillLevelUpCommand{" +
                "conditions=" + conditions +
                ", logInfo=" + logInfo +
                ", commands=" + commands +
                '}';
    }
}
