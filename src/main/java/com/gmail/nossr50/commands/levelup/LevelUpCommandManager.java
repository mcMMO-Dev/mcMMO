package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class LevelUpCommandManager {
    private final @NotNull Set<LevelUpCommand> commands;
    private final @NotNull mcMMO plugin;

    public LevelUpCommandManager(@NotNull mcMMO plugin) {
        this.plugin = plugin;
        this.commands = new HashSet<>();
    }

    public void registerCommand(@NotNull LevelUpCommand command) {
        commands.add(command);
        mcMMO.p.getLogger().info("Registered command on level up: " + command);
    }

    public void apply(@NotNull McMMOPlayer mmoPlayer, @NotNull PrimarySkillType primarySkillType, Set<Integer> levelsGained) {
        if (!mmoPlayer.getPlayer().isOnline()) {
            return;
        }

        for (LevelUpCommand command : commands) {
            command.apply(mmoPlayer, primarySkillType, levelsGained);
        }
    }

    public void clear() {
        mcMMO.p.getLogger().info("Clearing registered commands on level up");
        commands.clear();
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }
}
