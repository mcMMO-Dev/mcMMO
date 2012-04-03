package com.gmail.nossr50.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;

public class AddxpCommand implements CommandExecutor {
    private final mcMMO plugin;

    public AddxpCommand(mcMMO instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player sendingPlayer;
        Player modifiedPlayer;
        int xp;
        SkillType skill;
        String skillName;

        if (sender instanceof Player) {
            sendingPlayer = (Player) sender;

            if (sendingPlayer != null && !mcPermissions.getInstance().mmoedit(sendingPlayer)) {
                sendingPlayer.sendMessage(mcLocale.getString("mcPlayerListener.NoPermission"));
                return true;
            }
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
                System.out.println("Usage is /addxp playername skillname xp"); //TODO: Needs more locale.
            }

            return true;

        case 3:
            modifiedPlayer = plugin.getServer().getPlayer(args[0]);
            String playerName = modifiedPlayer.getName();

            if (modifiedPlayer != null && m.isInt(args[2]) && Skills.isSkill(args[1])) {
                xp = Integer.valueOf(args[2]);
                skill = Skills.getSkillType(args[1]);

                Users.getProfile(modifiedPlayer).addXPOverride(skill, xp);

                if (skill.equals(SkillType.ALL)) {
                    skillName = "all skills";
                }
                else {
                    skillName = m.getCapitalized(skill.toString());
                }

                if (sender instanceof Player) {
                    sender.sendMessage(ChatColor.RED + skillName + " has been modified for " + playerName + "."); //TODO: Use locale
                }
                else {
                    System.out.println(skillName + " has been modified for " + playerName + "."); //TODO: Use locale
                }

                modifiedPlayer.sendMessage(ChatColor.GREEN + "You were awarded " + xp + " experience in " + skillName + "!"); //TODO: Needs more locale.

                if (skill.equals(SkillType.ALL)) {
                    Skills.XpCheckAll(modifiedPlayer);
                }
                else {
                    Skills.XpCheckSkill(skill, modifiedPlayer);
                }
            }

            return true;

        default:
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "Usage is /addxp playername skillname xp"); //TODO: Needs more locale.
            }
            else {
                System.out.println("Usage is /addxp playername skillname xp"); //TODO: Needs more locale.
            }

            return true;
        }
    }
}
