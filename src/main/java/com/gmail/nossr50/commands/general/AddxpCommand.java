package com.gmail.nossr50.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.Skills;

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
        String skillName;
        String usage = ChatColor.RED + "Proper usage is /addxp [player] <skill> <xp>"; //TODO: Needs more locale.

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.tools.mmoedit")) {
            return true;
        }

        switch (args.length) {
        case 2:
            if (sender instanceof Player) {
                if (m.isInt(args[1]) && Skills.isSkill(args[0])) {
                    modifiedPlayer = (Player) sender;
                    xp = Integer.valueOf(args[1]);
                    skill = Skills.getSkillType(args[0]);

                    Users.getProfile(modifiedPlayer).addXPOverride(skill, xp);

                    if (skill.equals(SkillType.ALL)) {
                        skillName = "all skills";
                    }
                    else {
                        skillName = m.getCapitalized(skill.toString());
                    }

                    modifiedPlayer.sendMessage(ChatColor.GREEN + "You were awarded " + xp + " experience in " + skillName + "!"); //TODO: Needs more locale.

                    if (skill.equals(SkillType.ALL)) {
                        Skills.XpCheckAll(modifiedPlayer);
                    }
                    else {
                        Skills.XpCheckSkill(skill, modifiedPlayer);
                    }
                }
            }
            else {
                sender.sendMessage(usage);
            }

            return true;

        case 3:
            modifiedPlayer = plugin.getServer().getPlayer(args[0]);
            String playerName = modifiedPlayer.getName();

            if (modifiedPlayer != null && m.isInt(args[2]) && Skills.isSkill(args[1])) {
                xp = Integer.valueOf(args[2]);
                skill = Skills.getSkillType(args[1]);
                String message;

                Users.getProfile(modifiedPlayer).addXPOverride(skill, xp);

                if (skill.equals(SkillType.ALL)) {
                    skillName = "all skills";
                    message = ChatColor.RED + "All skills have been modified for " + playerName + "."; //TODO: Use locale
                }
                else {
                    skillName = m.getCapitalized(skill.toString());
                    message = ChatColor.RED + skillName + " has been modified for " + playerName + "."; //TODO: Use locale
                }

                sender.sendMessage(message);
                modifiedPlayer.sendMessage(ChatColor.GREEN + "You were awarded " + xp + " experience in " + skillName + "!"); //TODO: Needs more locale.

                if (skill.equals(SkillType.ALL)) {
                    Skills.XpCheckAll(modifiedPlayer);
                }
                else {
                    Skills.XpCheckSkill(skill, modifiedPlayer);
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
