package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.mcMMO;
import org.bukkit.block.Furnace;
import org.bukkit.scheduler.BukkitRunnable;

public class FurnaceCleanupTask extends BukkitRunnable {

    private final Furnace furnace;

    public FurnaceCleanupTask(Furnace furnace) {
        this.furnace = furnace;
    }

    @Override
    public void run() {
        if(furnace != null && furnace.getInventory().getResult() == null) {
            //Furnace is empty so stop tracking it
            mcMMO.getSmeltingTracker().untrackFurnace(furnace);
        }
    }
}
