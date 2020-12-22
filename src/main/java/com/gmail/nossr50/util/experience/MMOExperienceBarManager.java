package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.CoreSkillConstants;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.ExperienceBarHideTask;
import com.gmail.nossr50.util.player.NotificationManager;
import com.neetgames.mcmmo.skill.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * ExperienceBarManager handles displaying and updating mcMMO experience bars for players
 * Each ExperienceBarManager only manages a single player
 */
public class MMOExperienceBarManager {
    private final OnlineMMOPlayer mmoPlayer;

    int delaySeconds = 3;

    private @NotNull final Map<SkillIdentity, SkillBossBarState> barStateMapRef;

    private @NotNull final HashMap<SkillIdentity, ExperienceBarWrapper> experienceBars;
    private @NotNull final HashMap<SkillIdentity, ExperienceBarHideTask> experienceBarHideTaskHashMap;


    public MMOExperienceBarManager(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull Map<SkillIdentity, SkillBossBarState> barStateMapRef)
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
        for(Map.Entry<SkillIdentity, SkillBossBarState> entry : barStateMapRef.entrySet()) {
            SkillIdentity key = entry.getKey();
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

    public void updateExperienceBar(@NotNull SkillIdentity skillIdentity, @NotNull Plugin plugin)
    {
        if(isBarDisabled(skillIdentity))
            return;

        //Init Bar
        if(experienceBars.get(skillIdentity) == null)
            experienceBars.put(skillIdentity, new ExperienceBarWrapper(skillIdentity, mmoPlayer.getPersistentPlayerData()));

        //Get Bar
        ExperienceBarWrapper experienceBarWrapper = experienceBars.get(skillIdentity);

        //Update Progress
        experienceBarWrapper.setProgress(mmoPlayer.getExperienceManager().getProgressInCurrentSkillLevel(skillIdentity));

        //Show Bar
        experienceBarWrapper.showExperienceBar();

        //Setup Hide Bar Task
        if(experienceBarHideTaskHashMap.get(skillIdentity) != null)
        {
            experienceBarHideTaskHashMap.get(skillIdentity).cancel();
        }

        scheduleHideTask(skillIdentity, plugin);
    }

    private boolean isBarDisabled(PrimarySkillType primarySkillType) {
        return barStateMapRef.get(primarySkillType) == BarState.DISABLED
                //Config checks
                || !ExperienceConfig.getInstance().isExperienceBarsEnabled()
                || !ExperienceConfig.getInstance().isExperienceBarEnabled(primarySkillType);
    }

    private boolean isBarAlwaysVisible(PrimarySkillType primarySkillType) {
        return barStateMapRef.get(primarySkillType) == BarState.ALWAYS_ON;
    }

    private void scheduleHideTask(PrimarySkillType primarySkillType, Plugin plugin) {
        if(isBarAlwaysVisible(primarySkillType))
            return;

        ExperienceBarHideTask experienceBarHideTask = new ExperienceBarHideTask(this, mmoPlayer, primarySkillType);
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

        NotificationManager.sendPlayerInformationChatOnlyPrefixed(mmoPlayer.getPlayer(), "Commands.XPBar.DisableAll");
    }

    public void xpBarSettingToggle(@NotNull SkillBossBarSetting skillBossBarSetting, @Nullable SkillIdentity skillIdentity) {
        switch(skillBossBarSetting) {
            case SHOW:
                barStateMapRef.put(skillIdentity, SkillBossBarState.ALWAYS_ON);

                //Remove lingering tasks
                if(experienceBarHideTaskHashMap.containsKey(skillIdentity)) {
                    experienceBarHideTaskHashMap.get(skillIdentity).cancel();
                }

                updateExperienceBar(skillIdentity, mcMMO.p);
                break;
            case HIDE:
                barStateMapRef.put(skillIdentity, SkillBossBarState.DISABLED);

                //Remove lingering tasks
                if(experienceBarHideTaskHashMap.containsKey(skillIdentity)) {
                    experienceBarHideTaskHashMap.get(skillIdentity).cancel();
                }

                hideExperienceBar(skillIdentity);
                break;
            case RESET:
                resetBarSettings();
                break;
        }

        informPlayer(skillBossBarSetting, skillIdentity);
    }

    private void resetBarSettings() {
        barStateMapRef.putAll(generateDefaultBarStateMap());
    }

    private void informPlayer(@NotNull SkillBossBarSetting settingTarget, @Nullable PrimarySkillType skillType) {
        //Inform player of setting change
        if(settingTarget != SkillBossBarSetting.RESET) {
            NotificationManager.sendPlayerInformationChatOnlyPrefixed(mmoPlayer.getPlayer(), "Commands.XPBar.SettingChanged", skillType.getName(), settingTarget.toString());
        } else {
            NotificationManager.sendPlayerInformationChatOnlyPrefixed(mmoPlayer.getPlayer(), "Commands.XPBar.Reset");
        }
    }

    public static @NotNull HashMap<SkillIdentity, SkillBossBarState> generateDefaultBarStateMap() {
        HashMap<SkillIdentity, SkillBossBarState> barStateMap = new HashMap<>();

        setBarStateDefaults(barStateMap);

        return barStateMap;
    }

    public static void setBarStateDefaults(HashMap<SkillIdentity, SkillBossBarState> barStateHashMap) {
        for(RootSkill rootSkill : CoreSkillConstants.getImmutableCoreRootSkillSet()) {

            if(CoreSkillConstants.isChildSkill(rootSkill.getSkillIdentity())) {
                barStateHashMap.put(rootSkill.getSkillIdentity(), SkillBossBarState.DISABLED);
            } else {
                barStateHashMap.put(rootSkill.getSkillIdentity(), SkillBossBarState.NORMAL);
            }
        }
    }
}
