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

public class MmoeditCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;
        int newValue;
        boolean allSkills = false;
        SkillType skill = null;

        switch (args.length) {
            case 2:
                if (!Permissions.mmoedit(sender)) {
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

                newValue = Integer.parseInt(args[1]);
                profile = UserManager.getPlayer((Player) sender).getProfile();

                if (allSkills) {
                    for (SkillType skillType : SkillType.values()) {
                        if (skillType.isChildSkill()) {
                            continue;
                        }

                        profile.modifySkill(skillType, newValue);
                    }

                    sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.AllSkills.1", newValue));
                }
                else {
                    skill = SkillType.getSkill(args[0]);
                    profile.modifySkill(skill, newValue);
                    sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.1", SkillUtils.getSkillName(skill), newValue));
                }

                return true;

            case 3:
                if (!Permissions.mmoeditOthers(sender)) {
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

                newValue = Integer.parseInt(args[2]);
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

                            profile.modifySkill(skillType, newValue);
                        }
                    }
                    else {
                        skill = SkillType.getSkill(args[1]);
                        profile.modifySkill(skill, newValue);
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

                            profile.modifySkill(skillType, newValue);
                        }

                        mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.mmoedit.AllSkills.1", newValue));
                    }
                    else {
                        skill = SkillType.getSkill(args[1]);
                        profile.modifySkill(skill, newValue);
                        mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.1", SkillUtils.getSkillName(skill), newValue));
                    }
                }

                if (allSkills) {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", args[0]));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.2", SkillUtils.getSkillName(skill), args[0]));
                }

                return true;

            default:
                return false;
        }
    }
}
