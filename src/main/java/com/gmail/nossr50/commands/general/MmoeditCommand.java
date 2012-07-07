package com.gmail.nossr50.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

public class MmoeditCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;
        int newValue;
        SkillType skill;
        String skillName;
        String usage = ChatColor.RED + "Proper usage is /mmoedit [player] <skill> <level>"; //TODO: Needs more locale.

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.tools.mmoedit")) {
            return true;
        }

        switch (args.length) {
        case 2:
            if (sender instanceof Player) {
                if (!Skills.isSkill(args[0])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    return true;
                }

                if (Misc.isInt(args[1])) {
                    Player player = (Player) sender;
                    newValue = Integer.valueOf(args[1]);
                    skill = Skills.getSkillType(args[0]);
                    profile = Users.getProfile(player);

                    if (skill.equals(SkillType.ALL)) {
                        skillName = "all skills";
                    }
                    else {
                        skillName = Misc.getCapitalized(skill.toString());
                    }

                    profile.modifySkill(skill, newValue);
                    sender.sendMessage(ChatColor.GREEN + "Your level in " + skillName + " was set to " + newValue + "!"); //TODO: Needs more locale.
                }
                else {
                    sender.sendMessage(usage);
                }
            }
            else {
                sender.sendMessage(usage);
            }

            return true;

        case 3:
            if (!Misc.isInt(args[2])) {
                sender.sendMessage(usage);
                return true;
            }

            skill = Skills.getSkillType(args[1]);

            if (skill == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (skill.equals(SkillType.ALL)) {
                skillName = "all skills";
            }
            else {
                skillName = Misc.getCapitalized(skill.toString());
            }

            newValue = Integer.valueOf(args[2]);
            McMMOPlayer mcmmoPlayer = Users.getPlayer(args[0]);

            if (mcmmoPlayer != null) {
                profile = mcmmoPlayer.getProfile();

                profile.modifySkill(skill, newValue);
                mcmmoPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Your level in " + skillName + " was set to " + newValue + "!"); //TODO: Needs more locale.
                sender.sendMessage(ChatColor.RED + skillName + " has been modified for " + args[0] + "."); //TODO: Use locale
            }
            else {
                profile = new PlayerProfile(args[0], false); //Temporary Profile

                if (!profile.isLoaded()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    return true;
                }

                profile.modifySkill(skill, newValue);
                profile.save();
            }
            return true;

        default:
            sender.sendMessage(usage);
            return true;
        }
    }
}
