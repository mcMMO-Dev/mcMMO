package com.gmail.nossr50.commands.general;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

//TODO: Any way we can make this work for offline use?
public class AddxpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player modifiedPlayer;
        int xp;
        SkillType skill;
        String usage = LocaleLoader.getString("Commands.Usage.3", new Object[] {"addxp", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">", "<" + LocaleLoader.getString("Commands.Usage.XP") + ">" });

        switch (args.length) {
        case 2:
            if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.addxp") && !Permissions.mmoedit((Player) sender)) {
                return true;
            }

            if (sender instanceof Player) {
                if (!SkillTools.isSkill(args[1])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    return true;
                }

                if (Misc.isInt(args[1])) {
                    modifiedPlayer = (Player) sender;
                    xp = Integer.valueOf(args[1]);
                    skill = SkillTools.getSkillType(args[0]);

                    PlayerProfile profile = Users.getProfile(modifiedPlayer);
                    McMMOPlayer mcMMOPlayer = Users.getPlayer(modifiedPlayer);
                    mcMMOPlayer.addXPOverride(skill, xp);

                    if (skill.equals(SkillType.ALL)) {
                        modifiedPlayer.sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", new Object[] {xp}));
                    }
                    else {
                        modifiedPlayer.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", new Object[] {xp, Misc.getCapitalized(skill.toString())}));
                    }

                    if (skill.equals(SkillType.ALL)) {
                        SkillTools.xpCheckAll(modifiedPlayer, profile);
                    }
                    else {
                        SkillTools.xpCheckSkill(skill, modifiedPlayer, profile);
                    }
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
            if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.addxp.others") && !Permissions.mmoedit((Player) sender)) {
                return true;
            }

            modifiedPlayer = mcMMO.p.getServer().getPlayer(args[0]);
            String playerName = modifiedPlayer.getName();
            McMMOPlayer mcMMOPlayer = Users.getPlayer(modifiedPlayer);
            PlayerProfile profile = Users.getProfile(modifiedPlayer);

            // TODO: Not sure if we actually need a null check here
            if (profile == null || !profile.isLoaded()) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            if (!SkillTools.isSkill(args[1])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (Misc.isInt(args[2])) {
                xp = Integer.valueOf(args[2]);
                skill = SkillTools.getSkillType(args[1]);

                mcMMOPlayer.addXPOverride(skill, xp);

                if (skill.equals(SkillType.ALL)) {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", new Object[] {playerName}));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", new Object[] {Misc.getCapitalized(skill.toString()), playerName}));
                }

                if (skill.equals(SkillType.ALL)) {
                    modifiedPlayer.sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", new Object[] {xp}));
                    SkillTools.xpCheckAll(modifiedPlayer, profile);
                }
                else {
                    modifiedPlayer.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", new Object[] {xp, Misc.getCapitalized(skill.toString())}));
                    SkillTools.xpCheckSkill(skill, modifiedPlayer, profile);
                }
            }
            else {
                sender.sendMessage(usage);
            }

            return true;

        default:
            sender.sendMessage(usage);
            return true;
        }
    }
}
