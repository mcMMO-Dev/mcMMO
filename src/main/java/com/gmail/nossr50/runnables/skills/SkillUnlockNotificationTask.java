package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.sounds.SkillUnlockSoundThrottle;


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
        // Batched unlock notifications only play the unlock sound for the first
        // notification of the batch; mass level changes queue dozens of these
        final boolean playSound = SkillUnlockSoundThrottle.tryPlaySound(
                mmoPlayer.getPlayer().getUniqueId());
        NotificationManager.sendPlayerUnlockNotification(mmoPlayer, subSkillType, playSound);
    }
}
