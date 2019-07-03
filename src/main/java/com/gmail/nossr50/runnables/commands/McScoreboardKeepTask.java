package com.gmail.nossr50.runnables.commands;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class McScoreboardKeepTask extends BukkitRunnable {
    private Player player;

    public McScoreboardKeepTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isValid() && pluginRef.getScoreboardManager().isBoardShown(player.getName())) {
            pluginRef.getScoreboardManager().keepBoard(player.getName());
        }
    }
}
