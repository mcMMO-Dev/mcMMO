package com.gmail.nossr50.commands.levelup;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class SimpleLevelUpCommand implements CommandsOnLevel {
    private final @NotNull BiPredicate<PrimarySkillType, Integer> condition;
    private final @NotNull LinkedList<String> commands;
    private final boolean logInfo;

    public SimpleLevelUpCommand(@Nullable BiPredicate<PrimarySkillType, Integer> condition,
            @NotNull LinkedList<String> commands, boolean logInfo) {
        this.condition = requireNonNull(condition, "condition must not be null");
        this.commands = requireNonNull(commands);
        if (commands.isEmpty()) {
            throw new RuntimeException("expected commands to be a non-empty list.");
        }
        this.logInfo = logInfo;
    }

    public void process(@NotNull McMMOPlayer player, @NotNull PrimarySkillType primarySkillType,
            @NotNull Set<Integer> levelsGained, @NotNull Set<Integer> powerLevelsGained) {
        boolean conditionMet = levelsGained.stream().anyMatch(
                level -> condition.test(primarySkillType, level));
        if (conditionMet) {
            executeCommand(player);
        }
    }

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
    static String injectedCommand(String command, McMMOPlayer player) {
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

    static private void replaceAll(StringBuilder builder, String from, String to) {
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
        SimpleLevelUpCommand that = (SimpleLevelUpCommand) o;
        return logInfo == that.logInfo && Objects.equals(condition, that.condition)
                && Objects.equals(commands, that.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, logInfo, commands);
    }

    @Override
    public String toString() {
        return "SkillLevelUpCommand{" +
                "condition=" + condition +
                ", logInfo=" + logInfo +
                ", commands=" + commands +
                '}';
    }
}
