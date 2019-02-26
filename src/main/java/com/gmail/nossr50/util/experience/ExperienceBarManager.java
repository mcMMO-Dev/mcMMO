package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.runnables.skills.ExperienceBarHideTask;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * ExperienceBarManager handles displaying and updating mcMMO experience bars for players
 * Each ExperienceBarManager only manages a single player
 */
public class ExperienceBarManager {
    private McMMOPlayer mcMMOPlayer;

    HashMap<PrimarySkillType, ExperienceBarWrapper> experienceBars;
    HashMap<PrimarySkillType, ExperienceBarHideTask> experienceBarHideTaskHashMap;

    public ExperienceBarManager(McMMOPlayer mcMMOPlayer)
    {
        //Init map
        experienceBars = new HashMap<>();
        experienceBarHideTaskHashMap = new HashMap<>();

        this.mcMMOPlayer = mcMMOPlayer;
    }

    public void updateExperienceBar(PrimarySkillType primarySkillType, Plugin plugin)
    {
        if(!ExperienceConfig.getInstance().isExperienceBarsEnabled() || !ExperienceConfig.getInstance().isExperienceBarEnabled(primarySkillType))
            return;

        //Init Bar
        if(experienceBars.get(primarySkillType) == null)
            experienceBars.put(primarySkillType, new ExperienceBarWrapper(primarySkillType, mcMMOPlayer));

        //Get Bar
        ExperienceBarWrapper experienceBarWrapper = experienceBars.get(primarySkillType);

        //Update Progress
        experienceBarWrapper.setProgress(mcMMOPlayer.getProgressInCurrentSkillLevel(primarySkillType));

        //Show Bar
        experienceBarWrapper.showExperienceBar();

        //Setup Hide Bar Task
        if(experienceBarHideTaskHashMap.get(primarySkillType) != null)
        {
            experienceBarHideTaskHashMap.get(primarySkillType).cancel();
            scheduleHideTask(primarySkillType, plugin);
        } else {
            scheduleHideTask(primarySkillType, plugin);
        }
    }

    private void scheduleHideTask(PrimarySkillType primarySkillType, Plugin plugin) {
        ExperienceBarHideTask experienceBarHideTask = new ExperienceBarHideTask(this, mcMMOPlayer, primarySkillType);
        experienceBarHideTask.runTaskLater(plugin, 20*2);
        experienceBarHideTaskHashMap.put(primarySkillType, experienceBarHideTask);
    }

    public void hideExperienceBar(PrimarySkillType primarySkillType)
    {
        experienceBars.get(primarySkillType).hideExperienceBar();
    }

    public void clearTask(PrimarySkillType primarySkillType)
    {
        experienceBarHideTaskHashMap.remove(primarySkillType);
    }
}
