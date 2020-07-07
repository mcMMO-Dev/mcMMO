package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.ExperienceBarHideTask;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * ExperienceBarManager handles displaying and updating mcMMO experience bars for players
 * Each ExperienceBarManager only manages a single player
 */
public class ExperienceBarManager {
    private final McMMOPlayer mcMMOPlayer;
    int delaySeconds = 3;

    private HashMap<PrimarySkillType, ExperienceBarWrapper> experienceBars;
    private HashMap<PrimarySkillType, ExperienceBarHideTask> experienceBarHideTaskHashMap;

    private final HashMap<PrimarySkillType, BarState> barStateMap;

    private HashSet<PrimarySkillType> alwaysVisible;
    private HashSet<PrimarySkillType> disabledBars;

    public ExperienceBarManager(McMMOPlayer mcMMOPlayer, HashMap<PrimarySkillType, BarState> barStateMap)
    {
        this.mcMMOPlayer = mcMMOPlayer;
        this.barStateMap = barStateMap;

        init();
    }

    public void init() {
        //Init maps
        experienceBars = new HashMap<>();
        experienceBarHideTaskHashMap = new HashMap<>();

        //Init sets
        alwaysVisible = new HashSet<>();
        disabledBars = new HashSet<>();

        syncBarStates();
    }

    private void syncBarStates() {
        for(Map.Entry<PrimarySkillType, BarState> entry : barStateMap.entrySet()) {
            PrimarySkillType key = entry.getKey();
            BarState barState = entry.getValue();

            switch(barState) {
                case NORMAL:
                    break;
                case ALWAYS_ON:
                    xpBarSettingToggle(XPBarSettingTarget.SHOW, key);
                case DISABLED:
                    xpBarSettingToggle(XPBarSettingTarget.HIDE, key);
            }
        }
    }

    private void resetBarStateMap() {
        SkillUtils.setBarStateDefaults(barStateMap);
    }

    public void updateExperienceBar(PrimarySkillType primarySkillType, Plugin plugin)
    {
        if(disabledBars.contains(primarySkillType)
                || !ExperienceConfig.getInstance().isExperienceBarsEnabled()
                || !ExperienceConfig.getInstance().isExperienceBarEnabled(primarySkillType))
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
        }

        scheduleHideTask(primarySkillType, plugin);
    }

    private void scheduleHideTask(PrimarySkillType primarySkillType, Plugin plugin) {
        if(alwaysVisible.contains(primarySkillType))
            return;

        ExperienceBarHideTask experienceBarHideTask = new ExperienceBarHideTask(this, mcMMOPlayer, primarySkillType);
        experienceBarHideTask.runTaskLater(plugin, 20* delaySeconds);
        experienceBarHideTaskHashMap.put(primarySkillType, experienceBarHideTask);
    }

    public void hideExperienceBar(PrimarySkillType primarySkillType)
    {
        if(experienceBars.containsKey(primarySkillType))
            experienceBars.get(primarySkillType).hideExperienceBar();
    }

    public void clearTask(PrimarySkillType primarySkillType)
    {
        experienceBarHideTaskHashMap.remove(primarySkillType);
    }

    public void disableAllBars() {
        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            xpBarSettingToggle(XPBarSettingTarget.HIDE, primarySkillType);
        }

        NotificationManager.sendPlayerInformationChatOnlyPrefixed(mcMMOPlayer.getPlayer(), "Commands.XPBar.DisableAll");
    }

    public void xpBarSettingToggle(@NotNull XPBarSettingTarget settingTarget, @Nullable PrimarySkillType skillType) {
        switch(settingTarget) {
            case SHOW:
                disabledBars.remove(skillType);
                alwaysVisible.add(skillType);

                //Remove lingering tasks
                if(experienceBarHideTaskHashMap.containsKey(skillType)) {
                    experienceBarHideTaskHashMap.get(skillType).cancel();
                }

                updateExperienceBar(skillType, mcMMO.p);
                barStateMap.put(skillType, BarState.ALWAYS_ON);
                break;
            case HIDE:
                alwaysVisible.remove(skillType);
                disabledBars.add(skillType);

                //Remove lingering tasks
                if(experienceBarHideTaskHashMap.containsKey(skillType)) {
                    experienceBarHideTaskHashMap.get(skillType).cancel();
                }

                hideExperienceBar(skillType);
                barStateMap.put(skillType, BarState.DISABLED);
                break;
            case RESET:
                resetBarSettings();
                break;
        }

        informPlayer(settingTarget, skillType);
    }

    private void resetBarSettings() {
        //Hide all currently permanent bars
        for(PrimarySkillType permanent : alwaysVisible) {
            hideExperienceBar(permanent);
        }

        resetBarStateMap();

        alwaysVisible.clear();
        disabledBars.clear();

        //Hide child skills by default
        xpBarSettingToggle(XPBarSettingTarget.HIDE, PrimarySkillType.SALVAGE);
        xpBarSettingToggle(XPBarSettingTarget.HIDE, PrimarySkillType.SMELTING);
    }

    private void informPlayer(@NotNull ExperienceBarManager.@NotNull XPBarSettingTarget settingTarget, @Nullable PrimarySkillType skillType) {
        //Inform player of setting change
        if(settingTarget != XPBarSettingTarget.RESET) {
            NotificationManager.sendPlayerInformationChatOnlyPrefixed(mcMMOPlayer.getPlayer(), "Commands.XPBar.SettingChanged", skillType.getName(), settingTarget.toString());
        } else {
            NotificationManager.sendPlayerInformationChatOnlyPrefixed(mcMMOPlayer.getPlayer(), "Commands.XPBar.Reset");
        }
    }

    public enum XPBarSettingTarget { SHOW, HIDE, RESET, DISABLE }

    public enum BarState { NORMAL, ALWAYS_ON, DISABLED }
}
