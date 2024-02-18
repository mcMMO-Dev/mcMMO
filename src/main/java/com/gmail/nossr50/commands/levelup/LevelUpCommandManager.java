package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Manages commands to be executed on level up
 */
public class LevelUpCommandManager {
    private final @NotNull Set<LevelUpCommand> levelUpCommands;
    private final @NotNull mcMMO plugin;

    public LevelUpCommandManager(@NotNull mcMMO plugin) {
        this.plugin = requireNonNull(plugin, "plugin cannot be null");
        this.levelUpCommands = new HashSet<>();
    }

    public void registerCommand(@NotNull LevelUpCommand levelUpCommand) {
        requireNonNull(levelUpCommand, "skillLevelUpCommand cannot be null");
        levelUpCommands.add(levelUpCommand);
        LogUtils.debug(mcMMO.p.getLogger(), "Registered level up command - SkillLevelUpCommand: " + levelUpCommand);
    }

    /**
     * Apply the level up commands to the player
     *
     * @param mmoPlayer         the player
     * @param primarySkillType  the skill type
     * @param levelsGained      the levels gained
     */
    public void applySkillLevelUp(@NotNull McMMOPlayer mmoPlayer, @NotNull PrimarySkillType primarySkillType,
                                  Set<Integer> levelsGained, Set<Integer> powerLevelsGained) {
        if (!mmoPlayer.getPlayer().isOnline()) {
            return;
        }

        for (LevelUpCommand command : levelUpCommands) {
            command.process(mmoPlayer, primarySkillType, levelsGained, powerLevelsGained);
        }
    }

    public @NotNull Set<LevelUpCommand> getLevelUpCommands() {
        return levelUpCommands;
    }

    /**
     * Clear all registered commands
     */
    public void clear() {
        mcMMO.p.getLogger().info("Clearing registered commands on level up");
        levelUpCommands.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LevelUpCommandManager that = (LevelUpCommandManager) o;
        return Objects.equals(levelUpCommands, that.levelUpCommands) && Objects.equals(plugin, that.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(levelUpCommands, plugin);
    }

    @Override
    public String toString() {
        return "LevelUpCommandManager{" +
                "levelUpCommands=" + levelUpCommands +
                ", plugin=" + plugin +
                '}';
    }
}
