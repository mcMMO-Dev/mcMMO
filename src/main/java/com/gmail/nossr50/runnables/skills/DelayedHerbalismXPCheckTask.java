package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.BlockSnapshot;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class DelayedHerbalismXPCheckTask extends BukkitRunnable {

    private final OnlineMMOPlayer mmoPlayer;
    private final ArrayList<BlockSnapshot> chorusBlocks;

    public DelayedHerbalismXPCheckTask(OnlineMMOPlayer mmoPlayer, ArrayList<BlockSnapshot> chorusBlocks) {
        this.mmoPlayer = mmoPlayer;
        this.chorusBlocks = chorusBlocks;
    }

    @Override
    public void run() {
        mmoPlayer.getHerbalismManager().awardXPForBlockSnapshots(chorusBlocks);
    }
}
