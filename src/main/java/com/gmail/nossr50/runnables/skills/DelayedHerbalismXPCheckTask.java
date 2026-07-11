package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.BlockSnapshot;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.CancellableRunnable;
import java.util.ArrayList;

/**
 * @deprecated XP for multi-block plants, chorus trees included, is verified and awarded by
 *         {@link PlantCollapseXpTask}
 */
@Deprecated(forRemoval = true, since = "2.3.000")
public class DelayedHerbalismXPCheckTask extends CancellableRunnable {

    private final McMMOPlayer mmoPlayer;
    private final ArrayList<BlockSnapshot> chorusBlocks;

    public DelayedHerbalismXPCheckTask(McMMOPlayer mmoPlayer,
            ArrayList<BlockSnapshot> chorusBlocks) {
        this.mmoPlayer = mmoPlayer;
        this.chorusBlocks = chorusBlocks;
    }

    @Override
    public void run() {
        mmoPlayer.getHerbalismManager().awardXPForBlockSnapshots(chorusBlocks);
    }
}
