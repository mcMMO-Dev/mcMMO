package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;
import com.gmail.nossr50.util.experience.ExperienceBarManager;

import java.util.function.Consumer;

public class ExperienceBarHideTask implements Consumer<Task> {
    private final BukkitMMOPlayer mcMMOPlayer;
    private final PrimarySkillType primarySkillType;
    private final ExperienceBarManager experienceBarManagerRef;

    public ExperienceBarHideTask(ExperienceBarManager experienceBarManagerRef, BukkitMMOPlayer mcMMOPlayer, PrimarySkillType primarySkillType) {
        this.experienceBarManagerRef = experienceBarManagerRef;
        this.mcMMOPlayer = mcMMOPlayer;
        this.primarySkillType = primarySkillType;
    }

    @Override
    public void accept(Task task) {
        if (experienceBarManagerRef == null || mcMMOPlayer == null)
            return;

        experienceBarManagerRef.hideExperienceBar(primarySkillType);
        experienceBarManagerRef.clearTask(primarySkillType);
    }
}
