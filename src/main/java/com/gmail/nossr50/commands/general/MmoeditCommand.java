package com.gmail.nossr50.commands.general;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class MmoeditCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;
        int newValue;
        SkillType skill;
        String skillName;
        String usage = LocaleLoader.getString("Commands.Usage.3", new Object[] {"mmoedit", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">", "<" + LocaleLoader.getString("Commands.Usage.Level") + ">" });

        switch (args.length) {
        case 2:
            if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mmoedit") && !Permissions.mmoedit((Player) sender)) {
                return true;
            }

            if (sender instanceof Player) {
                if (!SkillTools.isSkill(args[0])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    return true;
                }

                if (Misc.isInt(args[1])) {
                    Player player = (Player) sender;
                    newValue = Integer.valueOf(args[1]);
                    skill = SkillTools.getSkillType(args[0]);
                    profile = Users.getProfile(player);

                    if (skill.equals(SkillType.ALL)) {
                        skillName = "all skills";
                    }
                    else {
                        skillName = Misc.getCapitalized(skill.toString());
                    }

                    profile.modifySkill(skill, newValue);
                    sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.1", new Object[] {skillName, newValue}));
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
            if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.mmoedit.others") && !Permissions.mmoedit((Player) sender)) {
                return true;
            }

            if (!Misc.isInt(args[2])) {
                sender.sendMessage(usage);
                return true;
            }

            skill = SkillTools.getSkillType(args[1]);

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

                if (profile == null) {
                    sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                    return true;
                }

                profile.modifySkill(skill, newValue);
                mcmmoPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.1", new Object[] {skillName, newValue}));
                sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.2", new Object[] {skillName, args[0]}));
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
