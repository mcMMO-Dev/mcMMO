package com.gmail.nossr50.commands.general;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

//TODO: Any way we can make this work for offline use?
public class AddxpCommand implements CommandExecutor {
    private final mcMMO plugin;

    public AddxpCommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player modifiedPlayer;
        int xp;
        SkillType skill;
        String usage = LocaleLoader.getString("Commands.Usage.3", new Object[] {"addxp", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">", "<" + LocaleLoader.getString("Commands.Usage.XP") + ">" });

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.tools.mmoedit")) {
            return true;
        }

        switch (args.length) {
        case 2:
            if (sender instanceof Player) {
                if (!Skills.isSkill(args[1])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                    return true;
                }

                if (Misc.isInt(args[1])) {
                    modifiedPlayer = (Player) sender;
                    xp = Integer.valueOf(args[1]);
                    skill = Skills.getSkillType(args[0]);

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
                        Skills.xpCheckAll(modifiedPlayer, profile);
                    }
                    else {
                        Skills.xpCheckSkill(skill, modifiedPlayer, profile);
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
            modifiedPlayer = plugin.getServer().getPlayer(args[0]);
            String playerName = modifiedPlayer.getName();
            McMMOPlayer mcMMOPlayer = Users.getPlayer(modifiedPlayer);
            PlayerProfile profile = Users.getProfile(modifiedPlayer);

            if (profile == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            if (!profile.isLoaded()) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return true;
            }

            if (!Skills.isSkill(args[1])) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Invalid"));
                return true;
            }

            if (Misc.isInt(args[2])) {
                xp = Integer.valueOf(args[2]);
                skill = Skills.getSkillType(args[1]);

                mcMMOPlayer.addXPOverride(skill, xp);

                if (skill.equals(SkillType.ALL)) {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.2", new Object[] {playerName}));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.2", new Object[] {Misc.getCapitalized(skill.toString()), playerName}));
                }

                if (skill.equals(SkillType.ALL)) {
                    modifiedPlayer.sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", new Object[] {xp}));
                    Skills.xpCheckAll(modifiedPlayer, profile);
                }
                else {
                    modifiedPlayer.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", new Object[] {xp, Misc.getCapitalized(skill.toString())}));
                    Skills.xpCheckSkill(skill, modifiedPlayer, profile);
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
