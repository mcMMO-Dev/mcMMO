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
    private final @NotNull Set<SkillLevelUpCommand> skillLevelCommands;
    private final @NotNull Set<PowerLevelUpCommand> powerLevelUpCommands;
    private final @NotNull mcMMO plugin;

    public LevelUpCommandManager(@NotNull mcMMO plugin) {
        this.plugin = requireNonNull(plugin, "plugin cannot be null");
        this.skillLevelCommands = new HashSet<>();
        this.powerLevelUpCommands = new HashSet<>();
    }

    public void registerCommand(@NotNull SkillLevelUpCommand skillLevelUpCommand) {
        requireNonNull(skillLevelUpCommand, "skillLevelUpCommand cannot be null");
        skillLevelCommands.add(skillLevelUpCommand);
        LogUtils.debug(mcMMO.p.getLogger(), "Registered level up command - SkillLevelUpCommand: " + skillLevelUpCommand);
    }

    public void registerCommand(@NotNull PowerLevelUpCommand powerLevelUpCommand) {
        requireNonNull(powerLevelUpCommand, "powerLevelUpCommand cannot be null");
        powerLevelUpCommands.add(powerLevelUpCommand);
        LogUtils.debug(mcMMO.p.getLogger(), "Registered level up command - PowerLevelUpCommand: " + powerLevelUpCommand);
    }

    /**
     * Apply the level up commands to the player
     *
     * @param mmoPlayer         the player
     * @param primarySkillType  the skill type
     * @param levelsGained      the levels gained
     */
    public void applySkillLevelUp(@NotNull McMMOPlayer mmoPlayer, @NotNull PrimarySkillType primarySkillType, Set<Integer> levelsGained) {
        if (!mmoPlayer.getPlayer().isOnline()) {
            return;
        }

        for (SkillLevelUpCommand command : skillLevelCommands) {
            command.process(mmoPlayer, primarySkillType, levelsGained);
        }
    }

    public void applyPowerLevelUp(@NotNull McMMOPlayer mmoPlayer, Set<Integer> levelsGained) {
        if (!mmoPlayer.getPlayer().isOnline()) {
            return;
        }

        for (PowerLevelUpCommand command : powerLevelUpCommands) {
            command.process(mmoPlayer, levelsGained);
        }
    }

    public @NotNull Set<SkillLevelUpCommand> getSkillLevelCommands() {
        return skillLevelCommands;
    }

    public @NotNull Set<PowerLevelUpCommand> getPowerLevelUpCommands() {
        return powerLevelUpCommands;
    }

    /**
     * Clear all registered commands
     */
    public void clear() {
        mcMMO.p.getLogger().info("Clearing registered commands on level up");
        skillLevelCommands.clear();
        powerLevelUpCommands.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LevelUpCommandManager that = (LevelUpCommandManager) o;
        return Objects.equals(skillLevelCommands, that.skillLevelCommands) && Objects.equals(powerLevelUpCommands, that.powerLevelUpCommands) && Objects.equals(plugin, that.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillLevelCommands, powerLevelUpCommands, plugin);
    }

    @Override
    public String toString() {
        return "LevelUpCommandManager{" +
                "skillLevelCommands=" + skillLevelCommands +
                ", powerLevelUpCommands=" + powerLevelUpCommands +
                ", plugin=" + plugin +
                '}';
    }
}
