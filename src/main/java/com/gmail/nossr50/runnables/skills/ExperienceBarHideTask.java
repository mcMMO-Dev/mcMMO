package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import org.bukkit.scheduler.BukkitRunnable;

public class ExperienceBarHideTask extends BukkitRunnable {
    public final McMMOPlayer mmoPlayer;
    public final PrimarySkillType primarySkillType;
    public final MMOExperienceBarManager MMOExperienceBarManagerRef;

    public ExperienceBarHideTask(MMOExperienceBarManager MMOExperienceBarManagerRef, McMMOPlayer mmoPlayer, PrimarySkillType primarySkillType)
    {
        this.MMOExperienceBarManagerRef = MMOExperienceBarManagerRef;
        this.mmoPlayer = mmoPlayer;
        this.primarySkillType = primarySkillType;
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

        MMOExperienceBarManagerRef.hideExperienceBar(primarySkillType);
        MMOExperienceBarManagerRef.clearTask(primarySkillType);
    }
}
