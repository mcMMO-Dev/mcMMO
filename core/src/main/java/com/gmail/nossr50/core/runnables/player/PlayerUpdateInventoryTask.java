package com.gmail.nossr50.core.runnables.player;


import com.gmail.nossr50.core.mcmmo.entity.Player;

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
