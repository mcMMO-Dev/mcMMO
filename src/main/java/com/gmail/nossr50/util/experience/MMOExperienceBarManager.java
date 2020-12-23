package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
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

    private @NotNull final Map<RootSkill, SkillBossBarState> barStateMapRef;

    private @NotNull final Map<RootSkill, ExperienceBarWrapper> experienceBars;
    private @NotNull final Map<RootSkill, ExperienceBarHideTask> experienceBarHideTaskHashMap;

    public MMOExperienceBarManager(@NotNull McMMOPlayer mmoPlayer, @NotNull Map<RootSkill, SkillBossBarState> barStateMapRef)
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
        for(Map.Entry<RootSkill, SkillBossBarState> entry : barStateMapRef.entrySet()) {
            RootSkill key = entry.getKey();
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

    public void updateExperienceBar(@NotNull RootSkill rootSkill, @NotNull Plugin plugin)
    {
        if(isBarDisabled(rootSkill))
            return;

        //Init Bar
        if(experienceBars.get(rootSkill) == null)
            experienceBars.put(rootSkill, new ExperienceBarWrapper(rootSkill, mmoPlayer));

        //Get Bar
        ExperienceBarWrapper experienceBarWrapper = experienceBars.get(rootSkill);

        //Update Progress
        experienceBarWrapper.setProgress(mmoPlayer.getExperienceHandler().getProgressInCurrentSkillLevel(rootSkill));

        //Show Bar
        experienceBarWrapper.showExperienceBar();

        //Setup Hide Bar Task
        if(experienceBarHideTaskHashMap.get(rootSkill) != null)
        {
            experienceBarHideTaskHashMap.get(rootSkill).cancel();
        }

        scheduleHideTask(rootSkill, plugin);
    }

    private boolean isBarDisabled(@NotNull RootSkill rootSkill) {
        return barStateMapRef.get(rootSkill) == SkillBossBarState.DISABLED
                //Config checks
                || !ExperienceConfig.getInstance().isExperienceBarsEnabled()
                || !ExperienceConfig.getInstance().isExperienceBarEnabled(rootSkill);
    }

    private boolean isBarAlwaysVisible(@NotNull RootSkill rootSkill) {
        return barStateMapRef.get(rootSkill) == SkillBossBarState.ALWAYS_ON;
    }

    private void scheduleHideTask(@NotNull RootSkill rootSkill, @NotNull Plugin plugin) {
        if(isBarAlwaysVisible(rootSkill))
            return;

        ExperienceBarHideTask experienceBarHideTask = new ExperienceBarHideTask(this, mmoPlayer, rootSkill);
        experienceBarHideTask.runTaskLater(plugin, 20 * delaySeconds);
        experienceBarHideTaskHashMap.put(rootSkill, experienceBarHideTask);
    }

    public void hideExperienceBar(@NotNull RootSkill rootSkill)
    {
        if(experienceBars.containsKey(rootSkill))
            experienceBars.get(rootSkill).hideExperienceBar();
    }

    public void clearTask(@NotNull RootSkill rootSkill)
    {
        experienceBarHideTaskHashMap.remove(rootSkill);
    }

    public void disableAllBars() {
        for(RootSkill rootSkill : mcMMO.p.getSkillRegister().getRootSkills()) {
            xpBarSettingToggle(SkillBossBarSetting.HIDE, rootSkill);
        }

        NotificationManager.sendPlayerInformationChatOnlyPrefixed(Misc.adaptPlayer(mmoPlayer), "Commands.XPBar.DisableAll");
    }

    public void xpBarSettingToggle(@NotNull SkillBossBarSetting skillBossBarSetting, @NotNull RootSkill rootSkill) {
        switch(skillBossBarSetting) {
            case SHOW:
                barStateMapRef.put(rootSkill, SkillBossBarState.ALWAYS_ON);

                //Remove lingering tasks
                if(experienceBarHideTaskHashMap.containsKey(rootSkill)) {
                    experienceBarHideTaskHashMap.get(rootSkill).cancel();
                }

                updateExperienceBar(rootSkill, mcMMO.p);
                break;
            case HIDE:
                barStateMapRef.put(rootSkill, SkillBossBarState.DISABLED);

                //Remove lingering tasks
                if(experienceBarHideTaskHashMap.containsKey(rootSkill)) {
                    experienceBarHideTaskHashMap.get(rootSkill).cancel();
                }

                hideExperienceBar(rootSkill);
                break;
            case RESET:
                resetBarSettings();
                break;
        }

        informPlayer(skillBossBarSetting, rootSkill);
    }

    private void resetBarSettings() {
        barStateMapRef.putAll(generateDefaultBarStateMap());
    }

    private void informPlayer(@NotNull SkillBossBarSetting settingTarget, @NotNull RootSkill rootSkill) {
        //Inform player of setting change
        if(settingTarget != SkillBossBarSetting.RESET) {
            NotificationManager.sendPlayerInformationChatOnlyPrefixed(Misc.adaptPlayer(mmoPlayer), "Commands.XPBar.SettingChanged", rootSkill.getSkillName(), settingTarget.toString());
        } else {
            NotificationManager.sendPlayerInformationChatOnlyPrefixed(Misc.adaptPlayer(mmoPlayer), "Commands.XPBar.Reset");
        }
    }

    public static @NotNull Map<RootSkill, SkillBossBarState> generateDefaultBarStateMap() {
        HashMap<RootSkill, SkillBossBarState> barStateMap = new HashMap<>();

        setBarStateDefaults(barStateMap);

        return barStateMap;
    }

    public static void setBarStateDefaults(@NotNull Map<RootSkill, SkillBossBarState> barStateHashMap) {
        for(RootSkill rootSkill : CoreSkills.getCoreSkills()) {

            if(CoreSkills.isChildSkill(rootSkill)) {
                barStateHashMap.put(rootSkill, SkillBossBarState.DISABLED);
            } else {
                barStateHashMap.put(rootSkill, SkillBossBarState.NORMAL);
            }
        }
    }
}
