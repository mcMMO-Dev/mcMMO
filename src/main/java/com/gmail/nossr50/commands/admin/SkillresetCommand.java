package com.gmail.nossr50.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class SkillresetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;
        boolean allSkills = false;

        switch (args.length) {
        case 1:
            if (!Permissions.hasPermission(sender, "mcmmo.commands.skillreset")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            if (!(sender instanceof Player)) {
                return false;
            }

            if (args[0].equalsIgnoreCase("all")) {
                allSkills = true;
            }
            else if (!SkillTools.isSkill(args[0])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (allSkills) {
                for (SkillType skillType : SkillType.values()) {
                    if (skillType.isChildSkill()) {
                        continue;
                    }

                    if (!Permissions.hasPermission(sender, "mcmmo.commands.skillreset." + args[0].toLowerCase())) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }
                }
            }
            else {
                if (!Permissions.hasPermission(sender, "mcmmo.commands.skillreset." + args[0].toLowerCase())) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }
            }

            profile = Users.getPlayer((Player) sender).getProfile();

            if (allSkills) {
                for (SkillType skillType : SkillType.values()) {
                    if (skillType.isChildSkill()) {
                        continue;
                    }

                    profile.modifySkill(skillType, 0);
                }

                sender.sendMessage(LocaleLoader.getString("Commands.Reset.All"));
            }
            else {
                profile.modifySkill(SkillType.getSkill(args[0]), 0);
                sender.sendMessage(LocaleLoader.getString("Commands.Reset.Single", Misc.getCapitalized(args[0])));
            }

            return true;

        case 2:
            if (!Permissions.hasPermission(sender, "mcmmo.commands.skillreset.others")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            if (args[1].equalsIgnoreCase("all")) {
                allSkills = true;
            }
            else if (!SkillTools.isSkill(args[1])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (allSkills) {
                for (SkillType skillType : SkillType.values()) {
                    if (skillType.isChildSkill()) {
                        continue;
                    }

                    if (!Permissions.hasPermission(sender, "mcmmo.commands.skillreset.others." + args[1].toLowerCase())) {
                        sender.sendMessage(command.getPermissionMessage());
                        return true;
                    }
                }
            }
            else {
                if (!Permissions.hasPermission(sender, "mcmmo.commands.skillreset.others." + args[1].toLowerCase())) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }
            }

            McMMOPlayer mcMMOPlayer = Users.getPlayer(args[0]);

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

                        profile.modifySkill(skillType, 0);
                    }
                }
                else {
                    profile.modifySkill(SkillType.getSkill(args[1]), 0);
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

                        profile.modifySkill(skillType, 0);
                    }

                    mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.Reset.All"));
                }
                else {
                    profile.modifySkill(SkillType.getSkill(args[1]), 0);
                    mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.Reset.Single", Misc.getCapitalized(args[1])));
                }
            }

            if (allSkills) {
                sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", args[0]));
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.2", Misc.getCapitalized(args[1]), args[0]));
            }

            return true;

        default:
            return false;
        }
    }
}
