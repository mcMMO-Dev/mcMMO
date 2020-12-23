package com.gmail.nossr50.runnables.skills;

import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.neetgames.mcmmo.skill.RootSkill;
import org.bukkit.scheduler.BukkitRunnable;

public class ExperienceBarHideTask extends BukkitRunnable {
    public final OnlineMMOPlayer mmoPlayer;
    public final RootSkill rootSkill;
    public final MMOExperienceBarManager MMOExperienceBarManagerRef;

    public ExperienceBarHideTask(MMOExperienceBarManager MMOExperienceBarManagerRef, OnlineMMOPlayer mmoPlayer, RootSkill rootSkill) {
        this.MMOExperienceBarManagerRef = MMOExperienceBarManagerRef;
        this.mmoPlayer = mmoPlayer;
        this.rootSkill = rootSkill;
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
        if(MMOExperienceBarManagerRef == null || mmoPlayer == null)
            return;

        MMOExperienceBarManagerRef.hideExperienceBar(rootSkill);
        MMOExperienceBarManagerRef.clearTask(rootSkill);
    }
}
