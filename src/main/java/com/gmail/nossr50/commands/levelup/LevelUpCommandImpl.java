package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class LevelUpCommandImpl implements LevelUpCommand {
    private final @NotNull Predicate<Integer> shouldApply;
    private final boolean logInfo;
    private final @NotNull String commandStr;

    private final @NotNull Set<PrimarySkillType> skills;

    public LevelUpCommandImpl(@NotNull Predicate<Integer> shouldApply, @NotNull String commandStr, @NotNull Set<PrimarySkillType> skills, boolean logInfo) {
        this.shouldApply = shouldApply;
        this.commandStr = commandStr;
        this.skills = skills;
        this.logInfo = logInfo;
    }

    @Override
    public void process(McMMOPlayer player, PrimarySkillType primarySkillType, Set<Integer> levelsGained) {
        if(!skills.contains(primarySkillType)) {
            return;
        }

        for (int i : levelsGained) {
            if (shouldApply.test(i)) {
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
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LevelUpCommandImpl that = (LevelUpCommandImpl) o;
        return logInfo == that.logInfo && Objects.equals(shouldApply, that.shouldApply) && Objects.equals(commandStr, that.commandStr) && Objects.equals(skills, that.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shouldApply, logInfo, commandStr, skills);
    }

    @Override
    public String toString() {
        return "LevelUpCommandImpl{" +
                "shouldApply=" + shouldApply +
                ", logInfo=" + logInfo +
                ", commandStr='" + commandStr + '\'' +
                ", skills=" + skills +
                '}';
    }
}
