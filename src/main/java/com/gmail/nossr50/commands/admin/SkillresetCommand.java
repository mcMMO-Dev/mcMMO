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
        SkillType skill;

        switch (args.length) {
        case 1:
            if (!(sender instanceof Player)) {
                return false;
            }

            if (!SkillTools.isSkill(args[0])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            skill = SkillTools.getSkillType(args[0]);

            if (skill != SkillType.ALL && !Permissions.hasPermission(sender, "mcmmo.commands.skillreset." + skill.toString().toLowerCase())) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            profile = Users.getPlayer((Player) sender).getProfile();

            if (skill == SkillType.ALL) {
                for (SkillType type : SkillType.values()) {
                    if (type != SkillType.ALL) {
                        if (Permissions.hasPermission(sender, "mcmmo.commands.skillreset." + type.toString().toLowerCase())) {
                            profile.modifySkill(type, 0);
                            sender.sendMessage(LocaleLoader.getString("Commands.Reset.Single", Misc.getCapitalized(type.toString())));
                        }
                    }
                }
            }
            else {
                profile.modifySkill(skill, 0);
                sender.sendMessage(LocaleLoader.getString("Commands.Reset.Single", Misc.getCapitalized(skill.toString())));
            }

            return true;

        case 2:
            if (!SkillTools.isSkill(args[1])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            skill = SkillTools.getSkillType(args[1]);

            if (skill != SkillType.ALL && !Permissions.hasPermission(sender, "mcmmo.commands.skillreset.others." + skill.toString().toLowerCase())) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            McMMOPlayer mcMMOPlayer = Users.getPlayer(args[0]);

            // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
            if (mcMMOPlayer == null) {
                profile = new PlayerProfile(args[0], false);

                if (!profile.isLoaded()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    return true;
                }

                if (skill == SkillType.ALL) {
                    for (SkillType type : SkillType.values()) {
                        if (type != SkillType.ALL) {
                            if (Permissions.hasPermission(sender, "mcmmo.commands.skillreset.others" + type.toString().toLowerCase())) {
                                profile.modifySkill(type, 0);
                                sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.2", Misc.getCapitalized(type.toString()), args[0]));
                            }
                        }
                    }
                }
                else {
                    profile.modifySkill(skill, 0);
                    sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.2", Misc.getCapitalized(skill.toString()), args[0]));
                }

                profile.save(); // Since this is a temporary profile, we save it here.
            }
            else {
                profile = mcMMOPlayer.getProfile();
                Player player = mcMMOPlayer.getPlayer();

                if (skill == SkillType.ALL) {
                    for (SkillType type : SkillType.values()) {
                        if (type != SkillType.ALL) {
                            if (Permissions.hasPermission(sender, "mcmmo.commands.skillreset.others" + type.toString().toLowerCase())) {
                                profile.modifySkill(type, 0);
                                sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.2", Misc.getCapitalized(type.toString()), args[0]));
                                player.sendMessage(LocaleLoader.getString("Commands.Reset.Single", Misc.getCapitalized(type.toString())));
                            }
                        }
                    }
                }
                else {
                    profile.modifySkill(skill, 0);
                    sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.2", Misc.getCapitalized(skill.toString()), args[0]));
                    player.sendMessage(LocaleLoader.getString("Commands.Reset.Single", Misc.getCapitalized(skill.toString())));
                }
            }

            return true;

        default:
            return false;
        }
    }
}
