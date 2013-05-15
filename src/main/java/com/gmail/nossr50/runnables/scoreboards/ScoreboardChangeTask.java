package com.gmail.nossr50.runnables.scoreboards;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

public class ScoreboardChangeTask extends BukkitRunnable {
    private Player player;
    private Scoreboard oldScoreboard;

    public ScoreboardChangeTask(Player player, Scoreboard oldScoreboard) {
        this.player = player;
        this.oldScoreboard = oldScoreboard;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            player.setScoreboard(oldScoreboard);
            ScoreboardManager.enablePowerLevelDisplay(player);
        }

        ScoreboardManager.clearPendingTask(player.getName());
    }
}
