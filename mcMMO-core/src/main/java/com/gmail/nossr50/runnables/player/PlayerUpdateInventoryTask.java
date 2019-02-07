package com.gmail.nossr50.runnables.player;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("deprecation")
public class PlayerUpdateInventoryTask extends BukkitRunnable {
    private Player player;

    public PlayerUpdateInventoryTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        player.updateInventory();
    }
}
