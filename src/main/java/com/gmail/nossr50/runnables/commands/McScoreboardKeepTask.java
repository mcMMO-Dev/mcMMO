package com.gmail.nossr50.runnables.commands;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

public class McScoreboardKeepTask extends BukkitRunnable {
    private Player player;

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
