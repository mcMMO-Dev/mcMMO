package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

public class LevelUpCommandImpl implements LevelUpCommand {
    private final BiPredicate<PrimarySkillType, Integer> predicate;
    private final boolean logInfo;
    private final @NotNull LinkedList<String> commands;

    public LevelUpCommandImpl(@NotNull BiPredicate<PrimarySkillType, Integer> predicate, @NotNull String command, boolean logInfo) {
        this.predicate = predicate;
        this.commands = new LinkedList<>();
        this.commands.add(command);
        this.logInfo = logInfo;
    }

    public LevelUpCommandImpl(@NotNull BiPredicate<PrimarySkillType, Integer> predicate, @NotNull LinkedList<String> commands, boolean logInfo) {
        this.predicate = predicate;
        this.commands = commands;
        this.logInfo = logInfo;
    }

    @Override
    public void process(McMMOPlayer player, PrimarySkillType primarySkillType, Set<Integer> levelsGained) {
        for (int i : levelsGained) {
            if (predicate.test(primarySkillType, i)) {
                // execute command via server console in Bukkit
                if(logInfo) {
                    mcMMO.p.getLogger().info("Executing command: " + commands);
                } else {
                    LogUtils.debug(mcMMO.p.getLogger(), "Executing command: " + commands);
                }
                executeCommand(player, primarySkillType, i);
            }
        }
    }

    public void executeCommand(McMMOPlayer player, PrimarySkillType primarySkillType, int level) {
        // TODO: Change this to debug later
        mcMMO.p.getLogger().info("Executing commands for level up: " + commands);
        for (String command : commands) {
            // TODO: Change this to debug later
            mcMMO.p.getLogger().info("Executing command: " + command);
            String injectedCommand = injectedCommand(command, player, primarySkillType, level);
            // TODO: Remove verbose logging later
            if (!injectedCommand.equalsIgnoreCase(command)) {
                mcMMO.p.getLogger().info(("Command has been injected with new values: " + injectedCommand));
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), injectedCommand);
        }
    }

    private String injectedCommand(String command, McMMOPlayer player, PrimarySkillType primarySkillType, int level) {
        // replace %player% with player name, %skill% with skill name, and %level% with level
        command = safeReplace(command, "%player%", player.getPlayer().getName());
        command = safeReplace(command, "%skill%", primarySkillType.getName());
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
        LevelUpCommandImpl that = (LevelUpCommandImpl) o;
        return logInfo == that.logInfo && Objects.equals(predicate, that.predicate) && Objects.equals(commands, that.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate, logInfo, commands);
    }

    @Override
    public String toString() {
        return "LevelUpCommandImpl{" +
                "predicate=" + predicate +
                ", logInfo=" + logInfo +
                ", commandStr='" + commands + '\'' +
                '}';
    }
}
