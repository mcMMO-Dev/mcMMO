package com.gmail.nossr50.commands.experience;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class AddlevelsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;
        int levels;
        boolean allSkills = false;
        SkillType skill = null;

        switch (args.length) {
            case 2:
                if (!Permissions.addlevels(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!(sender instanceof Player)) {
                    return false;
                }

                if (args[0].equalsIgnoreCase("all")) {
                    allSkills = true;
                }
                else if (!SkillUtils.isSkill(args[0])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    return true;
                }

                if (!StringUtils.isInt(args[1])) {
                    return false;
                }

                levels = Integer.parseInt(args[1]);
                profile = UserManager.getPlayer((Player) sender).getProfile();

                if (allSkills) {
                    for (SkillType skillType : SkillType.values()) {
                        if (skillType.isChildSkill()) {
                            continue;
                        }

                        profile.addLevels(skillType, levels);
                    }
                }
                else {
                    skill = SkillType.getSkill(args[0]);
                    profile.addLevels(skill, levels);
                }

                if (allSkills) {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.1", levels));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.1", levels, SkillUtils.getSkillName(skill)));
                }

                return true;

            case 3:
                if (!Permissions.addlevelsOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (args[1].equalsIgnoreCase("all")) {
                    allSkills = true;
                }
                else if (!SkillUtils.isSkill(args[1])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    return true;
                }

                if (!StringUtils.isInt(args[2])) {
                    return false;
                }

                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(args[0]);
                levels = Integer.parseInt(args[2]);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    profile = new PlayerProfile(args[0], false);

                    if (!profile.isLoaded()) {
                        sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                        return true;
                    }

                    if (allSkills) {
                        for (SkillType skillType : SkillType.values()) {
                            if (skillType.isChildSkill()) {
                                continue;
                            }

                            profile.addLevels(skillType, levels);
                        }
                    }
                    else {
                        skill = SkillType.getSkill(args[1]);
                        profile.addLevels(skill, levels);
                    }

                    profile.save(); // Since this is a temporary profile, we save it here.
                }
                else {
                    profile = mcMMOPlayer.getProfile();

                    if (allSkills) {
                        for (SkillType skillType : SkillType.values()) {
                            if (skillType.isChildSkill()) {
                                continue;
                            }

                            profile.addLevels(skillType, levels);
                        }

                        mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.1", levels));
                    }
                    else {
                        skill = SkillType.getSkill(args[1]);
                        profile.addLevels(skill, levels);
                        mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.1", levels, SkillUtils.getSkillName(skill)));
                    }
                }

                if (allSkills) {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", args[0]));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", SkillUtils.getSkillName(skill), args[0]));
                }

                return true;

            default:
                return false;
        }
    }
}
