package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.text.StringUtils;
import com.google.common.collect.ImmutableList;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class XPBarCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().getPlayer((Player) sender);
            if(mmoPlayer == null) {
                NotificationManager.sendPlayerInformationChatOnlyPrefixed(Misc.adaptPlayer(mmoPlayer), "Profile.PendingLoad");
                return false;
            }

            if(args.length == 0) {
                return false;
            } else if(args.length < 2) {
              String option = args[0];

              if(option.equalsIgnoreCase(SkillBossBarSetting.RESET.toString())) {
                  mmoPlayer.getExperienceBarManager().xpBarSettingToggle(SkillBossBarSetting.RESET, null);
                  return true;
              } else if(option.equalsIgnoreCase(SkillBossBarSetting.DISABLE.toString())) {
                  mmoPlayer.getExperienceBarManager().disableAllBars();
                  return true;
              } else {
                  return false;
              }

              //Per skill Settings path
            } else if (args.length == 2) {
                String skillName = args[1];

                if(SkillUtils.isSkill(skillName)) {

                    PrimarySkillType targetSkill = PrimarySkillType.getSkill(skillName);

                    //Target setting
                    String option = args[0].toLowerCase();

                    SkillBossBarSetting settingTarget = getSettingTarget(option);
                    if(settingTarget != null && settingTarget != SkillBossBarSetting.RESET) {
                        //Change setting
                        mmoPlayer.getExperienceBarManager().xpBarSettingToggle(settingTarget, targetSkill);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private @Nullable SkillBossBarSetting getSettingTarget(String string) {
        switch (string.toLowerCase()) {
            case "hide":
                return SkillBossBarSetting.HIDE;
            case "show":
                return SkillBossBarSetting.SHOW;
            case "reset":
                return SkillBossBarSetting.RESET;
            case "disable":
                return SkillBossBarSetting.DISABLE;
        }

        return null;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> options = new ArrayList<>();

                for(SkillBossBarSetting settingTarget : SkillBossBarSetting.values()) {
                    options.add(StringUtils.getCapitalized(settingTarget.toString()));
                }

                return StringUtil.copyPartialMatches(args[0], options, new ArrayList<>(SkillBossBarSetting.values().length));
            case 2:
                if(!args[0].equalsIgnoreCase(SkillBossBarSetting.RESET.toString()))
                    return StringUtil.copyPartialMatches(args[1], PrimarySkillType.SKILL_NAMES, new ArrayList<>(PrimarySkillType.SKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }
}
