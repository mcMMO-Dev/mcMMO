package com.gmail.nossr50.commands.spout;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.spout.SpoutStuff;

public class XplockCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = ChatColor.RED + "Proper usage is /xplock [skill]";

        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (!Config.spoutEnabled || !Config.xpbar || !Config.getCommandXPLockEnabled()) {
            sender.sendMessage(mcLocale.getString("Commands.Disabled"));
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        switch (args.length) {
        case 0:
            if (PP.getXpBarLocked()) {
                PP.toggleXpBarLocked();
                player.sendMessage(mcLocale.getString("Commands.xplock.unlocked"));
                return true;
            }

            SkillType lastGained = PP.getLastGained();

            if (lastGained != null) {
                PP.toggleXpBarLocked();
                PP.setSkillLock(lastGained);
                player.sendMessage(mcLocale.getString("Commands.xplock.locked", new Object[] { m.getCapitalized(lastGained.toString()) }));
                return true;
            }

        case 1:
            if (Skills.isSkill(args[0])) {
                if (mcPermissions.getInstance().permission(player, "mcmmo.skills." + args[0].toLowerCase())) {
                    PP.setXpBarLocked(true);
                    PP.setSkillLock(Skills.getSkillType(args[0]));
                    SpoutStuff.updateXpBar(player);

                    player.sendMessage(mcLocale.getString("Commands.xplock.locked", new Object[] { m.getCapitalized(args[0]) }));
                    return true;
                }
                else {
                    player.sendMessage(mcLocale.getString("mcMMO.NoPermission"));
                    return true;
                }
            }
            else {
                player.sendMessage(mcLocale.getString("Commands.Skill.Invalid"));
                return true;
            }

        default:
            player.sendMessage(usage);
            return true;
        }
    }
}
