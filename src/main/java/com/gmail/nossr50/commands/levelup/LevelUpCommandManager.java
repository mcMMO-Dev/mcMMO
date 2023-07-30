package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Manages commands to be executed on level up
 */
public class LevelUpCommandManager {
    private final @NotNull Set<LevelUpCommand> commands;
    private final @NotNull mcMMO plugin;

    public LevelUpCommandManager(@NotNull mcMMO plugin) {
        this.plugin = plugin;
        this.commands = new HashSet<>();
    }

    /**
     * Register a level up command to be executed on level up
     *
     * @param levelUpCommand the levelUpCommand
     */
    public void registerCommand(@NotNull LevelUpCommand levelUpCommand) {
        commands.add(levelUpCommand);
        mcMMO.p.getLogger().info("Registered levelUpCommand on level up: " + levelUpCommand);
    }

    /**
     * Apply the level up commands to the player
     *
     * @param mmoPlayer         the player
     * @param primarySkillType  the skill type
     * @param levelsGained      the levels gained
     */
    public void apply(@NotNull McMMOPlayer mmoPlayer, @NotNull PrimarySkillType primarySkillType, Set<Integer> levelsGained) {
        if (!mmoPlayer.getPlayer().isOnline()) {
            return;
        }

        for (LevelUpCommand command : commands) {
            command.process(mmoPlayer, primarySkillType, levelsGained);
        }
    }

    /**
     * Clear all registered commands
     */
    public void clear() {
        mcMMO.p.getLogger().info("Clearing registered commands on level up");
        commands.clear();
    }

    /**
     * @return true if there are no registered commands
     */
    public boolean isEmpty() {
        return commands.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LevelUpCommandManager that = (LevelUpCommandManager) o;
        return Objects.equals(commands, that.commands) && Objects.equals(plugin, that.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commands, plugin);
    }

    @Override
    public String toString() {
        return "LevelUpCommandManager{" +
                "commands=" + commands +
                ", plugin=" + plugin +
                '}';
    }
}
