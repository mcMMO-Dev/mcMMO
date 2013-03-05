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
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SkillresetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;
        boolean allSkills = false;
        SkillType skill = null;
        String skillName = "";

        switch (args.length) {
            case 1:
                if (!Permissions.skillreset(sender)) {
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

                profile = UserManager.getPlayer((Player) sender).getProfile();

                if (allSkills) {
                    for (SkillType skillType : SkillType.values()) {
                        if (skillType.isChildSkill()) {
                            continue;
                        }

                        if (!Permissions.skillreset(sender, skillType)) {
                            sender.sendMessage(command.getPermissionMessage());
                            continue;
                        }

                        profile.modifySkill(skillType, 0);
                    }

                    sender.sendMessage(LocaleLoader.getString("Commands.Reset.All"));
                }
                else {
                    skill = SkillType.getSkill(args[0]);
                    skillName = SkillUtils.getSkillName(skill);

                    if (!Permissions.skillreset(sender, skill)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }

                    profile.modifySkill(skill, 0);
                    sender.sendMessage(LocaleLoader.getString("Commands.Reset.Single", skillName));
                }

                return true;

            case 2:
                if (!Permissions.skillresetOthers(sender)) {
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

                if (!allSkills) {
                    skill = SkillType.getSkill(args[1]);
                    skillName = SkillUtils.getSkillName(skill);

                    if (!Permissions.skillresetOthers(sender, skill)) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }
                }

                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(args[0]);

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

                            if (!Permissions.skillresetOthers(sender, skill)) {
                                sender.sendMessage(command.getPermissionMessage());
                                continue;
                            }

                            profile.modifySkill(skillType, 0);
                        }
                    }
                    else {
                        profile.modifySkill(skill, 0);
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

                            if (!Permissions.skillresetOthers(sender, skillType)) {
                                sender.sendMessage(command.getPermissionMessage());
                                continue;
                            }

                            profile.modifySkill(skillType, 0);
                        }

                        mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.Reset.All"));
                    }
                    else {
                        profile.modifySkill(skill, 0);
                        mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.Reset.Single", skillName));
                    }
                }

                if (allSkills) {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", args[0]));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.2", skillName, args[0]));
                }

                return true;

            default:
                return false;
        }
    }
}
