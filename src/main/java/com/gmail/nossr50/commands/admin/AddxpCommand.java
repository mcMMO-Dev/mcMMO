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

public class AddxpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int xp;
        McMMOPlayer mcMMOPlayer;
        PlayerProfile profile;
        boolean allSkills = false;

        switch (args.length) {
        case 2:
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

            if (!Misc.isInt(args[1])) {
                return false;
            }

            xp = Integer.valueOf(args[1]);
            mcMMOPlayer = Users.getPlayer((Player) sender);
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
                mcMMOPlayer.applyXpGain(SkillTools.getSkillType(args[0]), xp);
                sender.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", xp, Misc.getCapitalized(args[0])));
            }

            return true;

        case 3:
            if (!Permissions.hasPermission(sender, "mcmmo.commands.addxp.others")) {
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

            if (!Misc.isInt(args[2])) {
                return false;
            }

            mcMMOPlayer = Users.getPlayer(args[0]);
            xp = Integer.valueOf(args[2]);

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
                    profile.setSkillXpLevel(SkillTools.getSkillType(args[1]), xp);
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
                    mcMMOPlayer.applyXpGain(SkillTools.getSkillType(args[1]), xp);
                    mcMMOPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", xp, Misc.getCapitalized(args[1])));
                }
            }

            if (allSkills) {
                sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", args[0]));
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", Misc.getCapitalized(args[1]), args[0]));
            }

            return true;

        default:
            return false;
        }
    }
}
