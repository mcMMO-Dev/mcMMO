package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class PowerLevelUpCommand implements CommandsOnLevel {
    private final Predicate<Integer> predicate;
    private final boolean logInfo;
    private final @NotNull LinkedList<String> commands;

    public PowerLevelUpCommand(@NotNull Predicate<Integer> predicate, @NotNull String command, boolean logInfo) {
        this.predicate = predicate;
        this.commands = new LinkedList<>();
        this.commands.add(command);
        this.logInfo = logInfo;
    }

    public PowerLevelUpCommand(@NotNull Predicate<Integer> predicate, @NotNull LinkedList<String> commands, boolean logInfo) {
        this.predicate = predicate;
        this.commands = commands;
        this.logInfo = logInfo;
    }

    public void process(McMMOPlayer player, Set<Integer> levelsGained) {
        for (int i : levelsGained) {
            if (predicate.test(i)) {
                // execute command via server console in Bukkit
                if(logInfo) {
                    mcMMO.p.getLogger().info("Executing command: " + commands);
                } else {
                    LogUtils.debug(mcMMO.p.getLogger(), "Executing command: " + commands);
                }
                executeCommand(player, i);
            }
        }
    }

    public void executeCommand(McMMOPlayer player,  int level) {
        LogUtils.debug(mcMMO.p.getLogger(), "Executing commands for level up: " + commands);
        for (String command : commands) {
            LogUtils.debug(mcMMO.p.getLogger(), "Executing command: " + command);
            String injectedCommand = injectedCommand(command, player, level);
            if (!injectedCommand.equalsIgnoreCase(command)) {
                LogUtils.debug(mcMMO.p.getLogger(), ("Command has been injected with new values: " + injectedCommand));
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), injectedCommand);
        }
    }

    private String injectedCommand(String command, McMMOPlayer player, int level) {
        // replace %player% with player name, %skill% with skill name, and %level% with level
        command = safeReplace(command, "%player%", player.getPlayer().getName());
        command = safeReplace(command, "%power_level%", "power level");
        command = safeReplace(command, "%skill%", "power level");
        command = safeReplace(command, "%level%", String.valueOf(level));
        return command;
    }

    private String safeReplace(String targetStr, String toReplace, String replacement) {
        if (replacement == null) {
            return targetStr;
        }

        return targetStr.replace(toReplace, replacement);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PowerLevelUpCommand that = (PowerLevelUpCommand) o;
        return logInfo == that.logInfo && Objects.equals(predicate, that.predicate) && Objects.equals(commands, that.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate, logInfo, commands);
    }

    @Override
    public String toString() {
        return "PowerLevelUpCommand{" +
                "predicate=" + predicate +
                ", logInfo=" + logInfo +
                ", commands=" + commands +
                '}';
    }
}
