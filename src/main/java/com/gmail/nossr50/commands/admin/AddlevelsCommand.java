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

public class AddlevelsCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;
        int levels;
        SkillType skill;

        switch (args.length) {
        case 2:
            if (!(sender instanceof Player)) {
                return false;
            }

            if (!SkillTools.isSkill(args[0])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (!Misc.isInt(args[1])) {
                return false;
            }

            levels = Integer.valueOf(args[1]);
            skill = SkillTools.getSkillType(args[0]);
            profile = Users.getPlayer((Player) sender).getProfile();

            if (skill.equals(SkillType.ALL)) {
                sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.1", levels));
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.1", levels, Misc.getCapitalized(skill.toString())));
            }

            profile.addLevels(skill, levels);
            return true;

        case 3:
            if (!Permissions.hasPermission(sender, "mcmmo.commands.addlevels.others")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            if (!SkillTools.isSkill(args[1])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (!Misc.isInt(args[2])) {
                return false;
            }

            McMMOPlayer mcMMOPlayer = Users.getPlayer(args[0]);
            levels = Integer.valueOf(args[2]);
            skill = SkillTools.getSkillType(args[1]);

            // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
            if (mcMMOPlayer == null) {
                profile = new PlayerProfile(args[0], false);

                if (!profile.isLoaded()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    return true;
                }

                profile.addLevels(skill, levels);
                profile.save(); // Since this is a temporary profile, we save it here.
            }
            else {
                profile = mcMMOPlayer.getProfile();
                Player player = mcMMOPlayer.getPlayer();

                profile.addLevels(skill, levels);

                // TODO: Is it even possible for the player to be offline at this point?
                if (player.isOnline()) {
                    if (skill.equals(SkillType.ALL)) {
                        player.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.1", levels));
                    }
                    else {
                        player.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.1", levels, Misc.getCapitalized(skill.toString())));
                    }
                }
            }

            if (skill.equals(SkillType.ALL)) {
                sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", args[0]));
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", Misc.getCapitalized(skill.toString()), args[0]));
            }

            return true;

        default:
            return false;
        }
    }
}
