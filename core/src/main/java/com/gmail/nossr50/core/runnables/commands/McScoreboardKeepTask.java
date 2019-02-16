package com.gmail.nossr50.core.runnables.commands;

import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.util.scoreboards.ScoreboardManager;

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
