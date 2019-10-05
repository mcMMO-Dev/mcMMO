package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.BlockSnapshot;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class DelayedHerbalismXPCheckTask extends BukkitRunnable {

    private final BukkitMMOPlayer mcMMOPlayer;
    private final ArrayList<BlockSnapshot> chorusBlocks;

    public DelayedHerbalismXPCheckTask(BukkitMMOPlayer mcMMOPlayer, ArrayList<BlockSnapshot> chorusBlocks) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.chorusBlocks = chorusBlocks;
    }

    @Override
    public void run() {
        mcMMOPlayer.getHerbalismManager().awardXPForBlockSnapshots(chorusBlocks);
    }
}
