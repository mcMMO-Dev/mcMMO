package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class PlayerUpdateInventoryTask implements Consumer<Task> {
    private Player player;

    public PlayerUpdateInventoryTask(Player player) {
        this.player = player;
    }

    @Override
    public void accept(Task task) {
        player.updateInventory();
    }
}
