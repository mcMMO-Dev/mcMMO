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

public class AddxpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int xp;
        McMMOPlayer mcMMOPlayer;
        PlayerProfile profile;
        boolean allSkills = false;
        SkillType skill = null;

        switch (args.length) {
            case 2:
                if (!Permissions.addxp(sender)) {
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

                xp = Integer.parseInt(args[1]);
                mcMMOPlayer = UserManager.getPlayer((Player) sender);
                profile = mcMMOPlayer.getProfile();

                if (allSkills) {
                    for (SkillType skillType : SkillType.values()) {
                        if (skillType.isChildSkill()) {
                            continue;
                        }

                        mcMMOPlayer.applyXpGain(skillType, xp);
                    }

                    sender.sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", xp));
                }
                else {
                    skill = SkillType.getSkill(args[0]);

                    mcMMOPlayer.applyXpGain(skill, xp);
                    sender.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", xp, SkillUtils.getSkillName(skill)));
                }

                return true;

            case 3:
                if (!Permissions.addxpOthers(sender)) {
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

                mcMMOPlayer = UserManager.getPlayer(args[0]);
                xp = Integer.parseInt(args[2]);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    profile = new PlayerProfile(args[0], false);

                    if (!profile.isLoaded()) {
                        sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                        return true;
                    }

                    // TODO: Currently the offline player doesn't level up automatically
                    if (allSkills) {
                        for (SkillType skillType : SkillType.values()) {
                            if (skillType.isChildSkill()) {
                                continue;
                            }

                            profile.setSkillXpLevel(skillType, xp);
                        }
                    }
                    else {
                        skill = SkillType.getSkill(args[1]);
                        profile.setSkillXpLevel(skill, xp);
                    }

                    profile.save(); // Since this is a temporary profile, we save it here.
                }
                else {
                    if (allSkills) {
                        for (SkillType skillType : SkillType.values()) {
                            if (skillType.isChildSkill()) {
                                continue;
                            }

                            mcMMOPlayer.applyXpGain(skillType, xp);
                        }

                        mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", xp));
                    }
                    else {
                        skill = SkillType.getSkill(args[1]);
                        mcMMOPlayer.applyXpGain(skill, xp);
                        mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", xp, SkillUtils.getSkillName(skill)));
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
