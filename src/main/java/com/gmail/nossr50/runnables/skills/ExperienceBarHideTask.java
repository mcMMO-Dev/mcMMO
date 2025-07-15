package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.experience.ExperienceBarManager;

public class ExperienceBarHideTask extends CancellableRunnable {
    public final McMMOPlayer mmoPlayer;
    public final PrimarySkillType primarySkillType;
    public final ExperienceBarManager experienceBarManagerRef;

    public ExperienceBarHideTask(ExperienceBarManager experienceBarManagerRef,
            McMMOPlayer mmoPlayer, PrimarySkillType primarySkillType) {
        this.experienceBarManagerRef = experienceBarManagerRef;
        this.mmoPlayer = mmoPlayer;
        this.primarySkillType = primarySkillType;
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
        if (experienceBarManagerRef == null || mmoPlayer == null) {
            return;
        }

        experienceBarManagerRef.hideExperienceBar(primarySkillType);
        experienceBarManagerRef.clearTask(primarySkillType);
    }
}
