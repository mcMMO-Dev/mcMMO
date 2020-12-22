package com.gmail.nossr50.commands.player;

import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.text.StringUtils;
import com.google.common.collect.ImmutableList;
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
                NotificationManager.sendPlayerInformationChatOnlyPrefixed(mmoPlayer.getPlayer(), "Profile.PendingLoad");
                return false;
            }

            if(args.length == 0) {
                return false;
            } else if(args.length < 2) {
              String option = args[0];

              if(option.equalsIgnoreCase(MMOExperienceBarManager.XPBarSettingTarget.RESET.toString())) {
                  mmoPlayer.getExperienceBarManager().xpBarSettingToggle(MMOExperienceBarManager.XPBarSettingTarget.RESET, null);
                  return true;
              } else if(option.equalsIgnoreCase(MMOExperienceBarManager.XPBarSettingTarget.DISABLE.toString())) {
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

                    MMOExperienceBarManager.XPBarSettingTarget settingTarget = getSettingTarget(option);
                    if(settingTarget != null && settingTarget != MMOExperienceBarManager.XPBarSettingTarget.RESET) {
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

    private @Nullable MMOExperienceBarManager.XPBarSettingTarget getSettingTarget(String string) {
        switch (string.toLowerCase()) {
            case "hide":
                return MMOExperienceBarManager.XPBarSettingTarget.HIDE;
            case "show":
                return MMOExperienceBarManager.XPBarSettingTarget.SHOW;
            case "reset":
                return MMOExperienceBarManager.XPBarSettingTarget.RESET;
            case "disable":
                return MMOExperienceBarManager.XPBarSettingTarget.DISABLE;
        }

        return null;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> options = new ArrayList<>();

                for(MMOExperienceBarManager.XPBarSettingTarget settingTarget : MMOExperienceBarManager.XPBarSettingTarget.values()) {
                    options.add(StringUtils.getCapitalized(settingTarget.toString()));
                }

                return StringUtil.copyPartialMatches(args[0], options, new ArrayList<>(MMOExperienceBarManager.XPBarSettingTarget.values().length));
            case 2:
                if(!args[0].equalsIgnoreCase(MMOExperienceBarManager.XPBarSettingTarget.RESET.toString()))
                    return StringUtil.copyPartialMatches(args[1], PrimarySkillType.SKILL_NAMES, new ArrayList<>(PrimarySkillType.SKILL_NAMES.size()));
            default:
                return ImmutableList.of();
        }
    }
}
