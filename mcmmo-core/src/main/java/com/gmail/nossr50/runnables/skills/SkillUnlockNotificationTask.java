package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitRunnable;


public class SkillUnlockNotificationTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private final BukkitMMOPlayer mcMMOPlayer;
    private final SubSkillType subSkillType;

    /**
     * Notify a player about a newly unlocked subskill
     *
     * @param mcMMOPlayer  target player
     * @param subSkillType the subskill that they just unlocked
     */
    public SkillUnlockNotificationTask(mcMMO pluginRef, BukkitMMOPlayer mcMMOPlayer, SubSkillType subSkillType) {
        this.pluginRef = pluginRef;
        this.mcMMOPlayer = mcMMOPlayer;
        this.subSkillType = subSkillType;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        //mcMMOPlayer.getPlayer().sendTitle(subSkillType.getLocaleName(), "Rank "+rank, 7, 20, 7);
        pluginRef.getNotificationManager().sendPlayerUnlockNotification(mcMMOPlayer, subSkillType);
    }
}
