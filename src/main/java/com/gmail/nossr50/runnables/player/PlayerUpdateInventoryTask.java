package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.util.CancellableRunnable;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class PlayerUpdateInventoryTask extends CancellableRunnable {
    private final Player player;

    public PlayerUpdateInventoryTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        player.updateInventory();
    }
}
