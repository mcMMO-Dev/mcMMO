package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ScoreboardKeepTask implements Consumer<Task> {
    private final mcMMO pluginRef;
    private Player player;

    public ScoreboardKeepTask(mcMMO pluginRef, Player player) {
        this.pluginRef = pluginRef;
        this.player = player;
    }

    @Override
    public void accept(Task task) {
        if (player.isValid() && pluginRef.getScoreboardManager().isBoardShown(player.getName())) {
            pluginRef.getScoreboardManager().keepBoard(player.getName());
        }
    }
}
