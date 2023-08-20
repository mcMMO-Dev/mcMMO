package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

public class LevelUpCommandImpl implements LevelUpCommand {
    private final BiPredicate<PrimarySkillType, Integer> predicate;
    private final boolean logInfo;
    private final @NotNull LinkedList<String> commandStr;

    public LevelUpCommandImpl(@NotNull BiPredicate<PrimarySkillType, Integer> predicate, @NotNull String commandStr, boolean logInfo) {
        this.predicate = predicate;
        this.commandStr = new LinkedList<>();
        this.commandStr.add(commandStr);
        this.logInfo = logInfo;
    }

    public LevelUpCommandImpl(@NotNull BiPredicate<PrimarySkillType, Integer> predicate, @NotNull LinkedList<String> commandStr, boolean logInfo) {
        this.predicate = predicate;
        this.commandStr = commandStr;
        this.logInfo = logInfo;
    }

    @Override
    public void process(McMMOPlayer player, PrimarySkillType primarySkillType, Set<Integer> levelsGained) {
        for (int i : levelsGained) {
            if (predicate.test(primarySkillType, i)) {
                // execute command via server console in Bukkit
                if(logInfo) {
                    mcMMO.p.getLogger().info("Executing command: " + commandStr);
                } else {
                    LogUtils.debug(mcMMO.p.getLogger(), "Executing command: " + commandStr);
                }
                executeCommand();
            }
        }
    }

    public void executeCommand() {
        // TODO: Change this to debug later
        mcMMO.p.getLogger().info("Executing commands for level up: " + commandStr);
        for (String command : commandStr) {
            // TODO: Change this to debug later
            mcMMO.p.getLogger().info("Executing command: " + command);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LevelUpCommandImpl that = (LevelUpCommandImpl) o;
        return logInfo == that.logInfo && Objects.equals(predicate, that.predicate) && Objects.equals(commandStr, that.commandStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate, logInfo, commandStr);
    }

    @Override
    public String toString() {
        return "LevelUpCommandImpl{" +
                "predicate=" + predicate +
                ", logInfo=" + logInfo +
                ", commandStr='" + commandStr + '\'' +
                '}';
    }
}
