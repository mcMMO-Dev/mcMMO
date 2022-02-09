package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class McScoreboardKeepTask extends BukkitRunnable {
    private final Player player;

    public McScoreboardKeepTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isValid() && ScoreboardManager.isBoardShown(player.getName())) {
            ScoreboardManager.keepBoard(player.getName());
        }
    }
}
