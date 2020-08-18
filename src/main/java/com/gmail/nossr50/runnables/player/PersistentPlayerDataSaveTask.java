package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.MMODataSnapshot;
import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitRunnable;

public class PersistentPlayerDataSaveTask extends BukkitRunnable {
    private final MMODataSnapshot dataSnapshot;

    public PersistentPlayerDataSaveTask(MMODataSnapshot dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
    }

    @Override
    public void run() {
        mcMMO.getDatabaseManager().saveUser(dataSnapshot);
    }
}
