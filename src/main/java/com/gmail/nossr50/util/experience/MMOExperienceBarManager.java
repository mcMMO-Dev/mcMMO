package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.datatypes.skills.CoreSkills;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.ExperienceBarHideTask;
import com.gmail.nossr50.util.player.NotificationManager;
import com.neetgames.mcmmo.skill.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link MMOExperienceBarManager} handles displaying and updating mcMMO experience bars for players
 * Each {@link MMOExperienceBarManager} only manages a single player
 */
public class MMOExperienceBarManager {
    private final McMMOPlayer mmoPlayer;

    int delaySeconds = 3;

    private @NotNull final Map<PrimarySkillType, SkillBossBarState> barStateMapRef;

    private @NotNull final Map<PrimarySkillType, ExperienceBarWrapper> experienceBars;
    private @NotNull final Map<PrimarySkillType, ExperienceBarHideTask> experienceBarHideTaskHashMap;

    public MMOExperienceBarManager(@NotNull McMMOPlayer mmoPlayer, @NotNull Map<PrimarySkillType, SkillBossBarState> barStateMapRef)
    {
        this.mmoPlayer = mmoPlayer;
        this.barStateMapRef = barStateMapRef;

        //Init maps
        experienceBars = new HashMap<>();
        experienceBarHideTaskHashMap = new HashMap<>();

        init();
    }

    private void init() {
        syncBarStates();
    }

    private void syncBarStates() {
        for(Map.Entry<PrimarySkillType, SkillBossBarState> entry : barStateMapRef.entrySet()) {
            PrimarySkillType key = entry.getKey();
            SkillBossBarState barState = entry.getValue();

            switch(barState) {
                case NORMAL:
                    break;
                case ALWAYS_ON:
                    xpBarSettingToggle(SkillBossBarSetting.SHOW, key);
                case DISABLED:
                    xpBarSettingToggle(SkillBossBarSetting.HIDE, key);
            }
        }
    }

    private void resetBarStateMap() {
        barStateMapRef.putAll(generateDefaultBarStateMap());
    }

    public void updateExperienceBar(@NotNull PrimarySkillType primarySkillType, @NotNull Plugin plugin)
    {
        if(isBarDisabled(primarySkillType))
            return;

        //Init Bar
        if(experienceBars.get(primarySkillType) == null)
            experienceBars.put(primarySkillType, new ExperienceBarWrapper(primarySkillType, mmoPlayer));

        //Get Bar
        ExperienceBarWrapper experienceBarWrapper = experienceBars.get(primarySkillType);

        //Update Progress
        experienceBarWrapper.setProgress(mmoPlayer.getExperienceHandler().getProgressInCurrentSkillLevel(primarySkillType));

        //Show Bar
        experienceBarWrapper.showExperienceBar();

        //Setup Hide Bar Task
        if(experienceBarHideTaskHashMap.get(primarySkillType) != null)
        {
            experienceBarHideTaskHashMap.get(primarySkillType).cancel();
        }

        scheduleHideTask(primarySkillType, plugin);
    }

    private boolean isBarDisabled(@NotNull PrimarySkillType primarySkillType) {
        return barStateMapRef.get(primarySkillType) == SkillBossBarState.DISABLED
                //Config checks
                || !ExperienceConfig.getInstance().isExperienceBarsEnabled()
                || !ExperienceConfig.getInstance().isExperienceBarEnabled(primarySkillType);
    }

    private boolean isBarAlwaysVisible(@NotNull PrimarySkillType primarySkillType) {
        return barStateMapRef.get(primarySkillType) == SkillBossBarState.ALWAYS_ON;
    }

    private void scheduleHideTask(@NotNull PrimarySkillType primarySkillType, @NotNull Plugin plugin) {
        if(isBarAlwaysVisible(primarySkillType))
            return;

        ExperienceBarHideTask experienceBarHideTask = new ExperienceBarHideTask(this, mmoPlayer, primarySkillType);
        experienceBarHideTask.runTaskLater(plugin, 20 * delaySeconds);
        experienceBarHideTaskHashMap.put(primarySkillType, experienceBarHideTask);
    }

    public void hideExperienceBar(@NotNull PrimarySkillType primarySkillType)
    {
        if(experienceBars.containsKey(primarySkillType))
            experienceBars.get(primarySkillType).hideExperienceBar();
    }

    public void clearTask(@NotNull PrimarySkillType primarySkillType)
    {
        experienceBarHideTaskHashMap.remove(primarySkillType);
    }

    public void disableAllBars() {
        for(PrimarySkillType primarySkillType : mcMMO.p.getSkillRegister().getRootSkills()) {
            xpBarSettingToggle(SkillBossBarSetting.HIDE, primarySkillType);
        }

        NotificationManager.sendPlayerInformationChatOnlyPrefixed(Misc.adaptPlayer(mmoPlayer), "Commands.XPBar.DisableAll");
    }

    public void xpBarSettingToggle(@NotNull SkillBossBarSetting skillBossBarSetting, @NotNull PrimarySkillType primarySkillType) {
        switch(skillBossBarSetting) {
            case SHOW:
                barStateMapRef.put(primarySkillType, SkillBossBarState.ALWAYS_ON);

                //Remove lingering tasks
                if(experienceBarHideTaskHashMap.containsKey(primarySkillType)) {
                    experienceBarHideTaskHashMap.get(primarySkillType).cancel();
                }

                updateExperienceBar(primarySkillType, mcMMO.p);
                break;
            case HIDE:
                barStateMapRef.put(primarySkillType, SkillBossBarState.DISABLED);

                //Remove lingering tasks
                if(experienceBarHideTaskHashMap.containsKey(primarySkillType)) {
                    experienceBarHideTaskHashMap.get(primarySkillType).cancel();
                }

                hideExperienceBar(primarySkillType);
                break;
            case RESET:
                resetBarSettings();
                break;
        }

        informPlayer(skillBossBarSetting, primarySkillType);
    }

    private void resetBarSettings() {
        barStateMapRef.putAll(generateDefaultBarStateMap());
    }

    private void informPlayer(@NotNull SkillBossBarSetting settingTarget, @NotNull PrimarySkillType primarySkillType) {
        //Inform player of setting change
        if(settingTarget != SkillBossBarSetting.RESET) {
            NotificationManager.sendPlayerInformationChatOnlyPrefixed(Misc.adaptPlayer(mmoPlayer), "Commands.XPBar.SettingChanged", primarySkillType.getName(), settingTarget.toString());
        } else {
            NotificationManager.sendPlayerInformationChatOnlyPrefixed(Misc.adaptPlayer(mmoPlayer), "Commands.XPBar.Reset");
        }
    }

    public static @NotNull Map<PrimarySkillType, SkillBossBarState> generateDefaultBarStateMap() {
        HashMap<PrimarySkillType, SkillBossBarState> barStateMap = new HashMap<>();

        setBarStateDefaults(barStateMap);

        return barStateMap;
    }

    public static void setBarStateDefaults(@NotNull Map<PrimarySkillType, SkillBossBarState> barStateHashMap) {
        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {

            if(!primarySkillType.isChildSkill()) {
                barStateHashMap.put(primarySkillType, SkillBossBarState.DISABLED);
            } else {
                barStateHashMap.put(primarySkillType, SkillBossBarState.NORMAL);
            }
        }
    }
}
