package com.gmail.nossr50.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
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
        Player modifiedPlayer;
        int xp;
        SkillType skill;
        McMMOPlayer mcMMOPlayer;
        PlayerProfile profile;

        switch (args.length) {
        case 2:
            if (!(sender instanceof Player)) {
                return false;
            }

            if (!SkillTools.isSkill(args[1])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (!Misc.isInt(args[1])) {
                return false;
            }

            xp = Integer.valueOf(args[1]);
            skill = SkillTools.getSkillType(args[0]);
            modifiedPlayer = (Player) sender;
            mcMMOPlayer = Users.getPlayer(modifiedPlayer);
            profile = mcMMOPlayer.getProfile();

            mcMMOPlayer.addXpOverride(skill, xp);

            if (skill.equals(SkillType.ALL)) {
                SkillTools.xpCheckAll(modifiedPlayer, profile);
                sender.sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", xp));
            }
            else {
                SkillTools.xpCheckSkill(skill, modifiedPlayer, profile);
                sender.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", xp, Misc.getCapitalized(skill.toString())));
            }

            return true;

        case 3:
            if (!Permissions.hasPermission(sender, "mcmmo.commands.addxp.others")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }

            modifiedPlayer = mcMMO.p.getServer().getPlayer(args[0]);
            mcMMOPlayer = Users.getPlayer(modifiedPlayer);
            profile = mcMMOPlayer.getProfile();

          //TODO: Any way we can make this work for offline use?
            if (profile == null || !profile.isLoaded()) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            if (!SkillTools.isSkill(args[1])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (!Misc.isInt(args[2])) {
                return false;
            }

            xp = Integer.valueOf(args[2]);
            skill = SkillTools.getSkillType(args[1]);
            String playerName = modifiedPlayer.getName();

            mcMMOPlayer.addXpOverride(skill, xp);

            if (skill.equals(SkillType.ALL)) {
                sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", playerName));
                modifiedPlayer.sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", xp));
                SkillTools.xpCheckAll(modifiedPlayer, profile);
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", Misc.getCapitalized(skill.toString()), playerName));
                modifiedPlayer.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", xp, Misc.getCapitalized(skill.toString())));
                SkillTools.xpCheckSkill(skill, modifiedPlayer, profile);
            }

            return true;

        default:
            return false;
        }
    }
}
