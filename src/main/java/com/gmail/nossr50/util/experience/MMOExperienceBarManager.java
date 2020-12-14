package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.ExperienceBarHideTask;
import com.gmail.nossr50.util.player.NotificationManager;
import com.neetgames.mcmmo.skill.SkillBossBarSetting;
import com.neetgames.mcmmo.skill.SkillBossBarState;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * ExperienceBarManager handles displaying and updating mcMMO experience bars for players
 * Each ExperienceBarManager only manages a single player
 */
public class MMOExperienceBarManager {
    private final McMMOPlayer mmoPlayer;

    int delaySeconds = 3;

    private @NotNull final Map<PrimarySkillType, SkillBossBarState> barStateMapRef;

    private @NotNull final EnumMap<PrimarySkillType, ExperienceBarWrapper> experienceBars;
    private @NotNull final EnumMap<PrimarySkillType, ExperienceBarHideTask> experienceBarHideTaskHashMap;


    public MMOExperienceBarManager(@NotNull McMMOPlayer mmoPlayer, @NotNull Map<PrimarySkillType, SkillBossBarState> barStateMapRef)
    {
        this.mmoPlayer = mmoPlayer;
        this.barStateMapRef = barStateMapRef;

        //Init maps
        experienceBars = new EnumMap<>(PrimarySkillType.class);
        experienceBarHideTaskHashMap = new EnumMap<>(PrimarySkillType.class);

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
            experienceBars.put(primarySkillType, new ExperienceBarWrapper(primarySkillType, mmoPlayer.getPersistentPlayerData()));

        //Get Bar
        ExperienceBarWrapper experienceBarWrapper = experienceBars.get(primarySkillType);

        //Update Progress
        experienceBarWrapper.setProgress(mmoPlayer.getExperienceManager().getProgressInCurrentSkillLevel(primarySkillType));

        //Show Bar
        experienceBarWrapper.showExperienceBar();

        //Setup Hide Bar Task
        if(experienceBarHideTaskHashMap.get(primarySkillType) != null)
        {
            experienceBarHideTaskHashMap.get(primarySkillType).cancel();
        }

        scheduleHideTask(primarySkillType, plugin);
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

    public void xpBarSettingToggle(@NotNull SkillBossBarSetting skillBossBarSetting, @Nullable PrimarySkillType skillType) {
        switch(skillBossBarSetting) {
            case SHOW:
                barStateMapRef.put(skillType, SkillBossBarState.ALWAYS_ON);

                //Remove lingering tasks
                if(experienceBarHideTaskHashMap.containsKey(skillType)) {
                    experienceBarHideTaskHashMap.get(skillType).cancel();
                }

                updateExperienceBar(skillType, mcMMO.p);
                break;
            case HIDE:
                barStateMapRef.put(skillType, SkillBossBarState.DISABLED);

                //Remove lingering tasks
                if(experienceBarHideTaskHashMap.containsKey(skillType)) {
                    experienceBarHideTaskHashMap.get(skillType).cancel();
                }

                hideExperienceBar(skillType);
                break;
            case RESET:
                resetBarSettings();
                break;
        }

        informPlayer(skillBossBarSetting, skillType);
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

    /*
     * Utility Methods
     */

    public static @NotNull EnumMap<PrimarySkillType, SkillBossBarState> generateDefaultBarStateMap() {
        EnumMap<PrimarySkillType, SkillBossBarState> barStateMap = new EnumMap<>(PrimarySkillType.class);

        setBarStateDefaults(barStateMap);

        return barStateMap;
    }

    public static void setBarStateDefaults(EnumMap<PrimarySkillType, SkillBossBarState> barStateHashMap) {
        for(PrimarySkillType skillType : PrimarySkillType.values()) {
            if(skillType.isChildSkill()) {
                barStateHashMap.put(skillType, SkillBossBarState.DISABLED);
            } else {
                barStateHashMap.put(skillType, SkillBossBarState.NORMAL);
            }
        }
    }
}
