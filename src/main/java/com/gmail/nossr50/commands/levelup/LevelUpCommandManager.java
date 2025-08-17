package com.gmail.nossr50.commands.levelup;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * Manages commands to be executed on level up
 */
public class LevelUpCommandManager {
    private final @NotNull Set<SimpleLevelUpCommand> simpleLevelUpCommands;
    private final @NotNull mcMMO plugin;

    public LevelUpCommandManager(@NotNull mcMMO plugin) {
        this.plugin = requireNonNull(plugin, "plugin cannot be null");
        this.simpleLevelUpCommands = new HashSet<>();
    }

    public void registerCommand(@NotNull SimpleLevelUpCommand simpleLevelUpCommand) {
        requireNonNull(simpleLevelUpCommand, "skillLevelUpCommand cannot be null");
        simpleLevelUpCommands.add(simpleLevelUpCommand);
        LogUtils.debug(mcMMO.p.getLogger(),
                "Registered level up command - SkillLevelUpCommand: " + simpleLevelUpCommand);
    }

    /**
     * Apply the level up commands to the player
     *
     * @param mmoPlayer the player
     * @param primarySkillType the skill type
     * @param levelsGained the levels gained
     */
    public void applySkillLevelUp(@NotNull McMMOPlayer mmoPlayer,
            @NotNull PrimarySkillType primarySkillType,
            Set<Integer> levelsGained, Set<Integer> powerLevelsGained) {
        if (!mmoPlayer.getPlayer().isOnline()) {
            return;
        }

        for (SimpleLevelUpCommand command : simpleLevelUpCommands) {
            command.process(mmoPlayer, primarySkillType, levelsGained, powerLevelsGained);
        }
    }

    public @NotNull Set<SimpleLevelUpCommand> getLevelUpCommands() {
        return simpleLevelUpCommands;
    }

    /**
     * Clear all registered commands
     */
    public void clear() {
        mcMMO.p.getLogger().info("Clearing registered commands on level up");
        simpleLevelUpCommands.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LevelUpCommandManager that = (LevelUpCommandManager) o;
        return Objects.equals(simpleLevelUpCommands, that.simpleLevelUpCommands) && Objects.equals(plugin,
                that.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(simpleLevelUpCommands, plugin);
    }

    @Override
    public String toString() {
        return "LevelUpCommandManager{" +
                "levelUpCommands=" + simpleLevelUpCommands +
                ", plugin=" + plugin +
                '}';
    }
}
