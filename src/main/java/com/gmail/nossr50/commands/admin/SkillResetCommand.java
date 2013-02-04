package com.gmail.nossr50.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class SkillResetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer modifiedPlayer;
        PlayerProfile profile;
        SkillType skill;
        String usage = LocaleLoader.getString("Commands.Usage.2", "skillreset", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">");

        switch (args.length) {
        case 1:
            if (sender instanceof Player) {
                if (!SkillTools.isSkill(args[0])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    return true;
                }

                skill = SkillTools.getSkillType(args[0]);

                if (CommandHelper.noCommandPermissions((Player) sender, "mcmmo.commands.skillreset." + skill.toString().toLowerCase())) {
                    return true;
                }

                modifiedPlayer = (Player) sender;
                profile = Users.getPlayer((Player) sender).getProfile();
                profile.modifySkill(skill, 0);

                if (skill == SkillType.ALL) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Reset.All"));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Reset.Single", Misc.getCapitalized(skill.toString())));
                }
            }
            else {
                sender.sendMessage(usage);
            }

            return true;

        case 2:
            modifiedPlayer = mcMMO.p.getServer().getOfflinePlayer(args[0]);
            profile = Users.getPlayer(args[0]).getProfile();

            // TODO:Not sure if we actually need a null check here
            if (profile == null || !profile.isLoaded()) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            if (!SkillTools.isSkill(args[1])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            skill = SkillTools.getSkillType(args[1]);

            if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.skillreset.others." + skill.toString().toLowerCase())) {
                return true;
            }

            profile.modifySkill(skill, 0);

            if (modifiedPlayer.isOnline()) {
                if (skill == SkillType.ALL) {
                    ((Player)modifiedPlayer).sendMessage(LocaleLoader.getString("Commands.Reset.All"));
                }
                else {
                    ((Player)modifiedPlayer).sendMessage(LocaleLoader.getString("Commands.Reset.Single", Misc.getCapitalized(skill.toString())));
                }
            }

            sender.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.2", Misc.getCapitalized(skill.toString()), args[0]));
            return true;

        default:
            sender.sendMessage(usage);
            return true;
        }
    }
}
