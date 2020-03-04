package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.BlockSnapshot;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DelayedHerbalismXPCheckTask implements Consumer<Task> {

    private final BukkitMMOPlayer mcMMOPlayer;
    private final ArrayList<BlockSnapshot> chorusBlocks;

    public DelayedHerbalismXPCheckTask(BukkitMMOPlayer mcMMOPlayer, ArrayList<BlockSnapshot> chorusBlocks) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.chorusBlocks = chorusBlocks;
    }

    @Override
    public void accept(Task task) {
        mcMMOPlayer.getHerbalismManager().awardXPForBlockSnapshots(chorusBlocks);
    }
}
