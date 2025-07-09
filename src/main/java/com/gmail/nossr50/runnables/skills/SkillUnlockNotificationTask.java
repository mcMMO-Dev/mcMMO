package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.player.NotificationManager;


public class SkillUnlockNotificationTask extends CancellableRunnable {
    private final McMMOPlayer mmoPlayer;
    private final SubSkillType subSkillType;
    private final int rank;

    /**
     * Notify a player about a newly unlocked subskill
     *
     * @param mmoPlayer target player
     * @param subSkillType the subskill that they just unlocked
     * @param rank the rank of the subskill
     */
    public SkillUnlockNotificationTask(McMMOPlayer mmoPlayer, SubSkillType subSkillType, int rank) {
        this.mmoPlayer = mmoPlayer;
        this.subSkillType = subSkillType;
        this.rank = rank;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread,
     * starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action
     * whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        //mmoPlayer.getPlayer().sendTitle(subSkillType.getLocaleName(), "Rank "+rank, 7, 20, 7);
        NotificationManager.sendPlayerUnlockNotification(mmoPlayer, subSkillType);
    }
}
