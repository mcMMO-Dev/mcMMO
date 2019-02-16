package com.gmail.nossr50.core.util.experience;

import com.gmail.nossr50.core.config.experience.ExperienceConfig;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.runnables.skills.ExperienceBarHideTask;
import com.gmail.nossr50.core.skills.PrimarySkillType;

import java.util.HashMap;

/**
 * ExperienceBarManager handles displaying and updating mcMMO experience bars for players
 * Each ExperienceBarManager only manages a single player
 */
public class ExperienceBarManager {
    HashMap<PrimarySkillType, ExperienceBarWrapper> experienceBars;
    HashMap<PrimarySkillType, ExperienceBarHideTask> experienceBarHideTaskHashMap;
    private McMMOPlayer mcMMOPlayer;

    public ExperienceBarManager(McMMOPlayer mcMMOPlayer) {
        //Init map
        experienceBars = new HashMap<>();
        experienceBarHideTaskHashMap = new HashMap<>();

        this.mcMMOPlayer = mcMMOPlayer;
    }

    public void updateExperienceBar(PrimarySkillType primarySkillType) {
        if (!ExperienceConfig.getInstance().isExperienceBarsEnabled() || !ExperienceConfig.getInstance().isExperienceBarEnabled(primarySkillType))
            return;

        //Init Bar
        if (experienceBars.get(primarySkillType) == null)
            experienceBars.put(primarySkillType, new ExperienceBarWrapper(primarySkillType, mcMMOPlayer));

        //Get Bar
        ExperienceBarWrapper experienceBarWrapper = experienceBars.get(primarySkillType);

        //Update Progress
        experienceBarWrapper.setProgress(mcMMOPlayer.getProgressInCurrentSkillLevel(primarySkillType));

        //Show Bar
        experienceBarWrapper.showExperienceBar();

        //Setup Hide Bar Task
        if (experienceBarHideTaskHashMap.get(primarySkillType) != null) {
            experienceBarHideTaskHashMap.get(primarySkillType).cancel();
            scheduleHideTask(primarySkillType);
        } else {
            scheduleHideTask(primarySkillType);
        }
    }

    private void scheduleHideTask(PrimarySkillType primarySkillType) {
        ExperienceBarHideTask experienceBarHideTask = new ExperienceBarHideTask(this, mcMMOPlayer, primarySkillType);
        experienceBarHideTask.runTaskLater(20 * 2);
        experienceBarHideTaskHashMap.put(primarySkillType, experienceBarHideTask);
    }

    public void hideExperienceBar(PrimarySkillType primarySkillType) {
        experienceBars.get(primarySkillType).hideExperienceBar();
    }

    public void clearTask(PrimarySkillType primarySkillType) {
        experienceBarHideTaskHashMap.remove(primarySkillType);
    }
}
