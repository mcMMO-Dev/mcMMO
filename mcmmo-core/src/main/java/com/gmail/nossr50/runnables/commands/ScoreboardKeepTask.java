package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardKeepTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private Player player;

    public ScoreboardKeepTask(mcMMO pluginRef, Player player) {
        this.pluginRef = pluginRef;
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isValid() && pluginRef.getScoreboardManager().isBoardShown(player.getName())) {
            pluginRef.getScoreboardManager().keepBoard(player.getName());
        }
    }
}
